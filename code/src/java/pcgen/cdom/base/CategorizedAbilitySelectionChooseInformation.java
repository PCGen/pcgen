/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.cdom.choiceset.CollectionToAbilitySelection;
import pcgen.cdom.content.AbilitySelection;
import pcgen.core.AbilityCategory;

/**
 * CategorizedAbilitySelectionChooseInformation
 */
public class CategorizedAbilitySelectionChooseInformation extends BasicChooseInformation<AbilitySelection>
{

    private CollectionToAbilitySelection casChoiceSet;

    /**
     * Create a new CategorizedAbilitySelectionChooseInformation instance
     * indicating the name of the choice and the objects to be chosen from.
     *
     * @param name   The name of this ChoiceSet
     * @param choice The PrimitiveChoiceSet indicating the Collection of objects
     *               for this ChoiceSet
     * @throws IllegalArgumentException if the given name or PrimitiveChoiceSet is null
     */
    public CategorizedAbilitySelectionChooseInformation(String name, CollectionToAbilitySelection choice)
    {
        super(name, choice, choice.getCategory().getPersistentFormat());
        this.casChoiceSet = choice;
    }

    /**
     * @return The ability category of the choices.
     */
    public AbilityCategory getCategory()
    {
        return casChoiceSet.getCategory();
    }

}
