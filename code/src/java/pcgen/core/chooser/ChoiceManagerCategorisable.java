/**
 * ChoiceManagerCategorisable.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.List;

import pcgen.core.CategorisableStore;

public interface ChoiceManagerCategorisable {

	/**
	 * @param inNumberOfChoices
	 * @param inRequestedSelections
	 * @param inMaxNewSelections
	 */
	public abstract void initialise(int inNumberOfChoices,
			int inRequestedSelections, int inMaxNewSelections);

	/**
	 * Choose some objects out of a CategorisableStore.  The previousSelections
	 * List should only contain objects that are in the CategorisableStore   
	 *
	 * @param store
	 * @param previousSelections
	 * @return list
	 */
	public abstract List doChooser(final CategorisableStore store,
			final List previousSelections);

}