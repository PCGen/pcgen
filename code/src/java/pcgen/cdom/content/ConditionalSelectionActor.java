/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * A ConditionalSelectionActor is a Decorator on a ChooseSelectionActor that provides
 * the ability to make the application conditional on Prerequisites.
 * 
 * @param <T> The type of object selected by this ConditionalSelectionActor
 */
public class ConditionalSelectionActor<T> extends ConcretePrereqObject implements ChooseSelectionActor<T>
{

	/**
	 * The underlying ChooseSelectionActor to be applied to a PlayerCharacter if
	 * the Prerequisites of this ConditionalSelectionActor are met by the
	 * PlayerCharacter.
	 */
	private final ChooseSelectionActor<T> actor;

	/**
	 * Constructs a new ConditionalSelectionActor with the given
	 * ChooseSelectionActor as the underlying ChooseSelectionActor to be applied
	 * to a PlayerCharacter if the Prerequisites of this
	 * ConditionalSelectionActor are met by the PlayerCharacter.
	 * 
	 * @param csa
	 *            The ChooseSelectionActor to be applied to a PlayerCharacter if
	 *            the Prerequisites of this ConditionalSelectionActor are met by
	 *            the PlayerCharacter.
	 */
	public ConditionalSelectionActor(ChooseSelectionActor<T> csa)
	{
		Objects.requireNonNull(csa, "Cannot have null ChoiceActor");
		actor = csa;
	}

	/**
	 * Applies the given choice to the given PlayerCharacter if the
	 * PlayerCharacter meets the Prerequisites stored in this
	 * ConditionalSelectionActor.
	 * 
	 * @param cdo
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being applied to the given PlayerCharacter
	 * @param pc
	 *            The PlayerCharacter to which the given choice should be
	 *            applied.
	 */
	@Override
	public void applyChoice(ChooseDriver cdo, T choice, PlayerCharacter pc)
	{
		if (qualifies(pc, cdo))
		{
			actor.applyChoice(cdo, choice, pc);
		}
	}

	/**
	 * Removes the given choice from the given PlayerCharacter. The given choice
	 * is removed unconditionally (regardless of the Prerequisites in this
	 * ConditionalSelectionActor). This unconditional removal is required in order
	 * to avoid any Prerequisites later acquired (including result from the
	 * choice itself) from preventing removal.
	 * 
	 * @param cdo
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being removed from the given PlayerCharacter
	 * @param pc
	 *            The PlayerCharacter from which the given choice should be
	 *            removed.
	 */
	@Override
	public void removeChoice(ChooseDriver cdo, T choice, PlayerCharacter pc)
	{
		actor.removeChoice(cdo, choice, pc);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ConditionalSelectionActor<?> other)
		{
			return actor.equals(other.actor) && equalsPrereqObject(other);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return actor.hashCode();
	}

	/**
	 * Returns the source of this ConditionalSelectionActor. Provided primarily to
	 * allow the Token/Loader system to properly identify the source of
	 * ConditionalSelectionActors for purposes of unparsing.
	 * 
	 * @return The source of this ConditionalSelectionActor
	 */
	@Override
	public String getSource()
	{
		return actor.getSource();
	}

	/**
	 * Returns the LST format for this ConditionalSelectionActor. Provided
	 * primarily to allow the Token/Loader system to properly unparse the
	 * ConditionalSelectionActor.
	 * 
	 * @return The LST format of this ConditionalSelectionActor
	 * @throws PersistenceLayerException
	 *             if there is a problem converting this
	 *             ConditionalSelectionActor to the LST format
	 */
	@Override
	public String getLstFormat() throws PersistenceLayerException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(actor.getLstFormat());
		if (hasPrerequisites())
		{
			sb.append('|');
			sb.append(new PrerequisiteWriter().getPrerequisiteString(getPrerequisiteList()));
		}
		return sb.toString();
	}

	@Override
	public Class<T> getChoiceClass()
	{
		return actor.getChoiceClass();
	}

	public static <GT> ConditionalSelectionActor<GT> getCSA(ChooseSelectionActor<GT> csa)
	{
		return new ConditionalSelectionActor<>(csa);
	}
}
