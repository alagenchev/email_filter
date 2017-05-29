package email_filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.lang3.StringEscapeUtils;

public class MessageParser {

	public static String getAcceptLink(String body) throws IOException {

		String[] lines = body.split("\n");
		String address = "";

		for (String line : lines) {
			if (line.contains("Accept this offer")) {
				int end = line.indexOf("Accept this offer");
				String beginningString = line.substring(0, end);
				String hrefTag = "a href=\"";
				String closeTag = "\"";
				int indexBeginURL = beginningString.indexOf(hrefTag);
				
				beginningString = beginningString.substring(indexBeginURL + hrefTag.length());
				int closeTagIndex = beginningString.indexOf(closeTag);
				address = beginningString.substring(0, closeTagIndex);
				
				address = StringEscapeUtils.unescapeHtml4(address);
				return address;
			}
		}

		return address;
	}
}
