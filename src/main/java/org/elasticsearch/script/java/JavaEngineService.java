package org.elasticsearch.script.java;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.math.UnboxedMathUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.AbstractJavaScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.ScriptException;
import org.elasticsearch.script.SearchScript;
import org.elasticsearch.search.lookup.SearchLookup;

public class JavaEngineService extends AbstractComponent implements ScriptEngineService {

	private static final AtomicInteger i = new AtomicInteger();

	private static final String GENERATED_PACKAGE_NAME = JavaEngineService.class.getPackage().getName() + ".generated";

	protected final ESLogger logger = Loggers.getLogger(getClass());

	@Inject
	public JavaEngineService(Settings settings) {
		super(settings);
	}

	@Override
	public String[] types() {
		return new String[] { "java" };
	}

	@Override
	public String[] extensions() {
		return new String[] { "java" };
	}

	/**
	 * See {@link org.elasticsearch.script.mvel.MvelScriptEngineService#MvelScriptEngineService(Settings)}
	 * 
	 * @return
	 */
	@Override
	public Object compile(String script) {
		String[] imports = settings.get("plugin.script.java.imports", "").split("[:;,]");
		String classImports = "";
		for (String classImport : imports) {
			if (!"".equals(classImport)) {
				classImports += "import " + classImport + ";\n";
			}
		}

		String classPackage = GENERATED_PACKAGE_NAME;
		String className = "GeneratedJavaScript" + i.incrementAndGet();
		String classSource = "" + //
				"// Generated on " + new Date() + "\n" + //
				"package " + classPackage + ";\n" + //

				"import " + AbstractJavaScript.class.getName() + ";\n" + //
				"import static " + UnboxedMathUtils.class.getName() + ".*;\n" + //
				"import java.util.*;\n" + //
				classImports + //

				"public class " + className + " extends AbstractJavaScript {\n" + //

				"   protected Object execute() {\n" + //
				"      " + script + "\n" + //
				"   }\n" + //

				"}\n";

		return compile(classPackage, className, classSource);
	}

	@Override
	public Object execute(Object compiledScript, Map<String, Object> vars) {
		return newInstance(compiledScript, vars, null).run();
	}

	@Override
	public ExecutableScript executable(Object compiledScript, Map<String, Object> vars) {
		return newInstance(compiledScript, vars, null);
	}

	@Override
	public SearchScript search(Object compiledScript, SearchLookup lookup, @Nullable Map<String, Object> vars) {
		return newInstance(compiledScript, vars, lookup);
	}

	@Override
	public Object unwrap(Object value) {
		return value;
	}

	public void close() {
		// Nothing to do here...
	}

	private Object compile(String classPackage, String className, String classSource) {
		String qualifiedClassName = classPackage + "." + className;
		List<JavaFileObject> javaFiles = Collections.<JavaFileObject> singletonList(new CharSequenceJavaFileObject(
				qualifiedClassName, classSource));

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StringWriter writer = new StringWriter();
		MemoryJavaFileManager fileManager = new MemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));

		logger.debug("Compiling java script class:\n{}", classSource);
		Boolean success = compiler.getTask(writer, fileManager, null, options(), null, javaFiles).call();
		if (!success) {
			throw new ScriptException("Could not compile java script class: " + classSource + " because: "
					+ writer.toString());
		}

		try {
			return fileManager.getClassLoader(null).loadClass(qualifiedClassName);
		} catch (Exception e) {
			throw new ScriptException("Exception loading script class: " + qualifiedClassName + " with source:\n"
					+ classSource, e);
		}
	}

	private static AbstractJavaScript newInstance(Object compiledScript, Map<String, Object> vars, SearchLookup lookup) {
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
			throw new ScriptException("Exception instantiating script object: " + compiledScript, e);
		}
	}

	private static List<String> options() {
		// Set compiler's classpath to be same as the runtime's
		String classpath = System.getProperty("java.class.path") + System.getProperty("path.separator") + getPluginPath();
		List<String> options = Arrays.asList("-classpath", classpath);

		return options;
	}

	private static String getPluginPath() {
		return JavaEngineService.class.getProtectionDomain().getCodeSource().getLocation().getFile();
	}

}
