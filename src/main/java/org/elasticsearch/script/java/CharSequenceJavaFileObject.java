package org.elasticsearch.script.java;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class CharSequenceJavaFileObject extends SimpleJavaFileObject {

	private final CharSequence content;

	public CharSequenceJavaFileObject(String className, CharSequence content) {
		super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.content = content;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return content;
	}

}