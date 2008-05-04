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
		if (visType.equalsIgnoreCase("EXPORT"))
		{
			ability.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (visType.equalsIgnoreCase("NO"))
		{
			ability.setVisibility(Visibility.HIDDEN);
		}
		else if (visType.equalsIgnoreCase("DISPLAY"))
		{
			ability.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else
		{
			if (!"YES".equalsIgnoreCase(visType))
			{
				Logging.errorPrint("Unexpected value used in " + getTokenName()
						+ " in Ability");
				Logging.errorPrint(" " + visType + " is not a valid value for "
						+ getTokenName());
				Logging
						.errorPrint(" Valid values in Ability are EXPORT, NO, DISPLAY, and YES");
				return false;
			}
			ability.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}
}
