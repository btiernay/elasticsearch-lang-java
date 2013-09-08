package org.elasticsearch.script.java;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractJavaEngineServiceTest {

	protected static JavaEngineService engine;

	@BeforeClass
	public static void setup() {
		Settings settings = settings("java.lang.*;");
		ClusterService clusterService = mockClusterService("org.elasticsearch.*;");

		engine = new JavaEngineService(settings, clusterService);
	}

	@AfterClass
	public static void close() {
		engine.close();
	}

	private static ClusterService mockClusterService(String imports) {
		MetaData metaData = MetaData.builder().persistentSettings(settings(imports)).build();
		ClusterState state = ClusterState.builder().metaData(metaData).build();
		ClusterService clusterService = mock(ClusterService.class);
		when(clusterService.state()).thenReturn(state);
		return clusterService;
	}

	private static Settings settings(String imports) {
		return ImmutableSettings.builder().put("script.java.imports", imports).build();
	}

}
