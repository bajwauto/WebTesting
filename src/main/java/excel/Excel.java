package excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	private boolean fileExists;
	private String sheetName;
	private File file;

	/**
	 * This method is used to get the coordinates of a cell in form of a Map
	 * containing the keys "row" and "column"
	 * 
	 * @param row    - zero-based row index of the cell
	 * @param column - zero-based column index of the cell
	 * @return Map containing the row and the column index values in the "row" and
	 *         "column" keys respectively
	 */
	private static Map<String, Integer> cell(int row, int column) {
		Map<String, Integer> cellCoords = new HashMap<String, Integer>();
		cellCoords.put("row", row);
		cellCoords.put("column", column);
		return cellCoords;
	}

	/**
	 * This method is used to fetch the value from the given cell
	 * 
	 * @param cell - reference to the desired cell
	 * @return Object reference to the value contained in the cell
	 */
	private static Object getCellValue(Cell cell) {
		Object cellValue;
		switch (cell.getCellType()) {
		case BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		case NUMERIC:
			cellValue = cell.getNumericCellValue();
			break;
		case _NONE:
		case STRING:
		default:
			cellValue = cell.getStringCellValue();
		}
		return cellValue;
	}

	public Excel(String workbookPath, String sheetName) {
		this.sheetName = sheetName;
		this.file = new File(workbookPath);
		fileExists = file.exists();
	}

	public Excel(String workbookPath) {
		this(workbookPath, "Sheet1");
	}

	/**
	 * This method is used to write a String value to cell in an Excel Sheet
	 * 
	 * @param cellCoords - Map containing information about cell's coordinates which
	 *                   is to be edited
	 * @param value      - String value to write to an excel cell
	 * @throws IOException
	 */
	public void write(Map<String, Integer> cellCoords, String value) throws IOException {
		FileOutputStream fos;
		if (!fileExists) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet worksheet = workbook.createSheet(sheetName);
			Row row = worksheet.createRow(cellCoords.get("row"));
			Cell cell = row.createCell(cellCoords.get("column"));
			cell.setCellValue(value);
			fos = new FileOutputStream(file);
			workbook.write(fos);
			fos.close();
			workbook.close();
		} else {
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet worksheet = workbook.getSheet(sheetName);
			if (worksheet == null)
				worksheet = workbook.createSheet(sheetName);
			Row row = worksheet.getRow(cellCoords.get("row"));
			if (row == null)
				row = worksheet.createRow(cellCoords.get("row"));
			Cell cell = row.getCell(cellCoords.get("column"));
			if (cell == null)
				cell = row.createCell(cellCoords.get("column"));
			cell.setCellValue(value);
			fos = new FileOutputStream(file);
			workbook.write(fos);
			fis.close();
			fos.close();
			workbook.close();
		}
	}

	/**
	 * This method is used to read all the data present in an excel sheet with 1st
	 * row containing the names of column headers
	 * 
	 * @return - List of Map between column names and column values. Each item in
	 *         the list will represent the data present in a single row in form of a
	 *         mapping between the column header name and the column value. The size
	 *         of the list is same as the no. of rows in the excel sheet.
	 * @throws IOException
	 */
	public List<Map<String, Object>> read() throws IOException {
		List<Map<String, Object>> excelData = new ArrayList<Map<String, Object>>();
		if (!fileExists)
			excelData = null;
		else {
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet worksheet = workbook.getSheet(sheetName);
			if (worksheet == null)
				excelData = null;
			else {
				Iterator<Row> rows = worksheet.rowIterator();
				Row headerRow = worksheet.getRow(0);
				while (rows.hasNext()) {
					Row row = rows.next();
					if (row.getRowNum() == 0)
						continue;
					else {
						Map<String, Object> rowData = new LinkedHashMap<String, Object>();
						Iterator<Cell> cells = row.cellIterator();
						while (cells.hasNext()) {
							Cell cell = cells.next();
							Cell headerCell = headerRow.getCell(cell.getColumnIndex());
							rowData.put((String) getCellValue(headerCell), getCellValue(cell));
						}
						excelData.add(rowData);
					}
				}
			}
			workbook.close();
			fis.close();
		}
		return excelData;
	}

	/**
	 * This method is used to read the data present in the given row of an excel
	 * sheet
	 * 
	 * @param row - zero-based index of the row whose data is to be read
	 * @return - Row data in form of mapping between column names and column values
	 *         for the given row
	 * @throws IOException
	 */
	public Map<String, Object> read(int row) throws IOException {
		Map<String, Object> rowData = new HashMap<String, Object>();
		List<Map<String, Object>> excelData = read();
		if (excelData == null)
			rowData = null;
		else if (excelData.size() < row)
			rowData = null;
		else
			rowData = excelData.get(row - 1);
		return rowData;
	}

	/**
	 * This method is used to read the value from a particular column of the given
	 * row of an excel sheet
	 * 
	 * @param row          - zero-based row index
	 * @param columnHeader - name of the column from where data is to be fetched
	 * @return - value of the desired cell represented by row Index and column name
	 * @throws IOException
	 */
	public Object read(int row, String columnHeader) throws IOException {
		Object cellData;
		Map<String, Object> rowData = read(row);
		if (rowData == null)
			cellData = null;
		else if (!rowData.containsKey(columnHeader))
			cellData = null;
		else
			cellData = rowData.get(columnHeader);
		return cellData;
	}

	/**
	 * This method is used to read the value from a particular cell represented by
	 * its row and column indices
	 * 
	 * @param row    - zero-based row index
	 * @param column - zero-based column index
	 * @return - value read from a cell represented by its row and column indices
	 * @throws IOException
	 */
	public Object read(int row, int column) throws IOException {
		return read(cell(row, column));
	}

	/**
	 * This method is used to read the value from a particular cell represented by
	 * its row and column indices
	 * 
	 * @param cellCoords - Map containing information about cell's coordinates which
	 *                   is to be read
	 * @return - value read from a cell represented by its row and column indices
	 * @throws IOException
	 */
	public Object read(Map<String, Integer> cellCoords) throws IOException {
		Object cellData = new Object();
		int row = cellCoords.get("row");
		int column = cellCoords.get("column");
		Map<String, Object> rowData = read(row);
		if (rowData == null)
			cellData = null;
		else {
			if (rowData.size() < column + 1)
				cellData = null;
			else {
				int counter = 0;
				for (Map.Entry<String, Object> entry : rowData.entrySet()) {
					if (counter != column) {
						++counter;
						continue;
					} else {
						cellData = entry.getValue();
						break;
					}
				}
			}
		}
		return cellData;
	}
}
