package org.elasticsearch.script.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.AbstractJavaScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.ScriptException;
import org.elasticsearch.script.SearchScript;
import org.elasticsearch.search.lookup.SearchLookup;

/**
 * See http://www.javablogging.com/dynamic-in-memory-compilation/
 */
public class JavaEngineService extends AbstractComponent implements ScriptEngineService {

	private static final AtomicInteger i = new AtomicInteger();
	
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private final JavaFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));

	@Inject
	public JavaEngineService(Settings settings) {
		super(settings);
	}

	@Override
	public void close() {
		// nothing to do here...
	}

	@Override
	public String[] types() {
		return new String[] { "java" };
	}

	@Override
	public String[] extensions() {
		return new String[] { "java" };
	}

	@Override
	public Object compile(String script) {
		String classPackage = getClass().getPackage().getName();
		String className = "Java" + i.incrementAndGet();
		String classSource = //
		"package " + classPackage + ";\n" + //
				"import " + AbstractJavaScript.class.getName() + ";\n" + //
				"import java.util.*;\n" + //
				"import static java.lang.System.currentTimeMillis;\n" + //
				"public class " + className + " extends AbstractJavaScript {\n" + //
				"   protected Object execute() {\n" + //
				"   " + script + "\n" + //
				"   }\n" + //
				"}\n";

		String qualifiedClassName = classPackage + "." + className;
		List<JavaFileObject> javaFiles = new ArrayList<JavaFileObject>();
		javaFiles.add(new CharSequenceJavaFileObject(qualifiedClassName, classSource));
		compiler.getTask(null, fileManager, null, null, null, javaFiles).call();

		try {
			return fileManager.getClassLoader(null).loadClass(qualifiedClassName).newInstance();
		} catch (Exception e) {
			throw new ScriptException("Exception creating script class: " + qualifiedClassName + " with source: " + classSource, e);
		}
	}

	@Override
	public Object execute(Object compiledScript, Map<String, Object> vars) {
		AbstractJavaScript script = (AbstractJavaScript) compiledScript;
		setVars(vars, script);
		return script.run();
	}

	@Override
	public ExecutableScript executable(Object compiledScript, Map<String, Object> vars) {
		AbstractJavaScript script = (AbstractJavaScript) compiledScript;
		setVars(vars, script);
		return script;
	}

	@Override
	public SearchScript search(Object compiledScript, SearchLookup lookup, @Nullable Map<String, Object> vars) {
		AbstractJavaScript script = (AbstractJavaScript) compiledScript;
		setVars(vars, script);
		script.setLookup(lookup);
		return script;
	}

	@Override
	public Object unwrap(Object value) {
		return value;
	}

	private void setVars(Map<String, Object> vars, AbstractJavaScript script) {
		for (Entry<String, Object> entry : vars.entrySet()) {
			script.setNextVar(entry.getKey(), entry.getValue());
		}
	}

}
