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
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pcgen.cdom.enumeration.Type;
import pcgen.core.PObject;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code GeneraChooserFacadeBase} is a base from which a
 * ChooserFacade may be simply implemented. The implementing class need only call 
 * the constructor and implement the commit to process the selections.
 *
 * 
 */

public abstract class GeneralChooserFacadeBase implements ChooserFacade
{

	private final String name;

	private final List<InfoFacade> origAvailable;
	private final List<InfoFacade> origSelected;
	private final int maxNewSelections;

	private final DefaultListFacade<InfoFacade> availableList;
	private final DefaultListFacade<InfoFacade> selectedList;
	private final DefaultReferenceFacade<Integer> numSelectionsRemain;

	private final String availableTableTypeNameTitle;
	private final String selectedTableTitle;
	private final String selectionCountName;
	private final String addButtonName;
	private final String removeButtonName;

	private final String availableTableTitle;
	private ChooserTreeViewType defaultView = ChooserTreeViewType.TYPE_NAME;

	private boolean requireCompleteSelection;
	private boolean preferRadioSelection;

	private final InfoFactory infoFactory;

	/**
	 * Create a new instance of GeneraChooserFacadeBase with default localised 
	 * text for the chooser. Note none of the supplied lists will be directly 
	 * modified.   
	 * 
	 * @param name The title of the chooser.
	 * @param available The list of items to select from.
	 * @param selected The list of items already selected. The user may choose to deselect items from this list.
	 * @param maxNewSelections The number of selections the user may make in addition to those in the selected list.
	 * @param infoFactory The factory which provides descriptions for items. 
	 */
	GeneralChooserFacadeBase(String name, List<InfoFacade> available, List<InfoFacade> selected, int maxNewSelections,
		InfoFactory infoFactory)
	{
		this(name, available, selected, maxNewSelections, LanguageBundle.getString("in_available"), //$NON-NLS-1$
			LanguageBundle.getString("in_typeName"), //$NON-NLS-1$
			LanguageBundle.getString("in_selected"), //$NON-NLS-1$
			LanguageBundle.getString("in_selRemain"), //$NON-NLS-1$
			LanguageBundle.getString("in_add"), //$NON-NLS-1$
			LanguageBundle.getString("in_remove"), //$NON-NLS-1$ 
			infoFactory);
	}

	/**
	 * Create a new instance of GeneraChooserFacadeBase with supplied text for 
	 * the chooser. Note none of the supplied lists will be directly modified.   
	
	 * @param name The title of the chooser.
	 * @param available The list of items to select from.
	 * @param selected The list of items already selected. The user may choose to deselect items from this list.
	 * @param maxNewSelections The number of selections the user may make in addition to those in the selected list.
	 * @param availableTableTitle The title for the available list in flat mode.
	 * @param availableTableTypeNameTitle The title for the available list in tree mode.
	 * @param selectedTableTitle The title for the selected list.
	 * @param selectionCountName The label for the number of selections remaining.
	 * @param addButtonName The label for the add button.
	 * @param removeButtonName The label for the remove button.
	 * @param infoFactory The factory which provides descriptions for items. 
	 */
	GeneralChooserFacadeBase(String name, List<InfoFacade> available, List<InfoFacade> selected, int maxNewSelections,
		String availableTableTitle, String availableTableTypeNameTitle, String selectedTableTitle,
		String selectionCountName, String addButtonName, String removeButtonName, InfoFactory infoFactory)
	{
		this.name = name;
		this.origAvailable = available;
		this.origSelected = selected;
		this.maxNewSelections = maxNewSelections;
		this.availableTableTitle = availableTableTitle;
		this.availableTableTypeNameTitle = availableTableTypeNameTitle;
		this.selectedTableTitle = selectedTableTitle;
		this.selectionCountName = selectionCountName;
		this.addButtonName = addButtonName;
		this.removeButtonName = removeButtonName;
		this.infoFactory = infoFactory;

		// Build working content
		availableList = new DefaultListFacade<>(origAvailable);
		selectedList = new DefaultListFacade<>(origSelected);
		numSelectionsRemain = new DefaultReferenceFacade<>(maxNewSelections);

	}

	@Override
	public final ListFacade<InfoFacade> getAvailableList()
	{
		return availableList;
	}

	@Override
	public final ListFacade<InfoFacade> getSelectedList()
	{
		return selectedList;
	}

	@Override
	public final void addSelected(InfoFacade item)
	{
		if (numSelectionsRemain.get() <= 0)
		{
			return;
		}
		selectedList.addElement(item);
		availableList.removeElement(item);
		numSelectionsRemain.set(numSelectionsRemain.get() - 1);
	}

	@Override
	public final void removeSelected(InfoFacade item)
	{
		selectedList.removeElement(item);
		availableList.addElement(item);
		numSelectionsRemain.set(numSelectionsRemain.get() + 1);
	}

	@Override
	public ReferenceFacade<Integer> getRemainingSelections()
	{
		return numSelectionsRemain;
	}

	@Override
	public abstract void commit();

	@Override
	public final void rollback()
	{
		availableList.setContents(origAvailable);
		selectedList.setContents(origSelected);
		numSelectionsRemain.set(maxNewSelections);
	}

	@Override
	public final String getName()
	{
		return name;
	}

	@Override
	public String getAvailableTableTypeNameTitle()
	{
		return availableTableTypeNameTitle;
	}

	@Override
	public String getAvailableTableTitle()
	{
		return availableTableTitle;
	}

	@Override
	public String getSelectedTableTitle()
	{
		return selectedTableTitle;
	}

	@Override
	public String getAddButtonName()
	{
		return addButtonName;
	}

	@Override
	public String getRemoveButtonName()
	{
		return removeButtonName;
	}

	@Override
	public String getSelectionCountName()
	{
		return selectionCountName;
	}

	@Override
	public List<String> getBranchNames(InfoFacade item)
	{
		List<String> branches = new ArrayList<>();

		if (item instanceof PObject pObject)
		{
			branches = pObject.getTrueTypeList(true)
			                  .stream()
			                  .map(Type::toString)
			                  .collect(Collectors.toUnmodifiableList());
		}
		return branches;
	}

	@Override
	public ChooserTreeViewType getDefaultView()
	{
		return defaultView;
	}

	/**
	 * @param defaultView the flatDefault to set
	 */
	public void setDefaultView(ChooserTreeViewType defaultView)
	{
		this.defaultView = defaultView;
	}

	@Override
	public boolean isRequireCompleteSelection()
	{
		return requireCompleteSelection;
	}

	@Override
	public boolean isPreferRadioSelection()
	{
		return preferRadioSelection;
	}

	@Override
	public boolean isUserInput()
	{
		return false;
	}

	@Override
	public boolean isInfoAvailable()
	{
		return true;
	}

	@Override
	public InfoFactory getInfoFactory()
	{
		return infoFactory;
	}

}
