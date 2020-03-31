/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.DataSetInitializedFacet;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;

/**
 * The Class {@code MasterSkillFacet} caches a copy of all class skill
 * lists. This allows faster checking of whether skills are class skills for a
 * character class. Note this is a "global" facet in that it does not have
 * method that depend on CharID (they are not character specific).
 * 
 */
public class MasterAvailableSpellFacet extends AbstractListFacet<DataSetID, AvailableSpell>
		implements DataSetInitializedFacet
{
	private DataSetInitializationFacet datasetInitializationFacet;

	/**
	 * Initializes the global lists of ClassSkillLists. This method only needs
	 * to be called once for each set of sources that are loaded.
	 */
	@Override
	public synchronized void initialize(LoadContext lc)
	{
		DataSetID dsID = lc.getDataSetID();
		MasterListInterface masterLists = SettingsHandler.getGameAsProperty().get().getMasterLists();
		List<CDOMReference<CDOMList<Spell>>> useLists = new ArrayList<>();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			Collection<CDOMList<Spell>> lists = ref.getContainedObjects();
			for (CDOMList<Spell> list : lists)
			{
				if ((list instanceof ClassSpellList) || (list instanceof DomainSpellList))
				{
					useLists.add(ref);
					break;
				}
			}
		}
		for (CDOMReference<CDOMList<Spell>> ref : useLists)
		{
			for (Spell spell : masterLists.getObjects(ref))
			{
				Collection<AssociatedPrereqObject> assoc = masterLists.getAssociations(ref, spell);
				for (AssociatedPrereqObject apo : assoc)
				{
					int lvl = apo.getAssociation(AssociationKey.SPELL_LEVEL);
					for (CDOMList<Spell> list : ref.getContainedObjects())
					{
						AvailableSpell as = new AvailableSpell(list, spell, lvl);
						if (apo.hasPrerequisites())
						{
							as.addAllPrerequisites(apo.getPrerequisiteList());
						}
						add(dsID, as);
					}
				}
			}
		}
	}

	/**
	 * Retrieve a list of all spells for a particular spell list. 
	 * @param spellList The list to be queried
	 * @param dsID The owning data set
	 * @return The list of available spells.
	 */
	public List<AvailableSpell> getAllSpellsInList(CDOMList<Spell> spellList, DataSetID dsID)
	{
		List<AvailableSpell> spellsInList = new ArrayList<>();
		Collection<AvailableSpell> spells = getSet(dsID);
		for (AvailableSpell as : spells)
		{
			if (as.getSpelllist().equals(spellList))
			{
				spellsInList.add(as);
			}
		}

		return spellsInList;
	}

	/**
	 * Retrieve a list of any occurrence of a specific spell in the particular spell list. 
	 * @param spellList The list to be queried
	 * @param dsID The owning data set
	 * @param spell The spell to be found.
	 * @return The list of available spells.
	 */
	public List<AvailableSpell> getMatchingSpellsInList(CDOMList<Spell> spellList, DataSetID dsID, Spell spell)
	{
		List<AvailableSpell> spellsInList = new ArrayList<>();
		Collection<AvailableSpell> spells = getSet(dsID);
		for (AvailableSpell as : spells)
		{
			if (as.getSpelllist().equals(spellList) && as.getSpell().equals(spell))
			{
				spellsInList.add(as);
			}
		}

		return spellsInList;
	}

	public void setDataSetInitializationFacet(DataSetInitializationFacet datasetInitializationFacet)
	{
		this.datasetInitializationFacet = datasetInitializationFacet;
	}

	public void init()
	{
		datasetInitializationFacet.addDataSetInitializedFacet(this);
	}
}
