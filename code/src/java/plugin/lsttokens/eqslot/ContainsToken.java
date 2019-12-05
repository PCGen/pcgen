package plugin.lsttokens.eqslot;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.lst.EquipSlotLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CONTAINS Token
 */
public class ContainsToken implements EquipSlotLstToken
{

    @Override
    public String getTokenName()
    {
        return "CONTAINS";
    }

    @Override
    public boolean parse(EquipSlot eqSlot, String value, String gameMode)
    {
        if (value == null || value.isEmpty())
        {
            Logging.log(Logging.LST_ERROR, "Invalid empty " + getTokenName() + " value.");
            return false;
        }

        final StringTokenizer token = new StringTokenizer(value, Constants.EQUALS);

        if (token.countTokens() < 2)
        {
            Logging.log(Logging.LST_ERROR,
                    "Missing = in value '" + value + "' of " + getTokenName() + Constants.COLON + value);
            return false;
        } else if (token.countTokens() > 2)
        {
            Logging.log(Logging.LST_ERROR,
                    "Too many = in value '" + value + "' of " + getTokenName() + Constants.COLON + value);
            return false;
        }

        final String type = token.nextToken();
        final String numString = token.nextToken();
        final int num;

        if (numString.equals("*"))
        {
            num = 9999;
        } else
        {
            num = Integer.parseInt(numString);
        }

        final String[] types = type.split(",");
        for (String pair : types)
        {
            eqSlot.addContainedType(pair.intern());
        }
        eqSlot.setContainNum(num);
        return true;
    }
}
