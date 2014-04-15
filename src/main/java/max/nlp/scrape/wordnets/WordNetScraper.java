package max.nlp.scrape.wordnets;

import java.io.File;

public class WordNetScraper extends max.nlp.scrape.AbstractScraper {


	public WordNetScraper() {
		super();
	}

	public void scrapeWordNet(boolean deleteCompressedFile) {
		String source = config.getWordNetURL();
		String compressedFile = config.getDownloadDirectory()+ "WordNet.tar.gz";
		String uncompressedDir = config.getWordNetDir();

		log.info("Downloading wordnet");
		wget(compressedFile, source);
		log.info("Uncompressing wordnet");
		uncompressTarGZ(compressedFile, uncompressedDir);

		if (deleteCompressedFile) {
			log.info("Trying to delete [" + compressedFile + "]");
			File f = new File(compressedFile);
			boolean deleted = f.delete();
			if (deleted)
				log.info("Deleted [" + f.getName() + "]");
			else
				log.info("Unable to delete: " + f.getName() + "]");
		}
	}
	
	public void scrapeUniversalWordNet(boolean deleteCompressedFile) {
		String source = config.getWordNetURL();
		String compressedFile = config.getDownloadDirectory()+ "UniversalWordNet.zip";
		String uncompressedDir = config.getWordNetDir();

		log.info("Downloading universal wordnet ");
		wget(compressedFile, source);
		log.info("Uncompressing universal wordnet ");
		uncompressZip(compressedFile, uncompressedDir);

		if (deleteCompressedFile) {
			log.info("Trying to delete [" + compressedFile + "]");
			File f = new File(compressedFile);
			boolean deleted = f.delete();
			if (deleted)
				log.info("Deleted [" + f.getName() + "]");
			else
				log.info("Unable to delete: " + f.getName() + "]");
		}
	}

	public static void main(String[] args) {
		WordNetScraper w = new WordNetScraper();
		w.scrapeWordNet(false);
	}

}
