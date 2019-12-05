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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.bonustokens.MonNonSkillHD;
import plugin.bonustokens.MonSkillPts;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelMaxParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonNonSkillTHDTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

    static MonnonskillhdToken token = new MonnonskillhdToken();
    static CDOMTokenLoader<PCClass> loader =
            new CDOMTokenLoader<>();

    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        addBonus(MonNonSkillHD.class);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
    }

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmpty()
    {
        try
        {
            assertFalse(parse(""));
        } catch (IllegalArgumentException e)
        {
            // This is Okay too :)
        }
        assertNoSideEffects();
    }

    @Test
    public void testOnlyPre()
    {
        assertFalse(parse("PRERACE:1,Human"));
        assertNoSideEffects();
    }

    @Test
    public void testOtherBonus() throws PersistenceLayerException
    {
        addBonus(MonSkillPts.class);
        MonskillToken monskill = new MonskillToken();
        TokenRegistration.register(new PreLevelMaxParser());
        TokenRegistration.register(monskill);
        assertTrue(monskill.parseToken(primaryContext, primaryProf, "1").passed());
        primaryContext.commit();
        assertNull(token.unparse(primaryContext, primaryProf));
        assertNotNull(primaryContext.unparse(primaryProf));
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1");
    }

    @Test
    public void testRoundRobinNumber() throws PersistenceLayerException
    {
        runRoundRobin("3");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("3+CL(\"FIGHTER\")");
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1|PRERACE:1,HUMAN");
    }

    @Test
    public void testRoundRobinDupePre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1", "VARIABLE1|PRERACE:1,HUMAN");
    }

    @Test
    public void testRoundRobinDiffPre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1|PRERACE:1,DWARF", "VARIABLE1|PRERACE:1,HUMAN");
    }

    @Test
    public void testRoundRobinDiffSamePre() throws PersistenceLayerException
    {
        runRoundRobin("VARIABLE1|PRERACE:1,HUMAN", "VARIABLE2|PRERACE:1,HUMAN");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "VARIABLE2";
    }

    @Override
    protected String getLegalValue()
    {
        return "VARIABLE1|PRERACE:1,HUMAN";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Test
    public void testUnparseOne()
    {
        expectSingle(setAndUnparse(1), Integer.toString(1));
    }

    @Test
    public void testUnparseOnePrereq() throws PersistenceLayerException
    {
        BonusObj bonus = getBonus(1);
        PreParserFactory prereqParser = PreParserFactory.getInstance();
        Prerequisite prereq = prereqParser.parse("PRERACE:1,Dwarf");
        assertNotNull(prereq);
        bonus.addPrerequisite(prereq);
        primaryProf.addToListFor(ListKey.BONUS, bonus);
        String[] sap = getToken().unparse(primaryContext, primaryProf);
        expectSingle(sap, "1|PRERACE:1,Dwarf");
    }

    // TODO Probably want to implement these? But needs deprecation warning
    // before these can be turned on
    // @Test
    // public void testUnparseZero() throws PersistenceLayerException
    // {
    // primaryProf.addToListFor(ListKey.BONUS, getBonus(0));
    // assertNull(getToken().unparse(primaryContext, primaryProf));
    // }
    //
    // @Test
    // public void testUnparseNegative() throws PersistenceLayerException
    // {
    // primaryProf.addToListFor(ListKey.BONUS, getBonus(-3));
    // assertNull(getToken().unparse(primaryContext, primaryProf));
    // }

    @Test
    public void testUnparseNull()
    {
        primaryProf.addToListFor(ListKey.BONUS, null);
        try
        {
            assertNull(getToken().unparse(primaryContext, primaryProf));
        } catch (NullPointerException e)
        {
            // This is okay too
        }
    }

    protected String[] setAndUnparse(int val)
    {
        primaryProf.addToListFor(ListKey.BONUS, getBonus(val));
        return getToken().unparse(primaryContext, primaryProf);
    }

    private BonusObj getBonus(int bonusValue)
    {
        BonusObj bon = Bonus.newBonus(primaryContext, "MONNONSKILLHD|NUMBER|" + bonusValue);
        assertNotNull(bon);
        bon.setTokenSource(token.getTokenName());
        return bon;
    }
}
