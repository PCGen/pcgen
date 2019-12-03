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

/**
 * A MasterListInterface is an object that stores data that is reasonably global
 * to a dataset, and that impacts all objects in that rules dataset. For
 * example, the initialized contents of ClassSpellLists (formed through the
 * CLASSES: token in Spell LST files) are stored in the master lists. Local
 * changes (e.g. those from SPELLLEVEL) are not stored in the
 * MasterListInterface, but in the object granting the modifications to the
 * given List.
 */
public interface MasterListInterface
{
	/**
	 * Returns a Set of all Active Lists defined in the MasterListInterface.
	 * Lists are returned regardless of their Class (ClassSpellList, etc.) and
	 * regardless of the underlying Class (e.g. Spell, Skill)
	 * 
	 * @return A Set of all Active Lists defined in the MasterListInterface.
	 */
    Set<CDOMReference<? extends CDOMList<?>>> getActiveLists();

	/**
	 * Returns a Collection of AssociatedPrereqObjects containing the
	 * associations for the lists underlying the given CDOMReference and the
	 * given Object on the list.
	 * 
	 * @param <T>
	 *            The type of object contained within the List for which the
	 *            associations are being returned
	 * @param key1
	 *            A CDOMReference containing one or more CDOMLists for which the
	 *            associations for the given Object should be returned
	 * @param key2
	 *            The CDOMObject for which the associations on the lists
	 *            contained within the given CDOMReference should be returned
	 * @return a Collection of AssociatedPrereqObjects containing the
	 *         associations for the lists underlying the given CDOMReference and
	 *         the given Object on the list.
	 */
    <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(
            CDOMReference<? extends CDOMList<T>> key1, T key2);

	/**
	 * Returns a Collection of AssociatedPrereqObjects containing the
	 * associations for the given list and the given Object on the list.
	 * 
	 * @param <T>
	 *            The type of object contained within the List for which the
	 *            associations are being returned
	 * @param key1
	 *            A CDOMList for which the associations for the given Object
	 *            should be returned
	 * @param key2
	 *            The CDOMObject for which the associations on the CDOMList
	 *            should be returned
	 * @return a Collection of AssociatedPrereqObjects containing the
	 *         associations for the given list and the given Object on the list.
	 */
    <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(CDOMList<T> key1, T key2);

	<T extends CDOMObject> Collection<T> getObjects(CDOMReference<CDOMList<T>> ref);
}
