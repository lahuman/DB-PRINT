package kr.pe.lahuman.out.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.pe.lahuman.data.DataMap;
import kr.pe.lahuman.out.OutPutFile;

public class HTMLOutPut implements OutPutFile {

	private FileOutputStream fos = null;

	@Override
	public void makeOutput(String outputFilePath,
			Map<String, List<DataMap<String, String>>> tableInfos) throws Exception {
		
		final String htmlHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>테이블 정의서(HTML)</title><style type=\"text/css\"><!--.entity{	width: 1024px;	margin-bottom: 50px;	page-break-before: always;}.entity-header tr td, .entity-header tr th{	text-align: left;	padding-left: 5px;	padding-right: 5px;	vertical-align: top;}.entity-header{	border-spacing: 0px;	border-top: solid 2px black;	margin:0px;}.column-list{	width: 100%;	border-spacing: 0px;	margin: 0px;	border-bottom: solid 2px black;}.column-list th{	border-bottom: solid 2px black;	border-top: solid 2px black;	text-align: left;}.column-list th, .column-list td{	padding-left: 5px;	padding-right: 5px;	vertical-align: top;}.under-line{	border-bottom: solid 1px gray;}.right-line{	border-right: solid 1px gray;}.left-line{	border-left: solid 1px gray;}th{	background-color: #eee;}--></style></head><body>";
		final String htmlFooter = "</body></html>";
		
		final String htmlTableHeader = "<table class=\"entity\" cellpadding=\"0\" cellspacing=\"0\">		<tr>			<td>				<table width=\"100%\" class=\"entity-header\">					<tr>						<th class=\"under-line right-line\" width=200px>테이블명</th>						<td class=\"under-line right-line\">${TABLE_ID}</td>					</tr>					<tr>						<th class=\"right-line\">테이블 설명</th>						<td >${TABLE_NAME}</td>					</tr>				</table>			</td>		</tr>		<tr>			<td>				<table class=\"column-list\">					<tr>						<th class=\"right-line\" width=\"40px\">번호</th>						<th class=\"right-line\">컬럼명</th>						<th class=\"right-line\">속성명</th>					<th class=\"right-line\">데이터타입</th>						<th class=\"right-line\">NULL여부</th>						<th class=\"right-line\">기본값</th>						<th>KEY</th>					</tr>			";
		final String htmlColumn = "					<tr>						<td class=\"right-line\" style=\"text-align:right;\">${COL_SEQ}</td>						<td class=\"right-line\">${COLUMN_ID}</td>						<td class=\"right-line\">${COLUMN_NAME}</td>						<td class=\"right-line\">${COLUMN_TYPE}</td>						<td class=\"right-line\">${NULLABLE}</td>						<td class=\"right-line\">${DATA_DEFAULT}</td>						<td>${CONSTRAINT_TYPE}</td>					</tr>";
		final String htmlTableFooter ="</table>			</td>		</tr>	</table>";
		
		File file = new File(outputFilePath);
		try{
			String htmlSource = htmlHeader;
			
			Iterator<Entry<String, List<DataMap<String, String>>>> iterator = tableInfos.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<String, List<DataMap<String, String>>> e =  iterator.next();
				String tableId = e.getKey();
				List<DataMap<String, String>> datas = e.getValue();
				
				for(int i=0; i<datas.size(); i++){
					DataMap<String, String> data = datas.get(i);
					if(i==0){
						//make table header
						String tableHeader = htmlTableHeader.replaceAll("[$]\\{TABLE_ID\\}", data.getString("TABLE_ID"));
						tableHeader = tableHeader .replaceAll("[$]\\{TABLE_NAME\\}", data.getString("TABLE_NAME"));
						htmlSource += tableHeader;
					}
					String tableColumn = htmlColumn.replaceAll("[$]\\{COL_SEQ\\}",data.getString("COL_SEQ"));
					tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_ID\\}",data.getString("COLUMN_ID"));
					if(data.get("COLUMN_NAME") != null){
						tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_NAME\\}",data.getString("COLUMN_NAME"));
					}else{
						tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_NAME\\}",data.getString("COLUMN_ID"));
					}
					tableColumn = tableColumn.replaceAll("[$]\\{COLUMN_TYPE\\}",data.getString("COLUMN_TYPE"));
					tableColumn = tableColumn.replaceAll("[$]\\{NULLABLE\\}",data.getString("NULLABLE"));
					tableColumn = tableColumn.replaceAll("[$]\\{DATA_DEFAULT\\}",data.getString("DATA_DEFAULT"));
					tableColumn = tableColumn.replaceAll("[$]\\{CONSTRAINT_TYPE\\}",data.getString("CONSTRAINT_TYPE"));
					htmlSource += tableColumn ;
				}
				htmlSource += htmlTableFooter;
			}
			
			htmlSource += htmlFooter;
			fos = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF8");
			try
			    {                       
			            out.write(htmlSource);
			            out.flush();
			    } finally
			    {
			            out.close();
			    }
		}catch(Exception e){
			throw e;
		}finally{
			if(fos != null){
				fos.close();
			}
		}
		
		
		
		
	}

}
