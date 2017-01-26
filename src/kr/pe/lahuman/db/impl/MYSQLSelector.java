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

public class MYSQLSelector extends DBSelector {

	String driverClass = "com.mysql.jdbc.Driver";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String getTableQuery = "show table status ";

	String getTableInfoQuery = "SHOW FULL COLUMNS FROM ";

	@Override
	public Map<String, List<DataMap<String, String>>> getTableInfos(
			Map<String, String> dbInfo) throws Exception{
		Map<String, List<DataMap<String, String>>> resultMap = new LinkedHashMap<String, List<DataMap<String, String>>>();
		Map<String, String> tableComment = new HashMap<String, String>();

		try {
			Class.forName(driverClass);
			// Create a connection to the database
			String serverName = dbInfo.get("serverName");
			String portNumber = dbInfo.get("serverPort");
			String sid = dbInfo.get("sid");
			String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/"
					+ sid;
			String username = dbInfo.get("userId");
			String password = dbInfo.get("password");
			con = DriverManager.getConnection(url, username, password);
			// table 목록 가져오기
			if(isTableFilter(dbInfo)){
				getTableQuery += 	"LIKE ?" ;
			}
			pstmt = con.prepareStatement(getTableQuery);
			if(isTableFilter(dbInfo)){
				pstmt.setString(1, dbInfo.get("tableFilter"));
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				resultMap.put(rs.getString(1),
						new ArrayList<DataMap<String, String>>());
				tableComment.put(rs.getString(1), rs.getString(18));
			}

			rs.close();
			pstmt.close();

			// table 정보 만들기
			Iterator<Entry<String, List<DataMap<String, String>>>> iterator = resultMap
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, List<DataMap<String, String>>> e = iterator
						.next();
				String tableId = e.getKey();
				List<DataMap<String, String>> datas = e.getValue();
				pstmt = con.prepareStatement(getTableInfoQuery + tableId);
				rs = pstmt.executeQuery();
				int i = 1;
				while (rs.next()) {
					DataMap<String, String> data = new DataMap<String, String>();
					data.put("TABLE_ID", tableId);
					data.put("TABLE_NAME", tableComment.get(tableId));
					data.put("COL_SEQ", (i++) + "");
					data.put("COLUMN_ID", rs.getString(1));
					data.put("COLUMN_NAME", rs.getString(9));
					data.put("COLUMN_TYPE", rs.getString(2));
					data.put("NULLABLE", rs.getString(4));
					data.put("DATA_DEFAULT", rs.getString(6));
					data.put("CONSTRAINT_TYPE", rs.getString(5));
					datas.add(data);
				}

				rs.close();
				pstmt.close();
			}

		} catch (Exception  e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
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
