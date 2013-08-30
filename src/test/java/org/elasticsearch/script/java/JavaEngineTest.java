package org.elasticsearch.script.java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavaEngineTest {

	private static JavaEngineService engine;

	@BeforeClass
	public static void setup() {
		engine = new JavaEngineService(ImmutableSettings.Builder.EMPTY_SETTINGS);
	}

	@AfterClass
	public static void close() {
		engine.close();
	}

	@Test
	public void testSimpleEquation() {
		Map<String, Object> vars = new HashMap<String, Object>();
		Object o = engine.execute(engine.compile("return 1 + 2;"), vars);
		assertThat(((Number) o).intValue(), equalTo(3));
	}

	@Test
	public void testSimpleVarAccess() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return vars(\"x\");"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

}
