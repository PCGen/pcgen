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
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;

/**
 * ActiveSpellsFacet is a Facet that tracks the active SPELLS for the
 * PlayerCharacter
 */
public class ActiveSpellsFacet extends AbstractSourcedListFacet<CharacterSpell>
		implements DataFacetChangeListener<CDOMObject>
{
	private RaceFacet raceFacet;

	private DeityFacet deityFacet;

	private TemplateFacet templateFacet;

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private FormulaResolvingFacet formulaResolvingFacet;

	private SpellsFacet spellsFacet;

	@Override
	protected Map<CharacterSpell, Set<Object>> getComponentMap()
	{
		return new TreeMap<CharacterSpell, Set<Object>>();
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		process(id);
	}

	public void process(CharID id)
	{
		Race race = raceFacet.get(id);
		removeAll(id, race);
		PlayerCharacter pc = trackingFacet.getPC(id);
		for (SpellLikeAbility sla : spellsFacet.getQualifiedSet(id))
		{
			Formula times = sla.getCastTimes();
			int resolvedTimes =
					formulaResolvingFacet.resolve(id, times,
						sla.getQualifiedKey()).intValue();
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

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		process(dfce.getCharID());
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setDeityFacet(DeityFacet deityFacet)
	{
		this.deityFacet = deityFacet;
	}

	public void setFormulaResolvingFacet(
		FormulaResolvingFacet formulaResolvingFacet)
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

	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
		deityFacet.addDataFacetChangeListener(this);
		templateFacet.addDataFacetChangeListener(this);
	}
}