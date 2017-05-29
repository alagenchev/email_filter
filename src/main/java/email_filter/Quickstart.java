package email_filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class Quickstart {
	private static final Logger logger = LoggerFactory.getLogger(Quickstart.class);

	/** Application name. */
	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/gmail-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at rm
	 * -rf ~/.credentials/gmail-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_MODIFY);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (final Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 *
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		final InputStream in = Quickstart.class.getResourceAsStream("/client_secret.json");
		final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		final Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
				.authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Gmail client service.
	 *
	 * @return an authorized Gmail client service
	 * @throws IOException
	 */
	public static Gmail getGmailService() throws IOException {
		final Credential credential = authorize();
		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	public static void main(final String[] args) throws IOException, InterruptedException {

		// Build a new authorized API client service.
		final Gmail service = getGmailService();

		// Print the labels in the user's account.
		final String user = "me";

		final LabelProcessor labelProcessor = new LabelProcessor();
		final String filteredEmailLabelId = labelProcessor.getLabelId(service, user, "auto_filtered");

		final MessageProcessor messageProcessor = new MessageProcessor(filteredEmailLabelId);

		int totalCount = 0;
		while (totalCount < 3) {

			int acceptedCount = 0;

			try {
				acceptedCount = processEmails(service, user, messageProcessor);
			} catch (final Exception ex) {
				logger.error("error processing emails", ex);
			}
			totalCount += acceptedCount;

			java.lang.Thread.sleep(10000);
		}
	}

	private static int processEmails(final Gmail service, final String user, final MessageProcessor messageProcessor)
			throws IOException {
		final ListMessagesResponse listResponse = service.users().messages().list(user)
				.setQ("has:nouserlabels is:unread subject:FNHW Rush Dispatch Offer").execute();

		final List<Message> messages = listResponse.getMessages();
		if (messages == null || messages.size() == 0) {
			logger.info("No emails found.");
			return 0;
		} else {
			logger.info(messages.size() + " emails found!");
			final int acceptedCount = messageProcessor.processMessages(service, user, messages);

			return acceptedCount;
		}
	}

}
