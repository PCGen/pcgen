/*
 * Copyright 2010-14 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * A CollectionToAbilitySelection wraps a PrimitiveCollection of Ability objects and
 * provide AbilitySelection objects.
 */
public class CollectionToAbilitySelection implements PrimitiveChoiceSet<AbilitySelection>
{
    /**
     * The underlying collection of Ability objects that are legal to choose from.
     */
    private final PrimitiveCollection<Ability> collection;

    /**
     * The AbilityCategory from which the Ability objects are drawn.
     */
    private final AbilityCategory category;

    /**
     * An infinite loop detection (it's possible a poorly written Ability can CHOOSE
     * itself, thus this would result in an infinite loop of resolution).
     */
    private static Stack<Ability> infiniteLoopDetectionStack = new Stack<>();

    /**
     * Constructs a new CollectionToAbilitySelection for the given AbilityCategory and
     * PrimitiveCollection.
     *
     * @param category   The AbilityCategory from which the Ability objects are drawn
     * @param collection The underlying collection of Ability objects that are legal to choose
     *                   from
     */
    public CollectionToAbilitySelection(AbilityCategory category, PrimitiveCollection<Ability> collection)
    {
        this.category = Objects.requireNonNull(category);
        this.collection = Objects.requireNonNull(collection);
    }

    @Override
    public Class<? super AbilitySelection> getChoiceClass()
    {
        return AbilitySelection.class;
    }

    @Override
    public GroupingState getGroupingState()
    {
        return collection.getGroupingState();
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return collection.getLSTformat(useAny);
    }

    @Override
    public Collection<AbilitySelection> getSet(PlayerCharacter pc)
    {
        Collection<? extends AbilityWithChoice> aColl = collection.getCollection(pc, new ExpandingConverter(pc));
        Set<AbilitySelection> returnSet = new HashSet<>();
        for (AbilityWithChoice a : aColl)
        {
            processAbility(pc, returnSet, a);
        }
        return returnSet;
    }

    private void processAbility(PlayerCharacter character, Set<AbilitySelection> returnSet, AbilityWithChoice awc)
    {
        Ability a = awc.getAbility();
        if (infiniteLoopDetectionStack.contains(a))
        {
            Stack<Ability> current = new Stack<>();
            current.addAll(infiniteLoopDetectionStack);
            Logging.errorPrint("Error: Circular Expansion Found: " + reportCircularExpansion(current));
            return;
        }
        try
        {
            infiniteLoopDetectionStack.push(a);
            if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
            {
                returnSet.addAll(addMultiplySelectableAbility(character, a, awc.getChoice()));
            } else
            {
                returnSet.add(new AbilitySelection(a, null));
            }
        } finally
        {
            infiniteLoopDetectionStack.pop();
        }
    }

    private Collection<AbilitySelection> addMultiplySelectableAbility(final PlayerCharacter aPC, Ability ability,
            String subName)
    {
        boolean isPattern = false;
        String nameRoot = null;
        if (subName != null)
        {
            final int percIdx = subName.indexOf('%');

            if (percIdx > -1)
            {
                isPattern = true;
                nameRoot = subName.substring(0, percIdx);
            } else if (!subName.isEmpty())
            {
                nameRoot = subName;
            }
        }

        ChooseInformation<?> chooseInfo = ability.get(ObjectKey.CHOOSE_INFO);
        final List<String> availableList = getAvailableList(aPC, chooseInfo);

        // Remove any that don't match

        if (nameRoot != null && !nameRoot.isEmpty())
        {
            for (int n = availableList.size() - 1;n >= 0;--n)
            {
                final String aString = availableList.get(n);

                if (!aString.startsWith(nameRoot))
                {
                    availableList.remove(n);
                }
            }

            // Example: ADD:FEAT(Skill Focus(Craft (Basketweaving))) If you
            // have no ranks in Craft (Basketweaving), the available list
            // will
            // be empty
            //
            // Make sure that the specified feat is available, even though
            // it
            // does not meet the prerequisite

            if (isPattern && !availableList.isEmpty())
            {
                availableList.add(nameRoot);
            }
        }

        List<AbilitySelection> returnList = new ArrayList<>(availableList.size());
        for (String s : availableList)
        {
            returnList.add(new AbilitySelection(ability, s));
        }
        return returnList;
    }

    private <T> List<String> getAvailableList(final PlayerCharacter aPC, ChooseInformation<T> chooseInfo)
    {
        final List<String> availableList = new ArrayList<>();
        Collection<? extends T> tempAvailList = chooseInfo.getSet(aPC);
        // chooseInfo may have sent us back weaponprofs, abilities or
        // strings, so we have to do a conversion here
        for (T o : tempAvailList)
        {
            availableList.add(chooseInfo.encodeChoice(o));
        }
        return availableList;
    }

    private String reportCircularExpansion(Stack<Ability> s)
    {
        StringBuilder sb = new StringBuilder(2000);
        processCircularExpansion(sb, s);
        sb.append("    which is a circular reference");
        return sb.toString();
    }

    private void processCircularExpansion(StringBuilder sb, Stack<Ability> s)
    {
        Ability a = s.pop();
        if (!s.isEmpty())
        {
            processCircularExpansion(sb, s);
            sb.append("     which includes");
        }
        sb.append(a.getCDOMCategory()).append(' ').append(a.getKeyName());
        sb.append(" selects items: ");
        sb.append(a.get(ObjectKey.CHOOSE_INFO).getLSTformat());
        sb.append('\n');
    }

    public AbilityCategory getCategory()
    {
        return category;
    }

    @Override
    public int hashCode()
    {
        return collection.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof CollectionToAbilitySelection)
                && ((CollectionToAbilitySelection) obj).collection.equals(collection);
    }

    public static class ExpandingConverter implements Converter<Ability, AbilityWithChoice>
    {

        private final PlayerCharacter character;

        public ExpandingConverter(PlayerCharacter pc)
        {
            character = pc;
        }

        @Override
        public Collection<AbilityWithChoice> convert(ObjectContainer<Ability> ref)
        {
            Set<AbilityWithChoice> returnSet = new HashSet<>();
            for (Ability a : ref.getContainedObjects())
            {
                processAbility(ref, returnSet, a);
            }
            return returnSet;
        }

        private void processAbility(ObjectContainer<Ability> ref, Set<AbilityWithChoice> returnSet, Ability a)
        {
            String choice = null;
            if (ref instanceof CDOMReference)
            {
                choice = ((CDOMReference<?>) ref).getChoice();
            }
            returnSet.add(new AbilityWithChoice(a, choice));
        }

        @Override
        public Collection<AbilityWithChoice> convert(ObjectContainer<Ability> ref, PrimitiveFilter<Ability> lim)
        {
            Set<AbilityWithChoice> returnSet = new HashSet<>();
            for (Ability a : ref.getContainedObjects())
            {
                if (lim.allow(character, a))
                {
                    processAbility(ref, returnSet, a);
                }
            }
            return returnSet;
        }
    }

    /*
     * A custom object, NOT an AbilitySelection. The reasoning is that
     * AbilitySelection does enforcement of MULT:YES to allow/require choices
     * and (1) We can't guarantee we have that here as all expansions may be
     * legal (2) We don't want to compromise on the tight enforcement by
     * AbilitySelection
     */
    private static class AbilityWithChoice
    {

        private final Ability ability;
        private final String choice;

        public AbilityWithChoice(Ability a, String c)
        {
            ability = a;
            choice = c;
        }

        public Ability getAbility()
        {
            return ability;
        }

        public String getChoice()
        {
            return choice;
        }

        @Override
        public int hashCode()
        {
            return ability.hashCode() ^ ((choice == null) ? 17 : choice.hashCode());
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (o instanceof AbilityWithChoice)
            {
                AbilityWithChoice other = (AbilityWithChoice) o;
                if (choice == null)
                {
                    if (other.choice != null)
                    {
                        return false;
                    }
                }
                return ability.equals(other.ability) && ((choice == other.choice) || choice.equals(other.choice));
            }
            return false;
        }
    }
}
