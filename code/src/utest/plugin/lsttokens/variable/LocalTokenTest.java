/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.content.DatasetVariable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class LocalTokenTest extends AbstractTokenTestCase<DatasetVariable>
{

    private static LocalToken token = new LocalToken();
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
    public void testDisplayNameProhibited()
    {
        DatasetVariable dv = new DatasetVariable();
        dv.setName("FirstName");
        ParseResult pr = token.parseToken(primaryContext, dv, "VarName");
        assertFalse(pr.passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadName()
    {
        assertFalse(parse("Bad-Name"));
        assertNoSideEffects();
    }

    @Test
    public void testValidBasic()
    {
        assertTrue(parse("PC.EQUIPMENT|IsANumberVar"));
    }

    @Test
    public void testValidFormatted()
    {
        assertTrue(parse("PC.EQUIPMENT|NUMBER=IsANumberVar"));
    }

    @Test
    public void testInvalidDoubleEqual()
    {
        assertFalse(parse("PC.EQUIPMENT|STRING==Pipe"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoublePipe()
    {
        assertFalse(parse("PC.EQUIPMENT||STRING=Pipe"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidThreeArgs()
    {
        assertFalse(parse("PC.EQUIPMENT|STRING|Pipe"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidThreeEqualArgs()
    {
        assertFalse(parse("PC.EQUIPMENT|STRING=Pipe=Too"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyEqual()
    {
        assertFalse(parse("PC.EQUIPMENT|="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyFormat()
    {
        assertFalse(parse("PC.EQUIPMENT|=Value"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyVarName()
    {
        assertFalse(parse("PC.EQUIPMENT|NUMBER="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadFormat()
    {
        assertFalse(parse("PC.EQUIPMENT|BADFORMAT=Illegal"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidName()
    {
        assertFalse(parse("PC.EQUIPMENT|NUMBER=Illegal Name!"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyScope()
    {
        assertFalse(parse("|STRING=Value"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadScope()
    {
        assertFalse(parse("BADSCOPE|STRING=Illegal"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinDefault() throws PersistenceLayerException
    {
        runRoundRobin("PC.EQUIPMENT|IsANumberVar");
    }

    @Test
    public void testRoundRobinFormatted() throws PersistenceLayerException
    {
        runRoundRobin("PC.EQUIPMENT|STRING=StringVar");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "PC.EQUIPMENT|Alt";
    }

    @Override
    protected String getLegalValue()
    {
        return "PC.EQUIPMENT|Var";
    }

    @Test
    public void testInvalidDupeVarName()
    {
        DatasetVariable dv = new DatasetVariable();
        ParseResult pr =
                token.parseToken(primaryContext, dv, "PC.EQUIPMENT|MyVar");
        assertTrue(pr.passed());
        assertFalse(parse("PC.EQUIPMENT|STRING=MyVar"));
        assertNoSideEffects();
    }

    @Override
    public void isCDOMEqual(DatasetVariable dv1, DatasetVariable dv2)
    {
        assertEquals(dv1.getDisplayName(), dv2.getDisplayName(), "Display Name not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getFormat(), dv2.getFormat(), () -> "Format not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getScope(), dv2.getScope(), () -> "Scope Name not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getSourceURI(), dv2.getSourceURI(), () -> "Source URI not equal " + dv1 + " and " + dv2);
        assertEquals(dv1.getExplanation(), dv2.getExplanation(), () -> "Explanation not equal " + dv1 + " and " + dv2);
    }

    @Override
    protected DatasetVariable get(LoadContext context, String name)
    {
        return new DatasetVariable();
    }

    @Override
    @Test
    public void testOverwrite()
    {
        assertTrue(parse(getLegalValue()));
        validateUnparsed(primaryContext, primaryProf, getLegalValue());
        assertFalse(parse(getAlternateLegalValue()));
    }

}
