/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.facet.base;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.DataFacetChangeEvent;
import pcgen.cdom.facet.DataFacetChangeListener;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.spell.Spell;

/**
 * KnownSpellFacet is a Facet that tracks the Available Spells (and target
 * objects) that are contained in a Player Character. These are post-resolution
 * of spells for which the PC is qualified.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public abstract class AbstractSpellInputFacet implements
		DataFacetChangeListener<CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Triggered when one of the Facets to which ConditionallyKnownSpellFacet
	 * listens fires a DataFacetChangeEvent to indicate a CDOMObject was added
	 * to a Player Character.
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
		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> listrefs =
				cdo.getModifiedLists();
		CharID id = dfce.getCharID();
		for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : listrefs)
		{
			processListRef(id, cdo, ref);
		}
	}

	private void processListRef(CharID id, CDOMObject cdo,
		CDOMReference<? extends CDOMList<? extends PrereqObject>> listref)
	{
		for (CDOMList<? extends PrereqObject> list : listref
			.getContainedObjects())
		{
			if (!(list instanceof ClassSpellList)
				&& !(list instanceof DomainSpellList))
			{
				continue;
			}
			CDOMList<Spell> spelllist = (CDOMList<Spell>) list;
			processList(id, spelllist, listref, cdo);
		}
	}

	private void processList(CharID id, CDOMList<Spell> spelllist,
		CDOMReference<? extends CDOMList<? extends PrereqObject>> listref,
		CDOMObject cdo)
	{
		AbstractConditionalSpellFacet conditionalFacet = getConditionalFacet();
		AbstractSpellStorageFacet unconditionalFacet = getUnconditionalFacet();
		for (CDOMReference<Spell> objref : cdo
			.getListMods((CDOMReference<? extends CDOMList<Spell>>) listref))
		{
			for (AssociatedPrereqObject apo : cdo.getListAssociations(listref,
				objref))
			{
				if (meetsAddConditions(apo))
				{
					Collection<Spell> spells = objref.getContainedObjects();
					if (apo.hasPrerequisites())
					{
						conditionalFacet
							.addAll(id, spelllist, spells, apo, cdo);
					}
					else
					{
						Integer lvl =
								apo.getAssociation(AssociationKey.SPELL_LEVEL);
						unconditionalFacet.addAll(id, spelllist, lvl, spells,
							cdo);
					}
				}
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which ConditionallyKnownSpellFacet
	 * listens fires a DataFacetChangeEvent to indicate a CDOMObject was removed
	 * from a Player Character.
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
		CharID id = dfce.getCharID();
		CDOMObject source = dfce.getCDOMObject();
		getUnconditionalFacet().removeAll(id, source);
		getConditionalFacet().removeAll(id, source);
	}


	protected abstract AbstractConditionalSpellFacet getConditionalFacet();

	protected abstract boolean meetsAddConditions(AssociatedPrereqObject apo);

	protected abstract AbstractSpellStorageFacet getUnconditionalFacet();

	public void setConsolidationFacet(
		CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for KnwonSpellFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the KnwonSpellFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
