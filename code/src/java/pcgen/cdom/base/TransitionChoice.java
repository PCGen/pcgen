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
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.PlayerCharacter;
import pcgen.util.StringPClassUtil;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * This is a transitional class from PCGen 5.15+ to the final CDOM core. It is
 * provided as convenience to hold a set of choices and the number of choices
 * allowed, prior to final implementation of the new choice system
 * 
 * @param <T>
 */
public class TransitionChoice<T>
{

	private final ChoiceSet<? extends T> choices;
	private final Formula choiceCount;
	private String title;
	private boolean required = true;
	private ChoiceActor<T> choiceActor;
	private boolean allowStack = false;
	private Integer stackLimit = null;

	public TransitionChoice(ChoiceSet<? extends T> cs, Formula count)
	{
		choices = cs;
		choiceCount = count;
	}

	public ChoiceSet<? extends T> getChoices()
	{
		return choices;
	}

	public Formula getCount()
	{
		return choiceCount;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TransitionChoice)
		{
			TransitionChoice<?> other = (TransitionChoice<?>) obj;
			return choiceCount.equals(other.choiceCount)
					&& choices.equals(other.choices);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return choiceCount.hashCode() * 29 + choices.hashCode();
	}

	public Collection<? extends T> driveChoice(PlayerCharacter pc)
	{
		ChooserInterface c = ChooserFactory.getChooserInstance();
		int intValue = choiceCount.resolve(pc, "").intValue();
		c.setPoolFlag(required);
		if (intValue == Integer.MAX_VALUE)
		{
			c.setPickAll(true);
		}
		else
		{
			c.setTotalChoicesAvail(intValue);
		}
		if (title == null)
		{
			title = "Choose a "
					+ StringPClassUtil.getStringFor(choices.getChoiceClass());
		}
		c.setTitle(title);
		Set<? extends T> set = choices.getSet(pc);
		Set<T> allowed = new HashSet<T>();
		List<Object> assocList = pc.getAssocList(this, AssociationListKey.ADD);
		for (T o : set)
		{
			if (choiceActor == null || choiceActor.allow(o, pc, allowStack))
			{
				if (assocList != null && stackLimit != null && stackLimit > 0)
				{
					int takenCount = 0;
					for (Object choice : assocList)
					{
						if (choice.equals(o))
						{
							takenCount++;
						}
					}
					if (stackLimit <= takenCount)
					{
						continue;
					}
				}
				allowed.add(o);
			}
		}

		if (c.pickAll() || intValue == set.size())
		{
			return allowed;
		}
		else
		{
			c.setAvailableList(new ArrayList<T>(allowed));
			c.setVisible(true);
			return c.getSelectedList();
		}
	}

	public void setTitle(String string)
	{
		title = string;
	}

	public void setRequired(boolean b)
	{
		required = b;
	}

	public void setChoiceActor(ChoiceActor<T> ca)
	{
		choiceActor = ca;
	}

	public void act(Collection<? extends T> driveChoice, CDOMObject owner,
			PlayerCharacter apc)
	{
		if (choiceActor == null)
		{
			throw new IllegalStateException(
					"Cannot act without a defined ChoiceActor");
		}
		for (T choice : driveChoice)
		{
			choiceActor.applyChoice(owner, choice, apc);
			apc.addAssoc(this, AssociationListKey.ADD, choice);
		}
	}

	public T castChoice(Object o)
	{
		return (T) o;
	}

	public void allowStack(boolean allow)
	{
		allowStack = allow;
	}

	public void setStackLimit(int limit)
	{
		stackLimit = limit;
	}

	public boolean allowsStacking()
	{
		return allowStack;
	}

	public int getStackLimit()
	{
		return stackLimit == null ? 0 : stackLimit;
	}

	public ChoiceActor<T> getChoiceActor()
	{
		return choiceActor;
	}
}
