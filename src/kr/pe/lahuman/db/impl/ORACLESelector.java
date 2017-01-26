package kr.pe.lahuman.db.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.pe.lahuman.data.DataMap;
import kr.pe.lahuman.db.DBSelector;

public class ORACLESelector extends DBSelector {

	String driverClass = "oracle.jdbc.driver.OracleDriver";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String getTableQuery = "SELECT TABLE_NAME FROM USER_ALL_TABLES";
	
	String getTableInfoQuery = "SELECT A.TABLE_NAME TABLE_ID "+
			"      ,NVL((SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME = A.TABLE_NAME), a.table_name) TABLE_NAME "+
			"      ,B.COLUMN_ID COL_SEQ "+
			"      ,B.COLUMN_NAME COLUMN_ID "+
			"      ,NVL((SELECT COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME = A.TABLE_NAME AND COLUMN_NAME = B.COLUMN_NAME), B.COLUMN_NAME) COLUMN_NAME "+
			"      ,B.DATA_TYPE||'('||DATA_LENGTH||')' COLUMN_TYPE "+
			"      ,B.NULLABLE "+
			"      , B.DATA_DEFAULT DATA_DEFAULT "+
			"      , ( "+
			"          SELECT max(C.constraint_type ) "+
			"          FROM   "+
			"            user_constraints C "+
			"           , user_cons_columns D "+
			"           WHERE A.TABLE_NAME = C.TABLE_NAME "+
			"            AND   A.TABLE_NAME = D.TABLE_NAME "+
			"            AND   B.column_name = D.column_name "+
			"            AND   D.constraint_name = C.constraint_name "+
			"            AND C.constraint_type IN ('R', 'P') "+
			"        ) constraint_type "+
			"FROM  USER_ALL_TABLES  A "+
			"     ,USER_TAB_COLUMNS B "+
			"WHERE B.TABLE_NAME = A.TABLE_NAME "+
			"AND   A.TABLE_NAME = ? "+
			" ORDER BY table_id,col_seq";
	
	
	
	@Override
	public Map<String, List<DataMap<String, String>>> getTableInfos(Map<String, String> dbInfo) throws Exception{
	
		Map<String, List<DataMap<String, String>>> resultMap = new LinkedHashMap<String, List<DataMap<String,String>>>();
		try {
			Class.forName(driverClass);
			   // Create a connection to the database
		    String serverName = dbInfo.get("serverName"); 
		    String portNumber = dbInfo.get("serverPort"); 
		    String sid = dbInfo.get("sid");     
		    String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
		    String username = dbInfo.get("userId");
		    String password = dbInfo.get("password");
		    con = DriverManager.getConnection(url, username, password);
		    //table 목록 가져오기
			if(isTableFilter(dbInfo)){
				getTableQuery += 	" WHERE TABLE_NAME LIKE ?" ;
			}
		    pstmt = con.prepareStatement(getTableQuery);
		    if(isTableFilter(dbInfo)){
				pstmt.setString(1, dbInfo.get("tableFilter"));
			}
		    rs = pstmt.executeQuery();
		    while(rs.next()){
		    	resultMap.put(rs.getString("TABLE_NAME"), new ArrayList<DataMap<String,String>>());
		    }
		    
		    rs.close();
		    pstmt.close();
		    
		    //table 정보 만들기
		    Iterator<Entry<String, List<DataMap<String, String>>>> iterator = resultMap.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<String, List<DataMap<String, String>>> e =  iterator.next();
				String tableId = e.getKey();
				List<DataMap<String, String>> datas = e.getValue();
				pstmt = con.prepareStatement(getTableInfoQuery);	
				pstmt.setString(1, tableId);
				rs = pstmt.executeQuery();
				
				 while(rs.next()){
					 DataMap<String, String> data = new DataMap<String, String>();
				    data.put("TABLE_ID",rs.getString("TABLE_ID") );
				    data.put("TABLE_NAME",rs.getString("TABLE_NAME") );
				    data.put("COL_SEQ",rs.getString("COL_SEQ") );
				    data.put("COLUMN_ID",rs.getString("COLUMN_ID") );
				    data.put("COLUMN_NAME",rs.getString("COLUMN_NAME") );
				    data.put("COLUMN_TYPE",rs.getString("COLUMN_TYPE") );
				    data.put("NULLABLE",rs.getString("NULLABLE") );
				    data.put("DATA_DEFAULT",rs.getString("DATA_DEFAULT") );
				    data.put("CONSTRAINT_TYPE",rs.getString("CONSTRAINT_TYPE") );
				    datas.add(data);
				  }
				 
				 rs.close();
				 pstmt.close();
			}
			
			  
			    
			
		    
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(con != null){
				try {
					con.close();
					con = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}		
		}
		
		
		return resultMap;
	}




}
