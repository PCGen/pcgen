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
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
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
 * @param <T> The type of objects being chosen from. 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class CDOMChooserFacadeImpl<T> implements ChooserFacade
{

	private final String name;
	
	private final List<T> origAvailable;
	private final List<T> origSelected;
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
	private ChooserTreeViewType defaultView =
			ChooserTreeViewType.TYPE_NAME;
	
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
	CDOMChooserFacadeImpl(String name, List<T> available, List<T> selected, int maxNewSelections)
	{
		this(name, available, selected, maxNewSelections, 
			LanguageBundle.getString("in_available"), //$NON-NLS-1$
			LanguageBundle.getString("in_typeName"), //$NON-NLS-1$
			LanguageBundle.getString("in_selected"),  //$NON-NLS-1$
			LanguageBundle.getString("in_selRemain"),  //$NON-NLS-1$
			LanguageBundle.getString("in_add"), //$NON-NLS-1$
			LanguageBundle.getString("in_remove")); //$NON-NLS-1$
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
	 */
	CDOMChooserFacadeImpl(String name, List<T> available,
		List<T> selected, int maxNewSelections,
		String availableTableTitle, String availableTableTypeNameTitle, 
		String selectedTableTitle,
		String selectionCountName, String addButtonName, String removeButtonName)
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
				
		// Build working content
		availableList = new DefaultListFacade<InfoFacade>(createInfoFacadeList(origAvailable));
		selectedList = new DefaultListFacade<InfoFacade>(createInfoFacadeList(origSelected));
		numSelectionsRemain = new DefaultReferenceFacade<Integer>(maxNewSelections);
		finalSelected = new ArrayList<T>(origSelected);
	}

	private List<InfoFacade> createInfoFacadeList(List<T> origAvailable2)
	{
		List<InfoFacade> infoFacadeList = new ArrayList<InfoFacade>(origAvailable2.size());
		for (T object : origAvailable2)
		{
			if (object instanceof InfoFacade)
			{
				infoFacadeList.add((InfoFacade) object);
			}
			else if (object instanceof CDOMObject)
			{
				CDOMInfoWrapper wrapper = new CDOMInfoWrapper((CDOMObject) object);
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
			else if (object instanceof CDOMChooserFacadeImpl.InfoWrapper)
			{
				selected = (T) ((CDOMChooserFacadeImpl.InfoWrapper) object).getObj();
			}
			else
			{
				selected = (T) object;
			}
			finalSelected.add(selected);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void rollback()
	{
		availableList.setContents(createInfoFacadeList(origAvailable));
		selectedList.setContents(createInfoFacadeList(origSelected));
		numSelectionsRemain.setReference(maxNewSelections);
		finalSelected.clear();
		finalSelected.addAll(origSelected);
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	private class CDOMInfoWrapper implements InfoFacade
	{
		private final CDOMObject cdomObj;

		public CDOMInfoWrapper(CDOMObject cdomObj)
		{
			this.cdomObj = cdomObj;
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return String.valueOf(cdomObj);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSource()
		{
			return SourceFormat.getFormattedString(cdomObj,
				Globals.getSourceDisplay(), true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSourceForNodeDisplay()
		{
			return SourceFormat.getFormattedString(cdomObj,
				SourceFormat.LONG, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getKeyName()
		{
			return cdomObj.getKeyName();
		}

		/**
		 * {@inheritDoc}
		 */
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
	}

	private class InfoWrapper implements InfoFacade
	{
		private final Object obj;

		public InfoWrapper(Object cdomObj)
		{
			this.obj = cdomObj;
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return String.valueOf(obj);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSource()
		{
			return "";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSourceForNodeDisplay()
		{
			return "";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getKeyName()
		{
			return obj.toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isNamePI()
		{
	    	return false;
		}

		/**
		 * @return the obj
		 */
		public Object getObj()
		{
			return obj;
		}
	}
}
