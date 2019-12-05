/*
 * Copyright 2010-15 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.pobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.Converter;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.util.Logging;

/**
 * AbilityToken implements the Ability primitive, e.g.:
 * <p>
 * CHOOSE:SKILL|ABILITY=FEAT[SkillChoiceThingy]
 * <p>
 * The "ABILITY=Category[Key]" section of the CHOOSE above is implemented in
 * this class.
 * <p>
 * The contents of this primitive refer to selections made in another object. In
 * the case of the example CHOOSE above, any selections made in the CHOOSE:SKILL
 * present in the Feat "SkillChoiceThingy" will be available for selection in
 * the object on which the example CHOOSE is present. (In practice this is often
 * used in things like Weapon Mastery)
 *
 * @param <T> The type of object on which this Primitive can be used (in this
 *            case, CDOMObject, i.e. anywhere CHOOSE is legal)
 */
public class AbilityToken<T> implements PrimitiveToken<T>
{

    private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

    private CDOMSingleRef<Ability> ref;

    private Category<Ability> category;

    private Class<T> refClass;

    @Override
    public boolean initialize(LoadContext context, Class<T> cl, String value, String args)
    {
        if (args == null)
        {
            Logging.errorPrint("Syntax for ABILITY primitive is ABILITY=category[key]");
            return false;
        }
        Category<Ability> cat =
                context.getReferenceContext().silentlyGetConstructedCDOMObject(ABILITY_CATEGORY_CLASS, value);
        category = cat;
        ref = context.getReferenceContext().getManufacturerId(cat).getReference(args);
        refClass = cl;
        return true;
    }

    @Override
    public String getTokenName()
    {
        return "ABILITY";
    }

    @Override
    public Class<? super T> getReferenceClass()
    {
        if (refClass == null)
        {
            return Object.class;
        } else
        {
            return refClass;
        }
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return "ABILITY=" + category.getKeyName() + '[' + ref.getLSTformat(useAny) + ']';
    }

    private <R> List<R> getList(PlayerCharacter pc, Ability a)
    {
        // workaround for cloning issue
        List<R> availableList = new ArrayList<>();
        List<CNAbility> theFeats = pc.getMatchingCNAbilities(a);
        for (CNAbility ability : theFeats)
        {
            @SuppressWarnings("unchecked")
            List<? extends R> list = (List<? extends R>) pc.getDetailedAssociations(ability);
            if (list != null)
            {
                availableList.addAll(list);
            }
        }
        return availableList;
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof AbilityToken)
        {
            AbilityToken<?> other = (AbilityToken<?>) obj;
            if (ref == null)
            {
                return (other.ref == null) && (refClass == null) && (other.refClass == null);
            }
            return refClass.equals(other.refClass) && ref.equals(other.ref);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return (ref == null) ? -57 : ref.hashCode();
    }

    @Override
    public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<T, R> c)
    {
        /*
         * In theory the converter can be ignored here, since an equivalent
         * would exist within the ChooseInformation
         */
        List<R> currentItems = getList(pc, ref.get());
        if (currentItems == null)
        {
            return Collections.emptySet();
        }
        return new HashSet<>(currentItems);
    }

}
