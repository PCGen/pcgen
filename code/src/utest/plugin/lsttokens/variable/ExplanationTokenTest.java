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
package plugin.lsttokens.variable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.cdom.content.DatasetVariable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ExplanationTokenTest extends
        AbstractTokenTestCase<DatasetVariable>
{

    private static ExplanationToken token = new ExplanationToken();
    private static CDOMTokenLoader<DatasetVariable> loader =
            new CDOMTokenLoader<>();

    @Override
    public CDOMPrimaryToken<DatasetVariable> getToken()
    {
        return token;
    }

    @Override
    public Class<DatasetVariable> getCDOMClass()
    {
        return DatasetVariable.class;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Override
    public CDOMLoader<DatasetVariable> getLoader()
    {
        return loader;
    }

    @Test
    public void testDisplayNameRequired()
    {
        DatasetVariable dv = new DatasetVariable();
        ParseResult pr = token.parseToken(primaryContext, dv, "Try Me!");
        assertFalse(pr.passed());
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        runRoundRobin("This does something, really!");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Alternate Explanation!";
    }

    @Override
    protected String getLegalValue()
    {
        return "Explanation";
    }

    @Override
    public void isCDOMEqual(DatasetVariable dv1, DatasetVariable dv2)
    {
        assertEquals(dv1.getDisplayName(), dv2.getDisplayName(), () -> "Display Name not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getFormat(), dv2.getFormat(), () -> "Format not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getScope(), dv2.getScope(), () -> "Scope Name not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getSourceURI(), dv2.getSourceURI(), () -> "Source URI not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getExplanation(), dv2.getExplanation(), () -> "Explanation not equal " + dv1 + " and " + dv2);
    }
}
