package kr.pe.lahuman.test;

import java.util.HashMap;
import java.util.Map;

import kr.pe.lahuman.db.DBSelector;
import kr.pe.lahuman.factory.FactoryBean;
import kr.pe.lahuman.out.OutPutFile;

public class TestCase {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	    
//		String serverName = dbInfo.get("serverName"); 
//	    String portNumber = dbInfo.get("serverPort"); 
//	    String sid = dbInfo.get("sid");     
//	    String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
//	    String username = dbInfo.get("userId");
//	    String password = dbInfo.get("password");
	    
		Map<String, String> dbInfo = new HashMap<String, String>();
		//oracle info
//		dbInfo.put("serverName", "192.168.47.128");
//		dbInfo.put("serverPort", "1521");
//		dbInfo.put("sid", "orcl");
//		dbInfo.put("userId", "lahuman");
//		dbInfo.put("password", "dlarhkdrb");
		
		//mysql info
		dbInfo.put("serverName", "lahuman.pe.kr");
		dbInfo.put("serverPort", "3306");
		dbInfo.put("sid", "vidio_english");
		dbInfo.put("userId", "vidioenglish");
		dbInfo.put("password", "vidioenglish");
		dbInfo.put("tableFilter", "rea%");
		
		DBSelector db = FactoryBean.getInstanceDB("Mysql");
		OutPutFile file = FactoryBean.getInstanceFile("Html");
		file.makeOutput("C:\\TEST_.HTML", db.getTableInfos(dbInfo));
	}

}
