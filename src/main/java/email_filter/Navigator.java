package email_filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Navigator {
	public static void navigate(String address) throws IOException {
		URL url = new URL(address);
		   HttpsURLConnection test = (HttpsURLConnection) url.openConnection();
		    test.addRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko" );
		    test.addRequestProperty( "Accept" , "text/html,application/xhtml+xml,application/xml,image/png, image/svg+xml,;q=0.9,*/*;q=0.8");
		    test.addRequestProperty( "Accept-Charset" , "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		    test.addRequestProperty( "Accept-Language" , "en-US,en;q=0.8" );
		    test.addRequestProperty( "Connection" , "close" );
		    test.setRequestMethod("GET");


		    test.setInstanceFollowRedirects(true);
		    test.connect();
		    
		    print_content(test);

		    // Nopes DONT TRY THIS
		    //in = new BufferedReader(new InputStreamReader(url.openStream()));

		    BufferedReader in = new BufferedReader( new InputStreamReader( test.getInputStream() ) );      
		    String htmlContent = "";
		    for( String inputLine = ""; ( inputLine = in.readLine() ) != null; )
		        htmlContent += inputLine;
		    System.out.println( htmlContent );
	}
	
	   private static void print_content(HttpsURLConnection con){
			if(con!=null){

			try {

			   System.out.println("****** Content of the URL ********");
			   BufferedReader br =
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));

			   String input;

			   while ((input = br.readLine()) != null){
			      System.out.println(input);
			   }
			   br.close();

			} catch (IOException e) {
			   e.printStackTrace();
			}

		       }

		   }

}
