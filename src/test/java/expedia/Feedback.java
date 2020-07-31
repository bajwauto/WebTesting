package expedia;

import java.util.Map;

import org.testng.annotations.Test;

import support.custom.Retry;

public class Feedback extends Base {
	@Retry
	@Test(dataProviderClass = Data.class, dataProvider = "excel", groups = { "regression",
			"run" }, priority = 4, enabled = true)
	public void websiteFeedback(Map<String, Object> data) {
		openFeedbackPage();
		provideFeedback(data);
	}
}
