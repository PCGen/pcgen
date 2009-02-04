/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 */
package pcgen.cdom.base;

import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;

/**
 * A ChooseResultActor is an object that can apply and remove choices (based on
 * the CHOOSE token) to a PlayerCharacter. This is an object that will act after
 * a selection has been made by a user through through the chooser system.
 */
public interface ChooseResultActor
{

	/**
	 * Applies the given choice to the given PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter to which the given choice should be
	 *            applied.
	 * @param obj
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being applied to the given PlayerCharacter
	 */
	void apply(PlayerCharacter pc, CDOMObject obj, String choice);

	/**
	 * Removes the given choice from the given PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter from which the given choice should be
	 *            removed.
	 * @param obj
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being removed from the given PlayerCharacter
	 */
	void remove(PlayerCharacter pc, CDOMObject obj, String choice);

	/**
	 * Returns the source of this ChooseResultActor. Provided primarily to allow
	 * the Token/Loader system to properly identify the source of
	 * ChooseResultActors for purposes of unparsing.
	 * 
	 * @return The source of this ChooseResultActor
	 */
	String getSource();

	/**
	 * Returns the LST format for this ChooseResultActor. Provided primarily to
	 * allow the Token/Loader system to properly unparse the ChooseResultActor.
	 * 
	 * @return The LST format of this ChooseResultActor
	 */
	String getLstFormat() throws PersistenceLayerException;

}
