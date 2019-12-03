/*
 * PrereqObject.java Copyright 2006 Aaron Divinsky <boomer70@yahoo.com>
 *   Copyright 2008 Tom Parker <thpr@users.sourceforge.net>
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
 * 
 * 
 */
package pcgen.cdom.base;

import pcgen.core.PlayerCharacter;

/**
 * A QualifyingObject is an object that contains a list of Prerequisites. This
 * list of Prerequisites is designed to serve as a list of conditions that must
 * be met before the QualifyingObject can be "used"
 */
@FunctionalInterface
public interface QualifyingObject
{

	/**
	 * Tests if the specified PlayerCharacter passes all the prerequisites.
	 * 
	 * @param playerCharacter
	 *            The PlayerCharacter to test.
	 * @param owner
	 *            The Loadable that owns the QualifyingObject (used for PRExxx
	 *            resolution)
	 * @return true if the given PlayerCharacter passes all the prerequisites.
	 */
    boolean qualifies(PlayerCharacter playerCharacter, Object owner);

}
