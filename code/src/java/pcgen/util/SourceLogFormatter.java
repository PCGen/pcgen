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
import java.time.Clock;
import java.time.LocalDateTime;
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

    @Override
    public String format(LogRecord record)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(LocalDateTime.now(Clock.systemUTC()));

        sb.append(SEPERATOR);
        sb.append(record.getLevel());
        sb.append(SEPERATOR);
        sb.append(Thread.currentThread().getName());
        sb.append(SEPERATOR);

        // Pick out the caller from the stack trace, ignoring the
        // logging classes themselves
        StackTraceElement[] stack = new Throwable().getStackTrace();
        StackTraceElement caller = null;

        for (int i = 1;i < stack.length;i++) //1 to skip this method
        {
            if (!stack[i].getClassName().startsWith("pcgen.util.Logging")
                    && !stack[i].getClassName().startsWith("java.util.logging")
                    && !stack[i].getClassName().startsWith("pcgen.system.LoggingRecorder"))
            {
                caller = stack[i];
                break;
            }
        }

        if (caller != null)
        {
            if (caller.getLineNumber() >= 0)
            {
                sb.append(JAVA_EXT_PATTERN.matcher(caller.getFileName()).replaceFirst(""));
                sb.append(':');
                sb.append(caller.getLineNumber());
            } else
            {
                sb.append(caller.getClassName());
                sb.append(' ');
                sb.append(caller.getMethodName());
            }
        }

        sb.append(SEPERATOR);

        sb.append(formatMessage(record));

        if (record.getThrown() != null)
        {
            sb.append('\n');
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.flush();
            sb.append(sw);
        }

        sb.append('\n');

        return sb.toString();
    }
}
