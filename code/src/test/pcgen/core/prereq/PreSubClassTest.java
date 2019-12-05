/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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

import static org.junit.Assert.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.SubClassApplication;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.pretokens.test.PreSubClassTester;

import org.junit.jupiter.api.Test;


class PreSubClassTest extends AbstractCharacterTestCase
{
    /**
     * Test to ensure that a character with a named class can be found.
     *
     * @throws PrerequisiteException the prerequisite exception
     */
    @Test
    void testNamedSubClass() throws PrerequisiteException
    {
        final PCClass pcClass = new PCClass();
        pcClass.setName("MyClass");
        pcClass.put(StringKey.KEY_NAME, "KEY_MyClass");
        BuildUtilities.setFact(pcClass, "SpellType", "Arcane");

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(3, pcClass);
        SubClassApplication.setSubClassKey(character, character
                .getClassKeyed("KEY_MyClass"), "MySubClass");

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("subclass");
        prereq.setKey("mysubclass");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.GTEQ);

        final PreSubClassTester test = new PreSubClassTester();
        final int passes = test.passes(prereq, character, null);
        assertEquals(1, passes);
    }

    /**
     * Test to make sure subclass still found if multiple classes, only one with subclass.
     *
     * @throws PrerequisiteException the prerequisite exception
     */
    @Test
    public void testCharWithMultipleClasses() throws PrerequisiteException
    {
        final PCClass pcClass = new PCClass();
        pcClass.setName("MyClass");
        BuildUtilities.setFact(pcClass, "SpellType", "Arcane");

        final PCClass pcClass2 = new PCClass();
        pcClass2.setName("Other Class");

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, pcClass);
        character.incrementClassLevel(2, pcClass2);
        SubClassApplication.setSubClassKey(character, character
                .getClassKeyed("Other Class"), "OtherSubClass");

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("subclass");
        prereq.setKey("othersubclass");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.GTEQ);

        final PreSubClassTester test = new PreSubClassTester();
        final int passes = test.passes(prereq, character, null);
        assertEquals(1, passes);
    }

    /**
     * Test to make sure subclass still found if multiple classes, both with subclass.
     *
     * @throws PrerequisiteException the prerequisite exception
     */
    @Test
    public void testCharWithMultipleSubClasses() throws PrerequisiteException
    {
        final PCClass pcClass = new PCClass();
        pcClass.setName("MyClass");
        BuildUtilities.setFact(pcClass, "SpellType", "Arcane");

        final PCClass pcClass2 = new PCClass();
        pcClass2.setName("Other Class");

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, pcClass);
        SubClassApplication.setSubClassKey(character, character
                .getClassKeyed("MyClass"), "MySubClass");
        character.incrementClassLevel(2, pcClass2);
        SubClassApplication.setSubClassKey(character, character
                .getClassKeyed("Other Class"), "OtherSubClass");

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("subclass");
        prereq.setKey("othersubclass");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.GTEQ);

        final PreSubClassTester test = new PreSubClassTester();
        final int passes = test.passes(prereq, character, null);
        assertEquals(1, passes);
    }

    /**
     * Test to ensure that a character without a named subclass cannot be found.
     *
     * @throws PrerequisiteException the prerequisite exception
     */
    @Test
    public void testNamedSubClassFail() throws PrerequisiteException
    {
        final PCClass pcClass = new PCClass();
        pcClass.setName("MyClass");
        BuildUtilities.setFact(pcClass, "SpellType", "Arcane");

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, pcClass);

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("subclass");
        prereq.setKey("mysubclass");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.GTEQ);

        final PreSubClassTester test = new PreSubClassTester();
        final int passes = test.passes(prereq, character, null);
        assertEquals(0, passes);
    }

    /**
     * Test to ensure that a character without a named subclass cannot be found.
     *
     * @throws PrerequisiteException the prerequisite exception
     */
    @Test
    public void testNamedDifSubClassFail() throws PrerequisiteException
    {
        final PCClass pcClass = new PCClass();
        pcClass.setName("MyClass");
        BuildUtilities.setFact(pcClass, "SpellType", "Arcane");

        final PlayerCharacter character = getCharacter();
        character.incrementClassLevel(1, pcClass);
        SubClassApplication.setSubClassKey(character, character
                .getClassKeyed("MyClass"), "MySubClass");

        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("subclass");
        prereq.setKey("myothersubclass");
        prereq.setOperand("1");
        prereq.setOperator(PrerequisiteOperator.GTEQ);

        final PreSubClassTester test = new PreSubClassTester();
        final int passes = test.passes(prereq, character, null);
        assertEquals(0, passes);
    }

}
