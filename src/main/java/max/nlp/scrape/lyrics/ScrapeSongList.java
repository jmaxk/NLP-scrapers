package max.nlp.scrape.lyrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrapeSongList {


	public static void main(String[] args) {
		try {
			PrintWriter w = new PrintWriter(new FileWriter(new File("/home/max/lyrical/restOfSongs.txt")));
			BufferedReader b = new BufferedReader(new FileReader(new File("/home/max/lyrical/restOfArtists.txt")));
			String base = "http://lyricstranslate.com";
			String line = "";					
			while ((line = b.readLine()) != null){
				System.out.println(line);
				if (!line.contains("****")){
					String[] fields = line.split("\t");
					String URL = fields[0];
					String artist = fields[1];
					Connection.Response response = Jsoup.connect(URL).ignoreHttpErrors(true)
							.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
							.timeout(10000)
							.execute();
					int statusCode = response.statusCode();
					if (statusCode == 200){
						Document doc = response.parse();
						Elements songs = doc.select("tbody");
						Document table = Jsoup.parse(songs.html());
						Elements linkas = table.select("a[href]"); // a with href
						String song = "";
						for (Element link : linkas){
							String potentialURL = link.attr("href");
							if ((!potentialURL.contains("request")) && (!potentialURL.contains("#video"))){
								String lang = link.ownText();
								if (lang.contains("lyrics")){
									song = lang.replaceAll(" lyrics", "");
									lang = "English";
								}
								w.println(base + potentialURL + "\t" + song + "\t" + lang + "\t" + artist);
							}

						}
						w.flush();
					
						Thread.sleep(500);
					}
				}
			}

b.close();

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
