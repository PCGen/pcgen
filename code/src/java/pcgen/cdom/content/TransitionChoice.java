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
package pcgen.cdom.content;

import pcgen.cdom.base.ChoiceSet;

public class TransitionChoice<T>
{

	private final ChoiceSet<? extends T> choices;
	private final int choiceCount;

	public TransitionChoice(ChoiceSet<? extends T> cs, int count)
	{
		choices = cs;
		choiceCount = count;
	}

	public ChoiceSet<? extends T> getChoices()
	{
		return choices;
	}

	public int getCount()
	{
		return choiceCount;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TransitionChoice)
		{
			TransitionChoice<?> other = (TransitionChoice<?>) obj;
			return choiceCount == other.choiceCount
					&& choices.equals(other.choices);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return choiceCount * 29 + choices.hashCode();
	}

}
