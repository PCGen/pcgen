/*
 * InfoSkillsSorter.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on Jan 13, 2003, 9:26 PM CST
 */
package pcgen.gui;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.gui.utils.PObjectNode;

/**
 * @author  B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 * @version $Revision$
 */
public interface InfoSkillsSorter
{
	/**
	 * Final Pass of the node
	 * @param node
	 * @return the node that has had the final pass
	 */ 
	PObjectNode finalPass(PObjectNode node);

	/**
	 * Get the next sorter
	 * @return the next sorter
	 */
	InfoSkillsSorter nextSorter();

	/**
	 * Returns true if hte node belongs 'here'
	 * @param node
	 * @param skill
	 * @return true if hte node belongs 'here'
	 */
	boolean nodeGoHere(PObjectNode node, Skill skill);

	/**
	 * Returns true if the node has a next node
	 * @return true if the node has a next node
	 */
	boolean nodeHaveNext();

	/**
	 * Returns what part the skill is for on the PC
	 * @param available
	 * @param skill
	 * @param pc
	 * @return what part the skill is for on the PC
	 */
	Object whatPart(boolean available, Skill skill, PlayerCharacter pc);
}
