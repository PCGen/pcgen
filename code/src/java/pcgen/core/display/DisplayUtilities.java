package pcgen.core.display;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;

public final class DisplayUtilities
{
	private DisplayUtilities()
	{
		//Can't instantiate
	}

	/**
	 * Concatenates the Display Name of given Collection of CDOMObjects into a String
	 * using the separator as the delimiter.
	 * 
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection.
	 * 
	 * This method is value-semantic. DisplayUtilities will not maintain a
	 * reference to or modify the given Collection.
	 * 
	 * @param cdoCollection
	 *            An Collection of CDOMObjects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the Display Name of the
	 *         given Collection of CDOMObject objects
	 */
	public static String joinDisplayName(
		Collection<? extends CDOMObject> cdoCollection, String separator)
	{
		if (cdoCollection == null)
		{
			return "";
		}

		final StringBuilder result =
				new StringBuilder(cdoCollection.size() * 10);

		boolean needjoin = false;

		for (CDOMObject obj : cdoCollection)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.getDisplayName());
		}

		return result.toString();
	}

}
