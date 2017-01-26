package kr.pe.lahuman.out;

import java.util.List;
import java.util.Map;

import kr.pe.lahuman.data.DataMap;

public interface OutPutFile {
	
	void makeOutput(String outputFilePath, Map<String, List<DataMap<String, String>>> tableInfos) throws Exception;
	
}
