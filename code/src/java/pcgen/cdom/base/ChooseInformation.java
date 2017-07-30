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

import org.jetbrains.annotations.NotNull;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.rules.context.LoadContext;

import java.util.Collection;

/**
 * This is a transitional class from PCGen 5.15+ to the final CDOM core. It is
 * provided as convenience to hold a set of choices and the number of choices
 * allowed, prior to final implementation of the new choice system.
 * 
 * This is a TransitionChoice that is designed to be stored in a PlayerCharacter
 * file when saved. Thus, encoding and decoding (to a 'persistent' string)
 * methods are provided.
 * 
 * @param <T>
 */
public interface ChooseInformation<T>
{

	String getName();

	String getLSTformat();

	String getTitle();

	GroupingState getGroupingState();

	ClassIdentity<? super T> getClassIdentity();

	Collection<? extends T> getSet(PlayerCharacter pc);
	
	void restoreChoice(PlayerCharacter pc, ChooseDriver owner, T item);

	void removeChoice(PlayerCharacter pc, ChooseDriver owner, T item);

	ChoiceManagerList<T> getChoiceManager(ChooseDriver owner, int cost);

	CharSequence composeDisplay(@NotNull Collection<? extends T> collection);



	public T decodeChoice(LoadContext context, String persistentFormat);

	public String encodeChoice(T item);


	public void setChoiceActor(Chooser<T> actor);

	public Chooser<T> getChoiceActor();

}

