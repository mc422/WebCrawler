import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	private static String startURL;
	private static String domainURL;
	public static HashSet<String> urls;
	public static HashSet<String> visited;
	public static HashSet<String> disallows;
	
	public WebCrawler(String url){
		startURL = url;
		domainURL = getDomain(url);
		urls = new HashSet<String>();
		visited = new HashSet<String>();
		urls.add(startURL);
		visited.add(startURL);
		disallows = StandardRobot();
	}
	
	public WebCrawler(String url, String domain){
		startURL = url;
		domainURL = domain;
		urls = new HashSet<String>();
		visited = new HashSet<String>();
		urls.add(startURL);
		visited.add(startURL);
		disallows = StandardRobot();
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
					if(checkURL(href)){
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
	
	/**
	 * method use to process robots.txt file and extract disallow list
	 * @param null
	 * @return HashSet of disallow url link list
	 */
	public static HashSet<String> StandardRobot(){
		String robot = domainURL + "/robots.txt";
		HashSet<String> disallow = new HashSet<String>();
		URL url = null;
		try {
			url = new URL(robot);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty(
	                "User-Agent",
	                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
	        conn.setRequestProperty("Accept", "text/html");
	        
	        InputStream input = conn.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input,
	                "utf-8"));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	        	if(line.startsWith("Disallow")){
	        		int index = line.indexOf('/');
	        		disallow.add(line.substring(index));
	        	}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("No suitable robot.txt file found");
		}
		
		return disallow;
	}
	
	public static StringBuffer processText(String link) throws IOException{
		URL url = new URL(link);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
        conn.setRequestProperty("Accept", "text/html");
        
        InputStream input = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input,
                "utf-8"));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\r\n");
        }
		return sb;
	}
	
	public static String fixURL(String url){
		String newURL = url;
		if(!newURL.startsWith(domainURL)){
			if(!newURL.startsWith("http")){
				if(newURL.startsWith("/"))
					newURL = domainURL.concat(newURL);
				else 
					newURL = domainURL.concat("/" + newURL);
			} 
		}
		return newURL;
	}
	
	/**
	 * method to check if the url follow the rules and is not in the disallow
	 * list of the robot  
	 * @param url too check
	 * @return true means this url should be crawl in the future, false otherwise
	 */
	public static boolean checkURL(String url){
		int length = url.length();
		String suffix = url.substring(length-3, length);
		
		// check if the url has the same web Domian
		if(!url.startsWith(domainURL))
			return false;
		// check if the url has been visited before
		if(visited.contains(url))
			return false;
		// check if the url is a web-attach document
		if(suffix.equals("pdf"))
			return false;
		if(suffix.equals("zip"))
			return false;
		if(suffix.equals("doc"))
			return false;
		// go through disallow list to see if this url is below to one of the disallow
		for(Iterator<String> it=disallows.iterator(); it.hasNext();){
			String disallow = domainURL + it.next();
			disallow = disallow.substring(0, disallow.length()-1);
			if(url.indexOf(disallow)>-1)
				return false;
		}
		return true;
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
		WebCrawler cralwer = new WebCrawler("http://www.cs.utah.edu");
		System.out.println(cralwer.getDomainURL());
		System.out.println("_______________________");
		cralwer.start();
		
/*		for(Iterator<String> it = cralwer.visited.iterator(); it.hasNext();){
			String temp = it.next();
			System.out.println(temp);
		}*/
		

/*		HashSet<String> disa = cralwer.StandardRobot();
		System.out.println(disa.size());
		if(disa.contains("/internal/")){
			System.out.println("good");
		}*/
/*		try {
			System.out.println(cralwer.processText("http://www.cs.utah.edu/robots.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
