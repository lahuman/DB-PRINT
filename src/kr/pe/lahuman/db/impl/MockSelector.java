package kr.pe.lahuman.db.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kr.pe.lahuman.data.DataMap;
import kr.pe.lahuman.db.DBSelector;

public class MockSelector extends DBSelector {

	@Override
	public Map<String, List<DataMap<String, String>>> getTableInfos(Map<String, String> dbInfo) {
		Map<String, List<DataMap<String, String>>> tableInfos= new LinkedHashMap<String, List<DataMap<String,String>>>();
		List<DataMap<String, String>> table1Info = new ArrayList<DataMap<String,String>>();
		DataMap<String, String> table1ColumnInfo1 = new DataMap<String, String>();
		DataMap<String, String> table1ColumnInfo2 = new DataMap<String, String>();
		
		table1ColumnInfo1.put("TABLE_ID", "TABLE1");
		table1ColumnInfo1.put("TABLE_NAME", "TABLE1");
		table1ColumnInfo1.put("COL_SEQ", "1");
		table1ColumnInfo1.put("COLUMN_ID", "DATA_TYPE_SEQ");
		table1ColumnInfo1.put("COLUMN_NAME", "자료종류SEQ");
		table1ColumnInfo1.put("COLUMN_TYPE", "Number");
		table1ColumnInfo1.put("NULLABLE", "");
		table1ColumnInfo1.put("DATA_DEFAULT", "'Y'");
		table1ColumnInfo1.put("CONSTRAINT_TYPE", "");
		
		table1Info.add(table1ColumnInfo1);
		
		table1ColumnInfo2.put("TABLE_ID", "TABLE1");
		table1ColumnInfo2.put("TABLE_NAME", "TABLE1");
		table1ColumnInfo2.put("COL_SEQ", "1");
		table1ColumnInfo2.put("COLUMN_ID", "DATA_TYPE_SEQ");
		table1ColumnInfo2.put("COLUMN_NAME", "자료종류SEQ");
		table1ColumnInfo2.put("COLUMN_TYPE", "Number");
		table1ColumnInfo2.put("NULLABLE", "Not Null");
		table1ColumnInfo2.put("DATA_DEFAULT", "'Y'");
		table1ColumnInfo2.put("CONSTRAINT_TYPE", "'PK'");
		
		table1Info.add(table1ColumnInfo2);
		
		tableInfos.put("table1", table1Info);
		tableInfos.put("table2", table1Info);
		
		return tableInfos;
	}



}
