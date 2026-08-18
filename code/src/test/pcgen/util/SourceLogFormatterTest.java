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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of {@link SourceLogFormatter}. These assert what the
 * formatter must guarantee, not how the line is laid out: the exact timestamp
 * pattern and thread rendering can change freely.
 */
class SourceLogFormatterTest
{
	private final SourceLogFormatter formatter = new SourceLogFormatter();

	/** The timestamp field, i.e. everything up to the first space. */
	private String timestampOf(Instant instant)
	{
		LogRecord record = new LogRecord(Level.INFO, "hello");
		record.setInstant(instant);
		return formatter.format(record).split(" ", 2)[0];
	}

	/**
	 * The reported time must derive from the record's own instant, not the wall
	 * clock at formatting time: the same instant always yields the same stamp,
	 * and different instants yield different stamps.
	 */
	@Test
	void usesTheRecordsOwnInstant()
	{
		String first = timestampOf(Instant.parse("2020-01-02T03:04:05.678Z"));
		String second = timestampOf(Instant.parse("2020-01-02T03:04:05.678Z"));
		assertEquals(first, second, "The same instant must always format to the same timestamp");

		String later = timestampOf(Instant.parse("2021-06-07T08:09:10.111Z"));
		assertNotEquals(first, later, "Different instants must format to different timestamps");
	}

	@Test
	void includesLevelAndMessage()
	{
		LogRecord record = new LogRecord(Level.WARNING, "something happened");

		String line = formatter.format(record);

		assertTrue(line.contains("WARNING"), line);
		assertTrue(line.contains("something happened"), line);
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

	/** The record is attributed to the thread that logged it, by name and id. */
	@Test
	void attributesTheLoggingThreadByNameAndId()
	{
		LogRecord record = new LogRecord(Level.INFO, "hello");
		Thread current = Thread.currentThread();

		String line = formatter.format(record);

		assertTrue(line.contains(current.getName()), "The logging thread's name must appear: " + line);
		assertTrue(line.contains(Long.toString(current.threadId())), "The logging thread's id must appear: " + line);
	}

	/**
	 * Regression guard: the formatter used to report
	 * {@code Thread.currentThread().getName()} unconditionally, attributing the
	 * record to whichever thread happened to format it. When the originating
	 * thread is gone its name is unavailable, so only the id must survive and the
	 * formatting thread must not be claimed as the origin.
	 */
	@Test
	void doesNotAttributeToTheFormattingThread() throws Exception
	{
		AtomicReference<LogRecord> logged = new AtomicReference<>();
		Thread producer = new Thread(() -> logged.set(new LogRecord(Level.INFO, "hello")), "producer-thread");
		producer.start();
		producer.join();

		LogRecord record = logged.get();
		// Formatted here, on a different thread from the one that created it.
		String line = formatter.format(record);

		assertTrue(line.contains(Long.toString(record.getLongThreadID())),
				"The originating thread id must survive: " + line);
		assertFalse(line.contains("producer-thread"),
				"The dead thread's name is not available, so it must not be claimed: " + line);
		assertFalse(line.contains(Thread.currentThread().getName()),
				"The formatting thread must not be reported as the origin: " + line);
	}
}
