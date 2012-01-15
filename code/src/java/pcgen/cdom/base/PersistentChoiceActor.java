/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.core.PlayerCharacter;

/**
 * A PersistentChoiceActor is a ChoiceActor that is designed to be saved and
 * restored with a PlayerCharacter. This is used in situations where certain
 * relationship information (e.g. associations) needs to be uniquely restored
 * when a PlayerCharacter is loaded from a persistent state (such as a save
 * file)
 * 
 * @param <T>
 *            The type of object that this PersistentChoiceActor can apply to a
 *            PlayerCharacter
 */
public interface PersistentChoiceActor<T> extends ChoiceActor<T>
{
	/**
	 * Encodes the given choice into a String sufficient to uniquely identify
	 * the choice. This may not sufficiently encode to be stored into a file or
	 * format which restricts certain characters (such as URLs), it simply
	 * encodes into an identifying String. There is no guarantee that this
	 * encoding is human readable, simply that the encoding is uniquely
	 * identifying such that the decodeChoice method of the PersistentChoiceActor
	 * is capable of decoding the String into the choice object.
	 * 
	 * @param item
	 *            The choice which should be encoded into a String sufficient to
	 *            identify the choice.
	 * 
	 * @return A String sufficient to uniquely identify the choice.
	 */
	public String encodeChoice(T item);

	/**
	 * Decodes a given String into a choice of the appropriate type. The String
	 * format to be passed into this method is defined solely by the return
	 * result of the encodeChoice method. There is no guarantee that the
	 * encoding is human readable, simply that the encoding is uniquely
	 * identifying such that this method is capable of decoding the String into
	 * the choice object.
	 * 
	 * @param persistentFormat
	 *            The String which should be decoded to provide the choice of
	 *            the appropriate type.
	 * 
	 * @return A choice object of the appropriate type that was encoded in the
	 *         given String.
	 */
	public T decodeChoice(String persistentFormat);

	/**
	 * Restores a choice to a PlayerCharacter. This method re-applies a choice
	 * when a PlayerCharacter is restored from a persistent state (the
	 * applyChoice method of ChoiceActor having been used to first apply the
	 * choice to a PlayerCharacter).
	 * 
	 * @param pc
	 *            The PlayerCharacter to which the choice should be restored.
	 * @param owner
	 *            The owning object of the choice being restored.
	 * @param item
	 *            The choice being restored to the given PlayerCharacter.
	 */
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner, T item);

	/**
	 * Removes a choice from a PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter from which the choice should be removed.
	 * @param owner
	 *            The owning object of the choice being removed.
	 * @param item
	 *            The choice being removed from the given PlayerCharacter.
	 */
	public void removeChoice(PlayerCharacter pc, CDOMObject owner, T item);
}
