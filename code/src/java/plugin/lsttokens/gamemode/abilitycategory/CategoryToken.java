/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.abilitycategory;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class CategoryToken implements CDOMPrimaryToken<AbilityCategory>, DeferredToken<AbilityCategory>
{

    @Override
    public String getTokenName()
    {
        return "CATEGORY";
    }

    @Override
    public ParseResult parseToken(LoadContext context, AbilityCategory ac, String value)
    {
        CDOMSingleRef<AbilityCategory> cat =
                context.getReferenceContext().getCDOMReference(AbilityCategory.class, value);
        ac.setAbilityCategory(cat);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AbilityCategory ac)
    {
        CDOMSingleRef<AbilityCategory> cat = ac.getAbilityCatRef();
        if (cat == null)
        {
            return null;
        }
        return new String[]{cat.getLSTformat(false)};
    }

    @Override
    public Class<AbilityCategory> getTokenClass()
    {
        return AbilityCategory.class;
    }

    @Override
    public Class<AbilityCategory> getDeferredTokenClass()
    {
        return AbilityCategory.class;
    }

    @Override
    public boolean process(LoadContext context, AbilityCategory ac)
    {
        CDOMSingleRef<AbilityCategory> parent = ac.getAbilityCatRef();
        if (parent == null)
        {
            Logging.log(Logging.LST_ERROR, "All Ability Categories must have a CATEGORY token. " + ac.getKeyName()
                    + " of " + context.getSourceURI() + " did not");
            return false;
        }
        String parentCat = parent.getLSTformat(false);
        if (!ac.getTypes().isEmpty() && parentCat.equalsIgnoreCase(ac.getKeyName()))
        {
            Logging.log(Logging.LST_ERROR, "TYPE " + ac.getTypes() + " is not valid in 'parent' category "
                    + ac.getKeyName() + " of " + context.getSourceURI() + Constants.DOT);
            return false;
        }
        if (ac.isAllAbilityTypes() && parentCat.equalsIgnoreCase(ac.getKeyName()))
        {
            Logging.log(Logging.LST_ERROR, "TYPE '*' is not valid in 'parent' category " + ac.getKeyName() + " of "
                    + context.getSourceURI() + Constants.DOT);
            return false;
        }

        if (ac.hasDirectReferences() && parentCat.equalsIgnoreCase(ac.getKeyName()))
        {
            Logging.log(Logging.LST_ERROR, "ABILITYLIST is not valid in 'parent' category " + ac.getKeyName() + " of "
                    + context.getSourceURI() + Constants.DOT);
            return false;
        }
        // Must be a universal set if no types
        if (ac.getTypes().isEmpty() && !ac.hasDirectReferences() && !ac.isAllAbilityTypes())
        {
            if (!parentCat.equalsIgnoreCase(ac.getKeyName()))
            {
                Logging.log(Logging.LST_ERROR, "Ability Category " + ac.getKeyName() + " had no TYPE or ABILITYLIST, "
                        + "but has a different CATEGORY.  File was: " + context.getSourceURI());
                return false;
            }
        }
        return true;
    }
}
