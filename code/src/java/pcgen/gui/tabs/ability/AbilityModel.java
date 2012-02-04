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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.DescriptionFormatting;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.system.LanguageBundle;
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
public class AbilityModel extends AbstractTreeTableModel implements
		TableColumnManagerModel
{
	private AbilitySelectionPanel.ViewMode theViewMode =
			AbilitySelectionPanel.ViewMode.TYPENAME;

	private PlayerCharacter thePC = null;
	private Map<AbilityCategory,Collection<Ability>> theAbilityList;
	//private AbilityCategory theCategory;
	private List<AbilityCategory> theCategoryList;
	private AbilityCategory currAbilityCat;
	private boolean useCategoryRoot;

	private IAbilityListFilter theFilter = null;

	private String theOptionsRoot = "InfoAbility."; //$NON-NLS-1$

	private PObjectNode typeRoot = null;
	private PObjectNode sourceRoot = null;
	private PObjectNode categoryRoot = null;

	/**
	 * Creates an AbilityModel.
	 * 
	 * @param aPC The PlayerCharacter this model is for.
	 * @param aList The list of <tt>Ability</tt> objects to manage
	 * @param aCategory The <tt>AbilityCategory</tt> this list comes from.
	 * @param viewMode
	 * @param anOptionRoot The key to store options under.
	 */
	public AbilityModel(final PlayerCharacter aPC, final Collection<Ability> aList,
		final AbilityCategory aCategory,
		final AbilitySelectionPanel.ViewMode viewMode, final String anOptionRoot)
	{
		super(null);
		thePC = aPC;
		theAbilityList = new HashMap<AbilityCategory,Collection<Ability>>();
		theAbilityList.put(aCategory, aList);

		theOptionsRoot = anOptionRoot;

		theViewMode = viewMode;

		theCategoryList = new ArrayList<AbilityCategory>();
		theCategoryList.add(aCategory);
		currAbilityCat = aCategory;
		useCategoryRoot = false;

		setPanelSpecificDefaults();
		for (final Column column : Column.values())
		{
			column.setVisible(SettingsHandler.getPCGenOption(theOptionsRoot
				+ ".viewcol." + column.toString(), column.isVisible())); //$NON-NLS-1$
		}
		resetModel(thePC, viewMode, false);
	}

	/**
	 * Creates an AbilityModel.
	 * 
	 * @param aPC The PlayerCharacter this model is for.
	 * @param aMap The lists of <tt>Ability</tt> objects by category to manage
	 * @param aCategoryList The list of <tt>AbilityCategories</tt> the list comes from
	 * @param viewMode The style of view to be used.
	 * @param anOptionRoot The key to store options under.
	 * @param splitByCategory Should the list be split by category
	 */
	public AbilityModel(final PlayerCharacter aPC, final Map<AbilityCategory,Collection<Ability>> aMap,
		final List<AbilityCategory> aCategoryList,
		final AbilitySelectionPanel.ViewMode viewMode, final String anOptionRoot,
		final boolean splitByCategory)
	{
		super(null);
		thePC = aPC;
		theAbilityList = aMap;

		theOptionsRoot = anOptionRoot;

		theViewMode = viewMode;

		theCategoryList = aCategoryList;
		useCategoryRoot = splitByCategory;
		currAbilityCat = theCategoryList.get(0);

		setPanelSpecificDefaults();
		for (final Column column : Column.values())
		{
			column.setVisible(SettingsHandler.getPCGenOption(theOptionsRoot
				+ ".viewcol." + column.toString(), column.isVisible())); //$NON-NLS-1$
		}
		resetModel(thePC, viewMode, false);
	}

	/**
	 * Set any defaults for the specific panel the model is tied to. 
	 */
	private void setPanelSpecificDefaults()
	{
		if (theOptionsRoot.indexOf("selected") >= 0)
		{
			Column.CHOICES.setVisible(true);			
		}
	}

	private void buildDefaultRoots()
	{
		// Even if the category root already exists we need to refresh it
		categoryRoot = buildCategoryRoot();

		if (typeRoot != null)
		{
			return;
		}
		
		typeRoot = new PObjectNode();
		sourceRoot = new PObjectNode();

		// We will use the global lists for this
		Collection<Ability> coll = Globals.getContext().ref.getManufacturer(Ability.class, currAbilityCat).getAllObjects();
		addTypeNodes(typeRoot, coll);
		addSourceNodes(sourceRoot, coll);
	}

	/**
	 * Add nodes for each type to the supplied node. The list of types 
	 * is obtained from the abilities provided.
	 *   
	 * @param root The node to add source nodes to.
	 * @param abilityList The list of abilities to obtain types from.
	 */
	private void addTypeNodes(final PObjectNode root,
		final Collection<Ability> abilityList)
	{
		final SortedSet<String> typeSet = new TreeSet<String>();
		for (final Ability ability : abilityList)
		{
			if (!((ability.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT) || (ability
					.getSafe(ObjectKey.VISIBILITY) == Visibility.DISPLAY_ONLY)))
			{
				continue;
			}

			for (Type t : ability.getTrueTypeList(true))
			{
				typeSet.add(t.toString());
			}
		}
		final PObjectNode[] ccTypes = new PObjectNode[typeSet.size()];
		int i = 0;
		for (final String type : typeSet)
		{
			ccTypes[i] = new PObjectNode();
			ccTypes[i].setItem(type);
			ccTypes[i].setParent(root);
			i++;
		}
		root.setChildren(ccTypes);
	}

	/**
	 * Add nodes for each source to the supplied node. The list of sources 
	 * is obtained from the abilities provided.
	 *   
	 * @param root The node to add source nodes to.
	 * @param abilityList The list of abilities to obtain sources from.
	 */
	private void addSourceNodes(final PObjectNode root,
		final Collection<Ability> abilityList)
	{
		final SortedSet<String> sourceSet = new TreeSet<String>();
		// We will use the global lists for this
		for (final Ability ability : abilityList)
		{
			if (!((ability.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT) || (ability
					.getSafe(ObjectKey.VISIBILITY) == Visibility.DISPLAY_ONLY)))
			{
				continue;
			}

			final String sourceString = SourceFormat.getFormattedString(
					ability, SourceFormat.MEDIUM, false);
			if (sourceString.length() != 0)
			{
				sourceSet.add(sourceString);
			}
		}
		final PObjectNode[] ccSources = new PObjectNode[sourceSet.size()];
		int i = 0;
		for (final String source : sourceSet)
		{
			ccSources[i] = new PObjectNode();
			ccSources[i].setItem(source);
			ccSources[i].setParent(root);
			i++;
		}
		root.setChildren(ccSources);
	}

	private PObjectNode buildCategoryRoot()
	{
		PObjectNode catRoot = new PObjectNode();
		final ArrayList<PObjectNode> ccAbilityCats = new ArrayList<PObjectNode>();
		for (final AbilityCategory cat : theCategoryList)
		{
			if (cat.isVisible(thePC))
			{
				PCAbilityCategory pcac = new PCAbilityCategory(cat, thePC);
				PObjectNode node = new PObjectNode();
				node.setItem(pcac);
				node.setParent(catRoot);
				ccAbilityCats.add(node);
			}
		}
		catRoot.setChildren(ccAbilityCats);
		return catRoot;
	}

	/**
	 * Set the mode used to display the tree.
	 * 
	 * @param aMode A <tt>ViewMode</tt> used to display the tree.
	 */
	public void setViewMode(final AbilitySelectionPanel.ViewMode aMode)
	{
		theViewMode = aMode;
	}

	/**
	 * Sets an object to use to control if which abilities should be shown.
	 * 
	 * @param aFilter An object implementing the <tt>IAbilityListFilter</tt>
	 * interface.
	 */
	public void setAbilityFilter(final IAbilityListFilter aFilter)
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
					retVal = StringUtil.join(ability.getTrueTypeList(true), ". ");
//					retVal = CoreUtility.join(ability.getTypeList(true), '.');
				}
				break;
			case COST:
				if (ability != null)
				{
					retVal = ability.getSafe(ObjectKey.SELECTION_COST);
				}
				break;
			case MULTIPLES:
				if (ability != null)
				{
					if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
					{
						retVal = LanguageBundle.getString("in_yes"); //$NON-NLS-1$
					}
					else
					{
						retVal = LanguageBundle.getString("in_no"); //$NON-NLS-1$
					}
				}
				break;
			case STACKS:
				if (ability != null)
				{
					if (ability.getSafe(ObjectKey.STACKS))
					{
						retVal = LanguageBundle.getString("in_yes"); //$NON-NLS-1$
					}
					else
					{
						retVal = LanguageBundle.getString("in_no"); //$NON-NLS-1$
					}
				}
				break;
			case REQUIREMENTS:
				if (ability != null)
				{
					retVal = PrereqHandler.toHtmlString(ability.getPrerequisiteList());
				}
				break;
			case DESCRIPTION:
				if (ability != null)
				{
					retVal = DescriptionFormatting.piDescSubString(thePC, ability);
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
		super.setRoot(aNode.clone());
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
		buildSubTreeNameOnly(showAll, (PObjectNode) super.getRoot(),
			theAbilityList.get(currAbilityCat));
	}

	private void buildSubTreeNameOnly(final boolean showAll,
		final PObjectNode root, Collection<Ability> abilityList)
	{
		String qFilter = this.getQFilter();

		for (final Ability ability : abilityList)
		{
			if (showAll == true || theFilter == null
				|| theFilter.accept(theViewMode, ability))
			{
				PObjectNode aFN = new PObjectNode();
				aFN.setParent(root);
				Nature nature = thePC.getAbilityNature(ability);
				if (nature != null)
				{
					switch (nature)
					{
						case AUTOMATIC:
							aFN.setColor(SettingsHandler.getFeatAutoColor());
							break;
						case VIRTUAL:
							aFN.setColor(SettingsHandler.getFeatVirtualColor());
							break;
					}
				}

				aFN.setItem(ability);

				//Does anyone know why we don't call
				//aFN.setIsValid(aFeat.passesPreReqToGain()) here?
				if (qFilter == null
					|| (ability.getDisplayName().toLowerCase().indexOf(qFilter) >= 0 || ability
						.getType().toLowerCase().indexOf(qFilter) >= 0))
				{
					(root).addChild(aFN);
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

		buildSubTreePrereqTree(showAll, (PObjectNode) super.getRoot(),
			theAbilityList.get(currAbilityCat));
	}

	private void buildSubTreePrereqTree(final boolean showAll,
		final PObjectNode rootAsPObjectNode, final Collection<Ability> abilityList)
	{
		// This list initially contains all abilities that pass the filter.
		final List<Ability> fList = new ArrayList<Ability>();
		for (final Ability ability : abilityList)
		{
			if (showAll || theFilter == null
				|| theFilter.accept(theViewMode, ability))
			{
				fList.add(ability);
			}
		}

		// Loop through the list of abilities and find any that don't have a
		// PREABILITY tag.  These are added directly to the tree.
		final List<Ability> aList = new ArrayList<Ability>();
		for (int i = 0; i < fList.size(); ++i)
		{
			final Ability aFeat = fList.get(i);

			if (!aFeat.hasPreReqTypeOf("FEAT") //$NON-NLS-1$
				&& !aFeat.hasPreReqTypeOf("ABILITY"))  //$NON-NLS-1$
			{
				fList.remove(aFeat);
				aList.add(aFeat);
				--i; // to counter increment
			}
		}

		// Add these abilities to the tree
		if (rootAsPObjectNode == null)
		{
			return;
		}

		final PObjectNode[] directChildren = new PObjectNode[aList.size()];

		for (int i = 0; i < aList.size(); ++i)
		{
			directChildren[i] =
					createAbilityPObjectNode(rootAsPObjectNode, aList.get(i));
		}

		rootAsPObjectNode.setChildren(directChildren);

		// fList now contains only those abilities that have prereqs on other
		// abilities.
		final List<Ability> unmatchedList = new ArrayList<Ability>(fList);
		int loopmax = 6; // only go 6 levels...
		while ((fList.size() > 0) && (loopmax-- > 0))
		{
			for (int i = 0; i < fList.size(); ++i)
			{
				final Ability ability = fList.get(i);
				boolean placed = false;

				// Make a copy of the prereq
				// list so we don't destroy
				// the other prereqs
				List<Prerequisite> preReqList = new ArrayList<Prerequisite>();

				for (Prerequisite prereq : ability.getPrerequisiteList())
				{
					if (PrerequisiteUtilities.hasPreReqKindOf(prereq, "FEAT") //$NON-NLS-1$
						|| PrerequisiteUtilities.hasPreReqKindOf(prereq,
							"ABILITY")) //$NON-NLS-1$
					{
						preReqList.add(prereq);
					}
				}
				// Add ability in each location where it fits, we will tidy up duplicates later
				for (int j = 0; j < rootAsPObjectNode.getChildCount(); ++j)
				{
					final PObjectNode po = rootAsPObjectNode.getChild(j);

					placed |= placedThisFeatInThisTree(ability, po, preReqList);
				}

				if (placed)
				{
					unmatchedList.remove(ability);
				}
			}
		}
		pruneDuplicatesFromTree(rootAsPObjectNode);

		// These abilities have PREABILITY tags but we couldn't find a match
		// for the ability. e.g. PREABILITY:1,TYPE.Metamagic
		// Add them into the root like any other ability.
		if (unmatchedList.size() > 0)
		{
			final PObjectNode[] cc = new PObjectNode[unmatchedList.size()];

			for (int i = 0; i < unmatchedList.size(); ++i)
			{
				cc[i] = createAbilityPObjectNode(rootAsPObjectNode, unmatchedList.get(i));
				rootAsPObjectNode.addChild(cc[i], true);
			}
		}
	}

	/**
	 * Ensure that each ability only occurs at the deepest level in a 
	 * particular sub tree. So for Dodge/Mobility/Spring Attack, Spring Attack 
	 * should only occur under mobility, and not also under dodge. We had to 
	 * populate first and then remove to ensure that the ability is in all 
	 * appropriate locations.
	 * @param parent The parent node to have duplicates pruned from.
	 */
	private void pruneDuplicatesFromTree(PObjectNode parent)
	{
		if (parent == null || parent.getChildCount() == 0)
		{
			return;
		}

		List<PObjectNode> deletions = new ArrayList<PObjectNode>();
		for (PObjectNode child : parent.getChildren())
		{
			for (PObjectNode sibling : parent.getChildren())
			{
				if (sibling != child)
				{
					if (hasChild(sibling, child.getItem()))
					{
						deletions.add(child);
						break;
					}
				}
			}

			pruneDuplicatesFromTree(child);
		}
		for (PObjectNode objectNode : deletions)
		{
			parent.getChildren().remove(objectNode);
		}
	}

	/**
	 * Identify if a node has a child node with the specified item.
	 * @param node The node whose descendants will be scanned
	 * @param item The item we are looking for
	 * @return true if any descendant has the item, false if not.
	 */
	private boolean hasChild(PObjectNode node, Object item)
	{
		if (node == null || node.getChildCount() == 0)
		{
			return false;
		}
		
		for (PObjectNode child : node.getChildren())
		{
			if (item == child.getItem())
			{
				return true;
			}
			if (hasChild(child, item))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Create a PObjectNode for an ability
	 * @param parent The intended parent of the node.
	 * @param ability The ability the node will hold
	 * @return A new PObjectNode.
	 */
	private PObjectNode createAbilityPObjectNode(PObjectNode parent,
		final Ability ability)
	{
		final PObjectNode newNode = new PObjectNode();
		newNode.setItem(ability);
		newNode.setParent(parent);

		Nature nature = thePC.getAbilityNature(ability);
		if (nature != null)
		{
			switch (nature)
			{
			case AUTOMATIC:
				newNode.setColor(SettingsHandler.getFeatAutoColor());
				break;
			case VIRTUAL:
				newNode.setColor(SettingsHandler.getFeatVirtualColor());
				break;
			}
		}
		return newNode;
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
		if (typeRoot == null)
		{
			return;
		}
		setRoot(typeRoot);

		final PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
		if (rootAsPObjectNode == null)
		{
			return;
		}

		buildSubTreeTypeName(showAll, rootAsPObjectNode, theAbilityList
			.get(currAbilityCat));
	}

	private void buildSubTreeTypeName(final boolean showAll,
		final PObjectNode rootAsPObjectNode, final Collection<Ability> abilityList)
	{
		for (final Ability ability : abilityList)
		{
			if (showAll || theFilter == null
				|| theFilter.accept(theViewMode, ability))
			{
				for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
				{
					if (ability
						.isType(rootAsPObjectNode.getChild(i).toString()))
					{
						final PObjectNode aFN = new PObjectNode();

						Nature nature = thePC.getAbilityNature(ability);
						if (nature != null)
						{
							switch (nature)
							{
							case AUTOMATIC:
								aFN
										.setColor(SettingsHandler
												.getFeatAutoColor());
								break;
							case VIRTUAL:
								aFN.setColor(SettingsHandler
										.getFeatVirtualColor());
								break;
							}
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
		if (sourceRoot == null)
		{
			return;
		}
		setRoot(sourceRoot);

		final PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
		// TODO - This shouldn't really be required since I just set the damn
		// root node.
		if (rootAsPObjectNode == null)
		{
			return;
		}

		buildSubTreeSourceName(showAll, rootAsPObjectNode, theAbilityList
			.get(currAbilityCat));
	}

	private void buildSubTreeSourceName(final boolean showAll,
		final PObjectNode rootAsPObjectNode, final Collection<Ability> abilityList)
	{
		for (final Ability ability : abilityList)
		{
			if (showAll || theFilter == null
				|| theFilter.accept(theViewMode, ability))
			{
				String sourceString = SourceFormat.getFormattedString(ability,
						SourceFormat.MEDIUM, false);
				if (sourceString == null || sourceString.trim().length() == 0)
				{
					sourceString = "None";
				}
				//
				boolean found = false;
				for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
				{
					if (sourceString.equals(rootAsPObjectNode.getChild(i)
						.toString()))
					{
						found = true;
						final PObjectNode aFN =
								createAbilityPObjectNode(rootAsPObjectNode
									.getChild(i), ability);
						rootAsPObjectNode.getChild(i).addChild(aFN);
					}
				}
				if (!found)
				{
					final PObjectNode aFN =
						createAbilityPObjectNode(rootAsPObjectNode, ability);
					rootAsPObjectNode.addChild(aFN, true);
				}
			}
		}
	}

	private boolean placedThisFeatInThisTree(final Ability anAbility,
		final PObjectNode po, final List<Prerequisite> aList)
	{
		final Ability parentAbility = (Ability) po.getItem(); // must be a Feat
		boolean placed = false;

		for (final Prerequisite prereq : aList)
		{
			if ((PrerequisiteUtilities.hasPreReqMatching(prereq, "FEAT",
				parentAbility.getKeyName())
				|| PrerequisiteUtilities.hasPreReqMatching(prereq, "ABILITY",
					parentAbility.getKeyName())))
			{
				boolean alreadyPresent = anAbility.equals(parentAbility);
				if (po.getChildCount() > 0)
				{
					for (PObjectNode pnode : po.getChildren())
					{
						if (anAbility.equals(pnode.getItem()))
						{
							alreadyPresent = true;
							break;
						}
					} 
				}
				if (!alreadyPresent)
				{
					final PObjectNode p = createAbilityPObjectNode(po, anAbility);
					po.addChild(p);
	
					placed = true; // successfully added
				}
			}

			for (int i = 0; i < po.getChildCount(); ++i)
			{
				boolean j =
						placedThisFeatInThisTree(anAbility, po.getChild(i),
							aList);

				if (j)
				{
					placed = true;
				}
			}
		}
		return placed; // not here
	}

	public void setCurrentAbilityCategory(AbilityCategory newCat)
	{
		currAbilityCat = newCat;
		typeRoot = null;
		sourceRoot = null;
	}

	/**
	 * Set a new list of ability categories. Used in cases such as a game 
	 * mode or data change. 
	 * @param newCatList The new 
	 */
	public void setAbilityCategories(List<AbilityCategory> newCatList)
	{
		theCategoryList = newCatList;
		currAbilityCat = theCategoryList.get(0);
		typeRoot = null;
		sourceRoot = null;
	}
	
	/**
	 * Sets the ability list to use.
	 * 
	 * @param aList A list of Abilities to manage.
	 */
	public void setAbilityList(final Map<AbilityCategory,Collection<Ability>> aList, PlayerCharacter aPc)
	{
		theAbilityList = aList;
		resetModel(aPc, theViewMode, false);
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
	public void resetModel(final PlayerCharacter aPC,
		final AbilitySelectionPanel.ViewMode mode, final boolean showAll)
	{
		thePC = aPC;
		// We are going to build and cache the type and source tree roots.
		buildDefaultRoots();

		if (useCategoryRoot)
		{
			buildTreeCategory(showAll, mode);
		}
		else
		{
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
		}
		
		if (super.getRoot() != null)
		{
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		}
	}

	/**
	 * The plan:
	 *   Allow the model to cope with either a list of ability categories
	 *   or a single category. We do this by having a category root for 
	 *   the list and splitting the buildXXXTree methods into two, one to 
	 *   set the root, and one to populate a node based on a category. The
	 *   single category can then call the set root and then call the 
	 *   populate method, while the multiple one can iterate through 
	 *   categories on the category root and call the appropriate populate 
	 *   method for the category with the appropriate root.
	 *    
	 * @param showAll Force displaying of all abilities
	 * @param mode The display format for each category.
	 */
	private void buildTreeCategory(boolean showAll, final AbilitySelectionPanel.ViewMode mode)
	{
		if (categoryRoot == null)
		{
			return;
		}
		setRoot(categoryRoot);

		final PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

		// Loop over the category nodes, adding content to each
		for (final PObjectNode catNode : rootAsPObjectNode.getChildren())
		{
			PCAbilityCategory pcCat = (PCAbilityCategory) catNode.getItem();
			Collection<Ability> abilities = theAbilityList.get(pcCat.getCategory());
			if (abilities == null)
			{
				abilities = new ArrayList<Ability>();
			}
			  // Pass in specific abilityList
			switch (mode)
			{
				case TYPENAME:
					addTypeNodes(catNode, abilities);
					buildSubTreeTypeName(showAll, catNode, abilities);

					break;

				case NAMEONLY:
					buildSubTreeNameOnly(showAll, catNode, abilities);

					break;

				case PREREQTREE:
					buildSubTreePrereqTree(showAll, catNode, abilities);

					break;

				case SOURCENAME:
					addSourceNodes(catNode, abilities);
					buildSubTreeSourceName(showAll, catNode, abilities);

					break;
			}
		}
	}

	/**
	 * @see pcgen.gui.TableColumnManagerModel#getMColumnList()
	 */
	public List<String> getMColumnList()
	{
		final List<String> retList = new ArrayList<String>();

		final Column[] columns = Column.values();
		for (int i = 1; i < columns.length; i++)
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

	private void setColumnOption(final Column col, final String anOption,
		final String val)
	{
		SettingsHandler.setPCGenOption(theOptionsRoot
			+ "." + anOption + "." + col.toString(), val); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private int getColumnOption(final Column aCol, final String anOption,
		final int aDefault)
	{
		return SettingsHandler.getPCGenOption(theOptionsRoot
			+ "." + anOption + "." + aCol.toString(), aDefault); //$NON-NLS-1$ //$NON-NLS-2$
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
		return getColumnOption(Column.values()[col],
			"sizecol", Column.values()[col].getWidth()); //$NON-NLS-1$
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
		switch (column)
		{
			case COST:
			case MULTIPLES:
			case STACKS:
				tColumn
					.setCellRenderer(new pcgen.gui.utils.JTableEx.AlignCellRenderer(
						SwingConstants.CENTER));
				break;

			default:
				break;
		}
	}

	/**
	 * An enum for the Columns in the table.
	 */
	public enum Column
	{
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

		Column(final String aName, final int aDefaultWidth,
			final boolean visible)
		{
			theName = LanguageBundle.getString(aName);
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
