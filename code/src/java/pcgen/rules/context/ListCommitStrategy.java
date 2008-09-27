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
package pcgen.rules.context;

import java.net.URI;
import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;

public interface ListCommitStrategy
{

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(String tokenName,
			CDOMObject owner, CDOMReference<? extends CDOMList<T>> list,
			T allowed);

	public <T extends CDOMObject> void removeFromMasterList(String tokenName,
			CDOMObject owner, CDOMReference<? extends CDOMList<T>> list,
			T allowed);

	public Changes<CDOMReference> getMasterListChanges(
		String tokenName, CDOMObject owner, Class<? extends CDOMList<?>> cl);

	public boolean hasMasterLists();

	public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);

	public <T extends PrereqObject> AssociatedPrereqObject addToList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> allowed);

	public Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> getChangedLists(
		CDOMObject owner, Class<? extends CDOMList<?>> cl);

	public void removeAllFromList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<?>> swl);

	public <T extends PrereqObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<? super T>> swl,
		CDOMReference<T> ref);

	public <T extends PrereqObject> AssociatedChanges<CDOMReference<T>> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);

	public void setSourceURI(URI sourceURI);

	public void setExtractURI(URI sourceURI);

	public void clearAllMasterLists(String tokenName, CDOMObject owner);
}
