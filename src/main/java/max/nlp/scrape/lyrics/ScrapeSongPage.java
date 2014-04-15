package max.nlp.scrape.lyrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import max.nlp.dal.lyrics.LyricaDB;
import max.nlp.dal.lyrics.types.Song;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.MongoException;

public class ScrapeSongPage {

	public static void main(String[] args) {
		ScrapeSongPage p = new ScrapeSongPage();
		p.ingest();
	}
	public  void ingest() {
		int numSongs = 0;

		try {
			InputStream res = getClass().getResourceAsStream("/allSongs.txt");
			BufferedReader b = new BufferedReader(new InputStreamReader(res));
			String line = "";
		
			for (int i = 0; i < (2343+ 11999 + 580); i++){
				b.readLine();
			}
			LyricaDB db = LyricaDB.getDefault();
			while ((line = b.readLine()) != null){

				String[] fields = line.split("\t");
				String URL = fields[0];
				String title = fields[1];
				String lang = fields[2];
				String author = fields[3];
				Connection.Response response = Jsoup.connect(URL).ignoreHttpErrors(true)
						.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(10000)
						.execute();
				int statusCode = response.statusCode();
				if (statusCode == 200){
					Document doc = response.parse();
					Elements test = doc.select("li.song-node-info-author");
					Document trans = Jsoup.parse(test.first().html());
					String translator = trans.select("a").text();
					String rating = doc.select("span.average-rating").text().replaceAll("Average: ", "");
					String numVotes = doc.select("span.total-votes").text();
					if (numVotes.length() > 3){
						numVotes = numVotes.charAt(1) + "";
					}

					Song s;
					Elements foreign = doc.select("div.translate-node-text");
					if (foreign.size() > 0){
						String text = foreign.first().html().replaceAll("<br />", "[new]").replaceAll("<p>", "[startP]");
						String ftext = Jsoup.parse(text).text().replaceAll("\\[startP\\]", "\n").replaceAll("\\[new\\]", "\n");
						int splitID = ftext.indexOf(' ');
						lang = ftext.substring(0, splitID);
						ftext = ftext.substring(splitID, ftext.length() -1);
						s = new Song(lang,ftext,URL,author,title, translator);

					}
					else{
						Elements eng = doc.select("div.song-node-text");
						String text = eng.first().html().replaceAll("<br />", "[new]").replaceAll("<p>", "[startP]");
						String etext = Jsoup.parse(text).text().replaceAll("\\[startP\\]", "\n").replaceAll("\\[new\\]", "\n");
						Elements tempEles = doc.select("li.song-node-info-language");
						Element tempEle = tempEles.first();
						Document d2 = Jsoup.parse(tempEle.html());
						lang = d2.select("a[href]").first().text();
						s = new Song(lang,etext,URL,author,title, translator);
					}
					if (numVotes != null && rating != null){
						s.setNumVotes(numVotes);
						s.setRating(rating);
					}
					db.save(s);
					numSongs++;
					Thread.sleep(500);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(numSongs);
		}
		catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(numSongs);

		}
		catch(InterruptedException e ){
			e.printStackTrace();
			System.out.println(numSongs);
		}

	}
}
