/*
 * AbilitySelectionPanel.java
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.TableColumnManager;
import pcgen.gui.filter.Filterable;
import pcgen.gui.tabs.IFilterableView;
import pcgen.gui.tabs.InfoTabUtils;
import pcgen.gui.tabs.ability.AbilityModel.Column;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Visibility;

/**
 * This is a common base class which handles the mundane tasks of displaying
 * Abilities in a table and allowing the user to interact with them.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 *
 * @since 5.11.1
 */
public abstract class AbilitySelectionPanel extends JPanel implements
		IFilterableView, IAbilityListFilter
{
	private PlayerCharacter thePC;
	private List<AbilityCategory> theCategoryList;

	/** The model that represents the list of abilities to choose from */
	protected AbilityModel theModel;
	/** The table the abilities are displayed in */
	protected JTreeTable theTable;
	/** The sorter object used to sort the table */
	protected JTreeTableSorter theSorter;

	/** A list of listeners registered to receive ability selection events */
	private List<IAbilitySelectionListener> theListeners =
			new ArrayList<IAbilitySelectionListener>(2);

	/** A list of listeners registered to receive ability category selection events */
	private List<IAbilityCategorySelectionListener> theCatListeners =
			new ArrayList<IAbilityCategorySelectionListener>();

	
	private String theOptionsRoot = "InfoAbility."; //$NON-NLS-1$

	/** enum for the possible view modes supported */
	public enum ViewMode
	{
		/** view mode for Type->Name */
		TYPENAME,
		/** view mode for Name (essentially a JTable) */
		NAMEONLY,
		/** view in requirement tree mode */
		PREREQTREE,
		/** view mode for Source->Name */
		SOURCENAME
	}

	/**
	 * The manner in which to display the tree.
	 */
	ViewMode theViewMode = ViewMode.PREREQTREE;

	/** 
	 * This is a temporary used to store the current value of the view mode
	 * when the QFilter changes it.
	 */
	private transient ViewMode theSavedViewMode = null;

	/** 
	 * The object that is controlling the filtering (using the PCGen Filters).
	 */
	private Filterable theFilter = null;
	private ListSelectionListener listSelListener = null;

	/**
	 * Construct and build a new panel to display a list of abilities.
	 * 
	 * @param aPC
	 * @param aCategory
	 */
	public AbilitySelectionPanel(final PlayerCharacter aPC,
		final AbilityCategory aCategory)
	{
		thePC = aPC;
		theCategoryList = new ArrayList<AbilityCategory>();
		theCategoryList.add(aCategory);

		theOptionsRoot += aCategory.getKeyName();

		final int vm =
				SettingsHandler
					.getPCGenOption(
						getFullOptionKey() + ".viewmode", getDefaultViewMode().ordinal()); //$NON-NLS-1$
		theViewMode = ViewMode.values()[vm];
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
			}
		});
	}

	/**
	 * Construct and build a new panel to display a list of abilities.
	 * 
	 * @param aPC
	 * @param aCategoryList
	 */
	public AbilitySelectionPanel(final PlayerCharacter aPC,
		final List<AbilityCategory> aCategoryList)
	{
		thePC = aPC;
		theCategoryList = aCategoryList;

		theOptionsRoot += aCategoryList.get(0).getDisplayLocation();

		final int vm =
				SettingsHandler
					.getPCGenOption(
						getFullOptionKey() + ".viewmode", getDefaultViewMode().ordinal()); //$NON-NLS-1$
		theViewMode =
				(vm >= 0 && vm < ViewMode.values().length)
					? ViewMode.values()[vm] : getDefaultViewMode();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
			}
		});
	}

	/**
	 * Return the <tt>PlayerCharacter</tt> this panel is displaying info for.
	 * 
	 * @return The PC
	 */
	public PlayerCharacter getPC()
	{
		return thePC;
	}

	/**
	 * Sets the PlayerCharacter this panel is displaying information for.
	 * 
	 * @param aPC The PlayerCharacter to set.
	 */
	public void setPC(final PlayerCharacter aPC)
	{
		thePC = aPC;
	}

	/**
	 * Return the <tt>AbilityCategory</tt> these abilities come from.
	 * TODO Convert this to react to current category
	 * @return The Ability category
	 */
	public AbilityCategory getCategory()
	{
		return  theCategoryList.get(0);
	}

	/**
	 * Return the <tt>AbilityCategories</tt> that are being displayed.
	 * @return The List of ability categories
	 */
	public List<AbilityCategory> getCategoryList()
	{
		return  Collections.unmodifiableList(theCategoryList);
	}

	/**
	 * Returns the list of abilities to display in this panel.
	 * 
	 * <p>This method is abstract and must be overridden by subclasses.
	 * 
	 * @return A <tt>List</tt> of <tt>Ability</tt> objects.
	 */
	protected abstract Map<AbilityCategory,Collection<Ability>> getAbilityList();

	/**
	 * Return a String key to use when saving state and options.
	 * 
	 * <p>This method is abstract and must be overridden by subclasses.
	 * 
	 * @return A String to use as a base key.
	 */
	protected abstract String getOptionKey();

	/**
	 * Return an indicator of whether to split the ability list by 
	 * category or not..
	 * 
	 * <p>This method is abstract and must be overridden by subclasses.
	 * 
	 * @return true if a category split should be used. 
	 */
	protected abstract boolean getSplitByCategory();

	/**
	 * Initializes the GUI components.
	 * 
	 * <p>This method constructs the model from the ability list.  It then
	 * builds the Tree to display the abilities in.  A Scroller is added to the
	 * table to provide scrolling support and a Column button is provided to 
	 * allow configuration of visible columns.
	 *
	 */
	protected void initComponents()
	{
		theModel =
				new AbilityModel(thePC, getAbilityList(), theCategoryList,
					theViewMode, getFullOptionKey(), getSplitByCategory());

		theModel.setAbilityFilter(this);

		theTable = new JTreeTable(theModel);
		final TableColumnModel tableColumnModel = theTable.getColumnModel();

		for (int i = 0; i < tableColumnModel.getColumnCount(); i++)
		{
			final TableColumn col = tableColumnModel.getColumn(i);

			final AbilityModel.Column amCol = AbilityModel.Column.get(i);
			final int colWidth =
					SettingsHandler.getPCGenOption(getFullOptionKey()
						+ ".sizecol." + amCol.toString(), amCol.getWidth()); //$NON-NLS-1$
			col.setPreferredWidth(colWidth);

			col.addPropertyChangeListener(new PropertyChangeListener()
			{

				public void propertyChange(final PropertyChangeEvent anEvt)
				{
					if (anEvt.getPropertyName().equals("width")) //$NON-NLS-1$
					{
						SettingsHandler
							.setPCGenOption(
								getFullOptionKey()
									+ ".sizecol." + Column.get(col.getModelIndex()), Integer.parseInt(anEvt.getNewValue().toString())); //$NON-NLS-1$
					}
				}

			});
		}

		theSorter =
				new JTreeTableSorter(theTable,
					(PObjectNode) theModel.getRoot(), theModel);

		addListSelectionListener();

		final JTree tree = theTable.getTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());

		final JScrollPane scrollPane =
				new JScrollPane(theTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);

		final JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		// TODO - This should probably be an icon.
		columnButton.setText("^"); //$NON-NLS-1$
		new TableColumnManager(theTable, columnButton, theModel);
	}

	/**
	 * Adds a new listener to the panel that will be advised of selection
	 * events that occur within the panel.
	 * 
	 * @param aListener The object to notify
	 */
	public void addAbilitySelectionListener(
		final IAbilitySelectionListener aListener)
	{
		if (theListeners.contains(aListener) == false)
		{
			theListeners.add(aListener);
		}
	}

	/**
	 * Returns a list of listeners listening for ability selection events.
	 * 
	 * @return An unmodifiable list of listeners.
	 */
	public List<IAbilitySelectionListener> getListeners()
	{
		return Collections.unmodifiableList(theListeners);
	}

	/**
	 * This method adds a <tt>ListSelectionListener</tt> to the table and adds
	 * itself as the listener so that we will be advised of selection changes
	 * with the table.
	 * 
	 * <p>Events are passed on to the registered listeners and the
	 * <tt>abilitySelected</tt> method is called.
	 * 
	 * @see #abilitySelected(Ability)
	 * @see #addAbilitySelectionListener(IAbilitySelectionListener)
	 *
	 */
	private void addListSelectionListener()
	{
		if (listSelListener == null)
		{
			listSelListener = new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = InfoTabUtils.getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						final Object temp =
								theTable.getTree().getPathForRow(idx)
									.getLastPathComponent();
						final Ability ability = getAbilityFromObject(temp);
						final PCAbilityCategory pcac = getPCAbilityCategoryFromObject(temp);
						
						for (final IAbilitySelectionListener listener : theListeners)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									abilitySelected(ability);
									listener.abilitySelected(ability);
									if (pcac != null)
									{
										categorySelected(pcac);
									}
								}
							});
						}
					}
				};
			};
		}
		theTable.getSelectionModel().addListSelectionListener(listSelListener);
	}
	
	/**
	 * Remove the selection listener from the ability table. Normally a 
	 * temporary measure to suppress event generation from a system triggered 
	 * change.  
	 */
	private void removeListSelectionListener()
	{
		if (listSelListener != null)
		{
			theTable.getSelectionModel().removeListSelectionListener(
				listSelListener);
		}
	}

	/**
	 * This method is called when an ability is selected (clicked on) in the
	 * table.
	 * 
	 * <p>This implementation does nothing.  Subclasses can override it to 
	 * provide custom behaviour.
	 * 
	 * @param anAbility The selected <tt>Ability</tt>
	 */
	protected void abilitySelected(@SuppressWarnings("unused")
	final Ability anAbility)
	{
		// Placeholder.
	}

	/**
	 * This method is called when an ability category is selected 
	 * (clicked on) in the table.
	 * 
	 * <p>This implementation does nothing.  Subclasses can override it to 
	 * provide custom behaviour.
	 * 
	 * @param anAbilityCat The selected <tt>PC Ability Category</tt>
	 */
	protected void categorySelected(@SuppressWarnings("unused")
	final PCAbilityCategory anAbilityCat)
	{
		// Placeholder.
	}

	/**
	 * This is a utility method that safely gets an <tt>Ability</tt> object from
	 * the tree.
	 * 
	 * @param anObject The node in the tree.
	 * 
	 * @return An <tt>Ability</tt> object or <tt>null</tt> if the selected item
	 * is not an Ability.
	 */
	protected Ability getAbilityFromObject(final Object anObject)
	{
		if (anObject == null)
		{
			// This should never happen.  I don't think we need a user
			// message here.
			Logging.debugPrint("No ability selected while processing event"); //$NON-NLS-1$
			return null;
		}

		if (anObject instanceof PObjectNode)
		{
			final Object temp = ((PObjectNode) anObject).getItem();

			if (temp instanceof Ability)
			{
				return (Ability) temp;
			}
		}

		return null;
	}

	/**
	 * Adds a new ability category selection listener to the panel that 
	 * will be advised of ability category selection events that occur 
	 * within the panel.
	 * 
	 * @param aListener the listener
	 */
	public void addAbilityCategorySelectionListener(
		final IAbilityCategorySelectionListener aListener)
	{
		if (theCatListeners.contains(aListener) == false)
		{
			theCatListeners.add(aListener);
		}
	}

	/**
	 * Gets the ability category listeners.
	 * 
	 * @return An unmodifiable list of the category listeners
	 */
	public List<IAbilityCategorySelectionListener> getCategoryListeners()
	{
		return Collections.unmodifiableList(theCatListeners);
	}

	/**
	 * This is a utility method that safely gets an <tt>Ability</tt> object from
	 * the tree.
	 * 
	 * @param anObject The node in the tree.
	 * 
	 * @return An <tt>Ability</tt> object or <tt>null</tt> if the selected item
	 * is not an Ability.
	 */
	protected PCAbilityCategory getPCAbilityCategoryFromObject(final Object anObject)
	{
		if (anObject == null)
		{
			// This should never happen.  I don't think we need a user
			// message here.
			Logging.debugPrint("No ability selected while processing event"); //$NON-NLS-1$
			return null;
		}

		if (anObject instanceof PObjectNode)
		{
			final Object temp = ((PObjectNode) anObject).getItem();

			if (temp instanceof PCAbilityCategory)
			{
				return (PCAbilityCategory) temp;
			}
			// See if the parent of this element is an ability category
			final PObjectNode parent = ((PObjectNode) anObject).getParent();
			if (parent != null)
			{
				return getPCAbilityCategoryFromObject(parent);
			}
		}

		return null;
	}

	/**
	 * Gets the starting view mode if one hasn't been set.
	 * 
	 * @return A view mode.
	 */
	public abstract ViewMode getDefaultViewMode();

	/**
	 * Sets the current view mode for this panel's tree.
	 * 
	 * @param aMode A mode to construct the tree in.
	 */
	public void setViewMode(final int aMode)
	{
		theViewMode = ViewMode.values()[aMode];
		if (theModel != null)
		{
			theModel.setViewMode(theViewMode);
		}
		SettingsHandler.setPCGenOption(getFullOptionKey() + ".viewmode", aMode); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.tabs.IFilterableView#clearQFilter()
	 */
	public void clearQFilter()
	{
		theModel.clearQFilter();
		if (theSavedViewMode != null)
		{
			theViewMode = theSavedViewMode;
			theSavedViewMode = null;
		}
		theModel.resetModel(thePC, theViewMode, false);
		theTable.updateUI();
	}

	/**
	 * @see pcgen.gui.tabs.IFilterableView#getInitialChoice()
	 */
	public int getInitialChoice()
	{
		return theViewMode.ordinal();
	}

	/**
	 * @see pcgen.gui.tabs.IFilterableView#getViewChoices()
	 */
	public List<String> getViewChoices()
	{
		final List<String> viewChoices = new ArrayList<String>(4);
		viewChoices.add(LanguageBundle.getString("in_typeName")); //$NON-NLS-1$
		viewChoices.add(LanguageBundle.getString("in_nameLabel")); //$NON-NLS-1$
		viewChoices.add(LanguageBundle.getString("in_preReqTree")); //$NON-NLS-1$
		viewChoices.add(LanguageBundle.getString("in_sourceName")); //$NON-NLS-1$
		return viewChoices;
	}

	/**
	 * @see pcgen.gui.tabs.IFilterableView#setQFilter(java.lang.String)
	 */
	public void setQFilter(String aFilter)
	{
		theModel.setQFilter(aFilter);

		if (theSavedViewMode == null)
		{
			theSavedViewMode = theViewMode;
		}
		theViewMode = ViewMode.NAMEONLY;
		theModel.resetModel(thePC, theViewMode, false);
		theTable.updateUI();
	}

	/**
	 * @see pcgen.gui.tabs.IFilterableView#viewChanged(int)
	 */
	public void viewChanged(int aNewView)
	{
		setViewMode(aNewView);
		//		SettingsHandler.setFeatTab_SelectedListMode(viewSelectedMode);
		rebuildView();
	}

	/**
	 * This method forces the view to be rebuilt.
	 * 
	 * <p>This method should be called if <b>only</b> the view but <b>not</b>
	 * the data needs to be updated.  This could be because the view mode has
	 * changed or some other setting affects the visibility of the tree
	 * elements.
	 */
	public void rebuildView()
	{
		if (theTable != null)
		{
			final List<String> pathList = theTable.getExpandedPaths();
			theModel.resetModel(thePC, theViewMode, false);

			if (theSorter != null)
			{
				theSorter.sortNodeOnColumn();
			}
			theTable.updateUI();
			theTable.expandPathList(pathList);
		}
	}

	/**
	 * This method forces an update of the data and rebuilding of the view.
	 * 
	 * <p>This method should be called if the underlying data has changed.  For
	 * example, adding or removing an ability.
	 *
	 */
	public void update()
	{
		if (theTable != null)
		{
			List<String> pathList = theTable.getExpandedPaths();
			final int selRow = theTable.getSelectedRow();
			Object selObj = null;
			if (selRow >= 0)
			{
				selObj = theTable.getValueAt(selRow, 0);
			}
			//theTable.getTree().g
			//final Object selObj = theModel.nodeForRow(selRow);
			theModel.setAbilityList(getAbilityList(), thePC);
			if (theSorter != null)
			{
				theSorter.setRoot((PObjectNode) theModel.getRoot());
				theSorter.sortNodeOnColumn();
			}
			theTable.updateUI();
			theTable.expandPathList(pathList);
			removeListSelectionListener();
			if (selObj != null && selRow < theTable.getRowCount())
			{
				theTable.addRowSelectionInterval(selRow, selRow);
			}
			addListSelectionListener();
		}
	}

	/**
	 * Adds a <tt>Filterable</tt> object so that Abilities in the list will be
	 * filtered.
	 * 
	 * @param aFilterer A Filterable object.
	 */
	public void addFilterer(final Filterable aFilterer)
	{
		theFilter = aFilterer;
	}

	/**
	 * @see pcgen.gui.tabs.ability.IAbilityListFilter#accept(ViewMode, pcgen.core.Ability)
	 */
	public boolean accept(@SuppressWarnings("unused")
		final ViewMode aMode, final Ability anAbility)
	{
		if (!((anAbility.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT) || (anAbility
				.getSafe(ObjectKey.VISIBILITY) == Visibility.DISPLAY_ONLY)))
		{
			return false;
		}

		if (theFilter != null && theFilter.accept(getPC(), anAbility) == false)
		{
			return false;
		}

		return true;
	}

	String getFullOptionKey()
	{
		return theOptionsRoot + "." + getOptionKey(); //$NON-NLS-1$
	}
	
	/**
	 * Set a new category to be displayed. 
	 * @param aCategory The ability category
	 */
	public void setCategory(final AbilityCategory aCategory)
	{
		theCategoryList = new ArrayList<AbilityCategory>();
		theCategoryList.add(aCategory);
		if (theModel != null)
		{
			theModel.setCurrentAbilityCategory(aCategory);
		}
	}
	
	/**
	 * Set a new list of categories to be displayed. 
	 * @param aCategoryList The list of ability categories
	 */
	public void setCategories(final List<AbilityCategory> aCategoryList)
	{
		theCategoryList = new ArrayList<AbilityCategory>();
		theCategoryList.addAll(aCategoryList);
		if (theModel != null)
		{
			theModel.setAbilityCategories(aCategoryList);
		}
	}
}
