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

import pcgen.core.Constants;
import pcgen.core.Globals;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <code>PropertyFactory</code>
 *
 * @author Thomas Behr 03-01-02
 * @version $Revision$
 *
 * This good as is, as far as I can tell
 *
 * Mario Bonassin
 */
public final class PropertyFactory
{
	/** Undefined Property */
	public static final String UNDEFINED = " not defined."; //$NON-NLS-1$

	private static ResourceBundle bundle;

	/**
	 * author: Thomas Behr 03-01-02
	 */
	static
	{
		init();
	}

	/**
	 * Get the Mnemonic
	 * @param property
	 * @return Mnemonic of the property
	 */
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
	 * Returns a localized string from the language bundle for the key passed.
	 * This method accepts a variable number of parameters and will replace
	 * {argno} in the string with each passed paracter in turn.
	 * @param aKey The key of the string to retrieve
	 * @param varargs A variable number of parameters to substitute into the
	 * returned string.
	 * @return A formatted localized string
	 */
	public static String getFormattedString(String aKey, Object... varargs)
	{
		return MessageFormat.format(getString(aKey), varargs);
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
		String value = null;
		try
		{
			value = bundle.getString(key);
		}
		catch (MissingResourceException mre)
		{
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
		if (language == null || language.equals(Constants.EMPTY_STRING))
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
			bundle =
					ResourceBundle.getBundle(
						"pcgen/gui/prop/LanguageBundle", locale); //$NON-NLS-1$
		}
		catch (MissingResourceException mrex)
		{
			bundle = null;
			Logging.errorPrint("Can't find language bundle", mrex); //$NON-NLS-1$
		}
	}
}
