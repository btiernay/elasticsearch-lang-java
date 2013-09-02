package org.elasticsearch.script;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.search.lookup.SearchLookup;

/**
 * This class needs to be in this package to expose
 * {@link #setLookup(SearchLookup)}.
 */
public abstract class AbstractJavaScript extends AbstractSearchScript {

	private final Map<String, Object> vars = new HashMap<String, Object>();

	public void setLookup(SearchLookup lookup) {
		super.setLookup(lookup);
	}

	@Override
	public void setNextVar(String name, Object value) {
		vars.put(name, value);
	}

	@Override
	public Object run() {
		return execute();
	}

	protected abstract Object execute();

	protected Object var(String name) {
		return vars.get(name);
	}

	protected Object vars(char name) {
		return vars.get(String.valueOf(name));
	}

	protected Integer integerVar(String name) {
		return (Integer) var(name);
	}

	protected Integer integerVar(char name) {
		return (Integer) vars(name);
	}

	protected Long longVar(String name) {
		return (Long) var(name);
	}
	
	protected Long longVar(char name) {
		return (Long) vars(name);
	}
	
	protected Float floatVar(String name) {
		return (Float) var(name);
	}
	
	protected Float floatVar(char name) {
		return (Float) vars(name);
	}
	
	protected Double doubleVar(String name) {
		return (Double) var(name);
	}
	
	protected Double doubleVar(char name) {
		return (Double) vars(name);
	}
	
}
