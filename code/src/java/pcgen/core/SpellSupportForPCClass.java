/*
 * SpellSupportForPCClass
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.BonusSpellInfo;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.analysis.SpellCountCalc;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

public class SpellSupportForPCClass
{
    /*
     * ALLCLASSLEVELS castForLevelMap is part of PCClassLevel - or nothing at
     * all since this seems to be a form of cache? - DELETEVARIABLE
     */
    private HashMap<Integer, Integer> castForLevelMap = null;

    private SpellProgressionCache spellCache = null;

    private boolean spellCacheValid = false;

    private final PCClass source;

    public SpellSupportForPCClass(PCClass cis)
    {
        source = cis;
    }

    /**
     * Get the highest level of spell that this class can cast.
     *
     * @return the highest level of spells that this class can cast, or -1 if
     * this class can not cast spells
     */
    /*
     * PCCLASSLEVELONLY This calculation is dependent upon the class level and
     * is therefore appropriate only for PCClassLevel
     */
    public int getMaxCastLevel()
    {
        int currHighest = -1;
        if (castForLevelMap != null)
        {
            for (int key : castForLevelMap.keySet())
            {
                final Integer value = castForLevelMap.get(key);
                if (value != null)
                {
                    if (value > 0 && key > currHighest)
                    {
                        currHighest = key;
                    }
                }
            }
        }
        return currHighest;
    }

    /**
     * Get the highest level of spell that this class can cast.
     *
     * @param aPC The character to build the casting information for.
     * @return the highest level of spells that this class can cast, or -1 if
     * this class can not cast spells
     */
    public int getMaxCastLevel(PlayerCharacter aPC)
    {
        if (castForLevelMap == null)
        {
            calcCastPerDayMapForLevel(aPC);
        }
        return getMaxCastLevel();
    }

    public List<Formula> getCastListForLevel(int aLevel)
    {
        if (!updateSpellCache(false))
        {
            return null;
        }
        return spellCache.getCastForLevel(aLevel);
    }

    public boolean hasCastList()
    {
        return updateSpellCache(false) && spellCache.hasCastProgression();
    }

    public int getHighestLevelSpell()
    {
        if (!updateSpellCache(false))
        {
            return -1;
        }
        return Math.max(spellCache.getHighestCastSpellLevel(), spellCache.getHighestKnownSpellLevel());
    }

    /**
     * Identify if the character can cast spells for this class. This will take
     * into account the class casting progression as well as the character's
     * bonuses.
     *
     * @param aPC The character to be checked.
     * @return true if the character can cast spells for this class, false if not.
     */
    public boolean canCastSpells(PlayerCharacter aPC)
    {
        if (!updateSpellCache(false) || !spellCache.hasCastProgression())
        {
            return false;
        }

        for (int i = 0;i < 100;i++)
        {
            final int numSpellsCastable = getCastForLevel(i, aPC);
            if (numSpellsCastable > 0)
            {
                return true;
            }
        }

        // No casting ability found
        return false;

    }

    public int getKnownForLevel(int spellLevel, PlayerCharacter aPC)
    {
        int total = 0;
        int stat = 0;
        final String classKeyName = "CLASS." + source.getKeyName();
        final String levelSpellLevel = ";LEVEL." + spellLevel;
        final String allSpellLevel = ";LEVEL.All";

        int pcLevel = aPC.getLevel(source);
        pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", source.getKeyName());
        pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + source.getSpellType());

        /*
         * CONSIDER Why is known testing getNumFromCastList??? - thpr 11/8/06
         */
        if (updateSpellCache(false) && spellCache.hasCastProgression()
                && (getNumFromCastList(pcLevel, spellLevel, aPC) < 0))
        {
            // Don't know any spells of this level
            // however, character might have a bonus spells e.g. from certain
            // feats
            return (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + levelSpellLevel);
        }

        total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + levelSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE." + source.getSpellType() + levelSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any" + levelSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + allSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE." + source.getSpellType() + allSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any" + allSpellLevel);

        PCStat aStat = source.baseSpellStat();
        String statString = Constants.NONE;

        if (aStat != null)
        {
            stat = aPC.getTotalStatFor(aStat);
            statString = aStat.getKeyName();
        }
        final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "KNOWN." + statString)
                + (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT")
                + (int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT;CLASS=" + source.getKeyName());
        if (!source.getSafe(ObjectKey.USE_SPELL_SPELL_STAT) && !source.getSafe(ObjectKey.CASTER_WITHOUT_SPELL_STAT))
        {
            final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + statString, "").intValue();

            if ((maxSpellLevel + bonusStat) < spellLevel)
            {
                return total;
            }
        }

        stat += bonusStat;

        int mult = (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", classKeyName + levelSpellLevel);
        mult += (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", "TYPE." + source.getSpellType() + levelSpellLevel);

        if (mult < 1)
        {
            mult = 1;
        }

        if (!updateSpellCache(false))
        {
            return total;
        }

        if (spellCache.hasKnownProgression())
        {
            List<Formula> knownList = spellCache.getKnownForLevel(pcLevel);
            if (spellLevel >= 0 && knownList != null && spellLevel < knownList.size())
            {
                total += mult * knownList.get(spellLevel).resolve(aPC, "").intValue();

                // add Stat based bonus
                BonusSpellInfo bsi = Globals.getContext().getReferenceContext()
                        .silentlyGetConstructedCDOMObject(BonusSpellInfo.class, String.valueOf(spellLevel));

                if (Globals.checkRule(RuleConstants.BONUSSPELLKNOWN) && (bsi != null) && bsi.isValid())
                {
                    int base = bsi.getStatScore();
                    if (stat >= base)
                    {
                        int range = bsi.getStatRange();
                        total += Math.max(0, (stat - base + range) / range);
                    }
                }
            }
        }

        // if we have known spells (0==no known spells recorded)
        // or a psi specialty.
        if (total > 0 && spellLevel > 0)
        {
            // make sure any slots due from specialties
            total += source.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY);
            // (including domains) are added
            Integer assoc = aPC.getDomainSpellCount(source);
            if (assoc != null)
            {
                total += assoc;
            }
        }

        // Add in any from SPELLKNOWN
        total += aPC.getKnownSpellCountForLevel(source.get(ObjectKey.CLASS_SPELLLIST), spellLevel);

        return total;
    }

    public int getMinLevelForSpellLevel(int spellLevel, boolean allowBonus)
    {
        if (!updateSpellCache(false))
        {
            return -1;
        }
        return spellCache.getMinLevelForSpellLevel(spellLevel, allowBonus);
    }

    public int getMaxSpellLevelForClassLevel(int classLevel)
    {
        if (!updateSpellCache(false))
        {
            return -1;
        }
        return spellCache.getMaxSpellLevelForClassLevel(classLevel);
    }

    public boolean hasKnownList()
    {
        return updateSpellCache(false) && spellCache.hasKnownProgression();
    }

    public int getSpecialtyKnownForLevel(int spellLevel, PlayerCharacter aPC)
    {
        int total;
        total = (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN",
                "CLASS." + source.getKeyName() + ";LEVEL." + spellLevel);
        total += (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN",
                "TYPE." + source.getSpellType() + ";LEVEL." + spellLevel);

        int pcLevel = aPC.getLevel(source);
        pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", source.getKeyName());
        pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + source.getSpellType());

        PCStat aStat = source.baseSpellStat();

        if (aStat != null)
        {
            final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + aStat.getKeyName(), "").intValue();

            if (spellLevel > maxSpellLevel)
            {
                return total;
            }
        }

        if (updateSpellCache(false))
        {
            List<Formula> specKnown = spellCache.getSpecialtyKnownForLevel(pcLevel);
            if (specKnown != null && specKnown.size() > spellLevel)
            {
                total += specKnown.get(spellLevel).resolve(aPC, "").intValue();
            }
        }

        // make sure any slots due from specialties
        total += source.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY);
        // (including domains) are added
        Integer assoc = aPC.getDomainSpellCount(source);
        if (assoc != null)
        {
            total += assoc;
        }

        return total;
    }

    public boolean updateSpellCache(boolean force)
    {
        if (force || !spellCacheValid)
        {
            SpellProgressionCache cache = new SpellProgressionCache();
            for (PCClassLevel cl : source.getOriginalClassLevelCollection())
            {
                Integer lvl = cl.get(IntegerKey.LEVEL);
                List<Formula> cast = cl.getListFor(ListKey.CAST);
                if (cast != null)
                {
                    cache.setCast(lvl, cast);
                }
                List<Formula> known = cl.getListFor(ListKey.KNOWN);
                if (known != null)
                {
                    cache.setKnown(lvl, known);
                }
                List<Formula> spec = cl.getListFor(ListKey.SPECIALTYKNOWN);
                if (spec != null)
                {
                    cache.setSpecialtyKnown(lvl, spec);
                }
            }
            if (!cache.isEmpty())
            {
                spellCache = cache;
            }
            spellCacheValid = true;
        }
        return spellCache != null;
    }

    /**
     * Build a caster level map for this class. The map will be of the form
     * <Integer,Integer> where the key is the spell level and the value is the
     * number of times per day that spell level can be cast by the character
     *
     * @param aPC
     */
    /*
     * PCCLASSLEVELONLY This calculation is dependent upon the class level and
     * is therefore appropriate only for PCClassLevel
     */
    void calcCastPerDayMapForLevel(final PlayerCharacter aPC)
    {
        //
        // TODO: Shouldn't we be using Globals.getLevelInfo().size() instead of
        // 100?
        // Byngl -- November 25, 2002
        //
        if (castForLevelMap == null)
        {
            castForLevelMap = new HashMap<>(100);
        }
        for (int i = 0;i < 100;i++)
        {
            final int s = getCastForLevel(i, aPC);
            castForLevelMap.put(i, s);
        }
    }

    public boolean isAutoKnownSpell(Spell aSpell, int spellLevel, boolean useMap, PlayerCharacter aPC)
    {
        List<KnownSpellIdentifier> knownSpellsList = source.getListFor(ListKey.KNOWN_SPELLS);
        if (knownSpellsList == null)
        {
            return false;
        }

        if (useMap)
        {
            final Integer val = castForLevelMap.get(spellLevel);

            if ((val == null) || val == 0 || (aSpell == null))
            {
                return false;
            }
        } else if ((getCastForLevel(spellLevel, aPC) == 0) || (aSpell == null))
        {
            return false;
        }

        if (SpellCountCalc.isProhibited(aSpell, source, aPC) && !SpellCountCalc.isSpecialtySpell(aPC, source, aSpell))
        {
            return false;
        }

        // iterate through the KNOWNSPELLS: tag
        for (KnownSpellIdentifier filter : knownSpellsList)
        {
            if (filter.matchesFilter(aSpell, spellLevel))
            {
                return true;
            }
        }
        return false;
    }

    public int getNumFromCastList(int iCasterLevel, int iSpellLevel, PlayerCharacter aPC)
    {
        if (iCasterLevel == 0)
        {
            // can't cast spells!
            return -1;
        }

        List<Formula> castListForLevel = getCastListForLevel(iCasterLevel);
        if (castListForLevel == null || iSpellLevel >= castListForLevel.size())
        {
            return -1;
        }
        return castListForLevel.get(iSpellLevel).resolve(aPC, "").intValue();
    }

    public int getCastForLevel(int spellLevel, PlayerCharacter aPC)
    {
        return getCastForLevel(spellLevel, Globals.getDefaultSpellBook(), true, true, aPC);
    }

    public int getCastForLevel(int spellLevel, String bookName, boolean includeAdj, boolean limitByStat,
            PlayerCharacter aPC)
    {
        int pcLevel = aPC.getLevel(source);
        int total = 0;
        int stat = 0;
        final String classKeyName = "CLASS." + source.getKeyName();
        final String levelSpellLevel = ";LEVEL." + spellLevel;
        final String allSpellLevel = ";LEVEL.All";
        pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", source.getKeyName());
        pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + source.getSpellType());
        PCStat aStat = source.bonusSpellStat();
        String statString = Constants.NONE;
        if (aStat != null)
        {
            stat = aPC.getTotalStatFor(aStat);
            statString = aStat.getKeyName();
        }
        final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "CAST." + statString)
                + (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT")
                + (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS=" + source.getKeyName());
        if (getNumFromCastList(pcLevel, spellLevel, aPC) < 0)
        {
            // can't cast spells of this level
            // however, character might have a bonus spell slot e.g. from
            // certain feats
            total = (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel);
            if (total > 0)
            {
                // Bonus slots benefits from high spellcasting stat as well (see http://www.d20srd.org/srd/epic/feats.htm#improvedSpellCapacity)
                BonusSpellInfo bsi = Globals.getContext().getReferenceContext()
                        .silentlyGetConstructedCDOMObject(BonusSpellInfo.class, String.valueOf(spellLevel));
                if ((bsi != null) && bsi.isValid())
                {
                    int base = bsi.getStatScore();
                    stat += bonusStat;
                    if (stat >= base)
                    {
                        int range = bsi.getStatRange();
                        total += Math.max(0, (stat - base + range) / range);
                    }
                }
            }
            return total;
        }
        total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + source.getSpellType() + levelSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any" + levelSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + allSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + source.getSpellType() + allSpellLevel);
        total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any" + allSpellLevel);
        if (limitByStat)
        {
            PCStat ss = source.baseSpellStat();
            if (ss != null)
            {
                final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + ss.getKeyName(), "").intValue();
                if ((maxSpellLevel + bonusStat) < spellLevel)
                {
                    return total;
                }
            }
        }
        stat += bonusStat;
        // Now we decide whether to adjust the number of slots down
        // the road by adding specialty slots.
        // Reworked to consider the fact that a lower-level
        // specialty spell can go into this level of specialty slot
        //
        int adj = 0;
        if (includeAdj && !bookName.equals(Globals.getDefaultSpellBook())
                && (aPC.hasAssocs(source, AssociationKey.SPECIALTY) || aPC.hasDomains()))
        {
            // We need to do this for EVERY spell level up to the
            // one really under consideration, because if there
            // are any specialty spells available BELOW this level,
            // we might wind up using THIS level's slots for them.
            for (int ix = 0;ix <= spellLevel;++ix)
            {
                Collection<CharacterSpell> aList = aPC.getCharacterSpells(source, ix);
                Collection<Spell> bList = new ArrayList<>();
                if (!aList.isEmpty())
                {
                    // Assume no null check on castInfo requried, because
                    // getNumFromCastList above would have returned -1
                    if ((ix > 0) && "DIVINE".equalsIgnoreCase(source.getSpellType()))
                    {
                        for (Domain d : aPC.getDomainSet())
                        {
                            if (source.getKeyName().equals(aPC.getDomainSource(d).getPcclass().getKeyName()))
                            {
                                bList = aPC.getSpellsIn(d.get(ObjectKey.DOMAIN_SPELLLIST), ix);
                            }
                        }
                    }
                    for (CharacterSpell cs : aList)
                    {
                        int x = -1;
                        if (!bList.isEmpty())
                        {
                            if (bList.contains(cs.getSpell()))
                            {
                                x = 0;
                            }
                        } else
                        {
                            x = cs.getInfoIndexFor(aPC, Constants.EMPTY_STRING, ix, 1);
                        }
                        if (x > -1)
                        {
                            PCClass target = source;
                            String subClassKey = aPC.getSubClassName(source);
                            if (subClassKey != null && (!subClassKey.isEmpty()) && !subClassKey.equals(Constants.NONE))
                            {
                                target = source.getSubClassKeyed(subClassKey);
                            }
                            adj = aPC.getSpellSupport(target).getSpecialtyKnownForLevel(spellLevel, aPC);
                            break;
                        }
                    }
                }
                // end of what to do if aList is not empty
                if (adj > 0)
                {
                    break;
                }
            }
            // end of looping up to this level looking for specialty spells that
            // can be cast
        }
        // end of deciding whether there are specialty slots to distribute
        int mult = (int) aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName + levelSpellLevel);
        mult += (int) aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE." + source.getSpellType() + levelSpellLevel);

        if (mult < 1)
        {
            mult = 1;
        }
        final int t = getNumFromCastList(pcLevel, spellLevel, aPC);
        total += ((t * mult) + adj);
        BonusSpellInfo bsi = Globals.getContext().getReferenceContext()
                .silentlyGetConstructedCDOMObject(BonusSpellInfo.class, String.valueOf(spellLevel));
        if ((bsi != null) && bsi.isValid())
        {
            int base = bsi.getStatScore();
            if (stat >= base)
            {
                int range = bsi.getStatRange();
                total += Math.max(0, (stat - base + range) / range);
            }
        }
        return total;
    }

    public int getHighestLevelSpell(PlayerCharacter pc)
    {
        final String classKeyName = "CLASS." + source.getKeyName();
        int mapHigh = getHighestLevelSpell();
        int high = mapHigh;
        for (int i = mapHigh;i < mapHigh + 30;i++)
        {
            final String levelSpellLevel = ";LEVEL." + i;
            if (pc.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel) > 0)
            {
                high = i;
            } else if (pc.getTotalBonusTo("SPELLKNOWN", classKeyName + levelSpellLevel) > 0)
            {
                high = i;
            }
        }
        return high;
    }

    public String getBonusCastForLevelString(int spellLevel, String bookName, PlayerCharacter aPC)
    {
        if (getCastForLevel(spellLevel, bookName, true, true, aPC) > 0)
        {
            // if this class has a specialty, return +1
            if (aPC.hasAssocs(source, AssociationKey.SPECIALTY))
            {
                PCClass target = source;
                String subClassKey = aPC.getSubClassName(source);
                if (subClassKey != null && (!subClassKey.isEmpty()) && !subClassKey.equals(Constants.NONE))
                {
                    target = source.getSubClassKeyed(subClassKey);
                }

                return "+" + aPC.getSpellSupport(target).getSpecialtyKnownForLevel(spellLevel, aPC);
            }

            if (!aPC.hasDomains())
            {
                return "";
            }

            // if the spelllevel is >0 and this class has a characterdomain
            // associated with it, return +1
            if ((spellLevel > 0) && "DIVINE".equalsIgnoreCase(source.getSpellType()))
            {
                for (Domain d : aPC.getDomainSet())
                {
                    if (source.getKeyName().equals(aPC.getDomainSource(d).getPcclass().getKeyName()))
                    {
                        return "+1";
                    }
                }
            }
        }

        return "";
    }

    public boolean hasKnownSpells(PlayerCharacter aPC)
    {
        for (int i = 0;i <= getHighestLevelSpell();i++)
        {
            if (getKnownForLevel(i, aPC) > 0)
            {
                return true;
            }
        }

        return false;
    }

}
