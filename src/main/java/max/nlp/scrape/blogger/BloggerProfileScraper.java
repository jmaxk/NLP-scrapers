package max.nlp.scrape.blogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import max.nlp.dal.blog.blogger.BlogAuthorProfile;
import max.nlp.dal.blog.blogger.BloggerDB;
import max.nlp.scrape.AbstractScraper;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scrapes blogger profile 
 * @author jmaxk
 *
 */
public class BloggerProfileScraper extends AbstractScraper {
	private final static Logger log = Logger
			.getLogger(BloggerProfileScraper.class.getName());
	
	//Less than this and you get banned 
	private final static int timeout = 6000;

	public static BlogAuthorProfile scrapeProfile(String profileURL) {
		BlogAuthorProfile profile = new BlogAuthorProfile(profileURL);
		try {
			Document doc = Jsoup.connect(profileURL).timeout(timeout).get();
			// Build the author profile
			Element table = doc.select("table").first();
			for (Element element : table.select("tr")) {
				Elements cat = element.getElementsByAttributeValue("class",
						"item-key");
				String category = cat.text();
				Elements feats = element.getElementsByTag("span");
				String fieldName = feats.attr("class");
				if (fieldName.isEmpty()) {
					feats = element.getElementsByTag("td");
					fieldName = category;
					category = null;
					String fieldContent = element.getElementsByTag("td").text();
					profile.addMetadata(null, fieldName, fieldContent);

				} else {
					String fieldContent = feats.text();
					profile.addMetadata(category, fieldName, fieldContent);
				}
			}
			List<String> blogs = new ArrayList<String>();
			for (Element element : doc.getElementsByAttributeValue("rel", "me")) {
				String blogURL = element.attr("href");
				blogs.add(blogURL);
			}
			profile.setBlogs(blogs);
		} catch (Exception e) {
			log.error("bad profile:" + profileURL);
			log.error(e.getMessage());
			e.printStackTrace();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				log.error(e.getMessage());
				e.printStackTrace();

			}
			return null;

		}
		return profile;
	}

	/**
	 * Scrapes profiles from a file, and stores them in the db
	 * @param pFile, a file with one profile per line 
	 */
	public static void scrapeOnlyProfiles(String pFile) {

		try {

			BloggerDB db = BloggerDB.getInstance();

			File file = new File(pFile);
			@SuppressWarnings("unchecked")
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			for (String profileURL : lines) {
				if (db.findProfileByURL(profileURL) == null) {
					BlogAuthorProfile profile = scrapeProfile(profileURL);
					if ((profile != null) && (profile.getBlogs() != null)
							&& (profile.getBlogs().size() > 0)) {
						System.out.println("saving: " + profileURL);
						db.saveProfile(profile);
					} else {
						System.out.println("skipped: " + profileURL);
					}
				}
			}
		} catch (IOException e) {
			log.error(e.getStackTrace());
		}
	}



	public static void main(String[] args) {
		 String pFile = "/home/max/proj/bloggerscraper/old/uniqBlogs.txt";
		 scrapeOnlyProfiles(pFile);

	}
}
