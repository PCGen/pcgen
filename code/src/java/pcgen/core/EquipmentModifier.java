/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.scope.EquipmentPartScope;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Delta;

/**
 * Definition and games rules for an equipment modifier.
 */
public final class EquipmentModifier extends PObject implements Comparable<Object>, Cloneable
{
    private static final String PERCENT_CHOICE_PATTERN = Pattern.quote(Constants.LST_PERCENT_CHOICE);
    private static final Formula CHOICE_FORMULA = FormulaFactory.getFormulaFor("%CHOICE");

    /**
     * returns all BonusObjs that are "active", for example, ones that pass all
     * prerequisite tests and should be applied.
     *
     * @param caller The object that will be used to test prerequisites
     *               against to determine if a bonus should be applied.
     * @param aPC    The PC that the prerequisites will be applied against to
     *               determine if a bonus is active
     * @return returns all BonusObjs that are "active"
     */
    public List<BonusObj> getActiveBonuses(final Equipment caller, final PlayerCharacter aPC)
    {
        final List<BonusObj> aList = new ArrayList<>();

        for (BonusObj bonus : getBonusList(caller))
        {
            if (PrereqHandler.passesAll(bonus, caller, aPC))
            {
                aPC.setApplied(bonus, true);
                aList.add(bonus);
            }
        }

        return aList;
    }

    /**
     * This is prohibited since the associations are stored on the Equipment.
     * Thankfully, bonuses are usually exported through the Equipment, via
     * getBonusList(Equipment) or via getActiveBonuses(Equipment, PC), not
     * as a stand-alone behavior.
     */
    @Override
    public List<BonusObj> getBonusList(PlayerCharacter pc)
    {
        throw new UnsupportedOperationException(
                "Cannot resolve bonuses on EqMod via PlayerCharacter - requires Equipment");
    }

    /**
     * This method assumes that there can only be one bonus in any given
     * Equipment modifier that uses %CHOICE.  It retrieves the list of bonuses
     * using the super classes getBonusList() and then examines each of them in
     * turn.  If it finds that one of the bonuses contains %CHOICE, it replaces
     * it with a one new bonus object for every entry in "associated".
     *
     * @param e a PObject that has the associated bonuses
     * @return a complete list of bonus objects with %CHOICE expanded to
     * include one entry for each associated choice.
     */
    @Override
    public List<BonusObj> getBonusList(Equipment e)
    {
        return getBonusList(super.getBonusList(e), e.getAssociationList(this));
    }

    private List<BonusObj> getBonusList(List<BonusObj> bonusList, List<String> associations)
    {
        ArrayList<BonusObj> myBonusList = new ArrayList<>(bonusList);
        for (int i = myBonusList.size() - 1;i > -1;i--)
        {
            final BonusObj aBonus = myBonusList.get(i);
            final String aString = aBonus.toString();

            final int idx = aString.indexOf("%CHOICE");

            if (idx >= 0)
            {
                // Add an entry for each of the associated list entries
                for (String assoc : associations)
                {
                    final BonusObj newBonus =
                            Bonus.newBonus(Globals.getContext(), aString.replaceAll(PERCENT_CHOICE_PATTERN, assoc));

                    if (aBonus.hasPrerequisites())
                    {
                        newBonus.clearPrerequisiteList();
                        for (Prerequisite prereq : aBonus.getPrerequisiteList())
                        {
                            try
                            {
                                newBonus.addPrerequisite(prereq.specify(assoc));
                            } catch (CloneNotSupportedException e)
                            {
                                // TODO Handle this?
                            }
                        }
                    }

                    myBonusList.add(newBonus);
                }

                myBonusList.remove(aBonus);
            }
        }

        return myBonusList;
    }

    /**
     * Does this Equipment Modifier add aType to the equipment it is applied
     * to? If aType begins with an &#34; (Exclamation Mark) the &#34; will
     * be removed before checking the type.
     *
     * @param type the type string to check for.
     * @return Whether the item is of this type
     */
    public boolean isIType(Type type)
    {
        return containsInList(ListKey.ITEM_TYPES, type);
    }

    /**
     * A list of Special properties tailored to the PC and the piece of
     * equipment passed as arguments.
     *
     * @param caller The Equipment this modifier is applied to.
     * @param pc     The Pc that the Special Property will be tailored for
     * @return a list of strings representing Special properties to be
     * applied to the Equipment
     */
    public List<String> getSpecialProperties(final Equipment caller, final PlayerCharacter pc)
    {
        final List<String> retList = new ArrayList<>();
        for (SpecialProperty sp : getSafeListFor(ListKey.SPECIAL_PROPERTIES))
        {
            String propName = sp.getParsedText(pc, caller, this);

            // TODO WTF is this loop doing? how many times does it expect "%CHOICE" to
            // appear in the special property?

            for (String assoc : caller.getAssociationList(this))
            {
                propName = propName.replaceFirst("%CHOICE", assoc);
            }

            if ((propName != null) && !propName.equals(""))
            {
                retList.add(propName);
            }
        }

        return retList;
    }

    /**
     * Get the bonus to
     *
     * @param aPC   a Player Character object
     * @param aType
     * @param aName
     * @param obj
     * @return bonus
     */
    public double bonusTo(final PlayerCharacter aPC, final String aType, final String aName, final Equipment obj)
    {
        return BonusCalc.bonusTo(this, aType, aName, obj, getBonusList(obj), aPC);
    }

    /**
     * Clone an EquipmentModifier
     *
     * @return a clone of the EquipmentModifier
     */
    @Override
    public EquipmentModifier clone()
    {
        EquipmentModifier aObj = null;

        try
        {
            aObj = (EquipmentModifier) super.clone();
        } catch (CloneNotSupportedException exc)
        {
            ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
        }

        return aObj;
    }

    /* TODO: This needs to call getEquipNamePortion until after 5.10, when it can
     * be changed to a programmer useful string as per normal.
     */

    /**
     * Return a string representation of the EquipmentModifier.
     *
     * @return a String representation of the EquipmentModifier
     */
    @Override
    public String toString()
    {
        return getDisplayName();
    }

    public int getSR(Equipment parent, PlayerCharacter aPC)
    {
        SpellResistance sr = get(ObjectKey.SR);
        if (sr == null)
        {
            return 0;
        }

        if (sr.getReduction().equals(CHOICE_FORMULA) && parent.hasAssociations(this))
        {
            return Delta.parseInt(parent.getFirstAssociation(this));
        }

        return sr.getReduction().resolve(parent, true, aPC, getQualifiedKey()).intValue();
    }

    /**
     * lets this object compare to others.
     *
     * @param o The object to compare to
     * @return -1, 0 or 1 as per Comparator
     */
    @Override
    public int compareTo(final Object o)
    {
        if (o instanceof EquipmentModifier)
        {
            return getKeyName().compareTo(((CDOMObject) o).getKeyName());
        }

        return getKeyName().compareTo(o.toString());
    }

    public String getDisplayType()
    {
        List<Type> trueTypeList = getTrueTypeList(true);
        return StringUtil.join(trueTypeList, ".");
    }

    @Override
    public Optional<String> getLocalScopeName()
    {
        return Optional.of(EquipmentPartScope.PC_EQUIPMENT_PART);
    }

    private VarScoped variableParent;

    public void setVariableParent(VarScoped vs)
    {
        variableParent = vs;
    }

    @Override
    public Optional<VarScoped> getVariableParent()
    {
        return Optional.ofNullable(variableParent);
    }
}
