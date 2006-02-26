/*
 * InfoSpells.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Written by Bryan McRoberts <merton_monk@users.sourceforge.net>,
 * Re-written by Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on April 21, 2001, 2:15 PM
 * Re-created on April 1st, 2002, 2:15 am
 *
 * Current Ver: $Revision: 1.99 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/07 15:40:51 $
 *
 */
package pcgen.gui.tabs;

import pcgen.core.*;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.tabs.spells.SpellModel;
import pcgen.gui.utils.*;
import pcgen.util.FOPHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  <code>InfoSpells</code> creates a new tabbed panel.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>, Jayme Cox <jaymecox@netscape.net>
 * created    den 11 maj 2001
 * @version    $Revision: 1.99 $
 */
public class InfoSpells extends FilterAdapterPanel implements CharacterInfoTab
{
	static final long serialVersionUID = 755097384157285101L;
	private static List bookList = new ArrayList();
	private static String currSpellBook = Globals.getDefaultSpellBook();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private static int primaryViewMode = 0;
	private static int secondaryViewMode = 1;
	private static int primaryViewSelectMode = 0;
	private static int secondaryViewSelectMode = 1;
	private static boolean needsUpdate = true;
	private static String addMsg = "";

	private final JLabel avaLabel = new JLabel(PropertyFactory.getString("InfoSpells.sort.spells.by")); //$NON-NLS-1$
	private final JLabel selLabel = new JLabel(PropertyFactory.getString("InfoSpells.sort.spellbooks.by")); //$NON-NLS-1$
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton addBookButton;
	private JButton addSpellButton;
	private JButton delBookButton;
	private JButton delSpellButton;
	private JButton printHtml;
	private JButton printPdf;
	private JButton selectSpellSheetButton = new JButton(PropertyFactory.getString("InfoSpells.select.spellsheet")); //$NON-NLS-1$
	private JCheckBox shouldAutoSpells = new JCheckBox(PropertyFactory.getString("InfoSpells.autoload")); //$NON-NLS-1$
	private JComboBoxEx primaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx primaryViewSelectComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewSelectComboBox = new JComboBoxEx();
	private JLabelPane classLabel = new JLabelPane();
	private JLabelPane infoLabel = new JLabelPane();
	private JMenuItem addMMMenu;
	private JMenuItem addMenu;
	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private JTextField selectSpellSheetField = new JTextField();
	private JTextField spellBookNameText = new JTextField();
	private JTreeTable availableTable; // available Spells
	private JTreeTable selectedTable; // spellbook Spells
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private Spell lastSpell = null;
	private List characterMetaMagicFeats = new ArrayList();
	private SpellModel availableModel = null; // Model for JTreeTable
	private SpellModel selectedModel = null; // Model for JTreeTable
	private String lastClass = ""; //$NON-NLS-1$
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 *  Constructor for the InfoSpells object
	 * @param pc
	 *
	 */
	public InfoSpells(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(Constants.tabNames[Constants.TAB_SPELLS]);

		addMsg = SettingsHandler.getGame().getAddWithMetamagicMessage();
		if (addMsg.length() == 0)
		{
			addMsg = PropertyFactory.getString("InfoSpells.add.with.metamagic");
		}

		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					initComponents();
					initActionListeners();
				}
			});
	}

	public void setPc(PlayerCharacter pc)
	{
		if(this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			selectedModel.setCharacter(pc);
			availableModel.setCharacter(pc);
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Spells.Order", Constants.TAB_SPELLS); //$NON-NLS-1$
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Spells.Order", order); //$NON-NLS-1$
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(Constants.TAB_SPELLS);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(Constants.TAB_SPELLS);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List getToDos()
	{
		List toDoList = new ArrayList();

		boolean hasFree = false;
		for (Iterator iter = pc.getClassList().iterator(); iter.hasNext();)
		{
			PCClass aClass = (PCClass) iter.next();

			if (((aClass.getKnownList().size() > 0) && aClass.getKnownList()!= null) || aClass.hasKnownSpells(pc) )
			{
				int highestSpellLevel = aClass.getHighestLevelSpell();
				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					if (pc.availableSpells(i, aClass, Globals.getDefaultSpellBook(), true, true, pc))
					{
						hasFree = true;
						break;
					}
				}
			}
		}

		if (hasFree)
		{
			toDoList.add(PropertyFactory.getString("InfoSpells.Todo.Remain")); //$NON-NLS-1$
		}
		return toDoList;
	}

	public void refresh()
	{
		if(pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if(readyForRefresh)
		{
			needsUpdate = true;
			updateCharacterInfo();
		}
		else
		{
			serial = 0;
		}
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 **/
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * @param flag 
	 * @deprecated Unused -remove 5.9.5
	 */
	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
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
	 * specifies the filter selection mode
	 * @return FilterConstants.DISABLED_MODE = -2
	 **/
	public final int getSelectionMode()
	{
		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSpellFilters(this);

		setKitFilter("SPELL"); //$NON-NLS-1$
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void refreshFiltering()
	{
		updateAvailableModel();
		updateSelectedModel();
	}

	/**
	 * set the class info text in the Class Info panel
	 * to the currently selected Character Class
	 * @param aClass
	 */
	private void setClassLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			lastClass = aClass.getName();

			int highestSpellLevel = aClass.getHighestLevelSpell();
			StringBuffer b = new StringBuffer();
			b.append("<html><table border=1><tr><td><font size=-2><b>"); //$NON-NLS-1$
			b.append(aClass.piSubString()).append(" ["); //$NON-NLS-1$
			b.append(String.valueOf(aClass.getLevel() + (int) pc.getTotalBonusTo("PCLEVEL", aClass.getName()))); //$NON-NLS-1$
			b.append("]</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				b.append("<td><font size=-2><b><center>&nbsp;").append(i).append("&nbsp;</b></center></font></td>"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			b.append("</tr>"); //$NON-NLS-1$
			b.append("<tr><td><font size=-1><b>Cast</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				b.append("<td><font size=-1><center>").append(getNumCast(aClass, i, pc)).append("</center></font></td>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			// Making sure KnownList can be handled safely and produces the correct behaviour
			if (((aClass.getKnownList().size() > 0) && aClass.getKnownList()!= null) || aClass.hasKnownSpells(pc) )
			{
				b.append("<tr><td><font size=-1><b>Known</b></font></td>"); //$NON-NLS-1$

				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					final int a = aClass.getKnownForLevel(aClass.getLevel(), i, pc);
					final int bonus = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), i, pc);
					StringBuffer bString = new StringBuffer();

					if (bonus > 0)
					{
						bString.append('+').append(bonus);
					}

					b.append("<td><font size=-1><center>").append(a).append(bString).append("</center></font></td>"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			b.append("<tr><td><font size=-1><b>DC</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				b.append("<td><font size=-1><center>").append(getDC(aClass, i, pc)).append("</center></font></td>"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			b.append("</tr></table>"); //$NON-NLS-1$

			b.append(PropertyFactory.getString("InfoSpells.caster.type")); //$NON-NLS-1$
			b.append("<b>").append(aClass.getSpellType()).append("</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
			b.append(PropertyFactory.getString("InfoSpells.stat.bonus")); //$NON-NLS-1$
			b.append("<b>").append(aClass.getSpellBaseStat()).append("</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$

			if (aClass.getSpecialtyListString(pc).length() != 0)
			{
				b.append(PropertyFactory.getString("InfoSpells.school")); //$NON-NLS-1$
				b.append("<b>").append(aClass.getSpecialtyListString(pc)).append("</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (aClass.getProhibitedString().length() != 0)
			{
				b.append(PropertyFactory.getString("InfoSpells.prohibited.school")); //$NON-NLS-1$
				b.append("<b>").append(aClass.getProhibitedString()).append("</b><br>"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			String bString = aClass.getSource();

			if (bString.length() > 0)
			{
				b.append("<b>").append(PropertyFactory.getString("InfoSpells.source")).append("</b>").append(bString); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			b.append("</html>"); //$NON-NLS-1$
			classLabel.setText(b.toString());
		}
	}

	private static int getDC(PCClass aClass, int level, PlayerCharacter pc)
	{
		Spell aSpell = new Spell();
		int DC = aSpell.getDCForPlayerCharacter(pc, null, aClass, level);

		return DC;
	}

	/*
	 * set the spell Info text in the Spell Info panel to the
	 * currently selected spell
	 */
	private void setInfoLabelText(SpellInfo si)
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
			b.append("<html><font size=+1><b>").append(aSpell.piSubString()).append("</b></font>"); //$NON-NLS-1$ //$NON-NLS-2$

			final String addString = si.toString(); // would add [featList]

			if (addString.length() > 0)
			{
				b.append(" &nbsp;").append(addString); //$NON-NLS-1$
			}

			b.append(" &nbsp;<b>").append(PropertyFactory.getString("InfoSpells.level.title")).append("</b>&nbsp; "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (cs.getOwner() != null)
			{

				int[] levels = aSpell.levelForKey(cs.getOwner().getSpellKey(), pc);

				for (int index = 0; index < levels.length; ++index)
				{
					if (index > 0)
					{
						b.append(',');
					}

					b.append(levels[index]);
				}
			}
			else
				b.append(aSpell.getLevelString());

			b.append(PropertyFactory.getFormattedString("InfoSpells.html.spell.details", //$NON-NLS-1$
					new Object[] {
									aSpell.getSchool(),
									aSpell.getSubschool(),
									aSpell.descriptor(),
									aSpell.getComponentList(),
									aSpell.getCastingTime(),
									pc.parseSpellString(aSpell, aSpell.getDuration(), cs.getOwner()),
									aSpell.getRange(),
									pc.parseSpellString(aSpell, aSpell.getTarget(), cs.getOwner()),
									aSpell.getSaveInfo(),
									aSpell.getSpellResistance(),
									pc.parseSpellString(aSpell, aSpell.getDescription(), cs.getOwner())
								}));


			if (Spell.hasPPCost())
			{
				b.append(" &nbsp;<b>PP&nbsp;Cost</b>:&nbsp;").append(aSpell.getPPCost());
			}

			final String cString = aSpell.preReqHTMLStrings(pc, false);
			if (cString.length() > 0)
			{
				b.append(" &nbsp;<b>Requirements</b>:&nbsp;").append(cString);
			}

			String spellSource = aSpell.getSource();

			if (spellSource.length() > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("InfoSpells.source.title")).append("</b>&nbsp;").append(spellSource); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			b.append("</html>"); //$NON-NLS-1$
			infoLabel.setText(b.toString());
		}
	}

	private static String getNumCast(PCClass aClass, int level, PlayerCharacter pc)
	{
		int cLevel = aClass.getLevel();
		String sbook = Globals.getDefaultSpellBook();
//		final String cast = aClass.getCastForLevel(cLevel, level, sbook, pc)
		final String cast = aClass.getCastForLevel(cLevel, level, sbook, true, false, pc)
			+ aClass.getBonusCastForLevelString(cLevel, level, sbook, pc);

		return cast;
	}

	private static int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private class AvailableClickHandler implements ClickHandler
	{
		public void singleClickEvent() {
			// Do Nothing
		}
		
		public void doubleClickEvent()
		{
			addSpellButton();
		}
		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private class SelectedClickHandler implements ClickHandler
	{
		public void singleClickEvent() {
			// Do Nothing
		}
		
		public void doubleClickEvent()
		{
			delSpellButton();
		}
		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private final void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		final JTree atree = availableTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());

		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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

							if (primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS)
							{
								className = avaCPath.getPathComponent(1).toString();
							}
							else if ((secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL) && (avaCPath.getPathCount() > 2))
							{
								className = avaCPath.getPathComponent(2).toString();
							}
							else if (lastClass != null)
							{
								className = lastClass;
							}

							//className may have HTML encoding, so get rid of it
							className = Utility.stripHTML(className);

							PCClass aClass = pc.getClassNamed(className);

							if (!className.equalsIgnoreCase(lastClass) && (className.length() > 0) && (aClass != null))
							{
								setClassLabelText(aClass);
							}
						}

						final Object temp = atree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							lastSpell = null;
							infoLabel.setText();

							return;
						}

						PObjectNode fNode = (PObjectNode) temp;

						if (fNode.getItem() instanceof SpellInfo)
						{
							CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();

							if (spellA.getSpell() != null)
							{
								addSpellButton.setEnabled(true);
								addMenu.setEnabled(true);
								addMMMenu.setEnabled(true);
								setInfoLabelText((SpellInfo) fNode.getItem());
							}
						}
						else
						{
							addSpellButton.setEnabled(false);
							addMenu.setEnabled(false);
							addMMMenu.setEnabled(false);
						}
					}
				}
			});

		// now do the selectedTable and selectedTree
		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree selectedTree = selectedTable.getTree();
		selectedTree.setRootVisible(false);
		selectedTree.setShowsRootHandles(true);
		selectedTree.setCellRenderer(new LabelTreeCellRenderer());

		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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

						TreePath selCPath = selectedTree.getSelectionPath();

						if (!selectedTree.isSelectionEmpty())
						{
							spellBookNameText.setText(selCPath.getPathComponent(1).toString());
							spellBookNameTextActionPerformed();
						}

						final Object temp = selectedTree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							lastSpell = null;
							infoLabel.setText();

							return;
						}

						PObjectNode fNode = (PObjectNode) temp;

						if (fNode.getItem() instanceof SpellInfo)
						{
							CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();

							if (spellA.getSpell() != null)
							{
								delSpellButton.setEnabled(true);
								setInfoLabelText((SpellInfo) fNode.getItem());
							}
						}
					}
				}
			});

		availableTable.addMouseListener(new JTreeTableMouseAdapter(availableTable, new AvailableClickHandler(), true));
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(selectedTable, new SelectedClickHandler(), true));

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void setSelectedIndex(JTreeTable aTable, int idx)
	{
		aTable.setRowSelectionInterval(idx, idx);
	}

	/*
	 *
	 * This is used to add new spellbooks when the
	 * spellBookNameText JTextField is edited
	 *
	 */
	private void addBookButton()
	{
		final String aString = spellBookNameText.getText();

		if (aString.equals(currSpellBook))
		{
			return;
		}

		// added to prevent spellbooks being given the same name as a class
		for (Iterator i = Globals.getClassList().iterator(); i.hasNext();)
		{
			PCClass current = (PCClass) i.next();

			if ((aString.equals(current.getName())))
			{
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_spellbook_name_error"), //$NON-NLS-1$
					Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				spellBookNameText.setText(""); //$NON-NLS-1$

				return;
			}
		}

		if (pc.addSpellBook(aString))
		{
			pc.setDirty(true);
			spellBookNameText.setText(aString);
			spellBookNameTextActionPerformed();
			updateSelectedModel();
		}
		else
		{
			Logging.errorPrint("addBookButton:failed"); //$NON-NLS-1$

			return;
		}
	}

	private final void createAvailableModel()
	{
		updateBookList();
		if (availableModel == null)
		{
			availableModel = new SpellModel(this, primaryViewMode, secondaryViewMode, true, bookList, currSpellBook);
		}
		else
		{
			availableModel.resetModel(primaryViewMode, secondaryViewMode, true, bookList, currSpellBook);
			if (currSpellBook.equals("")) //$NON-NLS-1$
			{
				currSpellBook = Globals.getDefaultSpellBook();
			}
			spellBookNameText.setText(currSpellBook);
		}

		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
			availableSort.sortNodeOnColumn();
		}
	}

	/**
	 *
	 * add all metamagic feats to arrayList
	 *
	 **/
	private void createFeatList()
	{
		//Calculate the aggregate feat list
		pc.aggregateFeatList();
		pc.setAggregateFeatsStable(true);
		pc.setAutomaticFeatsStable(true);
		pc.setVirtualFeatsStable(true);

		// get the list of metamagic feats for the PC
		characterMetaMagicFeats.clear();
		List feats = pc.aggregateFeatList();
		Globals.sortPObjectList(feats);

		for (Iterator i = feats.iterator(); i.hasNext();)
		{
			Ability aFeat = (Ability) i.next();

			if (aFeat.isType("Metamagic")) //$NON-NLS-1$
			{
				characterMetaMagicFeats.add(aFeat.getName());
			}
		}
	}

	/**
	 * Creates the SpellModel that will be used.
	 **/
	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createSelectedModel()
	{
		updateBookList();
		if (selectedModel == null)
		{
			selectedModel = new SpellModel(this, primaryViewSelectMode, secondaryViewSelectMode, false, bookList, currSpellBook);
		}
		else
		{
			selectedModel.resetModel(primaryViewSelectMode, secondaryViewSelectMode, false, bookList, currSpellBook);
			if (currSpellBook.equals("")) //$NON-NLS-1$
			{
				currSpellBook = Globals.getDefaultSpellBook();
			}
			spellBookNameText.setText(currSpellBook);
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
			selectedSort.sortNodeOnColumn();
		}
	}


	private void delBookButton()
	{
		String aString = spellBookNameText.getText();

		if (aString.equalsIgnoreCase(Globals.getDefaultSpellBook()))
		{
			Logging.errorPrint(PropertyFactory.getString("InfoSpells.can.not.delete.default.spellbook")); //$NON-NLS-1$

			return;
		}

		if (pc.delSpellBook(aString))
		{
			pc.setDirty(true);
			currSpellBook = Globals.getDefaultSpellBook();

			updateAvailableModel();
			updateSelectedModel();
		}
		else
		{
			Logging.errorPrint("delBookButton:failed "); //$NON-NLS-1$

			return;
		}
	}

	private void updateBookList()
	{
		for (Iterator iBook = pc.getSpellBooks().iterator(); iBook.hasNext();)
		{
			// build spell book list
			String sBook = (String) iBook.next();
			if (!bookList.contains(sBook))
			{
				bookList.add(sBook);
			}
		}

	}


	/**
	 * Exports Spell through selected output sheet to a file
	 **/
	private void exportSpellsToFile()
	{
		final String template = SettingsHandler.getSelectedSpellSheet();
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getPcgPath());

		fcExport.setDialogTitle(PropertyFactory.getString("InfoSpells.export.spells.for") + pc.getDisplayName()); //$NON-NLS-1$

		if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final String aFileName = fcExport.getSelectedFile().getAbsolutePath();

		if (aFileName.length() < 1)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.must.set.filename"), "PCGen", MessageType.ERROR); //$NON-NLS-1$ //$NON-NLS-2$

			return;
		}

		try
		{
			final File outFile = new File(aFileName);

			if (outFile.isDirectory())
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.can.not.overwrite.directory"), "PCGen", MessageType.ERROR); //$NON-NLS-1$ //$NON-NLS-2$

				return;
			}

			if (outFile.exists())
			{
				int reallyClose = JOptionPane.showConfirmDialog(this,
						PropertyFactory.getFormattedString(PropertyFactory.getString("InfoSpells.confirm.overwrite"),outFile.getName()), //$NON-NLS-1$
						PropertyFactory.getFormattedString(PropertyFactory.getString("InfoSpells.overwritnig"), outFile.getName()), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			if (ext.equalsIgnoreCase(".htm") || ext.equalsIgnoreCase(".html")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8")); //$NON-NLS-1$
				Utility.printToWriter(w, template, pc);
			}
			else if (ext.equalsIgnoreCase(".fo") || ext.equalsIgnoreCase(".pdf")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				File tmpFile = File.createTempFile("tempSpells_", ".fo"); //$NON-NLS-1$ //$NON-NLS-2$
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8")); //$NON-NLS-1$
				Utility.printToWriter(w, template, pc);

				FOPHandler fh = new FOPHandler();

				// setting up pdf renderer
				fh.setMode(FOPHandler.PDF_MODE);
				fh.setInputFile(tmpFile);
				fh.setOutputFile(outFile);

				// render to awt
				fh.run();

				tmpFile.deleteOnExit();

				String errMessage = fh.getErrorMessage();

				if (errMessage.length() > 0)
				{
					ShowMessageDelegate.showMessageDialog(errMessage, "PCGen", MessageType.ERROR); //$NON-NLS-1$
				}
			}
		}
		catch (IOException ex)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString("InfoSpells.export.failed.retry", pc.getDisplayName()), "PCGen",   MessageType.ERROR); //$NON-NLS-1$ //$NON-NLS-2$
			Logging.errorPrint(PropertyFactory.getFormattedString("InfoSpells.export.failed", pc.getDisplayName()), ex); //$NON-NLS-1$
		}
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(""); //$NON-NLS-1$

		refresh();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoSpells.splitPane", (int) ((this.getSize().getWidth() * 2) / 10)); //$NON-NLS-1$
			t = SettingsHandler.getPCGenOption("InfoSpells.bsplit", (int) (this.getSize().getHeight() - 101)); //$NON-NLS-1$
			u = SettingsHandler.getPCGenOption("InfoSpells.asplit", (int) (this.getSize().getWidth() - 408)); //$NON-NLS-1$

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellSel", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "SpellSel", i)); //$NON-NLS-1$
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellAva", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "SpellAva", i)); //$NON-NLS-1$
			}
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoSpells.splitPane", s); //$NON-NLS-1$
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoSpells.bsplit", t); //$NON-NLS-1$
		}

		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoSpells.asplit", u); //$NON-NLS-1$
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new SpellPopupListener(treeTable, new SpellPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
			{
				public void componentShown(ComponentEvent evt)
				{
					formComponentShown();
				}
			});
		addComponentListener(new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e)
				{
					int s = splitPane.getDividerLocation();

					if (s > 0)
					{
						SettingsHandler.setPCGenOption("InfoSpells.splitPane", s); //$NON-NLS-1$
					}

					s = asplit.getDividerLocation();

					if (s > 0)
					{
						SettingsHandler.setPCGenOption("InfoSpells.asplit", s); //$NON-NLS-1$
					}

					s = bsplit.getDividerLocation();

					if (s > 0)
					{
						SettingsHandler.setPCGenOption("InfoSpells.bsplit", s); //$NON-NLS-1$
					}
				}
			});
		shouldAutoSpells.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					pc.setAutoSpells(shouldAutoSpells.isSelected());
				}
			});
		addSpellButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					addSpellButton();
				}
			});
		delSpellButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					delSpellButton();
				}
			});
		spellBookNameText.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					spellBookNameTextActionPerformed();
				}
			});
		addBookButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					addBookButton();
				}
			});
		delBookButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					delBookButton();
				}
			});
		primaryViewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				primaryViewComboBoxActionPerformed();
			}
		});
		secondaryViewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				secondaryViewComboBoxActionPerformed();
			}
		});
		primaryViewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				primaryViewSelectComboBoxActionPerformed();
			}
		});
		secondaryViewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				secondaryViewSelectComboBoxActionPerformed();
			}
		});
		selectSpellSheetButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					selectSpellSheetButton();
				}
			});
		printHtml.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					boolean aBool = SettingsHandler.getPrintSpellsWithPC();
					SettingsHandler.setPrintSpellsWithPC(true);
					Utility.previewInBrowser(SettingsHandler.getSelectedSpellSheet(), pc);
					SettingsHandler.setPrintSpellsWithPC(aBool);
				}
			});
		printPdf.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					boolean aBool = SettingsHandler.getPrintSpellsWithPC();
					PCGen_Frame1.getInst();
					SettingsHandler.setPrintSpellsWithPC(true);
					exportSpellsToFile();
					SettingsHandler.setPrintSpellsWithPC(aBool);
				}
			});

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
	private void initComponents()
	{
		readyForRefresh = true;
		//
		// View List Sanity check
		//
		int iView = SettingsHandler.getSpellsTab_AvailableListMode();

		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS) && (iView <= GuiConstants.INFOSPELLS_VIEW_TYPE))
		{
			primaryViewMode = iView;
		}
		while (secondaryViewMode == primaryViewMode)
		{
			if (secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR)
			{
				secondaryViewMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
			}
			else
			{
				secondaryViewMode++;
			}
		}

		SettingsHandler.setSpellsTab_AvailableListMode(primaryViewMode);
		iView = SettingsHandler.getSpellsTab_SelectedListMode();

		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS) && (iView <= GuiConstants.INFOSPELLS_VIEW_TYPE))
		{
			primaryViewSelectMode = iView;
		}
		while (secondaryViewSelectMode == primaryViewSelectMode)
		{
			if (secondaryViewSelectMode == GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR)
			{
				secondaryViewSelectMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
			}
			else
			{
				secondaryViewSelectMode++;
			}
		}
		SettingsHandler.setSpellsTab_SelectedListMode(primaryViewSelectMode);

		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_class")); //$NON-NLS-1$
		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_level")); //$NON-NLS-1$
		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_descriptor")); //$NON-NLS-1$
		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_range")); //$NON-NLS-1$
		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_duration")); //$NON-NLS-1$
		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_type")); //$NON-NLS-1$
		primaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_school")); //$NON-NLS-1$
		primaryViewComboBox.setSelectedIndex(primaryViewMode);
		Utility.setDescription(primaryViewComboBox, PropertyFactory.getString("InfoSpells.change.how.spell.are.listed")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_class")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_level")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_descriptor")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_range")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_duration")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_type")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_school")); //$NON-NLS-1$
		secondaryViewComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_nothing")); //$NON-NLS-1$
		secondaryViewComboBox.setSelectedIndex(secondaryViewMode);
		primaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_class")); //$NON-NLS-1$
		primaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_level")); //$NON-NLS-1$
		primaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_descriptor")); //$NON-NLS-1$
		primaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_range")); //$NON-NLS-1$
		primaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_duration")); //$NON-NLS-1$
		primaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_type")); //$NON-NLS-1$
		primaryViewSelectComboBox.setSelectedIndex(primaryViewSelectMode);
		Utility.setDescription(primaryViewSelectComboBox, PropertyFactory.getString("InfoSpells.change.how.spells.in.table.listed")); //$NON-NLS-1$
		secondaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_class")); //$NON-NLS-1$
		secondaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_level")); //$NON-NLS-1$
		secondaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_descriptor")); //$NON-NLS-1$
		secondaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_range")); //$NON-NLS-1$
		secondaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_duration")); //$NON-NLS-1$
		secondaryViewSelectComboBox.addItem(PropertyFactory.getString("InfoSpells.combo_type")); //$NON-NLS-1$
		Utility.setDescription(secondaryViewSelectComboBox, PropertyFactory.getString("InfoSpells.change.how.spells.in.table.listed")); //$NON-NLS-1$
		secondaryViewSelectComboBox.setSelectedIndex(secondaryViewSelectMode);


		bookList.add(Globals.getDefaultSpellBook());

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif"); //$NON-NLS-1$
		addSpellButton = new JButton(newImage);
		newImage = IconUtilitities.getImageIcon("Back16.gif"); //$NON-NLS-1$
		delSpellButton = new JButton(newImage);

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// build topPane which will contain leftPane and rightPane
		// leftPane will have two panels and a scrollregion
		// rightPane will have one panel and a scrollregion
		topPane.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		rightPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		//
		// first build the left pane
		// for the availabe spells table and info
		//
		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;

		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(primaryViewComboBox);
		aPanel.add(secondaryViewComboBox);
		Utility.setDescription(addSpellButton, PropertyFactory.getString("InfoSpells.add.selected")); //$NON-NLS-1$
		addSpellButton.setEnabled(false);
		aPanel.add(addSpellButton);

		Utility.setDescription(aPanel, PropertyFactory.getString("InfoSpells.rightclick.add.to.spellbooks")); //$NON-NLS-1$
		leftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;

		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);
		shouldAutoSpells.setSelected(pc.getAutoSpells());
		bPanel.add(shouldAutoSpells);
		leftPane.add(bPanel);

		// the available spells panel
		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;

		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		//
		// now build the right pane
		// for the selected (SpellBooks) table
		//
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		// Buttons above spellbooks and known spells
		Utility.buildConstraints(c, 0, 0, 1, 1, 2, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;

		JPanel iPanel = new JPanel();
		gridbag.setConstraints(iPanel, c);
		Utility.setDescription(delSpellButton, PropertyFactory.getString("InfoSpells.remove.selected")); //$NON-NLS-1$
		delSpellButton.setEnabled(false);
		iPanel.add(delSpellButton);
		rightPane.add(iPanel);

		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;

		JPanel sPanel = new JPanel();
		gridbag.setConstraints(sPanel, c);
		sPanel.add(selLabel);
		sPanel.add(primaryViewSelectComboBox);
		sPanel.add(secondaryViewSelectComboBox);
		rightPane.add(sPanel);

		Utility.buildConstraints(c, 2, 0, 1, 1, 1, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		JLabel spellBookLabel = new JLabel(PropertyFactory.getString("InfoSpells.spellbook")); //$NON-NLS-1$
		aPanel.add(spellBookLabel);
		spellBookNameText.setEditable(true);
		spellBookNameText.setPreferredSize(new Dimension(100, 20));
		aPanel.add(spellBookNameText);
		addBookButton = new JButton(PropertyFactory.getString("InfoSpells.add")); //$NON-NLS-1$
		aPanel.add(addBookButton);
		delBookButton = new JButton(PropertyFactory.getString("InfoSpells.delete")); //$NON-NLS-1$
		aPanel.add(delBookButton);
		rightPane.add(aPanel);

		selectSpellSheetField.setEditable(false);
		selectSpellSheetField.setBackground(Color.lightGray);
		selectSpellSheetField.setText(SettingsHandler.getSelectedSpellSheetName());
		selectSpellSheetField.setToolTipText(SettingsHandler.getSelectedSpellSheetName());

		JPanel ssPanel = new JPanel(new GridBagLayout());
		Utility.buildConstraints(c, 0, 1, 3, 1, 1, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(ssPanel, c);

		PCGen_Frame1.getInst();

		// TODO i8n
		printHtml = new JButton();
		printHtml.setToolTipText("Print Preview");
		IconUtilitities.maybeSetIcon(printHtml, "PrintPreview16.gif"); //$NON-NLS-1$
		printHtml.setEnabled(true);

		// TODO i8n
		printPdf = new JButton();
		printPdf.setToolTipText("Print");
		IconUtilitities.maybeSetIcon(printPdf, "Print16.gif"); //$NON-NLS-1$
		printPdf.setEnabled(true);

		c = new GridBagConstraints();
		Utility.buildConstraints(c, 0, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(2, 2, 2, 2);
		ssPanel.add(selectSpellSheetButton, c);
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 1, 0, 1, 1, 1.0, 0.0);
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.HORIZONTAL;
		ssPanel.add(selectSpellSheetField, c);
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 2, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(2, 2, 2, 2);
		ssPanel.add(printHtml, c);
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 3, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(2, 2, 2, 2);
		ssPanel.add(printPdf, c);
		rightPane.add(ssPanel);

		// List of spellbooks and known spells Panel
		Utility.buildConstraints(c, 0, 2, 3, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		selectedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (spell info)
		// bRightPane will contain a scrollregion (character Info)
		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();

		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();
		bLeftPane.setLayout(gridbag);
		bRightPane.setLayout(gridbag);

		asplit = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane, bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		// create a spell info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(PropertyFactory.getString("InfoSpells.spell.info")); //$NON-NLS-1$
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);
		bLeftPane.add(sScroll);

		// create a class info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;

		JScrollPane iScroll = new JScrollPane();

		TitledBorder iTitle = BorderFactory.createTitledBorder(PropertyFactory.getString("InfoSpells.class.info")); //$NON-NLS-1$
		iTitle.setTitleJustification(TitledBorder.CENTER);
		iScroll.setBorder(iTitle);
		classLabel.setBackground(topPane.getBackground());
		iScroll.setViewportView(classLabel);
		iScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gridbag.setConstraints(iScroll, c);
		bRightPane.add(iScroll);

		// now split the top and bottom Panels
		bsplit = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// make sure we update when switching tabs
		this.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					refresh();
				}
			});

		// add the sorter tables so that clicking on the TableHeader
		// actualy does something (gawd damn it's slow!)
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel.getRoot(), selectedModel);
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	private final void updateCharacterInfo()
	{
		lastClass = ""; //$NON-NLS-1$

		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		pc.getSpellList();
		shouldAutoSpells.setSelected(pc.getAutoSpells());

		updateAvailableModel();
		updateSelectedModel();

		createFeatList();
		classLabel.setText(""); //$NON-NLS-1$

		needsUpdate = false;
	}

	private void addSpellButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String bookName;

		if (selCPath == null)
		{
			bookName = spellBookNameText.getText();
		}
		else
		{
			bookName = selCPath.getPathComponent(1).toString();
		}

		if (bookName.length() <= 0)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.first.select.spellbook"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$

			return; // need to select a spellbook
		}

		if (!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS) ||
			!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.can.only.add.by.class.level"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;		// need to select class/level or level/class as sorters
		}

		currSpellBook = bookName;

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();

		for (int index = avaCPaths.length - 1; index >= 0; --index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			PObjectNode fNode = (PObjectNode) aComp;

			addSpellToTarget(fNode, bookName);
		}

		pc.setDirty(true);

		// reset selected spellbook model
		updateSelectedModel();
	}

	private List getInfoFromNode(PObjectNode fNode)
	{
		Spell aSpell;
		String className = ""; //$NON-NLS-1$
		int spLevel = -1;
		ArrayList returnList = new ArrayList(); // 0 = CharacterSpell; 1 = className; 2 = spellLevel

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
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.only.by.class.level"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return null;		// need to select class/level or level/class as sorters
		}

		spLevel = ((SpellInfo) fNode.getItem()).getActualLevel();
		aSpell = spellA.getSpell();
		if (theOwner instanceof Domain)
		{
			CharacterDomain cd = pc.getCharacterDomainForDomain(theOwner.getName());
			if ((cd != null) && cd.isFromPCClass())
			{
				className = cd.getObjectName();
				aClass = pc.getClassNamed(className);
			}
			else
			{
					return null;
			}
		}
		else
		{
			aClass = (PCClass)theOwner;
			className = aClass.getCastAs();
		}
		List aList = aClass.getSpellSupport().getCharacterSpell(aSpell, "" , spLevel); //$NON-NLS-1$
		returnList.add(spellA);
		returnList.add(className);
		returnList.add(String.valueOf(spLevel));
		for (Iterator ai = aList.iterator(); ai.hasNext();)
		{
			cs = (CharacterSpell) ai.next();
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

	/**
	 * adds spell contained in fNode to PC's spellbook named bookName
	 * @param fNode
	 * @param bookName
	 */
	private void addSpellToTarget(PObjectNode fNode, String bookName)
	{
		List aList = getInfoFromNode(fNode);
		if (aList == null)
		{
			return;
		}
		CharacterSpell cs = (CharacterSpell)aList.get(0);
		String className = (String)aList.get(1);
		int spLevel = Integer.parseInt((String)aList.get(2));

		if (cs == null)
		{
			return;
		}

		List featList = new ArrayList();
		final String aString = pc.addSpell(cs, featList, className, bookName, spLevel, spLevel);

		if (aString.length() > 0)
		{
			ShowMessageDelegate.showMessageDialog(aString, Constants.s_APPNAME, MessageType.ERROR);
			return;
		}
	}

	private void delSpellButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.select.spell.to.remove"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;		// need to selected a spellbook
		}

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;
		List aList = getInfoFromNode(fNode);
		if (aList == null)
			return;
		CharacterSpell cs = (CharacterSpell)aList.get(0);
		String className = (String)aList.get(1);
		Integer.parseInt((String)aList.get(2));

		if (cs == null)
			return;

		String bookName = selCPath.getPathComponent(1).toString();
		SpellInfo si = (SpellInfo) fNode.getItem();

		PCClass aClass = pc.getClassNamed(className);
		if (aClass == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.can.only.add.by.class.level"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;		// need to select class/level or level/class as sorters
		}

		bookName =
		currSpellBook = bookName;

		final String aString = pc.delSpell(si, aClass, bookName);

		if (aString.length() > 0)
		{
			ShowMessageDelegate.showMessageDialog(aString, Constants.s_APPNAME, MessageType.ERROR);
			return;
		}

		pc.setDirty(true);
		updateSelectedModel();
	}

	/**
	 * memorize a spell with metamagic feats applied.
	 */
	private void metamagicButton()
	{
		TreePath avaCPath = availableTable.getTree().getSelectionPath();
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		String bookName;
		if (selCPath == null)
		{
			bookName = spellBookNameText.getText();
		}
		else
		{
			bookName = selCPath.getPathComponent(1).toString();
		}

		if (bookName.length() <= 0)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.first.select.spellbook"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;		// need to selected a spellbook
		}

		// no adding metamagic'ed spells to the default spellbook
		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.no.memorized") + bookName, Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;		// need to selected a spellbook
		}

		currSpellBook = bookName;

		String className = ""; //$NON-NLS-1$

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.can.not.metamagic"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;
		}

		SpellInfo si = (SpellInfo)fNode.getItem();
		CharacterSpell spellA = si.getOwner();
		if (!(spellA.getOwner() instanceof PCClass))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.unable.to.metamagic")+spellA.getOwner().getName(), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;
		}

		PCClass aClass = (PCClass)spellA.getOwner();
		if (aClass == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("InfoSpells.con.only.metamagic.class.level"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return;		// need to select class/level or level/class as sorters
		}

		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			spellA = new CharacterSpell(spellA.getOwner(), spellA.getSpell());
		}
		className = aClass.getCastAs();

		// make sure all the feats are set
		createFeatList();


		ChooserInterface c = ChooserFactory.getChooserInstance();
		if (Spell.hasPPCost())
		{
			//
			// Does feat apply a BONUS:PPCOST to this spell, all spells, or all spells of
			// one of this spell's types? If it does, then we can possibly apply it to
			// this spell.
			//

			final String aName = spellA.getSpell().getName();
			List metamagicFeats = new ArrayList();
			for(Iterator cmeta = characterMetaMagicFeats.iterator(); cmeta.hasNext(); )
			{
				final Ability anAbility = Globals.getAbilityNamed("FEAT", (String) cmeta.next());
				if (anAbility == null)
				{
					continue;
				}

				boolean canAdd = false;
				List bonusList = BonusUtilities.getBonusFromList(anAbility.getBonusList(), Bonus.getBonusTypeFromName("PPCOST"));
				if (bonusList.size() == 0)
				{
					canAdd = true;		// if doesn't modify PP COST, then allow it
				}
				else
				{
					for(Iterator ab = bonusList.iterator(); ab.hasNext(); )
					{
						final pcgen.core.bonus.BonusObj aBonus = (pcgen.core.bonus.BonusObj) ab.next();
						final java.util.StringTokenizer aTok = new java.util.StringTokenizer(aBonus.getBonusInfo(), ",");
						while (aTok.hasMoreTokens())
						{
							final String aBI = aTok.nextToken();

							if (aBI.equalsIgnoreCase(aName) || aBI.equalsIgnoreCase("ALL"))
							{
								canAdd = true;
								break;
							}
							else if (aBI.startsWith("TYPE=") || aBI.startsWith("TYPE."))
							{
								if (spellA.getSpell().isType(aBI.substring(5)))
								{
									canAdd = true;
									break;
								}
							}
						}
					}
				}
				if (!canAdd)
				{
					continue;
				}
				metamagicFeats.add(anAbility);
			}
			c.setAvailableList(metamagicFeats);
		}
		else
		{
			c.setAvailableList(characterMetaMagicFeats);
		}
		c.setVisible(false);
		c.setPoolFlag(false);
		c.setAllowsDups(true);
		c.setTitle(addMsg); //$NON-NLS-1$
		c.setMessageText(PropertyFactory.getString("InfoSpells.select.metamagic")); //$NON-NLS-1$
		c.setPool(99);
		c.setVisible(true);

		final List fList = c.getSelectedList();
		List selFeatList = new ArrayList();
		int spLevel = si.getActualLevel();
		int realLevel = spLevel;

		for (int i = 0; i < fList.size(); ++i)
		{
			Ability aFeat = pc.getFeatNamed(fList.get(i).toString());
			realLevel += aFeat.getAddSpellLevel();
			selFeatList.add(aFeat);
		}

		final String aString = pc.addSpell(spellA, selFeatList, className, bookName, realLevel, spLevel);

		if (aString.length() > 0)
		{
			ShowMessageDelegate.showMessageDialog(aString, Constants.s_APPNAME, MessageType.ERROR);
			return;
		}

		pc.setDirty(true);

		updateSelectedModel();

		spellBookNameText.setText(bookName);

	}

	/**
	 *  Select a spell output sheet
	 **/
	private void selectSpellSheetButton()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(PropertyFactory.getString("InfoSpells.select.output.sheet")); //$NON-NLS-1$
		fc.setCurrentDirectory(SettingsHandler.getPcgenOutputSheetDir());
		fc.setSelectedFile(new File(SettingsHandler.getSelectedSpellSheet()));

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedSpellSheet(fc.getSelectedFile().getAbsolutePath());
			selectSpellSheetField.setText(SettingsHandler.getSelectedSpellSheetName());
			selectSpellSheetField.setToolTipText(SettingsHandler.getSelectedSpellSheetName());
		}
	}

	/**
	 * This is used when selecting a new spellbook
	 **/
	private void spellBookNameTextActionPerformed()
	{
		final String aString = spellBookNameText.getText();

		if ((aString == null) || aString.equals(currSpellBook))
		{
			return;
		}

		currSpellBook = aString;
		spellBookNameText.setText(aString);

		if (!bookList.contains(aString))
		{
			bookList.add(aString);
		}

		// if the user selects a new spellbook, we have to refresh
		// the available table because it could change the spells
		// known and memorizable
		updateAvailableModel();
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		List pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		List pathList = selectedTable.getExpandedPaths();

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

	private void primaryViewComboBoxActionPerformed()
	{
		final int index = primaryViewComboBox.getSelectedIndex();
		if (index == secondaryViewComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			//return;
		}
		if (index != primaryViewMode)
		{
			primaryViewMode = index;
			SettingsHandler.setSpellsTab_AvailableListMode(primaryViewMode);
			updateAvailableModel();
		}
	}

	private void secondaryViewComboBoxActionPerformed()
	{
		final int index = secondaryViewComboBox.getSelectedIndex();
		if (index == primaryViewComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			//return;
		}
		if (index != secondaryViewMode)
		{
			secondaryViewMode = index;
//			SettingsHandler.setSpellsTab_AvailableListMode(secondaryViewMode);
			updateAvailableModel();
		}
	}

	private void primaryViewSelectComboBoxActionPerformed()
	{
		final int index = primaryViewSelectComboBox.getSelectedIndex();
		if (index == secondaryViewSelectComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			//return;
		}
		if (index != primaryViewSelectMode)
		{
			primaryViewSelectMode = index;
			SettingsHandler.setSpellsTab_SelectedListMode(primaryViewSelectMode);
			updateSelectedModel();
		}
	}

	private void secondaryViewSelectComboBoxActionPerformed()
	{
		final int index = secondaryViewSelectComboBox.getSelectedIndex();
		if (index == primaryViewSelectComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			//return;
		}
		if (index != secondaryViewSelectMode)
		{
			secondaryViewSelectMode = index;
//			SettingsHandler.setSpellsTab_SelectedListMode(secondaryViewSelectMode);
			updateSelectedModel();
		}
	}


	private class SpellPopupListener extends MouseAdapter
	{
		private JTree tree;
		private SpellPopupMenu menu;

		private SpellPopupListener(JTreeTable treeTable, SpellPopupMenu aMenu)
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
							final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);

							for (int i = 0; i < menu.getComponentCount(); ++i)
							{
								final Component menuComponent = menu.getComponent(i);

								if (menuComponent instanceof JMenuItem)
								{
									KeyStroke ks = ((JMenuItem) menuComponent).getAccelerator();

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
				selPath = tree.getClosestPathForLocation(evt.getX(), evt.getY());

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

	private class SpellPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 755097384157285101L;
		private String lastSearch = "";

		private SpellPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				SpellPopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("InfoSpells.add.to.spellbook"), "shortcut EQUALS")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				SpellPopupMenu.this.add(createAddMMMenuItem(addMsg, "alt C")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				SpellPopupMenu.this.add(Utility.createMenuItem("Find item",
						new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = availableTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", "Find item", null, true));
			}
			else // selectedTable
			{
				SpellPopupMenu.this.add(createDelMenuItem(PropertyFactory.getString("InfoSpells.remove.spell"), "shortcut MINUS")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				SpellPopupMenu.this.add(Utility.createMenuItem("Find item",
						new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = selectedTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", "Find item", null, true));
			}
		}

		private JMenuItem createAddMMMenuItem(String label, String accelerator)
		{
			addMMMenu = Utility.createMenuItem(label, new AddMMSpellActionListener(), "add 1", (char) 0, accelerator, //$NON-NLS-1$
					addMsg, "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$

			return addMMMenu;
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			addMenu = Utility.createMenuItem(label, new AddSpellActionListener(), "add 1", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoSpells.add.to.spellbook"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$

			return addMenu;
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new DelSpellActionListener(), "remove 1", (char) 0, accelerator, //$NON-NLS-1$
				PropertyFactory.getString("InfoSpells.remove.spell"), "Remove16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private class AddMMSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				metamagicButton();
			}
		}

		private class AddSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton();
			}
		}

		private class DelSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton();
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
