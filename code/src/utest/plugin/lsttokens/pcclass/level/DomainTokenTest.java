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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class DomainTokenTest extends AbstractListInputTokenTestCase<PCClassLevel, Domain>
{

    static DomainToken token = new DomainToken();
    static CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<>();

    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @Override
    @BeforeEach
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
        return true;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Test
    public void testInvalidEmptyPre()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTrailingAfterPre()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1|PRERACE:Dwarf|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOnePre() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("TestWP1|PRERACE:1,Dwarf");
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
        runRoundRobin("TestWP1|PRERACE:1,Dwarf", "TestWP2|PRERACE:1,Human",
                "TestWP3");
    }

    @Test
    public void testInvalidInputBadPrerequisite()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        assertFalse(parse("TestWP1|PREFOO:1,Human"));
        assertNoSideEffects();
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.DOMAIN);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        Domain wp1 = construct(primaryContext, "TestWP1");
        primaryProf.addToListFor(ListKey.DOMAIN, buildQO(wp1));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue());
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(ListKey.DOMAIN, null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseMultiple()
    {
        Domain wp1 = construct(primaryContext, getLegalValue());
        primaryProf.addToListFor(ListKey.DOMAIN, buildQO(wp1));
        Domain wp2 = construct(primaryContext, getAlternateLegalValue());
        primaryProf.addToListFor(ListKey.DOMAIN, buildQO(wp2));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue() + getJoinCharacter()
                + getAlternateLegalValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = ListKey.DOMAIN;
        primaryProf.addToListFor(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseSinglePre() throws PersistenceLayerException
    {
        Domain wp1 = construct(primaryContext, "TestWP1");
        CDOMDirectSingleRef<Domain> ref = CDOMDirectSingleRef.getRef(wp1);
        PreParserFactory prereqParser = PreParserFactory.getInstance();
        Prerequisite prereq = prereqParser.parse("PRERACE:1,Dwarf");
        assertNotNull(prereq);
        QualifiedObject<CDOMSingleRef<Domain>> qo = new QualifiedObject<>(
                ref, prereq);
        primaryProf.addToListFor(ListKey.DOMAIN, qo);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue() + "|PRERACE:1,Dwarf");
    }

    private static QualifiedObject<CDOMSingleRef<Domain>> buildQO(Domain wp1)
    {
        return new QualifiedObject<>(CDOMDirectSingleRef
                .getRef(wp1));
    }

}
