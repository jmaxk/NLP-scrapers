package max.nlp.scrape;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

public class ScrapingConfiguration {

	public static final String CONFIG_FILE = "config/scrapingConfig.yaml";

	private static ScrapingConfiguration conf;

	 private static Hashtable<Object, Object> table;

	@SuppressWarnings("static-access")
	public static ScrapingConfiguration getInstance() {
		if (conf == null) {
			conf = new ScrapingConfiguration();
			table = conf.table;
		}
		return conf;
	}

	private ScrapingConfiguration() {
		table = new Hashtable<Object, Object>();
		InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/scrapingConfig.yaml");
		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		Map<String, Object> yamlProps = (Map<String, Object>) yaml.load(io);
		for (Entry<String, Object> e : yamlProps.entrySet()) {
			table.put(e.getKey(), e.getValue());
		}

		// Close the stream while ignoring exceptions
		IOUtils.closeQuietly(io);
	}


	public void setProperty(String k, Object v) {
		table.put(k, v);
	}

	public Object getProperty(String key) {
		return table.get(key);
	}
	
	public String getString(String key) {
		return (String) table.get(key);
	}
	
	public String getDownloadDirectory(){
		return (String) table.get("downloadDirectory");
	}
	
	
	
	public String getResourcesDirectory(){
		return (String) table.get("resourcesDirectory");
	}
	
	
	
	
	
	public String getWordNetURL(){
		return (String) table.get("wordnet.v3.url");
	}
	
	public String getWordNetDir(){
		return (String) table.get("wordnet.v3.dir");
	}
	
	
	
	public String getUniversalWordNetURL(){
		return (String) table.get("wordnet.v3.url");
	}
	
	public String getUniversalWordNetDir(){
		return (String) table.get("wordnet.v3.dir");
	}
	
	public String getVerbNetURL(){
		return (String) table.get("verbnet.32.url");
	}
	
	public String getVerbNetDir(){
		return (String) table.get("verbnet.32.dir");
	}

	
	
	public String getWiktionaryURL(){
		return (String) table.get("wiktionary.rootURL");
	}
	
	public String getBloggerAPIKey(){
		return (String) table.get("blogger.apiKey");
	}
	
	public String getWiktionaryDumpFile(String language){
		return getResourcesDirectory() + "wiktionary/" + language + "-dump.xml";
	}
	//wiktionary.rootURL 

}
