/*
 * PropertyFactory.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on January 03, 2002, 2:15 PM
 */
package pcgen.util;

import pcgen.core.Globals;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <code>PropertyFactory</code>
 *
 * @author Thomas Behr 03-01-02
 * @version $Revision: 1.27 $
 *
 * This good as is, as far as I can tell
 *
 * Mario Bonassin
 */
public final class PropertyFactory
{
	public static final String UNDEFINED = " not defined.";
    private static ResourceBundle bundle;

	/**
	 * author: Thomas Behr 03-01-02
	 */
	static
	{
		init();
	}

	public static char getMnemonic(String property)
	{
		return getMnemonic(property, '\0');
	}

	/**
	 * convenience method
	 * author: Thomas Behr 03-01-02
	 * @param key
	 * @return String
	 */
	public static String getString(String key)
	{
		return getProperty(key);
	}

	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 * @param key
	 * @param arg0
	 * @return formatted String
	 */
	public static String getFormattedString(String key, Object arg0)
	{
		String prop = getString(key);
		Object[] args = new Object[] { arg0 };

		return MessageFormat.format(prop, args);
	}

	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 * @param key
	 * @param arg0
	 * @param arg1
	 * @return formatted String
	 */
	public static String getFormattedString(String key, Object arg0, Object arg1)
	{
		String prop = getString(key);
		Object[] args = new Object[] { arg0, arg1 };

		return MessageFormat.format(prop, args);
	}

	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 * @param key
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return formatted String
	 */
	public static String getFormattedString(String key, Object arg0, Object arg1, Object arg2)
	{
		String prop = getString(key);
		Object[] args = new Object[] { arg0, arg1, arg2 };

		return MessageFormat.format(prop, args);
	}

	/**
	 * convenience method
	 * author: Frugal 2004/01/13
	 * @param key
	 * @param args
	 * @return formatted String
	 */
	public static String getFormattedString(String key, Object[] args)
	{
		String prop = getString(key);

		return MessageFormat.format(prop, args);
	}

	private static char getMnemonic(String property, char def)
	{
		final String mnemonic = getProperty(property);

		if (mnemonic.length() != 0)
		{
			return mnemonic.charAt(0);
		}

		return def;
	}

	/**
	 * author: Thomas Behr 03-01-02
	 * @param key
	 * @return property
	 */
	private static String getProperty(String key)
	{
	    String value=null;
	    try {
	        value = bundle.getString(key);
	    }
	    catch (MissingResourceException mre) {
	        value = key + UNDEFINED;
	    }
		return value;
	}

	/**
	 * author: Thomas Behr 03-01-02
	 * Initialises the PropertyFactory loading the appropriate bundles
	 * depending on the system Locale and the option selected in preferences.
	 */
	private static void init()
	{
		Locale locale = null;
		String language = Globals.getLanguage();
		if (language == null || language.equals(""))
		{
			locale = Locale.getDefault();
		}
		else
		{
			locale = new Locale(Globals.getLanguage(), Globals.getCountry());
			// We reset the default so that
			// a) The dialog buttons match the selected language.
			// b) English (if selected) isn't overriden by the system default
			Locale.setDefault(locale);
		}

		try
		{
			bundle = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", locale);
		}
		catch (MissingResourceException mrex)
		{
			bundle = null;
			Logging.errorPrint("Can't find language bundle", mrex);
		}
	}
}
