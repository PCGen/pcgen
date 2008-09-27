package pcgen.core.analysis;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class DescriptionFormatting
{

	private static String piDescString(PlayerCharacter aPC, PObject cdo,
		boolean useHeader)
	{
		final String desc = aPC.getDescription(cdo);

		if (cdo.getSafe(ObjectKey.DESC_PI))
		{
			final StringBuffer sb = new StringBuffer(desc.length() + 30);

			if (useHeader)
			{
				sb.append("<html>");
			}

			sb.append("<b><i>").append(desc).append("</i></b>");

			if (useHeader)
			{
				sb.append("</html>");
			}

			return sb.toString();
		}

		return desc;
	}

	/**
	 * Get the Product Identity description String
	 * @return the Product Identity description String
	 */
	public static String piDescString(PlayerCharacter aPC, PObject po)
	{
		return piDescString(aPC, po, true);
	}

	/**
	 * In some cases, we need a PI-formatted string to place within a
	 * pre-existing <html> tag
	 * @return PI description
	 */
	public static String piDescSubString(PlayerCharacter aPC, PObject po)
	{
		return piDescString(aPC, po, false);
	}

}
