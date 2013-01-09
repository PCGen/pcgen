/*
 * PreApplyParser.java
 * 
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on 18-Dec-2003
 * 
 * Current Ver: $Revision$
 * 
 * Last Editor: $Author$
 * 
 * Last Edited: $Date$
 *  
 */
package plugin.lsttokens.deprecated;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;
import plugin.bonustokens.Weapon;
import plugin.lsttokens.BonusLst;
import plugin.lsttokens.TempBonusLst;
import plugin.lsttokens.tempbonus.AnyPCToken;
import plugin.lsttokens.tempbonus.EQToken;
import plugin.lsttokens.tempbonus.PCToken;
import plugin.lsttokens.testsupport.TokenRegistration;

@SuppressWarnings("nls")
public class BonusWithPreApplyTest extends EnUsLocaleDependentTestCase
{

	private final BonusLst token = new BonusLst();
	private static boolean setup = false;
	
	public static void setUp() throws PersistenceLayerException
	{
		if (!setup)
		{
			setup = true;
			TokenRegistration.register(new TempBonusLst());
			TokenRegistration.register(new AnyPCToken());
			TokenRegistration.register(new PCToken());
			TokenRegistration.register(new EQToken());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAnyPc() throws Exception
	{
		setUp();
		TokenRegistration.register(Weapon.class);
		Spell spell =
				Globals.getContext().ref.constructNowIfNecessary(Spell.class,
					"MySpellAnyPc");
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS_EQUIP));
		String bonusString =
				"WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement|PREAPPLY:ANYPC";
		ParseResult pr =
				token.parseToken(Globals.getContext(), spell, bonusString);
		pr.addMessagesToLog();
		Logging.replayParsedMessages();
		Logging.clearParseMessages();
		assertTrue(pr.passed());
		Globals.getContext().commit();
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertTrue(spell.containsListFor(ListKey.BONUS_ANYPC));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS_EQUIP));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPc() throws Exception
	{
		setUp();
		TokenRegistration.register(Weapon.class);
		Spell spell =
				Globals.getContext().ref.constructNowIfNecessary(Spell.class,
					"MySpellPc");
		assertFalse(spell.containsListFor(ListKey.BONUS_EQUIP));
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS));
		String bonusString =
				"WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement|PREAPPLY:PC";
		ParseResult pr =
				token.parseToken(Globals.getContext(), spell, bonusString);
		pr.addMessagesToLog();
		Logging.replayParsedMessages();
		Logging.clearParseMessages();
		assertTrue(pr.passed());
		Globals.getContext().commit();
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertTrue(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
		assertFalse(spell.containsListFor(ListKey.BONUS_EQUIP));
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEquipOr() throws Exception
	{
		setUp();
		TokenRegistration.register(Weapon.class);
		Spell spell =
				Globals.getContext().ref.constructNowIfNecessary(Spell.class,
					"MySpellEqOr");
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
		assertFalse(spell.containsListFor(ListKey.BONUS_EQUIP));
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		String bonusString =
				"WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement|PREAPPLY:Ranged;Melee";
		ParseResult pr =
				token.parseToken(Globals.getContext(), spell, bonusString);
		pr.addMessagesToLog();
		Logging.replayParsedMessages();
		Logging.clearParseMessages();
		assertTrue(pr.passed());
		Globals.getContext().commit();
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertTrue(spell.containsListFor(ListKey.BONUS_EQUIP));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
	}

	@Test
	public void testEquipAnd() throws Exception
	{
		setUp();
		TokenRegistration.register(Weapon.class);
		Spell spell =
				Globals.getContext().ref.constructNowIfNecessary(Spell.class,
					"MySpellEqAnd");
		assertFalse(spell.containsListFor(ListKey.BONUS_EQUIP));
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
		String bonusString =
				"WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement|PREAPPLY:Wooden,Blunt";
		ParseResult pr =
				token.parseToken(Globals.getContext(), spell, bonusString);
		pr.addMessagesToLog();
		Logging.replayParsedMessages();
		Logging.clearParseMessages();
		assertTrue(pr.passed());
		Globals.getContext().commit();
		assertFalse(spell.containsListFor(ListKey.BONUS));
		assertTrue(spell.containsListFor(ListKey.BONUS_EQUIP));
		assertFalse(spell.containsListFor(ListKey.BONUS_PC));
		assertFalse(spell.containsListFor(ListKey.BONUS_ANYPC));
	}

}
