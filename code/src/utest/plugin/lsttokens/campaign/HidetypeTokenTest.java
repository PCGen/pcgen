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
package plugin.lsttokens.campaign;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class HidetypeTokenTest extends AbstractCDOMTokenTestCase<Campaign>
{
    static HidetypeToken token = new HidetypeToken();
    static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Campaign> getCDOMClass()
    {
        return Campaign.class;
    }

    @Override
    public CDOMLoader<Campaign> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidNoPipe()
    {
        assertFalse(parse("NoPipe"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoPipe()
    {
        assertFalse(parse("One|Two|Three"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoublePipe()
    {
        assertFalse(parse("Two||Pipe"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPipe()
    {
        assertFalse(parse("|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyKey()
    {
        assertFalse(parse("|Value"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue()
    {
        assertFalse(parse("SKILL|"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSkill() throws PersistenceLayerException
    {
        runRoundRobin("SKILL|QualityValue");
    }

    @Test
    public void testRoundRobinEquip() throws PersistenceLayerException
    {
        runRoundRobin("EQUIP|Quality Value");
    }

    @Test
    public void testRoundRobinFeat() throws PersistenceLayerException
    {
        runRoundRobin("FEAT|Niederösterreich|Finger Lakes");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SKILL|QualityValue";
    }

    @Override
    protected String getLegalValue()
    {
        return "EQUIP|Quality Value";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
