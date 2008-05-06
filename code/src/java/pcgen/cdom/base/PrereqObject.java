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
	
	public void addPrerequisite(Prerequisite preReq);

	public void addAllPrerequisites(Prerequisite... prereqs);

	public void addAllPrerequisites(Collection<Prerequisite> prereqs);
	
	public List<Prerequisite> getPrerequisiteList();

	public void clearPrerequisiteList();

	public boolean hasPrerequisites();

	public int getPrerequisiteCount();

	public void setPrerequisiteListFrom(PrereqObject prereqObject);

	public boolean hasPrerequisiteOfType(String matchType);
	
	public Class<? extends PrereqObject> getReferenceClass();

	@Deprecated
	public void addPreReq(Prerequisite preReq);
	
}
