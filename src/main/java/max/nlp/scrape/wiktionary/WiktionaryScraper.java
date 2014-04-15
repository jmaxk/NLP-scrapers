package max.nlp.scrape.wiktionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import max.nlp.scrape.AbstractScraper;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.jwktl.JWKTL;

public class WiktionaryScraper extends AbstractScraper {

	private String WIKTIONARY_ROOT_URL;

	public WiktionaryScraper() {
		super();
		log = Logger.getLogger(WiktionaryScraper.class);
		WIKTIONARY_ROOT_URL = config.getWiktionaryURL();
		File globalWiktionaryDirectory = new File(
				config.getResourcesDirectory() + "wiktionary/");
		if (!globalWiktionaryDirectory.exists()) {
			globalWiktionaryDirectory.mkdir();
		}
	}

	public static void main(String[] args) {

		 WiktionaryScraper w = new WiktionaryScraper();
		// w.scrapeWiktionary("es", true);
		 w.parse();

//		IWiktionaryEdition wkt = JWKTL.openEdition(new File(
//				"/home/max/resources/wiktionary/"));
//		IWiktionaryIterator<IWiktionaryEntry> entries = wkt.getAllEntries();
//		while (entries.hasNext()) {
//			IWiktionaryEntry entry = entries.next();
//			if (entry.getHeader().contains("без дураков"))
//				System.out.println(entry.getHeader());
//		}

	}

	public void parse() {
		File dumpFile = new File(
				"/local/wiktionary/en-dump.xml");
		File outputDirectory = new File("/home/max/resources/wiktionary/jwtlk/en");
		boolean overwriteExisting = true;

		JWKTL.parseWiktionaryDump(dumpFile, outputDirectory, overwriteExisting);

	}

	public void scrapeWiktionary(String language, boolean deleteCompressedFile) {
		try {
			String wiktionaryName = language + "wiktionary";
			String wiktionaryURL = WIKTIONARY_ROOT_URL + wiktionaryName
					+ "/latest/" + wiktionaryName
					+ "-latest-pages-articles.xml.bz2";
			String compressedFile = config.getDownloadDirectory()
					+ wiktionaryName + ".bz2";
			String dumpFile = config.getWiktionaryDumpFile(language);

			if (deleteCompressedFile) {
				log.info("Trying to delete [" + compressedFile + "]");
				File f = new File(compressedFile);
				boolean deleted = f.delete();
				if (deleted)
					log.info("Deleted [" + f.getName() + "]");
				else
					log.info("Unable to delete: " + f.getName() + "]");
			}

			log.info("Downloading Wiktionary for " + language);
			wget(compressedFile, wiktionaryURL);

			log.info("Uncompressing Wiktionary for " + language);
			uncompressBz2(compressedFile, dumpFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// if (deleteCompressedFile) {
		// log.info("Trying to delete [" + compressedFile + "]");
		// File f = new File(compressedFile);
		// boolean deleted = f.delete();
		// if (deleted)
		// log.info("Deleted [" + f.getName() + "]");
		// else
		// log.info("Unable to delete: " + f.getName() + "]");
		// }
	}

}
