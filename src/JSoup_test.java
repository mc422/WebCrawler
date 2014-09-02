import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JSoup_test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Document doc = Jsoup.connect("http://www.sparkbrowser.com/").get();
		Elements anchors = doc.select("a");
		for(Element anchor : anchors){
			System.out.println(anchor.attr("href"));
		}
		
		TreeSet<String> temp = new TreeSet<String>();
		HashSet<String> test = new HashSet<String>();
		


	}

}
