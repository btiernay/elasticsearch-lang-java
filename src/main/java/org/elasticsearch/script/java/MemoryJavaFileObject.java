package org.elasticsearch.script.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class MemoryJavaFileObject extends SimpleJavaFileObject {

	protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

	public MemoryJavaFileObject(String name, Kind kind) {
		super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
	}

	public byte[] getBytes() {
		return bos.toByteArray();
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return bos;
	}

}