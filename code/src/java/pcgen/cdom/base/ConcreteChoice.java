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

import pcgen.rules.context.LoadContext;

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
public class ConcreteChoice<T> implements PersistentChoice<T>
{

	/**
	 * The underlying SelectableSet used to determine the choices available when
	 * selections are to be made in this TransitionChoice.
	 */
	private final SelectableSet<? extends T> choices;

	/**
	 * The PersistentChoiceActor (optional) which will act upon any choices made
	 * from this PersistentTransitionChoice.
	 */
	private PersistentChoiceActor<T> choiceActor;

	/**
	 * Constructs a new TransitionChoice with the given SelectableSet (of
	 * possible choices) and Formula (indicating the number of choices that may
	 * be taken)
	 * 
	 * @param set
	 *            The SelectableSet indicating the choices available in this
	 *            TransitionChoice.
	 */
	public ConcreteChoice(SelectableSet<? extends T> set)
	{
		choices = set;
	}

	/**
	 * Returns the SelectableSet for this TransitionChoice.
	 * 
	 * TODO Should determine if this should be exposed. It seems this is
	 * primarily used to get access to getLSTformat and getChoiceClass, so
	 * perhaps the TransitionChoice should delegate those instead in order to
	 * protect the SelectableSet?
	 * 
	 * @return The SelectableSet for this TransitionChoice.
	 */
	public SelectableSet<? extends T> getChoices()
	{
		return choices;
	}

	/**
	 * Sets the (optional) ChoiceActor for this TransitionChoice. The
	 * ChoiceActor will be called when the act method of TransitionChoice is
	 * called. If the ChoiceActor is not set, then the set method may not be
	 * used without triggering an exception.
	 * 
	 * @param actor
	 *            The ChoiceActor for this TransitionChoice.
	 * @throws ClassCastException
	 *             if the given ChoiceActor is not a PersistentChoiceActor
	 */
	@Override
	public void setChoiceActor(ChoiceActor<T> actor)
	{
		choiceActor = (PersistentChoiceActor<T>) actor;
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
	 * @param item
	 *            The choice which should be encoded into a String sufficient to
	 *            identify the choice.
	 * 
	 * @return A String sufficient to uniquely identify the choice.
	 */
	@Override
	public String encodeChoice(T item)
	{
		return choiceActor.encodeChoice(item);
	}

	/**
	 * Decodes a given String into a choice of the appropriate type. The String
	 * format to be passed into this method is defined solely by the return
	 * result of the encodeChoice method. There is no guarantee that the
	 * encoding is human readable, simply that the encoding is uniquely
	 * identifying such that this method is capable of decoding the String into
	 * the choice object.
	 * @param persistentFormat
	 *            The String which should be decoded to provide the choice of
	 *            the appropriate type.
	 * 
	 * @return A choice object of the appropriate type that was encoded in the
	 *         given String.
	 */
	@Override
	public T decodeChoice(LoadContext context, String persistentFormat)
	{
		return choiceActor.decodeChoice(context, persistentFormat);
	}

	@Override
	public PersistentChoiceActor<T> getChoiceActor()
	{
		return choiceActor;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ConcreteChoice)
		{
			ConcreteChoice<?> other = (ConcreteChoice<?>) obj;
			return choices.equals(other.choices);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return choices.hashCode() + 29;
	}

	public String getLSTformat()
	{
		return choices.getLSTformat();
	}

	public String getName()
	{
		return choices.getName();
	}

	public String getTitle()
	{
		return choices.getTitle();
	}
}
