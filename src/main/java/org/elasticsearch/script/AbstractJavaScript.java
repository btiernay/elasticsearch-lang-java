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

	protected Object vars(String name) {
		return vars.get(name);
	}

	protected Object vars(char name) {
		return vars.get(String.valueOf(name));
	}

}
