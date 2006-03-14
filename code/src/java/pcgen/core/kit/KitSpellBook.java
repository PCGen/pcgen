/*
 * KitSpellBook.java
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
 *
 * Created on September 26, 2005
 *
 * $Id$
 */
package pcgen.core.kit;

import java.util.*;

public class KitSpellBook// extends BaseKit
{
	private String className;
	private String theName;
	private HashMap theSpells = new HashMap();

	public KitSpellBook(final String aClassName, final String aName)
	{
		className = aClassName;
		theName = aName;
	}

	public final String getName()
	{
		return theName;
	}

	public Collection getSpells()
	{
		return theSpells.values();
	}

	public void addSpell(final String aSpell, final List metamagicList,
						 final String countStr)
	{
		int numCopies = 1;
		try
		{
			numCopies = Integer.parseInt(countStr);
		}
		catch (NumberFormatException e)
		{
			// Assume one copy.
		}
		List entries = (List)theSpells.get(aSpell);
		if (entries == null)
		{
			// This spellbook doesn't contain this spell.
			entries = new ArrayList();
			KitSpellBookEntry sbe = new KitSpellBookEntry(className, theName, aSpell, metamagicList);
			sbe.addCopies(numCopies-1);
			entries.add(sbe);
			theSpells.put(aSpell, entries);
		}
		else
		{
			// We have a copy of this spell already.
			// Check to see if the modifiers are the same
			boolean found = false;
			KitSpellBookEntry sbe = null;
			for (Iterator i = entries.iterator(); i.hasNext(); )
			{
				sbe = (KitSpellBookEntry)i.next();
				List modifiers = sbe.getModifiers();
				if (modifiers == null)
				{
					if (metamagicList != null && metamagicList.size() > 0)
					{
						// This spell is modified and we are adding one that isn't
						continue;
					}
					found = true;
					break;
				}
				else if (modifiers.size() != metamagicList.size())
				{
					continue;
				}
				int count = metamagicList.size() - 1;
				for (Iterator j = modifiers.iterator(); j.hasNext(); )
				{
					if (!metamagicList.contains(j.next()))
					{
						continue;
					}
					count--;
				}
				if (count == 0)
				{
					found = true;
					break;
				}
			}
			if (found == true)
			{
				sbe.addCopies(numCopies);
			}
			else
			{
				entries.add(new KitSpellBookEntry(className, theName, aSpell, metamagicList));
			}
		}
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(theName + ": ");
		boolean first = true;
		for (Iterator i = theSpells.values().iterator(); i.hasNext(); )
		{
			if (first == false)
			{
				buf.append(",");
			}

			List entries = (List)i.next();
			for (Iterator j = entries.iterator(); j.hasNext(); )
			{
				KitSpellBookEntry sbe = (KitSpellBookEntry) j.next();
				buf.append(sbe.getName());
				if (sbe.getModifiers() != null)
				{
					for (Iterator k = sbe.getModifiers().iterator(); k.hasNext(); )
					{
						buf.append(" [").append(k.next()).append("]");
					}
				}
				if (sbe.getCopies() > 1)
				{
					buf.append(" (").append(sbe.getCopies()).append(")");
				}
				first = false;
			}
		}
		return buf.toString();
	}
}

