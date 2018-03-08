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

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import util.TestURI;

public abstract class AbstractCampaignTokenTestCase extends
		AbstractCDOMTokenTestCase<Campaign>
{

	static CDOMTokenLoader<Campaign> loader =
			new CDOMTokenLoader<>();

	public abstract ListKey<?> getListKey();

	public abstract boolean allowIncludeExclude();
	
	public Character getSeparator()
	{
		return null;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNull() throws PersistenceLayerException
	{
		assertFalse(parse(null));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEndPipe() throws PersistenceLayerException
	{
		assertFalse(parse("String|"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStartPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|String"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwo() throws PersistenceLayerException
	{
		if (getSeparator() == null)
		{
			assertFalse(parse("String|Other"));
			assertFalse(primaryProf.containsListFor(getListKey()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputIncludeNoParen()
			throws PersistenceLayerException
	{
		assertFalse(parse("String|INCLUDE:Incl"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputIncludeDoubleParen()
			throws PersistenceLayerException
	{
		assertFalse(parse("String|((INCLUDE:Incl))"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyInclude() throws PersistenceLayerException
	{
		assertFalse(parse("String|(INCLUDE:)"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMixedInclude() throws PersistenceLayerException
	{
		assertFalse(parse("String|(INCLUDE:This|CATEGORY=Cat,That)"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

		@Test
	public void testInvalidInputExcludeNoParen()
			throws PersistenceLayerException
	{
		assertFalse(parse("String|EXCLUDE:Incl"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputExcludeeDoubleParen()
			throws PersistenceLayerException
	{
		assertFalse(parse("String|((EXCLUDE:Incl))"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyExclude() throws PersistenceLayerException
	{
		assertFalse(parse("String|(EXCLUDE:)"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMixedExclude() throws PersistenceLayerException
	{
		assertFalse(parse("String|(EXCLUDE:This|CATEGORY=Cat,That)"));
		assertFalse(primaryProf.containsListFor(getListKey()));
		assertNoSideEffects();
	}

	public void testInvalidInclude() throws PersistenceLayerException
	{
		if (!allowIncludeExclude())
		{
			assertFalse(parse("@TestWP1|(INCLUDE:ARing|BItem)"));
		}
	}

	public void testInvalidExclude() throws PersistenceLayerException
	{
		if (!allowIncludeExclude())
		{
			assertFalse(parse("@TestWP1|(EXCLUDE:ARing|BItem)"));
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

	@Override
	protected void additionalSetup(LoadContext context)
	{
		super.additionalSetup(context);
		URI uri = TestURI.getURI();
		context.setSourceURI(uri);
	}
}
