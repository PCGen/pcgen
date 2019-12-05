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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;

import pcgen.AbstractCharacterTestCase;
import pcgen.LocaleDependentTestCase;
import plugin.pretokens.test.PreWieldTester;

import org.junit.jupiter.api.Test;


class AbstractPrerequisiteTestTest extends AbstractCharacterTestCase
{
    @Test
    void testVisionNotHandledFail()
    {
        final Prerequisite prereq = new Prerequisite();
        prereq.setKind("wield");

        LocaleDependentTestCase.before(Locale.US);
        try
        {
            final PreWieldTester test = new PreWieldTester();
            test.passes(prereq, getCharacter(), null);
            fail("Should have thrown a PrerequisiteException here.");
        } catch (PrerequisiteException pe)
        {
            assertEquals(PreWieldTester.class.getName()
                    + " does not support prerequisites for Characters.", pe
                    .getMessage());
        }
        LocaleDependentTestCase.after();
    }

}
