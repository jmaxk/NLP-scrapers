package max.nlp.scrape.blogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import max.nlp.scrape.AbstractScraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Scrape blogs from the blogger search pages
 * @author jmaxk
 *
 */
public class SearchPageScraper extends AbstractScraper {
	private PrintWriter w = null;

	public static void main(String[] args) {
		//Example usage for scraping blogs where loc0=USA and loc1=TX
		//loc0 is usually a country, and loc1 is usually a state, although the search queries are not  very well docuemtned 
		String startingURL = "http://www.blogger.com/profile-find.g?t=l&loc0=US&loc1=TX";
		SearchPageScraper p = new SearchPageScraper("/home/max/proj/bloggerscraper/search/TX.txt");
		p.scrape(startingURL, 0);
	}

	/**
	 * Writes the URLS from the search page to an output file 
	 * @param outputFile
	 */
	public SearchPageScraper(String outputFile) {
		try {
			w = new PrintWriter(new FileWriter(new File(outputFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param url, the starting url
	 * @param pageCount, the number of esarch pages to scrape 
	 */
	public void scrape(String url, int pageCount) {
		if (pageCount >= 10)
			return;
		try {
			Connection.Response response = Jsoup
					.connect(url)
					.ignoreHttpErrors(true)
					.userAgent(
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, likea Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.timeout(10000).execute();
			int statusCode = response.statusCode();
			Set<String> newProfiles = new HashSet<String>();
			if (statusCode == 200) {
				Document doc = response.parse();
				Elements profiles = doc.getElementsByAttribute("href");
				Iterator<Element> iterator = profiles.iterator();
				while (iterator.hasNext()) {
					String profileURL = iterator.next().attr("href");
					if (profileURL
							.startsWith("http://www.blogger.com/profile/")) {
						newProfiles.add(profileURL);
					}
				}
				Elements nextPage = doc.getElementsByAttributeValue("id",
						"next-btn");
				iterator = nextPage.iterator();
				if (iterator.hasNext()) {
					String nextPageURL = iterator.next().attr("href");
					if (nextPageURL != null) {
						for (String s : newProfiles) {
							w.println(s);
						}
						w.flush();
						Thread.sleep(1000);
						scrape(nextPageURL, ++pageCount);
					}

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
