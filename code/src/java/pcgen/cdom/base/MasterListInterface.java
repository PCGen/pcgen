/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.Collection;
import java.util.Set;

public interface MasterListInterface
{
	public <T extends CDOMObject> Set<CDOMReference> getActiveLists();

	public <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(
			CDOMReference<? extends CDOMList<T>> key1, T key2);

	public <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(
			CDOMList<T> key1, T key2);
}
