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
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code GeneraChooserFacadeBase} is a base from which a
 * ChooserFacade may be simply implemented. The implementing class need only call 
 * the constructor and implement the commit to process the selections.
 * 
 * @param <T> The type of objects being chosen from. 
 *
 * 
 */

public class CDOMChooserFacadeImpl<T> implements ChooserFacade
{

	private final String name;

	private final List<T> origAvailable;
	private final List<? extends T> origSelected;
	private final int maxNewSelections;
	private final List<T> finalSelected;

	private DefaultListFacade<InfoFacade> availableList;
	private DefaultListFacade<InfoFacade> selectedList;
	private DefaultReferenceFacade<Integer> numSelectionsRemain;

	private final String availableTableTypeNameTitle;
	private final String selectedTableTitle;
	private final String selectionCountName;
	private final String addButtonName;
	private final String removeButtonName;

	private final String availableTableTitle;
	private ChooserTreeViewType defaultView = ChooserTreeViewType.TYPE_NAME;

	private boolean dupsAllowed = false;

	private boolean requireCompleteSelection;

	private boolean preferRadioSelection = false;

	private boolean userInput = false;

	private final String stringDelimiter;

	private boolean infoAvailable = false;

	private InfoFactory infoFactory = null;

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
	public CDOMChooserFacadeImpl(String name, List<T> available, List<? extends T> selected, int maxNewSelections)
	{
		this(name, available, selected, maxNewSelections, LanguageBundle.getString("in_available"), //$NON-NLS-1$
			LanguageBundle.getString("in_typeName"), //$NON-NLS-1$
			LanguageBundle.getString("in_selected"), //$NON-NLS-1$
			LanguageBundle.getString("in_selRemain"), //$NON-NLS-1$
			LanguageBundle.getString("in_add"), //$NON-NLS-1$
			LanguageBundle.getString("in_remove"), null); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of GeneraChooserFacadeBase with default localised 
	 * text for the chooser. Note none of the supplied lists will be directly 
	 * modified.   
	 * 
	 * @param name The title of the chooser.
	 * @param available The list of items to select from.
	 * @param selected The list of items already selected. The user may choose to deselect items from this list.
	 * @param maxNewSelections The number of selections the user may make in addition to those in the selected list.
	 * @param stringDelimiter A string used to split the user viewable part of a string option from the full string. 
	 */
	public CDOMChooserFacadeImpl(String name, List<T> available, List<? extends T> selected, int maxNewSelections,
		String stringDelimiter)
	{
		this(name, available, selected, maxNewSelections, LanguageBundle.getString("in_available"), //$NON-NLS-1$
			LanguageBundle.getString("in_typeName"), //$NON-NLS-1$
			LanguageBundle.getString("in_selected"), //$NON-NLS-1$
			LanguageBundle.getString("in_selRemain"), //$NON-NLS-1$
			LanguageBundle.getString("in_add"), //$NON-NLS-1$
			LanguageBundle.getString("in_remove"), stringDelimiter); //$NON-NLS-1$
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
	 * @param stringDelimiter A string used to split the user viewable part of a string option from the full string. 
	 */
	public CDOMChooserFacadeImpl(String name, List<T> available, List<? extends T> selected, int maxNewSelections,
		String availableTableTitle, String availableTableTypeNameTitle, String selectedTableTitle,
		String selectionCountName, String addButtonName, String removeButtonName, String stringDelimiter)
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
		this.stringDelimiter = stringDelimiter;

		// Build working content
		availableList = new DefaultListFacade<>(createInfoFacadeList(origAvailable, stringDelimiter));
		selectedList = new DefaultListFacade<>(createInfoFacadeList(origSelected, stringDelimiter));
		numSelectionsRemain = new DefaultReferenceFacade<>(maxNewSelections);
		finalSelected = new ArrayList<>(origSelected);
	}

	private List<InfoFacade> createInfoFacadeList(List<? extends T> origAvailable2, String stringDelimiter)
	{
		List<InfoFacade> infoFacadeList = new ArrayList<>(origAvailable2.size());
		for (T object : origAvailable2)
		{
			if (object instanceof InfoFacade)
			{
				infoFacadeList.add((InfoFacade) object);
				infoAvailable = true;
			}
			else if (object instanceof CDOMObject)
			{
				CDOMInfoWrapper wrapper = new CDOMInfoWrapper((CDOMObject) object);
				infoFacadeList.add(wrapper);
			}
			else if (!StringUtils.isEmpty(stringDelimiter) && (object instanceof String))
			{
				DelimitedStringInfoWrapper wrapper = new DelimitedStringInfoWrapper((String) object, stringDelimiter);
				infoFacadeList.add(wrapper);
			}
			else
			{
				InfoWrapper wrapper = new InfoWrapper(object);
				infoFacadeList.add(wrapper);
			}
		}
		return infoFacadeList;
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
		if (!dupsAllowed)
		{
			availableList.removeElement(item);
		}
		numSelectionsRemain.set(numSelectionsRemain.get() - 1);
	}

	@Override
	public final void removeSelected(InfoFacade item)
	{
		selectedList.removeElement(item);
		if (!dupsAllowed)
		{
			availableList.addElement(item);
		}
		numSelectionsRemain.set(numSelectionsRemain.get() + 1);
	}

	@Override
	public ReferenceFacade<Integer> getRemainingSelections()
	{
		return numSelectionsRemain;
	}

	@Override
	public void commit()
	{
		finalSelected.clear();
		for (InfoFacade object : selectedList)
		{
			T selected;
			if (object instanceof CDOMChooserFacadeImpl.CDOMInfoWrapper)
			{
				selected = (T) ((CDOMChooserFacadeImpl.CDOMInfoWrapper) object).getCdomObj();
			}
			else if (object instanceof InfoWrapper)
			{
				selected = (T) ((InfoWrapper) object).getObj();
			}
			else
			{
				selected = (T) object;
			}
			finalSelected.add(selected);
		}
	}

	@Override
	public final void rollback()
	{
		availableList.setContents(createInfoFacadeList(origAvailable, stringDelimiter));
		selectedList.setContents(createInfoFacadeList(origSelected, stringDelimiter));
		numSelectionsRemain.set(maxNewSelections);
		finalSelected.clear();
		finalSelected.addAll(origSelected);
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
			for (Type type : pObject.getTrueTypeList(true))
			{
				branches.add(type.toString());
			}
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

	/**
	 * @return the finalSelected
	 */
	public List<T> getFinalSelected()
	{
		return finalSelected;
	}

	/**
	 * @param dupsAllowed Should the chooser allow an entry to be selected multiple times.
	 */
	public void setAllowsDups(boolean dupsAllowed)
	{
		this.dupsAllowed = dupsAllowed;
	}

	/**
	 * Identify if the user must use up all remaining selections before closing the chooser.
	 * @param requireCompleteSelection the requireCompleteSelection to set
	 */
	public void setRequireCompleteSelection(boolean requireCompleteSelection)
	{
		this.requireCompleteSelection = requireCompleteSelection;
	}

	@Override
	public boolean isRequireCompleteSelection()
	{
		return requireCompleteSelection;
	}

	/**
	 * @param preferRadioSelection Should this choice be displayed using radio buttons? 
	 */
	public void setPreferRadioSelection(boolean preferRadioSelection)
	{
		this.preferRadioSelection = preferRadioSelection;
	}

	@Override
	public boolean isPreferRadioSelection()
	{
		return preferRadioSelection;
	}

	@Override
	public boolean isUserInput()
	{
		return userInput;
	}

	public void setUserInput(boolean userInput)
	{
		this.userInput = userInput;
	}

	@Override
	public boolean isInfoAvailable()
	{
		return infoAvailable;
	}

	/**
	 * @return the infoFactory
	 */
	@Override
	public InfoFactory getInfoFactory()
	{
		return infoFactory;
	}

	/**
	 * @param infoFactory the infoFactory to set
	 */
	public void setInfoFactory(InfoFactory infoFactory)
	{
		this.infoFactory = infoFactory;
	}

	private static class CDOMInfoWrapper implements InfoFacade
	{
		private final CDOMObject cdomObj;

		public CDOMInfoWrapper(CDOMObject cdomObj)
		{
			this.cdomObj = cdomObj;

		}

		@Override
		public String toString()
		{
			return String.valueOf(cdomObj);
		}

		@Override
		public String getSource()
		{
			return SourceFormat.getFormattedString(cdomObj, Globals.getSourceDisplay(), true);
		}

		@Override
		public String getSourceForNodeDisplay()
		{
			return SourceFormat.getFormattedString(cdomObj, SourceFormat.LONG, false);
		}

		@Override
		public String getKeyName()
		{
			return cdomObj.getKeyName();
		}

		@Override
		public boolean isNamePI()
		{
			return cdomObj.getSafe(ObjectKey.NAME_PI);
		}

		/**
		 * @return the cdomObj
		 */
		public CDOMObject getCdomObj()
		{
			return cdomObj;
		}

		@Override
		public String getType()
		{
			final List<Type> types = cdomObj.getSafeListFor(ListKey.TYPE);
			return StringUtil.join(types, ".");
		}
	}

	private static class DelimitedStringInfoWrapper implements InfoFacade
	{
		private final String string;
		private final String delimiter;

		public DelimitedStringInfoWrapper(String string, String delimiter)
		{
			this.string = string;
			this.delimiter = StringUtils.trimToNull(delimiter);
		}

		@Override
		public String toString()
		{
			if (delimiter != null)
			{
				final int idx = string.indexOf(delimiter);

				if (idx > -1)
				{
					return string.substring(0, idx);
				}
			}
			return string;
		}

		@Override
		public String getSource()
		{
			return "";
		}

		@Override
		public String getSourceForNodeDisplay()
		{
			return "";
		}

		@Override
		public String getKeyName()
		{
			return string;
		}

		@Override
		public boolean isNamePI()
		{
			return false;
		}

		@Override
		public String getType()
		{
			return "";
		}
	}
}
