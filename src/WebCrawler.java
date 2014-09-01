import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.net.URI;
import java.net.URISyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	private static String startURL;
	private static String domainURL;
	public static HashSet<String> urls;
	public static HashSet<String> visited;
	
	public WebCrawler(String url){
		startURL = url;
		domainURL = getDomain(url);
		urls = new HashSet<String>();
		visited = new HashSet<String>();
		urls.add(startURL);
		visited.add(startURL);
	}
	
	public WebCrawler(String url, String domain){
		startURL = url;
		domainURL = domain;
		urls = new HashSet<String>();
		visited = new HashSet<String>();
		urls.add(startURL);
		visited.add(startURL);
	}
	
	public void start(){
		BufferedWriter output = null;
		try {
	          File file = new File("example.txt");
	          output = new BufferedWriter(new FileWriter(file));
	          output.write("something");
	        } catch ( IOException e ) {
	           e.printStackTrace();
        }
		while(urls.size() > 0){
			String current = urls.iterator().next();
			try {
				output.write(current);
				output.newLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			urls.remove(current);
			try {
				System.out.println("Current loading, Total urls: " + urls.size() + "......");
				Document doc = Jsoup.connect(current).get();
				Elements anchors = doc.select("a");
				for(Element anchor : anchors){
					String href = anchor.attr("href");
					href = fixURL(href);
					if(href.startsWith(domainURL) && !visited.contains(href)){
						urls.add(href);
						visited.add(href);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage() + " at:  " + current);
			}
		}
		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getDomain(String url){
		String domain = "";
		URI uri;
		try {
			uri = new URI(url);
			domain = uri.getHost();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(domain != "")
			return "http://" + domain;
		else
			return domain;
	}
	
	public static String fixURL(String url){
		String newURL = url;
		if(!newURL.startsWith(domainURL)){
			if(!newURL.startsWith("http")){
				if(newURL.startsWith("/"))
					newURL = domainURL.concat(newURL);
				else 
					newURL = domainURL.concat("/" + newURL);
			} else {
				/*String tempURL = newURL.split("/")[2];
				if(tempURL.lastIndexOf(".")>10)
					newURL = tempURL.concat(domainURL);*/
/*				newURL = newURL.split("/")[2];*/
			}
		}
		return newURL;
	}
	
	public String getStartURL(){
		return startURL;
	}
	
	public String getDomainURL(){
		return domainURL;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebCrawler cralwer = new WebCrawler("http://www.cs.utah.edu/");
		System.out.println(cralwer.getDomainURL());
		System.out.println("_______________________");
		cralwer.start();
/*		for(Iterator<String> it = cralwer.visited.iterator(); it.hasNext();){
			String temp = it.next();
			System.out.println(temp);
		}*/

	}

}
