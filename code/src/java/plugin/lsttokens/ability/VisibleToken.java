package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag
 * in the definition of an Ability.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
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
	 * @see pcgen.persistence.lst.AbilityLstToken#parse(pcgen.core.Ability, java.lang.String)
	 */
	public boolean parse(Ability ability, String value)
	{
		final String visType = value.toUpperCase();
		if (visType.startsWith("EXPORT"))
		{
			ability.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (visType.startsWith("NO"))
		{
			ability.setVisibility(Visibility.HIDDEN);
		}
		else if (visType.startsWith("DISPLAY"))
		{
			ability.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else
		{
			ability.setVisibility(Visibility.DEFAULT);
		}
		return true;
	}
}
