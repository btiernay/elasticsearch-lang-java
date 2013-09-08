package org.elasticsearch.script.java;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavaEngineServiceTest {

	private static JavaEngineService engine;

	@BeforeClass
	public static void setup() {
		Settings settings = ImmutableSettings.builder().put("plugin.script.java.imports", "java.lang.*;").build();
		engine = new JavaEngineService(settings);
	}

	@AfterClass
	public static void close() {
		engine.close();
	}

	@Test
	public void testSimpleExpression() {
		Map<String, Object> vars = new HashMap<String, Object>();
		Object o = engine.execute(engine.compile("return 1 + 2;"), vars);
		assertThat(((Number) o).intValue(), equalTo(3));
	}

	@Test
	public void testSimpleVarAccessUsingStringName() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return vars(\"x\");"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

	@Test
	public void testSimpleVarAccessUsingCharName() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return vars('x');"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

	@Test
	public void testInvalidSimpleVarAccessUsingCharName() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return x;"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

}
