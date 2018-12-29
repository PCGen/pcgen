/*
 * Copyright 2008 (C) James Dempsey
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

package plugin.lsttokens.gamemode.abilitycategory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadValidator;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.token.CDOMToken;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.Before;
import org.junit.Test;

/**
 * The Class {@code AbilityListTokenTest} verifies the processing of the
 * AbilityListToken.
 */
public class AbilityListTokenTest
{
	private RuntimeLoadContext context;

	@Before
	public void setUp() throws Exception
	{
		context = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
				new ConsolidatedListCommitStrategy());
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
	}

	private static void assertContains(AbilityCategory cat, Ability ab, boolean expected)
	{
		String key = ab.getKeyName();
		final Collection<CDOMSingleRef<Ability>> refs = cat.getAbilityRefs();
		boolean found = refs.stream()
		                    .anyMatch(ref -> ref.getLSTformat(false).equals(key));
		assertEquals(key + " in the list (" + expected + ") incorrect",
				expected, found
		);
	}
	
	/**
	 * Test a single entry is parsed correctly
	 */
	@Test
	public void testSingleEntry()
	{
		AbilityCategory aCat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "TestCat");
		aCat.setAbilityCategory(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()));
		assertFalse(
				"Test category should start with an empty list of keys",
				aCat.hasDirectReferences()
		);
		assertEquals("Test category should start with an empty list of keys",
				0, aCat.getAbilityRefs().size()
		);

		CDOMToken<AbilityCategory> token = new AbilityListToken();
		Ability track = BuildUtilities.buildFeat(context, "Track");
		token.parseToken(context, aCat, "Track");
		assertEquals("Test category should now have 1 key", 1, aCat
				.getAbilityRefs().size());
		assertContains(aCat, track, true);
	}

	/**
	 * Test that multiple entries are parsed correctly.
	 */
	@Test
	public void testMultipleEntries()
	{
		AbilityCategory aCat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "TestCat");
		aCat.setAbilityCategory(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()));
		assertFalse(
				"Test category should start with an empty list of keys",
				aCat.hasDirectReferences()
		);
		assertEquals("Test category should start with an empty list of keys",
				0, aCat.getAbilityRefs().size()
		);

		AbilityListToken token = new AbilityListToken();
		Ability track = BuildUtilities.buildFeat(context, "Track");
		Ability pbs = BuildUtilities.buildFeat(context, "Point Blank Shot");
		Ability pa = BuildUtilities.buildFeat(context, "Power Attack");
		token.parseToken(context, aCat, "Track|Point Blank Shot");
		assertEquals("Test category should now have 2 keys", 2, aCat
				.getAbilityRefs().size());
		assertContains(aCat, track, true);
		assertContains(aCat, pbs, true);
		assertContains(aCat, pa, false);
	}
	
	/**
	 * Test that entries with associated choices are parsed correctly
	 */
	@Test
	public void testEntriesWithAssoc()
	{
		AbilityCategory aCat = context.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "TestCat");
		aCat.setAbilityCategory(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()));
		assertThat(
				"Test category should start with an empty list of keys",
				aCat.hasDirectReferences(),
				is(false)
		);
		assertThat("Test category should start with an empty list of keys", aCat.getAbilityRefs().size(), is(0));

		AbilityListToken token = new AbilityListToken();
		Ability pbs = BuildUtilities.buildFeat(context, "Point Blank Shot");
		Ability sf = BuildUtilities.buildFeat(context, "Skill Focus");
		token.parseToken(context, aCat, "Point Blank Shot|Skill Focus (Ride)|Skill Focus (Bluff)");
		assertThat("Test category should now have 3 keys", aCat
				.getAbilityRefs().size(), is(3));
		assertContains(aCat, pbs, true);
		assertContains(aCat, sf, false); //Because this tests LST format
		context.getReferenceContext().validate(new LoadValidator(new ArrayList<>()));
		assertTrue(context.getReferenceContext().resolveReferences(null));
		Collection<CDOMSingleRef<Ability>> refs = aCat.getAbilityRefs();
		boolean pointBlankShot = refs.stream().anyMatch(ref -> ref.contains(pbs));
		assertTrue("Expected Point Blank Shot Ability", pointBlankShot);
		boolean skillFocus = refs.stream().anyMatch(ref -> ref.contains(sf));
		assertTrue("Expected Skill Focus Ability", skillFocus);
	}
}
