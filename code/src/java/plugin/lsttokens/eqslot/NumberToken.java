package plugin.lsttokens.eqslot;

import pcgen.core.character.EquipSlot;
import pcgen.persistence.lst.EquipSlotLstToken;

/**
 * Class deals with NUMBER Token
 */
public class NumberToken implements EquipSlotLstToken
{

	public String getTokenName()
	{
		return "NUMBER";
	}

	public boolean parse(EquipSlot eqSlot, String value, String gameMode)
	{
		eqSlot.setSlotNumType(value);
		return true;
	}
}
