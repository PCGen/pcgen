/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.testsupport;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;

public abstract class AbstractCampaignTokenTestCase extends
		AbstractCDOMTokenTestCase<Campaign>
{

	static CDOMTokenLoader<Campaign> loader =
			new CDOMTokenLoader<Campaign>();

	public abstract ListKey<?> getListKey();

	public abstract boolean allowIncludeExclude();
	
	public Character getSeparator()
	{
		return null;
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		URI uri = new URI("http://www.sourceforge.net");
		primaryContext.setSourceURI(uri);
		secondaryContext.setSourceURI(uri);
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(""));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNull() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(null));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEndPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStartPipe() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("|String"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwo() throws PersistenceLayerException
	{
		if (getSeparator() == null)
		{
			Assert.assertFalse(parse("String|Other"));
			Assert.assertFalse(primaryProf.containsListFor(getListKey()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputIncludeNoParen()
			throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|INCLUDE:Incl"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputIncludeDoubleParen()
			throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|((INCLUDE:Incl))"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyInclude() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|(INCLUDE:)"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMixedInclude() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|(INCLUDE:This|CATEGORY=Cat,That)"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

		@Test
	public void testInvalidInputExcludeNoParen()
			throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|EXCLUDE:Incl"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputExcludeeDoubleParen()
			throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|((EXCLUDE:Incl))"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyExclude() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|(EXCLUDE:)"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMixedExclude() throws PersistenceLayerException
	{
		Assert.assertFalse(parse("String|(EXCLUDE:This|CATEGORY=Cat,That)"));
		Assert.assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	public void testInvalidInclude() throws PersistenceLayerException
	{
		if (!allowIncludeExclude())
		{
			Assert.assertFalse(parse("@TestWP1|(INCLUDE:ARing|BItem)"));
		}
	}

	public void testInvalidExclude() throws PersistenceLayerException
	{
		if (!allowIncludeExclude())
		{
			Assert.assertFalse(parse("@TestWP1|(EXCLUDE:ARing|BItem)"));
		}
	}

	/*
	 * TODO Need to be able to catch this - but can't today.
	 */
	//	public void testInvalidBothIncludeExclude()
	//		throws PersistenceLayerException
	//	{
	//		assertFalse(parse("@TestWP1|(INCLUDE:ARing|BItem)|(EXCLUDE:CRing)"));
	//	}
	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("@TestWP1");
	}

	@Test
	public void testRoundRobinInclude() throws PersistenceLayerException
	{
		if (allowIncludeExclude())
		{
			runRoundRobin("@TestWP1|(INCLUDE:ARing|BItem)");
		}
	}

	@Test
	public void testRoundRobinExclude() throws PersistenceLayerException
	{
		if (allowIncludeExclude())
		{
			runRoundRobin("@TestWP1|(EXCLUDE:ARing|BItem)");
		}
	}

	@Test
	public void testRoundRobinIncludeCategory()
		throws PersistenceLayerException
	{
		if (allowIncludeExclude())
		{
			runRoundRobin("@TestWP1|(INCLUDE:CATEGORY=FEAT,ARing,BItem)");
		}
	}

	@Test
	public void testRoundRobinExcludeCategory()
		throws PersistenceLayerException
	{
		if (allowIncludeExclude())
		{
			runRoundRobin("@TestWP1|(EXCLUDE:CATEGORY=FEAT,ARing,BItem)");
		}
	}

	@Test
	public void testRoundRobinIncludeTwoCategory()
		throws PersistenceLayerException
	{
		if (allowIncludeExclude())
		{
			runRoundRobin("TestWP1|(INCLUDE:CATEGORY=FEAT,ARing,BItem|CATEGORY=Mutation,Weird)");
		}
	}

	@Test
	public void testRoundRobinExcludeTwoCategory()
		throws PersistenceLayerException
	{
		if (allowIncludeExclude())
		{
			runRoundRobin("TestWP1|(EXCLUDE:CATEGORY=FEAT,ARing,BItem|CATEGORY=Mutation,Weird)");
		}
	}

	@Override
	public Class<Campaign> getCDOMClass()
	{
		return Campaign.class;
	}

	@Override
	public CDOMLoader<Campaign> getLoader()
	{
		return loader;
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Direct";
	}

	@Override
	protected String getLegalValue()
	{
		return "@TestWP1";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
