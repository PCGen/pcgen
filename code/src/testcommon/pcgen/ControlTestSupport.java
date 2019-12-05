/*
 * Copyright 2018 Thomas Parker
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
package pcgen;

import java.util.Objects;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.rules.context.LoadContext;

/**
 * Utility methods to support CodeControl in tests.
 */
public class ControlTestSupport
{

    /**
     * Enable a Feature (emulating code control)
     *
     * @param context     The LoadContext on which the control should be enabled
     * @param featureName The feature to be enabled
     */
    public static void enableFeature(LoadContext context, String featureName)
    {
        CodeControl controller = context.getReferenceContext()
                .constructNowIfNecessary(CodeControl.class, "Controller");
        ObjectKey<Boolean> objectKey = ObjectKey.getKeyFor(Boolean.class,
                "*" + Objects.requireNonNull(featureName));
        controller.put(objectKey, true);
    }
}
