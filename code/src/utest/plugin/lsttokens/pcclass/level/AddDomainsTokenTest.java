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
package plugin.lsttokens.pcclass.level;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListContextTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddDomainsTokenTest extends
        AbstractListContextTokenTestCase<PCClassLevel, Domain>
{

    static AdddomainsToken token = new AdddomainsToken();
    static CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<>();

    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
    }

    @Override
    protected PCClassLevel get(LoadContext context, String name)
    {
        PCClass pcc = context.getReferenceContext().constructNowIfNecessary(PCClass.class,
                "Cl");
        return pcc.getOriginalClassLevel(1);
    }

    @Override
    public Class<PCClassLevel> getCDOMClass()
    {
        return PCClassLevel.class;
    }

    @Override
    public CDOMLoader<PCClassLevel> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClassLevel> getToken()
    {
        return token;
    }

    @Override
    public Class<Domain> getTargetClass()
    {
        return Domain.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public char getJoinCharacter()
    {
        return '.';
    }

    @Test
    public void testInvalidEmptyPre()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1[]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyPre2()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1["));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyPre3()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1]"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidMismatchedBracket()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1[PRERACE:Dwarf"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTrailingAfterBracket()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1[PRERACE:Dwarf]Hi"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOnePre() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1[PRERACE:1,Dwarf]");
    }

    @Test
    public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1[PRERACE:1,Dwarf].TestWP1[PRERACE:1,Human]");
    }

    @Test
    public void testRoundRobinThreeWithPre() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        runRoundRobin("TestWP1[PRERACE:1,Dwarf]" + getJoinCharacter()
                + "TestWP2[PRERACE:1,Human]" + getJoinCharacter() + "TestWP3");
    }

    @Test
    public void testInvalidInputBadPrerequisite()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        assertFalse(parse("TestWP1[PREFOO:1,Human]"));
        assertNoSideEffects();
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Override
    protected CDOMReference<? extends CDOMList<? extends PrereqObject>> getListReference()
    {
        return PCClass.ALLOWED_DOMAINS;
    }
}
