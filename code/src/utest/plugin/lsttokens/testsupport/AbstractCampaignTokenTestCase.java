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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URI;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;

import org.junit.jupiter.api.Test;
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
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNull()
    {
        assertFalse(parse(null));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEndPipe()
    {
        assertFalse(parse("String|"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStartPipe()
    {
        assertFalse(parse("|String"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwo()
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
    {
        assertFalse(parse("String|INCLUDE:Incl"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputIncludeDoubleParen()
    {
        assertFalse(parse("String|((INCLUDE:Incl))"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyInclude()
    {
        assertFalse(parse("String|(INCLUDE:)"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMixedInclude()
    {
        assertFalse(parse("String|(INCLUDE:This|CATEGORY=Cat,That)"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputExcludeNoParen()
    {
        assertFalse(parse("String|EXCLUDE:Incl"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputExcludeeDoubleParen()
    {
        assertFalse(parse("String|((EXCLUDE:Incl))"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyExclude()
    {
        assertFalse(parse("String|(EXCLUDE:)"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMixedExclude()
    {
        assertFalse(parse("String|(EXCLUDE:This|CATEGORY=Cat,That)"));
        assertFalse(primaryProf.containsListFor(getListKey()));
        assertNoSideEffects();
    }

    public void testInvalidInclude()
    {
        if (!allowIncludeExclude())
        {
            assertFalse(parse("@TestWP1|(INCLUDE:ARing|BItem)"));
        }
    }

    public void testInvalidExclude()
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
