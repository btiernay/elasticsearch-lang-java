package org.elasticsearch.plugin.java;

import static com.github.tlrx.elasticsearch.test.EsSetup.createIndex;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tlrx.elasticsearch.test.EsSetup;

public class JavaPluginTest {

	/**
	 * ES facade.
	 */
	protected EsSetup es;

	@Before
	public void before() {
		es = new EsSetup();
		es.execute(createIndex("index1"));
	}

	@After
	public void after() {
		es.terminate();
	}

	@Test
	public void testScriptField() throws InterruptedException, ExecutionException {
		Client client = es.client();

		String id = "id1";
		Map<String, Object> document = ImmutableMap.<String, Object> builder().put("field1", "value1")
				.put("field2", "value2").build();
		IndexResponse indexResponse = client.prepareIndex("index1", "type1").setId(id).setSource(document).setRefresh(true)
				.execute().get();
		assertThat(indexResponse.getId()).isEqualTo(id);

		int x = 1;
		int y = 1;
		int sum = x + y;
		Map<String, Object> params = ImmutableMap.<String, Object> builder().put("x", x).put("y", y).build();
		String script = "return (Integer)var('x') + (Integer)var('y');";
		
		SearchResponse searchResponse = client.prepareSearch("index1").addScriptField("result", "java", script, params)
				.execute().get();
		Integer result = searchResponse.getHits().getHits()[0].getFields().get("result").getValue();
		assertThat(result).isEqualTo(sum);
	}

}
