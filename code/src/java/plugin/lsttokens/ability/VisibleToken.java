package plugin.lsttokens.ability;

import pcgen.core.Ability;
import pcgen.persistence.lst.AbilityLstToken;

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
			ability.setVisible(Ability.VISIBILITY_OUTPUT_ONLY);
		}
		else if (visType.startsWith("NO"))
		{
			ability.setVisible(Ability.VISIBILITY_HIDDEN);
		}
		else if (visType.startsWith("DISPLAY"))
		{
			ability.setVisible(Ability.VISIBILITY_DISPLAY_ONLY);
		}
		else
		{
			ability.setVisible(Ability.VISIBILITY_DEFAULT);
		}
		return true;
	}
}
