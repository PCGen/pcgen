/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;

import org.junit.jupiter.api.Test;

class ControlUtilitiesTest
{

    @Test
    public void testGetControlTokenString()
    {
        LoadContext context = new RuntimeLoadContext(
                RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        assertNull(ControlUtilities.getControlToken(context, "Got"));
        CodeControl controller = context.getReferenceContext()
                .constructCDOMObject(CodeControl.class, "Controller");
        ObjectKey<String> ok = ObjectKey.getKeyFor(String.class, "*Got");
        controller.put(ok, "Loot");
        assertEquals("Loot", ControlUtilities.getControlToken(context, "Got"));
        assertNull(ControlUtilities.getControlToken(context, "Other"));
    }

    @Test
    public void testGetControlTokenControl()
    {
        LoadContext context = new RuntimeLoadContext(
                RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        CControl control =
                new CControl("Got", "Swag", Optional.empty(), "STRING");
        assertEquals("Swag",
                ControlUtilities.getControlToken(context, control));
        CodeControl controller = context.getReferenceContext()
                .constructCDOMObject(CodeControl.class, "Controller");
        ObjectKey<String> ok = ObjectKey.getKeyFor(String.class, "*Got");
        controller.put(ok, "Loot");
        assertEquals("Loot",
                ControlUtilities.getControlToken(context, control));
        assertNull(ControlUtilities.getControlToken(context, "Other"));
    }

}
