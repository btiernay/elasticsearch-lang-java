package org.elasticsearch.plugin.java;

import org.elasticsearch.cluster.settings.ClusterDynamicSettingsModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.script.ScriptModule;
import org.elasticsearch.script.java.JavaEngineService;

public class JavaPlugin extends AbstractPlugin {

	@Override
	public String name() {
		return "lang-java";
	}

	@Override
	public String description() {
		return "Java plugin allowing to add java \"script\" support";
	}

	public void onModule(ScriptModule module) {
		module.addScriptEngine(JavaEngineService.class);
	}

  public void onModule(ClusterDynamicSettingsModule module) {
    module.addDynamicSettings("plugin.script.java.imports");
	}
  
}
