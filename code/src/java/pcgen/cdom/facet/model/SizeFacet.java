/*
 * Copyright (c) Thomas Parker, 2009.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.model;

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.BonusChangeFacet.BonusChangeEvent;
import pcgen.cdom.facet.BonusChangeFacet.BonusChangeListener;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.analysis.LevelFacet.LevelChangeEvent;
import pcgen.cdom.facet.analysis.LevelFacet.LevelChangeListener;
import pcgen.cdom.facet.analysis.ResultFacet;
import pcgen.cdom.facet.base.AbstractDataFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.analysis.SizeUtilities;
import pcgen.output.publish.OutputDB;

/**
 * SizeFacet tracks the SizeAdjustment for a Player Character.
 */
public class SizeFacet extends AbstractDataFacet<CharID, SizeAdjustment>
        implements DataFacetChangeListener<CharID, CDOMObject>, LevelChangeListener, BonusChangeListener,
        ItemFacet<CharID, SizeAdjustment>
{
    private static final Class<SizeAdjustment> SIZEADJUSTMENT_CLASS = SizeAdjustment.class;

    private TemplateFacet templateFacet;
    private RaceFacet raceFacet;
    private FormulaResolvingFacet formulaResolvingFacet;
    private BonusCheckingFacet bonusCheckingFacet;
    private LevelFacet levelFacet;
    private ResultFacet resultFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    private LoadContextFacet loadContextFacet = FacetLibrary.getFacet(LoadContextFacet.class);

    /**
     * Returns the integer indicating the racial size for the Player Character
     * identified by the given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           racial size will be returned
     * @return the integer indicating the racial size for the Player Character
     * identified by the given CharID
     */
    public int racialSizeInt(CharID id)
    {
        String baseSizeControl = ControlUtilities.getControlToken(
                loadContextFacet.get(id.getDatasetID()).get(), CControl.BASESIZE);
        if (baseSizeControl != null)
        {
            SizeAdjustment baseSize = (SizeAdjustment) resultFacet
                    .getGlobalVariable(id, baseSizeControl);
            return baseSize.get(IntegerKey.SIZEORDER);
        } else
        {
            SizeFacetInfo info = getInfo(id);
            if (info == null)
            {
                return SizeUtilities.getDefaultSizeAdjustment().get(IntegerKey.SIZEORDER);
            }
            return info.racialSizeInt;
        }
    }

    private int calcRacialSizeInt(CharID id)
    {
        String baseSizeControl = ControlUtilities.getControlToken(
                loadContextFacet.get(id.getDatasetID()).get(), CControl.BASESIZE);
        if (baseSizeControl != null)
        {
            SizeAdjustment baseSize = (SizeAdjustment) resultFacet
                    .getGlobalVariable(id, baseSizeControl);
            return baseSize.get(IntegerKey.SIZEORDER);
        } else
        {
            SizeFacetInfo info = getConstructingInfo(id);

            int iSize = SizeUtilities.getDefaultSizeAdjustment().get(IntegerKey.SIZEORDER);
            Race race = raceFacet.get(id);
            if (race != null)
            {
                // get the base size for the race
                Formula size = race.getSafe(FormulaKey.SIZE);
                iSize = formulaResolvingFacet.resolve(id, size, "").intValue();

                // now check and see if a template has set the
                // size of the character in question
                // with something like SIZE:L
                for (PCTemplate template : templateFacet.getSet(id))
                {
                    Formula sizeFormula = template.get(FormulaKey.SIZE);
                    if (sizeFormula != null)
                    {
                        iSize = formulaResolvingFacet
                                .resolve(id, sizeFormula, template.getKeyName())
                                .intValue();
                    }
                }
            }
            info.racialSizeInt = iSize;
            return iSize;
        }
    }

    /**
     * Forces a complete update of the size information for the Player Character
     * identified by the given CharID.
     *
     * @param id The CharID indicating the Player Character on which to update
     *           the size information
     */
    public void update(CharID id)
    {
        SizeFacetInfo info = getConstructingInfo(id);
        int iSize = calcRacialSizeInt(id);

        Race race = raceFacet.get(id);
        if (race != null)
        {
            // Now check and see if a class has modified
            // the size of the character with something like:
            // BONUS:SIZEMOD|NUMBER|+1
            iSize += (int) bonusCheckingFacet.getBonus(id, "SIZEMOD", "NUMBER");

            // Now see if there is a HD advancement in size
            // (Such as for Dragons)
            iSize += sizesToAdvance(id, race);

            //
            // Must still be a valid size
            //
            int maxIndex =
                    Globals.getContext().getReferenceContext().getConstructedObjectCount(SIZEADJUSTMENT_CLASS) - 1;
            iSize = Math.min(maxIndex, Math.max(0, iSize));
        }

        SizeAdjustment oldSize = info.sizeAdj;
        SizeAdjustment newSize = Globals.getContext().getReferenceContext()
                .getSortedList(SizeAdjustment.class, IntegerKey.SIZEORDER).get(iSize);
        info.sizeAdj = newSize;
        if (oldSize != newSize)
        {
            if (oldSize != null)
            {
                fireDataFacetChangeEvent(id, oldSize, DataFacetChangeEvent.DATA_REMOVED);
            }
            fireDataFacetChangeEvent(id, newSize, DataFacetChangeEvent.DATA_ADDED);
        }
    }

    private int sizesToAdvance(CharID id, Race race)
    {
        return sizesToAdvance(race, levelFacet.getMonsterLevelCount(id));
    }

    int sizesToAdvance(Race race, int monsterLevelCount)
    {
        List<Integer> hda = race.getListFor(ListKey.HITDICE_ADVANCEMENT);
        int steps = 0;
        if (hda != null)
        {
            int limit = race.maxHitDiceAdvancement();
            for (Integer hitDie : hda)
            {
                if (monsterLevelCount <= hitDie)
                {
                    break;
                }
                if (hitDie < limit)
                {
                    steps++;
                }
            }
        }
        return steps;
    }

    /**
     * Returns the SizeAdjustment active for the Player Character identified by
     * the given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           SizeAdjustment will be returned
     * @return The SizeAdjustment active for the Player Character identified by
     * the given CharID
     */
    @Override
    public SizeAdjustment get(CharID id)
    {
        SizeFacetInfo info = getInfo(id);
        return info == null ? SizeUtilities.getDefaultSizeAdjustment() : info.sizeAdj;
    }

    /**
     * Returns the abbreviation of the SizeAdjustment active for the Player
     * Character identified by the given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           abbreviation of the SizeAdjustment will be returned
     * @return The abbreviation of the SizeAdjustment active for the Player
     * Character identified by the given CharID
     */
    public String getSizeAbb(CharID id)
    {
        return get(id).getKeyName();
    }

    /**
     * Returns the type-safe SizeFacetInfo for this SizeFacet and the given
     * CharID. Will return a new, empty SizeFacetInfo if no Size information has
     * been set for the given CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The SizeFacetInfo object is
     * owned by SizeFacet, and since it can be modified, a reference to that
     * object should not be exposed to any object other than SizeFacet.
     *
     * @param id The CharID for which the SizeFacetInfo should be returned
     * @return The SizeFacetInfo for the Player Character represented by the
     * given CharID.
     */
    private SizeFacetInfo getConstructingInfo(CharID id)
    {
        SizeFacetInfo rci = getInfo(id);
        if (rci == null)
        {
            rci = new SizeFacetInfo();
            setCache(id, rci);
        }
        return rci;
    }

    /**
     * Returns the type-safe SizeFacetInfo for this SizeFacet and the given
     * CharID. Will return a null if no Size information has been set for the
     * given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The SizeFacetInfo object is
     * owned by SizeFacet, and since it can be modified, a reference to that
     * object should not be exposed to any object other than SizeFacet.
     *
     * @param id The CharID for which the SizeFacetInfo should be returned
     * @return The SizeFacetInfo for the Player Character represented by the
     * given CharID.
     */
    private SizeFacetInfo getInfo(CharID id)
    {
        return (SizeFacetInfo) getCache(id);
    }

    /**
     * SizeFacetInfo is the data structure used by SizeFacet to store a Player
     * Character's size information.
     */
    private static class SizeFacetInfo
    {
        private int racialSizeInt;
        private SizeAdjustment sizeAdj;

        @Override
        public int hashCode()
        {
            return sizeAdj.hashCode() ^ racialSizeInt * 29;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (o instanceof SizeFacetInfo)
            {
                SizeFacetInfo sfi = (SizeFacetInfo) o;
                return (racialSizeInt == sfi.racialSizeInt) && sizeAdj.equals(sfi.sizeAdj);
            }
            return false;
        }
    }

    /**
     * Drives a recalculation of the size information for a Player Character
     * when a CDOMObject is added to the Player Character.
     * <p>
     * Triggered when one of the Facets to which SizeFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        update(dfce.getCharID());
    }

    /**
     * Drives a recalculation of the size information for a Player Character
     * when a CDOMObject is removed from the Player Character.
     * <p>
     * Triggered when one of the Facets to which SizeFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        update(dfce.getCharID());
    }

    /**
     * Drives a recalculation of the size information for a Player Character
     * when the level of the Player Character is changed.
     *
     * @param lce The LevelChangeEvent containing the information about the
     *            change
     */
    @Override
    public void levelChanged(LevelChangeEvent lce)
    {
        update(lce.getCharID());
    }

    /**
     * Drives a recalculation of the size information for a Player Character
     * when a BONUS on the Player Character is changed.
     *
     * @param bce The BonusChangeEvent containing the information about the
     *            change
     */
    @Override
    public void bonusChange(BonusChangeEvent bce)
    {
        update(bce.getCharID());
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    public void setRaceFacet(RaceFacet raceFacet)
    {
        this.raceFacet = raceFacet;
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    public void setLevelFacet(LevelFacet levelFacet)
    {
        this.levelFacet = levelFacet;
    }

    public void setResultFacet(ResultFacet resultFacet)
    {
        this.resultFacet = resultFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for SizeFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the SizeFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
        OutputDB.register("sizeadjustment", this);
    }

    /**
     * Copies the contents of the SizeFacet from one Player Character to another
     * Player Character, based on the given CharIDs representing those Player
     * Characters.
     * <p>
     * This is a method in SizeFacet in order to avoid exposing the mutable
     * SizeFacetInfo object to other classes. This should not be inlined, as
     * SizeFacetInfo is internal information to SizeFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no Size references are
     * maintained between the Player Characters represented by the given CharIDs
     * (meaning once this copy takes place, any change to the Size will only
     * impact the Player Character where the Size was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               Size information should be copied
     * @param copy   The CharID representing the Player Character to which the Size
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        SizeFacetInfo si = getInfo(source);
        if (si != null)
        {
            SizeFacetInfo copysfi = getConstructingInfo(copy);
            copysfi.racialSizeInt = si.racialSizeInt;
            copysfi.sizeAdj = si.sizeAdj;
        }
    }
}
