package email_filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import org.junit.Assert;

public class MessageParserTests {
	
	@Test
	public void testParse() throws IOException {
		
		String myCurrentDir = System.getProperty("user.dir") + "/tst/email_filter/";
		File file = new File(myCurrentDir + "messageBody");
		
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		String message = new String(data, "UTF-8");
		String acceptLink = MessageParser.getAcceptLink(message);
		Assert.assertEquals("http://fnhw.com/swobid/BidForm.aspx?v=18747&amp;s=5804195&amp;o=322270&amp;a=1", acceptLink);
	}
}
