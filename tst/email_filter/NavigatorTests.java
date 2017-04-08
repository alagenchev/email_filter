package email_filter;

import java.io.IOException;

import org.junit.Test;

public class NavigatorTests {
	
	@Test
	public void test() throws IOException {
		Navigator.navigate("https://fnhw.com/swobid/BidForm.aspx?v=18747&s=5804195&o=322270&a=1");
	}
}
