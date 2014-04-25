package max.nlp.scrape.wiktionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class APIScraper {

	private String rootURL = "http://en.wiktionary.org/w/api.php?";

	private ArrayList<String> titles = new ArrayList<String>();
	private String category;

	public void scrapeCategory() {
		String startingURL = rootURL + "action=query"
				+ "&generator=categorymembers&" + "gcmtitle=" + category
				+ "&prop=info&format=xml";
		String nextPage = scrape(startingURL);

		while (nextPage != null) {
			nextPage = scrape(startingURL + nextPage);
		}

	}

	public String scrape(String url) {
		String nextPage = null;
		try {
			Document doc = Jsoup.connect(url).get();
			Element nextPageEle = doc.getElementsByTag("categorymembers")
					.first();

			if (nextPageEle != null)
				nextPage = "&gcmcontinue=" + nextPageEle.attr("gcmcontinue");
			Elements pages = doc.getElementsByTag("page");
			titles.addAll(extractTitles(pages));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nextPage;
	}

	public static void main(String[] args) {
		APIScraper s = new APIScraper("Category:English_aphorisms");
		s.scrapeCategory();
		System.out.println(s.getTitles());
	}

	public ArrayList<String> getTitles() {
		return titles;
	}

	public APIScraper(String category) {
		this.category = category;
	}

	public List<String> extractTitles(Elements pages) {
		List<String> titles = new ArrayList<String>();
		for (int i = 0; i < pages.size(); i++) {
			Element word = pages.get(i);
			titles.add(word.attr("title"));
		}
		return titles;
	}
}
