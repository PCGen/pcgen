/*
 * Logging.java
 * Copyright 2003 (C) Jonas Karlsson <jujutsunerd@sf.net>
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
 * Created on April 12, 2003, 3:20 AM
 */
package pcgen.util;

import java.awt.Toolkit;
import java.text.NumberFormat;

/**
 * This contains logging functions. Should probably be handled via Log4J.
 * @author     Jonas Karlsson <jujutsunerd@sf.net>
 * @version    $Revision$
 */
public class Logging
{
	private static boolean debugMode = false;
	private static final Toolkit s_TOOLKIT = Toolkit.getDefaultToolkit();

	/**
	 * Set debugging state: <code>true</code> is on.
	 *
	 * @param argDebugMode boolean debugging state
	 */
	public static void setDebugMode(final boolean argDebugMode)
	{
		debugMode = argDebugMode;
	}

	/**
	 * Is someone debugging PCGen?
	 *
	 * @return boolean debugging state
	 */
	public static boolean isDebugMode()
	{
		return debugMode;
	}

	/**
	 * Print information message if PCGen is debugging.
	 *
	 * @param s String information message
	 */
	public static void debugPrint(final String s)
	{
		if (isDebugMode())
		{
			System.out.println(s);
		}
	}

	/**
	 * Print information message if PCGen is debugging.
	 *
	 * @param param1 String information message (usually variable)
	 * @param param2 Object information message (usually value)
	 */
	public static void debugPrint(final String param1, Object param2)
	{
		if (isDebugMode())
		{
			System.out.println(param1 + param2);
		}
	}

	/**
	 * Print localised information message if PCGen is debugging.
	 *
	 * @param param1 String information message (usually variable)
	 * @param param2 Object information message (usually value)
	 */
	public static void debugPrintLocalised(final String param1, Object param2)
	{
		if (isDebugMode())
		{
			String msg = PropertyFactory.getFormattedString(param1, param2);
			System.out.println(msg);
		}
	}

	/**
	 * Print localised information message if PCGen is debugging.
	 *
	 * @param message String information message (usually variable)
	 * @param param1 Object information message (usually value)
	 * @param param2 Object information message (usually value)
	 */
	public static void debugPrintLocalised(final String message, Object param1, Object param2)
	{
		if (isDebugMode())
		{
			String msg = PropertyFactory.getFormattedString(message, param1, param2);
			System.out.println(msg);
		}
	}

	/**
	 * Print the message. Currently quietly discards the Throwable.
	 *
	 * @param s String error message
	 * @param thr Throwable stack frame
	 */
	public static void debugPrint(final String s, final Throwable thr)
	{
		debugPrint(s);

		//thr.printStackTrace(System.err);
	}

	/**
	 * Print a localized error message from the passed in key.  If the
	 * application is in Debug mode will also issue a beep.
	 *
	 * @param aKey A key for the localized string in the language bundle
	 */
	public static void errorPrintLocalised(final String aKey)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		final String msg = PropertyFactory.getString(aKey);
		System.err.println(msg);
	}

	/**
	 * Print a localized error message including parameter substitution.  The
	 * method will issue a beep if the application is running in Debug mode.
	 * <p>This method accepts a variable number of parameters and will replace
	 * <code>{argno}</code> in the string with each passed paracter in turn.
	 * @param aKey A key for the localized string in the language bundle
	 * @param varargs Variable number of parameters to substitute into the 
	 * string
	 */
	public static void errorPrintLocalised(final String aKey, Object... varargs)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		final String msg = PropertyFactory.getFormattedString( aKey, varargs );
		System.err.println(msg);
	}

	/**
	 * Beep and print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 */
	public static void errorPrint(final String s)
	{
		if (isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		System.err.println(s);
	}

	/**
	 * Print error message with a stack trace if PCGen is
	 * debugging.
	 *
	 * @param s String error message
	 * @param thr Throwable stack frame
	 */
	public static void errorPrint(final String s, final Throwable thr)
	{
		errorPrint(s);
		thr.printStackTrace(System.err);
	}

	/**
	 * Print error message with a stack trace if PCGen is
	 * debugging.
	 *
	 * @param s String error message
	 * @param thr Throwable stack frame
	 */
	public static void errorPrintLocalised(final String s, final Throwable thr)
	{
		errorPrint(PropertyFactory.getString(s));
		thr.printStackTrace(System.err);
	}

	/**
	 * Report to the console on the current memory sitution.
	 */
	public static void memoryReport()
	{
		System.out.println(memoryReportStr());
	}

	/**
	 * Generate the memory report string
	 * @return the memory report string
	 */
	public static String memoryReportStr()
	{
		Runtime rt = Runtime.getRuntime();
		NumberFormat numFmt = NumberFormat.getNumberInstance();
		StringBuffer sb = new StringBuffer("Memory: ");
		sb.append(numFmt.format(rt.totalMemory()/1024.0));
		sb.append("Kb total, ");
		sb.append(numFmt.format(rt.freeMemory()/1024.0));
		sb.append("Kb free, ");
		sb.append(numFmt.format(rt.maxMemory()/1024.0));
		sb.append("Kb max.");
		return sb.toString();
	}

	/**
	 * Intentionally cause a NullPointerException and then print the stack trace.
	 * Occasionally useful for debugging
	 */	
	public static void PrintStackTrace()
	{
		String dummy = null;
		try
		{
			dummy.length();
		}
		catch (NullPointerException npe)
		{
			npe.printStackTrace();
		}
	}
}
