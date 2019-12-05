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
package pcgen.cdom.facet.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.PrerequisiteFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.QualifiedObject;
import pcgen.core.Vision;
import pcgen.core.prereq.Prerequisite;
import pcgen.util.enumeration.VisionType;

/**
 * VisionFacet is a Facet that tracks the Vision objects that are contained in a
 * Player Character.
 */
public class VisionFacet extends AbstractSourcedListFacet<CharID, QualifiedObject<Vision>>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private FormulaResolvingFacet formulaResolvingFacet;

    private BonusCheckingFacet bonusCheckingFacet;

    private PrerequisiteFacet prerequisiteFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Adds any granted Vision objects to this facet when a CDOMObject that
     * grants Vision objects is added to a Player Character.
     * <p>
     * Triggered when one of the Facets to which VisionFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        Collection<CDOMReference<Vision>> mods = cdo.getListMods(Vision.VISIONLIST);
        if (mods != null)
        {
            CharID id = dfce.getCharID();
            for (CDOMReference<Vision> ref : mods)
            {
                Collection<AssociatedPrereqObject> assoc = cdo.getListAssociations(Vision.VISIONLIST, ref);
                for (AssociatedPrereqObject apo : assoc)
                {
                    List<Prerequisite> prereqs = apo.getPrerequisiteList();
                    for (Vision v : ref.getContainedObjects())
                    {
                        add(id, new QualifiedObject<>(v, prereqs), cdo);
                    }
                }
            }
        }
    }

    /**
     * Removes any granted Vision objects to this facet when a CDOMObject that
     * grants Vision objects is removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which VisionFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    /**
     * Returns a non-null copy of the Collection of Vision objects which are
     * active on the Player Character identified by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Modification
     * of the returned Collection will not modify this VisionFacet and
     * modification of this VisionFacet will not modify the returned Collection.
     * Modifications to the returned Collection will also not modify any future
     * or previous objects returned by this (or other) methods on VisionFacet.
     * If you wish to modify the information stored in this VisionFacet, you
     * must use the add*() and remove*() methods of VisionFacet.
     *
     * @param id The CharID identifying the Player Character for which the
     *           active Vision objects is to be returned
     * @return a non-null copy of the Collection of Vision objects which are
     * active on the Player Character identified by the given CharID
     */
    public Collection<Vision> getActiveVision(CharID id)
    {
        Map<QualifiedObject<Vision>, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptyList();
        }
        Map<VisionType, Integer> map = new HashMap<>();
        for (Map.Entry<QualifiedObject<Vision>, Set<Object>> me : componentMap.entrySet())
        {
            QualifiedObject<Vision> qo = me.getKey();
            for (Object source : me.getValue())
            {
                if (prerequisiteFacet.qualifies(id, qo, source))
                {
                    String sourceString = (source instanceof CDOMObject) ? ((CDOMObject) source).getQualifiedKey() : "";
                    Vision v = qo.getRawObject();
                    Formula distance = v.getDistance();
                    int a = formulaResolvingFacet.resolve(id, distance, sourceString).intValue();
                    VisionType visType = v.getType();
                    Integer current = map.get(visType);
                    if (current == null || current < a)
                    {
                        map.put(visType, a);
                    }
                }
            }
        }

        /*
         * parse through the global list of vision tags and see if this PC has
         * any BONUS:VISION tags which will create a new visionMap entry, and
         * add any BONUS to existing entries in the map
         */
        for (VisionType vType : VisionType.getAllVisionTypes())
        {
            int aVal = (int) bonusCheckingFacet.getBonus(id, "VISION", vType.toString());

            if (aVal > 0)
            {
                Integer current = map.get(vType);
                map.put(vType, aVal + (current == null ? 0 : current));
            }
        }
        TreeSet<Vision> returnSet = new TreeSet<>();
        for (Map.Entry<VisionType, Integer> me : map.entrySet())
        {
            returnSet.add(new Vision(me.getKey(), FormulaFactory.getFormulaFor(me.getValue())));
        }
        return returnSet;
    }

    /**
     * Returns a Vision object for the given VisionType for the Player Character
     * identified by the given CharID.
     * <p>
     * For a Player Character that is not changed between calls to this method,
     * this method does not guarantee returning a Vision object of the same
     * identity for the same given VisionType. While the two Vision objects will
     * pass object equality (.equals()), they are not guaranteed to pass (or
     * guaranteed to fail) instance identity (a == b). This allows VisionFacet
     * to reserve the right to cache results, but does not require it.
     *
     * @param id   The CharID identifying the Player Character for which the
     *             Vision of the given VisionType is to be returned
     * @param type The VisionType for which the Vision is to be returned
     * @return A Vision object for the given VisionType for the Player Character
     * identified by the given CharID.
     */
    public Vision getActiveVision(CharID id, VisionType type)
    {
        Map<QualifiedObject<Vision>, Set<Object>> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return null;
        }
        Integer i = null;
        for (Map.Entry<QualifiedObject<Vision>, Set<Object>> me : componentMap.entrySet())
        {
            QualifiedObject<Vision> qo = me.getKey();
            Vision v = qo.getRawObject();
            VisionType visType = v.getType();
            if (type.equals(visType))
            {
                for (Object source : me.getValue())
                {
                    if (prerequisiteFacet.qualifies(id, qo, source))
                    {
                        String sourceString =
                                (source instanceof CDOMObject) ? ((CDOMObject) source).getQualifiedKey() : "";
                        Formula distance = v.getDistance();
                        int a = formulaResolvingFacet.resolve(id, distance, sourceString).intValue();
                        if (i == null || i < a)
                        {
                            i = a;
                        }
                    }
                }
            }
        }

        /*
         * parse through the global list of vision tags and see if this PC has
         * any BONUS:VISION tags which will create a new visionMap entry, and
         * add any BONUS to existing entries in the map
         */
        int a = (int) bonusCheckingFacet.getBonus(id, "VISION", type.toString());

        if (a > 0)
        {
            if (i == null || i < a)
            {
                i = a;
            }
        }
        if (i == null)
        {
            return null;
        }
        return new Vision(type, FormulaFactory.getFormulaFor(i));
    }

    /**
     * Returns the count of vision types possessed by the Player Character
     * identified by the given CharID.
     *
     * @param id The CharID identifying the Player Character for which the
     *           number of vision types is to be returned
     * @return The count of vision types possessed by the Player Character
     * identified by the given CharID
     */
    public int getVisionCount(CharID id)
    {
        // Slow method for now...
        return getActiveVision(id).size();
    }

    /**
     * Returns a new (empty) Map for this VisionFacet. This overrides the
     * default provided in AbstractSourcedListFacet, since this does not require
     * the IdentityHashMap (Vision is immutable and behaves properly with
     * .equals() and .hashCode() in terms of maintaining identity (whereas many
     * CDOMObjects do not as of 5.16))
     * <p>
     * Note that this method should always be the only method used to construct
     * a Map for this VisionFacet. It is actually preferred to use
     * getConstructingCacheMap(CharID) in order to implicitly call this method.
     *
     * @return A new (empty) Map for use in this VisionFacet.
     */
    @Override
    protected Map<QualifiedObject<Vision>, Set<Object>> getComponentMap()
    {
        return new HashMap<>();
    }

    public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
    {
        this.formulaResolvingFacet = formulaResolvingFacet;
    }

    public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
    {
        this.bonusCheckingFacet = bonusCheckingFacet;
    }

    public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
    {
        this.prerequisiteFacet = prerequisiteFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for VisionFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the VisionFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
