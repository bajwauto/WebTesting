package expedia;

import static log.Log.error;
import static log.Log.info;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;

import excel.Excel;
import utils.Utility;

public class Data {

	@DataProvider(name = "excel")
	public Object[][] getDataFromExcel(ITestNGMethod method) {
		String filePath = Utility.getAbsoluteProjectPaths("testData") + File.separator + method.getMethodName()
				+ ".xlsx";
		info("Reading test data from the excel file at locaton - " + filePath);
		Excel excel = new Excel(filePath, "Data");
		List<Map<String, Object>> excelData;
		Object[][] testData = null;
		try {
			excelData = excel.read();
			testData = new Object[excelData.size()][1];
			for (int i = 0; i < excelData.size(); i++)
				testData[i][0] = excelData.get(i);
		} catch (Exception e) {
			error("Test data file not found at path - " + filePath);
			Assert.fail("Test data file not found at path - " + filePath);
		}
		return testData;
	}
}
