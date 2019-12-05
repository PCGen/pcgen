/*
 * Copyright 2007 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.core.prereq;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.pretokens.parser.PreAbilityParser;

import org.junit.jupiter.api.Test;

/**
 * {@code PreAbilityTest} verifies the function of the
 * PreAbilityTester.
 */
public class PreAbilityTest extends AbstractCharacterTestCase
{
    /**
     * Test the function of the ANY key.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testAnyMatch() throws PersistenceLayerException
    {
        Ability ab2 =
                TestHelper.makeAbility("Dancer", "BARDIC",
                        "General.Bardic");
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq =
                parser.parse("ability", "1,CATEGORY.BARDIC,ANY",
                        false, false);
        assertFalse("Test any match with no abilities.", PrereqHandler.passes(
                prereq, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test any match with an ability.", PrereqHandler.passes(
                prereq, character, null));

    }

    /**
     * Test the function of the category matching.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testCategoryMatch() throws PersistenceLayerException
    {
        Ability ab2 =
                TestHelper.makeAbility("Dancer", "BARDIC",
                        "General.Bardic");
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq2 =
                parser.parse("ability", "1,CATEGORY.BARDIC,ANY",
                        false, false);
        assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
                prereq2, character, null));
        Prerequisite prereq3 =
                parser.parse("ability", "1,CATEGORY.FEAT,ANY",
                        false, false);
        assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
                prereq3, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test bardic match with an ability.", PrereqHandler.passes(
                prereq2, character, null));
        assertFalse("Test feat match with an ability.", PrereqHandler.passes(
                prereq3, character, null));

    }

    /**
     * Test the function of the category matching.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testKeyMatch() throws PersistenceLayerException
    {
        Ability ab2 =
                TestHelper.makeAbility("Dancer", "BARDIC",
                        "General.Bardic");
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq =
                parser.parse("ability", "1,CATEGORY.BARDIC,KEY_Dancer",
                        false, false);
        assertFalse("Test any match with no abilities.", PrereqHandler.passes(
                prereq, character, null));
        Prerequisite prereq2 =
                parser.parse("ability", "1,CATEGORY.BARDIC,KEY_Alertness",
                        false, false);
        assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
                prereq2, character, null));
        Prerequisite prereq3 =
                parser.parse("ability", "1,CATEGORY.FEAT,KEY_Dancer",
                        false, false);
        assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
                prereq3, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test any match with an ability.", PrereqHandler.passes(
                prereq, character, null));
        assertFalse("Test bardic match with an ability.", PrereqHandler.passes(
                prereq2, character, null));
        assertFalse("Test feat match with an ability.", PrereqHandler.passes(
                prereq3, character, null));

    }

    /**
     * Test the function of the type matching.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testTypeMatch() throws PersistenceLayerException
    {
        Ability ab2 =
                TestHelper.makeAbility("Dancer", "BARDIC", "General.Bardic");
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq =
                parser.parse("ability", "1,CATEGORY.BARDIC,TYPE.General", false,
                        false);
        assertFalse("Test general type match with no abilities.", PrereqHandler
                .passes(prereq, character, null));
        Prerequisite prereq2 =
                parser.parse("ability", "1,CATEGORY.BARDIC,TYPE.Bardic", false,
                        false);
        assertFalse("Test bardic type match with no abilities.", PrereqHandler
                .passes(prereq2, character, null));
        Prerequisite prereq3 =
                parser.parse("ability", "1,CATEGORY.BARDIC,TYPE.Fighter", false, false);
        assertFalse("Test fighter type match with no abilities.", PrereqHandler
                .passes(prereq3, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test general type  match with an ability.", PrereqHandler
                .passes(prereq, character, null));
        assertTrue("Test bardic type match with an ability.", PrereqHandler
                .passes(prereq2, character, null));
        assertFalse("Test fighter type match with an ability.", PrereqHandler
                .passes(prereq3, character, null));
    }

    /**
     * Test the function of the SERVESAS token with direct key matching.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testKeyMatchWithServesAs() throws PersistenceLayerException
    {
        Ability fd = TestHelper.makeAbility("Dancer", BuildUtilities.getFeatCat(), "General");
        Ability ab2 =
                TestHelper.makeAbility("Dancer", "BARDIC",
                        "General.Bardic");
        Ability strangeness =
                TestHelper.makeAbility("Strangeness", "BARDIC",
                        "General");
        ab2.addToListFor(ListKey.SERVES_AS_ABILITY, CDOMDirectSingleRef.getRef(fd));
        ab2.addToListFor(ListKey.SERVES_AS_ABILITY, CDOMDirectSingleRef.getRef(strangeness));
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq =
                parser.parse("ability", "1,CATEGORY.BARDIC,KEY_Dancer",
                        false, false);
        assertFalse("Test any match with no abilities.", PrereqHandler.passes(
                prereq, character, null));
        Prerequisite prereq2 =
                parser.parse("ability", "1,CATEGORY.BARDIC,KEY_Alertness",
                        false, false);
        assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
                prereq2, character, null));
        Prerequisite prereq3 =
                parser.parse("ability", "1,CATEGORY.FEAT,KEY_Dancer",
                        false, false);
        assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
                prereq3, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test any match with an ability.", PrereqHandler.passes(
                prereq, character, null));
        assertFalse("Servesas non existant ability should not cause match.", PrereqHandler.passes(
                prereq2, character, null));
        assertTrue("Servesas should cause match.", PrereqHandler.passes(
                prereq3, character, null));

    }

    /**
     * Test the function of the SERVESAS token with type matching .
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testTypeMatchWithServesAs() throws PersistenceLayerException
    {
        Ability pa = TestHelper.makeAbility("Power Attack", BuildUtilities.getFeatCat(), "Fighter");
        Ability ab2 =
                TestHelper.makeAbility("Dancer", "BARDIC", "General.Bardic");
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
        ab2.addToListFor(ListKey.SERVES_AS_ABILITY, CDOMDirectSingleRef.getRef(pa));

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq =
                parser.parse("ability", "1,CATEGORY.BARDIC,TYPE.General", false,
                        false);
        assertFalse("Test general type match with no abilities.", PrereqHandler
                .passes(prereq, character, null));
        Prerequisite prereq2 =
                parser.parse("ability", "1,CATEGORY.BARDIC,TYPE.Bardic", false,
                        false);
        assertFalse("Test bardic type match with no abilities.", PrereqHandler
                .passes(prereq2, character, null));
        Prerequisite prereq3 =
                parser.parse("ability", "1,CATEGORY.BARDIC,TYPE.Fighter", false, false);
        assertFalse("Test fighter type match with no abilities.", PrereqHandler
                .passes(prereq3, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test general type  match with an ability.", PrereqHandler
                .passes(prereq, character, null));
        assertTrue("Test bardic type match with an ability.", PrereqHandler
                .passes(prereq2, character, null));
        assertTrue("Test fighter type match with SERVESAS ability.", PrereqHandler
                .passes(prereq3, character, null));
    }

    /**
     * Test the function of the category matching.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testCategoryMatchWithServesAs() throws PersistenceLayerException
    {
        Ability fas = TestHelper.makeAbility("Fascinate", "BARDIC", "Normal");
        Ability ab2 =
                TestHelper.makeAbility("Dancer", BuildUtilities.getFeatCat(),
                        "General.Bardic");
        ab2.addToListFor(ListKey.SERVES_AS_ABILITY, CDOMDirectSingleRef.getRef(fas));
        ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);

        PlayerCharacter character = getCharacter();
        PreAbilityParser parser = new PreAbilityParser();
        Prerequisite prereq2 =
                parser.parse("ability", "1,CATEGORY.BARDIC,ANY",
                        false, false);
        assertFalse("Test bardic match with no abilities.", PrereqHandler.passes(
                prereq2, character, null));
        Prerequisite prereq3 =
                parser.parse("ability", "1,CATEGORY.FEAT,ANY",
                        false, false);
        assertFalse("Test feat match with no abilities.", PrereqHandler.passes(
                prereq3, character, null));

        addAbility(TestHelper.getAbilityCategory(ab2), ab2);

        assertTrue("Test bardic match with an ability.", PrereqHandler.passes(
                prereq2, character, null));
        assertTrue("Test feat match with an ability.", PrereqHandler.passes(
                prereq3, character, null));

    }

}
