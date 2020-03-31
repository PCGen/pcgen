/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.gui2.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.util.ListFacade;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SpellBuilderFacadeImplTest
 * 
 * 
 */
public class SpellBuilderFacadeImplTest extends AbstractCharacterTestCase
{

	private MockDataSetFacade dataset;
	private MockUIDelegate uiDelegate;
	private PCClass wizardCls;
	private PCClass divineCls;
	private PCClass fighterCls;
	private Spell clw;
	private Spell magicMissile;
	private Spell web;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		LoadContext context = Globals.getContext();
		
		wizardCls = TestHelper.makeClass("Wizard");
		BuildUtilities.setFact(wizardCls, "SpellType", "Arcane");
		context.unconditionallyProcess(wizardCls, "SPELLSTAT", "INT");
		wizardCls.put(ObjectKey.SPELLBOOK, true);
		wizardCls.put(ObjectKey.MEMORIZE_SPELLS, true);
		context.unconditionallyProcess(wizardCls.getOriginalClassLevel(1), "CAST", "3,1,0");

		divineCls = TestHelper.makeClass("DivineSpontaneous");
		BuildUtilities.setFact(divineCls, "SpellType", "Divine");
		context.unconditionallyProcess(divineCls, "SPELLSTAT", "WIS");
		divineCls.put(ObjectKey.SPELLBOOK, false);
		divineCls.put(ObjectKey.MEMORIZE_SPELLS, false);
		context.unconditionallyProcess(divineCls.getOriginalClassLevel(1), "KNOWN", "4,2,1");
		context.unconditionallyProcess(divineCls.getOriginalClassLevel(1), "CAST", "3,1,0");
		
		fighterCls = TestHelper.makeClass("Fighter");
		
		dataset = new MockDataSetFacade(SettingsHandler.getGameAsProperty().get());
		dataset.addAbilityCategory(BuildUtilities.getFeatCat());
		dataset.addClass(wizardCls);
		dataset.addClass(divineCls);
		dataset.addClass(fighterCls);
		uiDelegate = new MockUIDelegate();
		
		clw = TestHelper.makeSpell("Cure Light Wounds");
		context.unconditionallyProcess(clw, "CLASSES", divineCls.getKeyName()+"=1");
		magicMissile = TestHelper.makeSpell("Magic Missile");
		context.unconditionallyProcess(magicMissile, "CLASSES", wizardCls.getKeyName()+"=1");
		web = TestHelper.makeSpell("Web");
		context.unconditionallyProcess(web, "CLASSES", wizardCls.getKeyName()+"=2");

		finishLoad();
	}

	@Test
	public void testEmptyChoice()
	{
		SpellBuilderFacadeImpl spellBuilder = createSpellBuilder();
		ListFacade<InfoFacade> classes = spellBuilder.getClasses();
		assertNotNull("Class list should be defined", classes);
		assertTrue("Class list should have wizard", classes.containsElement(wizardCls));
		assertTrue("Class list should have divine class", classes.containsElement(divineCls));
		assertFalse("Class list should not have fighter", classes.containsElement(fighterCls));
		assertEquals("Class list length", 2, classes.getSize());
		
		
	}

	/**
	 * Verify that the selection of spells based on class and level is working correctly. 
	 */
	@Test
	public void testClassLevel()
	{
		SpellBuilderFacadeImpl spellBuilder = createSpellBuilder();
		spellBuilder.setClass(wizardCls);
		spellBuilder.setSpellLevel(1);
		ListFacade<InfoFacade> spells = spellBuilder.getSpells();
		assertNotNull("Spell list should be defined", spells);
		assertEquals("Spell list length", 1, spells.getSize());
		assertTrue("Spell list should have MM", spells.containsElement(magicMissile));

		spellBuilder.setSpellLevel(2);
		spells = spellBuilder.getSpells();
		assertNotNull("Spell list should be defined", spells);
		assertEquals("Spell list length", 1, spells.getSize());
		assertTrue("Spell list should have Web", spells.containsElement(web));
		assertFalse("Spell list should not have MM", spells.containsElement(magicMissile));

		spellBuilder.setClass(divineCls);
		spellBuilder.setSpellLevel(1);
		spells = spellBuilder.getSpells();
		assertNotNull("Spell list should be defined", spells);
		assertEquals("Spell list length", 1, spells.getSize());
		assertTrue("Spell list should have CLW", spells.containsElement(clw));
		
	}

	private SpellBuilderFacadeImpl createSpellBuilder()
	{
		PlayerCharacter pc = getCharacter();
		CharacterFacadeImpl charFacade =
				new CharacterFacadeImpl(pc, uiDelegate, dataset);
		assertNotNull("Unable to create CharacterFacadeImpl", charFacade);
		
		SpellBuilderFacadeImpl spellBuilder = new SpellBuilderFacadeImpl("", pc, null);
		assertNotNull("Unable to create SpellBuilderFacadeImpl", spellBuilder);
		return spellBuilder;
	}

	@Override
	protected void defaultSetupEnd()
	{
		//We'll take care of it ourselves
	}
	
	
}
