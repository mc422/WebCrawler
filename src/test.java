import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;


public class test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		URL url = new URL("http://www.cs.utah.edu");
		
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
        
        //String all = sb.toString();
        System.out.println(sb);
        
        String temp = "https://www.google.com/";
        if(temp.startsWith("http")){
        	System.out.println("good ");
        }
	}

}
