package max.nlp.scrape.verbnet;

import java.io.File;

import max.nlp.scrape.AbstractScraper;


public class VerbNetScraper extends AbstractScraper{


	public VerbNetScraper() {
		super();
	}
	
	public void scrapeVerbNet(boolean deleteCompressedFile) {
		String source = config.getWordNetURL();
		String compressedFile = config.getDownloadDirectory()+ "VerbNet.tar.gz";
		String uncompressedDir = config.getVerbNetDir();

		log.info("Downloading VerbNet");
		wget(compressedFile, source);
		log.info("Uncompressing VerbNet");
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
}
