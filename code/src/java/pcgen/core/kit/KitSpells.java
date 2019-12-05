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
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * {@code KitSpells}.
 */
public final class KitSpells extends BaseKit
{
    private String spellBook;
    private CDOMSingleRef<PCClass> castingClass;
    DoubleKeyMap<KnownSpellIdentifier, List<CDOMSingleRef<Ability>>, Integer> spells = new DoubleKeyMap<>();
    private Formula countFormula;

    private List<KitSpellBookEntry> theSpells = null;

    /**
     * @param formula the count formula to set
     */
    public void setCount(Formula formula)
    {
        countFormula = formula;
    }

    /**
     * @return the count formula
     */
    public Formula getCount()
    {
        return countFormula;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (castingClass != null)
        {
            sb.append(castingClass.getLSTformat(false));
        }
        sb.append(' ').append(spellBook).append(": ");
        boolean needComma = false;
        for (KnownSpellIdentifier ksi : spells.getKeySet())
        {
            if (needComma)
            {
                sb.append(',');
            }
            needComma = true;
            sb.append(ksi.getLSTformat());
            Set<List<CDOMSingleRef<Ability>>> abilities = spells.getSecondaryKeySet(ksi);
            for (List<CDOMSingleRef<Ability>> list : abilities)
            {
                if (list != null)
                {
                    sb.append(" [");
                    sb.append(ReferenceUtilities.joinLstFormat(list, ","));
                    sb.append(']');
                }
                Integer count = spells.get(ksi, list);
                if (count > 1)
                {
                    sb.append(" (").append(count).append(")");
                }

            }
        }

        return sb.toString();
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        theSpells = null;

        PCClass aClass = findDefaultSpellClass(castingClass, aPC);
        if (aClass == null)
        {
            warnings.add("SPELLS: Character does not have " + castingClass + " spellcasting class.");
            return false;
        }

        String workingBook = spellBook == null ? Globals.getDefaultSpellBook() : spellBook;
        List<KitSpellBookEntry> aSpellList = new ArrayList<>();
        if (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS) && !workingBook.equals(Globals.getDefaultSpellBook()))
        {
            warnings.add("SPELLS: " + aClass.getDisplayName() + " can only add to " + Globals.getDefaultSpellBook());
            return false;
        }

        for (KnownSpellIdentifier ksi : spells.getKeySet())
        {
            Collection<Spell> allSpells =
                    ksi.getContainedSpells(aPC, Collections.singletonList(aClass.get(ObjectKey.CLASS_SPELLLIST)));
            Set<List<CDOMSingleRef<Ability>>> feats = spells.getSecondaryKeySet(ksi);
            for (Spell sp : allSpells)
            {
                for (List<CDOMSingleRef<Ability>> list : feats)
                {
                    Integer count = spells.get(ksi, list);
                    aSpellList.add(new KitSpellBookEntry(spellBook, sp, list, count));
                }
            }
        }

        final Formula choiceFormula = getCount();
        int numberOfChoices;

        if (choiceFormula == null)
        {
            numberOfChoices = aSpellList.size();
        } else
        {
            numberOfChoices = choiceFormula.resolve(aPC, "").intValue();
        }

        //
        // Can't choose more entries than there are...
        //
        if (numberOfChoices > aSpellList.size())
        {
            numberOfChoices = aSpellList.size();
        }

        if (numberOfChoices == 0)
        {
            return false;
        }

        List<KitSpellBookEntry> xs;

        if (numberOfChoices == aSpellList.size())
        {
            xs = aSpellList;
        } else
        {
            //
            // Force user to make enough selections
            //
            while (true)
            {
                xs = Globals.getChoiceFromList("Choose " + aClass.getKeyName() + " spell(s) for " + workingBook,
                        aSpellList, new ArrayList<>(), numberOfChoices, aPC);

                if (!xs.isEmpty())
                {
                    break;
                }
            }
        }

        //
        // Add to list of things to add to the character
        //
        for (KitSpellBookEntry obj : xs)
        {
            if (obj != null)
            {
                obj.setPCClass(aClass);
                if (theSpells == null)
                {
                    theSpells = new ArrayList<>();
                }
                theSpells.add(obj);
            } else
            {
                warnings.add("SPELLS: Non-existant spell chosen");
            }
        }

        return (theSpells != null && !theSpells.isEmpty());
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        for (KitSpellBookEntry sbe : theSpells)
        {
            updatePCSpells(aPC, sbe, aPC.getClassKeyed(sbe.getPCClass().getKeyName()));
        }
    }

    private PCClass findDefaultSpellClass(final CDOMSingleRef<PCClass> ref, PlayerCharacter aPC)
    {
        if (castingClass == null)
        {
            List<? extends PObject> spellcastingClasses = aPC.getSpellClassList();
            for (PObject obj : spellcastingClasses)
            {
                if (obj instanceof PCClass)
                {
                    return (PCClass) obj;
                }
            }
            return null;
        }
        return aPC.getClassKeyed(ref.get().getKeyName());
    }

    /**
     * Add spells from this Kit to the PC
     *
     * @param pc      The PC to add the spells to
     * @param aSpell  A Spell to add to the PC
     * @param pcClass The class instance the spells are to be added to.
     */
    private void updatePCSpells(final PlayerCharacter pc, final KitSpellBookEntry aSpell, final PCClass pcClass)
    {
        Spell spell = aSpell.getSpell();

        int spLevel = 99;

        // Check to see if we have any domains that have this spell.

        PObject owner = null;
        if (pc.hasDomains())
        {
            for (Domain domain : pc.getDomainSet())
            {
                List<? extends CDOMList<Spell>> lists = pc.getSpellLists(domain);
                int newLevel = SpellLevel.getFirstLevelForKey(spell, lists, pc);
                if (newLevel > 0 && newLevel < spLevel)
                {
                    spLevel = newLevel;
                    owner = domain;
                }
            }
        }

        if (spLevel == 99)
        {
            spLevel = SpellLevel.getFirstLevelForKey(spell, pc.getSpellLists(pcClass), pc);
            owner = pcClass;
        }

        if (spLevel < 0)
        {
            Logging.errorPrint(
                    "SPELLS: " + pcClass.getDisplayName() + " cannot cast spell \"" + spell.getKeyName() + "\"");

            return;
        }

        final CharacterSpell cs = new CharacterSpell(owner, spell);
        final List<CDOMSingleRef<Ability>> modifierList = aSpell.getModifiers();
        int adjustedLevel = spLevel;
        List<Ability> metamagicFeatList = new ArrayList<>();
        for (CDOMSingleRef<Ability> feat : modifierList)
        {
            Ability anAbility = feat.get();
            adjustedLevel += anAbility.getSafe(IntegerKey.ADD_SPELL_LEVEL);
            metamagicFeatList.add(anAbility);
        }
        if (metamagicFeatList.isEmpty())
        {
            metamagicFeatList = null;
        }
        if (!pc.hasSpellBook(aSpell.getBookName()))
        {
            pc.addSpellBook(aSpell.getBookName());
        }

        for (int numTimes = 0;numTimes < aSpell.getCopies();numTimes++)
        {
            final String aString = pc.addSpell(cs, metamagicFeatList, pcClass.getKeyName(), aSpell.getBookName(),
                    adjustedLevel, spLevel);
            if (!aString.isEmpty())
            {
                Logging.errorPrint("Add spell failed:" + aString);
                return;
            }
        }
    }

    @Override
    public String getObjectName()
    {
        return "Spells";
    }

    public void setSpellBook(String string)
    {
        spellBook = string;
    }

    public String getSpellBook()
    {
        return spellBook;
    }

    public void setCastingClass(CDOMSingleRef<PCClass> reference)
    {
        castingClass = reference;
    }

    public CDOMSingleRef<PCClass> getCastingClass()
    {
        return castingClass;
    }

    public void addSpell(KnownSpellIdentifier ksi, ArrayList<CDOMSingleRef<Ability>> featList, int count)
    {
        spells.put(ksi, featList, count);
    }

    public Collection<KnownSpellIdentifier> getSpells()
    {
        return spells.getKeySet();
    }

    public Collection<List<CDOMSingleRef<Ability>>> getAbilities(KnownSpellIdentifier ksi)
    {
        return spells.getSecondaryKeySet(ksi);
    }

    public Integer getSpellCount(KnownSpellIdentifier ksi, List<CDOMSingleRef<Ability>> abils)
    {
        return spells.get(ksi, abils);
    }
}
