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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.analysis.SizeUtilities;

/**
 * UnarmedDamageFacet is a Facet that tracks the Unarmed Damage info that have
 * been added to a Player Character.
 */
public class UnarmedDamageFacet extends AbstractSourcedListFacet<List<String>>
		implements DataFacetChangeListener<CDOMObject>
{
	private RaceFacet raceFacet;

	private FormulaResolvingFacet formulaResolvingFacet;

	/**
	 * Triggered when one of the Facets to which UnarmedDamageFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
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
		if (cdo instanceof PCClass || cdo instanceof PCClassLevel)
		{
			return;
		}
		List<String> damage = cdo.getListFor(ListKey.UNARMED_DAMAGE);
		if (damage != null)
		{
			add(dfce.getCharID(), damage, cdo);
		}
	}

	/**
	 * Triggered when one of the Facets to which UnarmedDamageFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
	 * Player Character.
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

	public String getUDamForRace(CharID id)
	{
		Race race = raceFacet.get(id);
		int iSize = formulaResolvingFacet.resolve(id, race.getSafe(FormulaKey.SIZE),
				race.getQualifiedKey()).intValue();
		SizeAdjustment defAdj = SizeUtilities.getDefaultSizeAdjustment();
		SizeAdjustment sizAdj = Globals.getContext().ref.getItemInOrder(
				SizeAdjustment.class, iSize);
		if (sizAdj != null)
		{
			return Globals.adjustDamage("1d3", defAdj, sizAdj);
		}
		return "1d3";
	}

	@Override
	protected Map<List<String>, Set<Object>> getComponentMap()
	{
		return new HashMap<List<String>, Set<Object>>();
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}
}
