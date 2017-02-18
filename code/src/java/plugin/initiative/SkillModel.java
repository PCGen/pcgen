/*
 * Copyright 2003 (C) Ross M. Lodge
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
package plugin.initiative;

/**
 * <p>This class models a skill.  It basically represents a skill as a name and
 * total bonus.</p>
 *
 */
public class SkillModel extends CheckModel
{
	/**
	 * <p>Constructs a new skill model based on a string.  The string should
	 * have the following tokens, in the following order, separated by
	 * backslashes:</p>
	 * <ol>
	 * <li>|SKILL.%skill|</li>
	 * <li>1d20+/-|SKILL.%skill.TOTAL|</li>
	 * </ol>
	 *
	 * @param objectString String description of skill
	 */
	public SkillModel(String objectString)
	{
		super(objectString);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Skill " + super.toString();
	}

}
