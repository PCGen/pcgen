/*
 * SelectedAbilityPanel.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */

package pcgen.gui.tabs.ability;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui.tabs.components.FilterPanel;
import pcgen.gui.tabs.components.RemoveItemPanel;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableMouseAdapter;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

/**
 * This class encapsulates a Panel used to display and manage selected
 * Abilities.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class SelectedAbilityPanel extends AbilitySelectionPanel
{
	private static final String SELECTED_LABEL =
			LanguageBundle.getString("in_selected") + ": "; //$NON-NLS-1$//$NON-NLS-2$

	private RemoveItemPanel theRemoveButton;

	private JMenuItem theRemoveMenu;

	/**
	 * Construct the selected panel.
	 * 
	 * <p>This adds a <tt>FilterPanel</tt> and <tt>RemoveItemPanel</tt> to the 
	 * base class table.
	 * 
	 * @param aPC The PC
	 * @param aCategory The <tt>AbilityCategory</tt> to display selected
	 * Abilities for.
	 * 
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel
	 */
	public SelectedAbilityPanel(final PlayerCharacter aPC,
		final List<AbilityCategory> aCategoryList)
	{
		super(aPC, aCategoryList);

		setLayout(new BorderLayout());

		add(new FilterPanel(this, SELECTED_LABEL), BorderLayout.NORTH);

		theRemoveButton = new RemoveItemPanel();
		theRemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			final ActionEvent evt)
			{
				removeAbility();
			}
		});

		add(theRemoveButton, BorderLayout.SOUTH);
	}

	/**
	 * Called when the user has requested the selected ability be removed.
	 * 
	 * <p>This method simply informs any listeners that the request has taken
	 * place.
	 */
	public void removeAbility()
	{
		final Object temp = theTable.getTree().getLastSelectedPathComponent();
		final Ability ability = getAbilityFromObject(temp);
		if (ability == null)
		{
			return;
		}

		for (final IAbilitySelectionListener listener : getListeners())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					listener.removeAbility(ability);
				}
			});
		}
	}

	/**
	 * This method is overridden so that we can display a count of selected
	 * abilities in the table header.
	 *  
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#update()
	 */
	@Override
	public void update()
	{
		if (theTable != null)
		{
			int nameColIndex = theTable.convertColumnIndexToView(0);
			if (nameColIndex < 0)
			{
				nameColIndex = 0;
			}
			TableColumn nameCol =
					theTable.getColumnModel().getColumn(nameColIndex);
			String curVal = nameCol.getHeaderValue().toString();
			int endInd = curVal.lastIndexOf('(');
			if (endInd != -1)
			{
				curVal = curVal.substring(0, endInd).trim();
			}
			final BigDecimal spent = getPC().getAbilityPoolSpent(getCategory());
			NumberFormat format = NumberFormat.getNumberInstance();
			final String txt =
					curVal + " (" + format.format(spent.stripTrailingZeros()) + ")"; //$NON-NLS-1$//$NON-NLS-2$
			nameCol.setHeaderValue(txt);
		}
		super.update();
	}

	/**
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#getAbilityList()
	 */
	@Override
	protected Map<AbilityCategory,Collection<Ability>> getAbilityList()
	{
		return buildPCAbilityList();
	}

	/**
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#getOptionKey()
	 */
	@Override
	protected String getOptionKey()
	{
		return "selected"; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#getDefaultViewMode()
	 */
	@Override
	public ViewMode getDefaultViewMode()
	{
		return ViewMode.NAMEONLY;
	}

	/**
	 * @see pcgen.gui.tabs.IFilterableView#getViewChoices()
	 */
	public List<String> getViewChoices()
	{
		final List<String> viewChoices = new ArrayList<String>(4);
		viewChoices.add(LanguageBundle.getString("in_catTypeName")); //$NON-NLS-1$
		viewChoices.add(LanguageBundle.getString("in_catNameLabel")); //$NON-NLS-1$
		viewChoices.add(LanguageBundle.getString("in_catPreReqTree")); //$NON-NLS-1$
		viewChoices.add(LanguageBundle.getString("in_catSourceName")); //$NON-NLS-1$
		return viewChoices;
	}

	/**
	 * Overridden to add a MouseListener and Popup menu.
	 * 
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#initComponents()
	 */
	@Override
	protected void initComponents()
	{
		super.initComponents();

		theTable.addMouseListener(new JTreeTableMouseAdapter(theTable,
			new SelectedClickHandler(), false));
		theTable.addPopupMenu(new AbilityPopupMenu(theTable));
	}

	/**
	 * This method gets the ability list from the current PC for each ability 
	 * category and sorts each for display. Note that duplicates may appear 
	 * where an ability is taken in different natures (e.g. normal and auto)
	 * 
	 * @return A list of the current PCs abilities.
	 */
	private Map<AbilityCategory,Collection<Ability>> buildPCAbilityList()
	{
		final List<AbilityCategory> catList = getCategoryList();
		final Map<AbilityCategory, Collection<Ability>> abilityList =
				new HashMap<AbilityCategory, Collection<Ability>>();
		for (AbilityCategory abilityCategory : catList)
		{
			List<Ability> list = getPC().getAggregateAbilityListNoDuplicates(
					abilityCategory);
			Globals.sortPObjectListByName(list);
			abilityList.put(abilityCategory, list);
		}

		return abilityList;
	}

	private void setRemoveEnabled(final boolean enabled)
	{
		theRemoveButton.setEnabled(enabled);
		theRemoveMenu.setEnabled(enabled);
	}

	private class AbilityPopupMenu extends JPopupMenu
	{
		private JTreeTable theTreeTable;

		private AbilityPopupMenu(final JTreeTable aTreeTable)
		{
			theTreeTable = aTreeTable;
			final String menuText =
					LanguageBundle
						.getFormattedString(
							"InfoAbility.Menu.Remove", getCategory().getDisplayName()); //$NON-NLS-1$
			final String menuTip =
					LanguageBundle
						.getFormattedString(
							"InfoAbility.Menu.Remove.Tooltip", getCategory().getDisplayName()); //$NON-NLS-1$
			this.add(theRemoveMenu =
					Utility.createMenuItem(menuText, new ActionListener()
					{
						public void actionPerformed(@SuppressWarnings("unused")
						final ActionEvent evt)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									removeAbility();
								}
							});
						}
					}, null, (char) 0,
						"shortcut MINUS", menuTip, "Remove16.gif", true)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
		 */
		@Override
		public void show(final Component source, final int x, final int y)
		{
			final PObjectNode node =
					(PObjectNode) theTreeTable.getTree()
						.getLastSelectedPathComponent();
			if (node != null && node.getItem() instanceof Ability)
			{
				super.show(source, x, y);
			}
			else
			{
				removeAll();

				super.show(source, x, y);
			}
		}
	}

	private class SelectedClickHandler implements ClickHandler
	{
		/**
		 * Allow double click to remove an ability.
		 * 
		 * @see pcgen.gui.utils.ClickHandler#doubleClickEvent()
		 */
		public void doubleClickEvent()
		{
			removeAbility();
		}

		/**
		 * If this is an <tt>Ability</tt> object it is selectable.
		 * 
		 * @see pcgen.gui.utils.ClickHandler#isSelectable(java.lang.Object)
		 */
		public boolean isSelectable(final Object obj)
		{
			return !(obj instanceof String);
		}

		/**
		 * Not used.
		 * 
		 * @see pcgen.gui.utils.ClickHandler#singleClickEvent()
		 */
		public void singleClickEvent()
		{
			// Do Nothing
		}
	}

	/**
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#abilitySelected(pcgen.core.Ability)
	 */
	@Override
	protected void abilitySelected(final Ability anAbility)
	{
		super.abilitySelected(anAbility);
		if (anAbility != null)
		{
			setRemoveEnabled(getPC().getAbilityNature(anAbility) == Nature.NORMAL);
		}
		else
		{
			setRemoveEnabled(false);
		}
	}

	/**
	 * Process the selection of a new category.
	 * 
	 * @param anAbilityCat the ability category
	 * 
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#abilitySelected(pcgen.core.Ability)
	 */
	@Override
	protected void categorySelected(final PCAbilityCategory anAbilityCat)
	{
		super.categorySelected(anAbilityCat);
		if (anAbilityCat != null)
		{
			for (final IAbilityCategorySelectionListener listener : getCategoryListeners())
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						listener.abilityCategorySelected(anAbilityCat
							.getCategory());
					}
				});
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see pcgen.gui.tabs.ability.AbilitySelectionPanel#getSplitByCategory()
	 */
	@Override
	protected boolean getSplitByCategory()
	{
		return true;
	}
	
}
