/*
 * PrereqObject.java Copyright 2006 Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Version: $Revision$ Last Editor: $Author: $ Last Edited:
 * $Date$
 * 
 */
package pcgen.cdom.base;

import java.util.Collection;
import java.util.List;

import pcgen.core.prereq.Prerequisite;

/**
 * This class implements support for prerequisites for an object.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public interface PrereqObject
{
	
	/**
	 * Add a <tt>Prerequesite</tt> to the prereq list with a level qualifier.
	 * 
	 * <p>If the Prerequisite kind is &quot;clear&quot; all the prerequisites
	 * will be cleared from the list.
	 * 
	 * @param preReq The <tt>Prerequisite</tt> to add.
	 */
	public void addPrerequisite(Prerequisite preReq);

	/**
	 * Adds an <tt>Array</tt> of <tt>Prerequisite</tt> objects.
	 * 
	 * @param prereqs An <tt>Array</tt> of <tt>Prerequisite</tt> objects.
	 */
	public void addAllPrerequisites(Prerequisite... prereqs);

	/**
	 * Adds a <tt>Collection</tt> of <tt>Prerequisite</tt> objects.
	 * 
	 * @param prereqs A <tt>Collection</tt> of <tt>Prerequisite</tt> objects.
	 */
	public void addAllPrerequisites(Collection<Prerequisite> prereqs);
	
	/**
	 * Get the list of <tt>Prerequesite</tt>s.
	 * 
	 * @return An unmodifiable <tt>List</tt> of <tt>Prerequesite</tt>s or
	 *         <tt>
	 * null</tt> if no prerequisites have been set.
	 */
	public List<Prerequisite> getPrerequisiteList();

	/**
	 * Clear the prerequisite list.
	 */
	public void clearPrerequisiteList();

	/**
	 * Tests to see if this object has any prerequisites associated with it.
	 * 
	 * @return <tt>true</tt> if it has prereqs
	 */
	public boolean hasPrerequisites();

	/**
	 * Gets the number of prerequisites currently associated.
	 * 
	 * @return the number of prerequesites
	 */
	public int getPrerequisiteCount();

	public Class<? extends PrereqObject> getReferenceClass();
	
}
