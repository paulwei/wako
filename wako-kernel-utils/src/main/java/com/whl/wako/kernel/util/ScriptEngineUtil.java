package com.whl.wako.kernel.util;


import org.apache.commons.codec.digest.DigestUtils;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScriptEngineUtil {
	private static Compilable engine;
	static{
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = (Compilable) manager.getEngineByName("groovy");
	}
	private static Map<String, CachedScript> cacheMap = new LinkedHashMap<String, CachedScript>(500, 1, true){
		@Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 500;
        }
    };
	public static synchronized CachedScript buildCachedScript(String script, boolean cached) {
		String key = DigestUtils.md5Hex(script);
		CachedScript cs = cacheMap.get(key);
		if(cs == null){
			cs = new CachedScript(engine,script);
			if(cached) {
				cacheMap.put(key, cs);
			}
		}
		return cs;
	}
	public static ScriptEngine getEngine() {
        return (ScriptEngine) engine;
    }
}
