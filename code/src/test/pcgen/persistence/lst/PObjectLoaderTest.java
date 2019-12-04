/*
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * 
 * 
 */
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PObjectLoaderTest
{
	/**
	 * Sets up the test case by loading the system plugins.
	 */
	@BeforeEach
	public void setUp() {
		TestHelper.loadPlugins();
	}

	@Test
	public void testDefine() {
		Ability feat = new Ability();

		Globals.getContext().unconditionallyProcess(feat, "DEFINE", "Foo|0");

		assertEquals(1, feat.getVariableKeys().size());
		assertEquals("Foo", feat.getVariableKeys().iterator().next().toString());
		assertEquals("0", feat.get(VariableKey.getConstant("Foo")).toString());
	}

	@Test
	public void testBadDefine() {
		Ability feat = new Ability();
		assertFalse(
				Globals.getContext().processToken(feat, "DEFINE", "Foo"), "Parse fails for badly formed define");
	}

	@Test
	public void testUnlockDefineStat() {
		LoadContext context = Globals.getContext();
		
		AbstractReferenceContext ref = context.getReferenceContext();
		ref.importObject(BuildUtilities.createStat("Constitution", "CON"));
		ref.importObject(BuildUtilities.createStat("Intelligence", "INT"));

		Ability feat = new Ability();

		assertTrue(
				context.processToken(feat, "DEFINESTAT", "UNLOCK|INT"), "Parse fails for unlock");
		context.commit();
		assertTrue(context.getReferenceContext().resolveReferences(null));
		Logging.clearParseMessages();

		List<CDOMSingleRef<PCStat>> statList = feat.getListFor(ListKey.UNLOCKED_STATS);
		assertEquals(1, statList.size());
		assertEquals("INT", statList.get(0).get().getKeyName());
	}

	@Test
	public void testBadUnlockDefine() {
		Ability feat = new Ability();
		assertFalse(
				Globals.getContext()
				       .processToken(feat, "DEFINESTAT", "UNLOCK|INT|0"), "Parse fails to catch bad unlock definestat");
	}

	@Test
	public void testParsePreClear() {
		PObject object = new PObject();

		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(object, "PREVARLT", "GreaterRage,1");
		context.unconditionallyProcess(object, "PREFEAT", "1,Dodge");
		List<Prerequisite> list = object.getPrerequisiteList();
		assertEquals(2, list.size());

		context.unconditionallyProcess(object, "PRE", Constants.LST_DOT_CLEAR);
		List<Prerequisite> prerequisiteList = object.getPrerequisiteList();
		assertNotNull(prerequisiteList,
				"Prereq list should never be null as it is used in foreach loops directly.");
		assertTrue(prerequisiteList.isEmpty(), "Prereqlist should be empty after the clear");
	}
}
