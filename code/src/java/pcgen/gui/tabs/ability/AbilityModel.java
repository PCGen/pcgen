/*
 * AbilityModel.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Visibility;

/**
 * Extends AbstractTreeTableModel to build an available or
 * selected abilities tree for this tab.
 * <p/>
 * The basic idea of the TreeTableModel is that there is a
 * single <code>root</code> object. This root object has a null
 * <code>parent</code>.    All other objects have a parent which
 * points to a non-null object.    parent objects contain a list of
 * <code>children</code>, which are all the objects that point
 * to it as their parent.
 * objects (or <code>nodes</code>) which have 0 children
 * are leafs (the end of that linked list).
 * nodes which have at least 1 child are not leafs.
 * Leafs are like files and non-leafs are like directories.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class AbilityModel extends AbstractTreeTableModel implements TableColumnManagerModel
{
	private AbilitySelectionPanel.ViewMode theViewMode = AbilitySelectionPanel.ViewMode.TYPENAME;

	private PlayerCharacter thePC = null;
	private List<Ability> theAbilityList;
	private AbilityCategory theCategory;
	
	private IAbilityListFilter theFilter = null;
	
	private String theOptionsRoot = "InfoAbility."; //$NON-NLS-1$
	
	private PObjectNode typeRoot = null;
	private PObjectNode sourceRoot = null;

	/**
	 * Creates an AbilityModel.
	 * 
	 * @param aPC The PlayerCharacter this model is for.
	 * @param aList The list of <tt>Ability</tt> objects to manage
	 * @param aCategory The <tt>AbilityCategory</tt> this list comes from.
	 * @param viewMode
	 * @param anOptionRoot The key to store options under.
	 */
	public AbilityModel(final PlayerCharacter aPC,
						final List<Ability> aList,
						final AbilityCategory aCategory,
						final AbilitySelectionPanel.ViewMode viewMode,
						final String anOptionRoot) 
	{
		super(null);
		thePC = aPC;
		theAbilityList = aList;

		theOptionsRoot = anOptionRoot;
		
		theViewMode = viewMode;
		
		theCategory = aCategory;

		for ( final Column column : Column.values() )
		{
			column.setVisible(SettingsHandler.getPCGenOption(theOptionsRoot + ".viewcol." + column.toString(), column.isVisible())); //$NON-NLS-1$
		}
		resetModel(thePC, viewMode, false);
	}

	private void buildDefaultRoots()
	{
		if ( typeRoot != null )
		{
			return;
		}
		
		typeRoot = new PObjectNode();
		sourceRoot = new PObjectNode();
		
		final SortedSet<String> typeSet = new TreeSet<String>();
		final SortedSet<String> sourceSet = new TreeSet<String>();
		// We will use the global lists for this
		for ( final Ability ability : Globals.getAbilityList(theCategory) )
		{
			if (!((ability.getVisibility() == Visibility.DEFAULT)
			  || (ability.getVisibility() == Visibility.DISPLAY_ONLY)))
			{
				continue;
			}

			typeSet.addAll(ability.getTypeList(true));
			final String sourceString = ability.getSourceEntry().getSourceBook().getLongName();
			if ( sourceString != null )
			{
				sourceSet.add(sourceString);
			}
		}
		final PObjectNode[] ccTypes = new PObjectNode[typeSet.size()];
		int i = 0;
		for ( final String type : typeSet )
		{
			ccTypes[i] = new PObjectNode();
			ccTypes[i].setItem(type);
			ccTypes[i].setParent(typeRoot);
			i++;
		}
		typeRoot.setChildren(ccTypes);

		final PObjectNode[] ccSources = new PObjectNode[sourceSet.size()];
		i = 0;
		for ( final String source : sourceSet )
		{
			ccSources[i] = new PObjectNode();
			ccSources[i].setItem(source);
			ccSources[i].setParent(sourceRoot);
			i++;
		}
		sourceRoot.setChildren(ccSources);
		
	}
	
	/**
	 * Set the mode used to display the tree.
	 * 
	 * @param aMode A <tt>ViewMode</tt> used to display the tree.
	 */
	public void setViewMode( final AbilitySelectionPanel.ViewMode aMode )
	{
		theViewMode = aMode;
	}
	
	/**
	 * Sets an object to use to control if which abilities should be shown.
	 * 
	 * @param aFilter An object implementing the <tt>IAbilityListFilter</tt>
	 * interface.
	 */
	public void setAbilityFilter( final IAbilityListFilter aFilter )
	{
		theFilter = aFilter;
		resetModel(thePC, theViewMode, false);
	}
	
	/**
	 * Returns Class for the column.
	 * @param column
	 * @return Class
	 */
	@Override
	public Class<?> getColumnClass(final int column)
	{
		if (column == Column.NAME.ordinal())
		{
			return TreeTableModel.class;
		}
		return String.class;
	}

	/* The JTreeTableNode interface. */

	/**
	 * Returns int number of columns.
	 * @return column count
	 */
	public int getColumnCount()
	{
		return Column.values().length;
	}

	/**
	 * Returns String name of a column.
	 * @param column
	 * @return column name
	 */
	public String getColumnName(int column)
	{
		return Column.values()[column].getName();
	}

	/**
	 * @see pcgen.gui.utils.AbstractTreeTableModel#getRoot()
	 */
	// TODO - Do we need to define this??
	@Override
	public Object getRoot()
	{
		return super.getRoot();
	}

	/**
	 * Returns Object value of the column.
	 * @param node
	 * @param aColumn
	 * @return value
	 */
	public Object getValueAt(final Object node, final int aColumn)
	{
		PObjectNode fn = (PObjectNode) node;
		Object retVal = null;

		Ability ability = null;
		Object temp = fn.getItem();
		if (temp instanceof Ability)
		{
			ability = (Ability) temp;
		}

		Column column = Column.values()[aColumn];
		switch (column)
		{
			case NAME:
				retVal = fn.toString();
				break;
			case TYPE:
				if (ability != null)
				{
					retVal = ability.getTypeUsingFlag(true);
				}
				break;
			case COST:
				if (ability != null)
				{
					retVal = ability.getCostString();
				}
				break;
			case MULTIPLES:
				if (ability != null)
				{
					if (ability.isMultiples())
					{
						retVal = PropertyFactory.getString("in_yes"); //$NON-NLS-1$
					}
					else
					{
						retVal = PropertyFactory.getString("in_no"); //$NON-NLS-1$
					}
				}
				break;
			case STACKS:
				if (ability != null)
				{
					if (ability.isStacks())
					{
						retVal = PropertyFactory.getString("in_yes"); //$NON-NLS-1$
					}
					else
					{
						retVal = PropertyFactory.getString("in_no"); //$NON-NLS-1$
					}
				}
				break;
			case REQUIREMENTS:
				if (ability != null)
				{
					retVal = ability.preReqStrings();
				}
				break;
			case DESCRIPTION:
				if (ability != null)
				{
					retVal = ability.piDescSubString(thePC);
				}
				break;
			case CHOICES:
				retVal = fn.getChoices();
				break;
			case SOURCE:
				retVal = fn.getSource();
				break;
		}

		return retVal;
	}

	/**
	 * There must be a root object, though it can be hidden
	 * to make it's existence basically a convenient way to
	 * keep track of the objects
	 * @param aNode
	 */
	private void setRoot(PObjectNode aNode)
	{
		setRoot(aNode.clone());
	}

	/**
	 * Populates the tree with a list of feats by name only (not much of a tree).
	 * Simply adds feats to the root node.
	 *
	 * @param showAll Force displaying of all abilities
	 */
	private void buildTreeNameOnly(final boolean showAll)
	{
		super.setRoot(new PObjectNode());
		String qFilter = this.getQFilter();

		for ( final Ability ability : theAbilityList )
		{
			if ( showAll == true || theFilter == null || theFilter.accept(theViewMode, ability))
			{
				PObjectNode aFN = new PObjectNode();
				aFN.setParent((PObjectNode) super.getRoot());

				switch ( ability.getFeatType() )
				{
				case AUTOMATIC:
					aFN.setColor(SettingsHandler.getFeatAutoColor());
					break;
				case VIRTUAL:
					aFN.setColor(SettingsHandler.getFeatVirtualColor());
					break;
				}

				aFN.setItem(ability);

				//Does anyone know why we don't call
				//aFN.setIsValid(aFeat.passesPreReqToGain()) here?
				if (qFilter == null ||
					( ability.getDisplayName().toLowerCase().indexOf(qFilter) >= 0 ||
					 ability.getType().toLowerCase().indexOf(qFilter) >= 0 ))
				{
					((PObjectNode) super.getRoot()).addChild(aFN);
				}
			}
		}
	}

	/**
	 * Populates the model with feats in a prerequisite tree.  It retrieves
	 * all feats, then places the feats with no prerequisites under the root
	 * node.  It then iterates the remaining feats and places them under
	 * their appropriate prerequisite feats, creating a node called "Other" at
	 * the end if the prerequisites were not met.
	 *
	 * @param showAll Force showing all abilities
	 */
	private void buildTreePrereqTree(final boolean showAll)
	{
		setRoot(new PObjectNode());

		// This list initially contains all abilities that pass the filter.
		final List<Ability> fList = new ArrayList<Ability>();
		for ( final Ability ability : theAbilityList )
		{
			if ( showAll || theFilter == null || theFilter.accept(theViewMode, ability) )
			{
				fList.add( ability );
			}
		}

		// Loop through the list of abilities and find any that don't have a
		// PREABILITY tag.  These are added directly to the tree.
		final List<Ability> aList = new ArrayList<Ability>();
		for (int i = 0; i < fList.size(); ++i)
		{
			final Ability aFeat = fList.get(i);

			// TODO - Change this when we have PRExxx tags for Ability
			if (!aFeat.hasPreReqTypeOf("FEAT")) //$NON-NLS-1$
			{
				fList.remove(aFeat);
				aList.add(aFeat);
				--i; // to counter increment
			}
		}

		// Add these abilities to the tree
		final PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
		if ( rootAsPObjectNode == null )
		{
			return;
		}

		final PObjectNode[] directChildren = new PObjectNode[aList.size()];

		for (int i = 0; i < aList.size(); ++i)
		{
			final Ability ability = aList.get(i);
			
			directChildren[i] = new PObjectNode();
			directChildren[i].setItem(ability);
			directChildren[i].setParent(rootAsPObjectNode);

			switch ( ability.getFeatType() )
			{
			case AUTOMATIC:
				directChildren[i].setColor(SettingsHandler.getFeatAutoColor());
				break;
			case VIRTUAL:
				directChildren[i].setColor(SettingsHandler.getFeatVirtualColor());
				break;
			}
		}

		rootAsPObjectNode.setChildren(directChildren);

		// fList now contains only those abilities that have prereqs on other
		// abilities.
		int loopmax = 6; // only go 6 levels...
		while ((fList.size() > 0) && (loopmax-- > 0))
		{
			for (int i = 0; i < fList.size(); ++i)
			{
				final Ability ability = fList.get(i);
				int placed = 0;

				// Make a copy of the prereq
				// list so we don't destroy
				// the other prereqs
				List<Prerequisite> preReqList = new ArrayList<Prerequisite>();

				for (int pi = ability.getPreReqCount() - 1; pi >= 0; --pi)
				{
					final Prerequisite prereq = ability.getPreReq(pi);

					// TODO - Fix this. See comment above.
					if ((prereq.getKind() != null) && prereq.getKind().equalsIgnoreCase("FEAT")) //$NON-NLS-1$
					{
						preReqList.add(prereq);
					}
				}
				// TODO - What should happen if an ability has multiple pres?
				for (int j = 0; j < rootAsPObjectNode.getChildCount(); ++j)
				{
					final PObjectNode po = rootAsPObjectNode.getChild(j);

					placed = placedThisFeatInThisTree(ability, po, preReqList);

					if (placed > 0)
					{
						break;
					}
				}

				// TODO - Make a constant for this?
				if (placed == 2) // i.e. tree match
				{
					fList.remove(ability);
					--i; // since we're incrementing in the for loop
				}
			}
		}

		// These abilities have PREABILITY tags but we couldn't find a match
		// for the ability.  
		// TODO - This shouldn't happen should it?
		if (fList.size() > 0)
		{
			PObjectNode po = new PObjectNode();
			po.setItem(PropertyFactory.getString("in_other")); //$NON-NLS-1$
			final PObjectNode[] cc = new PObjectNode[fList.size()];

			for (int i = 0; i < fList.size(); ++i)
			{
				final Ability ability = fList.get(i);
				
				cc[i] = new PObjectNode();
				cc[i].setItem(ability);
				cc[i].setParent(po);

				switch ( ability.getFeatType() )
				{
				case AUTOMATIC:
					cc[i].setColor(SettingsHandler.getFeatAutoColor());
					break;
				case VIRTUAL:
					cc[i].setColor(SettingsHandler.getFeatVirtualColor());
					break;
				}
			}

			po.setChildren(cc);
			rootAsPObjectNode.addChild(po);
		}
	}

	/**
	 * Populates the list of feats as a type->name tree.  It sets the root
	 * of the tree to <code>InfoFeats.typeRoot</code>, which contains
	 * the types.  It then iterates the feat list and adds each feat to
	 * all applicable types.
	 *
	 * @param showAll Force displaying of all abilities
	 */
	private void buildTreeTypeName(final boolean showAll)
	{
		if ( typeRoot == null )
		{
			return;
		}
		setRoot(typeRoot);

		final PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
		if ( rootAsPObjectNode == null )
		{
			return;
		}

		for ( final Ability ability : theAbilityList )
		{
			if ( showAll || theFilter == null || theFilter.accept(theViewMode, ability))
			{
				for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
				{
					if (ability.isType(rootAsPObjectNode.getChild(i).toString()))
					{
						final PObjectNode aFN = new PObjectNode();

						switch ( ability.getFeatType() )
						{
						case AUTOMATIC:
							aFN.setColor(SettingsHandler.getFeatAutoColor());
							break;
						case VIRTUAL:
							aFN.setColor(SettingsHandler.getFeatVirtualColor());
							break;
						}

						aFN.setParent(rootAsPObjectNode.getChild(i));
						aFN.setItem(ability);
//						if (!Globals.checkRule(RuleConstants.FEATPRE))
//						{
//							// TODO - This seems to have no effect
//							PrereqHandler.passesAll( ability.getPreReqList(), null, ability );
//						}
						rootAsPObjectNode.getChild(i).addChild(aFN);
					}
				}
			}
		}
	}

	/**
	 * Populates the list of feats as a source->name tree.  It sets the root
	 * of the tree to <code>InfoFeats.sourceRoot</code>, which contains
	 * the sources.  It then iterates the feat list and adds each feat to
	 * all applicable source.
	 *
	 * @param showAll Force displaying of all abilities
	 */
	private void buildTreeSourceName(final boolean showAll)
	{
		if ( sourceRoot == null )
		{
			return;
		}
		setRoot(sourceRoot);

		final PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
		// TODO - This shouldn't really be required since I just set the damn
		// root node.
		if ( rootAsPObjectNode == null )
		{
			return;
		}
		
		for ( final Ability ability : theAbilityList )
		{
			if ( showAll || theFilter == null || theFilter.accept(theViewMode, ability) )
			{
				final String sourceString = ability.getSourceEntry().getSourceBook().getLongName();
				if ( sourceString == null )
				{
					Logging.errorPrint("In InfoFeats.buildTreeSourceName the feat " + ability + " has no source long entry.");
				}
				//
				for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
				{
					if (sourceString.equals(rootAsPObjectNode.getChild(i).toString()))
					{
						final PObjectNode aFN = new PObjectNode();

						switch ( ability.getFeatType() )
						{
						case AUTOMATIC:
							aFN.setColor(SettingsHandler.getFeatAutoColor());
							break;
						case VIRTUAL:
							aFN.setColor(SettingsHandler.getFeatVirtualColor());
							break;
						}

						aFN.setParent(rootAsPObjectNode.getChild(i));
						aFN.setItem(ability);

						// TODO - This code appears to have no effect.
//						if (Globals.checkRule(RuleConstants.FEATPRE))
//						{
//							// Method no longer exitsts - aFN.setIsValid(true);
//						}
//						else
//						{
//							PrereqHandler.passesAll( aFeat.getPreReqList(), getPc(), aFeat );
//						}
						rootAsPObjectNode.getChild(i).addChild(aFN);
					}
				}
			}
		}
	}

	private int placedThisFeatInThisTree(final Ability anAbility, 
										 final PObjectNode po, 
										 final List<Prerequisite> aList) 
	{
		final Ability parentAbility = (Ability) po.getItem(); // must be a Feat
		boolean trychildren = false;
		boolean thisisit = false;

		for (final Prerequisite prereq : aList)
		{
			final String pString = prereq.getKey();

			if (pString.equalsIgnoreCase(parentAbility.getKeyName()))
			{
				thisisit = true;
			}
			else
			{
				trychildren = true; // might be a child
			}

			if (thisisit)
			{
				final PObjectNode p = new PObjectNode();
				p.setItem(anAbility);
				p.setParent(po);
				po.addChild(p);

				switch ( anAbility.getFeatType() )
				{
				case AUTOMATIC:
					p.setColor(SettingsHandler.getFeatAutoColor());
					break;
				case VIRTUAL:
					p.setColor(SettingsHandler.getFeatVirtualColor());
					break;
				}

				return 2; // successfully added
			}
			else if (trychildren)
			{
				for (int i = 0; i < po.getChildCount(); ++i)
				{
					int j = placedThisFeatInThisTree(anAbility, po.getChild(i), aList);

					if (j == 2)
					{
						return 2;
					}
				}
			}
		}
		return 0; // not here
	}

	/**
	 * Sets the ability list to use.
	 * 
	 * @param aList A list of Abilities to manage.
	 */
	public void setAbilityList(final List<Ability> aList)
	{
		theAbilityList = aList;
		resetModel(thePC, theViewMode, false);
	}
	
	/**
	 * This assumes the FeatModel exists but needs to be repopulated
	 * Calls the various <code>buildTreeXXX</code> methods based on the
	 * <code>mode</code> parameter.
	 *
	 * @param mode      View mode for this tree, one of the ViewModes defined in
	 * 					<tt>AbilitySelectionPanel</tt>
	 * @param showAll
	 */
	public void resetModel(	final PlayerCharacter aPC, 
							final AbilitySelectionPanel.ViewMode mode, 
							final boolean showAll)
	{
		thePC = aPC;
		// We are going to build and cache the type and source tree roots.
		buildDefaultRoots();

		switch (mode)
		{
			case TYPENAME:
				buildTreeTypeName(showAll);

				break;

			case NAMEONLY:
				buildTreeNameOnly(showAll);

				break;

			case PREREQTREE:
				buildTreePrereqTree(showAll);

				break;

			case SOURCENAME:
				buildTreeSourceName(showAll);

				break;
		}

		if (super.getRoot() != null )
		{
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		}
	}

	/**
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnList()
	 */
	public List<String> getMColumnList()
	{
		final List<String> retList = new ArrayList<String>();
		
		final Column[] columns = Column.values();
		for(int i = 1; i < columns.length; i++) 
		{
			retList.add(columns[i].getName());
		}
		return retList;
	}

	/**
	 * @see pcgen.gui.TableColumnManagerModel#isMColumnDisplayed(int)
	 */
	public boolean isMColumnDisplayed(final int col)
	{
		return Column.values()[col].isVisible();
	}

	private void setColumnOption(final Column col, final String anOption, final String val)
	{
		SettingsHandler.setPCGenOption(theOptionsRoot + "." + anOption + "." + col.toString(), val); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private int getColumnOption(final Column aCol, final String anOption, final int aDefault)
	{
		return SettingsHandler.getPCGenOption(theOptionsRoot + "." + anOption + "." + aCol.toString(), aDefault); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @see pcgen.gui.TableColumnManagerModel#setMColumnDisplayed(int, boolean)
	 */
	public void setMColumnDisplayed(int col, boolean disp)
	{
		Column.values()[col].setVisible(disp);
		setColumnOption(Column.values()[col], "viewcol", String.valueOf(disp)); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnOffset()
	 */
	public int getMColumnOffset()
	{
		return 1;
	}

	/**
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnDefaultWidth(int)
	 */
	public int getMColumnDefaultWidth(int col) 
	{
		return getColumnOption(Column.values()[col], "sizecol", Column.values()[col].getWidth()); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.TableColumnManagerModel#setMColumnDefaultWidth(int, int)
	 */
	public void setMColumnDefaultWidth(final int col, final int width) 
	{
		Column.values()[col].setWidth(width);
		setColumnOption(Column.values()[col], "sizecol", String.valueOf(width)); //$NON-NLS-1$
	}
	
	/**
	 * @see pcgen.gui.TableColumnManagerModel#resetMColumn(int, javax.swing.table.TableColumn)
	 */
	public void resetMColumn(final int col, final TableColumn tColumn) 
	{
		final Column column = Column.values()[col];
		switch(column)
		{
			case COST:
			case MULTIPLES:
			case STACKS:
				tColumn.setCellRenderer(new pcgen.gui.utils.JTableEx.AlignCellRenderer(SwingConstants.CENTER));
				break;
				
			default:
				break;
		}
	}

	/**
	 * An enum for the Columns in the table.
	 */
	public enum Column {
		/** Name */
		NAME("AbilityModel.Columns.Name", 100, true), //$NON-NLS-1$
		/** Type */
		TYPE("AbilityModel.Columns.Type", 100, false), //$NON-NLS-1$
		/** Cost */
		COST("AbilityModel.Columns.Cost", 100, false), //$NON-NLS-1$
		/** Multiples */
		MULTIPLES("AbilityModel.Columns.Multiples", 100, false), //$NON-NLS-1$
		/** Stacks */
		STACKS("AbilityModel.Columns.Stacks", 100, false), //$NON-NLS-1$
		/** Prereqs */
		REQUIREMENTS("AbilityModel.Columns.Requirements", 100, false), //$NON-NLS-1$
		/** Description */
		DESCRIPTION("AbilityModel.Columns.Description", 100, false), //$NON-NLS-1$
		/** Choices */
		CHOICES("AbilityModel.Columns.Choices", 100, false), //$NON-NLS-1$
		/** Source */
		SOURCE("AbilityModel.Columns.Source", 100, false); //$NON-NLS-1$
	
		private String theName;
		private int theWidth;
		private boolean theVisibleFlag;
		
		Column(final String aName, final int aDefaultWidth, final boolean visible)
		{
			theName = PropertyFactory.getString(aName);
			theWidth = aDefaultWidth;
			theVisibleFlag = visible;
		}
		
		/**
		 * Sets the width of this column.
		 * 
		 * @param aWidth An integer width.
		 */
		public void setWidth(final int aWidth)
		{
			theWidth = aWidth;
		}
		
		/**
		 * Gets the width of this column.
		 * 
		 * @return An integer width.
		 */
		public int getWidth()
		{
			return theWidth;
		}
		
		/**
		 * Gets the display name for the column.
		 * 
		 * @return An internationized string.
		 */
		public String getName()
		{
			return theName;
		}
		
		/**
		 * Sets if the column is visible.
		 * 
		 * @param yesNo <tt>true</tt> makes the column visible.
		 */
		public void setVisible(final boolean yesNo)
		{
			theVisibleFlag = yesNo;
		}
		
		/**
		 * Checks if the column is visible.
		 * 
		 * @return <tt>true</tt> if the column is visibile.
		 */
		public boolean isVisible()
		{
			return theVisibleFlag;
		}
		
		/**
		 * Gets the enum for the specified ordinal.
		 * 
		 * @param ordinal An ordinal
		 * 
		 * @return The enum value
		 */
		public static Column get(final int ordinal)
		{
			return Column.values()[ordinal];
		}
	}
	
}
