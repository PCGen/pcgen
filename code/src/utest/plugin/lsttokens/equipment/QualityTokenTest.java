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
package plugin.lsttokens.equipment;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class QualityTokenTest extends AbstractCDOMTokenTestCase<Equipment>
{
    static QualityToken token = new QualityToken();
    static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Equipment> getCDOMClass()
    {
        return Equipment.class;
    }

    @Override
    public CDOMLoader<Equipment> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Equipment> getToken()
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
        assertFalse(parse("Key|"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("QualityName|QualityValue");
    }

    @Test
    public void testRoundRobinSpaces() throws PersistenceLayerException
    {
        runRoundRobin("Quality Name|Quality Value");
    }

    @Test
    public void testRoundRobinInternational() throws PersistenceLayerException
    {
        runRoundRobin("Niederösterreich Quality|Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        runRoundRobin("Languedoc-Roussillon Quality|Languedoc-Roussillon");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "First Quality|Niederösterreich";
    }

    @Override
    protected String getLegalValue()
    {
        return "First Quality|Languedoc-Roussillon";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
