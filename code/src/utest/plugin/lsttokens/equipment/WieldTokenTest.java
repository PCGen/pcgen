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
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.character.WieldCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreVariableParser;
import plugin.pretokens.writer.PreVariableWriter;

import org.junit.jupiter.api.Test;
import util.GameModeSupport;

public class WieldTokenTest extends AbstractCDOMTokenTestCase<Equipment>
{
    static WieldToken token = new WieldToken();
    static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

    @Override
    protected void resetContext()
    {
        try
        {
            TokenRegistration.register(new PreVariableParser());
            TokenRegistration.register(new PreVariableWriter());
        } catch (PersistenceLayerException e)
        {
            fail(e.getLocalizedMessage());
        }
        super.resetContext();
    }

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
    public void testBadInput()
    {
        try
        {
            boolean parse = parse("INVALID");
            assertFalse(parse);
        } catch (IllegalArgumentException e)
        {
            // OK
        }
    }

    @Test
    public void testBadInputEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinLight() throws PersistenceLayerException
    {
        runRoundRobin("Light");
    }

    @Test
    public void testRoundRobinOneHanded() throws PersistenceLayerException
    {
        runRoundRobin("OneHanded");
    }

    @Test
    public void testRoundRobinTwoHanded() throws PersistenceLayerException
    {
        runRoundRobin("TwoHanded");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "TwoHanded";
    }

    @Override
    protected String getLegalValue()
    {
        return "Light";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<WieldCategory> getObjectKey()
    {
        return ObjectKey.WIELD;
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(getObjectKey(), primaryContext.getReferenceContext()
                .silentlyGetConstructedCDOMObject(WieldCategory.class,
                        "OneHanded"));
        expectSingle(getToken().unparse(primaryContext, primaryProf),
                "OneHanded");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = getObjectKey();
        primaryProf.put(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Override
    protected void additionalSetup(LoadContext context)
    {
        super.additionalSetup(context);
        try
        {
            GameModeSupport.addDefaultWieldCategories(context);
        } catch (PersistenceLayerException e)
        {
            fail(e.getLocalizedMessage());
        }
    }
}
