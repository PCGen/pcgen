/*
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
 */
package pcgen.facade.core;

import java.util.List;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;

/**
 *  {@code ChooserFacade} defines the interface for backing general choosers,
 *  where a dialog is presented to a user asking them to select from a fixed list of 
 *  options.
 *
 * 
 */
public interface ChooserFacade
{

	/**
	 * {@code ChooserTreeViewType} defines the types of tree views that can
	 * be displayed in a chooser dialog.
	 */
	public enum ChooserTreeViewType
	{
		/** A flat display of just the choice name. */
		NAME,

		/** A hierarchical display of choice names within their types. */
		TYPE_NAME
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

	/**
	 * Does the user need to use up all remaining selections before they can 
	 * commit the chooser.
	 * @return true if the chooser needs to have 0 remaining selections before being committed.
	 */
	public boolean isRequireCompleteSelection();

	/**
	 * Would the caller prefer this choice be shown as a simple set of radio 
	 * buttons. Note: This preference may be ignored if the UIDelegate deems 
	 * the choice unsuitable for this presentation style.
	 * @return Should the choice be presented as radio buttons if possible?
	 */
	public boolean isPreferRadioSelection();

	/**
	 * Should the user be requested to enter values rather than select from a list.
	 * @return true if the user should type in values.
	 */
	public boolean isUserInput();

	/**
	 * @return Do the items in this chooser have extra info above a name.
	 */
	public boolean isInfoAvailable();

	/**
	 * Retrieve the factory which provides descriptions for items.
	 * @return The info factory.  
	 */
	public InfoFactory getInfoFactory();
}
