/*
 * Copyright (c) Thomas Parker, 2010.
 * Copyright 2002 (C) James Dempsey
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
package pcgen.core.system;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.SizeAdjustment;
import pcgen.util.Logging;

/**
 * {@code LoadInfo} describes the data associated with a loads and
 * encumbrance
 */
public class LoadInfo implements Loadable
{
    private URI sourceURI;
    private String loadInfoName;

    private final Map<CDOMSingleRef<SizeAdjustment>, BigDecimal> rawSizeMultiplierMap = new HashMap<>();
    private final Map<SizeAdjustment, BigDecimal> sizeMultiplierMap = new HashMap<>();

    private final SortedMap<Integer, BigDecimal> strengthLoadMap = new TreeMap<>();
    private int minStrenghScoreWithLoad = 0;
    private int maxStrengthScoreWithLoad = 0;

    private BigDecimal loadScoreMultiplier = BigDecimal.ZERO;
    private int loadMultStep = 10;

    private final Map<String, LoadInfo.LoadMapEntry> loadMultiplierMap = new HashMap<>();
    private String modifyFormula;

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    /**
     * @param multiplier the load score multiplier to set
     */
    public void setLoadScoreMultiplier(BigDecimal multiplier)
    {
        loadScoreMultiplier = multiplier;
    }

    public BigDecimal getLoadScoreMultiplier()
    {
        return loadScoreMultiplier;
    }

    /**
     * Add a load score/value pair
     *
     * @param score
     * @param load
     */
    public void addLoadScoreValue(int score, BigDecimal load)
    {
        strengthLoadMap.put(score, load);
        if (score > maxStrengthScoreWithLoad)
        {
            maxStrengthScoreWithLoad = score;
        }
        if (score < minStrenghScoreWithLoad)
        {
            minStrenghScoreWithLoad = score;
        }
    }

    /**
     * Get the value for a load score
     *
     * @param score
     * @return the value for a load score
     */
    public BigDecimal getLoadScoreValue(int score)
    {
        if (score < minStrenghScoreWithLoad)
        {
            return BigDecimal.ZERO;
        } else if (score > maxStrengthScoreWithLoad)
        {
            if (getLoadMultiplierCount() == 1)
            {
                // TODO Isn't this a bug??
                return getLoadScoreValue(minStrenghScoreWithLoad);
            }
            return loadScoreMultiplier.multiply(getLoadScoreValue(score - loadMultStep));
        } else
        {
            BigDecimal loadScore = strengthLoadMap.get(score);
            if (loadScore == null)
            {
                SortedMap<Integer, BigDecimal> headMap = strengthLoadMap.headMap(score);
                /*
                 * Assume headMap is populated, since minScore is tested, above -
                 * thpr Mar 14, 2007
                 */
                return strengthLoadMap.get(headMap.lastKey());
            }
            return loadScore;
        }
    }

    /**
     * Add a size adjustment
     *
     * @param size
     * @param multiplier
     */
    public void addSizeAdjustment(CDOMSingleRef<SizeAdjustment> size, BigDecimal multiplier)
    {
        rawSizeMultiplierMap.put(size, multiplier);
    }

    public void resolveSizeAdjustmentMap()
    {
        for (Map.Entry<CDOMSingleRef<SizeAdjustment>, BigDecimal> me : rawSizeMultiplierMap.entrySet())
        {
            sizeMultiplierMap.put(me.getKey().get(), me.getValue());
        }
    }

    /**
     * Get the size adjustment
     *
     * @param size
     * @return the size adjustment
     */
    public BigDecimal getSizeAdjustment(SizeAdjustment size)
    {
        if (sizeMultiplierMap.containsKey(size))
        {
            return sizeMultiplierMap.get(size);
        }
        if (Logging.isDebugMode())
        {
            Logging.debugPrint("Unable to find Load Multiplier for Size: " + size.getKeyName());
        }
        return BigDecimal.ONE;
    }

    /**
     * Add load multiplier
     *
     * @param encumbranceType
     * @param value
     * @param formula
     * @param checkPenalty
     */
    public void addLoadMultiplier(String encumbranceType, Float value, String formula, Integer checkPenalty)
    {
        LoadMapEntry newEntry = new LoadMapEntry(value, formula, checkPenalty);
        loadMultiplierMap.put(encumbranceType, newEntry);
    }

    /**
     * Get the load multiplier
     *
     * @param encumbranceType
     * @return load multiplier
     */
    public Float getLoadMultiplier(String encumbranceType)
    {
        if (loadMultiplierMap.containsKey(encumbranceType))
        {
            return loadMultiplierMap.get(encumbranceType).getMuliplier();
        }
        return null;
    }

    /**
     * Get the load move formula
     *
     * @param encumbranceType
     * @return the load move formula
     */
    public String getLoadMoveFormula(String encumbranceType)
    {
        if (loadMultiplierMap.containsKey(encumbranceType))
        {
            return loadMultiplierMap.get(encumbranceType).getFormula();
        }
        return "";
    }

    /**
     * Get the load check penalty
     *
     * @param encumbranceType
     * @return the load check penalty
     */
    public int getLoadCheckPenalty(String encumbranceType)
    {
        if (loadMultiplierMap.containsKey(encumbranceType))
        {
            return loadMultiplierMap.get(encumbranceType).getCheckPenalty();
        }
        return 0;
    }

    /**
     * Set the load modifier formula
     *
     * @param argFormula
     */
    public void setLoadModifierFormula(final String argFormula)
    {
        modifyFormula = argFormula;
    }

    /**
     * Get the load modifier formula
     *
     * @return the load modifier formula
     */
    public String getLoadModifierFormula()
    {
        return modifyFormula;
    }

    /**
     * Get the load multiplier count
     *
     * @return the load multiplier count
     */
    public int getLoadMultiplierCount()
    {
        return loadMultiplierMap.size();
    }

    private static class LoadMapEntry
    {
        private final Float multiplier;
        private final String moveFormula;
        private final Integer checkPenalty;

        /**
         * Constructor
         *
         * @param argMultiplier
         * @param argFormula
         * @param argPenalty
         */
        public LoadMapEntry(Float argMultiplier, String argFormula, Integer argPenalty)
        {
            multiplier = argMultiplier;
            moveFormula = argFormula;
            checkPenalty = argPenalty;
        }

        /**
         * Get multiplier
         *
         * @return multiplier
         */
        public Float getMuliplier()
        {
            return multiplier;
        }

        /**
         * Get the formula
         *
         * @return formula
         */
        public String getFormula()
        {
            return moveFormula;
        }

        /**
         * Get the check penalty
         *
         * @return the check penalty
         */
        public int getCheckPenalty()
        {
            return checkPenalty;
        }
    }

    public void setLoadMultStep(int step)
    {
        loadMultStep = step;
    }

    @Override
    public String getDisplayName()
    {
        return loadInfoName;
    }

    @Override
    public String getKeyName()
    {
        return getDisplayName();
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public void setName(String name)
    {
        loadInfoName = name;
    }

}
