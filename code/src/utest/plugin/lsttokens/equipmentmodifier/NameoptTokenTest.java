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
package plugin.lsttokens.equipmentmodifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.EqModNameOpt;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class NameoptTokenTest extends AbstractCDOMTokenTestCase<EquipmentModifier>
{
    static NameoptToken token = new NameoptToken();
    static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<>();

    @Override
    public Class<EquipmentModifier> getCDOMClass()
    {
        return EquipmentModifier.class;
    }

    @Override
    public CDOMLoader<EquipmentModifier> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<EquipmentModifier> getToken()
    {
        return token;
    }

    @Test
    public void testBadInputNegative()
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
    public void testBadInputPlainText()
    {
        assertFalse(parse("TEXT"));
        assertNoSideEffects();
    }

    @Test
    public void testBadInputEmptyText()
    {
        assertFalse(parse("TEXT="));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinNormal() throws PersistenceLayerException
    {
        runRoundRobin("NORMAL");
    }

    @Test
    public void testRoundRobinNoList() throws PersistenceLayerException
    {
        runRoundRobin("NOLIST");
    }

    @Test
    public void testRoundRobinNoName() throws PersistenceLayerException
    {
        runRoundRobin("NONAME");
    }

    @Test
    public void testRoundRobinNothing() throws PersistenceLayerException
    {
        runRoundRobin("NOTHING");
    }

    @Test
    public void testRoundRobinSpell() throws PersistenceLayerException
    {
        runRoundRobin("SPELL");
    }

    @Test
    public void testRoundRobinText() throws PersistenceLayerException
    {
        runRoundRobin("TEXT=This is the text");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SPELL";
    }

    @Override
    protected String getLegalValue()
    {
        return "TEXT=This is the text";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }


    @Test
    public void testOverwriteToText()
    {
        parse("SPELL");
        validateUnparsed(primaryContext, primaryProf, "SPELL");
        parse("TEXT=This is the text");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("SPELL", "TEXT=This is the text"));
    }

    @Test
    public void testOverwriteFromText()
    {
        parse("TEXT=This is the text");
        validateUnparsed(primaryContext, primaryProf, "TEXT=This is the text");
        parse("NOTHING");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("TEXT=This is the text", "NOTHING"));
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<EqModNameOpt> getObjectKey()
    {
        return ObjectKey.NAME_OPT;
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(getObjectKey(), EqModNameOpt.SPELL);
        expectSingle(getToken().unparse(primaryContext, primaryProf),
                EqModNameOpt.SPELL.toString());
    }

    @Test
    public void testUnparseLegalName()
    {
        primaryProf.put(StringKey.NAME_TEXT, "MyText");
        primaryProf.put(ObjectKey.NAME_OPT, EqModNameOpt.TEXT);
        expectSingle(getToken().unparse(primaryContext, primaryProf),
                "TEXT=MyText");
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

    @Test
    public void testUnparseIncompleteSpell()
    {
        primaryProf.put(ObjectKey.NAME_OPT, EqModNameOpt.TEXT);
        assertBadUnparse();
    }

    /*
     * TODO Another item that is overwrite sensitive, need to understand how
     * this should work and whether this is ok based on overwrite unit tests
     * above, or whether this is invalid
     */
    // @Test
    // public void testUnparseOther() throws PersistenceLayerException
    // {
    // primaryProf.put(ObjectKey.NAME_OPT, EqModNameOpt.SPELL);
    // primaryProf.put(StringKey.NAME_TEXT, "MyText");
    // assertBadUnparse();
    // }
}
