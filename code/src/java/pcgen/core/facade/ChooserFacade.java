/*
 * ChooserFacade.java
 * Copyright James Dempsey, 2012
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
 * Created on 04/01/2012 5:13:13 PM
 *
 * $Id$
 */
package pcgen.core.facade;

import java.util.List;

import pcgen.core.facade.util.ListFacade;

/**
 *  <code>ChooserFacade</code> defines the interface for backing general choosers, 
 *  where a dialog is presented to a user asking them to select from a fixed list of 
 *  options.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public interface ChooserFacade
{

	/**
	 * <code>ChooserTreeViewType</code> defines the types of tree views that can 
	 * be displayed in a chooser dialog.
	 */
	public enum ChooserTreeViewType
	{
		/** A flat display of just the choice name. */
		NAME, 
		
		/** A hierarchical display of choice names within their types. */
		TYPE_NAME;
	}

	/**
	 *
	 * @return the currently available items
	 */
	ListFacade<InfoFacade> getAvailableList();

	/**
	 * 
	 * @return the currently selected items
	 */
	ListFacade<InfoFacade> getSelectedList();

	/**
	 * Adds an item to the selected list and
	 * updates the available list to show that it
	 * is no longer available.
	 * @param item the item to be added
	 */
	void addSelected(InfoFacade item);

	/**
	 * Removes an item from the selected list and
	 * updates the available list to show that it
	 * is now available.
	 * @param item the item to be removed
	 */
	void removeSelected(InfoFacade item);

	/**
	 * @return A reference to the number of selections the user can make.
	 */
	ReferenceFacade<Integer> getRemainingSelections();

	/**
	 * Applies the changes in selection to the underlying character.
	 */
	void commit();

	/**
	 * Undoes any changes made to the selected and available lists.
	 */
	void rollback();


	/**
	 * Get the name of the chooser. This will be displayed as 
	 * the title of the chooser dialog box.
	 * @return the name of this chooser
	 */
	String getName();
	
	/**
	 * @return The title for the available list in its tree mode.
	 */
	String getAvailableTableTypeNameTitle();
	
	/**
	 * @return The title for the available list in its flat mode.
	 */
	String getAvailableTableTitle();

	/**
	 * @return The starting tree view for the chooser.
	 */
	ChooserTreeViewType getDefaultView();
	
	/**
	 * @return The title for the selected list.
	 */
	String getSelectedTableTitle();

	/**
	 * @return  The label for the add button.
	 */
	String getAddButtonName();

	/**
	 * @return The label for the remove.
	 */
	String getRemoveButtonName();

	/**
	 * @return The label for the number of selections remaining.
	 */
	String getSelectionCountName();
	
	/**
	 * Get the names of parent branches under which the item should be 
	 * displayed. If an empty list is returned the item will be displayed 
	 * at the top level. Will only be called in tree mode.
	 * @param item The item being displayed.
	 * @return The names of branches under which the node should be displayed.
	 */
	List<String> getBranchNames(InfoFacade item);
}
