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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.qualifier.pobject.QualifiedToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractPrimitiveTokenTestCase<T extends CDOMObject, TC extends Loadable>
        extends AbstractCDOMTokenTestCase<T>
{

    private static QualifierToken<CDOMObject> qual = new QualifiedToken<>();

    public abstract CDOMSecondaryToken<?> getSubToken();

    private final String prim;
    private final String target;
    private final String good;

    protected AbstractPrimitiveTokenTestCase(String primitive, String tgt)
    {
        prim = primitive;
        target = tgt;
        good = (target == null) ? prim : (prim + "=" + target);
    }

    public String getSubTokenName()
    {
        return getSubToken().getTokenName();
    }

    public abstract Class<TC> getTargetClass();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(getSubToken());
        TokenRegistration.register(qual);
    }

    protected void construct(LoadContext loadContext, String one)
    {
        loadContext.getReferenceContext().constructCDOMObject(getTargetClass(), one);
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return getSubTokenName() + '|' + "QUALIFIED[" + good + "]";
    }

    @Override
    protected String getLegalValue()
    {
        return getSubTokenName() + '|' + good;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testPrimitiveEquals()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + prim + "=]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitivePipe()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + good + "|]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveComma()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + good + ",]"));
        assertNoSideEffects();
    }

    @Test
    public void testPipePrimitive()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[|" + good + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testCommaPrimitive()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[," + good + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveDoublePipe()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP2||"
                + good + "]]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveDoubleComma()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=Foo,,"
                + good + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveAll1()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[ALL|" + good
                + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveAll2()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + good
                + "|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testTypePrimitiveBadSyntax()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=Foo]"
                + good));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveTypeBadSyntax()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + good
                + "]TYPE=Foo"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveTypePipeBadSyntax()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + good
                + "]TYPE=Foo|TYPE=Bar"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveTypeCommaBadSyntax()
    {
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=Foo]"
                + good + ",TYPE=Bar"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveUsedAsQualifier()
    {
        assertFalse(parse(getSubTokenName() + '|' + good + "[" + good + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testDotPrimitive()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        boolean ret = parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1." + good
                + "]");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testGoodBadNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|'
                + "QUALIFIED[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP3||"
                + good + "]"));
        assertNoSideEffects();
    }

    @Test
    public void testAllNoSideEffect()
    {
        // Test with All
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1|TestWP2]"));
        assertTrue(parseSecondary(getSubTokenName() + '|'
                + "QUALIFIED[TestWP1|TestWP2]"));
        assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[" + good
                + "|ALL]"));
        assertNoSideEffects();
    }

    @Test
    public void testPrimitiveGood() throws PersistenceLayerException
    {
        if (target != null)
        {
            construct(primaryContext, target);
            construct(secondaryContext, target);
        }
        runRoundRobin(getSubTokenName() + '|' + good);
    }

    @Test
    public void testQualifiedPrimitiveGood() throws PersistenceLayerException
    {
        if (target != null)
        {
            construct(primaryContext, target);
            construct(secondaryContext, target);
        }
        runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[" + good + "]");
    }

    protected void doPrimitiveIllegalTarget(String illegal)
    {
        String primitive = prim;
        if (illegal != null)
        {
            primitive += "=" + illegal;
        }
        boolean parse = parse(getSubTokenName() + '|' + "QUALIFIED["
                + primitive + "]");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

}
