package kr.pe.lahuman.db;

import java.util.List;
import java.util.Map;

import kr.pe.lahuman.data.DataMap;

public abstract class DBSelector {

	public abstract Map<String, List<DataMap<String, String>>> getTableInfos(Map<String, String> dbInfo)throws Exception;
	
	protected boolean isTableFilter(Map<String, String> dbInfo) {
		return dbInfo.get("tableFilter")!= null && !"".equals(dbInfo.get("tableFilter"));
	}
}
