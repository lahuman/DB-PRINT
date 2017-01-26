package kr.pe.lahuman.data;

import java.util.HashMap;

public class DataMap<K, V> extends HashMap<K, V> {

	
	public String getString(String key){
		return this.get(key)==null?"":this.get(key).toString();
	}
	
}
