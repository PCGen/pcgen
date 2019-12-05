/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractYesNoToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.util.Logging;

/**
 * Deals with the MULT token
 */
public class MultToken extends AbstractYesNoToken<Ability>
        implements CDOMPrimaryToken<Ability>, PostDeferredToken<Ability>
{

    @Override
    public String getTokenName()
    {
        return "MULT";
    }

    @Override
    protected ObjectKey<Boolean> getObjectKey()
    {
        return ObjectKey.MULTIPLE_ALLOWED;
    }

    @Override
    public Class<Ability> getTokenClass()
    {
        return Ability.class;
    }

    @Override
    public Class<Ability> getDeferredTokenClass()
    {
        return Ability.class;
    }

    @Override
    public boolean process(LoadContext context, Ability a)
    {
        if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
        {
            if (a.get(ObjectKey.CHOOSE_INFO) == null)
            {
                Logging.errorPrint(
                        "Ability (" + a.getCategory() + ") " + a.getKeyName() + " had MULT:YES but no CHOOSE", context);
                return false;
            }
            if (a.getKeyName().contains("("))
            {
                String base = AbilityUtilities.removeChoicesFromName(a.getKeyName());
                Ability conflict =
                        context.getReferenceContext().getManufacturerId(a.getCDOMCategory()).getActiveObject(base);
                if ((conflict != null) && conflict.getSafe(ObjectKey.MULTIPLE_ALLOWED))
                {
                    Logging.errorPrint("Ability (" + a.getCategory() + ") " + conflict.getKeyName()
                            + " had MULT:YES which " + "prohibits Ability Key with same base "
                            + "and parenthesis, but data included: " + a.getKeyName(), context);
                    return false;
                }
            }
        } else
        {
            if (a.get(ObjectKey.CHOOSE_INFO) != null)
            {
                Logging.errorPrint(
                        "Ability (" + a.getCategory() + ") " + a.getKeyName() + " had MULT:NO but did have CHOOSE",
                        context);
                return false;
            }
        }
        return false;
    }

    @Override
    public int getPriority()
    {
        return 1000;
    }
}
