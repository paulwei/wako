package com.whl.wako.kernel.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class CachedScript implements Serializable {
	private CompiledScript compiledScript;
	private static final Logger logger = LoggerFactory.getLogger(CachedScript.class);
	public CachedScript() {
	}

	public CachedScript(Compilable scriptEngine, String script) {
		try {
			compiledScript = scriptEngine.compile(script);
		} catch (Exception e) {
			logger.error("scriptEngine compile exception ",e);
		}
	}

	public <T> ScriptResult<T> run(Map<String, Object> context){
		ScriptContext ctx = new SimpleScriptContext();
		if(context!=null){
			put(ctx, context);
		}
		ScriptResult result = new ScriptResult(ctx);
		try {
			Object retval = compiledScript.eval(ctx);
			result.setRetval(retval);
		} catch (ScriptException e) {
			logger.error("compiledScript eval exception ",e);
		}
		return result;
	}

	private void put(ScriptContext ctx, Map<String, Object> context) {
		for (String k : context.keySet()) {
			Object value = context.get(k);
			if(value!=null && !BeanUtils.isSimpleValueType(value.getClass())){
				if(value instanceof Collection){
					ctx.setAttribute(k, BeanUtils.getBeanMapList((Collection) value, true), ScriptContext.ENGINE_SCOPE);
				}else{
					ctx.setAttribute(k, BeanUtils.getBeanMap(value,true), ScriptContext.ENGINE_SCOPE);
				}
			}else{
				ctx.setAttribute(k, value, ScriptContext.ENGINE_SCOPE);
			}
		}
	}
	public static class ScriptResult<T> {
		private ScriptContext ctx;
		private T retval;
		private String errorMsg;
		public ScriptResult(ScriptContext ctx){
			this.ctx = ctx;
		}
		public T getRetval() {
			return retval;
		}
		public Object getAttribute(String name){
			return ctx.getAttribute(name);
		}
		public void setRetval(T retval) {
			this.retval = retval;
		}
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public boolean hasError(){
			return this.errorMsg!=null;
		}
	}
	public CompiledScript getCompiledScript() {
		return compiledScript;
	}

}
