/*
 * Copyright 2007 (C) James Dempsey
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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

/**
 * {@code SourceLogFormatter} is a log formatter for the Java
 * Logging API that ignores the call from the PCGen logging class.
 */
public final class SourceLogFormatter extends Formatter
{
	private static final char SEPERATOR = ' ';
	private static final Pattern JAVA_EXT_PATTERN = Pattern.compile("\\.java");

	private static final DateTimeFormatter TIMESTAMP =
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

	private static final StackWalker WALKER = StackWalker.getInstance();

	@Override
	public String format(LogRecord logRecord)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(TIMESTAMP.format(logRecord.getInstant())); // two handlers produce the same instant

		sb.append(SEPERATOR);
		sb.append(logRecord.getLevel());
		sb.append(SEPERATOR);
		appendThread(sb, logRecord);
		sb.append(SEPERATOR);

		appendCaller(sb);

		sb.append(SEPERATOR);

		sb.append(formatMessage(logRecord));

		if (logRecord.getThrown() != null)
		{
			sb.append('\n');
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			logRecord.getThrown().printStackTrace(pw);
			pw.flush();
			sb.append(sw);
		}

		sb.append('\n');

		return sb.toString();
	}

	/**
	 * Appends the originating thread as {@code name#id}.
	 * <p>
	 * The id comes from the record, so it always identifies the thread that
	 * logged, and it matches the ids shown by thread dumps and debuggers. The
	 * name is only available from the live thread, so it is used solely when
	 * this is still the thread that logged; otherwise the id is printed alone
	 * rather than attributing the record to the wrong thread. Virtual threads
	 * are unnamed, so they also print as {@code #id}.
	 *
	 * @param sb the buffer to append to
	 * @param record the record being formatted
	 */
	private static void appendThread(StringBuilder sb, LogRecord record)
	{
		long threadId = record.getLongThreadID();
		Thread current = Thread.currentThread();
		if ((current.threadId() == threadId) && !current.getName().isEmpty())
		{
			sb.append(current.getName());
		}
		sb.append('#');
		sb.append(threadId);
	}

	/**
	 * Appends the calling location, skipping the logging plumbing itself.
	 * <p>
	 * Uses {@link StackWalker}, which walks lazily and stops at the first
	 * interesting frame, rather than materialising an entire stack trace for
	 * every record.
	 *
	 * @param sb the buffer to append to
	 */
	private static void appendCaller(StringBuilder sb)
	{
		Optional<StackWalker.StackFrame> caller = WALKER.walk(frames -> frames
				.filter(frame -> !isLoggingPlumbing(frame.getClassName()))
				.findFirst());
		if (caller.isEmpty())
		{
			return;
		}

		StackWalker.StackFrame frame = caller.orElseThrow();
		String fileName = frame.getFileName();
		if ((frame.getLineNumber() >= 0) && (fileName != null))
		{
			sb.append(JAVA_EXT_PATTERN.matcher(fileName).replaceFirst(""));
			sb.append(':');
			sb.append(frame.getLineNumber());
		}
		else
		{
			sb.append(frame.getClassName());
			sb.append(' ');
			sb.append(frame.getMethodName());
		}
	}

	private static boolean isLoggingPlumbing(String className)
	{
		return className.startsWith("pcgen.util.Logging")
			|| className.startsWith("java.util.logging")
			|| className.startsWith("pcgen.system.LoggingRecorder")
			|| className.equals(SourceLogFormatter.class.getName());
	}
}
