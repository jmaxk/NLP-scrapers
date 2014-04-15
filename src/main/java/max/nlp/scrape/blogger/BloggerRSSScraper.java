package max.nlp.scrape.blogger;

import java.util.List;

import max.nlp.dal.blog.blogger.BlogAuthorProfile;
import max.nlp.dal.blog.blogger.BloggerBlog;
import max.nlp.dal.blog.blogger.BloggerDB;
import max.nlp.dal.blog.blogger.BloggerPost;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mongodb.DBCursor;

/**
 * 
 * Scrapes blogs by looking at the RSS feed for the blog
 * @author jmaxk
 *
 */
public class BloggerRSSScraper {

	public static void main(String[] args) {
		BloggerRSSScraper rss = new BloggerRSSScraper();
		rss.scrape();
	}

	public void scrape() {

		BloggerDB db = BloggerDB.getInstance();
		DBCursor itr = db.getProfileIterator();
		while (itr.hasNext()) {
			BlogAuthorProfile p = new BlogAuthorProfile(itr.next());
			List<String> blogs = p.getBlogs();
			for (String blogURL : blogs) {
				if (db.findBlogbyURL(blogURL) == null) {
					BloggerBlog blog = scrapeBlog(blogURL);
					if (blog != null) {
						System.out.println("saving " + blog.getUrl());
						db.saveBlog(blog);
					}
				}
			}
		}
	}

	/**
	 * Scrapes a blog 
	 * @param blogURL
	 * @return
	 */
	public BloggerBlog scrapeBlog(String blogURL) {
		String url = blogURL + "feeds/posts/default";
		try {

			Connection.Response response = Jsoup
					.connect(url)
					.ignoreHttpErrors(true)
					.ignoreContentType(true)
					.userAgent(
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.timeout(300).execute();
			int statusCode = response.statusCode();
			if (statusCode == 200) {
				BloggerBlog b = new BloggerBlog();
				Document doc = response.parse();
				b.setBlogId(doc.select("id").get(0).text());
				Elements entries = doc.select("entry");

				b.setSource("rss");
				b.setUrl(blogURL);
				b.setName(doc.select("title").get(0).text());
				List<BloggerPost> posts = BloggerPost.fromRSS(entries);
				if (posts.isEmpty())
					return null;
				else {
					b.setPosts(posts);
					return b;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
