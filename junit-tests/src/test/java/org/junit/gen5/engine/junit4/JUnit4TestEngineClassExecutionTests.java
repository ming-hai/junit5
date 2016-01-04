/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine.junit4;

import static org.junit.gen5.engine.ExecutionEventConditions.*;
import static org.junit.gen5.engine.TestExecutionResultConditions.causeMessage;
import static org.junit.gen5.engine.TestPlanSpecification.*;

import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineAwareTestDescriptor;
import org.junit.gen5.engine.ExecutionEventRecordingEngineExecutionListener;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.junit4.samples.EnclosedJUnit4TestCase;
import org.junit.gen5.engine.junit4.samples.JUnit4SuiteWithJUnit3SuiteWithSingleTestCase;
import org.junit.gen5.engine.junit4.samples.PlainJUnit3TestCaseWithSingleTestWhichFails;
import org.junit.gen5.engine.junit4.samples.PlainJUnit4TestCaseWithFourTests;
import org.junit.gen5.engine.junit4.samples.PlainJUnit4TestCaseWithSingleTestWhichFails;
import org.junit.gen5.engine.junit4.samples.PlainJUnit4TestCaseWithTwoTests;

class JUnit4TestEngineClassExecutionTests {

	ExecutionEventRecordingEngineExecutionListener listener = new ExecutionEventRecordingEngineExecutionListener();

	@Test
	void executesPlainJUnit4TestCaseWithSingleTestWhichFails() {
		Class<?> testClass = PlainJUnit4TestCaseWithSingleTestWhichFails.class;

		execute(testClass);

		assertRecordedExecutionEventsContainsExactly(listener, //
			event(engine(), started()), //
			event(container(testClass.getName()), started()), //
			event(test("failingTest"), started()), //
			event(test("failingTest"), finishedWithFailure(causeMessage("this test should fail"))), //
			event(container(testClass.getName()), finishedSuccessfully()), //
			event(engine(), finishedSuccessfully()));
	}

	@Test
	void executesPlainJUnit4TestCaseWithTwoTests() {
		Class<?> testClass = PlainJUnit4TestCaseWithTwoTests.class;

		execute(testClass);

		assertRecordedExecutionEventsContainsExactly(listener, //
			event(engine(), started()), //
			event(container(testClass.getName()), started()), //
			event(test("failingTest"), started()), //
			event(test("failingTest"), finishedWithFailure(causeMessage("this test should fail"))), //
			event(test("successfulTest"), started()), //
			event(test("successfulTest"), finishedSuccessfully()), //
			event(container(testClass.getName()), finishedSuccessfully()), //
			event(engine(), finishedSuccessfully()));
	}

	@Test
	void executesPlainJUnit4TestCaseWithFourTests() {
		Class<?> testClass = PlainJUnit4TestCaseWithFourTests.class;

		execute(testClass);

		assertRecordedExecutionEventsContainsExactly(listener, //
			event(engine(), started()), //
			event(container(testClass.getName()), started()), //
			event(test("abortedTest"), started()), //
			event(test("abortedTest"), abortedWithReason(causeMessage("this test should be aborted"))), //
			event(test("failingTest"), started()), //
			event(test("failingTest"), finishedWithFailure(causeMessage("this test should fail"))), //
			event(test("ignoredTest"), skippedWithReason("<unknown>")), //
			event(test("successfulTest"), started()), //
			event(test("successfulTest"), finishedSuccessfully()), //
			event(container(testClass.getName()), finishedSuccessfully()), //
			event(engine(), finishedSuccessfully()));
	}

	@Test
	void executesEnclosedJUnit4TestCase() {
		Class<?> testClass = EnclosedJUnit4TestCase.class;
		Class<?> nestedClass = EnclosedJUnit4TestCase.NestedClass.class;

		execute(testClass);

		assertRecordedExecutionEventsContainsExactly(listener, //
			event(engine(), started()), //
			event(container(testClass.getName()), started()), //
			event(container(nestedClass.getName()), started()), //
			event(test("failingTest"), started()), //
			event(test("failingTest"), finishedWithFailure(causeMessage("this test should fail"))), //
			event(container(nestedClass.getName()), finishedSuccessfully()), //
			event(container(testClass.getName()), finishedSuccessfully()), //
			event(engine(), finishedSuccessfully()));
	}

	@Test
	void executesSuite() {
		Class<?> junit4SuiteClass = JUnit4SuiteWithJUnit3SuiteWithSingleTestCase.class;
		Class<?> testClass = PlainJUnit3TestCaseWithSingleTestWhichFails.class;

		execute(junit4SuiteClass);

		assertRecordedExecutionEventsContainsExactly(listener, //
			event(engine(), started()), //
			event(container(junit4SuiteClass.getName()), started()), //
			event(container("TestSuite with 1 tests"), started()), //
			event(container(testClass.getName()), started()), //
			event(test("test"), started()), //
			event(test("test"), finishedWithFailure(causeMessage("this test should fail"))), //
			event(container(testClass.getName()), finishedSuccessfully()), //
			event(container("TestSuite with 1 tests"), finishedSuccessfully()), //
			event(container(junit4SuiteClass.getName()), finishedSuccessfully()), //
			event(engine(), finishedSuccessfully()));
	}

	private void execute(Class<?> testClass) {
		JUnit4TestEngine engine = new JUnit4TestEngine();
		EngineAwareTestDescriptor engineTestDescriptor = engine.discoverTests(build(forClass(testClass)));
		engine.execute(new ExecutionRequest(engineTestDescriptor, listener));
	}
}