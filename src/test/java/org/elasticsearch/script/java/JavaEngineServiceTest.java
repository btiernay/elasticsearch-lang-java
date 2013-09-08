package org.elasticsearch.script.java;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.ScriptException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavaEngineServiceTest extends AbstractJavaEngineServiceTest {

	private static JavaEngineService engine;

	@BeforeClass
	public static void setup() {
		Settings settings = settings("java.lang.*;");
		MetaData metaData = MetaData.builder().persistentSettings(settings("java.lang.*;")).build();
		ClusterState state = ClusterState.builder().metaData(metaData).build();
		ClusterService clusterService = mock(ClusterService.class);
		when(clusterService.state()).thenReturn(state);
		
		engine = new JavaEngineService(settings, clusterService);
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

		Object o = engine.execute(engine.compile("return var(\"x\");"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

	@Test
	public void testSimpleVarAccessUsingCharName() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return var('x');"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

	@Test(expected = ScriptException.class)
	public void testInvalidSimpleVarAccessUsingCharName() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return x;"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

	private static Settings settings(String imports) {
		return ImmutableSettings.builder().put("script.java.imports", imports).build();
	}

}
