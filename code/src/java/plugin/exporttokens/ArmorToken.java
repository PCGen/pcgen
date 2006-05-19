package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Class deals with ARMOR Token
 */
public class ArmorToken extends Token
{
    
    /** Name of the Token */
    public static final String TOKENNAME = "ARMOR";

    /**
     * Get the token name
     * @return token name
     */
    public String getTokenName()
    {
        return TOKENNAME;
    }

    /**
     * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
     */
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        if (tokenSource.startsWith("ARMOR") && ((tokenSource.charAt(5) == '.') || Character.isDigit(tokenSource.charAt(5)))) {
            return getArmorToken(tokenSource, pc);
        }
        return null;
    }

    /**
     * @param tokenSource 
     * @param pc 
     * @return token
     * 
     */
    public static String getArmorToken(String tokenSource, PlayerCharacter pc)
    {
        return replaceTokenArmor(tokenSource, pc);
    }

    /**
     * See the PCGen Docs on Token Syntax
     * 
     * @param aString
     * @param aPC
     * @return int
     */
    private static String replaceTokenArmor(String aString, PlayerCharacter aPC)
    {
        final StringTokenizer aTok = new StringTokenizer(aString, ".");
        final String[] tokens = new String[aTok.countTokens()];

        for (int i = 0; aTok.hasMoreTokens(); ++i)
        {
            tokens[i] = aTok.nextToken();
        }

        String property = "";

        // When removing old syntax, this if should be removed
        if (tokens.length > 0)
        {
            property = tokens[tokens.length - 1];
        }

        int equipped = 3;
        int index = 0;
        String type = "";
        String subtype = "";
        int merge = Constants.MERGE_ALL;

        for (int i = 0; i < tokens.length; ++i)
        {
            if ("ARMOR".equals(tokens[i]))
            {
                continue;
            }

            // When removing old syntax, delete this if
            else if (tokens[i].startsWith("ARMOR"))
            {
                try
                {
                    index = Integer.parseInt(tokens[i].substring(5));
                }
                catch (NumberFormatException nfe)
                {
                    index = 0;
                }

                Logging.errorPrint("Old syntax ARMORx will be replaced for ARMOR.x");
            }
            else if ("ALL".equals(tokens[i]))
            {
                equipped = 3;
            }

            // When removing old syntax, delete this if
            else if (tokens[i].startsWith("ALL"))
            {
                Logging.errorPrint("Old syntax ALLx will be replaced for ALL.x");

                index = Integer.parseInt(tokens[i].substring(3));
                equipped = 3;
            }
            else if ("EQUIPPED".equals(tokens[i]))
            {
                equipped = 1;
            }

            // When removing old syntax, delete this if
            else if (tokens[i].startsWith("EQUIPPED"))
            {
                Logging.errorPrint("Old syntax EQUIPPEDx will be replaced for EQUIPPED.x");

                index = Integer.parseInt(tokens[i].substring(8));
                equipped = 1;
            }
            else if ("NOT_EQUIPPED".equals(tokens[i]))
            {
                equipped = 2;
            }

            // When removing old syntax, delete this if
            else if (tokens[i].startsWith("NOT_EQUIPPED"))
            {
                Logging.errorPrint("Old syntax NOT_EQUIPPEDx will be replaced for NOT_EQUIPPED.x");

                index = Integer.parseInt(tokens[i].substring(12));
                equipped = 2;
            }
            else if (tokens[i].equals("MERGENONE"))
            {
                merge = Constants.MERGE_NONE;
            }
            else if (tokens[i].equals("MERGELOC"))
            {
                merge = Constants.MERGE_LOCATION;
            }
            else if (tokens[i].equals("MERGEALL"))
            {
                merge = Constants.MERGE_ALL;
            }
            else if (tokens[i].equals("ISTYPE"))
            {
                property = tokens[i] + "." + tokens[i + 1];

                break;
            }
            else if (i < (tokens.length - 1))
            {
                try
                {
                    index = Integer.parseInt(tokens[i]);
                }
                catch (NumberFormatException exc)
                {
                    if ("".equals(type))
                    {
                        type = tokens[i];
                    }
                    else
                    {
                        subtype = tokens[i];
                    }
                }
            }
            else
            {
                property = tokens[i];
            }
        }

        if ("".equals(type))
        {
            return _replaceTokenArmor(index, property, equipped, merge, aPC);
        }
        else if ("SUIT".equals(type))
        {
            return _replaceTokenArmorSuit(index, subtype, property, equipped, merge, aPC);
        }
        else if ("SHIRT".equals(type))
        {
            return _replaceTokenArmorShirt(index, subtype, property, equipped, merge, aPC);
        }
        else if ("SHIELD".equals(type))
        {
            return _replaceTokenArmorShield(index, subtype, property, equipped, merge, aPC);
        }
        else if ("ITEM".equals(type) || "ACITEM".equals(type))
        {
            return _replaceTokenArmorItem(index, subtype, property, equipped, merge, aPC);
        }
        else
        {
            return _replaceTokenArmorVarious(index, type, subtype, property, equipped, merge, aPC);
        }
    }

    /**
     * select suits - shields
     * @param armor
     * @param property
     * @param equipped
     * @param merge
     * @param aPC
     * @return int
     */
    private static String _replaceTokenArmor(int armor, String property, int equipped, int merge, PlayerCharacter aPC)
    {
        final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Armor", equipped, merge);
        final List bArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shield", equipped, merge);

        for (Iterator e = bArrayList.iterator(); e.hasNext();)
        {
            aArrayList.remove(e.next());
        }

        if (armor < aArrayList.size())
        {
            final Equipment eq = (Equipment) aArrayList.get(armor);
            return _writeArmorProperty(eq, property, aPC);
        }

        return null;
    }

    /**
     * select items, which improve AC but are not type ARMOR
     * @param item
     * @param subtype
     * @param property
     * @param equipped
     * @param merge
     * @param aPC
     * @return int
     */
    private static String _replaceTokenArmorItem(int item, String subtype, String property, int equipped, int merge, PlayerCharacter aPC)
    {
        // select all pieces of equipment of status==equipped
        // filter all AC relevant stuff
        final List aArrayList = new ArrayList();

        for (Iterator e = aPC.getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
        {
            Equipment eq = (Equipment) e.next();

            if (("".equals(subtype) || eq.isType(subtype))
                && ((equipped == 3) || ((equipped == 2) && !eq.isEquipped()) || ((equipped == 1) && eq.isEquipped())))
            {
                if (eq.getBonusListString("AC") && !eq.isArmor() && !eq.isShield())
                {
                    aArrayList.add(eq);
                }
            }
        }

        if (item < aArrayList.size())
        {
            final Equipment eq = (Equipment) aArrayList.get(item);
            return _writeArmorProperty(eq, property, aPC);
        }

        return null;
    }

    /**
     * select shields
     * @param shield
     * @param subtype
     * @param property
     * @param equipped
     * @param merge
     * @param aPC
     * @return int
     */
    private static String _replaceTokenArmorShield(int shield, String subtype, String property, int equipped, int merge, PlayerCharacter aPC)
    {
        final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shield", subtype, equipped, merge);

        if (shield < aArrayList.size())
        {
            final Equipment eq = (Equipment) aArrayList.get(shield);
            return _writeArmorProperty(eq, property, aPC);
        }

        return null;
    }

    /**
     * select shirts
     * @param shirt
     * @param subtype
     * @param property
     * @param equipped
     * @param merge
     * @param aPC
     * @return int
     */
    private static String _replaceTokenArmorShirt(int shirt, String subtype, String property, int equipped, int merge, PlayerCharacter aPC)
    {
        final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Shirt", subtype, equipped, merge);

        if (shirt < aArrayList.size())
        {
            final Equipment eq = (Equipment) aArrayList.get(shirt);
            return _writeArmorProperty(eq, property, aPC);
        }

        return null;
    }

    /**
     * select suits
     * @param suit
     * @param subtype
     * @param property
     * @param equipped
     * @param merge
     * @param aPC
     * @return int
     */
    private static String _replaceTokenArmorSuit(int suit, String subtype, String property, int equipped, int merge, PlayerCharacter aPC)
    {
        final List aArrayList = aPC.getEquipmentOfTypeInOutputOrder("Suit", subtype, equipped, merge);

        //
        // Temporary hack until someone gets around to fixing it properly
        //
        //aArrayList.addAll(aPC.getEquipmentOfTypeInOutputOrder("Shirt", subtype, equipped));

        if (suit < aArrayList.size())
        {
            final Equipment eq = (Equipment) aArrayList.get(suit);
            return _writeArmorProperty(eq, property, aPC);
        }

        return null;
    }

    /**
     * select various stuff, that improves AC
     * @param index
     * @param type
     * @param subtype
     * @param property
     * @param equipped
     * @param merge
     * @param aPC
     * @return int
     */
    private static String _replaceTokenArmorVarious(int index, String type, String subtype, String property, int equipped,
                                          int merge, PlayerCharacter aPC)
    {
        Equipment eq;
        final List aArrayList = new ArrayList();

        for (Iterator mapIter = aPC.getEquipmentOfTypeInOutputOrder(type, subtype, equipped, merge).iterator();
            mapIter.hasNext();)
        {
            eq = (Equipment) mapIter.next();

            if (eq.getACMod(aPC).intValue() > 0)
            {
                aArrayList.add(eq);
            }
            else if (eq.getBonusListString("AC"))
            {
                aArrayList.add(eq);
            }
        }

        if (index < aArrayList.size())
        {
            eq = (Equipment) aArrayList.get(index);
            return _writeArmorProperty(eq, property, aPC);
        }

        return null;
    }

    private static String _writeArmorProperty(Equipment eq, String property, PlayerCharacter aPC)
    {
        String retString = "";
        
        if (property.startsWith("NAME"))
        {
            if (eq.isEquipped())
            {
                retString = retString + "*";
            }

            //retString = retString + eq.getName();
            retString = retString + eq.parseOutputName(eq.getOutputName(), aPC);
            retString = retString + eq.getAppliedName();
        }
        else if (property.startsWith("OUTPUTNAME"))
        {
            if (eq.isEquipped())
            {
                retString = retString + "*";
            }

            retString = retString + eq.parseOutputName(eq.getOutputName(), aPC);
            retString = retString + eq.getAppliedName();
        }
        else if (property.startsWith("TOTALAC"))
        {
            // adjustments for new equipment modifier
            // EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
            //FileAccess.write(output, Delta.toString(eq.getACMod()));
            retString = retString + Delta.toString((int) eq.bonusTo(aPC, "COMBAT", "AC", true));
        }
        else if (property.startsWith("BASEAC"))
        {
            // adjustments for new equipment modifier
            // EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
            //FileAccess.write(output, Delta.toString(eq.getACMod()));
            retString = retString + Delta.toString((int) eq.bonusTo("COMBAT", "AC", aPC, aPC));
        }
        else if (property.startsWith("ACBONUS"))
        {
            retString = retString + Delta.toString((int) eq.bonusTo(aPC, "COMBAT", "AC", true));
        }
        else if (property.startsWith("MAXDEX"))
        {
            final int iMax = eq.getMaxDex(aPC).intValue();

            if (iMax != Constants.MAX_MAXDEX)
            {
                retString = retString + Delta.toString(iMax);
            }
        }
        else if (property.startsWith("ACCHECK"))
        {
            retString = retString + Delta.toString(eq.acCheck(aPC));
        }
        else if (property.startsWith("EDR"))
        {
            retString = retString + Delta.toString(eq.eDR(aPC));
        }
        else if (property.startsWith("ISTYPE"))
        {
            if (eq.isType(property.substring(property.indexOf(".") + 1))) {
                retString = retString + "TRUE";
            } else {
                retString = retString + "FALSE";
            }
        }
        else if (property.startsWith("SPELLFAIL"))
        {
            retString = retString + eq.spellFailure(aPC).toString();
        }
        else if (property.startsWith("MOVE"))
        {
            final StringTokenizer aTok = new StringTokenizer(eq.moveString(), ",", false);
            String tempString = "";

            if (("M".equals(aPC.getSize()) || "S".equals(aPC.getSize())) && (aTok.countTokens() > 0))
            {
                tempString = aTok.nextToken();

                if ("S".equals(aPC.getSize()) && (aTok.countTokens() > 1))
                {
                    tempString = aTok.nextToken();
                }
            }

            retString = retString + tempString;
        }
        else if (property.startsWith("SPROP"))
        {
            retString = retString + eq.getSpecialProperties(aPC);
        }
        else if (property.startsWith("TYPE"))
        {
            String typeString = "";

            if (eq.isLight())
            {
                typeString = "Light";
            }
            else if (eq.isMedium())
            {
                typeString = "Medium";
            }
            else if (eq.isHeavy())
            {
                typeString = "Heavy";
            }
            else if (eq.isShield())
            {
                typeString = "Shield";
            }
            else if (eq.isExtra())
            {
                typeString = "Extra";
            }

            retString = retString + typeString;
        }
        else if (property.startsWith("WT"))
        {
            retString = retString + BigDecimalHelper.trimZeros(eq.getWeight(aPC).toString());
        }
        return retString;
    }
    
}
