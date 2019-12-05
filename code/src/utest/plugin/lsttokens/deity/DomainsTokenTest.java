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
package plugin.lsttokens.deity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URISyntaxException;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListContextTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DomainsTokenTest extends
        AbstractListContextTokenTestCase<Deity, Domain>
{
    static DomainsToken token = new DomainsToken();
    static CDOMTokenLoader<Deity> loader = new CDOMTokenLoader<>();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(new PreLevelParser());
        TokenRegistration.register(new PreClassParser());
        TokenRegistration.register(new PreLevelWriter());
        TokenRegistration.register(new PreClassWriter());
    }

    @Override
    public char getJoinCharacter()
    {
        return ',';
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
        return true;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return true;
    }

    @Override
    public boolean isClearLegal()
    {
        return true;
    }

    @Override
    public Class<Deity> getCDOMClass()
    {
        return Deity.class;
    }

    @Override
    public CDOMLoader<Deity> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Deity> getToken()
    {
        return token;
    }

    @Override
    protected Domain construct(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(Domain.class, one);
    }

    @Test
    public void testInvalidClearDotPre()
    {
        assertFalse(parse(".CLEAR.TestWP1|PRELEVEL:MIN=4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidClearPre()
    {
        assertFalse(parse(".CLEAR|PRELEVEL:MIN=4"));
        assertNoSideEffects();
    }

    @Override
    public void testRoundRobinTestAll() throws PersistenceLayerException
    {
        construct(primaryContext, "Foo");
        construct(secondaryContext, "Foo");
        runRoundRobin("ANY");
    }

    @Test
    public void testInvalidOnlyPre()
    {
        assertFalse(parse("!PRELEVEL:3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmbeddedNotPre()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1|!PRELEVEL:3|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadPre()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1,TestWP2|PREFOO:3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNotBadPre()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1,TestWP2|!PREFOO:3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmbeddedPre()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1|PRELEVEL:4|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1,TestWP2|PRELEVEL:MIN=5");
    }

    @Test
    public void testRoundRobinBasic() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1,TestWP2");
    }

    @Test
    public void testRoundRobinNotPre() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1,TestWP2|!PRELEVEL:MIN=5");
    }

    @Test
    public void testRoundRobinDoublePre() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1,TestWP2|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
    }

    @Test
    public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("TestWP1|PRECLASS:1,Fighter=1",
                "TestWP1|PRECLASS:1,Wizard=1");
    }

    @Test
    public void testRoundRobinTwoPrereqs() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1|PRECLASS:1,Fighter=1",
                "TestWP2|PRECLASS:1,Wizard=1");
    }

    @Test
    public void testRoundRobinUnparseDirect()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        AssociatedPrereqObject apo = new SimpleAssociatedObject();
        apo.setAssociation(AssociationKey.TOKEN, "DOMAINS");
        primaryProf.putToList(Deity.DOMAINLIST, CDOMDirectSingleRef
                .getRef(primaryContext.getReferenceContext().silentlyGetConstructedCDOMObject(
                        getTargetClass(), "TestWP1")), apo);
        primaryProf.putToList(Deity.DOMAINLIST, CDOMDirectSingleRef
                .getRef(primaryContext.getReferenceContext().silentlyGetConstructedCDOMObject(
                        getTargetClass(), "TestWP2")), apo);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        assertNotNull(unparsed);
        assertEquals(1, unparsed.length);
        assertEquals("TestWP1,TestWP2", unparsed[0]);
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Override
    protected CDOMReference<? extends CDOMList<? extends PrereqObject>> getListReference()
    {
        return Deity.DOMAINLIST;
    }
}
