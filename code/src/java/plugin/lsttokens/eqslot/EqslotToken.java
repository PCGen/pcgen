package plugin.lsttokens.eqslot;

import pcgen.core.character.EquipSlot;
import pcgen.persistence.lst.EquipSlotLstToken;

/**
 * Class deals with EQSLOT Token
 */
public class EqslotToken implements EquipSlotLstToken
{

    @Override
    public String getTokenName()
    {
        return "EQSLOT";
    }

    @Override
    public boolean parse(EquipSlot eqSlot, String value, String gameMode)
    {
        eqSlot.setSlotName(value.intern());
        return true;
    }
}
