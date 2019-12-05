/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.util.StringPClassUtil;
import pcgen.util.chooser.ChooserFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This is a transitional class from PCGen 5.15+ to the final CDOM core. It is
 * provided as convenience to hold a set of choices and the number of choices
 * allowed, prior to final implementation of the new choice system
 *
 * @param <T> The type of object that will be chosen when this TransitionChoice
 *            is used
 */
public class ConcreteTransitionChoice<T> implements TransitionChoice<T>
{

    /**
     * The underlying SelectableSet used to determine the choices available when
     * selections are to be made in this TransitionChoice.
     */
    private final SelectableSet<? extends T> choices;

    /**
     * The Formula indicating the number of choices to be made when selections
     * are made in this TransitionChoice.
     */
    private final Formula choiceCount;

    /**
     * IDentifies if this TransitionChoice selection is required - if it is
     * required, then the user cannot dismiss the dialog box without making a
     * choice (or the dialog box reappears, etc.)
     */
    private boolean required = true;

    /**
     * The ChoiceActor (optional) which will act upon any choices made from this
     * TransitionChoice.
     */
    private ChoiceActor<T> choiceActor;

    /**
     * Identifies if this TransitionChoice allows stacking of the same object.
     * <p>
     * This is typically done with Abilities, which has the STACKS: token in
     * order to identify stackable Abilities. Note that this field only allows
     * stacking, it does not enable stacking of objects which are not generally
     * stackable.
     */
    private boolean allowStack = false;

    /**
     * Identifies any limit to stacking in this TransitionChoice. This is only
     * enabled if allowStack is true, and limits the number of times a single
     * object may be stacked in this selection.
     */
    private Integer stackLimit = null;

    /**
     * Constructs a new TransitionChoice with the given SelectableSet (of
     * possible choices) and Formula (indicating the number of choices that may
     * be taken)
     *
     * @param set   The SelectableSet indicating the choices available in this
     *              TransitionChoice.
     * @param count The Formula indicating the number of choices that may be
     *              selected when selections are made in this TransitionChoice.
     */
    public ConcreteTransitionChoice(SelectableSet<? extends T> set, Formula count)
    {
        choices = set;
        choiceCount = count;
    }

    /**
     * Returns the SelectableSet for this TransitionChoice.
     * <p>
     * TODO Should determine if this should be exposed. It seems this is
     * primarily used to get access to getLSTformat and getChoiceClass, so
     * perhaps the TransitionChoice should delegate those instead in order to
     * protect the SelectableSet?
     *
     * @return The SelectableSet for this TransitionChoice.
     */
    @Override
    public SelectableSet<? extends T> getChoices()
    {
        return choices;
    }

    /**
     * Returns the Formula indicating the number of selections available when
     * selections are made in this TransitionChoice.
     *
     * @return The Formula indicating the number of selections available
     */
    @Override
    public Formula getCount()
    {
        return choiceCount;
    }

    /**
     * Returns true if the given Object is a TransitionChoice and has identical
     * underlying choices and choiceCount
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ConcreteTransitionChoice)
        {
            ConcreteTransitionChoice<?> other = (ConcreteTransitionChoice<?>) obj;
            if (Objects.equals(choiceCount, other.choiceCount))
            {
                return choices.equals(other.choices);
            }
        }
        return false;
    }

    /**
     * Returns a consistent-with-equals hashCode for this TransitionChoice.
     */
    @Override
    public int hashCode()
    {
        return choices.hashCode() + 29 * (choiceCount == null ? -1 : choiceCount.hashCode());
    }

    /**
     * Drives a selection for this TransitionChoice on the given
     * PlayerCharacter.
     *
     * @param pc The PlayerCharacter for which this TransitionChoice should
     *           drive a choice.
     * @return A Collection of objects of the type that this TransitionChoice
     * selects.
     */
    @Override
    public Collection<? extends T> driveChoice(PlayerCharacter pc)
    {
        int numChoices = choiceCount.resolve(pc, "").intValue();
        boolean pickall = (numChoices == Integer.MAX_VALUE);

        String title = choices.getTitle();
        if (title == null)
        {
            title = "Choose a " + StringPClassUtil.getStringFor(choices.getChoiceClass());
        }

        Collection<? extends T> set = choices.getSet(pc);
        Set<T> allowed = new LinkedHashSet<>();
        List<Object> assocList = pc.getAssocList(this, AssociationListKey.ADD);
        for (T item : set)
        {
            if (choiceActor == null || choiceActor.allow(item, pc, allowStack))
            {
                if (assocList != null && stackLimit != null && stackLimit > 0)
                {
                    int takenCount = 0;
                    for (Object choice : assocList)
                    {
                        if (choice.equals(item))
                        {
                            takenCount++;
                        }
                    }
                    if (stackLimit <= takenCount)
                    {
                        continue;
                    }
                }
                allowed.add(item);
            }
        }

        //TODO: What about allowing duplicates?
        if (pickall || numChoices == set.size())
        {
            return allowed;
        } else
        {
            CDOMChooserFacadeImpl<T> chooserFacade =
                    new CDOMChooserFacadeImpl<>(title, new ArrayList<>(allowed), new ArrayList<>(), numChoices);
            chooserFacade.setAllowsDups(allowStack);
            chooserFacade.setRequireCompleteSelection(required);
            chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
            ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
            //TODO: What about cancel? Should it be allowed?
            return chooserFacade.getFinalSelected();
        }
    }

    /**
     * Sets whether a selection from this TransitionChoice is required. If
     * required, a TransitionChoice will not exit the driveChoice method until
     * the user has made a selection.
     *
     * @param isRequired true if a selection from this TransitionChoice should be
     *                   required.
     */
    @Override
    public void setRequired(boolean isRequired)
    {
        required = isRequired;
    }

    /**
     * Sets the (optional) ChoiceActor for this TransitionChoice. The
     * ChoiceActor will be called when the act method of TransitionChoice is
     * called. If the ChoiceActor is not set, then the set method may not be
     * used without triggering an exception.
     *
     * @param actor The ChoiceActor for this TransitionChoice.
     */
    @Override
    public void setChoiceActor(ChoiceActor<T> actor)
    {
        choiceActor = actor;
    }

    /**
     * Acts upon choices made in this TransitionChoice.
     *
     * @param choicesMade The choices on which this TransitionChoice should act.
     * @param owner       The owning object for this TransitionChoice
     * @param apc         The PlayerCharacter to which the choices should be applied.
     */
    @Override
    public void act(Collection<? extends T> choicesMade, CDOMObject owner, PlayerCharacter apc)
    {
        if (choiceActor == null)
        {
            throw new IllegalStateException("Cannot act without a defined ChoiceActor");
        }
        for (T choice : choicesMade)
        {
            choiceActor.applyChoice(owner, choice, apc);
            apc.addAssoc(this, AssociationListKey.ADD, choice);
        }
    }

    /**
     * Casts an object to the (Generic) Type of this TransitionChoice.
     *
     * @param item The incoming object
     * @return The incoming object, cast to the (Generic) type of this
     * TransitionChoice.
     */
    @SuppressWarnings("unchecked")
    public T castChoice(Object item)
    {
        return (T) item;
    }

    /**
     * Sets whether this TransitionChoice should allow stacking.
     *
     * @param allow true if this TransitionChoice should allow stacking; false
     *              otherwise.
     */
    @Override
    public void allowStack(boolean allow)
    {
        allowStack = allow;
    }

    /**
     * Sets the stacking limit of this TransitionChoice. This is only enabled if
     * allowStack is set to true. This limits the number of times an individual
     * item can stack in a given TransitionChoice.
     *
     * @param limit The limit (number of times a stackable item may be selected in
     *              this TransitionChoice)
     */
    @Override
    public void setStackLimit(int limit)
    {
        stackLimit = limit;
    }

    /**
     * Identifies if this TransitionChoice allows stacking.
     *
     * @return true if this TransitionChoice should allow stacking; false
     * otherwise.
     */
    @Override
    public boolean allowsStacking()
    {
        return allowStack;
    }

    /**
     * Returns the Stacking Limit of this TransitionChoice. This is only enabled
     * if allowStack is set to true. This limits the number of times an
     * individual item can stack in a given TransitionChoice.
     *
     * @return The limit (number of times a stackable item may be selected in
     * this TransitionChoice)
     */
    @Override
    public Integer getStackLimit()
    {
        return stackLimit;
    }

    /**
     * Returns the ChoiceActor for this TransitionChoice.
     * <p>
     * CONSIDER Should look at another method to get rid of this - do the users
     * of this method require their own sub-class to TransitionChoice?
     *
     * @return The ChoiceActor for this TransitionChoice.
     */
    @Override
    public ChoiceActor<T> getChoiceActor()
    {
        return choiceActor;
    }
}
