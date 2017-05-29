package email_filter;

import java.io.IOException;

import org.junit.Test;

public class NavigatorTests {

	// @Test
	public void navigateHttpsTest() throws IOException {
		final Navigator navigator = new Navigator();
		navigator.navigate("https://fnhw.com/swobid/BidForm.aspx?v=18747&s=5804195&o=322270&a=1");
	}

	@Test
	public void navigateHttpTest() throws IOException {
		final Navigator navigator = new Navigator();
		navigator.navigate("http://fnhw.com/swobid/BidForm.aspx?v=18747&s=5804195&o=322270&a=1");
	}
}
