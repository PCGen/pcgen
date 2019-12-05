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
package plugin.lsttokens.skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ACheckTokenTest extends AbstractCDOMTokenTestCase<Skill>
{

    static AcheckToken token = new AcheckToken();
    static CDOMTokenLoader<Skill> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Skill> getCDOMClass()
    {
        return Skill.class;
    }

    @Override
    public CDOMLoader<Skill> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Skill> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputString()
    {
        internalTestInvalidInputString(null);
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStringSet()
    {
        assertTrue(parse("YES"));
        assertTrue(parseSecondary("YES"));
        assertEquals(SkillArmorCheck.YES, primaryProf
                .get(ObjectKey.ARMOR_CHECK));
        internalTestInvalidInputString(SkillArmorCheck.YES);
        assertNoSideEffects();
    }

    public void internalTestInvalidInputString(Object val)
    {
        assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        // Note case sensitivity
        //TODO 516 deprecation
        // assertFalse(parse("Yes"));
        // assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        // assertFalse(parse("No"));
        // assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
        // assertFalse(parse("Double"));
        // assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("YES"));
        assertEquals(SkillArmorCheck.YES, primaryProf
                .get(ObjectKey.ARMOR_CHECK));
        assertTrue(parse("NO"));
        assertEquals(SkillArmorCheck.NONE, primaryProf.get(ObjectKey.ARMOR_CHECK));
        assertTrue(parse("PROFICIENT"));
        assertEquals(SkillArmorCheck.NONPROF, primaryProf
                .get(ObjectKey.ARMOR_CHECK));
        assertTrue(parse("DOUBLE"));
        assertEquals(SkillArmorCheck.DOUBLE, primaryProf
                .get(ObjectKey.ARMOR_CHECK));
        assertTrue(parse("WEIGHT"));
        assertEquals(SkillArmorCheck.WEIGHT, primaryProf
                .get(ObjectKey.ARMOR_CHECK));
    }

    @Test
    public void testRoundRobinYes() throws PersistenceLayerException
    {
        runRoundRobin("YES");
    }

    @Test
    public void testRoundRobinNo() throws PersistenceLayerException
    {
        runRoundRobin("NONE");
    }

    @Test
    public void testRoundRobinProficient() throws PersistenceLayerException
    {
        runRoundRobin("NONPROF");
    }

    @Test
    public void testRoundRobinDouble() throws PersistenceLayerException
    {
        runRoundRobin("DOUBLE");
    }

    @Test
    public void testRoundRobinWeight() throws PersistenceLayerException
    {
        runRoundRobin("WEIGHT");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "DOUBLE";
    }

    @Override
    protected String getLegalValue()
    {
        return "WEIGHT";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<SkillArmorCheck> getObjectKey()
    {
        return ObjectKey.ARMOR_CHECK;
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(getObjectKey(), SkillArmorCheck.DOUBLE);
        expectSingle(getToken().unparse(primaryContext, primaryProf),
                SkillArmorCheck.DOUBLE.toString());
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
            //Yep!
        }
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
