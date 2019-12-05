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

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AutoLst;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractAutoTokenTestCase<TC extends CDOMObject> extends
        AbstractSelectionTokenTestCase<CDOMObject, TC>
{
    static AutoLst token = new AutoLst();
    static CDOMTokenLoader<CDOMObject> loader =
            new CDOMTokenLoader<>();
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
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    public boolean isTypeLegal()
    {
        return true;
    }

    @Override
    public boolean allowsParenAsSub()
    {
        return false;
    }

    @Override
    public boolean allowsFormula()
    {
        return false;
    }

    protected abstract void loadAllReference();

    protected abstract ChooseSelectionActor<TC> getActor();

    protected abstract void loadProf(CDOMSingleRef<TC> ref);

    protected abstract void loadTypeProf(String... types);

    protected abstract boolean allowsPrerequisite();

    @Test
    public void testInvalidEmptyPre()
    {
        construct(primaryContext, "TestWP1");
        boolean parse = parse(getSubTokenName() + '|' + "TestWP1[]");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidEmptyPre2()
    {
        construct(primaryContext, "TestWP1");
        boolean parse = parse(getSubTokenName() + '|' + "TestWP1[");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidEmptyPre3()
    {
        construct(primaryContext, "TestWP1");
        boolean parse = parse(getSubTokenName() + '|' + "TestWP1]");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidMismatchedBracket()
    {
        construct(primaryContext, "TestWP1");
        boolean parse =
                parse(getSubTokenName() + '|' + "TestWP1[PRERACE:Dwarf");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidTrailingAfterBracket()
    {
        construct(primaryContext, "TestWP1");
        boolean parse =
                parse(getSubTokenName() + '|' + "TestWP1[PRERACE:Dwarf]Hi");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinOnePre() throws PersistenceLayerException
    {
        if (allowsPrerequisite())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            runRoundRobin(getSubTokenName() + '|' + "TestWP1|PRERACE:1,Dwarf");
        }
    }

    @Test
    public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
    {
        if (allowsPrerequisite())
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            construct(secondaryContext, "TestWP1");
            construct(secondaryContext, "TestWP2");
            runRoundRobin(getSubTokenName() + '|' + "TestWP1|PRERACE:1,Dwarf",
                    getSubTokenName() + '|' + "TestWP1|PRERACE:1,Human");
        }
    }

    @Test
    public void testRoundRobinAllPlusWithPrereqLegal()
            throws PersistenceLayerException
    {
        if (isAllLegal() && allowsPrerequisite())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "ALL|PRERACE:1,Dwarf",
                    getSubTokenName() + '|' + "TestWP1|PRERACE:1,Human");
        }
    }

    @Test
    public void testRoundRobinAllIndivPrereq()
    {
        if (allowsPrerequisite())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            assertFalse(parse(getSubTokenName() + '|'
                    + "TestWP1|ALL[PRERACE:Dwarf]"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinAllDoublePrereq()
            throws PersistenceLayerException
    {
        if (isAllLegal() && allowsPrerequisite())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            runRoundRobin(getSubTokenName() + '|' + "ALL|PRERACE:1,Dwarf",
                    getSubTokenName() + '|' + "ALL|PRERACE:1,Human");
        }
    }

    @Test
    public void testInvalidAllPlusAllPrereqIllegal()
    {
        if (allowsPrerequisite())
        {
            assertFalse(parse(getSubTokenName() + '|' + "ALL|PRERACE:Dwarf|ALL"));
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidAllPlusListIllegal()
    {
        if (isAllLegal())
        {
            assertFalse(parse(getSubTokenName() + '|' + "%LIST|ALL"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputBadPrerequisite()
    {
        if (allowsPrerequisite())
        {
            construct(primaryContext, "TestWP1");
            construct(secondaryContext, "TestWP1");
            boolean parse = parse(getSubTokenName() + '|' + "TestWP1|PREFOO:1,Human");
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        }
    }

    @Test
    public void testUnparseSingle()
    {
        TC wp1 = construct(primaryContext, "TestWP1");
        CDOMSingleRef<TC> ref = CDOMDirectSingleRef.getRef(wp1);
        loadProf(ref);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "TestWP1");
    }

    @Test
    public void testUnparseType()
    {
        loadTypeProf("Foo", "Bar");
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + getTypePrefix()
                + "TYPE=Bar.Foo");
    }

    @Test
    public void testUnparseSingleAll()
    {
        if (isAllLegal())
        {
            TC wp1 = construct(primaryContext, "TestWP1");
            CDOMSingleRef<TC> ref = CDOMDirectSingleRef.getRef(wp1);
            loadProf(ref);
            loadAllReference();
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseAll()
    {
        if (isAllLegal())
        {
            loadAllReference();
            String[] unparsed = getToken().unparse(primaryContext, primaryProf);
            expectSingle(unparsed, getSubTokenName() + '|' + "ALL");
        }
    }

    @Test
    public void testRoundRobinList() throws PersistenceLayerException
    {
        runRoundRobin(getSubTokenName() + '|' + "%LIST");
    }

    @Test
    public void testRoundRobinListPre() throws PersistenceLayerException
    {
        if (allowsPrerequisite())
        {
            runRoundRobin(getSubTokenName() + '|' + "%LIST|PRERACE:1,Dwarf");
        }
    }

    @Test
    public void testUnparseListAll()
    {
        if (isAllLegal())
        {
            primaryProf.addToListFor(ListKey.NEW_CHOOSE_ACTOR, getActor());
            loadAllReference();
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseTypeAll()
    {
        if (isAllLegal())
        {
            loadTypeProf("Foo", "Bar");
            loadAllReference();
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseList()
    {
        primaryProf.addToListFor(ListKey.NEW_CHOOSE_ACTOR, getActor());
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "%LIST");
    }
}
