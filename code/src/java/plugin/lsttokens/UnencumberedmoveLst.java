/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.enumeration.Load;

/**
 * @author djones4
 */
public class UnencumberedmoveLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "UNENCUMBEREDMOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setEncumberedLoadMove(Load.LIGHT, anInt);
		obj.setEncumberedArmorMove(Load.LIGHT, anInt);
		
		final StringTokenizer st = new StringTokenizer(value, "|");
		
		while (st.hasMoreTokens())
		{
			final String loadString = st.nextToken();
		
			if (loadString.equalsIgnoreCase("MediumLoad"))
			{
				obj.setEncumberedLoadMove(Load.MEDIUM, anInt);
			}
			else if (loadString.equalsIgnoreCase("HeavyLoad"))
			{
				obj.setEncumberedLoadMove(Load.HEAVY, anInt);
			}
			else if (loadString.equalsIgnoreCase("Overload"))
			{
				obj.setEncumberedLoadMove(Load.OVERLOAD, anInt);
			}
			else if (loadString.equalsIgnoreCase("MediumArmor"))
			{
				obj.setEncumberedArmorMove(Load.MEDIUM, anInt);
			}
			else if (loadString.equalsIgnoreCase("HeavyArmor"))
			{
				obj.setEncumberedArmorMove(Load.OVERLOAD, anInt);
			}
			else if (loadString.equalsIgnoreCase("LightLoad") || loadString.equalsIgnoreCase("LightArmor"))
			{
				//do nothing, but accept values as valid
			}
			else
			{
				ShowMessageDelegate.showMessageDialog("Invalid value of \"" + loadString + "\" for UNENCUMBEREDMOVE in \"" + obj.getDisplayName() + "\".",
					"PCGen", MessageType.ERROR);
			}
		}
		return true;
	}
}
