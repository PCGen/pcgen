/*
 * SpellModel.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 10-Dec-2004
 */
package pcgen.gui.tabs.spells;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.DescriptionFormatting;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.analysis.SpellPoint;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.gui.GuiConstants;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 *  The TreeTableModel has a single <code>root</code> node
 *  This root node has a null <code>parent</code>.
 *  All other nodes have a parent which points to a non-null node.
 *  Parent nodes contain a list of  <code>children</code>, which
 *  are all the nodes that point to it as their parent.
 *  <code>nodes</code> which have 0 children are leafs (the end of
 *  that linked list).  nodes which have at least 1 child are not leafs
 *  Leafs are like files and non-leafs are like directories.
 *  The leafs contain an Object that we want to know about (Spells)
 **/
public final class SpellModel extends AbstractTreeTableModel implements
		TableColumnManagerModel
{
	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the SpellModel class
	private static final int COL_NAME = 0;
	private static final int COL_SCHOOL = 1;
	private static final int COL_SUBSCHOOL = 2;
	private static final int COL_DESCRIPTOR = 3;
	private static final int COL_COMPONENT = 4;
	private static final int COL_CASTTIME = 5;
	private static final int COL_RANGE = 6;
	private static final int COL_DESCRIPTION = 7;
	private static final int COL_TARGET = 8;
	private static final int COL_DURATION = 9;
	private static final int COL_SAVE = 10;
	private static final int COL_SR = 11;
	private static final int COL_SRC = 12;
	private static final int COL_PPCOST = 13;
	private static final int COL_SPCOST = 14;

	private final int[] colDefaultWidth =
			{200, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
				100, 100};

	// there are two roots. One for available spells
	// and one for selected spells (spellbooks)
	private PObjectNode theRoot;

	// list of columns names
	private String[] colNameList = {""}; //$NON-NLS-1$

	private int[] colTranslateList = {0};
	private List<Boolean> displayList;

	// Types of the columns.
	private boolean includeRace = false;
	private PlayerCharacter pc;
	private boolean available = false;

	/**
	 * Creates a SpellModel for a particular character.
	 *
	 * @param primaryMode The primary sort order
	 * @param secondaryMode The secondary sort order
	 * @param available Is this an available (true) or selected (false) list
	 * @param bookList The list of books to be displayed.
	 * @param currSpellBook The name of the currently selected spell book
	 * @param fullSpellList Should we display a full list of available spells?
	 * @param pc The character we are building the spell list for.
	 * @param spellTab The tab the list is being displayed upon.
	 * @param emptyMessage The message to be displayed if the model is empty
	 */
	public SpellModel(int primaryMode, int secondaryMode, boolean available,
		List<String> bookList, String currSpellBook, int spellListType,
		PlayerCharacter pc, InfoSpellsSubTab spellTab, String emptyMessage)
	{
		super(null);
		this.available = available;

		setCharacter(pc);

		//
		// if you change/add/remove entries to nameList
		// you also need to change the static COL_XXX defines
		// at the begining of this file
		//

		if (Globals.hasSpellPPCost())
		{
			colTranslateList =
					new int[]{COL_NAME, COL_SCHOOL, COL_SUBSCHOOL,
						COL_DESCRIPTOR, COL_PPCOST, COL_COMPONENT,
						COL_CASTTIME, COL_RANGE, COL_DESCRIPTION, COL_TARGET,
						COL_DURATION, COL_SAVE, COL_SR, COL_SRC};
		}
		else if(Spell.hasSpellPointCost())
		{
			colTranslateList =
				new int[]{COL_NAME, COL_SCHOOL, COL_SUBSCHOOL,
					COL_DESCRIPTOR, COL_SPCOST, COL_COMPONENT,
					COL_CASTTIME, COL_RANGE, COL_DESCRIPTION, COL_TARGET,
					COL_DURATION, COL_SAVE, COL_SR, COL_SRC};
		}
		else
		{
			colTranslateList =
					new int[]{COL_NAME, COL_SCHOOL, COL_SUBSCHOOL,
						COL_DESCRIPTOR, COL_COMPONENT, COL_CASTTIME, COL_RANGE,
						COL_DESCRIPTION, COL_TARGET, COL_DURATION, COL_SAVE,
						COL_SR, COL_SRC};
		}

		colNameList = makeHeaderList(colTranslateList);
		displayList = makeDisplayList(available);

		resetModel(primaryMode, secondaryMode, available, bookList,
			currSpellBook, spellListType, spellTab, emptyMessage);
	}

	/**
	 * Build the list of column names for the list of column ids.
	 * @param transList The list of column ids.
	 * @return The list of column names.
	 */
	private String[] makeHeaderList(int[] transList)
	{
		String[] aList = new String[transList.length];
		String aString;
		for (int i = 0; i < transList.length; ++i)
		{
			switch (transList[i])
			{
				case COL_NAME:
					aString = PropertyFactory.getString("SpellModel.Name"); //$NON-NLS-1$
					break;

				case COL_SCHOOL:
					aString = PropertyFactory.getString("SpellModel.Scool"); //$NON-NLS-1$
					break;

				case COL_SUBSCHOOL:
					aString = PropertyFactory.getString("SpellModel.SubSchool"); //$NON-NLS-1$
					break;

				case COL_DESCRIPTOR:
					aString =
							PropertyFactory.getString("SpellModel.Descriptor"); //$NON-NLS-1$
					break;

				case COL_COMPONENT:
					aString =
							PropertyFactory.getString("SpellModel.Components"); //$NON-NLS-1$
					break;

				case COL_CASTTIME:
					aString =
							PropertyFactory.getString("SpellModel.CastingTime"); //$NON-NLS-1$
					break;

				case COL_RANGE:
					aString = PropertyFactory.getString("SpellModel.Range"); //$NON-NLS-1$
					break;

				case COL_DESCRIPTION:
					aString =
							PropertyFactory.getString("SpellModel.Description"); //$NON-NLS-1$
					break;

				case COL_TARGET:
					aString =
							PropertyFactory.getString("SpellModel.TargetArea"); //$NON-NLS-1$
					break;

				case COL_DURATION:
					aString = PropertyFactory.getString("SpellModel.Duration"); //$NON-NLS-1$
					break;

				case COL_SAVE:
					aString = PropertyFactory.getString("SpellModel.SaveInfo"); //$NON-NLS-1$
					break;

				case COL_SR:
					aString = PropertyFactory.getString("SpellModel.SR"); //$NON-NLS-1$
					break;

				case COL_SRC:
					aString =
							PropertyFactory.getString("SpellModel.SourceFile"); //$NON-NLS-1$
					break;

				case COL_PPCOST:
					aString = PropertyFactory.getString("SpellModel.PPCost"); //$NON-NLS-1$
					break;

				case COL_SPCOST:
					aString = PropertyFactory.getString("SpellModel.SpellPointCost"); //$NON-NLS-1$
					break;
					
				default:
					aString = Integer.toString(transList[i]);
					break;
			}
			aList[i] = aString;
		}
		return aList;
	}

	private List<Boolean> makeDisplayList(boolean available)
	{
		List<Boolean> retList = new ArrayList<Boolean>();
		retList.add(Boolean.TRUE);
		if (available)
		{
			int i = 1;
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SCHOOL
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SUBSCHOOL
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_DESCRIPTOR
			if (Globals.hasSpellPPCost())
			{
				retList.add(Boolean.valueOf(getColumnViewOption(
					colNameList[i++], true))); //COL_PPCOST
			}
			else if (Spell.hasSpellPointCost())
			{
				retList.add(Boolean.valueOf(getColumnViewOption(
					colNameList[i++], true))); //COL_SPCOST
			}
			
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_COMPONENT
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_CASTTIME
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_RANGE
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_DESCRIPTION
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_TARGET
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_DURATION
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SAVE
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SR
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SRC
		}
		else
		{
			int i = 1;
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				true))); //COL_SCHOOL
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				true))); //COL_SUBSCHOOL
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				true))); //COL_DESCRIPTOR
			if (Globals.hasSpellPPCost())
			{
				retList.add(Boolean.valueOf(getColumnViewOption(
					colNameList[i++], true))); //COL_PPCOST
			}
			else if (Spell.hasSpellPointCost())
			{
				retList.add(Boolean.valueOf(getColumnViewOption(
					colNameList[i++], true))); //COL_SPCOST
			}
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_COMPONENT
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_CASTTIME
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_RANGE
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_DESCRIPTION
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_TARGET
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_DURATION
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SAVE
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				false))); //COL_SR
			retList.add(Boolean.valueOf(getColumnViewOption(colNameList[i++],
				true))); //COL_SRC
		}

		return retList;
	}

	/**
	 * Translate a column index into a column id.
	 * @param column The column index
	 * @return The column id.
	 */
	private int translateColumn(final int column)
	{
		return colTranslateList[column];
	}

	/**
	 * Returns boolean if can edit a cell. (SpellModel)
	 * @param node The model node being checked
	 * @param column The index of the column to be checked.
	 * @return true if cell editable
	 **/
	public boolean isCellEditable(Object node, int column)
	{
		column = translateColumn(column);
		return (column == COL_NAME);
	}

	/**
	 * Returns Class for the column. (SpellModel)
	 * @param column The index of the column.
	 * @return Class
	 **/
	public Class<?> getColumnClass(int column)
	{
		column = translateColumn(column);
		return (column == COL_NAME) ? TreeTableModel.class : String.class;
	}

	/* The JTreeTableNode interface. */

	/**
	 * Returns int number of columns. (SpellModel)
	 * @return column count
	 **/
	public int getColumnCount()
	{
		return colNameList.length;
	}

	/**
	 * Returns String name of a column. (SpellModel)
	 * @param column The index of the column.
	 * @return colmun name
	 **/
	public String getColumnName(int column)
	{
		return colNameList[column];
	}

	/**
	 * return the root node
	 * @return Object
	 */
	public Object getRoot()
	{
		return super.getRoot();
	}

	/**
	 * Returns Object value of the column. (SpellModel)
	 * @param node The model node
	 * @param column The index of the column.
	 * @return value
	 **/
	public Object getValueAt(Object node, int column)
	{
		final PObjectNode fn = (PObjectNode) node;
		Spell aSpell = null;
		CharacterSpell spellA = null;

		if (fn == null)
		{
			Logging.errorPrint(PropertyFactory
				.getString("SpellModel.NoActiveNode")); //$NON-NLS-1$

			return null;
		}

		if (fn.getItem() instanceof SpellInfo)
		{
			spellA = ((SpellInfo) fn.getItem()).getOwner();
			aSpell = spellA.getSpell();
			((SpellInfo) fn.getItem()).getActualLevel();
		}

		column = translateColumn(column);

		switch (column)
		{
			case COL_NAME:
				if (SettingsHandler.guiUsesOutputNameSpells())
				{
					return (aSpell != null) ? OutputNameFormatting.getOutputName(aSpell) : fn
						.toString();
				}
				else
				{
					return fn.toString();
				}

			case COL_SCHOOL:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.SPELL_SCHOOL) : null;

			case COL_SUBSCHOOL:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL) : null;

			case COL_DESCRIPTOR:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.SPELL_DESCRIPTOR) : null;

			case COL_COMPONENT:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.COMPONENTS) : null;

			case COL_CASTTIME:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.CASTTIME) : null;

			case COL_RANGE:
				return (aSpell != null) ? StringUtil.joinToStringBuffer(aSpell.getListFor(ListKey.RANGE), ", ") : null;

			case COL_DESCRIPTION:

				if ((aSpell != null) && (spellA != null))
				{
					return pc.parseSpellString(spellA, pc
						.getDescription(aSpell));
				}

				return (aSpell != null) ? DescriptionFormatting.piDescString(pc, aSpell) : null;

			case COL_TARGET:

				if ((aSpell != null) && (spellA != null))
				{
					return pc.parseSpellString(spellA, aSpell.getSafe(StringKey.TARGET_AREA));
				}

				return (aSpell != null) ? aSpell.getSafe(StringKey.TARGET_AREA) : null;

			case COL_DURATION:

				if ((aSpell != null) && (spellA != null))
				{
					return pc.parseSpellString(spellA, aSpell.getListAsString(ListKey.DURATION));
				}

				return (aSpell != null) ? aSpell.getListAsString(ListKey.DURATION) : null;

			case COL_SAVE:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.SAVE_INFO) : null;

			case COL_SR:
				return (aSpell != null) ? aSpell.getListAsString(ListKey.SPELL_RESISTANCE) : null;

			case COL_SRC:
				return (aSpell != null) ? SourceFormat.getFormattedString(aSpell,
				Globals.getSourceDisplay(), true)
					: null;

			case COL_SPCOST:
				return (spellA != null) ? SpellPoint.getSpellPointCostActual(pc, aSpell): null;

			case COL_PPCOST:
				return (spellA != null) ? aSpell.getSafe(IntegerKey.PP_COST): null; 
				
			default:
				return fn.getItem();
		}

		// return null;
	}

	/**
	 * There must be a root node, but we keep it hidden
	 * @param aNode
	 */
	private void setRoot(PObjectNode aNode)
	{
		super.setRoot(aNode);
	}

	/**
	 * this method looks for any domains associated with this class
	 * and finds any spells associated with this domain
	 * and then adds this list to a "Domains" directory
	 * to keep the Domain list in an easily distinguished place
	 * @param className
	 * @param theParent
	 * @param iLev
	 **/
	private void addDomainSpellsForClass(PCClass aClass, PObjectNode theParent,
		int iLev)
	{
		if (!pc.hasDomains())
		{
			return;
		}

		PObjectNode p = new PObjectNode();
		p.setItem("Domains"); //$NON-NLS-1$

		boolean dom = false;

		for (Domain aDom : pc.getDomainSet())
		{
			// if any domains have this class as a source
			// and is a valid domain, add them
			if (aClass.getKeyName().equals(
					pc.getDomainSource(aDom).getPcclass().getKeyName()))
			{
				List<Spell> domainSpells = Globals.getSpellsIn(iLev,
						Collections.singletonList(aDom
								.get(ObjectKey.DOMAIN_SPELLLIST)), pc);
				p.setParent(theParent);

				if (!dom)
				{
					theParent.addChild(p);
				}

				dom = true;
				setNodeSpells(domainSpells, p, iLev, aDom, Globals
					.getDefaultSpellBook(), pc);
			}
		}
	}

	/**
	 * This function takes a branch and adds the spells to it.
	 * @param charSpells
	 * @param tNode
	 * @param iLev
	 * @param obj
	 * @param book
	 * @param pc
	 */
	private void setNodeSpells(List<?> charSpells, PObjectNode tNode, int iLev,
		PObject obj, String book, PlayerCharacter pc)
	{
		for (Object o : charSpells)
		{
			PObjectNode fCN;

			if (o instanceof CharacterSpell)
			{
				final CharacterSpell cs = (CharacterSpell) o;
				final SpellInfo si = cs.getSpellInfoFor(book, iLev);

				if (si == null)
				{
					continue;
				}

				fCN = new PObjectNode();
				fCN.setItem(si);
			}
			else
			{
				Spell aSpell = (Spell) o;

				if (!SpellLevel.levelForKeyContains(aSpell, pc.getSpellLists(obj), iLev, pc))
				{
					continue;
				}

				CharacterSpell cs = new CharacterSpell(obj, aSpell);
				SpellInfo si = cs.addInfo(iLev, 1, book);
				fCN = new PObjectNode();
				fCN.setItem(si);
			}

			fCN.setParent(tNode);
			tNode.addChild(fCN);
		}
		// end spells loop
	}

	/**
	 * This assumes the SpellModel exists but
	 * needs branches and nodes to be repopulated
	 *
	 * @param primaryMode The primary sort order
	 * @param secondaryMode The secondary sort order
	 * @param available Is this an available (true) or selected (false) list
	 * @param bookList The list of books to be displayed.
	 * @param currSpellBook The name of the currently selected spell book
	 * @param spellListType Should we display known only/all available spells or all spells?
	 * @param spellTab The tab the list is being displayed upon.
	 * @param emptyMessage The message to be displayed if the model is empty
	 */
	public void resetModel(int primaryMode, int secondaryMode,
		boolean available, List<String> bookList, String currSpellBook,
		int spellListType, InfoSpellsSubTab spellTab, String emptyMessage)
	{
		List<PObject> classList = new ArrayList<PObject>();
		List<Object> spellList = new ArrayList<Object>();
		includeRace = false;

		PObjectNode[] primaryNodes = null;
		PObjectNode[] secondaryNodes = null;
		PObjectNode[] bookNodes = null;

		theRoot = new PObjectNode();
		setRoot(theRoot);

		if (pc == null)
		{
			return;
		}

		boolean knownSpellsOnly =
				spellListType == GuiConstants.INFOSPELLS_AVAIL_KNOWN;

		if (knownSpellsOnly)
		{
			bookNodes = new PObjectNode[bookList.size()];
			int ix = 0;
			for (String bookName : bookList)
			{
				bookNodes[ix] = new PObjectNode();
				if (pc != null)
				{
					bookNodes[ix].setItem(pc.getSpellBookByName(bookName));
				}
				else
				{
					bookNodes[ix].setItem(bookName);
				}
				bookNodes[ix].setParent(theRoot);
				for (CharacterSpell charSpell : pc.getCharacterSpells(pc.getRace(), bookName))
				{
					if (spellTab.shouldDisplayThis(charSpell.getSpell()))
					{
						spellList.add(charSpell);
					}
				}
				ix++;
			}
			theRoot.setChildren(bookNodes);
		}

		includeRace = !spellList.isEmpty();

		getSpellcastingClasses(spellListType, classList, spellList, spellTab);

		if (includeRace)
		{
			classList.add(pc.getRace());
		}
		//TODO: Add any extra classes that already have spells present

		// the structure will be
		// root
		//   (book names) bookNodes (only for right-side tab, the knownSpellsOnly e.g. selected spells)
		//     primary nodes  (the first "sort by" selection)
		//       secondary nodes (the second "sort by" selection)
		// the first time (e.g. firstPass==true) through the loop, make sure all nodes are created and attached
		boolean firstPass = true;
		HashMap<String, List<SpellInfo>> usedMap =
				new HashMap<String, List<SpellInfo>>();
		String mapKey;

		for (Object sp : spellList)
		{
			Spell spell = null;
			CharacterSpell cs = null;

			if (sp instanceof CharacterSpell)
			{
				cs = (CharacterSpell) sp;
				spell = cs.getSpell();
				if (!knownSpellsOnly && cs.getOwner() instanceof Domain)
				{
					// domain spells elsewhere
					continue;
				}
			}
			if (sp instanceof Spell)
			{
				spell = (Spell) sp;
			}

			// for each spellbook, ignored for "fullSpellList" left-side of tab
			// the <= bookList.size() is intended, so it will be processed once
			// when ix==0 for the knownSpellsOnly model
			for (int ix = 0; ix <= bookList.size(); ix++)
			{
				if (knownSpellsOnly && ix == bookList.size())
					break;
				if (!knownSpellsOnly && ix > 0)
				{
					break;
				}
				// default currently selected spellbook
				String bookName = currSpellBook;
				if (knownSpellsOnly)
				{
					bookName = bookList.get(ix).toString();
				}
				if (firstPass)
				{
					primaryNodes = getNodesByMode(primaryMode, classList);
				}
				else if (knownSpellsOnly)
				{
					// get the primaryNodes, which are the specified bookNode's children
					bookNodes[ix].getChildren().toArray(primaryNodes);
				}
				else
				{
					// get the primaryNodes, which are the root's children
					theRoot.getChildren().toArray(primaryNodes);
				}

				for (int pindex = 0; pindex < primaryNodes.length; pindex++)
				{
					if (sp instanceof Spell)
						cs = null;
					boolean primaryMatch = false; // spell match's primaryNode's criteria
					boolean spellMatch = false; // spell match's primaryNode and secondaryNode criteria
					SpellInfo si = null;
					PObject aClass = null;
					int iLev = -1;

					PObjectNode primaryNode = primaryNodes[pindex];
					switch (primaryMode)
					{
						case GuiConstants.INFOSPELLS_VIEW_CLASS: // By Class
							aClass = classList.get(pindex);
							primaryMatch =
									SpellLevel.levelForKeyContains(spell, pc
									.getSpellLists(aClass), iLev, pc);
							if (cs != null)
							{
								if (aClass instanceof Race)
								{
									primaryMatch =
											(cs.getOwner().getKeyName()
												.equals(aClass.getKeyName()));
								}
								else if (cs.getOwner() instanceof Domain)
								{
									primaryMatch = pc.containsCharacterSpell(
										aClass, cs);
								}
								else
								{
									primaryMatch =
											(cs.getOwner().getKeyName()
												.equals(aClass.getKeyName()));
								}
							}
							break;
						case GuiConstants.INFOSPELLS_VIEW_LEVEL: // By Level
							iLev = pindex;
							primaryMatch = true;
							si = null;
							if (primaryMatch && cs != null)
							{
								si = cs.getSpellInfoFor(bookName, iLev);
							}
							if (si == null)
							{
								primaryMatch = SpellLevel.isLevel(spell, iLev, pc);
							}
							else if (!knownSpellsOnly && si.getFeatList() != null)
							{
								continue;
							}
							break;
						case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR: // By Descriptor
							primaryMatch =
									spell.containsInList(ListKey.SPELL_DESCRIPTOR,
										primaryNode.toString());
							break;
						case GuiConstants.INFOSPELLS_VIEW_RANGE: // By Range
							primaryMatch =
									spell.getListAsString(ListKey.RANGE).equals(
										primaryNode.toString());
							break;
						case GuiConstants.INFOSPELLS_VIEW_DURATION: // By Duration
							primaryMatch =
									spell.getListAsString(ListKey.DURATION).equals(
										primaryNode.toString());
							break;
						case GuiConstants.INFOSPELLS_VIEW_TYPE: // By Type
							primaryMatch = spell.isType(primaryNode.toString());
							break;
						case GuiConstants.INFOSPELLS_VIEW_SCHOOL: // By Type
							SpellSchool ss =
								Globals.getContext().ref
									.silentlyGetConstructedCDOMObject(
										SpellSchool.class, primaryNode.toString());
							primaryMatch = (ss != null) &&
									spell.containsInList(ListKey.SPELL_SCHOOL, ss);
							break;
					}

					if (secondaryMode == GuiConstants.INFOSPELLS_VIEW_NOTHING)
					{
						if (!firstPass && !primaryMatch)
							continue;
						secondaryNodes = new PObjectNode[1];
						secondaryNodes[0] = new PObjectNode();
						secondaryNodes[0].setItem(""); //$NON-NLS-1$
					}
					else if (firstPass)
					{
						secondaryNodes =
								getNodesByMode(secondaryMode, classList);
					}
					else
					{
						if (!primaryMatch)
						{
							continue;
						}
						primaryNode.getChildren().toArray(secondaryNodes);
					}

					for (int sindex = 0; sindex < secondaryNodes.length; sindex++)
					{
						mapKey =
								bookName
									+ "." + primaryNode.toString() + "." + secondaryNodes[sindex].toString(); //$NON-NLS-1$ //$NON-NLS-2$
						switch (secondaryMode)
						{
							case GuiConstants.INFOSPELLS_VIEW_CLASS: // By Class
								aClass = classList.get(sindex);
								spellMatch =
										primaryMatch
											&& (SpellLevel.getFirstLevelForKey(spell, pc.getSpellLists(aClass), pc) >= 0);
								break;
							case GuiConstants.INFOSPELLS_VIEW_LEVEL: // By Level
								iLev = sindex;
								spellMatch = false;
								si = null;
								if (primaryMatch)
								{
									if (cs != null)
									{
										si = cs.getSpellInfoFor(bookName, iLev);
										spellMatch = si != null;
									}
									if (si == null)
									{
										if (aClass != null)
										{
											spellMatch =
													SpellLevel.levelForKeyContains(spell, pc.getSpellLists(aClass), iLev, pc);
										}
										else
										{
											spellMatch =
													SpellLevel.isLevel(spell, iLev, pc);
										}
									}
								}
								if (!knownSpellsOnly && si != null
									&& si.getFeatList() != null)
								{
									continue;
								}
								break;
							case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR: // By Descriptor
								spellMatch =
										primaryMatch
											&& spell.containsInList(ListKey.SPELL_DESCRIPTOR,
													secondaryNodes[sindex].toString());
								break;
							case GuiConstants.INFOSPELLS_VIEW_RANGE: // By Range
								spellMatch =
										primaryMatch
											&& spell.getListAsString(ListKey.RANGE).equals(
												secondaryNodes[sindex]
													.toString());
								break;
							case GuiConstants.INFOSPELLS_VIEW_DURATION: // By Duration
								spellMatch =
										primaryMatch
											&& spell.getListAsString(ListKey.DURATION).equals(
												secondaryNodes[sindex]
													.toString());
								break;
							case GuiConstants.INFOSPELLS_VIEW_TYPE: // By Type
								spellMatch =
										primaryMatch
											&& spell
												.isType(secondaryNodes[sindex]
													.toString());
								break;
							case GuiConstants.INFOSPELLS_VIEW_NOTHING: // No secondary criteria
								spellMatch = primaryMatch;
								break;
							case GuiConstants.INFOSPELLS_VIEW_SCHOOL: // By Type
								SpellSchool ss =
									Globals.getContext().ref
										.silentlyGetConstructedCDOMObject(
											SpellSchool.class, secondaryNodes[sindex].toString());
								spellMatch =
										primaryMatch && (ss != null)
											&& spell.containsInList(ListKey.SPELL_SCHOOL, ss);
								break;
						}
						if (firstPass
							&& secondaryMode != GuiConstants.INFOSPELLS_VIEW_NOTHING)
						{
							secondaryNodes[sindex].setParent(primaryNode);
							if (!knownSpellsOnly && aClass != null && iLev > -1
								&& (aClass instanceof PCClass))
							{
								addDomainSpellsForClass((PCClass) aClass,
										secondaryNodes[sindex], iLev);
							}
						}

						// Make sure we have
						// the right spellbook
						if ((si != null) && !si.getBook().equals(bookName))
						{
							continue;
						}

						if (knownSpellsOnly && (si != null))
						{
							List<SpellInfo> aList = usedMap.get(mapKey);
							if (aList != null && aList.contains(si))
							{
								continue;
							}
						}
						if (aClass != null && iLev > -1 && spellMatch
							&& (aClass instanceof PCClass))
						{
							int theLevel = iLev;
							if (si != null)
								theLevel = -1;
							CDOMObject theObject = aClass;
							if (cs != null)
								theObject = cs.getOwner();
							spellMatch =
									SpellLevel.levelForKeyContains(spell, 
									pc.getSpellLists(theObject), theLevel, pc);
						}
						if (spellMatch && si == null && !knownSpellsOnly)
						{
							PObject bClass = aClass;
							// if there's only 1 class, then use that to determine which spells are qualified
							if (aClass == null && classList.size() == 1)
								bClass = classList.get(0);
							cs = new CharacterSpell(bClass, spell);
							si = cs.addInfo(iLev, 1, bookName);
						}
						// didn't find a match, so try the next node
						if (!spellMatch || si == null)
						{
							continue;
						}

						// Everything looks ok
						// so add this spell
						PObjectNode thisNode = secondaryNodes[sindex];
						if (secondaryMode == GuiConstants.INFOSPELLS_VIEW_NOTHING)
						{
							thisNode = primaryNode;
						}
						addSpellToNode(si, thisNode, usedMap, mapKey);
					}
					if (secondaryMode != GuiConstants.INFOSPELLS_VIEW_NOTHING)
						primaryNode.setChildren(secondaryNodes);
					if (!knownSpellsOnly)
					{
						primaryNode.setParent(theRoot);
					}
					else
					{
						primaryNode.setParent(bookNodes[ix]);
					}
				} // end primaryNodes
				if (knownSpellsOnly)
				{
					bookNodes[ix].setChildren(primaryNodes);
				}
				else
				{
					theRoot.setChildren(primaryNodes);
				}
			} // end bookNodes
			firstPass = false;
		} // end spell list

		PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
		if (rootAsPObjectNode.getChildCount() > 0)
		{
			rootAsPObjectNode.pruneEmpty();
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		}
		else
		{
			PObjectNode node = new PObjectNode();
			node.setDisplayName(emptyMessage);
			PObject obj = new PObject();
			obj.setName(emptyMessage);
			node.setItem(obj);
			node.setParent(rootAsPObjectNode);
			rootAsPObjectNode.addChild(node);
		}
	}

	/**
	 * Add the spell to the node and to the map of spell assignments.
	 *  
	 * @param si The spell to be added.
	 * @param parentNode The node to add the spell to.
	 * @param usedMap The map of spell assignments
	 * @param mapKey The key of the node the spell is being added to.
	 */
	private void addSpellToNode(SpellInfo si, PObjectNode parentNode,
		HashMap<String, List<SpellInfo>> usedMap, String mapKey)
	{
		// Add the spell to the node
		PObjectNode spellNode = new PObjectNode();
		spellNode.setItem(si);
		spellNode.setParent(parentNode);
		parentNode.addChild(spellNode);

		// Add it to the map of spells
		List<SpellInfo> aList = usedMap.get(mapKey);
		if (aList == null)
		{
			aList = new ArrayList<SpellInfo>();
			usedMap.put(mapKey, aList);
		}
		aList.add(si);
	}

	/**
	 * @param spellListType
	 * @param classList
	 * @param spellList
	 */
	private void getSpellcastingClasses(int spellListType,
		List<PObject> classList, List<Object> spellList,
		InfoSpellsSubTab spellTab)
	{
		// get the list of spell casting Classes
		Collection<PCClass> classes;
		if (spellListType == GuiConstants.INFOSPELLS_AVAIL_ALL_SPELL_LISTS)
		{
			classes = Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class);
		}
		else
		{
			classes = pc.getClassSet();
		}

		for (PCClass aClass : classes)
		{
			if (aClass.get(StringKey.SPELLTYPE) != null)
			{
				if (pc.getSpellSupport(aClass).zeroCastSpells() && !pc.getSpellSupport(aClass).hasKnownList())
				{
					continue;
				}

				classList.add(aClass);

				//if (fullSpellList && currSpellBook.equals(Globals.getDefaultSpellBook()))
				if (spellListType != GuiConstants.INFOSPELLS_AVAIL_KNOWN)
				{
					for (Spell s : Globals.getSpellsIn(-1,
						pc.getSpellLists(aClass), pc)) //$NON-NLS-1$
					{
						if (!spellList.contains(s)
							&& spellTab.shouldDisplayThis(s))
						{
							spellList.add(s);
						}
					}
				}
				/*
				 else if (fullSpellList)
				 {
				 spellList.addAll(aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), -1));
				 }
				 */
				else
				{
					Collection<? extends CharacterSpell> sp = pc.getCharacterSpells(aClass);
					List<CharacterSpell> cSpells = new ArrayList<CharacterSpell>(sp);
					// Add in the spells granted by objects
					SpellLevel.addBonusKnowSpellsToList(pc, aClass, cSpells);
					
					spellList.addAll(cSpells);
					for (Object tempSpell : cSpells)
					{
						Spell spell;
						if (tempSpell instanceof CharacterSpell)
						{
							spell = ((CharacterSpell) tempSpell).getSpell();
						}
						else
						{
							spell = (Spell) tempSpell;
						}
						if (spellTab.shouldDisplayThis(spell))
						{
							spellList.add(spell);
						}

					}
				}
			}
		}
	}

	/**
	 * @param primaryMode
	 * @param classList
	 * @return PObjectNode[]
	 */
	private PObjectNode[] getNodesByMode(int primaryMode,
		List<PObject> classList)
	{
		PObjectNode[] primaryNodes = null;
		switch (primaryMode)
		{
			case GuiConstants.INFOSPELLS_VIEW_CLASS: // By Class
				primaryNodes = getClassNameNodes(classList);
				break;
			case GuiConstants.INFOSPELLS_VIEW_LEVEL: // By Level
				primaryNodes = getLevelNodes();
				break;
			case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR: // By Descriptor
				primaryNodes = getDescriptorNodes();
				break;
			case GuiConstants.INFOSPELLS_VIEW_RANGE: // By Range
				primaryNodes = getRangeNodes();
				break;
			case GuiConstants.INFOSPELLS_VIEW_DURATION: // By Duration
				primaryNodes = getDurationNodes();
				break;
			case GuiConstants.INFOSPELLS_VIEW_TYPE: // By Type
				primaryNodes = getTypeNodes();
				break;
			case GuiConstants.INFOSPELLS_VIEW_SCHOOL: // By School
				primaryNodes = getSchoolNodes();
				break;
		}
		return primaryNodes;
	}

	private PObjectNode[] getClassNameNodes(List<PObject> classList)
	{
		PObjectNode[] tempNodes = new PObjectNode[classList.size()];
		for (int ix = 0; ix < classList.size(); ++ix)
		{
			PObject obj = classList.get(ix);
			String objName = OutputNameFormatting.piString(obj, true);
			tempNodes[ix] = new PObjectNode();
			tempNodes[ix].setItem(objName);
		}
		return tempNodes;
	}

	private PObjectNode[] getLevelNodes()
	{
		PObjectNode[] tempNodes = new PObjectNode[Constants.MAX_SPELL_LEVEL+1];
		for (int ix = 0; ix <= Constants.MAX_SPELL_LEVEL; ++ix)
		{
			tempNodes[ix] = new PObjectNode();
			String ix2 = "" + ix; //$NON-NLS-1$
			if (ix < 10)
			{
				ix2 = " " + ix2; //$NON-NLS-1$
			}
			tempNodes[ix]
				.setItem(PropertyFactory.getString("SpellModel.24") + ix2); //$NON-NLS-1$
		}
		return tempNodes;
	}

	private PObjectNode[] getDescriptorNodes()
	{
		PObjectNode[] tempNodes =
				new PObjectNode[Globals.getDescriptorSet().size()];
		int ix = 0;
		for (String s : Globals.getDescriptorSet())
		{
			tempNodes[ix] = new PObjectNode();
			tempNodes[ix++].setItem(s);
		}
		return tempNodes;
	}

	private PObjectNode[] getRangeNodes()
	{
		PObjectNode[] tempNodes =
				new PObjectNode[Globals.getRangesSet().size()];
		int ix = 0;
		for (String s : Globals.getRangesSet())
		{
			tempNodes[ix] = new PObjectNode();
			tempNodes[ix++].setItem(s);
		}
		return tempNodes;
	}

	private PObjectNode[] getDurationNodes()
	{
		PObjectNode[] tempNodes =
				new PObjectNode[Globals.getDurationSet().size()];
		int ix = 0;
		for (String s : Globals.getDurationSet())
		{
			tempNodes[ix] = new PObjectNode();
			tempNodes[ix++].setItem(s);
		}
		return tempNodes;
	}

	private PObjectNode[] getTypeNodes()
	{
		PObjectNode[] tempNodes =
				new PObjectNode[Globals.getTypeForSpells().size()];
		int ix = 0;
		for (String s : Globals.getTypeForSpells())
		{
			tempNodes[ix] = new PObjectNode();
			tempNodes[ix++].setItem(s);
		}
		return tempNodes;
	}

	private PObjectNode[] getSchoolNodes()
	{
		PObjectNode[] tempNodes =
				new PObjectNode[SettingsHandler.getGame()
					.getUnmodifiableSchoolsList().size()];
		int ix = 0;
		for (String s : SettingsHandler.getGame().getUnmodifiableSchoolsList())
		{
			tempNodes[ix] = new PObjectNode();
			tempNodes[ix++].setItem(s);
		}
		return tempNodes;
	}

	/**
	 * @param pc
	 */
	public void setCharacter(PlayerCharacter pc)
	{
		this.pc = pc;
	}

	public List<String> getMColumnList()
	{
		List<String> retList = new ArrayList<String>();
		for (int i = 1; i < colNameList.length; i++)
		{
			retList.add(colNameList[i]);
		}
		return retList;
	}

	public boolean isMColumnDisplayed(int col)
	{
		return (displayList.get(col)).booleanValue();
	}

	public int getMColumnOffset()
	{
		return 1;
	}

	public void setMColumnDisplayed(int col, boolean disp)
	{
		setColumnViewOption(colNameList[col], disp);
		displayList.set(col, Boolean.valueOf(disp));
	}

	public int getMColumnDefaultWidth(int col)
	{
		return SettingsHandler.getPCGenOption(getOptionName()
			+ "sizecol." + colNameList[col], colDefaultWidth[col]); //$NON-NLS-1$
	}

	public void setMColumnDefaultWidth(int col, int width)
	{
		SettingsHandler.setPCGenOption(getOptionName()
			+ "sizecol." + colNameList[col], width); //$NON-NLS-1$
	}

	private boolean getColumnViewOption(String colName, boolean defaultVal)
	{
		return SettingsHandler.getPCGenOption(getOptionName()
			+ "viewcol." + colName, defaultVal); //$NON-NLS-1$
	}

	private void setColumnViewOption(String colName, boolean val)
	{
		SettingsHandler.setPCGenOption(
			getOptionName() + "viewcol." + colName, val); //$NON-NLS-1$
	}

	private String getOptionName()
	{
		StringBuffer nameSb = new StringBuffer("InfoSpells."); //$NON-NLS-1$
		if (available)
		{
			nameSb.append("left."); //$NON-NLS-1$
		}
		else
		{
			nameSb.append("right."); //$NON-NLS-1$
		}
		return nameSb.toString();
	}

	public void resetMColumn(int col, TableColumn column)
	{
		// TODO Auto-generated method stub

	}
}
