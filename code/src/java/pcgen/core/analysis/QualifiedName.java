/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Ability.java and Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.analysis;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

public class QualifiedName
{

	/**
	 * This method generates a name for this Ability which includes any choices
	 * made and a count of how many times it has been applied.
	 * 
	 * @param pc
	 *            TODO
	 * 
	 * @return The name of the full Ability, plus any sub-choices made for this
	 *         character. Starts with the name of the ability, and then (for
	 *         types other than weapon proficiencies), either appends a count of
	 *         the times the ability is applied e.g. " (3x)", or a list of the
	 *         sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public static String qualifiedName(PlayerCharacter pc, Ability a)
	{
		String outputName = a.getOutputName();
		if ("[BASE]".equalsIgnoreCase(outputName))
		{
			return a.getDisplayName();
		}
		// start with the name of the ability
		// don't do for Weapon Profs
		final StringBuffer aStrBuf = new StringBuffer(outputName);

		if (pc.hasAssociations(a)
				&& !a.getKeyName().startsWith("Armor Proficiency"))
		{
			if (!a.hasChooseToken()
					|| (a.getSafe(ObjectKey.MULTIPLE_ALLOWED) && a
							.getSafe(ObjectKey.STACKS)))
			{
				if (pc.getDetailedAssociationCount(a) > 1)
				{
					// number of items only (ie stacking), e.g. " (1x)"
					aStrBuf.append(" (");
					aStrBuf.append((int) (pc.getDetailedAssociationCount(a) * a
							.getSafe(ObjectKey.SELECTION_COST).doubleValue()));
					aStrBuf.append("x)");
				}
			}
			else
			{
				// has a sub-detail
				aStrBuf.append(" (");
				aStrBuf.append(StringUtil.joinToStringBuffer(pc
						.getExpandedAssociations(a), ", "));
				aStrBuf.append(')');
			}
		}

		return aStrBuf.toString();
	}

	public static String qualifiedName(PlayerCharacter pc, Skill s)
	{
		if (!pc.hasAssociations(s))
		{
			return s.getOutputName();
		}

		final StringBuilder buffer = new StringBuilder();
		buffer.append(s.getOutputName()).append("(");
		buffer.append(StringUtil.joinToStringBuffer(pc.getAssociationList(s),
				", "));
		buffer.append(")");

		return buffer.toString();
	}

}
