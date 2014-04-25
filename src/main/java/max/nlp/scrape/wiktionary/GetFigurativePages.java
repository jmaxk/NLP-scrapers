package max.nlp.scrape.wiktionary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetFigurativePages {

	// http://en.wiktionary.org/wiki/Category:English_non-idiomatic_translation_targets

	// maybe useful
	// http://en.wiktionary.org/wiki/Category:English_terms_with_placeholder_%22it%22
	public static void main(String[] args) {
		List<String> cats = new ArrayList<String>() {
			{
				add("Category:English_aphorisms"); // 25
				add("Category:English_calques"); // 101
				add("Category:English_catchphrases"); // 7
				add("Category:English_colloquialisms"); // 2000
				add("Category:English_clausal_phrases"); // 4
				add("Category:English_deixes");
				add("Category:English_euphemisms"); // 606,only look at													// mutliword
				add("Category:English_idioms"); // 7000
				add("Category:English_live_metaphors"); // 10
				add("Category:English_phrasal_prepositions"); // 43
				add("Category:English_phrasal_verbs"); // 2100
				add("Category:English_phrases"); // 1359
				add("Category:English_rhetorical_questions"); // 65
				add("Category:English_similes"); // 233
				add("Category:English_slang"); // 6491, need to look at informal
												// def only ora slang
				add("Category:English_subordinate_clauses");
				

				//cultural references 
				add("Category:English_allusions"); // 17

			}
		};
		try {
			PrintWriter w = new PrintWriter(new FileWriter(new File(
					"/home/jmaxk/temp/fig.txt")));
			for (String category : cats) {
				APIScraper s = new APIScraper(category);
				s.scrapeCategory();
				ArrayList<String> pages = s.getTitles();
				w.println(category);
				pages.forEach((page) -> w.println("\t" + page));
				w.flush();
			}
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//slang, colloquail
}
