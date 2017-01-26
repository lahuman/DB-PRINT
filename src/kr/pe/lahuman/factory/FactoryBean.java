package kr.pe.lahuman.factory;

import kr.pe.lahuman.db.DBSelector;
import kr.pe.lahuman.out.OutPutFile;

public class FactoryBean {

	private FactoryBean(){
		
		
	};
	
	
	public static DBSelector getInstanceDB(String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String classPath = "kr.pe.lahuman.db.impl."+type+"Selector";
		Class<?> dbSelector = Class.forName(classPath);
		DBSelector dbSelect= (DBSelector) dbSelector.newInstance();
		return dbSelect;
	}
	
	public static OutPutFile getInstanceFile(String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String classPath = "kr.pe.lahuman.out.impl."+type+"OutPut";
		Class<?> object = Class.forName(classPath);
		OutPutFile outPutFile= (OutPutFile) object.newInstance();
		return outPutFile;
	}
}
