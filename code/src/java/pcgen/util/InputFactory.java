/*
 * InputFactory.java
 * Copyright 2004 (C) Chris Ward <frugal@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on July 16, 2004
 */
package pcgen.util;

/**
 * A Factory class that generates Input Interfaces
 */
public class InputFactory
{

	private static String interfaceClassname = null;

	/**
	 * Deliberately private so it can't be instantiated.
	 */
	private InputFactory()
	{
		// Empty Constructor
	}

	/**
	 * The default implementation returns a SwingChooser
	 * @return InputInterface
	 */
	public static InputInterface getInputInstance()
	{
		try
		{
			Class c = Class.forName(interfaceClassname);
			InputInterface ci = (InputInterface) c.newInstance();
			return ci;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param interfaceClassname The interfaceClassname to set.
	 */
	public static void setInterfaceClassname(String interfaceClassname)
	{
		InputFactory.interfaceClassname = interfaceClassname;
	}
}
