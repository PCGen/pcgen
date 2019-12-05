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
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public abstract class AbstractDamageTokenTestCase extends
        AbstractCDOMTokenTestCase<Equipment>
{

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

    @Test
    public void testInvalidEmptyInput()
    {
        assertFalse(getToken().parseToken(primaryContext, primaryProf, "").passed());
    }

    @Test
    public void testRoundRobinTwo() throws PersistenceLayerException
    {
        runRoundRobin("2");
    }

    @Test
    public void testRoundRobinDeeSix() throws PersistenceLayerException
    {
        runRoundRobin("1d6");
    }

    @Test
    public void testRoundRobinDash() throws PersistenceLayerException
    {
        runRoundRobin("-");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "-";
    }

    @Override
    protected String getLegalValue()
    {
        return "1d2";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseLegal()
    {
        expectSingle(setAndUnparse(getLegalValue()), getLegalValue());
    }

    @Test
    public void testUnparseNull()
    {
        getUnparseTarget().put(getStringKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    /*
     * TODO Need to define the appropriate behavior here - is the token
     * responsible for catching this?
     */
    // @Test
    // public void testUnparseEmpty() throws PersistenceLayerException
    // {
    // primaryProf.put(getStringKey(), "");
    // assertBadUnparse();
    // }

    private static StringKey getStringKey()
    {
        return StringKey.DAMAGE;
    }

    protected String[] setAndUnparse(String val)
    {
        getUnparseTarget().put(getStringKey(), val);
        return getToken().unparse(primaryContext, primaryProf);
    }

    protected abstract CDOMObject getUnparseTarget();

}
