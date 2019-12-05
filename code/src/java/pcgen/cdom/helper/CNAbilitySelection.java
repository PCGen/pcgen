/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.base.Reducible;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.LoadContext;

public class CNAbilitySelection extends ConcretePrereqObject implements QualifyingObject, Reducible
{

    private final CNAbility cna;

    private final String selection;

    public CNAbilitySelection(CNAbility cna)
    {
        this(cna, null);
    }

    public CNAbilitySelection(CNAbility cna, String choice)
    {
        Ability abil = cna.getAbility();
        if (choice != null && !abil.getSafe(ObjectKey.MULTIPLE_ALLOWED))
        {
            throw new IllegalArgumentException(
                    "AbilitySelection " + choice + " with MULT:NO Ability " + abil + " must not have choices");
        }
        if (choice == null && abil.getSafe(ObjectKey.MULTIPLE_ALLOWED))
        {
            throw new IllegalArgumentException(
                    "AbilitySelection with MULT:YES Ability " + abil + ": must have choices");
        }
        this.cna = cna;
        selection = choice;
    }

    public CNAbility getCNAbility()
    {
        return cna;
    }

    public String getSelection()
    {
        return selection;
    }

    public String getAbilityKey()
    {
        return cna.getAbilityKey();
    }

    public boolean containsAssociation(String assoc)
    {
        return Objects.equals(assoc, selection);
    }

    public String getPersistentFormat()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append("CATEGORY=");
        sb.append(cna.getAbilityCategory().getKeyName());
        sb.append('|');
        sb.append("NATURE=");
        sb.append(cna.getNature());
        sb.append('|');
        sb.append(cna.getAbilityKey());
        if (selection != null)
        {
            sb.append('|');
            sb.append(selection);
        }
        return sb.toString();
    }

    public static CNAbilitySelection getAbilitySelectionFromPersistentFormat(LoadContext context,
            String persistentFormat)
    {
        StringTokenizer st = new StringTokenizer(persistentFormat, Constants.PIPE);
        String catString = st.nextToken();
        if (!catString.startsWith("CATEGORY="))
        {
            throw new IllegalArgumentException("String in getAbilitySelectionFromPersistentFormat "
                    + "must start with CATEGORY=, found: " + persistentFormat);
        }
        String cat = catString.substring(9);
        AbilityCategory ac = SettingsHandler.getGame().getAbilityCategory(cat);
        if (ac == null)
        {
            throw new IllegalArgumentException(
                    "Category in getAbilitySelectionFromPersistentFormat " + "must exist found: " + cat);
        }
        String natureString = st.nextToken();
        if (!natureString.startsWith("NATURE="))
        {
            throw new IllegalArgumentException("Second argument in String in getAbilitySelectionFromPersistentFormat "
                    + "must start with NATURE=, found: " + persistentFormat);
        }
        String natString = natureString.substring(7);
        Nature nat = Nature.valueOf(natString);
        String ab = st.nextToken();
        Ability a = context.getReferenceContext().getManufacturerId(ac).getActiveObject(ab);
        if (a == null)
        {
            throw new IllegalArgumentException("Third argument in String in getAbilitySelectionFromPersistentFormat "
                    + "must be an Ability, but it was not found: " + persistentFormat);
        }
        String sel = null;
        if (st.hasMoreTokens())
        {
            /*
             * No need to check for MULT:YES/NO here, as that is checked
             * implicitly in the construction of AbilitySelection below
             */
            sel = st.nextToken();
        } else if (persistentFormat.endsWith(Constants.PIPE))
        {
            // Handle the StringTokenizer ignoring blank tokens at the end
            sel = "";
        }
        if (st.hasMoreTokens())
        {
            throw new IllegalArgumentException("String in getAbilitySelectionFromPersistentFormat "
                    + "must have 3 or 4 arguments, but found more: " + persistentFormat);
        }
        CNAbility cna = CNAbilityFactory.getCNAbility(ac, nat, a);
        return new CNAbilitySelection(cna, sel);
    }

    public String getFullAbilityKey()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append(cna.getAbilityKey());
        if (selection != null && !selection.isEmpty())
        {
            sb.append('(');
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        sb.append(cna.getAbility().getDisplayName());
        if (selection != null && !selection.isEmpty())
        {
            sb.append('(');
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    @Override
    public int hashCode()
    {
        return cna.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CNAbilitySelection)
        {
            CNAbilitySelection other = (CNAbilitySelection) o;
            if (selection == null)
            {
                if (other.selection != null)
                {
                    return false;
                }
            } else
            {
                if (!selection.equals(other.selection))
                {
                    return false;
                }
            }
            return cna.equals(other.cna);
        }
        return false;
    }

    @Override
    public CDOMObject getCDOMObject()
    {
        return cna.getCDOMObject();
    }
}
