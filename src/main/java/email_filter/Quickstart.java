package email_filter;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.model.Thread;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quickstart {
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
	 * If modifying these scopes, delete your previously saved credentials at
	 * rm -rf ~/.credentials/gmail-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_MODIFY);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
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
		InputStream in = Quickstart.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
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
		Credential credential = authorize();
		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		
		// Build a new authorized API client service.
		Gmail service = getGmailService();

		// Print the labels in the user's account.
		String user = "me";
		
		ListLabelsResponse labelsResponse = service.users().labels().list(user).execute();
		List<Label> labels = labelsResponse.getLabels();
		
		for(Label label: labels) {
			String name = label.getName();
			
			if(name.equals("auto_filtered")) {
				System.out.println("label id: " + label.getId());
			}
		}
		while(true) {

			
		ListMessagesResponse listResponse = service.users().messages().list(user)
				.setQ("has:nouserlabels is:unread subject:FNHW Rush Dispatch Offer").execute();
		
		List<Message> messages = listResponse.getMessages();
		if (messages == null || messages.size() == 0) {
			System.out.println("No emails found.");
		} else {
			processMessages(service, user, messages);
		}
		
		java.lang.Thread.sleep(3000);
		}
	}

	private static void processMessages(Gmail service, String user, List<Message> messages) throws IOException {
		System.out.println("emails:");
		for (Message message : messages) {
			Message fullMessage = service.users().messages().get(user, message.getId()).execute();

//				BigInteger historyId = fullMessage.getHistoryId();
//				
//			    List<History> histories = new ArrayList<History>();
//			    ListHistoryResponse response = service.users().history().list(user)
//			        .setStartHistoryId(historyId).execute();
//			    
//			    histories.addAll(response.getHistory());
			
			List<MessagePart> messageParts = fullMessage.getPayload().getParts();

			for (MessagePart part : messageParts) {

				String mimeType = part.getMimeType();

				if (mimeType.equals("text/html")) {

					String body = part.getBody().getData();
					byte[] decodedBody = Base64.decodeBase64(body);
					String decoded1 = StringUtils.newStringUtf8(decodedBody);
					String acceptLink = MessageParser.getAcceptLink(decoded1);
					System.out.printf("- %s\n", decoded1);
					System.out.println("accept link: " + acceptLink);
				}
			}
			
			List<String> labelIds = new ArrayList<>();
			labelIds.add("Label_27");
			ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(labelIds);
			    service.users().messages().modify(user, message.getId(), mods).execute();

		}
	}

}
