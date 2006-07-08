/*
 * NameGui.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the syllables for a given Name construction rule.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision$
 */
final class NameRule
{
	private List<String> rule = new ArrayList<String>();
	private int chance = 0;

	NameRule(final int argChance)
	{
		this.chance = argChance;
	}

	public int getChance()
	{
		return chance;
	}

	public String[] getRuleSyllables()
	{
		return rule.toArray(new String[rule.size()]);
	}

	public void addSyllable(final String syllable)
	{
		rule.add(syllable);
	}
}
