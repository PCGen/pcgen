/*
 * SourceLogFormatter.java
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
 *
 *
 */
package pcgen.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
import java.util.regex.Pattern;

/**
 * {@code SourceLogFormatter} is a log formater for the Java
 * Loggings API that ignores the call from the PCGen logging class.
 *
 *
 */
public final class SourceLogFormatter extends Formatter
{
	private static final char SEPERATOR = ' ';
	private final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.S");
	private final Date date = new Date(); 
	private static final Pattern javaExtPattern = Pattern.compile("\\.java");
	
	/* (non-Javadoc)
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
    @Override
	public String format(LogRecord record)
	{
		StringBuilder sb = new StringBuilder();
		
		date.setTime(record.getMillis());
		sb.append(df.format(date));
		
		sb.append(SEPERATOR);
		sb.append(String.valueOf(record.getLevel()));
		sb.append(SEPERATOR);
		sb.append(Thread.currentThread().getName());
		sb.append(SEPERATOR);

		// Pick out the caller from the stack trace, ignoring the 
		// logging classes themselves 
		StackTraceElement[] stack = new Throwable().getStackTrace();
		StackTraceElement caller = null;		
		
		for (int i=1 ; i<stack.length ; i++) //1 to skip this method
		{
			if (!stack[i].getClassName().startsWith("pcgen.util.Logging") 
				&& !stack[i].getClassName().startsWith("java.util.logging")
				&& !stack[i].getClassName().startsWith("pcgen.system.LoggingRecorder"))
			{
				caller = stack[i];
				break;
			}
		}
		
		if (caller!=null) 
		{
			if (caller.getLineNumber()>=0)
			{
				sb.append(javaExtPattern.matcher(caller.getFileName()).replaceFirst(""));
				sb.append(':');
				sb.append(caller.getLineNumber());
			}
			else
			{
				sb.append(caller.getClassName());
				sb.append(' ');
				sb.append(caller.getMethodName());
			}
		}

		sb.append(SEPERATOR);
		
		sb.append(formatMessage(record));
		
		if (record.getThrown()!=null)
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
