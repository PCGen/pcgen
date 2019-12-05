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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.helper.SpellLevel;
import pcgen.cdom.helper.SpellLevelInfo;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.CDOMChoiceManager;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.rules.context.LoadContext;

import org.jetbrains.annotations.NotNull;

/**
 * This is a transitional class from PCGen 5.15+ to the final CDOM core. It is
 * provided as convenience to hold a set of choices and the number of choices
 * allowed, prior to final implementation of the new choice system.
 * <p>
 * This is a TransitionChoice that is designed to be stored in a PlayerCharacter
 * file when saved. Thus, encoding and decoding (to a 'persistent' string)
 * methods are provided.
 */
public class SpellLevelChooseInformation implements ChooseInformation<SpellLevel>
{

    private final List<SpellLevelInfo> info;

    /**
     * The name of this ChoiceSet
     */
    private final String setName;

    /**
     * The title (presented to the user) of this ChoiceSet
     */
    private String title = null;

    /**
     * The PersistentChoiceActor (optional) which will act upon any choices made
     * from this PersistentTransitionChoice.
     */
    private Chooser<SpellLevel> choiceActor;

    /**
     * Constructs a new TransitionChoice with the given ChoiceSet (of possible
     * choices) and Formula (indicating the number of choices that may be taken)
     *
     * @param name   The name of this ChoiceSet
     * @param choice The PrimitiveChoiceSet indicating the Collection of objects
     *               for this ChoiceSet
     * @throws IllegalArgumentException if the given name or PrimitiveChoiceSet is null
     */
    public SpellLevelChooseInformation(String name, List<SpellLevelInfo> choice)
    {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(choice, "PrimitiveChoiceSet cannot be null");
        // Need to populate info first to avoid thread issues
        info = new ArrayList<>(choice);
        if (info.isEmpty())
        {
            throw new IllegalArgumentException("PrimitiveChoiceSet cannot be null");
        }
        setName = name;
    }

    /**
     * Sets the (optional) ChoiceActor for this TransitionChoice. The
     * ChoiceActor will be called when the act method of TransitionChoice is
     * called. If the ChoiceActor is not set, then the set method may not be
     * used without triggering an exception.
     *
     * @param actor The ChoiceActor for this TransitionChoice.
     * @throws ClassCastException if the given ChoiceActor is not a PersistentChoiceActor
     */
    @Override
    public void setChoiceActor(Chooser<SpellLevel> actor)
    {
        choiceActor = actor;
    }

    /**
     * Encodes the given choice into a String sufficient to uniquely identify
     * the choice. This may not sufficiently encode to be stored into a file or
     * format which restricts certain characters (such as URLs), it simply
     * encodes into an identifying String. There is no guarantee that this
     * encoding is human readable, simply that the encoding is uniquely
     * identifying such that the decodeChoice method of the
     * PersistentTransitionChoice is capable of decoding the String into the
     * choice object.
     *
     * @param choice The choice which should be encoded into a String sufficient to
     *               identify the choice.
     * @return A String sufficient to uniquely identify the choice.
     */
    @Override
    public String encodeChoice(SpellLevel choice)
    {
        return choiceActor.encodeChoice(choice);
    }

    /**
     * Decodes a given String into a choice of the appropriate type. The String
     * format to be passed into this method is defined solely by the return
     * result of the encodeChoice method. There is no guarantee that the
     * encoding is human readable, simply that the encoding is uniquely
     * identifying such that this method is capable of decoding the String into
     * the choice object.
     *
     * @param persistenceFormat The String which should be decoded to provide the choice of
     *                          the appropriate type.
     * @return A choice object of the appropriate type that was encoded in the
     * given String.
     */
    @Override
    public SpellLevel decodeChoice(LoadContext context, String persistenceFormat)
    {
        return choiceActor.decodeChoice(context, persistenceFormat);
    }

    @Override
    public Chooser<SpellLevel> getChoiceActor()
    {
        return choiceActor;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SpellLevelChooseInformation)
        {
            SpellLevelChooseInformation other = (SpellLevelChooseInformation) obj;
            if (title == null)
            {
                if (other.title != null)
                {
                    return false;
                }
            } else if (!title.equals(other.title))
            {
                return false;
            }
            return setName.equals(other.setName) && info.equals(other.info);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return setName.hashCode() + 29;
    }

    /**
     * Returns a representation of this ChoiceSet, suitable for storing in an
     * LST file.
     */
    @Override
    public String getLSTformat()
    {
        return StringUtil.join(info, Constants.PIPE);
    }

    /**
     * Returns the Class contained within this ChoiceSet
     *
     * @return the Class contained within this ChoiceSet
     */
    @Override
    public Class<SpellLevel> getReferenceClass()
    {
        return SpellLevel.class;
    }

    /**
     * Returns a Set of objects contained within this ChoiceSet for the given
     * PlayerCharacter.
     *
     * @param pc The PlayerCharacter for which the choices in this ChoiceSet
     *           should be returned.
     * @return a Set of objects contained within this ChoiceSet for the given
     * PlayerCharacter.
     */
    @Override
    public Collection<SpellLevel> getSet(PlayerCharacter pc)
    {
        Set<SpellLevel> list = new HashSet<>();
        for (SpellLevelInfo sli : info)
        {
            list.addAll(sli.getLevels(pc));
        }
        return list;
    }

    /**
     * Returns the name of this ChoiceSet. Note that this name is suitable for
     * display, but it does not represent information that should be stored in a
     * persistent state (it is not sufficient information to reconstruct this
     * ChoiceSet)
     *
     * @return The name of this ChoiceSet
     */
    @Override
    public String getName()
    {
        return setName;
    }

    /**
     * Sets the title of this ChoiceSet. Note that this should be the name that
     * is displayed to the user when a selection from this ChoiceSet is made,
     * but it does not represent information that should be stored in a
     * persistent state (it is not sufficient information to reconstruct this
     * ChoiceSet)
     */
    public void setTitle(String choiceTitle)
    {
        title = choiceTitle;
    }

    /**
     * Returns the title of this ChoiceSet. Note that this should be the name
     * that is displayed to the user when a selection from this ChoiceSet is
     * made, but it does not represent information that should be stored in a
     * persistent state (it is not sufficient information to reconstruct this
     * ChoiceSet)
     *
     * @return The title of this ChoiceSet
     */
    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_NONE;
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, SpellLevel choice)
    {
        choiceActor.restoreChoice(pc, owner, choice);
    }

    @Override
    public ChoiceManagerList<SpellLevel> getChoiceManager(ChooseDriver owner, int cost)
    {
        return new CDOMChoiceManager<>(owner, this, null, cost);
    }

    @Override
    public CharSequence composeDisplay(@NotNull Collection<? extends SpellLevel> collection)
    {
        return ChooseInformationUtilities.buildEncodedString(collection);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver owner, SpellLevel item)
    {
        choiceActor.removeChoice(pc, owner, item);
    }

    @Override
    public String getPersistentFormat()
    {
        return "INVALID*NOT*PERSISTENT*";
    }
}
