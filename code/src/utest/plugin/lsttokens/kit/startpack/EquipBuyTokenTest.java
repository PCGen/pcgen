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
package plugin.lsttokens.kit.startpack;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.core.QualifiedObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractFormulaTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EquipBuyTokenTest extends AbstractFormulaTokenTestCase<Kit>
{

    PreClassParser preclass = new PreClassParser();
    PreClassWriter preclasswriter = new PreClassWriter();
    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preclass);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
    }

    static EquipBuyToken token = new EquipBuyToken();
    static CDOMTokenLoader<Kit> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Kit> getCDOMClass()
    {
        return Kit.class;
    }

    @Override
    public CDOMLoader<Kit> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Kit> getToken()
    {
        return token;
    }

    @Override
    public FormulaKey getFormulaKey()
    {
        return null;
    }

    @Override
    protected Formula getFormula()
    {
        QualifiedObject<Formula> qo = primaryProf.get(ObjectKey.EQUIP_BUY);
        if (qo != null)
        {
            return qo.getRawObject();
        }
        return null;
    }

    @Test
    public void testInvalidInputOnlyPre()
    {
        try
        {
            assertFalse(parse("PRECLASS:1,Fighter=1"));
        } catch (IllegalArgumentException e)
        {
            // this is okay too :)
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmbeddedPre()
    {
        assertFalse(parse("TestWP1|PRECLASS:1,Fighter=1|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipePre()
    {
        assertFalse(parse("TestWP1||PRECLASS:1,Fighter=1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputPostPrePipe()
    {
        assertFalse(parse("TestWP1|PRECLASS:1,Fighter=1|"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("TestWP1|PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinFormulaComplex() throws PersistenceLayerException
    {
        runRoundRobin("if(var(\"SIZE==3||SIZE==4\"),5,10)|PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinTwoPre() throws PersistenceLayerException
    {
        runRoundRobin("TestWP1|!PRERACE:1,Human|PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinNotPre() throws PersistenceLayerException
    {
        runRoundRobin("TestWP1|!PRECLASS:1,Fighter=1");
    }

    @Override
    protected String[] setAndUnparse(Formula val)
    {
        primaryProf.put(ObjectKey.EQUIP_BUY, new QualifiedObject<>(val));
        return getToken().unparse(primaryContext, primaryProf);
    }

}
