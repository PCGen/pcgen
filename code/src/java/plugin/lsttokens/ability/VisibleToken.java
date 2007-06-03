package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag in the
 * definition of an Ability.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2007-02-10 11:55:15 -0500
 * (Sat, 10 Feb 2007) $
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisibleToken implements AbilityLstToken
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/**
	 * @see pcgen.persistence.lst.AbilityLstToken#parse(pcgen.core.Ability,
	 *      java.lang.String)
	 */
	public boolean parse(Ability ability, String value)
	{
		final String visType = value.toUpperCase();
		if (visType.startsWith("EXPORT"))
		{
			if (!"EXPORT".equalsIgnoreCase(visType))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Ability");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" assuming you meant EXPORT, please use EXPORT (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			ability.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (visType.startsWith("NO"))
		{
			if (!"NO".equalsIgnoreCase(visType))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Ability");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" assuming you meant NO, please use NO (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			ability.setVisibility(Visibility.HIDDEN);
		}
		else if (visType.startsWith("DISPLAY"))
		{
			if (!"DISPLAY".equalsIgnoreCase(visType))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Ability");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" assuming you meant DISPLAY, please use DISPLAY (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			ability.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else
		{
			if (!"YES".equalsIgnoreCase(visType))
			{
				Logging.deprecationPrint("Unexpected value used in "
					+ getTokenName() + " in Ability");
				Logging.deprecationPrint(" " + visType
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Ability are EXPORT, NO, DISPLAY, and YES");
				Logging
					.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
			}
			ability.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}
}
