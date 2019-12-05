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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.ShieldProf;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ProficiencyTokenTest extends AbstractCDOMTokenTestCase<Equipment>
{
    static ProficiencyToken token = new ProficiencyToken();
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
    public void testInvalidInputEmpty()
    {
        assertNull(token.unparse(primaryContext, primaryProf));
        assertFalse(parse(""));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputString()
    {
        assertFalse(parse("String"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinedComma()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1,TestWP2"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinedPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1|TestWP2"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP1.TestWP2"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyWeapon()
    {
        assertFalse(parse("WEAPON|"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputWeaponString()
    {
        assertTrue(parse("WEAPON|String"));
        assertConstructionError();
    }

    // FIXME These are invalid due to RC being overly protective at the moment
    // @Test
    // public void testInvalidInputWeaponType() throws PersistenceLayerException
    // {
    // assertTrue(parse("WEAPON|TYPE=TestType"));
    // assertFalse(primaryContext.ref.validate());
    // }

    @Test
    public void testInvalidInputWeaponJoinedComma()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("WEAPON|TestWP1,TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputWeaponJoinedPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("WEAPON|TestWP1|TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputWeaponJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("WEAPON|TestWP1.TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    // FIXME These are invalid due to RC being overly protective at the moment
    // @Test
    // public void testInvalidInputAll()
    // {
    // assertTrue(parse( "ALL"));
    // assertFalse(primaryContext.ref.validate());
    // }
    //
    // @Test
    // public void testInvalidInputAny()
    // {
    // assertTrue(parse( "ANY"));
    // assertFalse(primaryContext.ref.validate());
    // }
    // @Test
    // public void testInvalidInputCheckType()
    // {
    // if (!isTypeLegal())
    // {
    // assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
    // assertFalse(primaryContext.ref.validate());
    // }
    // }
    //

    @Test
    public void testReplacementInputsWeapon()
    {
        String[] unparsed;
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("WEAPON|TestWP1"));
        assertTrue(parse("WEAPON|TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("WEAPON|TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testInvalidInputEmptyArmor()
    {
        assertFalse(parse("ARMOR|"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputArmorString()
    {
        assertTrue(parse("ARMOR|String"));
        assertConstructionError();
    }

    // FIXME These are invalid due to RC being overly protective at the moment
    // @Test
    // public void testInvalidInputArmorType() throws PersistenceLayerException
    // {
    // assertTrue(parse("ARMOR|TYPE=TestType"));
    // assertFalse(primaryContext.ref.validate());
    // }

    @Test
    public void testInvalidInputArmorJoinedComma()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("ARMOR|TestWP1,TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputArmorJoinedPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("ARMOR|TestWP1|TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputArmorJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("ARMOR|TestWP1.TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    // FIXME These are invalid due to RC being overly protective at the moment
    // @Test
    // public void testInvalidInputAll()
    // {
    // assertTrue(parse( "ALL"));
    // assertFalse(primaryContext.ref.validate());
    // }
    //
    // @Test
    // public void testInvalidInputAny()
    // {
    // assertTrue(parse( "ANY"));
    // assertFalse(primaryContext.ref.validate());
    // }
    // @Test
    // public void testInvalidInputCheckType()
    // {
    // if (!isTypeLegal())
    // {
    // assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
    // assertFalse(primaryContext.ref.validate());
    // }
    // }
    //

    @Test
    public void testReplacementInputsArmor()
    {
        String[] unparsed;
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("ARMOR|TestWP1"));
        assertTrue(parse("ARMOR|TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("ARMOR|TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testInvalidInputEmptyShield()
    {
        assertFalse(parse("SHIELD|"));
        assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
        assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
        assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputShieldString()
    {
        assertTrue(parse("SHIELD|String"));
        assertConstructionError();
    }

    // FIXME These are invalid due to RC being overly protective at the moment
    // @Test
    // public void testInvalidInputShieldType() throws PersistenceLayerException
    // {
    // assertTrue(parse("SHIELD|TYPE=TestType"));
    // assertFalse(primaryContext.ref.validate());
    // }

    @Test
    public void testInvalidInputShieldJoinedComma()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("SHIELD|TestWP1,TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputShieldJoinedPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("SHIELD|TestWP1|TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputShieldJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("SHIELD|TestWP1.TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(ObjectKey.WEAPON_PROF));
            assertNull(primaryProf.get(ObjectKey.SHIELD_PROF));
            assertNull(primaryProf.get(ObjectKey.ARMOR_PROF));
            assertNoSideEffects();
        }
    }

    // FIXME These are invalid due to RC being overly protective at the moment
    // @Test
    // public void testInvalidInputAll()
    // {
    // assertTrue(parse( "ALL"));
    // assertFalse(primaryContext.ref.validate());
    // }
    //
    // @Test
    // public void testInvalidInputAny()
    // {
    // assertTrue(parse( "ANY"));
    // assertFalse(primaryContext.ref.validate());
    // }
    // @Test
    // public void testInvalidInputCheckType()
    // {
    // if (!isTypeLegal())
    // {
    // assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
    // assertFalse(primaryContext.ref.validate());
    // }
    // }
    //

    @Test
    public void testReplacementInputsShield()
    {
        String[] unparsed;
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("SHIELD|TestWP1"));
        assertTrue(parse("SHIELD|TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("SHIELD|TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    protected static void construct(LoadContext loadContext, String one)
    {
        loadContext.getReferenceContext().constructCDOMObject(WeaponProf.class, one);
        loadContext.getReferenceContext().constructCDOMObject(ShieldProf.class, one);
        loadContext.getReferenceContext().constructCDOMObject(ArmorProf.class, one);
    }

    private static boolean isClearLegal()
    {
        return false;
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "ARMOR|TestWP2";
    }

    @Override
    protected String getLegalValue()
    {
        return "ARMOR|TestWP1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
