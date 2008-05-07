package pcgen.base.lang;

import java.util.Collection;

public final class StringUtil
{

	private StringUtil()
	{
		// Do not instantiate
	}

	/**
	 * Concatenates the Collection of Strings into a String using the separator
	 * as the delimiter.
	 * 
	 * @param strings
	 *            An Collection of strings
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String
	 */
	public static String join(final Collection<?> strings,
		final String separator)
	{
		return joinToStringBuffer(strings, separator).toString();
	}

	/**
	 * Concatenates the Collection of Strings into a StringBuffer using the
	 * separator as the delimiter.
	 * 
	 * @param strings
	 *            An Collection of strings
	 * @param separator
	 *            The separating character
	 * @return A 'separator' separated String
	 */
	public static StringBuilder joinToStringBuffer(final Collection<?> strings,
		final String separator)
	{
		if (strings == null)
		{
			return new StringBuilder();
		}

		final StringBuilder result = new StringBuilder(strings.size() * 10);

		boolean needjoin = false;

		for (Object obj : strings)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.toString());
		}

		return result;
	}

	/**
	 * Concatenates the Array of Strings into a String using the separator as
	 * the delimiter.
	 * 
	 * @param strings
	 *            An Array of strings
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String
	 */
	public static String join(String[] strings, String separator)
	{
		if (strings == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(strings.length * 10);

		boolean needjoin = false;

		for (Object obj : strings)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.toString());
		}

		return result.toString();
	}
}
