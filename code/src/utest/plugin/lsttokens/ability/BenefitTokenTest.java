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
package plugin.lsttokens.ability;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BenefitTokenTest extends AbstractCDOMTokenTestCase<Ability>
{

    static BenefitToken token = new BenefitToken();
    static CDOMTokenLoader<Ability> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Ability> getCDOMClass()
    {
        return Ability.class;
    }

    @Override
    public CDOMLoader<Ability> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Ability> getToken()
    {
        return token;
    }

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
        TokenRegistration.register(prerace);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(preracewriter);
    }

    @Test
    public void testInvalidDoublePipe()
    {
        assertFalse(parse("SA Number %||VarF"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEndingPipe()
    {
        assertFalse(parse("SA Number|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidStartingPipe()
    {
        assertFalse(parse("|Var"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidPre()
    {
        assertFalse(parse("SA Number One|PREFOO:1,Fighter=1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidVarAfterPre()
    {
        assertFalse(parse("SA % plus %|Var|PRECLASS:1,Fighter|Var2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidParen()
    {
        assertFalse(parse("The caster gains attack, damage bonus, +(min(6,(CASTERLEVEL/3))."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre()
    {
        assertFalse(parse("PRECLASS:1,Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("SA Number One");
    }

    @Test
    public void testRoundRobinPercent() throws PersistenceLayerException
    {
        runRoundRobin("SA at 50%% effectiveness");
    }

    @Test
    public void testRoundRobinVariable() throws PersistenceLayerException
    {
        runRoundRobin("SA Number %1|Variab");
    }

    @Test
    public void testRoundRobinPre() throws PersistenceLayerException
    {
        runRoundRobin("SA Number One|PRECLASS:1,Fighter=1");
    }

    @Test
    public void testRoundRobinDoublePre() throws PersistenceLayerException
    {
        runRoundRobin("SA Number One|PRECLASS:1,Fighter=1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinVarDoublePre() throws PersistenceLayerException
    {
        runRoundRobin("SA Number %1 before %2|Var|TwoVar|PRECLASS:1,Fighter=1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinCompound() throws PersistenceLayerException
    {
        runRoundRobin(
                "SA Number %1 before %2|Var|TwoVar|PRECLASS:1,Fighter=1|PRERACE:1,Human",
                "SA Number One|PRECLASS:1,Fighter=1");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SA Number %1|Variab";
    }

    @Override
    protected String getLegalValue()
    {
        return "SA Number One|PRECLASS:1,Fighter=1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Override
    protected Ability get(LoadContext context, String name)
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(name);
        context.getReferenceContext().importObject(a);
        return a;
    }
}
