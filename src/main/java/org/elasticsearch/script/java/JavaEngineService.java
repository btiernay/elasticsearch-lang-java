package org.elasticsearch.script.java;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.AbstractJavaScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.ScriptException;
import org.elasticsearch.script.SearchScript;
import org.elasticsearch.search.lookup.SearchLookup;

public class JavaEngineService extends AbstractComponent implements ScriptEngineService {

	private static final AtomicInteger i = new AtomicInteger();

	protected final ESLogger logger = Loggers.getLogger(getClass());

	@Inject
	public JavaEngineService(Settings settings) {
		super(settings);
	}

	public void close() {
		// Nothing to do here...
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
		String classPackage = getClass().getPackage().getName() + ".generated";
		String className = "GeneratedJavaScript" + i.incrementAndGet();
		String classSource = "" + //
				"// Generated on " + new Date() + "\n" + //
				"package " + classPackage + ";\n" + //
				"\n" + //
				"import " + AbstractJavaScript.class.getName() + ";\n" + //
				"import java.util.*;\n" + //
				"import static java.lang.System.currentTimeMillis;\n" + //
				"\n" + //
				"public class " + className + " extends AbstractJavaScript {\n" + //
				"   protected Object execute() {\n" + //
				"      " + script + "\n" + //
				"   }\n" + //
				"}\n";

		String qualifiedClassName = classPackage + "." + className;
		List<JavaFileObject> javaFiles = new ArrayList<JavaFileObject>();
		javaFiles.add(new CharSequenceJavaFileObject(qualifiedClassName, classSource));

		logger.debug("Creating java script class:\n{}", classSource);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		// Set compiler's classpath to be same as the runtime's
		List<String> options = new ArrayList<String>();
		String classpath = System.getProperty("java.class.path") + System.getProperty("path.separator")
				+ getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		options.addAll(Arrays.asList("-classpath", classpath));

		StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
		MemoryJavaFileManager fileManager = new MemoryJavaFileManager(standardFileManager);
		StringWriter writer = new StringWriter();

		Boolean success = compiler.getTask(writer, fileManager, null, options, null, javaFiles).call();
		if (!success) {
			throw new ScriptException("Could not compile script: " + writer.toString() + " using classpath: " + classpath);
		}

		try {
			ClassLoader classLoader = fileManager.getClassLoader(null);
			return classLoader.loadClass(qualifiedClassName);
		} catch (Exception e) {
			throw new ScriptException(e.getClass().getSimpleName() + " creating script class: " + qualifiedClassName
					+ " with source:\n" + classSource, e);
		}
	}

	@Override
	public Object execute(Object compiledScript, Map<String, Object> vars) {
		return instance(compiledScript, vars, null).run();
	}

	@Override
	public ExecutableScript executable(Object compiledScript, Map<String, Object> vars) {
		return instance(compiledScript, vars, null);
	}

	@Override
	public SearchScript search(Object compiledScript, SearchLookup lookup, @Nullable Map<String, Object> vars) {
		return instance(compiledScript, vars, lookup);
	}

	@Override
	public Object unwrap(Object value) {
		return value;
	}

	private static AbstractJavaScript instance(Object compiledScript, Map<String, Object> vars, SearchLookup lookup) {
		try {
			Class<?> scriptClass = (Class<?>) compiledScript;
			AbstractJavaScript script = (AbstractJavaScript) scriptClass.newInstance();

			if (vars != null) {
				for (Entry<String, Object> entry : vars.entrySet()) {
					script.setNextVar(entry.getKey(), entry.getValue());
				}
			}

			if (lookup != null) {
				script.setLookup(lookup);
			}

			return script;
		} catch (Exception e) {
			throw new ScriptException(e.getClass().getSimpleName() + " instantiating script object: " + compiledScript, e);
		}
	}

}
