package email_filter;

import java.io.IOException;
import java.util.List;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

public class LabelProcessor {
	public String getLabelId(Gmail service, String user, String labelName) throws IOException {
		ListLabelsResponse labelsResponse = service.users().labels().list(user).execute();
		List<Label> labels = labelsResponse.getLabels();
		
		for(Label label: labels) {
			String name = label.getName();
			
			if(name.equals(labelName)) {
				return label.getId();
			}
		}
		
		return null;
	}
}
