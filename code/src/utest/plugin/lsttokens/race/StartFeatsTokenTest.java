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
package plugin.lsttokens.race;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.bonustokens.Feat;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreHDParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreHDWriter;
import plugin.pretokens.writer.PreLevelWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StartFeatsTokenTest extends AbstractCDOMTokenTestCase<Race>
{

    PreHDParser prehd = new PreHDParser();
    PreHDWriter prehdwriter = new PreHDWriter();
    PreLevelParser prelevel = new PreLevelParser();
    PreLevelWriter prelevelwriter = new PreLevelWriter();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(prehd);
        TokenRegistration.register(prehdwriter);
        TokenRegistration.register(prelevel);
        TokenRegistration.register(prelevelwriter);
        TokenRegistration.register(Feat.class);
    }

    static StartfeatsToken token = new StartfeatsToken();
    static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<Race> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Race> getToken()
    {
        return token;
    }

    // TODO Probably want to implement these? But needs deprecation warning
    // before these can be turned on
    // @Test
    // public void testInvalidZero() throws PersistenceLayerException
    // {
    // assertFalse(token.parse(primaryContext, primaryProf, "0"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidNegative() throws PersistenceLayerException
    // {
    // assertFalse(token.parse(primaryContext, primaryProf, "-5"));
    // assertNoSideEffects();
    // }

    @Test
    public void testInvalidEquation()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "1+2").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidString()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "String").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDecimal()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "4.0").passed());
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        runRoundRobin("1");
    }

    @Test
    public void testRoundRobinOneTwice() throws PersistenceLayerException
    {
        runRoundRobin("1", "1");
    }

    @Test
    public void testRoundRobinFive() throws PersistenceLayerException
    {
        runRoundRobin("5");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "4";
    }

    @Override
    protected String getLegalValue()
    {
        return "3";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Test
    public void testUnparseOne() throws PersistenceLayerException
    {
        expectSingle(setAndUnparse(1), Integer.toString(1));
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
            //This is okay too
        }
    }

    protected String[] setAndUnparse(int val) throws PersistenceLayerException
    {
        primaryProf.addToListFor(ListKey.BONUS, getBonus(val));
        return getToken().unparse(primaryContext, primaryProf);
    }

    private BonusObj getBonus(int bonusValue) throws PersistenceLayerException
    {
        BonusObj bon = Bonus.newBonus(primaryContext, "FEAT|POOL|" + bonusValue);
        assertNotNull(bon);
        PreParserFactory prereqParser = PreParserFactory.getInstance();
        Prerequisite prereq = prereqParser
                .parse("PREMULT:1,[PREHD:MIN=1],[PRELEVEL:MIN=1]");
        assertNotNull(prereq);
        bon.addPrerequisite(prereq);
        bon.setTokenSource(token.getTokenName());
        return bon;
    }
}
