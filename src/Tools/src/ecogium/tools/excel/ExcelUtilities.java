package ecogium.tools.excel;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtilities {
	private static final Logger _logger = Logger.getLogger(ExcelUtilities.class);
	
	public static WritableWorkbook CreateWritableWorkbook(String filename){
		File xlsFile = new File(filename);
		if(xlsFile.exists()){
			xlsFile.delete();
		}
		
		try {
			return Workbook.createWorkbook(xlsFile);
		} catch (IOException e) {
			_logger.error("Erreur lors de la creation du workshop.", e);
			return null;
		}
	}
	
	public static WritableSheet GetOrCreateWritableSheet(WritableWorkbook workBook, String sheetName){
		WritableSheet countrySheet = workBook.getSheet(sheetName);
		if(countrySheet == null){
			int numSheet = workBook.getNumberOfSheets();
			countrySheet = workBook.createSheet(sheetName, numSheet);
		}
		
		return countrySheet;
	}
	
}
