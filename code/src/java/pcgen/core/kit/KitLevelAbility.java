/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * {@code KitLevelAbility}.
 */
public final class KitLevelAbility extends BaseKit
{
	private CDOMSingleRef<PCClass> theClassName;
	private int theLevel;
	private final List<String> choiceList = new ArrayList<>();
	private PersistentTransitionChoice<?> add;

	/**
	 * Set the class
	 * @param className
	 */
	public void setClass(CDOMSingleRef<PCClass> className)
	{
		theClassName = className;
	}

	public CDOMSingleRef<PCClass> getPCClass()
	{
		return theClassName;
	}

	/**
	 * Set the level
	 * @param level
	 */
	public void setLevel(int level)
	{
		theLevel = level;
	}

	public int getLevel()
	{
		return theLevel;
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		//TODO is this clean (add.toString?)
		buf.append(add);
		buf.append(": [");
		boolean firstTime = true;
		for (String choiceStr : choiceList)
		{
			if (!firstTime)
			{
				buf.append(", ");
			}
			buf.append(choiceStr);

			firstTime = false;
		}
		buf.append("]");
		return buf.toString();
	}

	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		return doApplication(aPC);
	}

	@Override
	public void apply(PlayerCharacter aPC)
	{
		doApplication(aPC);
	}

	private boolean doApplication(PlayerCharacter aPC)
	{
		PCClass theClass = theClassName.get();
		PCClass classKeyed = aPC.getClassKeyed(theClass.getKeyName());
		if (classKeyed == null)
		{
			//Error?
			Logging.log(Logging.ERROR, "Character should have the class: " + theClass.getKeyName() + ".");
		}
		//Look for ADD in class
		List<PersistentTransitionChoice<?>> adds = theClass.getListFor(ListKey.ADD);
		if (adds == null)
		{
			//Error?
			Logging.log(Logging.ERROR, "The class should have returned a list but returned null.");
		}
		for (PersistentTransitionChoice<?> ch : adds)
		{
			if (add.equals(ch))
			{
				process(aPC, classKeyed, ch);
				return true;
			}
		}
		return false;
	}

	private <T> void process(PlayerCharacter pc, PCClass cl, PersistentTransitionChoice<T> ch)
	{
		List<T> list = new ArrayList<>();
		for (String s : choiceList)
		{
			list.add(ch.decodeChoice(Globals.getContext(), s));
		}
		//use ch
		ch.act(list, cl, pc);
	}

	@Override
	public String getObjectName()
	{
		return "Class Feature";
	}

	public void addChoice(String string)
	{
		choiceList.add(string);
	}

	public void setAdd(PersistentTransitionChoice<?> name)
	{
		add = name;
	}

	public PersistentTransitionChoice<?> getAdd()
	{
		return add;
	}
}
