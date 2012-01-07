/*
 * LanguageBasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 22, 2002, 11:38 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.util.PropertyFactory;

/**
 * <code>LanguageBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class LanguageBasePanel extends BasePanel<Language>
{
	//private AvailableSelectedPanel pnlLanguageType;
	private TypePanel pnlLanguageType;

	/** Creates new form LanguageBasePanel */
	public LanguageBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	/**
	 * Set the list of available types
	 * @param aList
	 * @param sort
	 */
	public void setTypesAvailableList(final List<Type> aList, final boolean sort)
	{
		pnlLanguageType.setAvailableList(aList, sort);
	}

	/**
	 * Set the list of selected types
	 * @param aList
	 * @param sort
	 */
	public void setTypesSelectedList(final List<Type> aList, final boolean sort)
	{
		pnlLanguageType.setSelectedList(aList, sort);
	}

	/**
	 * Get the list of selected types
	 * @return the list of selected types
	 */
	public Object[] getTypesSelectedList()
	{
		return pnlLanguageType.getSelectedList();
	}

	@Override
	public void updateData(Language thisPObject)
	{
		for (Object o : getTypesSelectedList())
		{
			thisPObject.addToListFor(ListKey.TYPE, Type.getConstant(o.toString()));
		}
	}

	@Override
	public void updateView(Language thisLanguage)
	{
		List<Type> availableList = new ArrayList<Type>();
		List<Type> selectedList = new ArrayList<Type>();

		for (Language aLanguage : Globals.getContext().ref.getConstructedCDOMObjects(Language.class))
		{
			for (Type type : aLanguage.getTrueTypeList(false))
			{
				if (!type.equals(Type.CUSTOM))
				{
					if (!availableList.contains(type))
					{
						availableList.add(type);
					}
				}
			}
		}

		//
		// remove this language's type from the available list and place into selected list
		//
		for (Type type : thisLanguage.getTrueTypeList(false))
		{
			if (!type.equals(Type.CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);
	}

	private void initComponentContents()
	{
	    // TODO This method currently does nothing?
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		//pnlLanguageType = new AvailableSelectedPanel();
		pnlLanguageType = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new BorderLayout());

		//pnlLanguageType.setHeader(PropertyFactory.getString("in_type"));
		add(pnlLanguageType, BorderLayout.CENTER);
	}
}
