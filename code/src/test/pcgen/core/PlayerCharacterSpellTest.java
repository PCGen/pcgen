/*
 * Copyright James Dempsey, 2012
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

import static org.junit.Assert.assertEquals;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code PlayerCharacterSpellTest} checks the function of spell related
 * code in PlayerCharacter and associated objects.
 *
 * <br/>
 * 
 */

public class PlayerCharacterSpellTest extends AbstractCharacterTestCase
{

	private Domain sunDomain;
	private Spell classSpell;
	private Spell domainSpell;
	private PCClass divineClass;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		LoadContext context = Globals.getContext();
		CampaignSourceEntry source = TestHelper.createSource(getClass());

		// Spells
		classSpell = TestHelper.makeSpell("classSpell");
		domainSpell = TestHelper.makeSpell("domainSpell");

		final String classLine =
				"CLASS:MyClass	TYPE:Base.PC	SPELLSTAT:CHA	MEMORIZE:YES	SPELLBOOK:NO";
		PCClassLoader classLoader = new PCClassLoader();
		divineClass = classLoader.parseLine(context, null, classLine, source);
		BuildUtilities.setFact(divineClass, "SpellType", "Divine");
		classLoader.parseLine(context, divineClass,
			"CLASS:MyClass	KNOWNSPELLS:LEVEL=0|LEVEL=1|LEVEL=2|LEVEL=3|LEVEL=4|LEVEL=5|LEVEL=6|LEVEL=7|"
					+ "LEVEL=8|LEVEL=9	BONUS:CASTERLEVEL|Cleric|CL",
			source);
		classLoader.parseClassLevelLine(context, divineClass, 1, source,
			"CAST:5,4	BONUS:DOMAIN|NUMBER|2	BONUS:VAR|DomainLVL|CL");
		
		final String domainLine = "Sun	SPELLLEVEL:DOMAIN|Sun=1|KEY_domainSpell";
		GenericLoader<Domain> domainLoader = new GenericLoader<>(Domain.class);
		domainLoader.parseLine(context, null, domainLine, source);
		sunDomain = context.getReferenceContext().silentlyGetConstructedCDOMObject(Domain.class, "Sun");

		CDOMReference<ClassSpellList> ref = TokenUtilities.getTypeOrPrimitive(context,
			ClassSpellList.class, divineClass.getKeyName());
		AssociatedPrereqObject edge =
				context.getListContext().addToMasterList("CLASSES", classSpell,
					ref, classSpell);
		edge.setAssociation(AssociationKey.SPELL_LEVEL, 1);

		finishLoad();
	}

	/**
	 * Test that domain spell lists are built and managed correctly.
     */
	@Test
	public void testDomainSpell() {
		PlayerCharacter pc = getCharacter();
		setPCStat(pc, cha, 15);
		pc.incrementClassLevel(1, divineClass);
		PCClass cls = pc.getClassKeyed(divineClass.getKeyName());
		pc.getSpellSupport(cls).getMaxCastLevel(pc);
		pc.addDomain(sunDomain);
		
		List<? extends CDOMList<Spell>> spellLists = pc.getSpellLists(sunDomain);
		assertEquals("Incorrect number of spell lists for domain", 1, spellLists.size());
		int level = SpellLevel.getFirstLevelForKey(domainSpell, spellLists, pc);
		assertEquals("Incorrect spell level in domain list", 1, level);
	}

	/**
	 * Test that class spell lists are built and managed correctly.
     */
	@Test
	public void testPcClassSpell() {
		PlayerCharacter pc = getCharacter();
		pc.incrementClassLevel(1, divineClass);
		
		List<? extends CDOMList<Spell>> spellLists = pc.getSpellLists(pc.getClassKeyed(divineClass.getKeyName()));
		assertEquals("Incorrect number of spell lists in class list", 1, spellLists.size());
		int level = SpellLevel.getFirstLevelForKey(classSpell, spellLists, pc);
		assertEquals("Incorrect spell level in class list", 1, level);
	}

	@Override
	protected void defaultSetupEnd()
	{
		//Nothing, we will trigger ourselves
	}
}
