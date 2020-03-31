/*
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.content.BonusSpellInfo;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpellListTokenTest extends AbstractCharacterTestCase
{
	private PCClass arcaneClass = null;
	private PCClass divineClass = null;
	private Race human = null;

	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		LoadContext context = Globals.getContext();

		SettingsHandler.getGameAsProperty().get().setSpellBaseDC("10+SPELLLEVEL+BASESPELLSTAT");

		SimpleLoader<BonusSpellInfo> bonusSpellLoader = new SimpleLoader<>(
				BonusSpellInfo.class);
		try
		{
			URI testURI = new URI("file:/" + getClass().getName() + ".java");
			bonusSpellLoader.parseLine(context, "1	BASESTATSCORE:12	STATRANGE:8", testURI);
			bonusSpellLoader.parseLine(context, "2	BASESTATSCORE:14	STATRANGE:8", testURI);
			bonusSpellLoader.parseLine(context, "3	BASESTATSCORE:16	STATRANGE:8", testURI);
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}		

		// Human
		human = new Race();

		final BonusObj bon = Bonus.newBonus(context, "FEAT|POOL|2");
		human.addToListFor(ListKey.BONUS, bon);

		arcaneClass = new PCClass();
		arcaneClass.setName("TestArcane");
		BuildUtilities.setFact(arcaneClass, "SpellType", "Arcane");
		context.unconditionallyProcess(arcaneClass, "SPELLSTAT", "CHA");
		arcaneClass.put(ObjectKey.SPELLBOOK, false);
		arcaneClass.put(ObjectKey.MEMORIZE_SPELLS, false);
		context.unconditionallyProcess(arcaneClass.getOriginalClassLevel(1), "KNOWN", "4,2,1");
		context.unconditionallyProcess(arcaneClass.getOriginalClassLevel(1), "CAST", "3,1,0");
		context.getReferenceContext().importObject(arcaneClass);

		divineClass = new PCClass();
		divineClass.setName("TestDivine");
		BuildUtilities.setFact(divineClass, "SpellType", "Divine");
		context.unconditionallyProcess(divineClass, "SPELLSTAT", "WIS");
		divineClass.put(ObjectKey.SPELLBOOK, false);
		divineClass.put(ObjectKey.MEMORIZE_SPELLS, true);
		context.unconditionallyProcess(divineClass.getOriginalClassLevel(1), "CAST", "3,1,0");
		context.unconditionallyProcess(divineClass, "SPELLLEVEL",
				"CLASS|SPELLCASTER.Divine=1|Cure Light Wounds");
		context.getReferenceContext().importObject(divineClass);
		context.resolveDeferredTokens();
		context.getReferenceContext().buildDerivedObjects();
		context.getReferenceContext().resolveReferences(null);
	}

	@AfterEach
	@Override
	protected void tearDown() throws Exception
	{
		Globals.getContext().getReferenceContext().forget(divineClass);
		Globals.getContext().getReferenceContext().forget(arcaneClass);

		super.tearDown();
	}

	/**
	 * Test the SPELLLISTBOOK sub-tag of the SPELLLIST token.
	 */
	@Test
	public void testSpellListBookToken()
	{
		PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, arcaneClass, true);
		character.incrementClassLevel(1, divineClass, true);

		SpellListBookToken token = new SpellListBookToken();
		// TODO: These don't work yet, so skip them to avoid false errors.
//		assertEquals("SpellListBookToken(1lv TA)", "", token.getToken(
//			"SPELLLISTBOOK.0.1", character, null));
//		assertEquals("SpellListBookToken(1lv TD)", "TestDivine", token
//			.getToken("SPELLLISTBOOK.1.1", character, null));
	}

	/**
	 * Test the SPELLLISTCAST sub-tag of the SPELLLIST token.
	 */
	@Test
	public void testSpellListCastToken()
	{
		PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.setStat(cha, 12);
		character.calcActiveBonuses();
		character.incrementClassLevel(1, arcaneClass, true);

		SpellListCastToken token = new SpellListCastToken();
		assertEquals("testSpellListCastToken(1lv TA)", "2", token.getToken(
			"SPELLLISTCAST.0.1", character, null));
	}

	/**
	 * Test the SPELLLISTCLASS sub-tag of the SPELLLIST token.
	 */
	@Test
	public void testSpellListClassToken()
	{
		PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, arcaneClass, true);
		character.incrementClassLevel(1, divineClass, true);

		SpellListClassToken token = new SpellListClassToken();
		assertEquals("testSpellListClassToken(1lv TA)", "TestArcane", token
			.getToken("SPELLLISTCLASS.0", character, null));
		assertEquals("testSpellListClassToken(1lv TD)", "TestDivine", token
			.getToken("SPELLLISTCLASS.1", character, null));
	}

	/**
	 * Test the SPELLLISTDCSTAT sub-tag of the SPELLLIST token.
	 */
	@Test
	public void testSpellListDcStatToken()
	{
		PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, arcaneClass, true);
		character.incrementClassLevel(1, divineClass, true);

		SpellListDcStatToken token = new SpellListDcStatToken();
		assertEquals("testSpellListDcStatToken(1lv TA)", "CHA", token.getToken(
			"SPELLLISTDCSTAT.0.1", character, null));
		assertEquals("testSpellListDcStatToken(1lv TD)", "WIS", token.getToken(
			"SPELLLISTDCSTAT.1.1", character, null));
	}

	/**
	 * Test the SPELLLISTDC sub-tag of the SPELLLIST token.
	 */
	@Test
	public void testSpellListDcToken()
	{
		PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		setPCStat(character, cha, 14);
		character.calcActiveBonuses();
		character.incrementClassLevel(1, arcaneClass, true);
		character.calcActiveBonuses();

		SpellListDcToken token = new SpellListDcToken();
		assertEquals("SpellListDcToken(1lv TA)", "12", token.getToken(
			"SPELLLISTDC.0.0", character, null));
		assertEquals("SpellListDcToken(1lv TA)", "15", token.getToken(
			"SPELLLISTDC.0.3", character, null));
	}

	/**
	 * Test the SPELLLISTKNOWN sub-tag of the SPELLLIST token.
	 *
	 * Currently does nothing
	 */
	@Test
	public void testSpellListKnownToken()
	{
		// TODO Do Nothing?
	}

	/**
	 * Test the SPELLLISTTYPE sub-tag of the SPELLLIST token.
	 */
	@Test
	public void testSpellListTypeToken()
	{
		PlayerCharacter character = new PlayerCharacter();

		character.setRace(human);
		character.incrementClassLevel(1, arcaneClass, true);
		character.incrementClassLevel(1, divineClass, true);

		SpellListTypeToken token = new SpellListTypeToken();
		assertEquals("testSpellListTypeToken(1lv TA)", "Arcane", token
			.getToken("SPELLLISTTYPE.0.1", character, null));
		assertEquals("testSpellListTypeToken(1lv TD)", "Divine", token
			.getToken("SPELLLISTTYPE.1.1", character, null));
	}
}
