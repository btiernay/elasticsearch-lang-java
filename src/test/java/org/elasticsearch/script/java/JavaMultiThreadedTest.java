package org.elasticsearch.script.java;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.util.concurrent.jsr166y.ThreadLocalRandom;
import org.elasticsearch.script.ExecutableScript;
import org.junit.Test;

public class JavaMultiThreadedTest {

	private static final String script = "return ((Long)var(\"x\")) + ((Long)var(\"y\"));";

	protected final ESLogger logger = Loggers.getLogger(getClass());

	@Test
	public void testExecutableNoRuntimeParams() throws Exception {
		final JavaEngineService se = new JavaEngineService(ImmutableSettings.Builder.EMPTY_SETTINGS);
		final Object compiled = se.compile(script);
		final AtomicBoolean failed = new AtomicBoolean();

		Thread[] threads = new Thread[50];
		final CountDownLatch latch = new CountDownLatch(threads.length);
		final CyclicBarrier barrier = new CyclicBarrier(threads.length + 1);
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						long x = ThreadLocalRandom.current().nextInt();
						long y = ThreadLocalRandom.current().nextInt();
						long addition = x + y;
						Map<String, Object> vars = new HashMap<String, Object>();
						vars.put("x", x);
						vars.put("y", y);
						ExecutableScript script = se.executable(compiled, vars);
						for (int i = 0; i < 100000; i++) {
							long result = ((Number) script.run()).longValue();
							assertThat(result, equalTo(addition));
						}
					} catch (Throwable t) {
						failed.set(true);
						logger.error("failed", t);
					} finally {
						latch.countDown();
					}
				}
			});
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		barrier.await();
		latch.await();
		assertThat(failed.get(), equalTo(false));
	}

	@Test
	public void testExecutableWithRuntimeParams() throws Exception {
		final JavaEngineService se = new JavaEngineService(ImmutableSettings.Builder.EMPTY_SETTINGS);
		final Object compiled = se.compile(script);
		final AtomicBoolean failed = new AtomicBoolean();

		Thread[] threads = new Thread[50];
		final CountDownLatch latch = new CountDownLatch(threads.length);
		final CyclicBarrier barrier = new CyclicBarrier(threads.length + 1);
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						long x = ThreadLocalRandom.current().nextInt();
						Map<String, Object> vars = new HashMap<String, Object>();
						vars.put("x", x);
						ExecutableScript script = se.executable(compiled, vars);
						for (int i = 0; i < 100000; i++) {
							long y = ThreadLocalRandom.current().nextInt();
							long addition = x + y;
							script.setNextVar("y", y);
							long result = ((Number) script.run()).longValue();
							assertThat(result, equalTo(addition));
						}
					} catch (Throwable t) {
						failed.set(true);
						logger.error("failed", t);
					} finally {
						latch.countDown();
					}
				}
			});
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		barrier.await();
		latch.await();
		assertThat(failed.get(), equalTo(false));
	}

	@Test
	public void testExecute() throws Exception {
		final JavaEngineService se = new JavaEngineService(ImmutableSettings.Builder.EMPTY_SETTINGS);
		final Object compiled = se.compile(script);
		final AtomicBoolean failed = new AtomicBoolean();

		Thread[] threads = new Thread[50];
		final CountDownLatch latch = new CountDownLatch(threads.length);
		final CyclicBarrier barrier = new CyclicBarrier(threads.length + 1);
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						Map<String, Object> runtimeVars = new HashMap<String, Object>();
						for (int i = 0; i < 100000; i++) {
							long x = ThreadLocalRandom.current().nextInt();
							long y = ThreadLocalRandom.current().nextInt();
							long addition = x + y;
							runtimeVars.put("x", x);
							runtimeVars.put("y", y);
							long result = ((Number) se.execute(compiled, runtimeVars)).longValue();
							assertThat(result, equalTo(addition));
						}
					} catch (Throwable t) {
						failed.set(true);
						logger.error("failed", t);
					} finally {
						latch.countDown();
					}
				}
			});
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		barrier.await();
		latch.await();
		assertThat(failed.get(), equalTo(false));
	}
}
