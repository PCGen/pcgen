/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package pcgen.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

/**
 * Tests the log line produced by {@link SourceLogFormatter}.
 */
class SourceLogFormatterTest
{
	private final SourceLogFormatter formatter = new SourceLogFormatter();

	/**
	 * The timestamp must come from the record, not from the moment of
	 * formatting, so that a delayed or repeated format still reports when the
	 * event happened.
	 */
	@Test
	void usesTheRecordsOwnInstant()
	{
		LogRecord record = new LogRecord(Level.INFO, "hello");
		Instant loggedAt = Instant.parse("2020-01-02T03:04:05.678Z");
		record.setInstant(loggedAt);

		String line = formatter.format(record);

		String expected = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
				.withZone(ZoneId.systemDefault())
				.format(loggedAt);
		assertTrue(line.startsWith(expected),
				"Expected the line to start with the record's own instant " + expected + " but was: " + line);
	}

	@Test
	void includesLevelAndMessage()
	{
		LogRecord record = new LogRecord(Level.WARNING, "something happened");

		String line = formatter.format(record);

		assertTrue(line.contains("WARNING"), line);
		assertTrue(line.contains("something happened"), line);
		assertTrue(line.endsWith("\n"), "Each entry must be newline terminated");
	}

	@Test
	void rendersTheThrownStackTrace()
	{
		LogRecord record = new LogRecord(Level.SEVERE, "boom");
		record.setThrown(new IllegalStateException("kaboom"));

		String line = formatter.format(record);

		assertTrue(line.contains("IllegalStateException"), line);
		assertTrue(line.contains("kaboom"), line);
	}

	/**
	 * The name is only meaningful while this is still the thread that logged,
	 * but the id always identifies it, so both are reported together.
	 */
	@Test
	void reportsThreadNameAndIdWhenFormattingOnTheLoggingThread()
	{
		LogRecord record = new LogRecord(Level.INFO, "hello");
		Thread current = Thread.currentThread();

		String line = formatter.format(record);

		assertTrue(line.contains(current.getName() + '#' + current.threadId()),
				"Expected name#id for the logging thread but was: " + line);
	}

	/**
	 * Regression guard: the formatter used to print
	 * {@code Thread.currentThread().getName()} unconditionally, which attributes
	 * the record to whichever thread happened to format it.
	 */
	@Test
	void reportsOnlyTheIdWhenFormattedOnAnotherThread() throws Exception
	{
		AtomicReference<LogRecord> logged = new AtomicReference<>();
		Thread producer = new Thread(() -> logged.set(new LogRecord(Level.INFO, "hello")), "producer-thread");
		producer.start();
		producer.join();

		LogRecord record = logged.get();
		// Formatted here, on a different thread from the one that created it.
		String line = formatter.format(record);

		assertTrue(line.contains("#" + record.getLongThreadID()),
				"The originating thread id must survive, but was: " + line);
		assertFalse(line.contains("producer-thread#"),
				"The dead thread's name is not available, so it must not be claimed: " + line);
		assertFalse(line.contains(Thread.currentThread().getName() + '#' + record.getLongThreadID()),
				"The formatting thread must not be reported as the origin: " + line);
	}
}
