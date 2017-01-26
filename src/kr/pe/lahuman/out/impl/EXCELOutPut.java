package kr.pe.lahuman.out.impl;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.pe.lahuman.data.DataMap;
import kr.pe.lahuman.out.OutPutFile;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EXCELOutPut implements OutPutFile {

	@Override
	public void makeOutput(String outputFilePath,
			Map<String, List<DataMap<String, String>>> tableInfos)
			throws Exception {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("테이블 정의서(HTML)");
		
		 //turn off gridlines
        sheet.setDisplayGridlines(false);
        sheet.setPrintGridlines(false);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        
        //the following three statements are required only for HSSF
        sheet.setAutobreaks(true);
        printSetup.setFitHeight((short)1);
        printSetup.setFitWidth((short)1);
        
        Map<String, CellStyle> styles = createStyles(wb);
        
        sheet.setColumnWidth(0, 7*256);
        sheet.setColumnWidth(1, 30*256);
        sheet.setColumnWidth(2, 30*256);
        sheet.setColumnWidth(3, 15*256);
        sheet.setColumnWidth(4, 12*256);
        sheet.setColumnWidth(5, 20*256);
        sheet.setColumnWidth(6, 10*256);
        
		Iterator<Entry<String, List<DataMap<String, String>>>> iterator = tableInfos.entrySet().iterator();
		int rowNum = 1;
		while(iterator.hasNext()){
			Map.Entry<String, List<DataMap<String, String>>> e =  iterator.next();
			String tableId = e.getKey();
			List<DataMap<String, String>> datas = e.getValue();
			
			for(int i=0; i<datas.size(); i++){
				DataMap<String, String> data = datas.get(i);
				if(i==0){
					//MAKE TABLE
					String firstVal = "테이블명";
					String secondVal = data.getString("TABLE_ID");
//					

					rowNum = makeTableInfo(sheet, rowNum, firstVal, secondVal, styles);
					rowNum = makeTableInfo(sheet, rowNum, "테이블 설명", data.getString("TABLE_NAME"), styles);
//					
//					sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0, 1));
//					sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,2, 6));
//					Row tableDesc = sheet.createRow(rowNum++);
//					Cell tableName3 = tableDesc.createCell(0);
//					tableName3.setCellValue("테이블 설명");
//					Cell tableName4 = tableDesc.createCell(2);
//					tableName4.setCellValue(data.getString("TABLE_NAME"));
					
					//column heard
					Row columnHead = sheet.createRow(rowNum++);
					int idx = 0;
					makeHead(columnHead, "번호", idx++, styles);
					makeHead(columnHead, "컬럼명", idx++, styles);
					makeHead(columnHead, "속성명", idx++, styles);
					makeHead(columnHead, "데이터타입", idx++, styles);
					makeHead(columnHead, "NULL여부", idx++, styles);
					makeHead(columnHead, "기본값", idx++, styles);
					makeHead(columnHead, "KEY", idx++, styles);
					
				}
				//make body
				Row columnData= sheet.createRow(rowNum++);
				int idx = 0;
				makeData(columnData, data.getString("COL_SEQ"), idx++, styles, true);
				makeData(columnData, data.getString("COLUMN_ID"), idx++, styles, false);
				makeData(columnData, data.getString("COLUMN_NAME"), idx++, styles, false);
				makeData(columnData, data.getString("COLUMN_TYPE"), idx++, styles, false);
				makeData(columnData, data.getString("NULLABLE"), idx++, styles, true);
				makeData(columnData, data.getString("DATA_DEFAULT"), idx++, styles, false);
				makeData(columnData, data.getString("CONSTRAINT_TYPE"), idx++, styles, true);
				
				
//				String tableColumn = htmlColumn.replaceAll("[$]\\{COL_SEQ\\}",data.getString("COL_SEQ"));
//				tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_ID\\}",data.getString("COLUMN_ID"));
//				if(data.get("COLUMN_NAME") != null){
//					tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_NAME\\}",data.getString("COLUMN_NAME"));
//				}else{
//					tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_NAME\\}",data.getString("COLUMN_ID"));
//				}
//				tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_TYPE\\}",data.getString("COLUMN_TYPE"));
//				tableColumn = tableColumn.replaceAll("[$]\\{NULLABLE\\}",data.getString("NULLABLE"));
//				tableColumn = tableColumn.replaceAll("[$]\\{DATA_DEFAULT\\}",data.getString("DATA_DEFAULT"));
//				tableColumn = tableColumn.replaceAll("[$]\\{CONSTRAINT_TYPE\\}",data.getString("CONSTRAINT_TYPE"));
//				htmlSource += tableColumn ;
			}
			
			rowNum++;
		}
		String file = outputFilePath;
        if(wb instanceof XSSFWorkbook) file += "x";
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();
	}

	private void makeHead(Row columnHead, String val, int idx,  Map<String, CellStyle> styles) {
		Cell column1 = columnHead.createCell(idx);
		column1.setCellValue(val);
		column1.setCellStyle(styles.get("column_title"));
	}

	private void makeData(Row columnHead, String val, int idx,  Map<String, CellStyle> styles, boolean isCenter) {
		Cell column1 = columnHead.createCell(idx);
		column1.setCellValue(val);
		if(isCenter){
			column1.setCellStyle(styles.get("data_center"));
		}else{
			column1.setCellStyle(styles.get("data_left"));
		}
	}
	
	private int makeTableInfo(Sheet sheet, int rowNum, String firstVal,
			String secondVal,  Map<String, CellStyle> styles ) {
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0, 1));
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,2, 6));
		Row tableName = sheet.createRow(rowNum++);
		Cell tableName1 = tableName.createCell(0);
		tableName1.setCellStyle(styles.get("column_title"));
		tableName.createCell(1).setCellStyle(styles.get("column_title"));
		tableName1.setCellValue(firstVal);
		Cell tableName2 = tableName.createCell(2);
		tableName2.setCellStyle(styles.get("data_left"));
		tableName.createCell(3).setCellStyle(styles.get("data_left"));
		tableName.createCell(4).setCellStyle(styles.get("data_left"));
		tableName.createCell(5).setCellStyle(styles.get("data_left"));
		tableName.createCell(6).setCellStyle(styles.get("data_left"));
		tableName2.setCellValue(secondVal);
		return rowNum;
	}

    /**
     * cell styles used for formatting sheets
     */
    private Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

        short borderColor = IndexedColors.BLACK.getIndex();

        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)48);
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont);
        styles.put("title", style);

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short)12);
//        monthFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setTopBorderColor(borderColor);
        
        style.setFont(monthFont);
        styles.put("column_title", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setTopBorderColor(borderColor);
        styles.put("data_left", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(borderColor);
        style.setRightBorderColor(borderColor);
        style.setBottomBorderColor(borderColor);
        style.setTopBorderColor(borderColor);
        styles.put("data_center", style);
        return styles;
    }
}
