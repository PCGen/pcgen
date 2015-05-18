package pcgen.core;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ListKey;
import pcgen.util.Logging;

public class BenefitFormatting
{

	public static String getBenefits(PlayerCharacter aPC, List<? extends Object> objList)
	{
		if (objList.size() == 0)
		{
			return "";
		}
		PObject sampleObject;
		Object b = objList.get(0);
		if (b instanceof PObject)
		{
			sampleObject = (PObject) b;
		}
		else if (b instanceof CNAbility)
		{
			sampleObject = ((CNAbility) b).getAbility();
		}
		else
		{
			Logging
				.errorPrint("Unable to resolve Description with object of type: "
					+ b.getClass().getName());
			return "";
		}
		List<Description> theBenefits = sampleObject.getListFor(ListKey.BENEFIT);
		if ( theBenefits == null )
		{
			return Constants.EMPTY_STRING;
		}
		final StringBuilder buf = new StringBuilder(250);
		boolean needSpace = false;
		for ( final Description desc : theBenefits )
		{
			final String str = desc.getDescription(aPC, objList);
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
