/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.MasterAvailableSpellFacet;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.SpellBuilderFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * SpellBuilderFacadeImpl prepares the data for display in the Spell Choice
 * Dialog. It also manages the user's selections and updates the available
 * choices to match the choices already made.
 */
public class SpellBuilderFacadeImpl implements SpellBuilderFacade
{
    private final DefaultReferenceFacade<InfoFacade> pcClass;
    private final DefaultReferenceFacade<Integer> spellLevel;
    private final DefaultReferenceFacade<InfoFacade> spell;
    private final DefaultReferenceFacade<String> variant;
    private final DefaultReferenceFacade<Integer> casterLevel;
    private final DefaultReferenceFacade<String> spellType;

    private final DefaultListFacade<InfoFacade> availClasses;
    private final DefaultListFacade<Integer> availSpellLevels;
    private final DefaultListFacade<InfoFacade> availSpells;
    private final DefaultListFacade<String> availVariants;
    private final DefaultListFacade<Integer> availCasterlevels;
    private final DefaultListFacade<String> availSpellTypes;

    private final DefaultListFacade<AbilityFacade> availMetamagicFeats;
    private final DefaultListFacade<AbilityFacade> selMetamagicFeats;

    private List<String> classList;
    private List<String> levelList;
    private boolean metaAllowed;
    private Boolean spellBooks;
    private int minSpellLevel = 0;
    private int maxSpellLevel = 9;
    private String reqSpellType = "";
    private final List<String> subTypeList = new ArrayList<>();

    private final PlayerCharacter character;
    private Type requiredType;
    private List<AvailableSpell> classSpells;
    private CDOMList<Spell> spellList;
    private final MasterAvailableSpellFacet masterAvailableSpellFacet;
    private final DataSetID datasetID;

    /**
     * Create a new instance SpellBuilderFacadeImpl to manage a particular
     * spell choice.
     *
     * @param choiceValue The string defining the choice. Should not include the EQBUILDER.SPELL| tag itself.
     * @param character   The character which the item will belong to.
     * @param equip       The equipment, if any, that the spell will be associated with.
     */
    public SpellBuilderFacadeImpl(String choiceValue, PlayerCharacter character, Equipment equip)
    {
        this.character = character;
        masterAvailableSpellFacet = FacetLibrary.getFacet(MasterAvailableSpellFacet.class);
        datasetID = character.getCharID().getDatasetID();

        availClasses = new DefaultListFacade<>();
        availSpellLevels = new DefaultListFacade<>();
        availSpells = new DefaultListFacade<>();
        availVariants = new DefaultListFacade<>();
        availCasterlevels = new DefaultListFacade<>();
        availSpellTypes = new DefaultListFacade<>();
        availMetamagicFeats = new DefaultListFacade<>();

        pcClass = new DefaultReferenceFacade<>();
        spellLevel = new DefaultReferenceFacade<>();
        spell = new DefaultReferenceFacade<>();
        variant = new DefaultReferenceFacade<>();
        casterLevel = new DefaultReferenceFacade<>();
        spellType = new DefaultReferenceFacade<>();
        selMetamagicFeats = new DefaultListFacade<>();

        requiredType = Type.NONE;
        if (equip != null)
        {
            Type[] knownTypes = new Type[]{Type.POTION, Type.SCROLL, Type.WAND, Type.RING};
            for (Type itemType : knownTypes)
            {
                if (equip.isType(itemType.toString()))
                {
                    requiredType = itemType;
                    break;
                }
            }
        }

        parseChoiceValue(choiceValue);

        buildLists();

        if (availClasses.getSize() > 0)
        {
            setClass(availClasses.getElementAt(0));
        }
    }

    /**
     * Parse the choice string, if one was supplied.
     *
     * @param choiceValue The value to be parsed.
     */
    private void parseChoiceValue(String choiceValue)
    {
        classList = null;
        levelList = null;
        metaAllowed = true;
        spellBooks = null;

        if (StringUtils.isNotEmpty(choiceValue))
        {
            if (!parseClassLevelSyntax(choiceValue))
            {
                parseTypeSyntax(choiceValue);
            }
        }

        // Add in any relevant restrictions from preferences on crafting
        if (requiredType == Type.POTION)
        {
            maxSpellLevel = Math.min(maxSpellLevel, SettingsHandler.maxPotionSpellLevel().get());
        } else if (requiredType == Type.WAND)
        {
            maxSpellLevel = Math.min(maxSpellLevel, SettingsHandler.maxWandSpellLevel().get());
        }
    }

    /**
     * Parse the 'hidden' syntax (CMP?) allowing class and level selection.
     * Format: CLASS=Wizard|CLASS=Sorcerer|Metamagic=0|LEVEL=1|LEVEL=2|SPELLBOOKS=Y
     *
     * @param choiceValue The value to be parsed.
     * @return true if this is a class level syntax value.
     */
    private boolean parseClassLevelSyntax(String choiceValue)
    {
        //
        // CLASS=Wizard|CLASS=Sorcerer|Metamagic=0|LEVEL=1|LEVEL=2|SPELLBOOKS=Y
        final StringTokenizer aTok = new StringTokenizer(choiceValue, "|", false);

        while (aTok.hasMoreTokens())
        {
            String aString = aTok.nextToken();

            if (aString.startsWith("CLASS="))
            {
                if (classList == null)
                {
                    classList = new ArrayList<>();
                }

                classList.add(aString.substring(6));
            } else if (aString.startsWith("LEVEL="))
            {
                if (levelList == null)
                {
                    levelList = new ArrayList<>();
                }

                levelList.add(aString.substring(6));
            } else if (aString.startsWith("SPELLBOOKS="))
            {
                switch (aString.charAt(11))
                {
                    case 'Y':
                        spellBooks = true;

                        break;

                    case 'N':
                        spellBooks = false;

                        break;

                    default:
                        spellBooks = null;

                        break;
                }
            } else if (aString.equals("METAMAGIC=N"))
            {
                metaAllowed = false;
            } else
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse the standard syntax allowing selection on types and spell levels.
     *
     * @param choiceValue The value to be parsed.
     */
    private void parseTypeSyntax(String choiceValue)
    {

        StringTokenizer aTok = new StringTokenizer(choiceValue, "|");

        if (aTok.hasMoreTokens())
        {
            reqSpellType = aTok.nextToken();

            if (reqSpellType.equalsIgnoreCase("ANY") || reqSpellType.equalsIgnoreCase("ALL"))
            {
                reqSpellType = "";
            }
        }

        while (aTok.hasMoreTokens())
        {
            String aString = aTok.nextToken();

            try
            {
                minSpellLevel = Integer.parseInt(aString);

                break;
            } catch (NumberFormatException nfe)
            {
                subTypeList.add(aString);
            }
        }

        if (aTok.hasMoreTokens())
        {
            maxSpellLevel = Integer.parseInt(aTok.nextToken());
        }

    }

    /**
     * Use the parsed rules to build up the lists that will not change,
     * such as class/domain list and metamagic feats.
     */
    private void buildLists()
    {
        List<PCClass> classes = new ArrayList<>();
        List<Domain> domains = new ArrayList<>();

        if (classList != null)
        {
            for (String classKey : classList)
            {
                PObject obj = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                        classKey);

                if (obj == null)
                {
                    obj = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Domain.class,
                            classKey);
                    if (obj != null)
                    {
                        domains.add((Domain) obj);
                    }
                } else
                {
                    classes.add((PCClass) obj);
                }
            }
        } else
        {
            for (Spell spell : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Spell.class))
            {
                if (isSpellOfSubType(spell))
                {
                    addSpellInfoToList(spell, classes, domains, reqSpellType);
                }
            }

            for (PCClass aClass : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(PCClass.class))
            {
                if (!aClass.getSpellType().equals(Constants.NONE))
                {
                    // Only adds if the class can cast
                    if (character.getSpellSupport(aClass).canCastSpells(character))
                    {
                        continue;
                    }

                    if (!("".equals(reqSpellType)) && (!reqSpellType.contains(aClass.getSpellType())))
                    {
                        continue;
                    }

                    if (!classes.contains(aClass))
                    {
                        classes.add(aClass);
                    }
                }
            }
        }

        if (spellBooks != null)
        {
            for (int i = classes.size() - 1;i >= 0;--i)
            {
                PCClass obj = classes.get(i);

                if (!spellBooks) // can't have books
                {
                    if (obj.getSafe(ObjectKey.SPELLBOOK))
                    {
                        classes.remove(i);
                    }
                } else
                // must have books
                {
                    if (!obj.getSafe(ObjectKey.SPELLBOOK))
                    {
                        classes.remove(i);
                    }
                }
            }
            if (spellBooks)
            {
                domains.clear();
            }
        }

        Globals.sortPObjectListByName(classes);
        List<InfoFacade> allObjects = new ArrayList<>(classes);
        Globals.sortPObjectListByName(domains);
        allObjects.addAll(domains);

        availClasses.setContents(allObjects);

        // Spell levels
        List<Integer> spellLevelValues = new ArrayList<>();
        if ((levelList != null) && (!levelList.isEmpty()))
        {
            for (int i = minSpellLevel;i < levelList.size();++i)
            {
                spellLevelValues.add(Integer.valueOf(levelList.get(i)));
            }
        } else
        {
            for (int i = minSpellLevel;i <= maxSpellLevel;i++)
            {
                spellLevelValues.add(i);
            }
        }
        availSpellLevels.setContents(spellLevelValues);

        // Caster levels
        updateAvailCasterLevels(1, 20);

        //Metamagic
        if (metaAllowed)
        {
            List<Ability> metamagicFeats = new ArrayList<>();
            for (Ability anAbility : Globals.getContext().getReferenceContext().getManufacturerId(AbilityCategory.FEAT)
                    .getAllObjects())
            {
                if (anAbility.isType("Metamagic"))
                {
                    metamagicFeats.add(anAbility);
                }
            }
            Globals.sortPObjectListByName(metamagicFeats);
            availMetamagicFeats.setContents(metamagicFeats);
        }
    }

    private void addSpellInfoToList(final Spell aSpell, List<PCClass> classes, List<Domain> domains, String spellType)
    {
        Set<String> unfoundItems = new HashSet<>();
        final HashMapToList<CDOMList<Spell>, Integer> levelInfo = character.getSpellLevelInfo(aSpell);

        if ((levelInfo == null) || (levelInfo.isEmpty()))
        {
            return;
        }

        for (CDOMList<Spell> spellList : levelInfo.getKeySet())
        {
            if (spellList instanceof ClassSpellList)
            {
                String key = spellList.getKeyName();

                final PCClass aClass =
                        Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, key);

                if (aClass != null)
                {
                    if (!("".equals(spellType)) && (!spellType.contains(aClass.getSpellType())))
                    {
                        continue;
                    }

                    if (!classes.contains(aClass))
                    {
                        classes.add(aClass);
                    }
                } else
                {
                    key = 'C' + key;

                    if (!unfoundItems.contains(key))
                    {
                        unfoundItems.add(key);
                        Logging.errorPrint("Class " + key.substring(1) + " not found. Was used in spell " + aSpell);
                    }
                }
            } else if (spellList instanceof DomainSpellList)
            {
                if (!("".equals(spellType)) && (!spellType.contains("Divine")))
                {
                    continue;
                }

                String key = spellList.getKeyName();

                final Domain aDomain =
                        Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Domain.class, key);

                if (aDomain != null)
                {
                    if (!domains.contains(aDomain))
                    {
                        domains.add(aDomain);
                    }
                } else
                {
                    key = 'D' + key;

                    if (!unfoundItems.contains(key))
                    {
                        unfoundItems.add(key);
                        Logging.errorPrint("Domain " + key.substring(1) + " not found. Was used in spell " + aSpell);
                    }
                }
            } else
            {
                Logging.errorPrint("Unknown spell source: " + spellList);
            }
        }
    }

    private boolean isSpellOfSubType(Spell aSpell)
    {
        if (subTypeList.isEmpty())
        {
            return true;
        }

        boolean finalIsOfType = false;

        for (String s : subTypeList)
        {
            boolean isOfType = true;
            StringTokenizer aTok = new StringTokenizer(s, ";,");

            while (aTok.hasMoreTokens())
            {
                String subType = aTok.nextToken();

                if (subType.startsWith("SCHOOL."))
                {
                    SpellSchool ss = Globals.getContext().getReferenceContext()
                            .silentlyGetConstructedCDOMObject(SpellSchool.class, subType.substring(7));
                    if (ss == null || !aSpell.containsInList(ListKey.SPELL_SCHOOL, ss))
                    {
                        isOfType = false;

                        break;
                    }
                }

                if (subType.startsWith("SUBSCHOOL."))
                {
                    if (!aSpell.containsInList(ListKey.SPELL_SUBSCHOOL, subType.substring(10)))
                    {
                        isOfType = false;

                        break;
                    }
                }

                if (subType.startsWith("DESCRIPTOR."))
                {
                    String descriptor = subType.substring(11);

                    if (!aSpell.containsInList(ListKey.SPELL_DESCRIPTOR, descriptor))
                    {
                        isOfType = false;

                        break;
                    }
                }
            }

            if (isOfType)
            {
                finalIsOfType = true;

                break;
            }
        }

        return finalIsOfType;
    }

    private void updateAvailCasterLevels(int min, int max)
    {
        List<Integer> levelsForCasting = new ArrayList<>(20);
        for (int i = min;i <= max;i++)
        {
            levelsForCasting.add(i);
        }
        availCasterlevels.setContents(levelsForCasting);
    }

    /**
     * Update lists that depend on the selected level of spell
     * e.g. the list of spells
     */
    private void processLevelChange()
    {
        int baseSpellLevel = spellLevel.get();

        // List of available spells
        List<Spell> spellsOfLevel = new ArrayList<>();
        for (AvailableSpell availSpell : classSpells)
        {
            if (availSpell.getLevel() == baseSpellLevel)
            {
                spellsOfLevel.add(availSpell.getSpell());
            }

        }
        Globals.sortPObjectListByName(spellsOfLevel);
        availSpells.setContents(spellsOfLevel);
        InfoFacade selSpell = spell.get();
        if (selSpell == null || !spellsOfLevel.contains(selSpell))
        {
            Spell newSpell = null;
            if (!spellsOfLevel.isEmpty())
            {
                newSpell = spellsOfLevel.get(0);
            }
            selectSpell(newSpell);
        }

        // Spell type
        List<String> spellTypeList = getSpellTypeList();
        availSpellTypes.setContents(spellTypeList);
        spellType.set(spellTypeList.get(0));
    }

    private void selectSpell(Spell newSpell)
    {
        spell.set(newSpell);

        // Handle variants
        List<String> variants = new ArrayList<>();
        if (newSpell != null)
        {
            variants = newSpell.getSafeListFor(ListKey.VARIANTS);
        }
        Collections.sort(variants);
        availVariants.setContents(variants);
        if (variants.isEmpty())
        {
            variant.set(null);
        } else
        {
            String currVariant = variant.get();
            if (currVariant != null && !variants.contains(currVariant))
            {
                variant.set(null);
            }
        }

        recalcCasterLevelDetails();

    }

    private void recalcCasterLevelDetails()
    {
        // Metamagic
        int levelAdjust = 0;
        for (AbilityFacade feat : selMetamagicFeats)
        {
            levelAdjust += ((Ability) feat).getSafe(IntegerKey.ADD_SPELL_LEVEL);
        }

        // Limit Caster level
        int minClassLevel = 1;
        int maxClassLevel = 20;
        PCClass aClass;
        InfoFacade castingClass = pcClass.get();
        if (castingClass instanceof PCClass)
        {
            aClass = (PCClass) castingClass;
        } else if (castingClass instanceof Domain)
        {
            // TODO We should not be hardcoding the link between cleric and domains
            aClass = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
                    "Cleric");
        } else
        {
            Logging
                    .errorPrint("Found Casting Class in recalc that was not a Class or Domain: " + castingClass.getClass());
            return;
        }

        if (aClass != null)
        {
            minClassLevel =
                    character.getSpellSupport(aClass).getMinLevelForSpellLevel(spellLevel.get() + levelAdjust, true);
            minClassLevel = Math.max(1, minClassLevel);
            if (aClass.hasMaxLevel())
            {
                maxClassLevel = aClass.getSafe(IntegerKey.LEVEL_LIMIT);
            }
        }

        updateAvailCasterLevels(minClassLevel, maxClassLevel);
        int currCasterLevel = casterLevel.get() == null ? 0 : casterLevel.get();
        if (currCasterLevel < minClassLevel)
        {
            casterLevel.set(minClassLevel);
        } else if (currCasterLevel > maxClassLevel)
        {
            casterLevel.set(maxClassLevel);
        }
    }

    private List<String> getSpellTypeList()
    {
        List<String> spellTypes = new ArrayList<>();

        InfoFacade castingClass = pcClass.get();
        if (castingClass instanceof PCClass)
        {
            spellTypes.add(((PCClass) castingClass).getSpellType());
        } else if (castingClass instanceof Domain)
        {
            spellTypes.add("Divine");
        } else
        {
            Logging.errorPrint("Found Casting Class that was not a Class or Domain: " + castingClass.getClass());
        }

        return spellTypes;
    }

    private boolean canCreateItem(Spell aSpell)
    {
        if (requiredType == Type.NONE)
        {
            return true;
        }
        return aSpell.isAllowed(requiredType);
    }

    @Override
    public void setClass(InfoFacade classFacade)
    {
        pcClass.set(classFacade);

        if (classFacade instanceof Domain)
        {
            spellList = ((Domain) classFacade).get(ObjectKey.DOMAIN_SPELLLIST);
        } else
        {
            spellList = ((PCClass) classFacade).get(ObjectKey.CLASS_SPELLLIST);
        }

        classSpells = new ArrayList<>();
        for (AvailableSpell availSpell : masterAvailableSpellFacet.getAllSpellsInList(spellList, datasetID))
        {
            if (canCreateItem(availSpell.getSpell()))
            {
                classSpells.add(availSpell);
            }
        }

        if (spellLevel.get() == null)
        {
            spellLevel.set(availSpellLevels.getElementAt(0));
        }
        processLevelChange();
    }

    @Override
    public ReferenceFacade<InfoFacade> getClassRef()
    {
        return pcClass;
    }

    @Override
    public ListFacade<InfoFacade> getClasses()
    {
        return availClasses;
    }

    @Override
    public void setSpellLevel(Integer newSpellLevel)
    {
        spellLevel.set(newSpellLevel);
        processLevelChange();
    }

    @Override
    public ListFacade<Integer> getLevels()
    {
        return availSpellLevels;
    }

    @Override
    public ReferenceFacade<Integer> getSpellLevelRef()
    {
        return spellLevel;
    }

    @Override
    public void setSpell(InfoFacade spellFacade)
    {
        if (spellFacade instanceof Spell)
        {
            selectSpell((Spell) spellFacade);
        }
    }

    @Override
    public ReferenceFacade<InfoFacade> getSpellRef()
    {
        return spell;
    }

    @Override
    public ListFacade<InfoFacade> getSpells()
    {
        return availSpells;
    }

    @Override
    public void setVariant(String newVariant)
    {
        variant.set(newVariant);
    }

    @Override
    public ReferenceFacade<String> getVariantRef()
    {
        return variant;
    }

    @Override
    public ListFacade<String> getVariants()
    {
        return availVariants;
    }

    @Override
    public void setCasterLevel(Integer newCasterLevel)
    {
        casterLevel.set(newCasterLevel);
    }

    @Override
    public ReferenceFacade<Integer> getCasterLevelRef()
    {
        return casterLevel;
    }

    @Override
    public ListFacade<Integer> getCasterLevels()
    {
        return availCasterlevels;
    }

    @Override
    public void setSpellType(String newSpellType)
    {
        spellType.set(newSpellType);
    }

    @Override
    public ReferenceFacade<String> getSpellTypeRef()
    {
        return spellType;
    }

    @Override
    public ListFacade<String> getSpellTypes()
    {
        return availSpellTypes;
    }

    @Override
    public void setSelectedMetamagicFeats(Object[] newFeats)
    {
        List<AbilityFacade> chosenFeats = new ArrayList<>();
        for (Object choice : newFeats)
        {
            if (choice instanceof AbilityFacade)
            {
                chosenFeats.add((AbilityFacade) choice);
            }
        }
        selMetamagicFeats.setContents(chosenFeats);
        recalcCasterLevelDetails();
    }

    @Override
    public ListFacade<AbilityFacade> getSelectedMetamagicFeats()
    {
        return selMetamagicFeats;
    }

    @Override
    public ListFacade<AbilityFacade> getAvailMetamagicFeats()
    {
        return availMetamagicFeats;
    }

}
