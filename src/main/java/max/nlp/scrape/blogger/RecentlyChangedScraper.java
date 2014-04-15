package max.nlp.scrape.blogger;


import java.io.IOException;
import java.util.Iterator;

import max.nlp.scrape.AbstractScraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RecentlyChangedScraper extends AbstractScraper {

	private static String url = "http://www.blogger.com/changes10.xml";

	public static void main(String[] args) {
//		scrapeRecentlyChanged();

	}

	public static void scrapeRecentlyChanged() {
		try {
			Connection.Response response = Jsoup
					.connect(url)
					.ignoreHttpErrors(true)
					.userAgent(
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.timeout(10000).execute();
			int statusCode = response.statusCode();
			if (statusCode == 200) {
				Document doc = response.parse();
				System.out.println(doc);
				Elements songs = doc.select("weblog");
				Iterator<Element> iterator = songs.iterator();
				while (iterator.hasNext())
					System.out.println(iterator.next().attr("url"));

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	

}
