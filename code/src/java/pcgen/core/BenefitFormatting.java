package pcgen.core;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;

public class BenefitFormatting
{

	public static String getBenefits(PlayerCharacter aPC, Ability a)
	{
		List<Description> theBenefits = a.getListFor(ListKey.BENEFIT);
		if ( theBenefits == null )
		{
			return Constants.EMPTY_STRING;
		}
		final StringBuffer buf = new StringBuffer();
		boolean needSpace = false;
		for ( final Description desc : theBenefits )
		{
			final String str = desc.getDescription(aPC, a);
			if ( str.length() > 0 )
			{
				if ( needSpace )
				{
					buf.append(' ');
				}
				buf.append(str);
				needSpace = true;
			}
		}
		return buf.toString();
	}

}
