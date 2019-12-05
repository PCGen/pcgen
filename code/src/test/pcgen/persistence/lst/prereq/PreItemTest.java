/*
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreItemParser;

import org.junit.jupiter.api.Test;

/**
 * {@code PreItemTest} is ...
 */
@SuppressWarnings("nls")
public class PreItemTest extends EnUsLocaleDependentTestCase
{
    @Test
    public void testItemPresent() throws Exception
    {
        PreItemParser parser = new PreItemParser();
        // Test of |PREITEM:1,TYPE.Saddle";

        Prerequisite prereq =
                parser.parse("ITEM", "1,TYPE.Saddle", false, false);

        assertEquals(
                "<prereq kind=\"item\" key=\"TYPE.Saddle\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n", prereq.toString());
    }

    @Test
    public void testItemNotPresent() throws Exception
    {
        PreItemParser parser = new PreItemParser();
        // Test of |!PREITEM:1,TYPE.Saddle";

        Prerequisite prereq =
                parser.parse("ITEM", "1,TYPE.Saddle", true, false);

        assertEquals(
                "<prereq kind=\"item\" key=\"TYPE.Saddle\" operator=\"LT\" operand=\"1\" >\n"
                        + "</prereq>\n", prereq.toString());
    }

}
