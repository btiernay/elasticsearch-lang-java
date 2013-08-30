package org.elasticsearch.script;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.script.AbstractSearchScript;
import org.elasticsearch.search.lookup.SearchLookup;

public abstract class AbstractJavaScript extends AbstractSearchScript {

	private final Map<String, Object> vars = new HashMap<String, Object>();

	/**
	 * This class Needs to be in this package to expose this setter.
	 * @param lookup
	 */
	public void setLookup(SearchLookup lookup) {
		super.setLookup(lookup);
	}

	@Override
	public Object run() {
		try {
			return execute();
		} finally {
			vars.clear();
		}
	}

	protected abstract Object execute();

	protected Object vars(String name) {
		return vars.get(name);
	}

	@Override
	public void setNextVar(String name, Object value) {
		vars.put(name, value);
	}

}
