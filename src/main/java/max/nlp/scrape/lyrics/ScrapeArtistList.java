package max.nlp.scrape.lyrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ScrapeArtistList {


	public static void main(String[] args) {
		try {
			PrintWriter w = new PrintWriter(new FileWriter(new File("/home/max/listOfArtists2.txt")));

			String baseURI = "http://lyricstranslate.com";
			String artistURI = "http://lyricstranslate.com/en/artists-page-";
			for (int i = 51;i <= 166; i ++){
				w.println("***** " + i + " *****" );
				String currentURI = artistURI + i;

				Document doc = Jsoup.connect(currentURI).get();
				for (Element tr : doc.getAllElements()) {
					Elements tds = tr.getElementsByAttributeValue("class", "ltsearch-artist");
					Document d = Jsoup.parse(tds.html());
					Elements linkas = d.select("a[href]"); // a with href
					for (Element link : linkas) {
						String relative = link.attr("href").replaceAll(">", "").replaceAll("<", "");
						w.println(baseURI + relative +   "\t" +  relative.replaceAll("/en/", "").replaceAll("-lyrics.html", ""));
					}
				}
				Thread.sleep(1000);
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
