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

import java.util.Collection;
import java.util.List;

import pcgen.core.prereq.Prerequisite;

/**
 * A PrereqObject is an object that contains a list of Prerequisites. This list
 * of Prerequisites is designed to serve as a list of conditions that must be
 * met before the PrereqObject can be "used"
 */
public interface PrereqObject
{

	/**
	 * Add a Prerequisite to the PrereqObject.
	 * 
	 * If the Prerequisite kind is CLEAR all the prerequisites will be cleared
	 * from the list.
	 * 
	 * @param prereq
	 *            The Prerequisite to add to the PrereqObject.
	 */
	public void addPrerequisite(Prerequisite prereq);

	/**
	 * Adds a Collection of Prerequisite objects to the PrereqObject.
	 * 
	 * @param prereqs
	 *            A Collection of Prerequisite objects to added to the
	 *            PrereqObject.
	 */
	public void addAllPrerequisites(Collection<Prerequisite> prereqs);

	/**
	 * Returns true if this PrereqObject contains any Prerequisites; false
	 * otherwise.
	 * 
	 * @return true if this PrereqObject contains any Prerequisites; false
	 *         otherwise.
	 */
	public boolean hasPrerequisites();

	/**
	 * Returns a List of the Prerequisite objects contained in the PrereqObject.
	 * If the PrereqObject contains no Prerequisites, the return value may be
	 * null or an empty list, it is implementation-specific.
	 * 
	 * @return A List of Prerequisite objects contained in the PrereqObject.
	 */
	public List<Prerequisite> getPrerequisiteList();

	/**
	 * Remove All Prerequisites contained in the PrereqObject.
	 */
	public void clearPrerequisiteList();

	/**
	 * Returns the number of Prerequisites contained in the PrereqObject.
	 * 
	 * @return the number of Prerequisites contained in the PrereqObject.
	 */
	public int getPrerequisiteCount();
}
