package email_filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.ModifyMessageRequest;

public class MessageProcessor {
	private final String filteredEmailLabelId;
	private final Navigator navigator;
	private final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);

	public MessageProcessor(final String filteredEmailLabelId) {
		this.filteredEmailLabelId = filteredEmailLabelId;
		this.navigator = new Navigator();
	}

	public int processMessages(final Gmail service, final String user, final List<Message> messages)
			throws IOException {
		int successCount = 0;
		for (final Message message : messages) {
			final boolean accepted = this.processMessage(service, user, message);
			if (accepted) {
				successCount++;
			}

		}

		return successCount;
	}

	private boolean processMessage(final Gmail service, final String user, final Message message) throws IOException {

		boolean accepted = false;
		final Message fullMessage = getFullMessage(service, user, message);

		// BigInteger historyId = fullMessage.getHistoryId();
		//
		// List<History> histories = new ArrayList<History>();
		// ListHistoryResponse response = service.users().history().list(user)
		// .setStartHistoryId(historyId).execute();
		//
		// histories.addAll(response.getHistory());

		final String body = this.getHTMLBodyPart(fullMessage);

		if (body.contains("Accept this offer")) {
			this.logger.info("Had an email with available offer!");
			final String acceptLink = MessageParser.getAcceptLink(body);

			this.navigator.navigate(acceptLink);

			this.markEmailAsProcessed(service, user, message);
			accepted = true;
		}
		return accepted;
	}

	private static Message getFullMessage(final Gmail service, final String user, final Message message)
			throws IOException {
		return service.users().messages().get(user, message.getId()).execute();
	}

	private void markEmailAsProcessed(final Gmail service, final String user, final Message message)
			throws IOException {
		this.logger.info("Marking mail as processed!");
		final List<String> labelIds = new ArrayList<>();
		labelIds.add(this.filteredEmailLabelId);
		final ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(labelIds);
		service.users().messages().modify(user, message.getId(), mods).execute();
	}

	private String getHTMLBodyPart(final Message fullMessage) {

		String body = "";
		final List<MessagePart> messageParts = fullMessage.getPayload().getParts();

		for (final MessagePart part : messageParts) {

			final String mimeType = part.getMimeType();

			if (mimeType.equals("text/html")) {

				final String encodedBody = part.getBody().getData();

				final byte[] byteDecodedBody = Base64.decodeBase64(encodedBody);
				body = StringUtils.newStringUtf8(byteDecodedBody);

			}
		}

		return body;
	}
}
