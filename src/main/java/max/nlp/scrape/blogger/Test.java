package max.nlp.scrape.blogger;

import max.nlp.dal.blog.blogger.BlogAuthorProfile;

public class Test {

	public static void main(String[] args) {
		BlogAuthorProfile profile = BloggerProfileScraper.scrapeProfile("http://www.blogger.com/profile/09098191782293764634");
		System.out.println("la");
	}
}
