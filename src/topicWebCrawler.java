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

public class topicWebCrawler {
	
	private static int maxCrawl;
	private static String startURL;
	private static String domainURL;
	public static HashSet<String> urls;
	public static HashSet<String> visited;
	public static HashSet<String> disallows;
	public static HashSet<String> topic;
	
	public topicWebCrawler(String url, int max, HashSet<String> topic){
		maxCrawl = max;
		startURL = url;
		domainURL = getDomain(url);
		urls = new HashSet<String>();
		visited = new HashSet<String>();
		urls.add(startURL);
		visited.add(startURL);
		disallows = StandardRobot();
		this.topic = topic;
	}
	
	public topicWebCrawler(String url, String domain, int max, HashSet<String> topic){
		maxCrawl = max;
		startURL = url;
		domainURL = domain;
		urls = new HashSet<String>();
		visited = new HashSet<String>();
		urls.add(startURL);
		visited.add(startURL);
		disallows = StandardRobot();
		this.topic = topic;
	}
	
	public void start() throws IOException, InterruptedException{		
		int count = 1;
        File file = new File("mode2_URL.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write("URL record of all web crawler");
        output.newLine();
        output.write(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        output.newLine();
        
        while(urls.size() > 0){
	        String current = urls.iterator().next();
			output.write(current);
			output.newLine();
			urls.remove(current);
			
			try {
				System.out.println("Current loading, Total urls: " + urls.size() + "......");
				Document doc = Jsoup.connect(current).get();
				
				String keyURL = current.substring(7).replace('/', '_').replace(':', '_').
						replace('?', '_').replace('*', '_');
				String filename = "download/" + keyURL + ".html";
				file = new File(filename);
				BufferedWriter download = new BufferedWriter(new FileWriter(file));
				download.write(doc.toString());
				download.close();
				count++;
				
				Elements anchors = doc.select("a");
				for(Element anchor : anchors){
					String href = anchor.attr("href");
					href = fixURL(href);
					String innerHtml = anchor.text();
					if(checkTopicWord(innerHtml)){
						//System.out.println(href+ " with text:  " + innerHtml);
						if(checkTopicURL(href)){
							urls.add(href);
							visited.add(href);
						}
					}					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage() + " at:  " + current);
			}
			Thread.sleep(500);
			if(count>maxCrawl)
				break;
        }
		
		output.close();
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
	        boolean start = false;
	        while ((line = reader.readLine()) != null) {
	        	if(!start){
	        		if(line.startsWith("User-agent: *"))
	        			start = true;
	        	} else {
	        		if(line.startsWith("Disallow:")){
	        			int index = line.indexOf('/');
		        		disallow.add(line.substring(index));		
	        		} else {
	        			if(!line.startsWith("Allow"))
	        				start = false;
	        		}
	        	}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("No suitable robot.txt file found");
		}
		
		return disallow;
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
	public static boolean checkTopicURL(String url){
		int length = url.length();
		String suffix = url.substring(length-3, length);
		
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
			//disallow = disallow.substring(0, disallow.length()-1);
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
	
	public static boolean checkTopicWord(String url){
		for(Iterator<String> it = topic.iterator(); it.hasNext();){
			String key = it.next();
			if(url.contains(key))
				return true;
		}
		return false;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		
		HashSet<String> topics = new HashSet<String>();
		topics.add("Day");
		topics.add("slavery");
/*		topics.add("computing");
		topics.add("computer");*/
		
		topicWebCrawler TopicCrawler = new topicWebCrawler("http://en.wikipedia.org", 
				"http://en.wikipedia.org", 20, topics);
		TopicCrawler.start();
		
/*		for(Iterator<String> it = cralwer.visited.iterator(); it.hasNext();){
			String temp = it.next();
			System.out.println(temp);
		}*/
		

/*		HashSet<String> disa = crawler.StandardRobot();
		for(Iterator<String> it = disa.iterator(); it.hasNext();){
			String tmp = it.next();
			System.out.println(tmp);
		}*/
/*		try {
			System.out.println(cralwer.processText("http://www.cs.utah.edu/robots.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
