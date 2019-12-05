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
package plugin.lsttokens.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;

import pcgen.LocaleDependentTestCase;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class GenderLockTokenTest extends AbstractCDOMTokenTestCase<PCTemplate>
{

    static GenderlockToken token = new GenderlockToken();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
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
        assertTrue(parse("Male"));
        assertTrue(parseSecondary("Male"));
        assertEquals(Gender.Male, primaryProf.get(ObjectKey.GENDER_LOCK));
        internalTestInvalidInputString(Gender.Male);
        assertNoSideEffects();
    }

    public void internalTestInvalidInputString(Object val)
    {
        assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertFalse(parse("Always"));
        assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
        // Note case sensitivity
        assertFalse(parse("MALE"));
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("Male"));
        assertEquals(Gender.Male, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertTrue(parse("Female"));
        assertEquals(Gender.Female, primaryProf.get(ObjectKey.GENDER_LOCK));
        assertTrue(parse("Neuter"));
        assertEquals(Gender.Neuter, primaryProf.get(ObjectKey.GENDER_LOCK));
    }

    @Test
    public void testRoundRobinMale() throws PersistenceLayerException
    {
        LocaleDependentTestCase.before(Locale.US);
        runRoundRobin("Male");
        LocaleDependentTestCase.after();
    }

    @Test
    public void testRoundRobinMaleI18N() throws PersistenceLayerException
    {
        LocaleDependentTestCase.before(Locale.FRENCH);
        runRoundRobin("Male");
        LocaleDependentTestCase.after();
    }

    @Test
    public void testRoundRobinFemale() throws PersistenceLayerException
    {
        LocaleDependentTestCase.before(Locale.US);
        runRoundRobin("Female");
        LocaleDependentTestCase.after();
    }

    @Test
    public void testRoundRobinFemaleI18N() throws PersistenceLayerException
    {
        LocaleDependentTestCase.before(Locale.FRENCH);
        runRoundRobin("Female");
        LocaleDependentTestCase.after();
    }

    @Test
    public void testRoundRobinNeuter() throws PersistenceLayerException
    {
        LocaleDependentTestCase.before(Locale.US);
        runRoundRobin("Neuter");
        LocaleDependentTestCase.after();
    }

    @Test
    public void testRoundRobinNeuterI18N() throws PersistenceLayerException
    {
        LocaleDependentTestCase.before(Locale.FRENCH);
        runRoundRobin("Neuter");
        LocaleDependentTestCase.after();
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Neuter";
    }

    @Override
    protected String getLegalValue()
    {
        return "Female";
    }


    @Test
    public void testUnparseNull()
    {
        primaryProf.put(ObjectKey.GENDER_LOCK, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(ObjectKey.GENDER_LOCK, Gender.Male);
        LocaleDependentTestCase.before(Locale.US);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "Male");
        LocaleDependentTestCase.after();
        LocaleDependentTestCase.before(Locale.FRENCH);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "Male");
        LocaleDependentTestCase.after();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.GENDER_LOCK;
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
