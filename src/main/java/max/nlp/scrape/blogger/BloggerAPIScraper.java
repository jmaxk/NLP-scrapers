package max.nlp.scrape.blogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import max.nlp.dal.blog.blogger.BlogAuthorProfile;
import max.nlp.dal.blog.blogger.BloggerBlog;
import max.nlp.dal.blog.blogger.BloggerDB;
import max.nlp.dal.blog.blogger.BloggerPost;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.Blogger.Blogs.Get;
import com.google.api.services.blogger.Blogger.Blogs.GetByUrl;
import com.google.api.services.blogger.BloggerRequestInitializer;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class BloggerAPIScraper {

	private static BloggerAPIScraper instance = null;
	private static Blogger blogger = null;
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final String key = "AIzaSyC9w-jCLgKxnJ8LyYHdBWTpfIVYD0d8b1s";
	private static final JsonFactory jsonFactory = new JacksonFactory();
	private static BloggerDB db = BloggerDB.getInstance();

	private BloggerAPIScraper() {

		blogger = new com.google.api.services.blogger.Blogger.Builder(
				HTTP_TRANSPORT, jsonFactory, null)
				.setApplicationName("research")
				.setGoogleClientRequestInitializer(
						new BloggerRequestInitializer(key)).build();

	}

	public static BloggerAPIScraper getInstance() {
		if (instance == null)
			instance = new BloggerAPIScraper();
		return instance;
	}

	public Blog getBlogByURL(String blogURL) {
		Blog b = null;
		try {
			GetByUrl e = blogger.blogs().getByUrl(blogURL);
			try {
				b = e.execute();
			} catch (GoogleJsonResponseException e1) {
				if (e1.getStatusCode() == 404) {
				//TODO: remove blog from lists
				}
				if (e1.getMessage().contains("Daily Limit")){
					System.out.println("Limit exceeded");
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;

	}

	public Blog getBlogByID(String id) {
		Blog b = null;
		try {
			Get e = blogger.blogs().get(id);
			b = e.execute();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;

	}

	public List<BasicDBObject> getPostsFromBlog(Blog b)
			throws GoogleJsonResponseException {
		List<BasicDBObject> postsForMongo = new ArrayList<BasicDBObject>();

		// try maxresults === w/e
		// https://developers.google.com/blogger/docs/3.0/reference/posts/list
		try {
			Blogger.Posts.List postsListAction = blogger.posts()
					.list(b.getId());

			PostList posts;

			posts = postsListAction.execute();

			// Keep track of the page number so we limit ourselves to retrieving
			// 50 posts
			int currentPageNumber = 0;
			while (posts != null && posts.getItems() != null
					&& currentPageNumber <= 20) {
				currentPageNumber++;

				for (Post post : posts.getItems()) {
					Object o = com.mongodb.util.JSON.parse(post.toString());
					BasicDBObject postAsDBObject = (BasicDBObject) o;
					postsForMongo.add(postAsDBObject);
				}

				// Fetch the next page
				postsListAction.setPageToken(posts.getNextPageToken());
				posts = postsListAction.execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return postsForMongo;
	}

	public static void scrapeOnlyBlogs() {
		DBCursor itr = db.getProfileIterator();
		while (itr.hasNext()) {
			BlogAuthorProfile profile = new BlogAuthorProfile(itr.next()
					.toMap());
			List<String> potentialBlogs = profile.getBlogs();
			if ((potentialBlogs != null) && !potentialBlogs.isEmpty()) {
				for (String url : potentialBlogs) {
					if (db.findBlogbyURL(url) == null) {
						BloggerBlog blog = scrapeBlog(url);
						if (blog != null) {
							db.saveBlog(blog);
						}
					}
				}
			}

		}
	}

	public static BloggerBlog scrapeBlog(String blogURL) {
		BloggerAPIScraper bloggerScraper = BloggerAPIScraper.getInstance();

		Blog googleBlog = bloggerScraper.getBlogByURL(blogURL);
		if (googleBlog != null) {
			try {
				BloggerBlog b = new BloggerBlog();

				List<BloggerPost> posts = BloggerPost.fromList(bloggerScraper
						.getPostsFromBlog(googleBlog));
				b.setPosts(posts);
				b.setBlogId(googleBlog.getId());
				b.setUrl(blogURL);
				b.setName(googleBlog.getName());
				return b;
			} catch (GoogleJsonResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		scrapeOnlyBlogs();
	}

}
