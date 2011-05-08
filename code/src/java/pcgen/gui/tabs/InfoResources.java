/*
 * InfoResources.java
 *
 *************************************************************************
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
 *************************************************************************
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 *************************************************************************/
package pcgen.gui.tabs;

import static pcgen.gui.HTMLUtils.BOLD;
import static pcgen.gui.HTMLUtils.BR;
import static pcgen.gui.HTMLUtils.END_BOLD;
import static pcgen.gui.HTMLUtils.END_FONT;
import static pcgen.gui.HTMLUtils.END_HTML;
import static pcgen.gui.HTMLUtils.END_ITALIC;
import static pcgen.gui.HTMLUtils.END_LI;
import static pcgen.gui.HTMLUtils.END_UL;
import static pcgen.gui.HTMLUtils.FONT_PLUS_1;
import static pcgen.gui.HTMLUtils.HTML;
import static pcgen.gui.HTMLUtils.ITALIC;
import static pcgen.gui.HTMLUtils.LI;
import static pcgen.gui.HTMLUtils.PARA;
import static pcgen.gui.HTMLUtils.UL;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.text.Position.Bias;
import javax.swing.tree.TreePath;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.list.CompanionList;
import pcgen.core.Equipment;
import pcgen.core.FollowerOption;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Movement;
import pcgen.core.PCCheck;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.analysis.StatAnalysis;
import pcgen.core.character.Follower;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.tabs.resources.AvailableFollowerModel;
import pcgen.gui.tabs.resources.SelectedFollowerModel;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.InfoLabelTextBuilder;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableMouseAdapter;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.SourcedFollower;
import pcgen.gui.utils.Utility;
import pcgen.io.PCGFile;
import pcgen.io.PCGIOHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 *  <code>InfoResources</code> creates a new tabbed panel that is used to
 *  allow creating/adding familiars, cohorts, companions, intelligent items
 *  vehicles and buildings
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version  $Revision$
 *
 **/
public class InfoResources extends FilterAdapterPanel implements
		CharacterInfoTab
{
	static final long serialVersionUID = 7236403406005940947L;

	private static final Tab tab = Tab.RESOURCES;

	private static boolean needsUpdate = true;

	// table model modes
	private FlippingSplitPane botSplit =
			new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private FlippingSplitPane centerSplit =
			new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);
	private FlippingSplitPane topSplit =
			new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private AvailableFollowerModel availableModel = null; // available Model
	private SelectedFollowerModel selectedModel = null; // selected Model
	private JButton addButton = new JButton();
	private JButton addModButton = new JButton();
	private JButton delButton = new JButton();
	private JButton delModButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton updateButton = new JButton();
	private JCheckBox shouldLoadCompanion =
			new JCheckBox(PropertyFactory
				.getString("InfoResources.AutoLoadCompanions")); //$NON-NLS-1$
	private final JLabel sortLabel =
			new JLabel(PropertyFactory.getString("InfoResources.SortLabel")); //$NON-NLS-1$
	private JComboBoxEx viewSortBox = new JComboBoxEx();
	private JLabelPane followerInfo = new JLabelPane();
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel botPane = new JPanel();
	private JPanel followerPane = new JPanel();
	private JPanel masterPane = new JPanel();
	private JPanel topPane = new JPanel();
	private JTreeTable availableTable; // available table
	private JTreeTable selectedTable; // selected table
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private TreePath selPath;
	private boolean hasBeenSized = false;
	private int viewSortMode = 0;

	private final JLabel lblQFilter =
			new JLabel(PropertyFactory.getString("InfoTabs.FilterLabel")); //$NON-NLS-1$
	private JTextField textQFilter = new JTextField();
	private JButton clearQFilterButton =
			new JButton(PropertyFactory.getString("in_mnuToolsFiltersClear")); //$NON-NLS-1$
	private static Integer saveViewMode = null;

	PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	private static final String TAB_ORDER_KEY = ".Panel.Resources.Order"; //$NON-NLS-1$

	/**
	 *  Constructor for the InfoResources object
	 * @param aPC
	 **/
	public InfoResources(PlayerCharacter aPC)
	{
		this.pc = aPC;
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(tab.toString());

		initComponents();

		initActionListeners();

		//FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * Sets the PlayerCharacter to display info for
	 * @param aPC The PlayerCharacter to display
	 * @see pcgen.gui.CharacterInfoTab#setPc(pcgen.core.PlayerCharacter)
	 */
	public void setPc(PlayerCharacter aPC)
	{
		if (this.pc != aPC || aPC.getSerial() > serial)
		{
			this.pc = aPC;
			serial = aPC.getSerial();
			availableModel.setCharacter(aPC);
			selectedModel.setCharacter(aPC);
			forceRefresh();
		}
	}

	/**
	 * Gets the currently displayed PlayerCharacter
	 * @return The PlayerCharacter being displayed
	 */
	public PlayerCharacter getPc()
	{
		return pc;
	}

	/**
	 * Gets an integer placement within the list of tabs to
	 * display this tab in.
	 * @return The order to display this tab in
	 */
	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(TAB_ORDER_KEY, tab.ordinal());
	}

	/**
	 * @param order The order to put this tab in in the list
	 * of tabs to display
	 */
	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(TAB_ORDER_KEY, order);
	}

	/**
	 * Returns the name of this tab
	 * @return The tab name
	 */
	public String getTabName()
	{
		final GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	/**
	 * This this tab visible within the GUI
	 * @return true if the tab will be shown
	 */
	public boolean isShown()
	{
		final GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
		for (CompanionList compType : Globals.getContext().ref
				.getConstructedCDOMObjects(CompanionList.class))
		{
			// Check if we have a number set for this type
			int maxVal = pc.getMaxFollowers(compType);
			if (maxVal > 0)
			{
				for (Follower aF : pc.getFollowerList())
				{
					if (compType.equals(aF.getType()))
					{
						maxVal--;
					}
				}

				if (maxVal > 0)
				{
					toDoList.add(PropertyFactory.getFormattedString(
						"ToDo.InfoResources.AddFollower", compType, maxVal)); //$NON-NLS-1$
				}
			}
		}
		return toDoList;
	}

	/**
	 * Refresh this tab if a change to the displayed 
	 * PlayerCharacter is detected.
	 */
	public void refresh()
	{
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	/**
	 * Force this tabs data to be considered invalid.
	 */
	public void forceRefresh()
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
	 * Returns the Component used to view this tab.
	 * @return The component that draws the view for this tab
	 */
	public JComponent getView()
	{
		return this;
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 **/
	@Override
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * Sets the update flag for this tab
	 * It's a lazy update and will only occur
	 * on other status change
	 * @param flag true to flag the tab as needing a refresh
	 **/
	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 **/
	@Override
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * Specifies the filter selection mode
	 * @return FilterConstants.DISABLED_MODE = -2
	 **/
	@Override
	public final int getSelectionMode()
	{
		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * Initializes the Filters used by this tab.
	 * @see pcgen.gui.filter.FilterAdapterPanel#initializeFilters()
	 */
	@Override
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
	}

	/**
	 * Called when the filtering has changed.  Components should
	 * redraw themselves using the new filter parameters.
	 * @see pcgen.gui.filter.FilterAdapterPanel#refreshFiltering()
	 */
	@Override
	public final void refreshFiltering()
	{
		updateAvailableModel();
		updateSelectedModel();
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	public final void updateCharacterInfo()
	{
		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		shouldLoadCompanion.setSelected(pc.getLoadCompanion());

		if (pc.getMaster() != null)
		{
			setFollowerInfo(pc);
			this.remove(masterPane);
			this.add(followerPane, BorderLayout.CENTER);
			followerPane.setVisible(true);
			masterPane.setVisible(false);
			followerPane.updateUI();
			this.updateUI();
		}
		else
		{
			updateAvailableModel();
			updateSelectedModel();
			this.remove(followerPane);
			this.add(masterPane, BorderLayout.CENTER);
			masterPane.setVisible(true);
			followerPane.setVisible(false);
			followerPane.updateUI();
			masterPane.updateUI();
			this.updateUI();
		}

		needsUpdate = false;
	}

	/**
	 * set the Follower Info text in the FollowerInfo panel
	 * @param obj
	 **/
	private void setFollowerInfo(Object obj)
	{
		if (obj == null)
		{
			return;
		}

		if (obj instanceof PlayerCharacter)
		{
			//Globals.errorPrint("setFI: " + pc.getName());
			Follower aF = pc.getMaster();
			PlayerCharacter mPC = null;

			for (PlayerCharacter nPC : Globals.getPCList())
			{
				if (aF.getFileName().equals(nPC.getFileName()))
				{
					mPC = nPC;
				}
			}

			if (mPC == null)
			{
				followerInfo.setText(PropertyFactory
					.getString("InfoResources.LoadMasterNotice")); //$NON-NLS-1$

				return;
			}

			StringBuffer b = new StringBuffer();
			b.append(HTML);
			b.append(followerStatBlock(aF, pc));

			//
			// add some of the Master's stats
			// but first switch the "current PC" to master
			// so stats show up correctly
			//
			b.append(PARA);
			b.append(FONT_PLUS_1);
			b.append(BOLD);
			b.append(PropertyFactory
				.getString("InfoResources.MasterInformation")); //$NON-NLS-1$
			b.append(END_BOLD).append(END_FONT).append(BR);
			b.append(BOLD);
			b.append(PropertyFactory.getString("InfoResources.PCNameLabel")); //$NON-NLS-1$
			b.append(END_BOLD).append(" "); //$NON-NLS-1$
			b.append(mPC.getName());
			b.append(BR).append(BOLD);
			b.append(PropertyFactory.getString("InfoResources.FileLabel")); //$NON-NLS-1$
			b.append(END_BOLD).append(" "); //$NON-NLS-1$
			b.append(mPC.getFileName());
			b.append(BR).append(BOLD);
			b.append(PropertyFactory.getString("in_sumRace")); //$NON-NLS-1$
			b.append(END_BOLD).append(" "); //$NON-NLS-1$
			b.append(mPC.getRace());
			b.append(BR).append(BOLD);
			b.append(Globals.getGameModeHPAbbrev());
			b.append(END_BOLD).append(": ").append(mPC.hitPoints()); //$NON-NLS-1$
			b.append(BR);

			int bonus = mPC.baseAttackBonus();
			b.append(BOLD);
			String babAbbrev = SettingsHandler.getGame().getBabAbbrev();
			if (babAbbrev == null)
			{
				babAbbrev = "BAB"; //$NON-NLS-1$
			}
			b.append(babAbbrev);
			b.append(END_BOLD).append(": "); //$NON-NLS-1$
			b
				.append(
					(bonus >= 0)
						? PropertyFactory.getString("in_plusSign") : "").append(bonus); //$NON-NLS-1$ //$NON-NLS-2$
			b.append(BR);

			b.append(END_HTML);

			followerInfo.setText(b.toString());
			followerInfo.setVisible(true);
			followerInfo.repaint();
		}
	}

	private void addButton()
	{
		if ("".equals(pc.getFileName())) //$NON-NLS-1$
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.SaveFirst"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		TreePath avaCPath = availableTable.getTree().getSelectionPath();

		PObjectNode node =
				(PObjectNode) avaCPath.getParentPath().getLastPathComponent();
		TreePath selCPath =
				selectedTable.getTree().getNextMatch(node.getDisplayName(), 0,
					Bias.Forward);
		if (selCPath == null)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.DestinationFirst"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return;
		}
		selectedTable.getTree().setSelectionPath(selCPath);
		SelectedFollowerModel.FollowerType target =
				(SelectedFollowerModel.FollowerType) ((PObjectNode) selCPath
					.getPathComponent(1)).getItem();
		if (target.getNumRemaining() == 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.NoMoreFollowers"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return;
		}

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		final SourcedFollower sf = (SourcedFollower) fNode.getItem();
		FollowerOption opt = sf.option;
		if (!opt.qualifies(pc, sf.owner))
		{
			return;
		}
		final Race race = opt.getRace();

		if (race == null)
		{
			return;
		}

		String nName;

		Logging
			.debugPrint("addButton:race: " + race.getDisplayName() + " -> " + target); //$NON-NLS-1$ //$NON-NLS-2$

		// first ask for the name of the new object
		Object nValue =
				JOptionPane.showInputDialog(null, PropertyFactory
					.getFormattedString("InfoResources.EnterName", target), //$NON-NLS-1$
					Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE);

		if (nValue != null)
		{
			nName = ((String) nValue).trim();
		}
		else
		{
			return;
		}

		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(PropertyFactory.getFormattedString(
			"InfoResources.SaveCaption", target, nName)); //$NON-NLS-1$
		fc.setSelectedFile(new File(SettingsHandler.getPcgPath(), nName
			+ Constants.s_PCGEN_CHARACTER_EXTENSION));
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());

		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		File file = fc.getSelectedFile();

		if (!PCGFile.isPCGenCharacterFile(file))
		{
			file =
					new File(file.getParent(), file.getName()
						+ Constants.s_PCGEN_CHARACTER_EXTENSION);
		}

		if (file.exists())
		{
			int iConfirm =
					JOptionPane
						.showConfirmDialog(
							null,
							PropertyFactory.getFormattedString(
								"InfoSpells.confirm.overwrite", file.getName()), //$NON-NLS-1$
							PropertyFactory
								.getString("in_confirmOverwriteCaption"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

			if (iConfirm != JOptionPane.YES_OPTION)
			{
				return;
			}
		}

		PlayerCharacter newPC = new PlayerCharacter();
		newPC.setName(nName);
		newPC.setFileName(file.getAbsolutePath());

		for (PCStat aStat : newPC.getStatSet())
		{
			newPC.setAssoc(aStat, AssociationKey.STAT_SCORE, 10);
		}

		newPC.setAlignment(pc.getPCAlignment());
		newPC.setRace(race);
		newPC.setDirty(true);

		CompanionList cList = target.getType();

		final Follower newMaster =
				new Follower(pc.getFileName(), pc.getName(), cList);
		newMaster.setAdjustment(opt.getAdjustment());
		newPC.setMaster(newMaster);

		final Follower newFollower =
				new Follower(file.getAbsolutePath(), nName, cList);
		newFollower.setRace(newPC.getRace());
		pc.addFollower(newFollower);
		pc.setDirty(true);
		pc.setCalcFollowerBonus(pc);

		ShowMessageDelegate.showMessageDialog(PropertyFactory
			.getFormattedString("InfoResources.SaveAndSwitch", nName), //$NON-NLS-1$
			Constants.APPLICATION_NAME, MessageType.INFORMATION);

		// save the new Follower to a file

		try
		{
			(new PCGIOHandler()).write(newPC, file.getAbsolutePath());
		}
		catch (Exception ex)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getFormattedString("Errors.Save", newPC.getDisplayName()), //$NON-NLS-1$
				Constants.APPLICATION_NAME, MessageType.ERROR);
			Logging.errorPrint(PropertyFactory.getFormattedString(
				"Errors.Save", newPC.getDisplayName()), ex); //$NON-NLS-1$
			return;
		}

		// must force an Update before switching tabs
		setNeedsUpdate(true);
		pc.calcActiveBonuses();

		// now load the new Follower from the file
		// and switch tabs
		PlayerCharacter loadedChar =
				PCGen_Frame1.getInst().loadPCFromFile(file);
		loadedChar.calcActiveBonuses();
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();
	}

	private void addFileButton()
	{
		if ("".equals(pc.getFileName())) //$NON-NLS-1$
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.SaveFirst"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.DestinationFirst"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return;
		}
		SelectedFollowerModel.FollowerType target =
			(SelectedFollowerModel.FollowerType) ((PObjectNode) selCPath
				.getPathComponent(1)).getItem();
		if (target.getNumRemaining() == 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.NoMoreFollowers"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return;
		}

		File file = null;
		file = findPCGFile();

		if ((file == null) || !file.exists())
		{
			return;
		}

		PlayerCharacter newPC = null;
		PlayerCharacter oldPC = pc;
		int oldIndex = PCGen_Frame1.getBaseTabbedPane().getSelectedIndex();
		int newIndex = PCGen_Frame1.FIRST_CHAR_TAB;

		for (PlayerCharacter iPC : Globals.getPCList())
		{
			if (iPC.getFileName().equals(file.toString()))
			{
				Logging.errorPrint("already open"); //$NON-NLS-1$
				PCGen_Frame1.getBaseTabbedPane().setSelectedIndex(newIndex);
				newPC = iPC;

				break;
			}

			newIndex++;
		}

		if (newPC == null)
		{
			newPC = PCGen_Frame1.getInst().loadPCFromFile(file);
			if (newPC == null)
			{
				Logging.errorPrint(PropertyFactory.getFormattedString(
					"Errors.Load", file.toString())); //$NON-NLS-1$

				return;
			}
		}
		CompanionList cList = target.getType();

		Follower newMaster =
				new Follower(oldPC.getFileName(), oldPC.getName(), cList);
		newPC.setMaster(newMaster);

		Follower newFollower =
				new Follower(file.getAbsolutePath(), newPC.getName(), cList);
		newFollower.setRace(newPC.getRace());
		oldPC.addFollower(newFollower);
		oldPC.setDirty(true);
		newPC.setDirty(true);
		PCGen_Frame1.getInst().savePC(oldPC, false);
		PCGen_Frame1.getInst().savePC(newPC, false);
		PCGen_Frame1.getBaseTabbedPane().setSelectedIndex(oldIndex);
		PCGen_Frame1.getInst().revertToSavedItem_actionPerformed(null);

		// must force an Update before switching tabs
		setNeedsUpdate(true);
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoInventory());
		pane.refresh();

		// we run this after the tabs have been switched
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				formComponentShown();
			}
		});
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new AvailableFollowerModel(pc, viewSortMode);
		}
		else
		{
			availableModel.resetModel(viewSortMode);
		}

		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
		}
	}

	/**
	 * This creates the GUI pane that a "follower" sees
	 * displays the followers stats and let's them update from master
	 **/
	private void createFollowerView()
	{
		followerPane.setLayout(new BorderLayout());

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		updateButton.setText(PropertyFactory
			.getString("InfoResources.UpdateFromMaster")); //$NON-NLS-1$
		updateButton.setEnabled(true);
		aPanel.add(updateButton);
		followerPane.add(aPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(followerInfo);
		TitledBorder sTitle =
				BorderFactory.createTitledBorder(PropertyFactory
					.getString("InfoResources.FollowerInformation")); //$NON-NLS-1$
		sTitle.setTitleJustification(TitledBorder.CENTER);
		scrollPane.setBorder(sTitle);
		followerInfo.setBackground(topPane.getBackground());
		scrollPane.setViewportView(followerInfo);
		scrollPane.setVisible(true);

		followerPane.add(scrollPane, BorderLayout.CENTER);
		followerPane.setVisible(true);

		// now add the entire mess (centered of course)
		//this.setLayout(new BorderLayout());
		//this.add(followerPane, BorderLayout.CENTER);
	}

	/**
	 * This creates the GUI pane that a "master" sees to allow the PC
	 * to add new followers, familiars, artifacts, etc
	 **/
	private void createMasterView()
	{
		masterPane.setLayout(new BorderLayout());
		masterPane.setBorder(BorderFactory.createEtchedBorder());

		// build topPane which will contain leftPane and rightPane
		// leftPane will have two panels and a scrollregion
		// rightPane will have one panel and a scrollregion
		topPane.setLayout(new BorderLayout());

		JPanel leftPane = new JPanel(new BorderLayout());
		JPanel rightPane = new JPanel(new BorderLayout());

		// split the left and right panes
		topSplit.setLeftComponent(leftPane);
		topSplit.setRightComponent(rightPane);
		topSplit.setOneTouchExpandable(true);
		topSplit.setDividerSize(10);
		topSplit.setBorder(BorderFactory.createEtchedBorder());

		leftPane.add(InfoTabUtils.createFilterPane(sortLabel, viewSortBox,
			lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		// the available table panel
		JScrollPane scrollPane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Utility.setDescription(scrollPane, PropertyFactory
			.getString("InfoResources.RightClickToAdd")); //$NON-NLS-1$
		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText(PropertyFactory.getString("in_caretSymbol")); //$NON-NLS-1$

		new TableColumnManager(availableTable, columnButton, availableModel);
		leftPane.add(scrollPane, BorderLayout.CENTER);
		// Centre the size and alignment columns
		int index = availableTable.convertColumnIndexToView(1);
		if (index > -1)
		{
			availableTable.setColAlign(index, SwingConstants.CENTER);
		}

		// build the left pane
		// for the available table
		JPanel bottomLeftPane =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		shouldLoadCompanion.setSelected(pc.getLoadCompanion());
		bottomLeftPane.add(shouldLoadCompanion, BorderLayout.WEST);
		addButton.setIcon(IconUtilitities.getImageIcon("Forward16.gif")); //$NON-NLS-1$
		Utility.setDescription(addButton, PropertyFactory
			.getString("InfoResources.ClickToAdd")); //$NON-NLS-1$
		addButton.setEnabled(false);
		bottomLeftPane.add(addButton);
		leftPane.add(bottomLeftPane, BorderLayout.SOUTH);

		// now build the right pane
		// for the selected table
		JScrollPane scrollPane2 =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Utility.setDescription(scrollPane2, PropertyFactory
			.getString("InfoResources.RightClickToRemove")); //$NON-NLS-1$
		JButton columnButton2 = new JButton();
		scrollPane2.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText(PropertyFactory.getString("in_caretSymbol")); //$NON-NLS-1$
		new TableColumnManager(selectedTable, columnButton2, selectedModel);
		rightPane.add(scrollPane2, BorderLayout.CENTER);

		JPanel bottomRightPane = new JPanel();
		bottomRightPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		delButton.setIcon(IconUtilitities.getImageIcon("Back16.gif")); //$NON-NLS-1$
		Utility.setDescription(delButton, PropertyFactory
			.getString("ClickToRemove")); //$NON-NLS-1$
		delButton.setEnabled(false);
		bottomRightPane.add(delButton);
		loadButton.setText(PropertyFactory
			.getString("InfoResources.LoadDlgPrompt")); //$NON-NLS-1$
		loadButton.setEnabled(false);
		bottomRightPane.add(loadButton);
		rightPane.add(bottomRightPane, BorderLayout.SOUTH);

		// add the split pane to the top panel
		topPane.add(topSplit, BorderLayout.CENTER);

		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (current object info)
		// bRightPane will contain a panel and buttons
		botPane.setLayout(new BorderLayout());

		//JPanel bLeftPane = new JPanel();
		JScrollPane bLeftPane = new JScrollPane(infoLabel);
		JPanel bRightPane = new JPanel();

		botSplit.setLeftComponent(bLeftPane);
		botSplit.setRightComponent(bRightPane);
		botSplit.setOneTouchExpandable(true);
		botSplit.setDividerSize(10);

		botPane.add(botSplit, BorderLayout.CENTER);

		// Bottom left panel
		TitledBorder sTitle =
				BorderFactory.createTitledBorder(PropertyFactory
					.getString("InfoResources.Information")); //$NON-NLS-1$
		sTitle.setTitleJustification(TitledBorder.CENTER);
		bLeftPane.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		bLeftPane.setViewportView(infoLabel);

		// Bottom right panel
		// create a template select and view panel
		JPanel iPanel = new JPanel();
		iPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		addModButton.setText(PropertyFactory
			.getString("InfoResources.AddModifier")); //$NON-NLS-1$
		addModButton.setEnabled(false);
		iPanel.add(addModButton);
		delModButton.setText(PropertyFactory
			.getString("InfoResources.DelModifier")); //$NON-NLS-1$
		delModButton.setEnabled(false);
		iPanel.add(delModButton);
		bRightPane.add(iPanel);

		//
		// now split the top and bottom Panels
		centerSplit.setTopComponent(topPane);
		centerSplit.setBottomComponent(botPane);
		centerSplit.setOneTouchExpandable(true);
		centerSplit.setDividerSize(10);

		// Now add centerSplit (which has top and bottom splits)
		masterPane.add(centerSplit, BorderLayout.CENTER);

		// now add the entire mess (centered of course)
		//this.setLayout(new BorderLayout());
		//this.add(masterPane, BorderLayout.CENTER);
	}

	/**
	 * Given a Follower and PC, return string of vital stats
	 * @param aF
	 * @param newPC
	 * @return follower stat block
	 **/
	private static String followerStatBlock(Follower aF, PlayerCharacter newPC)
	{
		StringBuffer b = new StringBuffer();
		b.append(FONT_PLUS_1).append(BOLD);
		b.append(PropertyFactory.getString("InfoResources.NameLabel")); //$NON-NLS-1$
		b.append(END_BOLD).append(" "); //$NON-NLS-1$
		b.append(newPC.getName()).append(END_FONT);
		b.append(BR).append(BOLD);
		b.append(PropertyFactory.getString("InfoResources.TypeLabel")); //$NON-NLS-1$
		b.append(END_BOLD).append(" ").append(aF.getType()); //$NON-NLS-1$
		b.append(BR).append(BOLD);
		b.append(PropertyFactory.getString("in_sumRace")); //$NON-NLS-1$
		b.append(END_BOLD).append(" ").append(newPC.getRace()); //$NON-NLS-1$
		b.append(BR);

		for (PCStat stat : Globals.getContext().ref.getOrderSortedCDOMObjects(PCStat.class))
		{
			b.append(BOLD).append(stat.getAbb())
					.append(END_BOLD).append(": ") //$NON-NLS-1$
					.append(StatAnalysis.getTotalStatFor(newPC, stat)).append(
							" "); //$NON-NLS-1$
		}

		b.append(BR);

		if (Globals.getGameModeACText().length() != 0)
		{
			b.append(BOLD).append(Globals.getGameModeACText()).append(END_BOLD)
				.append(" "); //$NON-NLS-1$
			b.append(ITALIC).append(PropertyFactory.getString("in_sumTotal")); //$NON-NLS-1$
			b.append(END_ITALIC).append(": ").append(newPC.getACTotal()); //$NON-NLS-1$
			b.append(" ").append(ITALIC); //$NON-NLS-1$
			b.append(PropertyFactory.getString("in_sumFlatfooted")); //$NON-NLS-1$
			b.append(END_ITALIC).append(": ").append(newPC.flatfootedAC()); //$NON-NLS-1$
			b.append(" ").append(ITALIC); //$NON-NLS-1$
			b.append(PropertyFactory.getString("in_sumTouch")); //$NON-NLS-1$
			b.append(END_ITALIC).append(": ").append(newPC.touchAC()); //$NON-NLS-1$
			b.append(BR);
		}
		else
		{
			b
				.append(BOLD)
				.append(PropertyFactory.getString("in_sumAC")).append(END_BOLD).append(" "); //$NON-NLS-1$//$NON-NLS-2$
			b
				.append(ITALIC)
				.append(PropertyFactory.getString("in_sumTotal")).append(END_ITALIC).append(": "); //$NON-NLS-1$//$NON-NLS-2$
			b.append((int) newPC.getTotalBonusTo("COMBAT", "AC")); //$NON-NLS-1$ //$NON-NLS-2$
			b.append(BR);
		}

		final int initMod = newPC.initiativeMod();
		b
			.append(BOLD)
			.append(PropertyFactory.getString("in_sumInit")).append(END_BOLD).append(": "); //$NON-NLS-1$//$NON-NLS-2$
		b
			.append(
				(initMod >= 0) ? PropertyFactory.getString("in_plusSign") : "").append(initMod); //$NON-NLS-1$ //$NON-NLS-2$
		b.append(BR);

		int bonus = newPC.baseAttackBonus();
		String babAbbrev = SettingsHandler.getGame().getBabAbbrev();
		if (babAbbrev == null)
		{
			babAbbrev = "BAB"; //$NON-NLS-1$
		}
		b.append(BOLD).append(babAbbrev).append(END_BOLD).append(": "); //$NON-NLS-1$
		b
			.append(
				(bonus >= 0) ? PropertyFactory.getString("in_plusSign") : "").append(bonus); //$NON-NLS-1$ //$NON-NLS-2$
		b.append(BR);
		b
			.append(" ").append(BOLD).append(Globals.getGameModeHPAbbrev()).append(END_BOLD).append(": ").append(newPC.hitPoints()); //$NON-NLS-1$//$NON-NLS-2$

		if (Globals.getGameModeAltHPText().length() != 0)
		{
			b
				.append(" ").append(BOLD).append(Globals.getGameModeAltHPAbbrev()).append(END_BOLD).append(": ").append(newPC.altHP()); //$NON-NLS-1$//$NON-NLS-2$
		}

		b.append(BR);

		b
			.append(BOLD)
			.append(PropertyFactory.getString("in_sumSaves")).append(END_BOLD).append(": "); //$NON-NLS-1$ //$NON-NLS-2$

		List<PCCheck> checkList = Globals.getContext().ref
				.getOrderSortedCDOMObjects(PCCheck.class);
		for (PCCheck check : checkList)
		{
			bonus = newPC.getTotalCheck(check);
			b
					.append(" ").append(ITALIC).append(check.toString()).append(END_ITALIC).append(": ") //$NON-NLS-1$ //$NON-NLS-2$
					.append(
							(bonus >= 0) ? PropertyFactory
									.getString("in_plusSign") : "").append(bonus); //$NON-NLS-1$//$NON-NLS-2$
		}

		b.append(BR);
		bonus = newPC.getSR();

		if (bonus > 0)
		{
			b
				.append(BOLD)
				.append(PropertyFactory.getString("in_demSpellResistance")).append(END_BOLD).append(": "); //$NON-NLS-1$ //$NON-NLS-2$
			b.append(newPC.getSR());
		}

		b.append(BR);
		b
			.append(BOLD)
			.append(PropertyFactory.getString("in_specialAb")).append(":").append(END_BOLD); //$NON-NLS-1$//$NON-NLS-2$
		b.append(UL);

		for (String sa : newPC.getSpecialAbilityListStrings())
		{
			b.append(LI).append(sa).append(END_LI);
		}

		b.append(END_UL);
		b.append(BR);

		return b.toString();
	}

	/*
	 * set the Info text in the Info panel
	 * to the currently selected object
	 */
	private void setInfoText(Object obj)
	{
		if (obj == null)
		{
			return;
		}

		Race aRace = null;
		FollowerOption option = null;
		if (obj instanceof Race)
		{
			aRace = (Race) obj;
		}
		else if (obj instanceof SourcedFollower)
		{
			SourcedFollower sf = (SourcedFollower) obj;
			option = sf.option;
			aRace = option.getRace();
		}
		if (aRace != null)
		{
			if (aRace.getKeyName().startsWith(Constants.NONESELECTED))
			{
				return;
			}

			final InfoLabelTextBuilder b =
					new InfoLabelTextBuilder(aRace.getDisplayName());
			b.append(": "); //$NON-NLS-1$
			Formula sz = aRace.get(FormulaKey.SIZE);
			if (sz != null)
			{
				String str = sz.toString();
				final SizeAdjustment sadj = Globals.getContext().ref
						.getAbbreviatedObject(SizeAdjustment.class, str);
				if (sadj != null)
				{
					str = sadj.getDisplayName();
				}
				b.append(str);
			}
			RaceType rt = aRace.get(ObjectKey.RACETYPE);
			if (rt != null)
			{
				b.append(" ").append(rt.toString()); //$NON-NLS-1$
			}

			b.appendLineBreak();
			String bString = "";
			List<Movement> movements = aRace.getListFor(ListKey.MOVEMENT);
			if (movements != null && !movements.isEmpty())
			{
				bString = movements.get(0).toString();
			}
			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_move", bString); //$NON-NLS-1$
			}


			if (option != null && PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			option.getPrerequisiteList(), true).length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_requirements", //$NON-NLS-1$
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
					option.getPrerequisiteList(), true));
			}
			
			bString = SourceFormat.getFormattedString(aRace,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_ieInfoLabelTextCostSource", bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
		else if (obj instanceof Follower)
		{
			Follower aF = (Follower) obj;
			PlayerCharacter newPC = null;

			for (PlayerCharacter nPC : Globals.getPCList())
			{
				if (aF.getFileName().equals(nPC.getFileName()))
				{
					newPC = nPC;
				}
			}

			if (newPC == null)
			{

				infoLabel.setText(PropertyFactory
					.getString("InfoResources.LoadFromFile")); //$NON-NLS-1$

				return;
			}

			StringBuffer b = new StringBuffer();
			b.append(HTML);
			b.append(followerStatBlock(aF, newPC));
			b.append(END_HTML);
			infoLabel.setText(b.toString());
		}
	}

	private static int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
				(DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	/**
	 * Creates the FollowerModels
	 **/
	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new SelectedFollowerModel(pc);
		}
		else
		{
			selectedModel.resetModel();
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
		}
	}

	private class AvailableClickHandler implements ClickHandler
	{
		/**
		 * Handles a single click event for the available panel.  Does nothing.
		 * @see pcgen.gui.utils.ClickHandler#singleClickEvent()
		 */
		public void singleClickEvent()
		{
			// Do Nothing
		}

		/**
		 * Handles double click event for the available panel.  Adds the clicked
		 * resource to the selected type.
		 * @see pcgen.gui.utils.ClickHandler#doubleClickEvent()
		 */
		@SuppressWarnings("synthetic-access")
		public void doubleClickEvent()
		{
			addButton();
		}

		/**
		 * Check if this Object is allowed to be selected.
		 * @return true if the clicked object can be selected.
		 * @see pcgen.gui.utils.ClickHandler#isSelectable(java.lang.Object)
		 */
		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private class SelectedClickHandler implements ClickHandler
	{
		/**
		 * Handles a single click event for the selected panel.  Does nothing.
		 * @see pcgen.gui.utils.ClickHandler#singleClickEvent()
		 */
		public void singleClickEvent()
		{
			// Do Nothing
		}

		/**
		 * Handles double click event for the selected panel.  Adds the clicked
		 * resource to the selected type.
		 * @see pcgen.gui.utils.ClickHandler#doubleClickEvent()
		 */
		@SuppressWarnings("synthetic-access")
		public void doubleClickEvent()
		{
			addFileButton();
		}

		/**
		 * Check if this Object is allowed to be selected.
		 * @return true if the clicked object can be selected.
		 * @see pcgen.gui.utils.ClickHandler#isSelectable(java.lang.Object)
		 */
		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private final void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

						final Object temp =
								atree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							infoLabel.setText();

							return;
						}

						PObjectNode fNode = (PObjectNode) temp;

						if (fNode.getItem() != null)
						{
							addButton.setEnabled(true);
							setInfoText(fNode.getItem());
						}
					}
				}
			});

		// now do the selectedTable and selectedTree
		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree stree = selectedTable.getTree();
		stree.setRootVisible(false);
		stree.setShowsRootHandles(true);
		stree.setCellRenderer(new LabelTreeCellRenderer());

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

						final Object temp =
								stree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							return;
						}

						PObjectNode fN = (PObjectNode) temp;

						if ((fN.getItem() != null)
							&& !(fN.getItem() instanceof String))
						{
							delButton.setEnabled(true);
							loadButton.setEnabled(true);
							setInfoText(fN.getItem());

							return;
						}
						infoLabel.setText(PropertyFactory.getString("in_none")); //$NON-NLS-1$
						loadButton.setEnabled(false);
						return;
					}
				}
			});

		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), false));
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), false));

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	/**
	 * removes an item from the selected table
	 **/
	private void delButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.SelectToRemove"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		int iConfirm =
				JOptionPane
					.showConfirmDialog(
						null,
						PropertyFactory
							.getString("InfoResources.ConfirmDelete"), PropertyFactory.getString("InfoResources.ConfirmRemove"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.YES_NO_OPTION);

		if (iConfirm != JOptionPane.YES_OPTION)
		{
			return;
		}

		if (fNode.getItem() instanceof Follower)
		{
			pc.delFollower((Follower) fNode.getItem());
		}
		else if (fNode.getItem() instanceof Equipment)
		{
			pc.removeEquipment((Equipment) fNode.getItem());
		}
		else
		{
			return;
		}

		pc.setDirty(true);
		updateSelectedModel();
	}

	/**
	 * Prompt the user to find the Followers .pcg file
	 * @return PCG File
	 **/
	private File findPCGFile()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(PropertyFactory.getString("InfoResources.FindFile")); //$NON-NLS-1$
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());

		if (fc.showOpenDialog(InfoResources.this) != JFileChooser.APPROVE_OPTION)
		{
			return null;
		}

		final File file = fc.getSelectedFile();

		if (file.exists() && file.canWrite())
		{
			return file;
		}

		return null;
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		refresh();

		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(PropertyFactory
			.getString("InfoResources.AddFollowersEtc")); //$NON-NLS-1$

		int top = topSplit.getDividerLocation();
		int bot = botSplit.getDividerLocation();
		int cent = centerSplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;

			Component c = getParent();
			top =
					SettingsHandler.getPCGenOption(
						"InfoResources.topSplit", ((c.getWidth() * 7) / 10)); //$NON-NLS-1$
			bot =
					SettingsHandler.getPCGenOption(
						"InfoResources.botSplit", (c.getWidth() - 300)); //$NON-NLS-1$
			cent =
					SettingsHandler.getPCGenOption(
						"InfoResources.centerSplit", (c.getHeight() - 100)); //$NON-NLS-1$

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ResSel", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					selectedTable, "ResSel", i)); //$NON-NLS-1$
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ResAva", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "ResAva", i)); //$NON-NLS-1$
			}
		}

		if (top > 0)
		{
			topSplit.setDividerLocation(top);
			SettingsHandler.setPCGenOption("InfoResources.topSplit", top); //$NON-NLS-1$
		}

		if (bot > 0)
		{
			botSplit.setDividerLocation(bot);
			SettingsHandler.setPCGenOption("InfoResources.botSplit", bot); //$NON-NLS-1$
		}

		if (cent > 0)
		{
			centerSplit.setDividerLocation(cent);
			SettingsHandler.setPCGenOption("InfoResources.centerSplit", cent); //$NON-NLS-1$
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new resPopupListener(treeTable,
			new ResourcesPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(@SuppressWarnings("unused")
			ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		topSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		botSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		centerSplit.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				addButton();
			}
		});
		delButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				delButton();
			}
		});
		loadButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				loadButton();
			}
		});
		updateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				updateButton();
			}
		});

		/*
		 addModButton.addActionListener(new ActionListener()
		 {
		 public void actionPerformed(ActionEvent evt)
		 {
		 addModButton(evt);
		 }
		 });
		 delModButton.addActionListener(new ActionListener()
		 {
		 public void actionPerformed(ActionEvent evt)
		 {
		 delModButton(evt);
		 }
		 });
		 */
		viewSortBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				viewSortBoxActionPerformed();
			}
		});
		shouldLoadCompanion.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				pc.setLoadCompanion(shouldLoadCompanion.isSelected());
			}
		});
		textQFilter.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(@SuppressWarnings("unused")
			DocumentEvent evt)
			{
				setQFilter();
			}

			public void insertUpdate(@SuppressWarnings("unused")
			DocumentEvent evt)
			{
				setQFilter();
			}

			public void removeUpdate(@SuppressWarnings("unused")
			DocumentEvent evt)
			{
				setQFilter();
			}
		});
		clearQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				clearQFilter();
			}
		});
	}

	private void saveDividerLocations()
	{
		if (!hasBeenSized)
		{
			return;
		}

		int s = topSplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoResources.topSplit", s); //$NON-NLS-1$
		}

		s = botSplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoResources.botSplit", s); //$NON-NLS-1$
		}

		s = centerSplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoResources.centerSplit", s); //$NON-NLS-1$
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
	private void initComponents()
	{
		readyForRefresh = true;

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		viewSortBox.addItem(PropertyFactory.getString("in_nameLabel") + "   "); //$NON-NLS-1$ //$NON-NLS-2$
		viewSortBox.addItem(PropertyFactory.getString("in_adjustment") + "   "); //$NON-NLS-1$//$NON-NLS-2$
		viewSortBox
			.addItem(PropertyFactory.getString("in_racetypeName") + "   "); //$NON-NLS-1$ //$NON-NLS-2$

		//viewSelectComboBox.setSelectedIndex(viewSelectMode);
		// create both versions of the GUI
		this.setLayout(new BorderLayout());
		createMasterView();
		createFollowerView();

		// add the sorter tables to that clicking
		// on the TableHeader does something
		availableSort =
				new JTreeTableSorter(availableTable,
					(PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort =
				new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel
					.getRoot(), selectedModel);
		addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(@SuppressWarnings("unused")
			FocusEvent evt)
			{
				refresh();
			}
		});

		for (int iRow = 0; iRow < availableTable.getRowCount(); iRow++)
		{
			final JTree tree = availableTable.getTree();
			TreePath iPath = tree.getPathForRow(iRow);

			if (iPath != null)
			{
				tree.makeVisible(iPath);
				tree.expandPath(iPath);
			}
		}
	}

	private void clearQFilter()
	{
		availableModel.clearQFilter();
		if (saveViewMode != null)
		{
			viewSortMode = saveViewMode.intValue();
			saveViewMode = null;
		}
		textQFilter.setText(""); //$NON-NLS-1$
		availableModel.resetModel(viewSortMode);
		clearQFilterButton.setEnabled(false);
		viewSortBox.setEnabled(true);
		updateAvailableModel();
	}

	private void setQFilter()
	{
		String aString = textQFilter.getText();

		if (aString.length() == 0)
		{
			clearQFilter();
			return;
		}
		availableModel.setQFilter(aString);

		if (saveViewMode == null)
		{
			saveViewMode = Integer.valueOf(viewSortMode);
		}
		viewSortMode = 1;
		availableModel.resetModel(viewSortMode);
		clearQFilterButton.setEnabled(true);
		viewSortBox.setEnabled(false);
		updateAvailableModel();
	}

	/**
	 * load Follower from .pcg file and create new tab
	 **/
	private void loadButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("InfoResources.SelectObjectLoad"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (fNode.getItem() instanceof Follower)
		{
			Follower aF = (Follower) fNode.getItem();

			if (aF == null)
			{
				return;
			}

			// now search the list of PC's to make sure we are
			// not already loaded
			for (PlayerCharacter nPC : Globals.getPCList())
			{
				if (aF.getFileName().equals(nPC.getFileName()))
				{
					ShowMessageDelegate
						.showMessageDialog(
							PropertyFactory.getFormattedString(
								"InfoResources.AlreadyLoaded", aF.getName()), Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$

					return;
				}
			}

			// Get the .pcg filename to load
			File file = new File(aF.getFileName());

			// Make sure file exists
			if (!file.exists())
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory
					.getFormattedString(
						"InfoResources.MovedChanged", aF.getFileName()), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.INFORMATION);

				// not there, so see if the user can find it
				Logging.errorPrint("b File: " + file.getAbsolutePath()); //$NON-NLS-1$
				file = findPCGFile();

				// still not found, just bail
				if (file == null)
				{
					return;
				}

				Logging.errorPrint("a File: " + file.getAbsolutePath()); //$NON-NLS-1$
			}

			// Followers .pcg filename/location may
			// have changed so make sure to update
			aF.setFileName(file.getAbsolutePath());
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory
						.getFormattedString(
							"InfoResources.LoadAndSwitch", aF.getName(), aF.getFileName()), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.INFORMATION);

			// now load the Follower from
			// the file and switch tabs
			PCGen_Frame1.getInst().loadPCFromFile(file);

			// must force an Update after switching tabs
			setNeedsUpdate(true);
			CharacterInfo pane = PCGen_Frame1.getCharacterPane();
			pane.setPaneForUpdate(pane.infoSummary());
			pane.refresh();

			// we run this after the tabs have been switched
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					formComponentShown();
				}
			});

			return;
		}
		else if (fNode.getItem() instanceof Equipment)
		{
			//pc.getEquipment((Equipment)fNode.getItem());
		}

		pc.setDirty(true);
		updateSelectedModel();
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		List<String> pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * Update Follower if master has been loaded
	 **/
	private void updateButton()
	{
		if (pc.getMaster() != null)
		{
			pc.setMaster(pc.getMaster());
			CharacterInfo pane = PCGen_Frame1.getCharacterPane();
			pane.setPaneForUpdate(pane.infoSkills());
			pane.setPaneForUpdate(pane.infoSummary());
			pane.setPaneForUpdate(pane.infoClasses());
			pane.setPaneForUpdate(pane.infoSpells());
			pane.setPaneForUpdate(pane.infoInventory());
			pane.refresh();

			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getFormattedString(
						"InfoResources.DoneUpdating", pc.getName()), Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$
		}

		pc.setDirty(true);
		setFollowerInfo(pc);
	}

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		List<String> pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	private void viewSortBoxActionPerformed()
	{
		final int index = viewSortBox.getSelectedIndex();

		if (index != viewSortMode)
		{
			viewSortMode = index;

			//SettingsHandler.setResourceTab_SortMode(viewSelectMode);
			updateAvailableModel();
		}
	}

	/**
	 * create right click menus and listeners
	 **/
	private class ResourcesPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 7236403406005940947L;
		private String lastSearch = ""; //$NON-NLS-1$

		// TODO Change the shortcut handling to not pass strings.
		ResourcesPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				ResourcesPopupMenu.this.add(createAddMenuItem(
					"InfoResources.AddNewToList", "shortcut EQUALS")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				ResourcesPopupMenu.this
					.add(Utility
						.createMenuItem(
							PropertyFactory.getString("InfoResources.FindItem"), //$NON-NLS-1$
							new ActionListener()
							{
								public void actionPerformed(
									@SuppressWarnings("unused")
									ActionEvent e)
								{
									lastSearch =
											availableTable
												.searchTree(lastSearch);
								}
							},
							"searchItem", (char) 0, "shortcut F", PropertyFactory.getString("InfoResources.FindItem"), null, true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			else
			// selectedTable
			{
				ResourcesPopupMenu.this.add(createDelMenuItem(
					"InfoResources.RemoveFromList", "shortcut MINUS")); //$NON-NLS-1$ //$NON-NLS-2$
				ResourcesPopupMenu.this.add(createAddFileMenuItem(
					"InfoResources.AddFromFile", "shortcut PLUS")); //$NON-NLS-1$//$NON-NLS-2$
				this.addSeparator();
				ResourcesPopupMenu.this
					.add(Utility
						.createMenuItem(
							PropertyFactory.getString("InfoResources.FindItem"), //$NON-NLS-1$
							new ActionListener()
							{
								public void actionPerformed(
									@SuppressWarnings("unused")
									ActionEvent e)
								{
									lastSearch =
											selectedTable
												.searchTree(lastSearch);
								}
							},
							"searchItem", (char) 0, "shortcut F", PropertyFactory.getString("InfoResources.FindItem"), null, true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		private JMenuItem createAddFileMenuItem(String label, String accelerator)
		{
			return Utility
				.createMenuItem(
					PropertyFactory.getString(label),
					new AddFileActionListener(),
					"add 1", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoResources.AddExistingFile"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility
				.createMenuItem(
					PropertyFactory.getString(label),
					new AddActionListener(),
					"add 1", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoResources.AddToList"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			return Utility
				.createMenuItem(
					PropertyFactory.getString(label),
					new DelActionListener(),
					"remove 1", (char) 0, accelerator, //$NON-NLS-1$
					PropertyFactory.getString("InfoResources.RemoveFromList"), "Remove16.gif", true); //$NON-NLS-1$//$NON-NLS-2$
		}

		private class AddActionListener implements ActionListener
		{
			/**
			 * Called when Add Menu Item is selected
			 * @param evt Not Used
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				addButton();
			}
		}

		private class AddFileActionListener implements ActionListener
		{
			/**
			 * Called when Add Existing Item menu is selected
			 * @param evt Not Used
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				addFileButton();
			}
		}

		private class DelActionListener implements ActionListener
		{
			/**
			 * Called when Remove Item menu is selected.
			 * @param evt Not Used
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				delButton();
			}
		}
	}

	private class resPopupListener extends MouseAdapter
	{
		private JTree tree;
		private ResourcesPopupMenu menu;

		resPopupListener(JTreeTable treeTable, ResourcesPopupMenu aMenu)
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
				// Walk through the list of accelerators to see
				// if the user has pressed a sequence used by
				// the popup. This would not otherwise happen
				// unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final Component menuComponent =
									menu.getComponent(i);

							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks =
										((JMenuItem) menuComponent)
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

		/**
		 * Called when the mouse button is pressed.  We might show the popup.
		 * @param evt Info about button press
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		/**
		 * Called when the mouse button is released.  We might show the popup.
		 * @param evt Info about the button press
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selPath =
						tree.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

}
