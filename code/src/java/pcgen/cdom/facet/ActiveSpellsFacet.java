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
package pcgen.cdom.facet;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.SpellLikeAbility;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;

/**
 * ActiveSpellsFacet is a Facet that tracks the active SPELLS for the
 * PlayerCharacter
 * 
 */
public class ActiveSpellsFacet extends AbstractSourcedListFacet<CharID, CharacterSpell>
		implements DataFacetChangeListener<CharID, CDOMObject>
{
	private RaceFacet raceFacet;

	private TemplateFacet templateFacet;

	private final PlayerCharacterTrackingFacet trackingFacet =
			FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	private FormulaResolvingFacet formulaResolvingFacet;

	private SpellsFacet spellsFacet;

	/**
	 * Returns a new (empty) Map for this ActiveSpellsFacet. This overrides the
	 * default provided in AbstractSourcedListFacet, in order to maintain a
	 * sorted list of Spells for the Player Character. This does not require the
	 * IdentityHashMap since CharacterSpell is not cloned, and behaves properly
	 * with .equals() and .hashCode() in terms of maintaining identity (whereas
	 * many CDOMObjects do not as of 5.16)
	 * 
	 * Note that this method should always be the only method used to construct
	 * a Map for this ActiveSpellsFacet. It is actually preferred to use
	 * getConstructingCacheMap(CharID) in order to implicitly call this method.
	 * 
	 * @return A new (empty) Map for use in this ActiveSpellsFacet.
	 */
	@Override
	protected Map<CharacterSpell, Set<Object>> getComponentMap()
	{
		return new TreeMap<>();
	}

	/**
	 * Adds the Spells associated with a CDOMObject which is granted to a Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which ActiveSpellsFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		process(dfce.getCharID());
	}

	/**
	 * Currently used as a global reset for the spell list, since
	 * ActiveSpellsFacet does not currently listen to all scenarios which can
	 * alter Spells granted to a Player Character.
	 * 
	 * Use of this method outside this facet is discouraged, as the long term
	 * goal is to get all of the processing for Spells into this Facet.
	 * Therefore, use of this global reset indicates incomplete implementation
	 * of Spells processing in this facet, and should be an indication that
	 * additional work is required in order to enhance the capability of this
	 * facet to appropriately update the Spells for a Player Character.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character that requires a
	 *            reset on the list of spells granted to the Player Character.
	 */
	public void process(CharID id)
	{
		Race race = raceFacet.get(id);
		removeAll(id, race);
		PlayerCharacter pc = trackingFacet.getPC(id);
		for (SpellLikeAbility sla : spellsFacet.getQualifiedSet(id))
		{
			Formula times = sla.getCastTimes();
			int resolvedTimes = formulaResolvingFacet.resolve(id, times, sla.getQualifiedKey()).intValue();
			String book = sla.getSpellBook();

			final CharacterSpell cs = new CharacterSpell(race, sla.getSpell());
			cs.setFixedCasterLevel(sla.getFixedCasterLevel());
			SpellInfo si = cs.addInfo(0, resolvedTimes, book);
			si.setTimeUnit(sla.getCastTimeUnit());
			si.setFixedDC(sla.getDC());

			pc.addSpellBook(new SpellBook(book, SpellBook.TYPE_INNATE_SPELLS));
			add(id, cs, race);
		}
	}

	/**
	 * Removes the Spells associated with a CDOMObject which is granted to a
	 * Player Character.
	 * 
	 * Triggered when one of the Facets to which ActiveSpellsFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		process(dfce.getCharID());
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setSpellsFacet(SpellsFacet spellsFacet)
	{
		this.spellsFacet = spellsFacet;
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	/**
	 * Initializes the connections for ActiveSpellsFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the ActiveSpellsFacet.
	 */
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
		templateFacet.addDataFacetChangeListener(this);
	}
}
