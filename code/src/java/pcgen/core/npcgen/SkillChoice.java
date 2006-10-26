/*
 * SkillChoice.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core.npcgen;

import java.util.List;

import pcgen.core.Globals;
import pcgen.core.Skill;
import pcgen.util.WeightedList;

/**
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class SkillChoice
{
	private String theKey = null;
	private List<Skill> theSkillList = new WeightedList<Skill>();
	
	public SkillChoice(final String aKey)
	{
		theKey = aKey;
		if ( theKey.startsWith("TYPE") )
		{
			final List<Skill> subSkills = Globals.getSkillsByType(theKey.substring(5));
			theSkillList.addAll( subSkills );
		}
		else
		{
			theSkillList.add(Globals.getSkillKeyed(theKey));
		}
	}
	
	public Skill getSkill()
	{
		final Skill skill = theSkillList.get(Globals.getRandomInt(theSkillList.size()));
		theSkillList.add(NPCGenerator.getSubSkillWeightAdd(), skill);
		return skill;
	}
	
	public boolean hasSkill( final String aKey )
	{
		if ( theKey.equals(aKey) )
		{
			return true;
		}
		if ( theSkillList.size() == 1 )
		{
			return false;
		}
		final Skill skill = Globals.getSkillKeyed(aKey);
		if ( skill == null )
		{
			return false;
		}
		
		for ( final Skill s : theSkillList )
		{
			if ( s.getKeyName().equals(aKey) )
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString()
	{
		return theSkillList.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((theKey == null) ? 0 : theKey.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SkillChoice other = (SkillChoice) obj;
		if (theKey == null)
		{
			if (other.theKey != null)
				return false;
		}
		else if (!theKey.equals(other.theKey))
			return false;
		return true;
	}
}
