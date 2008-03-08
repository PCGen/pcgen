package plugin.lsttokens.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import pcgen.core.spell.Spell;
import pcgen.core.spell.Spell.SpellPointType;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.util.Logging;

/**
 * Class deals with DURATION Token
 */
public class SpellPointCostToken implements SpellLstToken
{

	public String getTokenName()
	{
		return "SPELLPOINTCOST";
	}

	public boolean parse(Spell spell, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);
		while (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();
			boolean hasSubtokens = false;
			
			if (token.equals(".CLEAR"))
			{
				spell.clearSpellPointCost();
				return true;
			}
			if(token.contains("="))
			{
				hasSubtokens = true;
				String[] components = token.split("=");
				if (components.length != 2 || (components[0] == null || components[1] == null ))
				{
					Logging.errorPrint("Invalid number of Arguments in " + getTokenName() + "(" 
						+ spell.getDisplayName()+"): " +value);
					return false;
					
				}
				else
				{
					int tempvalue;
					try
					{
						tempvalue = Integer.parseInt(components[1]);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Invalid Value in " + getTokenName() + "(" 
							+ spell.getDisplayName()+"): " + token + ".  Value must be an integer.");
						return false;
					}
					
					spell.setParsedSpellPointCost(components[0], tempvalue);
				}
			}
			else if(hasSubtokens)
			{
				Logging.errorPrint("Invalid number of Arguments in " + getTokenName() + "(" 
					+ spell.getDisplayName()+"): " +value);
				return false;
			}
			else
			{
				int tempvalue;
				try
				{
					tempvalue = Integer.parseInt(token);
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint("Invalid Value in " + getTokenName() + "(" 
						+ spell.getDisplayName()+"): " + token + ".  Value must be an integer.");
					return false;
				}
				spell.setParsedSpellPointCost("TOTAL", tempvalue);
			}
		}
		return true;
	}
}
