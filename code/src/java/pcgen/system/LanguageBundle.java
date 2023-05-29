/*
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
 */
package pcgen.system;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import pcgen.util.Logging;

/**
 * {@code LanguageBundle} manages the localisation of the PCGen interface.
 * It provides a set of features to translate i18n keys into text in the
 * language chosen in preferences.
 *
 */
public final class LanguageBundle
{
	/** Key primarily for use in IDE il8n tools. */
	private static final String BUNDLE_NAME = "pcgen.lang"; //$NON-NLS-1$

	/** Undefined Property */
	private static final String UNDEFINED = " not defined."; //$NON-NLS-1$
	private static ResourceBundle bundle;

	private LanguageBundle()
	{
	}

	/**
	 * Get the Mnemonic
	 * @param property The key of the property to lookup.
	 * @return Mnemonic of the property
	 */
	public static int getMnemonic(String property)
	{
		return getMnemonic(property, '\0');
	}

	/**
	 * Convenience method to retrieve a localised string with no parameters.
	 * author: Thomas Behr 03-01-02
	 * @param key The key of the property to be retrieved.
	 * @return String The localised string.
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
		String string = getString(aKey);
		if (varargs != null && varargs.length > 0)
		{
			return MessageFormat.format(string, varargs);
		}
		return string;
	}

	private static char getMnemonic(String property, char def)
	{
		final String mnemonic = getProperty(property);

		if (!mnemonic.isEmpty())
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
		if (bundle == null)
		{
			init();
		}

		String value;
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
	 * Initialises the LanguageBundle loading the appropriate bundles
	 * depending on the system Locale and the option selected in preferences.
	 */
	static synchronized void init()
	{
		if (bundle != null)
		{
			Logging.log(Logging.WARNING, "Reinitialising the language bundle."); //$NON-NLS-1$
		}
		Logging.log(Logging.INFO, MessageFormat.format("Initialising language bundle with locale {0}.", //$NON-NLS-1$
			Locale.getDefault()));

		bundle = ResourceBundle.getBundle(BUNDLE_NAME + ".LanguageBundle"); //$NON-NLS-1$
	}

	/**
	 * This method is meant to be used in tests to reload the bundle if the default locale has changed.
	 */
	public static void reload()
	{
		Locale l = Locale.getDefault();
		if (bundle != null && ((l == null && bundle.getLocale() == null) || !l.equals(bundle.getLocale())))
		{
			bundle = null;
		}
	}

	/**
	 * Standard bundle key prefix.
	 */
	public static final String KEY_PREFIX = "in_"; //$NON-NLS-1$

	/**
	 * Allow pretty formatting of multiplier. For example, if d is 0.5d, it 
	 * returns x 1/2 ( 
	 * @param d a double value
	 * @return a formatted String
	 */
	public static String getPrettyMultiplier(double d)
	{
		if (d == 0.25d)
		{
			return getString("in_multQuarter"); //$NON-NLS-1$
		}
		else if (d == 0.5d)
		{
			return getString("in_multHalf"); //$NON-NLS-1$
		}
		else if (d == 0.75d)
		{
			return getString("in_multThreeQuarter"); //$NON-NLS-1$
		}
		else
		{
			return MessageFormat.format(getString("in_multiply"), d); //$NON-NLS-1$
		}
	}

	public static ResourceBundle getBundle()
	{
		return bundle;
	}
}
