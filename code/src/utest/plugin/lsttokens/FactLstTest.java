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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.base.format.StringManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FactLstTest extends AbstractGlobalTokenTestCase
{
    private static FactLst token = new FactLst();
    private static CDOMTokenLoader<Domain> loader = new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        TokenRegistration.clearTokens();
        super.setUp();
    }

    @Override
    public Class<Domain> getCDOMClass()
    {
        return Domain.class;
    }

    @Override
    public CDOMLoader<Domain> getLoader()
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

    @Test
    public void testInvalidInputOnlyNumber()
    {
        assertFalse(parse("Possibility"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotAFact()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("NaN|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoTarget()
    {
        assertFalse(parse("Possibility|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoFact()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoublePipe()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("Possibility||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStrange()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("|TestWP1|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEnd()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("Possibility|TestWP1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTooManyPipes()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("Possibility|TestWP1|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleJoin()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("Possibility||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputs()
    {
        construct(primaryContext, "TestWP1");
        assertTrue(parse("Possibility|TestWP1"));
        assertCleanConstruction();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("Possibility|TestWP1");
    }

    protected void construct(LoadContext loadContext, String one)
    {
        loadContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, one);
    }

    @Override
    protected String getLegalValue()
    {
        return "Possibility|TestWP1";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Possibility|TestWP2";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Override
    protected void additionalSetup(LoadContext context)
    {
        super.additionalSetup(context);
        FactDefinition fd = new FactDefinition();
        fd.setName("DOMAIN.Possibility");
        fd.setFactName("Possibility");
        fd.setUsableLocation(Domain.class);
        fd.setFormatManager(new StringManager());
        fd.setVisibility(Visibility.HIDDEN);
        context.getReferenceContext().importObject(fd);
        SourceFileLoader.processFactDefinitions(context);
    }


}
