package max.nlp.scrape.yelp;
/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp {

  OAuthService service;
  Token accessToken;

  /**
   * Setup the Yelp API OAuth credentials.
   *
   * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
   *
   * @param consumerKey Consumer key
   * @param consumerSecret Consumer secret
   * @param token Token
   * @param tokenSecret Token secret
   */
  public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }

  /**
   * Search with term and location.
   *
   * @param term Search term
   * @param latitude Latitude
   * @param longitude Longitude
   * @return JSON string response
   */
  public String search(String term, double latitude, double longitude) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", latitude + "," + longitude);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }
  
  public String search(String location) {
	    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/business_review_search");
	    request.addQuerystringParameter("location", location);
	    request.addQuerystringParameter("term", "food");

	    request.addQuerystringParameter("ywsid", "VM6cprFxivTjEhfQsoxKhQ");	    
	    this.service.signRequest(this.accessToken, request);
	    Response response = request.send();
	    return response.getBody();
	  }

  // CLI
  public static void main(String[] args) {
    // Update tokens here from Yelp developers site, Manage API access.
    String consumerKey = "_j35twk7LLc7HxXPr-8Jgg";
    String consumerSecret = "tfrAxjLfQqgCv3XHCWwpUWS6o7s";
    String token = "LswCIcIxhrY5_Xofn3yptonfxw3e6Kpi";
    String tokenSecret = "QgWIqrxFEUVXDAf5ZYU4KRXVNz0";

    Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
//    String response = yelp.search("burritos", 30.361471, -87.164326);
    String response = yelp.search("austin");

    System.out.println(response);
  }
}