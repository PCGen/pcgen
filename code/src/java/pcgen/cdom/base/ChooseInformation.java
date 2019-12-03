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

import java.util.Collection;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.rules.context.LoadContext;

import org.jetbrains.annotations.NotNull;

/**
 * This is a interface is provided as convenience to hold a set of choices and the number
 * of choices allowed for the 5.x/6.x CHOOSE system.
 * 
 * This is a TransitionChoice that is designed to be stored in a PlayerCharacter file when
 * saved. Thus, encoding and decoding (to a 'persistent' string) methods are provided.
 * 
 * @param <T> The Format (class) of object to be chosen
 */
public interface ChooseInformation<T>
{

	/**
	 * Returns the name of this ChooseInformation. This matches the subtoken name used
	 * under CHOOSE.
	 * 
	 * @return The name of this ChooseInformation
	 */
    String getName();

	/**
	 * Returns the persistent LST format for this ChooseInformation. This allows it to be
	 * unparsed.
	 * 
	 * @return The persistent LST format for this ChooseInformation
	 */
    String getLSTformat();

	/**
	 * Returns the title of this ChooseInformation. This is to be displayed to the user.
	 * 
	 * @return The title of this ChooseInformation
	 */
    String getTitle();

	/**
	 * Returns the GroupingState of this ChooseInformation. This allows validation that
	 * the total items placed into the CHOOSE token are a valid combination
	 * 
	 * @return The GroupingState of this ChooseInformation
	 */
    GroupingState getGroupingState();

	/**
	 * Returns the reference Class of this ChooseInformation, indicating the class of item
	 * to be chosen.
	 * 
	 * @return The reference Class of this ChooseInformation
	 */
    Class<? super T> getReferenceClass();

	/**
	 * Returns the persistent Format of the ClassIdentity of the objects to be chosen.
	 * 
	 * NOTE: Behavior of this method is not guaranteed if a valid Format cannot be
	 * discerned for the contents of this ChooseInformation. You may wish to consult with
	 * getReferenceClass before using this method.
	 * 
	 * @return The persistent Format of the ClassIdentity of the objects to be chosen
	 */
    String getPersistentFormat();

	/**
	 * Returns the Collection of objects defined by this ChooseInformation for the given
	 * PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the Collection of available choices should
	 *            be determined
	 * @return The Collection of objects defined by this ChooseInformation for the given
	 *         PlayerCharacter
	 */
    Collection<? extends T> getSet(PlayerCharacter pc);

	void restoreChoice(PlayerCharacter pc, ChooseDriver owner, T item);

	void removeChoice(PlayerCharacter pc, ChooseDriver owner, T item);

	ChoiceManagerList<T> getChoiceManager(ChooseDriver owner, int cost);

	CharSequence composeDisplay(@NotNull Collection<? extends T> collection);

	T decodeChoice(LoadContext context, String persistentFormat);

	String encodeChoice(T item);

	void setChoiceActor(Chooser<T> actor);

	Chooser<T> getChoiceActor();

}
