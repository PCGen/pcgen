/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.bonus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.bonustokens.SpellKnown;
import plugin.lsttokens.BonusLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpellKnownTest extends AbstractGlobalTokenTestCase
{
    static BonusLst token = new BonusLst();
    static CDOMTokenLoader<PCTemplate> loader =
            new CDOMTokenLoader<>();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        addBonus(SpellKnown.class);
    }

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
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputOnlyType()
    {
        assertFalse(parse("SpellKnown"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlyTypeBar()
    {
        assertFalse(parse("SpellKnown|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoValue()
    {
        assertFalse(parse("SpellKnown|CLASS.Wizard;LEVEL.1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputMissingValue()
    {
        try
        {
            assertFalse(parse("SpellKnown|CLASS.Wizard;LEVEL.1|"));
        } catch (IllegalArgumentException e)
        {
            //This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoTarget()
    {
        assertFalse(parse("SpellKnown||2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleFirstPipe()
    {
        assertFalse(parse("SpellKnown||CLASS.Wizard;LEVEL.1|1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoubleSecondPipe()
    {
        try
        {
            assertFalse(parse("SpellKnown|CLASS.Wizard;LEVEL.1||1"));
        } catch (IllegalArgumentException e)
        {
            //This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse(getLegalValue()));
        assertCleanConstruction();
        assertTrue(parse(getAlternateLegalValue()));
        assertCleanConstruction();
    }

    @Test
    public void testRoundRobinWizardOneNumber()
            throws PersistenceLayerException
    {
        runRoundRobin("SPELLKNOWN|CLASS.Wizard;LEVEL.1|1");
    }

    @Test
    public void testRoundRobinWizardOneFormula()
            throws PersistenceLayerException
    {
        runRoundRobin("SPELLKNOWN|CLASS.Wizard;LEVEL.1|FORMULA");
    }

    @Test
    public void testRoundRobinListNumber() throws PersistenceLayerException
    {
        runRoundRobin("SPELLKNOWN|%LIST|1");
    }

    @Override
    protected String getLegalValue()
    {
        return "SPELLKNOWN|CLASS.Cleric;LEVEL.3|1";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "SPELLKNOWN|CLASS.Wizard;LEVEL.1|1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }
}
