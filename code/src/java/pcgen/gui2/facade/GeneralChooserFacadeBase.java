/*
 * GeneralChooserFacadeBase.java
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
 * Created on 06/01/2012 9:23:01 AM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.Type;
import pcgen.core.PObject;
import pcgen.core.facade.ChooserFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>GeneraChooserFacadeBase</code> is a base from which a 
 * ChooserFacade may be simply implemented. The implementing class need only call 
 * the constructor and implement the commit to process the selections.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public abstract class GeneralChooserFacadeBase implements ChooserFacade
{

	private final String name;
	
	private final List<InfoFacade> origAvailable;
	private final List<InfoFacade> origSelected;
	private final int maxNewSelections;
	
	private DefaultListFacade<InfoFacade> availableList;
	private DefaultListFacade<InfoFacade> selectedList;
	private DefaultReferenceFacade<Integer> numSelectionsRemain;

	private final String availableTableTitle;
	private final String selectedTableTitle;
	private final String selectionCountName;
	private final String addButtonName;
	private final String removeButtonName;

	
	/**
	 * Create a new instance of GeneraChooserFacadeBase with default localised 
	 * text for the chooser. Note none of the supplied lists will be directly 
	 * modified.   
	 * 
	 * @param name The title of the chooser.
	 * @param available The list of items to select from.
	 * @param selected The list of items already selected. The user may choose to deselect items from this list.
	 * @param maxNewSelections The number of selections the user may make in addition to those in the selected list.
	 */
	GeneralChooserFacadeBase(String name, List<InfoFacade> available, List<InfoFacade> selected, int maxNewSelections)
	{
		this(name, available, selected, maxNewSelections, 
			LanguageBundle.getString("in_available"),
			LanguageBundle.getString("in_selected"), 
			LanguageBundle.getString("in_selRemain"), 
			LanguageBundle.getString("in_add"),
			LanguageBundle.getString("in_remove"));
	}

	/**
	 * Create a new instance of GeneraChooserFacadeBase with supplied text for 
	 * the chooser. Note none of the supplied lists will be directly modified.   

	 * @param name The title of the chooser.
	 * @param available The list of items to select from.
	 * @param selected The list of items already selected. The user may choose to deselect items from this list.
	 * @param maxNewSelections The number of selections the user may make in addition to those in the selected list.
	 * @param availableTableTitle The title for the available list.
	 * @param selectedTableTitle The title for the selected list.
	 * @param selectionCountName The label for the number of selections remaining.
	 * @param addButtonName The label for the add button.
	 * @param removeButtonName The label for the remove button.
	 */
	GeneralChooserFacadeBase(String name, List<InfoFacade> available,
		List<InfoFacade> selected, int maxNewSelections,
		String availableTableTitle, String selectedTableTitle,
		String selectionCountName, String addButtonName, String removeButtonName)
	{
		this.name = name;
		this.origAvailable = available;
		this.origSelected = selected;
		this.maxNewSelections = maxNewSelections;
		this.availableTableTitle = availableTableTitle;
		this.selectedTableTitle = selectedTableTitle;
		this.selectionCountName = selectionCountName;
		this.addButtonName = addButtonName;
		this.removeButtonName = removeButtonName;
				
		// Build working content
		availableList = new DefaultListFacade<InfoFacade>(origAvailable);
		selectedList = new DefaultListFacade<InfoFacade>(origSelected);
		numSelectionsRemain = new DefaultReferenceFacade<Integer>(maxNewSelections);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ListFacade<InfoFacade> getAvailableList()
	{
		return availableList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ListFacade<InfoFacade> getSelectedList()
	{
		return selectedList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addSelected(InfoFacade item)
	{
		if (numSelectionsRemain.getReference() <= 0)
		{
			return;
		}
		selectedList.addElement(item);
		availableList.removeElement(item);
		numSelectionsRemain.setReference(numSelectionsRemain.getReference()-1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeSelected(InfoFacade item)
	{
		selectedList.removeElement(item);
		availableList.addElement(item);
		numSelectionsRemain.setReference(numSelectionsRemain.getReference()+1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferenceFacade<Integer> getRemainingSelections()
	{
		return numSelectionsRemain;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void commit();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void rollback()
	{
		availableList.setContents(origAvailable);
		selectedList.setContents(origSelected);
		numSelectionsRemain.setReference(maxNewSelections);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName()
	{
		return name;
	}

	public String getAvailableTableTitle()
	{
		return availableTableTitle;
	}

	public String getSelectedTableTitle()
	{
		return selectedTableTitle;
	}

	public String getAddButtonName()
	{
		return addButtonName;
	}

	public String getRemoveButtonName()
	{
		return removeButtonName;
	}

	public String getSelectionCountName()
	{
		return selectionCountName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getBranchNames(InfoFacade item)
	{
		List<String> branches = new ArrayList<String>();
		
		if (item instanceof PObject)
		{
			PObject pObject = (PObject) item;
			for (Type type : pObject.getTrueTypeList(true))
			{
				branches.add(type.toString());
			}
		}
		return branches;
	}

}
