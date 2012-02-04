/*
 * Main.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on January 28, 2002.
 *
 * $Id$
 */
package pcgen.core;

import pcgen.util.Logging;

import java.lang.reflect.InvocationTargetException;

/**
 * <code>Main</code> wraps the real entry point for PCGen.  It checks the
 * command line for an alternative main entry point, defaulting to
 * <code>pcGenGUI</code>.  This makes it easy to run command line test cases by
 * including a main entry point in a class, and then giving
 * the full name of the class on the command line like this:<pre>
 * $ ./pcgen.sh pcgen.util.DiceExpression '1+d4'
 * </pre>
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
public final class Main
{
	/**
	 * <code>main</code> is a wrapper entry point for the real main as determined
	 * by the command line argument.
	 *
	 * @param args String[] command line arguments
	 */
	public static void main(String[] args)
	{
		String mainName = pcgen.system.Main.class.getName();

		// If arg[0] doesn't start with "pcgen." it's unlikely to be a class we
		// can instantiate and run.  Ignore it in that case.
		final String pcgenDot = "pcgen.";

		if (args.length > 0
				&& args[0].length() > pcgenDot.length()
				&& args[0].startsWith(pcgenDot))
		{
			// Save the real entry point and shift all the arguments down one
			// position in the argument list.
			mainName = args[0];

			final String[] newArgs
					= new String[(args.length > 1) ? (args.length - 1) : 0];

			System.arraycopy(args, 1, newArgs, 0, args.length);

			args = newArgs;
		}

		try
		{
			Class.forName(mainName)
					.getDeclaredMethod("main", new Class[]{String[].class})
					.invoke(null, new Object[]{args});
		}
		catch (final ClassNotFoundException ex)
		{
			dieWithBadEntryPoint(mainName, ex);
		}
		catch (final NoSuchMethodException ex)
		{
			dieWithBadEntryPoint(mainName, ex);
		}
		catch (IllegalAccessException ex)
		{
			dieWithBadEntryPoint(mainName, ex);
		}
		catch (InvocationTargetException ex)
		{
			dieWithBadEntryPoint(mainName, ex);
		}
	}

	private static void dieWithBadEntryPoint(final String mainName,
			final Exception e)
	{
		Logging.errorPrint("Bad main entry point in " + mainName, e);

		System.exit(1);
	}
}
