package expedia;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.testng.ITestNGMethod;
import org.testng.annotations.DataProvider;

import excel.Excel;
import utils.Utility;

public class Data {

	@DataProvider(name = "excel")
	public Object[][] getDataFromExcel(ITestNGMethod method) throws IOException {
		String filePath = Utility.getAbsoluteProjectPaths("testData") + File.separator + method.getMethodName()
				+ ".xlsx";
		Excel excel = new Excel(filePath, "Data");
		List<Map<String, Object>> excelData = excel.read();
		Object[][] testData = new Object[excelData.size()][1];
		for (int i = 0; i < excelData.size(); i++)
			testData[i][0] = excelData.get(i);
		return testData;
	}
}
