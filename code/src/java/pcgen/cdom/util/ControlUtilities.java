/*
 * Copyright 2016 (C) Tom Parker <thpr@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.util;

import java.util.Objects;
import java.util.Optional;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CodeControl;
import pcgen.rules.context.LoadContext;

/**
 * ControlUtilities provides convenience methods around Code Controls
 */
public final class ControlUtilities
{

    private ControlUtilities()
    {
        //Do not instantiate Utility class
    }

    /**
     * Returns the value of a code control in the given LoadContext.
     *
     * @param context The LoadContext in which the code control is being evaluated
     * @param command The code control for which the value should be returned
     * @return The value of a code control in the given LoadContext
     */
    public static String getControlToken(LoadContext context, String command)
    {
        CodeControl controller =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(CodeControl.class, "Controller");
        if (controller != null)
        {
            return controller.get(ObjectKey.getKeyFor(String.class, "*" + Objects.requireNonNull(command)));
        }
        return null;
    }

    /**
     * Returns true if a feature code control in the given LoadContext is enabled.
     *
     * @param context The LoadContext in which the code control is being evaluated
     * @param feature The feature code control for which the value should be returned
     * @return true if a feature code control in the given LoadContext is enabled; false
     * otherwise
     */
    public static boolean isFeatureEnabled(LoadContext context, String feature)
    {
        CodeControl controller = context.getReferenceContext()
                .silentlyGetConstructedCDOMObject(CodeControl.class, "Controller");
        if (controller != null)
        {
            Boolean object = controller.get(ObjectKey.getKeyFor(Boolean.class,
                    "*" + Objects.requireNonNull(feature)));
            return (object != null) && object;
        }
        return false;
    }

    /**
     * Returns the value of a code control in the given LoadContext. If the given CControl
     * is not present in the data, the default value of the given CControl will be
     * returned.
     *
     * @param context The LoadContext in which the code control is being evaluated
     * @param control The code control for which the value should be returned
     * @return The value of a code control in the given LoadContext
     */
    public static String getControlToken(LoadContext context, CControl control)
    {
        CodeControl controller =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(CodeControl.class, "Controller");
        if (controller != null)
        {
            ObjectKey<String> ok = ObjectKey.getKeyFor(String.class,
                    "*" + Objects.requireNonNull(control.getName()));
            return Optional.ofNullable(controller.get(ok))
                    .orElse(control.getDefaultValue());
        }
        return control.getDefaultValue();
    }

    /**
     * Returns true if the code control has a value in the given LoadContext.
     *
     * @param context The LoadContext in which the code control is being evaluated
     * @param command The code control which should be checked to determine if it exists
     * @return true if the code control has a value in the given LoadContext; false
     * otherwise
     */
    public static boolean hasControlToken(LoadContext context, String command)
    {
        CodeControl controller =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(CodeControl.class, "Controller");
        if (controller != null)
        {
            return controller.get(ObjectKey.getKeyFor(String.class, "*" + Objects.requireNonNull(command))) != null;
        }
        return false;
    }

    /**
     * Returns true if the code control has a value in the given LoadContext.
     *
     * @param context The LoadContext in which the code control is being evaluated
     * @param command The code control which should be checked to determine if it exists
     * @return true if the code control has a value in the given LoadContext; false
     * otherwise
     */
    public static boolean hasControlToken(LoadContext context, CControl command)
    {
        return hasControlToken(context, command.getName());
    }

}
