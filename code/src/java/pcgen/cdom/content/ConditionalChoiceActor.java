/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * A ConditionalChoiceActor is a Decorator on a ChooseResultActor that provides
 * the ability to make the application conditional on Prerequisites.
 */
public class ConditionalChoiceActor extends ConcretePrereqObject implements
		ChooseResultActor
{

	/**
	 * The underlying ChooseResultActor to be applied to a PlayerCharacter if
	 * the Prerequisites of this ConditionalChoiceActor are met by the
	 * PlayerCharacter.
	 */
	private final ChooseResultActor actor;

	/**
	 * Constructs a new ConditionalChoiceActor with the given ChooseResultActor
	 * as the underlying ChooseResultActor to be applied to a PlayerCharacter if
	 * the Prerequisites of this ConditionalChoiceActor are met by the
	 * PlayerCharacter.
	 * 
	 * @param cra
	 *            The ChooseResultActor to be applied to a PlayerCharacter if
	 *            the Prerequisites of this ConditionalChoiceActor are met by
	 *            the PlayerCharacter.
	 */
	public ConditionalChoiceActor(ChooseResultActor cra)
	{
		if (cra == null)
		{
			throw new IllegalArgumentException("Cannot have null ChoiceActor");
		}
		actor = cra;
	}

	/**
	 * Applies the given choice to the given PlayerCharacter if the
	 * PlayerCharacter meets the Prerequisites stored in this
	 * ConditionalChoiceActor.
	 * 
	 * @param pc
	 *            The PlayerCharacter to which the given choice should be
	 *            applied.
	 * @param cdo
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being applied to the given PlayerCharacter
	 */
	@Override
	public void apply(PlayerCharacter pc, CDOMObject cdo, String choice)
	{
		if (qualifies(pc, cdo))
		{
			actor.apply(pc, cdo, choice);
		}
	}

	/**
	 * Removes the given choice from the given PlayerCharacter. The given choice
	 * is removed unconditionally (regardless of the Prerequisites in this
	 * ConditionalChoiceActor). This unconditional removal is required in order
	 * to avoid any Prerequisites later acquired (including result from the
	 * choice itself) from preventing removal.
	 * 
	 * @param pc
	 *            The PlayerCharacter from which the given choice should be
	 *            removed.
	 * @param cdo
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being removed from the given PlayerCharacter
	 */
	@Override
	public void remove(PlayerCharacter pc, CDOMObject cdo, String choice)
	{
		actor.remove(pc, cdo, choice);
	}

	/**
	 * Returns true if the given object is a ConditionalChoiceActor with
	 * identical underlying ChooseResultActor and Prerequisites.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ConditionalChoiceActor)
		{
			ConditionalChoiceActor other = (ConditionalChoiceActor) obj;
			return actor.equals(other.actor) && equalsPrereqObject(other);
		}
		return false;
	}

	/**
	 * Returns a consistent-with-equals hashCode for this ConditionalChoiceActor
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return actor.hashCode();
	}

	/**
	 * Returns the source of this ConditionalChoiceActor. Provided primarily to
	 * allow the Token/Loader system to properly identify the source of
	 * ConditionalChoiceActors for purposes of unparsing.
	 * 
	 * @return The source of this ConditionalChoiceActor
	 */
	@Override
	public String getSource()
	{
		return actor.getSource();
	}

	/**
	 * Returns the LST format for this ConditionalChoiceActor. Provided
	 * primarily to allow the Token/Loader system to properly unparse the
	 * ConditionalChoiceActor.
	 * 
	 * @return The LST format of this ConditionalChoiceActor
	 */
	@Override
	public String getLstFormat() throws PersistenceLayerException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(actor.getLstFormat());
		if (hasPrerequisites())
		{
			sb.append('[').append(
					new PrerequisiteWriter()
							.getPrerequisiteString(getPrerequisiteList()))
					.append(']');
		}
		return sb.toString();
	}

}
