package plugin.lsttokens.eqslot;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.EquipSlotLstToken;

/**
 * Class deals with NUMSLOTS Token
 */
public class NumslotsToken implements EquipSlotLstToken
{

	@Override
	public String getTokenName()
	{
		return "NUMSLOTS";
	}

	@Override
	public boolean parse(EquipSlot eqSlot, String value, String gameMode)
	{
		//TODO: (DJ) this sucks, and means we have tokens that 
		//  we don't know the names of.  we need new syntax here.
		//TODO: revisit in 5.11.x
		final StringTokenizer token = new StringTokenizer(value, SystemLoader.TAB_DELIM);

		while (token.hasMoreTokens())
		{
			// parse the default number of each type
			final String cString = token.nextToken().trim();
			final StringTokenizer cTok = new StringTokenizer(cString, Constants.COLON);

			if (cTok.countTokens() == 2)
			{
				final String eqSlotType = cTok.nextToken();
				final String aNum = cTok.nextToken();
				if (!getTokenName().equals(eqSlotType))
				{
					Globals.setEquipSlotTypeCount(eqSlotType, Integer.parseInt(aNum));
					SystemCollections.addToBodyStructureList(eqSlotType, gameMode);
				}
			}
		}
		return true;
	}
}
