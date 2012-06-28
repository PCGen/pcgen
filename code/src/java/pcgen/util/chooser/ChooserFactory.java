/*
 * Chooser.java
 * Copyright 2002 (C) Jonas Karlsson
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
 */
package pcgen.util.chooser;

import pcgen.core.facade.UIDelegate;

/**
 * This factory class returns a Chooser of the appropriate type. This is intended
 * to reduce the core/gui interdependence. Much more work is needed on this...
 * Currently only a SwingChooser has been implemented.
 *
 * @author    Jonas Karlsson
 * @version $Revision$
 */
public final class ChooserFactory
{
	private static String interfaceClassname = null;
	private static String radioInterfaceClassname = null;
	private static String userInputInterfaceClassname = null;
	private static UIDelegate delegate;
	/**
	 * Deliberately private so it can't be instantiated.
	 */
	private ChooserFactory()
	{
		// Empty Constructor
	}

	/**
	 * The default implementation returns a SwingChooser
	 * @return ChooserInterface
	 */
	public static ChooserInterface getChooserInstance()
	{
		if (interfaceClassname == null)
		{
			return null;
		}
		try
		{
			Class<?> c = Class.forName(interfaceClassname);
			ChooserInterface ci = (ChooserInterface) c.newInstance();
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
	 * Get the radio instance
	 * @return ChooserRadio
	 */
	public static ChooserRadio getRadioInstance()
	{
		try
		{
			Class<?> c = Class.forName(radioInterfaceClassname);
			ChooserRadio ci = (ChooserRadio) c.newInstance();
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
	 * Get the userInput instance
	 * @return ChooserInterface
	 */
	public static ChooserInterface getUserInputInstance()
	{
		try
		{
			Class<?> c = Class.forName(userInputInterfaceClassname);
			ChooserInterface ci = (ChooserInterface) c.newInstance();
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
		ChooserFactory.interfaceClassname = interfaceClassname;
	}

	/**
	 * @param radioInterfaceClassname The radioInterfaceClassname to set.
	 */
	public static void setRadioInterfaceClassname(String radioInterfaceClassname)
	{
		ChooserFactory.radioInterfaceClassname = radioInterfaceClassname;
	}

	/**
	 * @param uiInterfaceClassname The uiInterfaceClassname to set.
	 */
	public static void setUserInputInterfaceClassname(String uiInterfaceClassname)
	{
		ChooserFactory.userInputInterfaceClassname = uiInterfaceClassname;
	}

	/**
	 * Get the class name of the interface
	 * @return the class name of the interface
	 */
	public static String getInterfaceClassname()
	{
		return ChooserFactory.interfaceClassname;
	}

	/**
	 * Get the class name of the radio interface
	 * @return the class name of hte radio interface
	 */
	public static String getRadioInterfaceClassname()
	{
		return ChooserFactory.radioInterfaceClassname;
	}

	/**
	 * Get the class name of the user input interface
	 * @return the class name of the user input interface
	 */
	public static String getUserInputInterfaceClassname()
	{
		return ChooserFactory.userInputInterfaceClassname;
	}

	/**
	 * @return the delegate
	 */
	public static UIDelegate getDelegate()
	{
		return delegate;
	}

	/**
	 * @param delegate the delgate to set
	 */
	public static void setDelegate(UIDelegate delegate)
	{
		ChooserFactory.delegate = delegate;
	}
}
