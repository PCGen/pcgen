/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core;

import java.util.List;

import pcgen.base.util.FixedStringList;

public interface AssociationStore
{
	public void addAssociation(PObject obj, String o);

	public void removeAssociation(PObject obj, String o);

	public void addAssociation(PObject obj, FixedStringList o);

	public void removeAssociation(PObject obj, FixedStringList o);

	public List<String> removeAllAssociations(PObject obj);

	public boolean hasAssociations(PObject obj);

	public List<String> getAssociationList(PObject obj);

	public boolean containsAssociated(PObject obj, String o);

	public boolean containsAssociated(PObject obj, FixedStringList o);

	public int getSelectCorrectedAssociationCount(PObject obj);

	public int getDetailedAssociationCount(PObject obj);

	public List<FixedStringList> getDetailedAssociations(PObject obj);

	public List<String> getExpandedAssociations(PObject obj);
	
	public String getFirstAssociation(PObject obj);
}
