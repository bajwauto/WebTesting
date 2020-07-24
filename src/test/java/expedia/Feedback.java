package expedia;

import java.util.Map;

import org.testng.annotations.Test;

public class Feedback extends Base {
	@Test(dataProviderClass = Data.class, dataProvider = "excel", groups = { "run" }, priority = 4, enabled = true)
	public void websiteFeedback(Map<String, Object> data) {
		openFeedbackPage();
		provideFeedback(data);
	}
}
