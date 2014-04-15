package max.nlp.scrape.opus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import max.nlp.scrape.AbstractScraper;

import org.apache.commons.io.FileUtils;


public class OPUSScraper extends AbstractScraper {

	// private static Logger log = Logger.getLogger(OPUSScraper.class);

	public static void main(String[] args) {
		OPUSScraper p = new OPUSScraper();
		p.downloadOPUSSubtitles();
	}

	// the strings need to be in alphabetical order to scrape opus
	// returns empty if they are the same str
	public String[] sortStringsAlphabetically(String s1, String s2) {
		String[] langs = new String[2];
		int order = s1.compareTo(s2);
		if (order <= 0) {
			langs[0] = s1;
			langs[1] = s2;
		} else if (order >= 0) {
			langs[0] = s2;
			langs[1] = s1;
		}
		return langs;
	}

	public void downloadOPUSSubtitles() {
		log.info("Starting OPUS download");
		wget(downloadDirectory + "opus.zip", config.getString("opus.subtitles.all"));
		log.info("Finished OPUS download");
	}

	public void downloadOPUSFile(String l1, String l2, boolean unzip) {
		try {
			String[] langs = sortStringsAlphabetically(l1, l2);
				String pair = langs[0] + "-" + langs[1];
				String suffix = pair + ".tmx.gz";
				URL downloadURL = new URL(config.getString("opus.subtitles.prefix") + suffix);
				String outputFileString = downloadDirectory + suffix;
				File outputFile = new File(outputFileString);
				log.info("Starting OPUS download for [" + l1 + "] and [" + l2 + "]");
				FileUtils.copyURLToFile(downloadURL, outputFile);
				log.info("Finished OPUS download for [" + l1 + "] and [" + l2 + "]");
				if (unzip) {
					log.info("Starting to unzip " + outputFileString);
					uncompressGzip(outputFileString, outputFileString.replaceAll(".gz", ""));
					log.info("Finished unzipping" + outputFileString);
				}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
