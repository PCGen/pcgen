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
package pcgen.cdom.facet;

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
import pcgen.core.QualifiedObject;
import pcgen.core.Vision;
import pcgen.core.prereq.Prerequisite;
import pcgen.util.enumeration.VisionType;

/**
 * VisionFacet is a Facet that tracks the Vision objects that are contained in a
 * Player Character.
 */
public class VisionFacet extends
		AbstractSourcedListFacet<QualifiedObject<Vision>> implements
		DataFacetChangeListener<CDOMObject>
{

	private FormulaResolvingFacet formulaResolvingFacet;

	private BonusCheckingFacet bonusCheckingFacet;

	private PrerequisiteFacet prerequisiteFacet;

	/**
	 * Triggered when one of the Facets to which VisionFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		Collection<CDOMReference<Vision>> mods = cdo
				.getListMods(Vision.VISIONLIST);
		if (mods != null)
		{
			CharID id = dfce.getCharID();
			for (CDOMReference<Vision> ref : mods)
			{
				Collection<AssociatedPrereqObject> assoc = cdo
						.getListAssociations(Vision.VISIONLIST, ref);
				for (AssociatedPrereqObject apo : assoc)
				{
					List<Prerequisite> prereqs = apo.getPrerequisiteList();
					for (Vision v : ref.getContainedObjects())
					{
						add(id, new QualifiedObject<Vision>(v, prereqs), cdo);
					}
				}
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which VisionFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	public Collection<Vision> getActiveVision(CharID id)
	{
		Map<QualifiedObject<Vision>, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptyList();
		}
		Map<VisionType, Integer> map = new HashMap<VisionType, Integer>();
		for (Map.Entry<QualifiedObject<Vision>, Set<Object>> me : componentMap
				.entrySet())
		{
			QualifiedObject<Vision> qo = me.getKey();
			for (Object source : me.getValue())
			{
				if (prerequisiteFacet.qualifies(id, qo, source))
				{
					String sourceString = (source instanceof CDOMObject) ? ((CDOMObject) source)
							.getQualifiedKey()
							: "";
					Vision v = qo.getRawObject();
					Formula distance = v.getDistance();
					int a = formulaResolvingFacet.resolve(id, distance, sourceString)
							.intValue();
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
			int aVal = (int) bonusCheckingFacet
					.getBonus(id, "VISION", vType.toString());

			if (aVal > 0)
			{
				Integer current = map.get(vType);
				map.put(vType, aVal + (current == null ? 0 : current));
			}
		}
		TreeSet<Vision> returnSet = new TreeSet<Vision>();
		for (Map.Entry<VisionType, Integer> me : map.entrySet())
		{
			returnSet.add(new Vision(me.getKey(), FormulaFactory
					.getFormulaFor(me.getValue().intValue())));
		}
		return returnSet;
	}

	public Vision getActiveVision(CharID id, VisionType type)
	{
		Map<QualifiedObject<Vision>, Set<Object>> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return null;
		}
		Integer i = null;
		for (Map.Entry<QualifiedObject<Vision>, Set<Object>> me : componentMap
				.entrySet())
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
						String sourceString = (source instanceof CDOMObject) ? ((CDOMObject) source)
								.getQualifiedKey()
								: "";
						Formula distance = v.getDistance();
						int a = formulaResolvingFacet
								.resolve(id, distance, sourceString).intValue();
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
		return new Vision(type, FormulaFactory.getFormulaFor(i.intValue()));
	}

	public int getVisionCount(CharID id)
	{
		// Slow method for now...
		return getActiveVision(id).size();
	}

	@Override
	protected Map<QualifiedObject<Vision>, Set<Object>> getComponentMap()
	{
		return new HashMap<QualifiedObject<Vision>, Set<Object>>();
	}

	public void setFormulaResolvingFacet(
		FormulaResolvingFacet formulaResolvingFacet)
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

}
