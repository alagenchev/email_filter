package email_filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Navigator {
	private final Logger logger = LoggerFactory.getLogger(Navigator.class);

	public void navigate(final String urlString) throws IOException {

		final URL url = this.getHttpsURL(urlString);
		final HttpsURLConnection test = (HttpsURLConnection) url.openConnection();
		test.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
		test.addRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml,image/png, image/svg+xml,;q=0.9,*/*;q=0.8");
		test.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		test.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		test.addRequestProperty("Connection", "close");
		test.setRequestMethod("GET");

		test.setInstanceFollowRedirects(true);
		test.connect();

		this.print_content(test);
	}

	private URL getHttpsURL(final String urlString) throws MalformedURLException {
		URL url = null;

		if (urlString.startsWith("https:\\")) {
			url = new URL(urlString);
		} else {
			final String httpsUrlString = urlString.replaceFirst("http", "https");
			url = new URL(httpsUrlString);
		}

		return url;
	}

	private void print_content(final HttpsURLConnection con) {
		if (con != null) {

			try {

				final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null) {
					if (input.contains("ResultsLabel")) {
						this.logger.info("Navigated Page:" + input);
					}
				}
				br.close();

			} catch (final IOException e) {
				this.logger.error("error while printing the navigated to page", e);
			}

		}

	}

}
