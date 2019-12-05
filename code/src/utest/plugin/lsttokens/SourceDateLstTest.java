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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class SourceDateLstTest extends AbstractGlobalTokenTestCase
{

    static SourcedateLst token = new SourcedateLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    public ObjectKey<Date> getKey()
    {
        return ObjectKey.SOURCE_DATE;
    }

    @Test
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNull(primaryProf.get(getKey()));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("2011-09"));
        assertEquals("September 1, 2011", token.unparse(primaryContext, primaryProf)[0]);
        assertTrue(parse("January 24, 2010"));
        assertEquals("January 24, 2010", token.unparse(primaryContext, primaryProf)[0]);
    }

//	@Test
//	public void testRoundRobinBase() throws PersistenceLayerException {
//		runRoundRobin("2011-09");
//	}

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        runRoundRobin("January 24, 2010");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "September 1, 2011";
    }

    @Override
    protected String getLegalValue()
    {
        return "January 24, 2010";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getKey(), null);
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));
    }

//	/*
//	 * TODO Need to define the appropriate behavior here - is the token
//	 * responsible for catching this?
//	 */
//	// @Test
//	// public void testUnparseEmpty() throws PersistenceLayerException
//	// {
//	// primaryProf.put(getStringKey(), "");
//	// assertNull(getToken().unparse(primaryContext, primaryProf));
//	// }
//
//	protected String[] setAndUnparse(Date val) {
//		primaryProf.put(getKey(), val);
//		return getToken().unparse(primaryContext, primaryProf);
//	}

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }
}
