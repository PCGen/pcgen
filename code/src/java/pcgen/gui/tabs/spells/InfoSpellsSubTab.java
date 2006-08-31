/*
 * InfoKnownSpells.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Jan 4, 2006
 *
 * $Id$
 *
 */
package pcgen.gui.tabs.spells;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import pcgen.core.Ability;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableMouseAdapter;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.Utility;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoSpellsSubTab</code> is the common base for the spell sub tabs.
 * It is the home for common code shared with the three sub tabs related to
 * being a CharacterInfoTab and displaying the available and selected spell
 * lists. <br/>
 * The sub tabs themselves are responsible for populating the lists and
 * dealing with selection events, as well as the layout of the tab and the
 * management of any extra fucntions.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public abstract class InfoSpellsSubTab extends FilterAdapterPanel implements CharacterInfoTab
{
	private static Tab tab;
	
	protected String currSpellBook = Globals.getDefaultSpellBook();

	protected int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	protected int primaryViewMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
	protected int secondaryViewMode = GuiConstants.INFOSPELLS_VIEW_LEVEL;
	protected int primaryViewSelectMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
	protected int secondaryViewSelectMode = GuiConstants.INFOSPELLS_VIEW_LEVEL;
	protected boolean needsUpdate = true;
	protected String addSpellWithMetaMagicTitle = null;

	protected List<String> availableBookList = new ArrayList<String>();
	protected List<String> selectedBookList = new ArrayList<String>();

	protected JMenuItem addMenu;
	protected JMenuItem addMetaMagicMenu;
	protected JMenuItem delSpellMenu;
	protected JMenuItem setAutoBookMenu;

	protected JTreeTable availableTable; // available Spells
	protected JTreeTable selectedTable; // selected Spells
	protected JTreeTableSorter availableSort = null;
	protected JTreeTableSorter selectedSort = null;
	protected SpellModel availableModel = null; // Model for JTreeTable
	protected SpellModel selectedModel = null; // Model for JTreeTable
	protected TreePath selPath;
	protected JLabelPane classLabel = new JLabelPane();
	protected JLabelPane infoLabel = new JLabelPane();
	protected String lastClass = ""; //$NON-NLS-1$
	protected Spell lastSpell = null;

	protected PlayerCharacter pc;
	protected int serial = 0;
	protected boolean readyForRefresh = false;

	/**
	 *  Constructor for the InfoSpells object
	 * @param pc
	 * @param tabID
	 *
	 */
	public InfoSpellsSubTab(PlayerCharacter pc, Tab aTab)
	{
		this.pc = pc;
		tab = aTab;
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(tab.toString());
	}

	/**
	 * @param flag
	 */
	public final void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	// -- Methods from FilterAdapterPanel that have common implementations --

	/**
	 * @see pcgen.gui.filter.Filterable#initializeFilters()
	 */
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSpellFilters(this);
	}

	/**
	 * @see pcgen.gui.filter.Filterable#refreshFiltering()
	 */
	public final void refreshFiltering()
	{
		updateAvailableModel();
		updateSelectedModel();
	}

	/**
	 * Specifies the filter selection mode
	 * @return FilterConstants.MULTI_MULTI_MODE
	 */
	public int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 **/
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "match any" option should be available<br>
	 * implementation of Filterable interface
	 *
	 * @return <code>true</code>, so the "match any" option is available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	protected boolean shouldDisplayThis(Spell spell)
	{
		return accept(pc, spell);
	}

	// -- Methods from CharacterInfoTab that have common implementations --

	/**
	 * @see pcgen.gui.CharacterInfoTab#setPc(pcgen.core.PlayerCharacter)
	 */
	public final void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			selectedModel.setCharacter(pc);
			availableModel.setCharacter(pc);
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getPc()
	 */
	public final PlayerCharacter getPc()
	{
		return pc;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#refresh()
	 */
	public final void refresh()
	{
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#forceRefresh()
	 */
	public final void forceRefresh()
	{
		if (readyForRefresh)
		{
			needsUpdate = true;
			updateCharacterInfo();
		}
		else
		{
			serial = 0;
		}
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getView()
	 */
	public final JComponent getView()
	{
		return this;
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getTabName()
	 */
	public final String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#isShown()
	 */
	public final boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	// -- Abstract methods that the subtabs must implement --

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	protected abstract void updateCharacterInfo();

	/**
	 * This is called when the tab is shown.
	 */
	protected abstract void formComponentShown();

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	protected abstract void initComponents();

	protected abstract void initActionListeners();

	protected abstract void updateBookList();

	/**
	 * Create the Available Spell Model, being the spells that the user
	 * can choose from.
	 */
	protected abstract void createAvailableModel();

	/**
	 * Create the Selected Spell Model, being the spells that the user
	 * has already chosen.
	 */
	protected abstract void createSelectedModel();

	protected abstract void setSelectedSpell(PObjectNode fNode,
		boolean availSpell);

	protected abstract void addSpellButton();

	protected abstract void delSpellButton();

	protected void addSpellMMButton()
	{
		// We ignore this but allow child classes to override it if
		// there is a required action.
	}

	protected void setAutoBookButton()
	{
		// We ignore this but allow child classes to override it if
		// there is a required action.
	}

	// -- Common implementations to support the twin lists of spells --

	/**
	 * Updates the Available table
	 **/
	protected final void updateAvailableModel()
	{
		List<String> pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * Updates the Selected table
	 **/
	protected final void updateSelectedModel()
	{
		List<String> pathList = selectedTable.getExpandedPaths();

		TreePath modelSelPath = selectedTable.getTree().getSelectionPath();
		int idx = selectedTable.getTree().getRowForPath(modelSelPath);

		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);

		selectedTable.getTree().setSelectionPath(modelSelPath);
		selectedTable.getTree().expandPath(modelSelPath);

		int count = selectedTable.getTree().getRowCount();

		if ((idx >= 0) && (idx < count))
		{
			setSelectedIndex(selectedTable, idx);
		}
	}

	/**
	 * Creates the SpellModel that will be used.
	 */
	protected final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	protected final void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable
			.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		final JTree atree = availableTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());

		availableTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						if (!atree.isSelectionEmpty())
						{
							TreePath avaCPath = atree.getSelectionPath();
							String className = ""; //$NON-NLS-1$

							for (int i = 0; i < avaCPath.getPathCount(); i++)
							{
								className = avaCPath.getPathComponent(i)
									.toString();

								//className may have HTML encoding, so get rid of it
								className = Utility.stripHTML(className);

								// TODO Check this
								PCClass aClass = pc.getClassKeyed(className);

								if (!className.equalsIgnoreCase(lastClass)
									&& (className.length() > 0)
									&& (aClass != null))
								{
									setClassLabelText(aClass);
									break;
								}

							}
						}

						final Object temp = atree.getPathForRow(idx)
							.getLastPathComponent();
						setSelectedSpell((PObjectNode) temp, true);

					}
				}
			});

		// now do the selectedTable and selectedTree
		selectedTable = new JTreeTable(selectedModel);
		selectedTable
			.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		final JTree selectedTree = selectedTable.getTree();
		selectedTree.setRootVisible(false);
		selectedTree.setShowsRootHandles(true);
		selectedTree.setCellRenderer(new LabelTreeCellRenderer());

		selectedTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						final Object temp = selectedTree.getPathForRow(idx)
							.getLastPathComponent();
						setSelectedSpell((PObjectNode) temp, false);
					}
				}
			});

		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), true));
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), true));

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	/**
	 * Create and activate a new popup menu and listener for the treeTable.
	 *
	 * @param treeTable The JTreeTable to have a popup menu.
	 */
	protected final void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new SpellPopupListener(treeTable,
			new SpellPopupMenu(treeTable, this)));
	}

	/**
	 * adds spell contained in fNode to PC's spellbook named bookName
	 * @param fNode
	 * @param bookName
	 */
	protected final void addSpellToTarget(PObjectNode fNode, String bookName)
	{
		List<Object> aList = getInfoFromNode(fNode);
		if (aList == null)
		{
			return;
		}
		CharacterSpell cs = (CharacterSpell) aList.get(0);
		String className = (String) aList.get(1);
		int spLevel = Integer.parseInt((String) aList.get(2));

		if (cs == null)
		{
			return;
		}

		List<Ability> featList = new ArrayList<Ability>();
		final String aString = pc.addSpell(cs, featList, className, bookName,
			spLevel, spLevel);

		if (aString.length() > 0)
		{
			ShowMessageDelegate.showMessageDialog(aString, Constants.s_APPNAME,
				MessageType.ERROR);
			return;
		}
	}

	protected final List<Object> getInfoFromNode(PObjectNode fNode)
	{
		Spell aSpell;
		String classKey = ""; //$NON-NLS-1$
		int spLevel = -1;
		ArrayList<Object> returnList = new ArrayList<Object>(); // 0 = CharacterSpell; 1 = className; 2 = spellLevel

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			return null;
		}

		CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();
		if (spellA.getOwner() instanceof Race)
		{
			return null;
		}

		CharacterSpell cs = null;
		PObject theOwner = spellA.getOwner();
		PCClass aClass = null;
		if (theOwner == null) // should only be true for multi-spellcasting-classed characters not sorted by class/level
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("InfoSpells.only.by.class.level"), //$NON-NLS-1$
				Constants.s_APPNAME, MessageType.ERROR);
			return null; // need to select class/level or level/class as sorters
		}

		spLevel = ((SpellInfo) fNode.getItem()).getActualLevel();
		aSpell = spellA.getSpell();
		if (theOwner instanceof Domain)
		{
			CharacterDomain cd = pc.getCharacterDomainForDomain(theOwner
				.getKeyName());
			if ((cd != null) && cd.isFromPCClass())
			{
				classKey = cd.getObjectName();
				aClass = pc.getClassKeyed(classKey);
			}
			else
			{
				return null;
			}
		}
		else
		{
			aClass = (PCClass) theOwner;
			classKey = aClass.getCastAs();
		}
		List<CharacterSpell> aList = aClass.getSpellSupport().getCharacterSpell(aSpell,
			"", spLevel); //$NON-NLS-1$
		returnList.add(spellA);
		returnList.add(classKey);
		returnList.add(String.valueOf(spLevel));
		for (Iterator<CharacterSpell> ai = aList.iterator(); ai.hasNext();)
		{
			cs = ai.next();
			if (cs.equals(spellA))
			{
				returnList.set(0, cs);
				return returnList;
			}
			if (!theOwner.equals(cs.getOwner()))
			{
				cs = null;
				continue;
			}
			returnList.set(0, cs);
			return returnList;
		}
		if (cs == null)
		{
			cs = new CharacterSpell(theOwner, aSpell);
			returnList.set(0, cs);
			return returnList;
		}
		return returnList;
	}

	protected final void primaryViewComboBoxActionPerformed(final int index)
	{
		if (index != primaryViewMode)
		{
			primaryViewMode = index;
			SettingsHandler.setSpellsTab_AvailableListMode(primaryViewMode);
			updateAvailableModel();
		}
	}

	protected final void secondaryViewComboBoxActionPerformed(final int index)
	{
		if (index != secondaryViewMode)
		{
			secondaryViewMode = index;
			//			SettingsHandler.setSpellsTab_AvailableListMode(secondaryViewMode);
			updateAvailableModel();
		}
	}

	protected final void primaryViewSelectComboBoxActionPerformed(
		final int index)
	{
		if (index != primaryViewSelectMode)
		{
			primaryViewSelectMode = index;
			SettingsHandler
				.setSpellsTab_SelectedListMode(primaryViewSelectMode);
			updateSelectedModel();
		}
	}

	protected final void secondaryViewSelectComboBoxActionPerformed(
		final int index)
	{
		if (index != secondaryViewSelectMode)
		{
			secondaryViewSelectMode = index;
			//			SettingsHandler.setSpellsTab_SelectedListMode(secondaryViewSelectMode);
			updateSelectedModel();
		}
	}

	/**
	 * set the class info text in the Class Info panel
	 * to the currently selected Character Class
	 * @param aClass
	 */
	protected final void setClassLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			lastClass = aClass.getKeyName();

			int highestSpellLevel = aClass.getHighestLevelSpell(pc);
			StringBuffer b = new StringBuffer();
			b.append("<html><table border=1><tr><td><font size=-2><b>"); //$NON-NLS-1$
			b.append(aClass.piSubString()).append(" ["); //$NON-NLS-1$
			b.append(String.valueOf(aClass.getLevel()
				+ (int) pc.getTotalBonusTo("PCLEVEL", aClass.getKeyName()))); //$NON-NLS-1$
			b.append("]</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				b.append("<td><font size=-2><b><center>&nbsp;"); //$NON-NLS-1$
				b.append(i);
				b.append("&nbsp;</b></center></font></td>"); //$NON-NLS-1$
			}

			b.append("</tr>"); //$NON-NLS-1$
			b.append("<tr><td><font size=-1><b>Cast</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				b.append("<td><font size=-1><center>"); //$NON-NLS-1$
				b.append(getNumCast(aClass, i, pc));
				b.append("</center></font></td>"); //$NON-NLS-1$
			}
			// Making sure KnownList can be handled safely and produces the correct behaviour
			if (((aClass.getKnownList().size() > 0) && aClass.getKnownList() != null)
				|| aClass.hasKnownSpells(pc))
			{
				b.append("<tr><td><font size=-1><b>Known</b></font></td>"); //$NON-NLS-1$

				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					final int a = aClass.getKnownForLevel(aClass.getLevel(), i,
						pc);
					final int bonus = aClass.getSpecialtyKnownForLevel(aClass
						.getLevel(), i, pc);
					StringBuffer bString = new StringBuffer();

					if (bonus > 0)
					{
						bString.append('+').append(bonus);
					}

					b.append("<td><font size=-1><center>"); //$NON-NLS-1$
					b.append(a).append(bString);
					b.append("</center></font></td>"); //$NON-NLS-1$
				}
			}

			b.append("<tr><td><font size=-1><b>DC</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				b.append("<td><font size=-1><center>"); //$NON-NLS-1$
				b.append(getDC(aClass, i, pc));
				b.append("</center></font></td>"); //$NON-NLS-1$
			}

			b.append("</tr></table>"); //$NON-NLS-1$

			b.append(PropertyFactory.getString("InfoSpells.caster.type")); //$NON-NLS-1$
			b.append("<b>").append(aClass.getSpellType()); //$NON-NLS-1$
			b.append("</b><br>"); //$NON-NLS-1$
			b.append(PropertyFactory.getString("InfoSpells.stat.bonus")); //$NON-NLS-1$
			b.append("<b>"); //$NON-NLS-1$
			b.append(aClass.getSpellBaseStat()).append("</b><br>"); //$NON-NLS-1$

			if (aClass.getSpecialtyListString(pc).length() != 0)
			{
				b.append(PropertyFactory.getString("InfoSpells.school")); //$NON-NLS-1$
				b.append("<b>"); //$NON-NLS-1$
				b.append(aClass.getSpecialtyListString(pc)).append("</b><br>"); //$NON-NLS-1$
			}

			if (aClass.getProhibitedString().length() != 0)
			{
				b.append(PropertyFactory
					.getString("InfoSpells.prohibited.school")); //$NON-NLS-1$
				b.append("<b>"); //$NON-NLS-1$
				b.append(aClass.getProhibitedString()).append("</b><br>"); //$NON-NLS-1$
			}

			String bString = aClass.getDefaultSourceString();

			if (bString.length() > 0)
			{
				b.append("<b>"); //$NON-NLS-1$
				b.append(PropertyFactory.getString("InfoSpells.source")); //$NON-NLS-1$
				b.append("</b>").append(bString); //$NON-NLS-1$
			}

			b.append("</html>"); //$NON-NLS-1$
			classLabel.setText(b.toString());
		}
	}

	/**
	 * Set the spell Info text in the Spell Info panel to the
	 * currently selected spell.
	 *
	 * @param si The info to be displayed.
	 */
	protected final void setInfoLabelText(SpellInfo si)
	{
		if (si == null)
		{
			return;
		}

		CharacterSpell cs = si.getOwner();
		lastSpell = cs.getSpell(); //even if that's null

		Spell aSpell = lastSpell;

		if (aSpell != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><font size=+1><b>"); //$NON-NLS-1$
			b.append(aSpell.piSubString()).append("</b></font>"); //$NON-NLS-1$

			final String addString = si.toString(); // would add [featList]

			if (addString.length() > 0)
			{
				b.append(" &nbsp;").append(addString); //$NON-NLS-1$
			}

			b.append(" &nbsp;<b>"); //$NON-NLS-1$
			b.append(PropertyFactory.getString("InfoSpells.level.title")); //$NON-NLS-1$
			b.append("</b>&nbsp; "); //$NON-NLS-1$
			if (cs.getOwner() != null)
			{

				int[] levels = aSpell.levelForKey(cs.getOwner().getSpellKey(),
					pc);

				for (int index = 0; index < levels.length; ++index)
				{
					if (index > 0)
					{
						b.append(',');
					}

					b.append(levels[index]);
				}
			}
			else b.append(aSpell.getLevelString());

			b.append(PropertyFactory.getFormattedString(
				"InfoSpells.html.spell.details", //$NON-NLS-1$
				new Object[]{
					aSpell.getSchool(),
					aSpell.getSubschool(),
					aSpell.descriptor(),
					aSpell.getComponentList(),
					aSpell.getCastingTime(),
					pc.parseSpellString(aSpell, aSpell.getDuration(), cs
						.getOwner()),
					pc.getSpellRange(aSpell, cs.getOwner(), si),
					pc.parseSpellString(aSpell, aSpell.getTarget(), cs
						.getOwner()),
					aSpell.getSaveInfo(),
					aSpell.getSpellResistance(),
					pc.parseSpellString(aSpell, aSpell.getDescription(), cs
						.getOwner())}));

			if (Spell.hasPPCost())
			{
				b.append(" &nbsp;<b>PP&nbsp;Cost</b>:&nbsp;").append(
					aSpell.getPPCost());
			}

			final String cString = aSpell.preReqHTMLStrings(pc, false);
			if (cString.length() > 0)
			{
				b.append(" &nbsp;<b>Requirements</b>:&nbsp;").append(cString);
			}

			String spellSource = aSpell.getDefaultSourceString();

			if (spellSource.length() > 0)
			{
				b.append(" &nbsp;<b>"); //$NON-NLS-1$
				b.append(PropertyFactory.getString("InfoSpells.source.title")); //$NON-NLS-1$
				b.append("</b>&nbsp;").append(spellSource); //$NON-NLS-1$
			}

			b.append("</html>"); //$NON-NLS-1$
			infoLabel.setText(b.toString());
		}
	}

	/**
	 * Set the spell Info text in the Spell Info panel to the
	 * currently selected spell book.
	 *
	 * @param book The book to be displayed.
	 */
	protected final void setInfoLabelText(SpellBook book)
	{
		if (book == null)
		{
			return;
		}

		StringBuffer b = new StringBuffer();
		b.append("<html><font size=+1><b>"); //$NON-NLS-1$
		b.append(book.getName()).append("</b></font>"); //$NON-NLS-1$

		b.append(" ("); //$NON-NLS-1$
		b.append(book.getTypeName());
		if (book.getName().equals(pc.getSpellBookNameToAutoAddKnown()))
		{
			b.append(" &nbsp;<b>"); //$NON-NLS-1$
			b.append("Default Book for New Known Spells").append("</b> "); //$NON-NLS-2$
		}
		b.append(")<br>"); //$NON-NLS-1$

		if (book.getType() == SpellBook.TYPE_SPELL_BOOK)
		{
			b.append("<b>"); //$NON-NLS-1$
			b.append("Num Pages").append("</b>: "); //$NON-NLS-2$
			b.append(book.getNumPages());
			b.append(" &nbsp;<b>"); //$NON-NLS-1$
			b.append("Used Pages").append("</b>: "); //$NON-NLS-2$
			b.append(book.getNumPagesUsed());
			b.append(" &nbsp;<b>"); //$NON-NLS-1$
			b.append("Page Use").append("</b>: "); //$NON-NLS-2$
			b.append(book.getPageFormula());
			b.append(" &nbsp;<b>"); //$NON-NLS-1$
			b.append("Num Spells").append("</b>: "); //$NON-NLS-2$
			b.append(book.getNumSpells());
			b.append("<br>"); //$NON-NLS-1$
		}

		if (book.getDescription() != null)
		{
			b.append(book.getDescription());
		}

		b.append("</html>"); //$NON-NLS-1$
		infoLabel.setText(b.toString());
	}

	/**
	 * Populate a spell sort combo box with the various sorting options
	 * and set its intial state.
	 *
	 * @param combo The combo box to be populated
	 * @param initialIndex The intial state of the combox box.
	 * @param includeNothing Should the nothing entry be included
	 */
	protected final void populateViewCombo(JComboBoxEx combo, int initialIndex,
		boolean includeNothing)
	{
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_class")); //$NON-NLS-1$
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_level")); //$NON-NLS-1$
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_descriptor")); //$NON-NLS-1$
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_range")); //$NON-NLS-1$
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_duration")); //$NON-NLS-1$
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_type")); //$NON-NLS-1$
		combo.addItem(PropertyFactory.getString("InfoSpells.combo_school")); //$NON-NLS-1$
		if (includeNothing)
		{
			combo
				.addItem(PropertyFactory.getString("InfoSpells.combo_nothing")); //$NON-NLS-1$

		}
		combo.setSelectedIndex(initialIndex);
	}

	/**
	 * Check and set the mode selected for viewing the list of available
	 * spells. The supplied value will be used as the primary sort order
	 * but the secondary sort order will only be changed if it is the same
	 * as the new sort order.
	 *
	 * @param iView The new primary sort oder.
	 */
	protected final void sanityCheckAvailableSpellMode(int iView)
	{
		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS)
			&& (iView <= GuiConstants.INFOSPELLS_VIEW_SCHOOL))
		{
			primaryViewMode = iView;
		}
		while (secondaryViewMode == primaryViewMode)
		{
			if (secondaryViewMode >= GuiConstants.INFOSPELLS_VIEW_SCHOOL)
			{
				secondaryViewMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
			}
			else
			{
				secondaryViewMode++;
			}
		}
	}

	/**
	 * Check and set the mode selected for viewing the list of selected
	 * spells. The supplied value will be used as the primary sort order
	 * but the secondary sort order will only be changed if it is the same
	 * as the new sort order.
	 *
	 * @param iView The new primary sort oder.
	 */
	protected final void sanityCheckSelectedSpellMode(int iView)
	{
		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS)
			&& (iView <= GuiConstants.INFOSPELLS_VIEW_SCHOOL))
		{
			primaryViewSelectMode = iView;
		}
		while (secondaryViewSelectMode == primaryViewSelectMode)
		{
			if (secondaryViewSelectMode >= GuiConstants.INFOSPELLS_VIEW_SCHOOL)
			{
				secondaryViewSelectMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
			}
			else
			{
				secondaryViewSelectMode++;
			}
		}
	}

	// -- Static helper methods --

	private static int getDC(PCClass aClass, int level, PlayerCharacter pc)
	{
		Spell aSpell = new Spell();
		int DC = aSpell.getDCForPlayerCharacter(pc, null, aClass, level);

		return DC;
	}

	private static final String getNumCast(PCClass aClass, int level,
		PlayerCharacter pc)
	{
		int cLevel = aClass.getLevel();
		String sbook = Globals.getDefaultSpellBook();
		//		final String cast = aClass.getCastForLevel(cLevel, level, sbook, pc)
		final String cast = aClass.getCastForLevel(cLevel, level, sbook, true,
			false, pc)
			+ aClass.getBonusCastForLevelString(cLevel, level, sbook, pc);

		return cast;
	}

	private static final int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e
			.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private static final void setSelectedIndex(JTreeTable aTable, int idx)
	{
		aTable.setRowSelectionInterval(idx, idx);
	}

	// -- Helper event handler and UI classes --

	protected class AvailableClickHandler implements ClickHandler
	{
		public void singleClickEvent()
		{
			// TODO Do nothing
		}

		public void doubleClickEvent()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					addSpellButton();
				}
			});
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	protected class SelectedClickHandler implements ClickHandler
	{
		public void singleClickEvent()
		{
			// Do Nothing
		}

		public void doubleClickEvent()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					delSpellButton();
				}
			});
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	protected class SpellPopupListener extends MouseAdapter
	{
		private JTree tree;
		private SpellPopupMenu menu;

		protected SpellPopupListener(JTreeTable treeTable, SpellPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				//
				// Walk through the list of accelerators to see if the user has
				// pressed a sequence used by the popup.
				// This would not otherwise happen unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke
							.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); ++i)
						{
							final Component menuComponent = menu
								.getComponent(i);

							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks = ((JMenuItem) menuComponent)
									.getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									((JMenuItem) menuComponent).doClick(2);

									return;
								}
							}
						}
					}

					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};

			treeTable.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selPath = tree
					.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				if (tree.isSelectionEmpty())
				{
					tree.setSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else if (!tree.isPathSelected(selPath))
				{
					tree.setSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else
				{
					tree.addSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}

	protected class SpellPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 755097384157285101L;
		private String lastSearch = "";

		protected SpellPopupMenu(JTreeTable treeTable, InfoSpellsSubTab subTab)
		{
			if (treeTable == availableTable)
			{
				SpellPopupMenu.this
					.add(createAddMenuItem(
						PropertyFactory
							.getString("InfoSpells.add.to.spellbook"), "shortcut EQUALS")); //$NON-NLS-1$ //$NON-NLS-2$
				if (addSpellWithMetaMagicTitle != null)
				{
					SpellPopupMenu.this.add(createAddMetaMagicMenuItem(
						addSpellWithMetaMagicTitle, "alt C")); //$NON-NLS-1$

				}
				this.addSeparator();
				SpellPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = availableTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", "Find item", null,
					true));
			}
			else
			// selectedTable
			{
				if (subTab instanceof InfoSpellBooks)
				{
					SpellPopupMenu.this
						.add(createSetAutoMenuItem(PropertyFactory
							.getString("InfoSpells.set.auto.book"), null)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				SpellPopupMenu.this.add(createDelMenuItem(PropertyFactory
					.getString("InfoSpells.remove.spell"), "shortcut MINUS")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				SpellPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = selectedTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", "Find item", null,
					true));
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			addMenu = Utility
				.createMenuItem(
					label,
					new AddSpellActionListener(),
					"add 1", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoSpells.add.to.spellbook"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$

			return addMenu;
		}

		private JMenuItem createAddMetaMagicMenuItem(String label,
			String accelerator)
		{
			addMetaMagicMenu = Utility.createMenuItem(label,
				new AddMMSpellActionListener(), "add 1", (char) 0, accelerator, //$NON-NLS-1$
				addSpellWithMetaMagicTitle, "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$

			return addMetaMagicMenu;
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			delSpellMenu = Utility
				.createMenuItem(
					label,
					new DelSpellActionListener(),
					"remove 1", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoSpells.remove.spell"), "Remove16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
			return delSpellMenu;
		}

		private JMenuItem createSetAutoMenuItem(String label, String accelerator)
		{
			setAutoBookMenu = Utility
				.createMenuItem(
					label,
					new SetAutoBookActionListener(),
					"set auto book", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoSpells.set.auto.book"), null, true); //$NON-NLS-1$ //$NON-NLS-2$
			return setAutoBookMenu;
		}

		private class AddSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton();
			}
		}

		private class AddMMSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellMMButton();
			}
		}

		private class DelSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton();
			}
		}

		private class SetAutoBookActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setAutoBookButton();
			}
		}

		private class SpellActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}
	}

}
