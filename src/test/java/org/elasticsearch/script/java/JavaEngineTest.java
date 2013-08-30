/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.script.java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavaEngineTest {

	private static JavaEngineService engine;

	@BeforeClass
	public static void setup() {
		engine = new JavaEngineService(ImmutableSettings.Builder.EMPTY_SETTINGS);
	}

	@AfterClass
	public static void close() {
		engine.close();
	}

	@Test
	public void testSimpleEquation() {
		Map<String, Object> vars = new HashMap<String, Object>();
		Object o = engine.execute(engine.compile("return 1 + 2;"), vars);
		assertThat(((Number) o).intValue(), equalTo(3));
	}

	@Test
	public void testSimpleVarAccess() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("x", 1);

		Object o = engine.execute(engine.compile("return vars(\"x\");"), vars);
		assertThat(((Number) o).intValue(), equalTo(1));
	}

}
