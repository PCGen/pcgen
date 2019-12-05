/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.prereq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PreBaseSizeTest} tests that the PREBASESIZE tag is
 * working correctly.
 */
@SuppressWarnings("nls")
public class PreBaseSizeTest extends AbstractCharacterTestCase
{
    Race race = new Race();
    PCTemplate template = new PCTemplate();

    /**
     * Test the PREBASESIZE code.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testBaseSize() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.setRace(race);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREBASESIZEEQ:Medium");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be equal to Medium");

        prereq = factory.parse("PREBASESIZELTEQ:Medium");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be <= to Medium");

        prereq = factory.parse("PREBASESIZEGTEQ:Medium");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be >= to Medium");

        prereq = factory.parse("PREBASESIZEGT:Small");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be > to Small");

        prereq = factory.parse("PREBASESIZELT:Large");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be < to Large");

        prereq = factory.parse("PREBASESIZELT:Medium");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character's base size should not be < to Medium");

        prereq = factory.parse("PREBASESIZEGT:Medium");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character's base size should not be > to Medium");
    }

    /**
     * Test to make sure template SIZE: changes the base size.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testModBaseSize() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        character.setRace(race);
        character.addTemplate(template);

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREBASESIZEEQ:L");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be equal to Large");

        prereq = factory.parse("PREBASESIZELTEQ:L");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be <= to L");

        prereq = factory.parse("PREBASESIZEGTEQ:L");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be >= to L");

        prereq = factory.parse("PREBASESIZEGT:Small");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be > to Small");

        prereq = factory.parse("PREBASESIZELT:Huge");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be < to Large");

        prereq = factory.parse("PREBASESIZELT:L");

        assertFalse(PrereqHandler
                .passes(prereq, character, null), "Character's base size should not be < to L");

        prereq = factory.parse("PREBASESIZEGT:L");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character's base size should not be > to Medium");
    }

    /**
     * Tests to make sure the base size does not include SIZEMOD adjustments.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testBaseSizePlusMod() throws PersistenceLayerException
    {
        final PlayerCharacter character = getCharacter();
        LoadContext context = Globals.getContext();

        character.setRace(race);
        final BonusObj sizeBonus = Bonus.newBonus(context, "SIZEMOD|NUMBER|1");
        race.addToListFor(ListKey.BONUS, sizeBonus);

        character.calcActiveBonuses();

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();
        prereq = factory.parse("PREBASESIZEEQ:Medium");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be equal to Medium");

        prereq = factory.parse("PREBASESIZELTEQ:Medium");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be <= to Medium");

        prereq = factory.parse("PREBASESIZEGTEQ:Medium");

        assertTrue(
                PrereqHandler.passes(prereq, character, null), "Character's base size should be >= to Medium");

        prereq = factory.parse("PREBASESIZEGT:Small");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be > to Small");

        prereq = factory.parse("PREBASESIZELT:Large");

        assertTrue(PrereqHandler
                .passes(prereq, character, null), "Character's base size should be < to Large");

        prereq = factory.parse("PREBASESIZELT:Medium");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character's base size should not be < to Medium");

        prereq = factory.parse("PREBASESIZEGT:Medium");

        assertFalse(
                PrereqHandler.passes(prereq, character, null), "Character's base size should not be > to Medium");
    }

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(medium);
        CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);

        race.setName("Human");
        race.put(FormulaKey.SIZE, new FixedSizeFormula(mediumRef));
        Globals.getContext().getReferenceContext().importObject(race);

        template.put(FormulaKey.SIZE, new FixedSizeFormula(largeRef));
    }
}
