package org.elasticsearch.script.java;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

class MemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private MemoryJavaFileObject javaFileObject;

	public MemoryJavaFileManager(StandardJavaFileManager standardManager) {
		super(standardManager);
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		return new ClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				byte[] b = javaFileObject.getBytes();
				if (b != null && b.length > 0) {
					return super.defineClass(name, javaFileObject.getBytes(), 0, b.length);
				} else {
					return super.findClass(name);
				}
			}
		};
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
			throws IOException {
		javaFileObject = new MemoryJavaFileObject(className, kind);
		return javaFileObject;
	}

}