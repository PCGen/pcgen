/*
 * InfoFeat.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied waarranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on December 29, 2001, 6:57 PM
 *
 * Current Ver: $Revision: 1.97 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 13:54:42 $
 *
 */
package pcgen.gui.tabs;

import pcgen.core.*;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.*;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * <code>InfoFeats</code>.
 * This class is responsible for drawing the feat related window - including
 * indicating what feats are available, which ones are selected, and handling
 * the selection/de-selection of feats
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.97 $
 * TODO I18N
 */
public final class InfoFeats extends FilterAdapterPanel implements CharacterInfoTab
{
	private static final String NO_QUALIFY_MESSAGE = "You do not meet the prerequisites required to take this " + getSingularTabName() + ".";
	private static final String DUPLICATE_MESSAGE  = "You already have that " + getSingularTabName() + ".";
	private static String FEAT_FULL_MESSAGE  = "You cannot select any more " + getSingularTabName() + "s.";
	private static boolean needsUpdate = true;
	private static PObjectNode typeRoot = new PObjectNode();
	private static PObjectNode preReqTreeRoot = null;
	private static PObjectNode sourceRoot = new PObjectNode();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	private static String singularName = null;

	// keep track of what view mode we're in for Available
	private static int viewAvailMode = GuiConstants.INFOFEATS_VIEW_TYPENAME;
	private static Integer saveAvailableViewMode = null;

	// default to "Name" in Selection table viewmode
	private static int viewSelectMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;
	private static Integer saveSelectedViewMode = null;

	private static final int HASABILITY_NO        = 0;
	private static final int HASABILITY_CHOSEN    = 1;
	private static final int HASABILITY_AUTOMATIC = 2;
	private static final int HASABILITY_VIRTUAL      = 4;
	static final int FEAT_OK = 0;
	static final int FEAT_DUPLICATE = 1;
	static final int FEAT_NOT_QUALIFIED = 2;
	static final int FEAT_FULL_FEAT = 3;
	private FeatModel availableModel = null; // Model for the JTreeTable.
	private FeatModel selectedModel = null; // Model for the JTreeTable.
	private FlippingSplitPane splitBotLeftRight;
	private FlippingSplitPane splitTopBot;
	private FlippingSplitPane splitTopLeftRight;
	private JButton addButton;
	private JButton leftButton;
	private JButton clearAvailableQFilterButton = new JButton("Clear");
	private JButton clearSelectedQFilterButton = new JButton("Clear");
	private JButton setAvailableQFilterButton = new JButton("Set");
	private JButton setSelectedQFilterButton = new JButton("Set");
	private JComboBoxEx viewAvailComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private JCheckBox chkViewAll = new JCheckBox();
	private JLabelPane infoLabel = new JLabelPane();
	private final JLabel lblAvailableQFilter = new JLabel("QuickFilter:");
	private final JLabel lblSelectedQFilter  = new JLabel("QuickFilter:");
	private JLabel featsRemainingLabel = new JLabel();
	private JMenuItem addMenu;
	private JMenuItem removeMenu;
	private JPanel topPane = new JPanel();
	private JScrollPane infoScroll = new JScrollPane();
	private JTextField numFeatsField = new JTextField();
	private JTextField textAvailableQFilter = new JTextField();
	private JTextField textSelectedQFilter = new JTextField();
	private JTreeTable availableTable; // the available Feats
	private JTreeTable selectedTable; // the selected Feats
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoFeats(PlayerCharacter pc)
	{
		this.pc = pc;
		// we will use the name to save component
		// specific settings in the options.ini file
		setName(Constants.tabNames[Constants.TAB_ABILITIES]);

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
		return SettingsHandler.getPCGenOption(".Panel.Feats.Order", Constants.TAB_ABILITIES);
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Feats.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(Constants.TAB_ABILITIES);
	}

	private static String getSingularTabName()
	{
		if (singularName == null)
		{
			singularName = SettingsHandler.getGame().getSingularTabName(Constants.TAB_ABILITIES);
		}
		return singularName;
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(Constants.TAB_ABILITIES);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List getToDos()
	{
		List toDoList = new ArrayList();
		if (pc != null && pc.getFeats() > 0.0d)
		{
			toDoList.add(PropertyFactory.getFormattedString("in_featTodoRemain", getSingularTabName())); //$NON-NLS-1$
		}
		else if (pc != null && pc.getFeats() < 0.0d)
		{
			toDoList.add(PropertyFactory.getFormattedString("in_featTodoTooMany", getSingularTabName())); //$NON-NLS-1$
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
			PObjectNode.resetPC(getPc());
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
	 * @param flag 
	 * @deprecated Unused -remove 5.9.5
	 */
	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 */
	public int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllFeatFilters(this);

		setKitFilter("FEAT");
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		forceRefresh();
	}

	private static int getEventSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private void setAddEnabled(boolean enabled)
	{
		addButton.setEnabled(enabled);
		addMenu.setEnabled(enabled);
	}

	private void setRemoveEnabled(boolean enabled)
	{
		leftButton.setEnabled(enabled);
		removeMenu.setEnabled(enabled);
	}

	private void addFeat()
	{
		//final String aString =
		// availableTable.getTree().getLastSelectedPathComponent().toString();
		String aString = null;
		Object temp = availableTable.getTree().getLastSelectedPathComponent();

		if (temp == null)
		{
			ShowMessageDelegate.showMessageDialog("Somehow, no " + getSingularTabName() + " was selected. Try again.", Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();

			if (temp instanceof Ability)
			{
				aString = ((Ability) temp).getName();
			}
		}

		//
		// Make sure we are dealing with a Feat object (double clicking on an expandable node can get here)
		//
		if (!(temp instanceof Ability))
		{
			return;
		}

		//final Feat aFeat = pc.getFeatNamed(aString);
		final int fq = checkFeatQualify((Ability) temp);

		switch (fq)
		{
			case FEAT_NOT_QUALIFIED:
				ShowMessageDelegate.showMessageDialog(NO_QUALIFY_MESSAGE, Constants.s_APPNAME, MessageType.INFORMATION);

				return;

			case FEAT_DUPLICATE:
				ShowMessageDelegate.showMessageDialog(DUPLICATE_MESSAGE, Constants.s_APPNAME, MessageType.INFORMATION);

				return;

			case FEAT_FULL_FEAT:
				ShowMessageDelegate.showMessageDialog(FEAT_FULL_MESSAGE, Constants.s_APPNAME, MessageType.INFORMATION);

				return;

			case FEAT_OK:

				// Feat is OK, so do nothing
				break;

			default:
				Logging.errorPrint(getSingularTabName() + " " + ((Ability) temp).getName() + " is somehow in state " + fq
					+ " which is not handled" + " in InfoFeats.addFeat()");

				break;
		}

		// we can only be here if the PC can add the feat
		try
		{
			pc.setDirty(true);

			// modFeat(featName, adding_feat, adding_all_selections)
			AbilityUtilities.modFeat(pc, null, aString, true, false);
		}
		catch (Exception exc)
		{
			ShowMessageDelegate.showMessageDialog("InfoFeats1: " + exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		// update the skills tab, as feats could effect totals
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoInventory());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();

		pc.aggregateFeatList();
		updateAvailableModel();
		updateSelectedModel();

//		selectedTable.getColumnModel().getColumn(0).setHeaderValue(getSingularTabName() + " (" + pc.getUsedFeatCount() + ")");
		selectedTable.getColumnModel().getColumn(0).setHeaderValue(getSingularTabName() + "s (" + BigDecimalHelper.trimBigDecimal(new BigDecimal(pc.getUsedFeatCount())).toString() + ")");

		setAddEnabled(false);
		pc.calcActiveBonuses();

		showRemainingFeatPoints();
	}

	private int checkFeatQualify(Ability anAbility)
	{
		String aString = anAbility.getName();
		anAbility = pc.getRealFeatNamed(aString);

		final boolean pcHasIt = (anAbility != null);

		if (pcHasIt && !anAbility.isMultiples())
		{
			return FEAT_DUPLICATE;
		}

		if (!pcHasIt)
		{
			anAbility = Globals.getAbilityNamed("FEAT", aString);
			if (
				anAbility != null &&
				!PrereqHandler.passesAll( anAbility.getPreReqList(), pc, anAbility ) &&
				!Globals.checkRule(RuleConstants.FEATPRE))
			{
				return FEAT_NOT_QUALIFIED;
			}
		}

		if (!pcHasIt && (anAbility != null))
		{
			if (anAbility.getCost(pc) > pc.getFeats())
			{
				return FEAT_FULL_FEAT;
			}
		}

		return FEAT_OK;
	}

	private void createModelAvailable()
	{
		if (availableModel == null)
		{
			availableModel = new FeatModel(viewAvailMode, true);
		}
		else
		{
			availableModel.resetModel(viewAvailMode, true, false);
		}

		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
		}
	}

	private void createModelSelected()
	{
		if (selectedModel == null)
		{
			selectedModel = new FeatModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false, chkViewAll.isSelected());
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
		}
	}

	/**
	 * Creates the FeatModel that will be used.
	 */
	private void createModels()
	{
		createModelAvailable();
		createModelSelected();
	}

	private class AvailableClickHandler implements ClickHandler
	{
		public void singleClickEvent() {
			// Do Nothing
		}
		
		public void doubleClickEvent()
		{
			// We run this after the event has been processed so that
			// we don't confuse the table when we change its contents
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						addFeat();
					}
				});
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
			// We run this after the event has been processed so that
			// we don't confuse the table when we change its contents
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						removeFeat();
					}
				});
		}
		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	/**
	 * This creates the JTreeTables for the available and selected feats
	 * It also creates the actions associated with the objects.
	 */
	private void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						//final String aString =
						//availableTable.getTree().getLastSelectedPathComponent().toString();
						/////////////////////////
						// Byngl Feb 20/2002
						// fix bug with displaying incorrect info when use cursor keys to
						// navigate the tree
						//
						//Object temp =
						//  availableTable.getTree().getLastSelectedPathComponent();
						final int idx = getEventSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();

						/////////////////////////
						if (temp == null)
						{
							ShowMessageDelegate.showMessageDialog("Somehow, no " + getSingularTabName() + " was selected. Try again.", Constants.s_APPNAME,
								MessageType.ERROR);

							return;
						}

						Ability aFeat = null;

						if (temp instanceof PObjectNode)
						{
							temp = ((PObjectNode) temp).getItem();

							if (temp instanceof Ability)
							{
								aFeat = (Ability) temp;
							}
						}

						if (SettingsHandler.isExpertGUI())
						{
							setAddEnabled((aFeat != null) && (checkFeatQualify(aFeat) == FEAT_OK));
						}
						else
						{
							setAddEnabled(aFeat != null);
						}

						if (aFeat != null)
						{
							StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.piSubString())
								.append("</b> &nbsp;TYPE:").append(aFeat.getTypeUsingFlag(true));

							if (!aFeat.getCostString().equals("1"))
							{
								bString.append(" <b>Cost:</b>").append(aFeat.getCostString());
							}

							if (aFeat.isMultiples())
							{
								bString.append(" &nbsp;Can be taken more than once");
							}

							if (aFeat.isStacks())
							{
								bString.append(" &nbsp;Stacks");
							}

							final String cString = aFeat.preReqHTMLStrings(pc, false);

							if (cString.length() > 0)
							{
								bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
							}

							bString.append(" &nbsp;<b>Description</b>:").append(aFeat.piDescSubString())
							.append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
							infoLabel.setText(bString.toString());
						}
					}
				}
			});

		final JTree tree = availableTable.getTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());

		availableTable.addMouseListener(new JTreeTableMouseAdapter(availableTable, new AvailableClickHandler(), false));

		selectedTable = new JTreeTable(selectedModel);

		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());

		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						/////////////////////////
						// Byngl Feb 20/2002
						// fix bug with displaying incorrect info when use cursor
						// keys to navigate the tree
						//
						final int idx = getEventSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						Object temp = btree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							return;
						}

						/////////////////////////
						boolean removeAllowed = false;
						Ability aFeat = null;

						if (temp instanceof PObjectNode)
						{
							temp = ((PObjectNode) temp).getItem();

							if (temp instanceof Ability)
							{
								aFeat = (Ability) temp;
								removeAllowed = aFeat.getFeatType() == Ability.ABILITY_NORMAL;

								//final Feat autoFeat =
								//  pc.getFeatAutomaticNamed(aFeat.getName());
								//removeAllowed = !Globals.featsMatch(aFeat, autoFeat);
							}
						}

						setRemoveEnabled(removeAllowed);

						if (aFeat != null)
						{
							StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.piSubString())
								.append("</b> &nbsp;TYPE:").append(aFeat.getType());

							if (!aFeat.getCostString().equals("1"))
							{
								bString.append(" <b>Cost:</b>").append(aFeat.getCostString());
							}

							if (aFeat.isMultiples())
							{
								bString.append(" &nbsp;Can be taken more than once");
							}

							if (aFeat.isStacks())
							{
								bString.append(" &nbsp;Stacks");
							}

							final String cString = aFeat.preReqHTMLStrings(pc, false);

							if (cString.length() > 0)
							{
								bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
							}

							bString.append(" &nbsp;<b>Description</b>:").append(aFeat.piDescSubString())
							.append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
							infoLabel.setText(bString.toString());
						}
					}
				}
			});

		selectedTable.addMouseListener(new JTreeTableMouseAdapter(selectedTable, new SelectedClickHandler(), false));

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		requestFocus();
		// TODO: I18N
		PCGen_Frame1.setMessageAreaTextWithoutSaving(getSingularTabName() + "s are color coded: Red = Character does not qualify; "
			+ "Yellow = Automatic; Magenta = Virtual");
		refresh();

		int width;
		int s = splitTopLeftRight.getDividerLocation();
		int t = splitTopBot.getDividerLocation();
		int u = splitBotLeftRight.getDividerLocation();

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoFeats.splitTopLeftRight",
					(int) ((this.getSize().getWidth() * 6) / 10));
			t = SettingsHandler.getPCGenOption("InfoFeats.splitTopBot", (int) ((this.getSize().getHeight() * 75) / 100));
			u = SettingsHandler.getPCGenOption("InfoFeats.splitBotLeftRight",
					(int) ((this.getSize().getWidth() * 6) / 10));

			// set the prefered width on selectedTable
			final TableColumnModel selectedTableColumnModel = selectedTable.getColumnModel();

			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("FeatSel", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "FeatSel", i));
			}

			// set the prefered width on availableTable
			final TableColumnModel availableTableColumnModel = availableTable.getColumnModel();

			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn aCol = availableTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("FeatAva", i);

				if (width != 0)
				{
					aCol.setPreferredWidth(width);
				}

				aCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "FeatAva", i));
			}
		}

		if (s > 0)
		{
			splitTopLeftRight.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoFeats.splitTopLeftRight", s);
		}

		if (t > 0)
		{
			splitTopBot.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoFeats.splitTopBot", t);
		}

		if (u > 0)
		{
			splitBotLeftRight.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoFeats.splitBotLeftRight", u);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new FeatPopupListener(treeTable, new FeatPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		if (Globals.getGameModeHasPointPool())
		{
			numFeatsField.setEditable(false);
			// SwingConstants.RIGHT is equivalent to JTextField.RIGHT but more
			// 'correct' in a Java coding context (it is a static reference)
			numFeatsField.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		else
		{
			numFeatsField.addFocusListener(new FocusAdapter()
				{
					public void focusLost(FocusEvent evt)
					{
						if (numFeatsField.getText().length() > 0)
						{
							if (pc != null)
							{
								pc.setDirty(true);
								pc.setFeats(Double.parseDouble(numFeatsField.getText()));
							}
						}
						else if (pc != null)
						{
//							numFeatsField.setText(String.valueOf(pc.getFeats()));
							showRemainingFeatPoints();
						}
					}
				});
		}
		viewAvailComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					viewAvailComboBoxActionPerformed();
				}
			});
		viewSelectComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					viewSelectComboBoxActionPerformed();
				}
			});
		textAvailableQFilter.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setAvailableQFilter();
				}
			});
		setAvailableQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setAvailableQFilter();
				}
			});
		clearAvailableQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					clearAvailableQFilter();
				}
			});
		textSelectedQFilter.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setSelectedQFilter();
				}
			});
		setSelectedQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setSelectedQFilter();
				}
			});
		clearSelectedQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					clearSelectedQFilter();
				}
			});
		chkViewAll.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					chkViewAllActionPerformed();
				}
			});
		addComponentListener(new ComponentAdapter()
			{
				public void componentShown(ComponentEvent evt)
				{
					formComponentShown();
				}
			});
		leftButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					removeFeat();
				}
			});
		addButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					addFeat();
				}
			});

		FilterFactory.restoreFilterSettings(this);
	}

	private void initComponents()
	{
		if (Globals.getGameModeHasPointPool())
		{
			FEAT_FULL_MESSAGE  = "You do not have enough remaining " + Globals.getGameModePointPoolName() + " to select this " + getSingularTabName() + ".";
		}

		readyForRefresh = true;
		//
		// Sanity check
		//
		int iView = SettingsHandler.getFeatTab_AvailableListMode();

		if ((iView >= GuiConstants.INFOFEATS_VIEW_TYPENAME) && (iView <= GuiConstants.INFOFEATS_VIEW_SOURCENAME))
		{
			viewAvailMode = iView;
		}

		SettingsHandler.setFeatTab_AvailableListMode(viewAvailMode);
		iView = SettingsHandler.getFeatTab_SelectedListMode();

		if ((iView >= GuiConstants.INFOFEATS_VIEW_TYPENAME) && (iView <= GuiConstants.INFOFEATS_VIEW_SOURCENAME))
		{
			viewSelectMode = iView;
		}

		SettingsHandler.setFeatTab_SelectedListMode(viewSelectMode);

		viewAvailComboBox.addItem(PropertyFactory.getString("in_typeName"));
		viewAvailComboBox.addItem(PropertyFactory.getString("in_nameLabel"));
		viewAvailComboBox.addItem(PropertyFactory.getString("in_preReqTree"));
		viewAvailComboBox.addItem(PropertyFactory.getString("in_sourceName"));
		Utility.setDescription(viewAvailComboBox,
			"You can change how the " + getSingularTabName()+ "s in the Available and Selected Tables are listed - either by name or in a directory-like structure.");
		viewAvailComboBox.setSelectedIndex(viewAvailMode);

		viewSelectComboBox.addItem(PropertyFactory.getString("in_typeName"));
		viewSelectComboBox.addItem(PropertyFactory.getString("in_nameLabel"));
		viewSelectComboBox.addItem(PropertyFactory.getString("in_preReqTree"));
		viewSelectComboBox.addItem(PropertyFactory.getString("in_sourceName"));
		Utility.setDescription(viewSelectComboBox,
			"You can change how the " + getSingularTabName() + "s in the Selected Tables are listed - either by name or in a directory-like structure.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);

		List typeList = new ArrayList();
		List sourceList = new ArrayList();

		for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); )
		{
			final Ability anAbility = (Ability) it.next();

			if ((anAbility.getVisible() != Ability.VISIBILITY_DEFAULT) &&
				(anAbility.getVisible() != Ability.VISIBILITY_DISPLAY_ONLY))
			{
				continue;
			}

			final StringTokenizer aTok = new StringTokenizer(anAbility.getType(), ".");

			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();

				if (!Globals.isAbilityTypeHidden(aString))
				{
					if (!typeList.contains(aString))
					{
						typeList.add(aString);
					}

					String sourceString = anAbility.getSourceWithKey("LONG");
					if ((sourceString != null) && (!sourceList.contains(sourceString)))
					{
						sourceList.add(sourceString);
					}
				}
			}
		}

		Collections.sort(typeList);
		PObjectNode[] ccTypes = new PObjectNode[typeList.size()];
		for (int i = 0; i < typeList.size(); ++i)
		{
			ccTypes[i] = new PObjectNode();
			ccTypes[i].setItem(typeList.get(i).toString());
			ccTypes[i].setParent(typeRoot);
		}
		typeRoot.setChildren(ccTypes);

		Collections.sort(sourceList);
		PObjectNode[] ccSources = new PObjectNode[sourceList.size()];
		for (int i = 0; i < sourceList.size(); ++i)
		{
			ccSources[i] = new PObjectNode();
			ccSources[i].setItem(sourceList.get(i).toString());
			ccSources[i].setParent(sourceRoot);
		}
		sourceRoot.setChildren(ccSources);

		// initialize Models
		createModels();

		// create available table of feats
		createTreeTables();

		topPane.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		//-------------------------------------------------------------
		// Top Pane - Left Available, Right Selected
		//
		JPanel tLeftPane = new JPanel();
		JPanel tRightPane = new JPanel();

		splitTopLeftRight = new FlippingSplitPane(splitOrientation, tLeftPane, tRightPane);
		splitTopLeftRight.setOneTouchExpandable(true);
		splitTopLeftRight.setDividerSize(10);

		// splitTopLeftRight.setDividerLocation(350);
		topPane.add(splitTopLeftRight, BorderLayout.CENTER);

		// Top Left - Available
		tLeftPane.setLayout(gridbag);
		Utility.buildConstraints(c, 0, 0, 4, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;

		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		JLabel avaLabel = new JLabel("Available: ");
		aPanel.add(avaLabel);
		aPanel.add(viewAvailComboBox);

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addButton = new JButton(newImage);
		Utility.setDescription(addButton, "Click to add the selected " + getSingularTabName() + " from the Available list of " + getSingularTabName() + "s");
		addButton.setEnabled(false);
		aPanel.add(addButton);
		tLeftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(lblAvailableQFilter, c);
		tLeftPane.add(lblAvailableQFilter);
		
		Utility.buildConstraints(c, 1, 1, 1, 1, 95, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(textAvailableQFilter, c);
		tLeftPane.add(textAvailableQFilter);
		
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(setAvailableQFilterButton, c);
		setAvailableQFilterButton.setToolTipText("Set a Filter on the list of feats below, e.g. 'spell'");
		tLeftPane.add(setAvailableQFilterButton);

		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(clearAvailableQFilterButton, c);
		clearAvailableQFilterButton.setEnabled(false);
		tLeftPane.add(clearAvailableQFilterButton);

		Utility.buildConstraints(c, 0, 2, 4, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		tLeftPane.add(scrollPane);

		// Right Pane - Selected
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		tRightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 4, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		JLabel selLabel = new JLabel("Selected: ");
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove the selected " + getSingularTabName() + " from the Selected list of " + getSingularTabName() + "s");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		if (SettingsHandler.allowFeatDebugging())
		{
			aPanel.add(chkViewAll);
		}
		tRightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(lblSelectedQFilter, c);
		tRightPane.add(lblSelectedQFilter);
		
		Utility.buildConstraints(c, 1, 1, 1, 1, 95, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(textSelectedQFilter, c);
		tRightPane.add(textSelectedQFilter);
		
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(setSelectedQFilterButton, c);
		setSelectedQFilterButton.setToolTipText("Set a Filter on the list of feats below, e.g. 'spell'");
		tRightPane.add(setSelectedQFilterButton);

		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(clearSelectedQFilterButton, c);
		clearSelectedQFilterButton.setEnabled(false);
		tRightPane.add(clearSelectedQFilterButton);

		Utility.buildConstraints(c, 0, 2, 4, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		tRightPane.add(scrollPane);

		//-------------------------------------------------------------
		// Bottom Pane - Left Info, Right Options / Data
		//
		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());

		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();

		splitBotLeftRight = new FlippingSplitPane(splitOrientation, bLeftPane, bRightPane);
		splitBotLeftRight.setOneTouchExpandable(true);
		splitBotLeftRight.setDividerSize(10);

		//splitBotLeftRight.setDividerLocation(450);
		botPane.add(splitBotLeftRight, BorderLayout.CENTER);

		// Left - Feat Info
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		bLeftPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(infoScroll, c);

		TitledBorder title1 = BorderFactory.createTitledBorder(getSingularTabName() + " Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		infoScroll.setBorder(title1);
		infoLabel.setBackground(bLeftPane.getBackground());
		infoScroll.setViewportView(infoLabel);
		Utility.setDescription(infoScroll, "Any requirements you don't meet are in italics.");
		bLeftPane.add(infoScroll);

		// Right - Feat Options / Data
		// - feats remaining...
		if (Globals.getGameModeHasPointPool())
		{
			featsRemainingLabel.setText(Globals.getGameModePointPoolName() + ':');
		}
		else
		{
			featsRemainingLabel.setText(getSingularTabName() + "s remaining: ");
		}

		JPanel cPanel = new JPanel();
		cPanel.setLayout(new FlowLayout());
		cPanel.add(featsRemainingLabel);


		if (pc != null)
		{
//			numFeatsField.setText(String.valueOf(pc.getFeats()));
			showRemainingFeatPoints();
			numFeatsField.setColumns(3);
		}

		cPanel.add(numFeatsField);
		if (Globals.getGameModeHasPointPool())
		{
			Utility.setDescription(numFeatsField, "How many " + Globals.getGameModePointPoolName() + " you have left to choose.");
		}
		else
		{
			Utility.setDescription(numFeatsField, "How many " + getSingularTabName() + "s you have left to choose (editable).");
		}
		bRightPane.add(cPanel);

		//----------------------------------------------------------------------
		// Split Top and Bottom
		splitTopBot = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, botPane);
		splitTopBot.setOneTouchExpandable(true);
		splitTopBot.setDividerSize(10);

		// splitTopBot.setDividerLocation(300);
		this.setLayout(new BorderLayout());
		this.add(splitTopBot, BorderLayout.CENTER);
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel.getRoot(), selectedModel);

		addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					refresh();
				}
			});
	}

	private void removeFeat()
	{
		String aString = null;
		Object temp = selectedTable.getTree().getLastSelectedPathComponent();

		if (temp == null)
		{
			ShowMessageDelegate.showMessageDialog("Somehow, no " + getSingularTabName() + " was selected.  Try again.", Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();

			if (temp instanceof Ability)
			{
				aString = ((Ability) temp).getName();
			}
		}

		try
		{
			pc.setDirty(true);

			// modFeat(featName, adding_feat, adding_all_selections)
			AbilityUtilities.modFeat(pc, null, aString, false, false);
		}
		catch (Exception exc)
		{
			ShowMessageDelegate.showMessageDialog("InfoFeats2: " + exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		// update the skills tab, as feats could effect totals
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoInventory());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();

		pc.aggregateFeatList();
		updateAvailableModel();
		updateSelectedModel();

//		selectedTable.getColumnModel().getColumn(0).setHeaderValue(getSingularTabName() + "s (" + pc.getUsedFeatCount() + ")");
		selectedTable.getColumnModel().getColumn(0).setHeaderValue(getSingularTabName() + "s (" + BigDecimalHelper.trimBigDecimal(new BigDecimal(pc.getUsedFeatCount())).toString() + ")");

		pc.calcActiveBonuses();
//		numFeatsField.setText(String.valueOf(pc.getFeats()));
		showRemainingFeatPoints();
		setRemoveEnabled(false);
	}

	private void updateAvailableModel()
	{
		List pathList = availableTable.getExpandedPaths();
		createModelAvailable();

		if (availableSort != null)
		{
			availableSort.sortNodeOnColumn();
		}
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	private void updateCharacterInfo()
	{
		PObjectNode.resetPC(getPc());

		if ((pc == null) || (pc.isAggregateFeatsStable() && !needsUpdate))
		{
			return;
		}

		pc.setAggregateFeatsStable(false);
		pc.setAutomaticFeatsStable(false);
		pc.setVirtualFeatsStable(false);
		pc.aggregateFeatList();

		JViewport aPort = infoScroll.getColumnHeader();

		if (aPort != null)
		{
			aPort.setVisible(false);
		}

		//showWeaponProfList();
		updateAvailableModel();
		updateSelectedModel();

//		selectedTable.getColumnModel().getColumn(0).setHeaderValue(getSingularTabName() + "s (" + pc.getUsedFeatCount() + ")");
		selectedTable.getColumnModel().getColumn(0).setHeaderValue(getSingularTabName() + "s (" + BigDecimalHelper.trimBigDecimal(new BigDecimal(pc.getUsedFeatCount())).toString() + ")");

		//selectedTable.getTableHeader().resizeAndRepaint();
//		numFeatsField.setText(String.valueOf(pc.getFeats()));
		showRemainingFeatPoints();
		needsUpdate = false;
	}

	private void updateSelectedModel()
	{
		List pathList = selectedTable.getExpandedPaths();
		createModelSelected();

		if (selectedSort != null)
		{
			selectedSort.sortNodeOnColumn();
		}
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	private void viewAvailComboBoxActionPerformed()
	{
		final int index = viewAvailComboBox.getSelectedIndex();

		if (index != viewAvailMode)
		{
			viewAvailMode = index;
			SettingsHandler.setFeatTab_AvailableListMode(viewAvailMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();

		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setFeatTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
	}

	private void chkViewAllActionPerformed()
	{
		updateSelectedModel();
	}

	/**
	 * Extends AbstractTreeTableModel to build an available or
	 * selected feats tree for this tab.
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
	 */
	private final class FeatModel extends AbstractTreeTableModel
	{
		// Names of the columns.
		private String[] cNames = { "Name", "Modified" };

		// Types of the columns.
		private Class[] cTypes = { TreeTableModel.class, String.class };
		private int modelType = 0; // availableModel

		/**
		 * Creates a FeatModel
		 * @param mode
		 * @param available
		 */
		private FeatModel(int mode, boolean available)
		{
			super(null);
			resetModel(mode, available, false);
		}

		/**
		 * Returns Class for the column.
		 * @param column
		 * @return Class
		 */
		public Class getColumnClass(int column)
		{
			return cTypes[column];
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 * @return column count
		 */
		public int getColumnCount()
		{
			return cNames.length;
		}

		/**
		 * Returns String name of a column.
		 * @param column
		 * @return column name
		 */
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				String colName = getSingularTabName();
				if (modelType != 0)
				{
					colName += " (" + pc.getUsedFeatCount() + ")";
				}
				return colName;

			}

			if (modelType == 0)
			{
				return "Source";
			}

			return "Choices";
		}

		public Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		/**
		 * Returns Object value of the column.
		 * @param node
		 * @param column
		 * @return value
		 */
		public Object getValueAt(Object node, int column)
		{
			PObjectNode fn = (PObjectNode) node;

			switch (column)
			{
				case 0:
					return fn.toString();

				case 1:

					if (modelType == 0)
					{
						return fn.getSource();
					}

					return fn.getChoices();

				case -1:
					return fn.getItem();

				default:
					Logging.errorPrint("In InfoFeats.getValueAt the column " + column + " is not supported.");

					break;
			}

			return null;
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
		 * This method gets the feat list from the current PC by calling
		 * <code>pc.aggregateFeatList()</code>.  Because <code>aggregateFeatList()</code>
		 * (correctly) returns chosen/auto/virtual feats aggregated together, this
		 * elimiates duplicate feats.  However, since we want to display feats with
		 * multiple choices (e.g. Weapon Focus) separately if they are chosen/auto/etc.,
		 * we add back the chosen, virtual, and automatic feats when the <code>isMultiples()</code>
		 * returns <code>true</code>.  Note that this <b>may</b> cause problems for
		 * the prerequisite tree, although the code there <b>appears</b> robust enough
		 * to handle it.
		 * The list is sorted before it is returned.
		 *
		 * @return A list of the current PCs feats.
		 */
		private List buildPCFeatList()
		{
			ArrayList returnValue = new ArrayList(pc.aggregateFeatList().size());

			for (Iterator pcFeats = pc.aggregateFeatList().iterator(); pcFeats.hasNext();)
			{
				final Ability aFeat = (Ability) pcFeats.next();

				if (aFeat.isMultiples())
				{
					final String featName = aFeat.getName();

					if (pc.hasRealFeatNamed(featName))
					{
						returnValue.add(pc.getRealFeatNamed(featName));
					}

					/*else*/ if (pc.hasFeatAutomatic(featName))
					{
						returnValue.add(pc.getFeatAutomaticNamed(featName));
					}

					/*else*/ if (pc.hasFeatVirtual(featName))
					{
						returnValue.add(AbilityUtilities.getFeatNamedInList(pc.getVirtualFeatList(), featName));
					}
				}
				else
				{
					returnValue.add(aFeat);
				}
			}

			//Need to sort the list.
			return Globals.sortPObjectList(returnValue);
		}

		/**
		 * Populates the tree with a list of feats by name only (not much of a tree).
		 * Simply adds feats to the root node.
		 *
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 * @param showAll
		 */
		private void buildTreeNameOnly(boolean available, boolean showAll)
		{
			super.setRoot(new PObjectNode());
			String qFilter = this.getQFilter();

			Iterator fI;

			if (available)
			{
				fI = Globals.getAbilityKeyIterator("FEAT");
			}
			else
			{
				fI = buildPCFeatList().iterator();
			}

			while (fI.hasNext())
			{
				final Ability aFeat = (Ability) fI.next();

				/*
				 * update for new filtering
				 * author: Thomas Behr 09-02-02
				 */
				if (!accept(pc, aFeat))
				{
					continue;
				}

				if (!showAll)
				{
					if (!((aFeat.getVisible() == Ability.VISIBILITY_DEFAULT)
						|| (aFeat.getVisible() == Ability.VISIBILITY_DISPLAY_ONLY)))
					{
						continue;
					}
				}

				int hasIt = HASABILITY_NO;
				final String featName = aFeat.getName();

				if (available)
				{
					if (pc.hasRealFeatNamed(featName))
					{
						hasIt = HASABILITY_CHOSEN;
					}
					else if (pc.hasFeatAutomatic(featName))
					{
						hasIt = HASABILITY_AUTOMATIC;
					}
					else if (pc.hasFeatVirtual(featName))
					{
						hasIt = HASABILITY_VIRTUAL;
					}
				}

				// for availableModel,
				// use virtual or non-acquired feats
				// for selectedModel,
				// use virtual, auto and chosen
				if ((available && ((hasIt == HASABILITY_VIRTUAL) || (hasIt == HASABILITY_NO) || aFeat.isMultiples()))
					|| (!available))
				{
					PObjectNode aFN = new PObjectNode();
					aFN.setParent((PObjectNode) super.getRoot());

					if (!available)
					{
						aFN.setCheckFeatState(PObjectNode.CAN_USE_FEAT, pc);
					}

					aFN.setItem(aFeat);

					//Does anyone know why we don't call
					//aFN.setIsValid(aFeat.passesPreReqToGain()) here?
					if (qFilter == null || 
							( aFeat.getName().toLowerCase().indexOf(qFilter) >= 0 ||
							  aFeat.getType().toLowerCase().indexOf(qFilter) >= 0 ))
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
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 * @param showAll
		 */
		private void buildTreePrereqTree(boolean available, boolean showAll)
		{
			if ((preReqTreeRoot == null) && available)
			{
				preReqTreeRoot = new PObjectNode();
			}

			if (available)
			{
				setRoot(preReqTreeRoot);
			}
			else
			{
				setRoot(new PObjectNode());
			}

			List aList = new ArrayList();
			List fList = new ArrayList();

			if (available)
			{
				Ability anAbility;

				for (Iterator it = Globals.getAbilityKeyIterator(
						Constants.FEAT_CATEGORY); it.hasNext();)
				{
					anAbility = (Ability) it.next();

					if (accept(pc, anAbility))
					{
						if (
							(anAbility.getVisible() == Ability.VISIBILITY_DEFAULT) ||
							(anAbility.getVisible() == Ability.VISIBILITY_DISPLAY_ONLY))
						{
							fList.add(anAbility);
						}
					}
				}
			}
			else
			{
				// fList = (ArrayList)pc.aggregateFeatList().clone();
				// make filters work ;-)
				Ability aFeat;

				//My concern here in using buildPCFeatList() instead
				//of pc.aggregateFeatList() is what duplicates would doo
				//to the tree.  I THINK that the code will find
				//the first prerequisite feat and add the feats to that
				//This may not be perfect, but I don't think it will blow up.
				for (Iterator it = buildPCFeatList().iterator(); it.hasNext();)
				{
					aFeat = (Ability) it.next();

					if (accept(pc, aFeat))
					{
						if ((aFeat.getVisible() == Ability.VISIBILITY_DEFAULT)
							|| (aFeat.getVisible() == Ability.VISIBILITY_DISPLAY_ONLY))
						{
							fList.add(aFeat);
						}
					}
				}
			}

			for (int i = 0; i < fList.size(); ++i)
			{
				final Ability aFeat = (Ability) fList.get(i);

				if (!aFeat.hasPreReqTypeOf("FEAT"))
				{
					fList.remove(aFeat);
					aList.add(aFeat);
					--i; // to counter increment
				}
			}

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			PObjectNode[] cc = new PObjectNode[aList.size()];

			for (int i = 0; i < aList.size(); ++i)
			{
				cc[i] = new PObjectNode();
				cc[i].setItem(aList.get(i));
				cc[i].setParent(rootAsPObjectNode);

				if (!available)
				{
					cc[i].setCheckFeatState(PObjectNode.CAN_USE_FEAT, pc);
				}
			}

			rootAsPObjectNode.setChildren(cc);

			int loopmax = 6; // only go 6 levels...

			while ((fList.size() > 0) && (loopmax-- > 0))
			{
				for (int i = 0; i < fList.size(); ++i)
				{
					final Ability aFeat = (Ability) fList.get(i);
					int placed = 0;

					for (int j = 0; j < rootAsPObjectNode.getChildCount(); ++j)
					{
						final PObjectNode po = rootAsPObjectNode.getChild(j);

						// Make a copy of the prereq
						// list so we don't destroy
						// the other prereqs
						List preReqList = new ArrayList();

						for (int pi = aFeat.getPreReqCount() - 1; pi >= 0; --pi)
						{
							final Prerequisite prereq = aFeat.getPreReq(pi);

							if ((prereq.getKind() != null) && prereq.getKind().equalsIgnoreCase("FEAT"))
							{
								preReqList.add(prereq);
							}
						}
						placed = placedThisFeatInThisTree(aFeat, po, preReqList, 0, available);

						if (placed > 0)
						{
							break;
						}
					}

					if (placed == 2) // i.e. tree match
					{
						fList.remove(aFeat);
						--i; // since we're incrementing in the for loop
					}
				}
			}

			if (fList.size() > 0)
			{
				PObjectNode po = new PObjectNode();
				po.setItem("Other");
				cc = new PObjectNode[fList.size()];

				for (int i = 0; i < fList.size(); ++i)
				{
					cc[i] = new PObjectNode();
					cc[i].setItem(fList.get(i));
					cc[i].setParent(po);


					final int state;
					if (modelType == 1 && available)
					{
						state = PObjectNode.CAN_GAIN_FEAT;
					}
					else if (modelType == 1)
					{
						state = PObjectNode.CAN_USE_FEAT;
					}
					else
					{
						state = PObjectNode.NOT_A_FEAT;
					}
					cc[i].setCheckFeatState(state, pc);
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
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 * @param showAll
		 */
		private void buildTreeTypeName(boolean available, boolean showAll)
		{
			setRoot(typeRoot);

			Iterator fI;

			if (available)
			{
				fI = Globals.getAbilityKeyIterator("FEAT");
			}
			else
			{
				fI = buildPCFeatList().iterator();
			}

			while (fI.hasNext())
			{
				final Ability aFeat = (Ability) fI.next();

				// in the availableTable, if filtering out unqualified feats
				// ignore any feats the PC doesn't qualify for

				/*
				 * update for new filtering
				 * author: Thomas Behr 09-02-02
				 */
				if (!accept(pc, aFeat))
				{
					continue;
				}

				if (!showAll)
				{
					if ((aFeat.getVisible() != Ability.VISIBILITY_DEFAULT)
							&& (aFeat.getVisible() != Ability.VISIBILITY_DISPLAY_ONLY))
					{
						continue;
					}
				}

				int hasIt = HASABILITY_NO;
				final String featName = aFeat.getName();

				if (available)
				{
					if (pc.hasRealFeatNamed(featName))
					{
						hasIt = HASABILITY_CHOSEN;
					}
					else if (pc.hasFeatAutomatic(featName))
					{
						hasIt = HASABILITY_AUTOMATIC;
					}
					else if (pc.hasFeatVirtual(featName))
					{
						hasIt = HASABILITY_VIRTUAL;
					}
				}

				// if putting together availableModel, use virtual or non-acquired
				// feats for selectedModel, use virtual, auto and chosen
				PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

				if ((available && ((hasIt == HASABILITY_VIRTUAL) || (hasIt == HASABILITY_NO) || aFeat.isMultiples()))
					|| (!available))
				{
					for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
					{
						if (aFeat.isType(rootAsPObjectNode.getChild(i).toString()))
						{
							PObjectNode aFN = new PObjectNode();

							if (!available)
							{
								aFN.setCheckFeatState(PObjectNode.CAN_USE_FEAT, pc);
							}

							aFN.setParent(rootAsPObjectNode.getChild(i));
							aFN.setItem(aFeat);
							if (Globals.checkRule(RuleConstants.FEATPRE))
							{
								// Method no longer exists - aFN.setIsValid(true);
							}
							else
							{
								PrereqHandler.passesAll( aFeat.getPreReqList(), pc, aFeat );
							}
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
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 * @param showAll
		 */
		private void buildTreeSourceName(boolean available, boolean showAll)
		{
			setRoot(sourceRoot);

			Iterator fI;

			if (available)
			{
				fI = Globals.getAbilityKeyIterator("FEAT");
			}
			else
			{
				fI = buildPCFeatList().iterator();
			}

			while (fI.hasNext())
			{
				final Ability aFeat = (Ability) fI.next();

				// in the availableTable, if filtering out unqualified feats
				// ignore any feats the PC doesn't qualify for

				/*
				 * update for new filtering
				 * author: Thomas Behr 09-02-02
				 */
				if (!accept(pc, aFeat))
				{
					continue;
				}

				if (!showAll)
				{
					if (!((aFeat.getVisible() == Ability.VISIBILITY_DEFAULT)
							|| (aFeat.getVisible() == Ability.VISIBILITY_DISPLAY_ONLY)))
					{
						continue;
					}
				}

				int hasIt = HASABILITY_NO;
				final String featName = aFeat.getName();

				if (available)
				{
					if (pc.hasRealFeatNamed(featName))
					{
						hasIt = HASABILITY_CHOSEN;
					}
					else if (pc.hasFeatAutomatic(featName))
					{
						hasIt = HASABILITY_AUTOMATIC;
					}
					else if (pc.hasFeatVirtual(featName))
					{
						hasIt = HASABILITY_VIRTUAL;
					}
				}

				// if putting together availableModel, use virtual or non-acquired
				// feats for selectedModel, use virtual, auto and chosen
				PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

				if ((available && ((hasIt == HASABILITY_VIRTUAL) || (hasIt == HASABILITY_NO) || aFeat.isMultiples()))
					|| (!available))
				{
					for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
					{
						if (aFeat.getSourceWithKey("LONG").equals(rootAsPObjectNode.getChild(i).toString()))
						{
							PObjectNode aFN = new PObjectNode();

							if (!available)
							{
								aFN.setCheckFeatState(PObjectNode.CAN_USE_FEAT, pc);
							}

							aFN.setParent(rootAsPObjectNode.getChild(i));
							aFN.setItem(aFeat);
							if (Globals.checkRule(RuleConstants.FEATPRE))
							{
								// Method no longer exitsts - aFN.setIsValid(true);
							}
							else
							{
								PrereqHandler.passesAll( aFeat.getPreReqList(), pc, aFeat );
							}
							rootAsPObjectNode.getChild(i).addChild(aFN);
						}
					}
				}
			}
		}

		private int placedThisFeatInThisTree(final Ability aFeat, PObjectNode po, List aList, int level, boolean available)
		{
			final Ability bFeat = (Ability) po.getItem(); // must be a Feat
			boolean trychildren = false;
			boolean thisisit = false;

			for (Iterator it = aList.iterator(); it.hasNext();)
			{
				Prerequisite prereq = (Prerequisite) it.next();
				String pString = prereq.getKey();

				if (pString.equalsIgnoreCase(bFeat.getName()))
				{
					thisisit = true;
				}
				else
				{
					trychildren = true; // might be a child
				}

				if (thisisit)
				{
					PObjectNode p = new PObjectNode();
					p.setItem(aFeat);
					p.setParent(po);
					po.addChild(p);

					final int state;
					if (modelType == 1 && available)
					{
						state = PObjectNode.CAN_GAIN_FEAT;
					}
					else if (modelType == 1)
					{
						state = PObjectNode.CAN_USE_FEAT;
					}
					else
					{
						state = PObjectNode.NOT_A_FEAT;
					}
					p.setCheckFeatState(state, pc);

					return 2; // successfully added
				}
				else if (trychildren)
				{
					for (int i = 0; i < po.getChildCount(); ++i)
					{
						int j = placedThisFeatInThisTree(aFeat, po.getChild(i), aList, level + 1, available);

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
		 * This assumes the FeatModel exists but needs to be repopulated
		 * Calls the various <code>buildTreeXXX</code> methods based on the
		 * <code>mode</code> parameter.
		 *
		 * @param mode      View mode for this tree, one of <code>GuiConstants.INFOFEATS_VIEW_NAMEONLY</code>,
		 *                  <code>GuiConstants.INFOFEATS_VIEW_TYPENAME</code>, or <code>GuiConstants.INFOFEATS_VIEW_PREREQTREE</code>.
		 * @param available <code>true</code> if this is the available feats tree,
		 *                  <code>false</code> if this is the selected feats tree.
		 * @param showAll
		 */
		private void resetModel(int mode, boolean available, boolean showAll)
		{
			if (!available)
			{
				modelType = 1;
			}

			switch (mode)
			{
				//NOTE:  I moved the code here into private methods
				//to make it more intelligible
				case GuiConstants.INFOFEATS_VIEW_TYPENAME:
					buildTreeTypeName(available, showAll);

					break;

				case GuiConstants.INFOFEATS_VIEW_NAMEONLY:
					buildTreeNameOnly(available, showAll);

					break;

				case GuiConstants.INFOFEATS_VIEW_PREREQTREE:
					buildTreePrereqTree(available, showAll);

					break;

				case GuiConstants.INFOFEATS_VIEW_SOURCENAME:
					buildTreeSourceName(available, showAll);

					break;

/*
   case InfoFeats.VIEW_SOURCENAME:
	   break;
   case InfoFeats.VIEW_SOURCETYPENAME:
	   break;
   case InfoFeats.VIEW_TYPESOURCENAME:
	   break;
 */
				default:
					Logging.errorPrint("In InfoFeats.resetModel the mode " + mode + " is not supported.");

					break;
			}

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
		}
	}

	private class FeatPopupListener extends MouseAdapter
	{
		private FeatPopupMenu menu;
		private JTree tree;

		private FeatPopupListener(JTreeTable treeTable, FeatPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
				{
					public void keyTyped(KeyEvent e)
					{
						dispatchEvent(e);
					}

					// Walk through the list of accelerators
					// to see if the user has pressed a sequence
					// used by the popup. This would not
					// happen unless the popup was showing
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

	private class FeatPopupMenu extends JPopupMenu
	{
		private JMenuItem duplicateMenuItem;
		private JMenuItem featFullMenuItem;
		private JMenuItem noQualifyMenuItem;
		private JTreeTable treeTable;

		private FeatPopupMenu(JTreeTable treeTable)
		{
			if ((this.treeTable = treeTable) == availableTable)
			{
				FeatPopupMenu.this.add(addMenu = Utility.createMenuItem("Add " + getSingularTabName(),
							new ActionListener()
						{
							public void actionPerformed(ActionEvent evt)
							{
								addFeat();
							}
						}, "infoFeats.addFeat", (char) 0, "shortcut EQUALS", "Add Feat", "Add16.gif", true));

				// Build menus now since expert settings
				// could get changed while we are running
				noQualifyMenuItem = Utility.createMenuItem(NO_QUALIFY_MESSAGE, null, null, (char) 0, null, null, null, false);
				duplicateMenuItem = Utility.createMenuItem(DUPLICATE_MESSAGE , null, null, (char) 0, null, null, null, false);
				featFullMenuItem  = Utility.createMenuItem(FEAT_FULL_MESSAGE , null, null, (char) 0, null, null, null, false);
			}
			else // selectedTable
			{
				FeatPopupMenu.this.add(removeMenu = Utility.createMenuItem("Remove " + getSingularTabName(),
							new ActionListener()
						{
							public void actionPerformed(ActionEvent evt)
							{
								removeFeat();
							}
						}, "infoFeats.removeFeat", (char) 0, "shortcut MINUS", "Remove Feat", "Remove16.gif", true));
			}
		}

		public void show(Component source, int x, int y)
		{
			PObjectNode node = (PObjectNode)treeTable.getTree().getLastSelectedPathComponent();
			if (node != null && node.getItem() instanceof Ability)
			{
				Ability aFeat = (Ability) ( (PObjectNode) treeTable.getTree().getLastSelectedPathComponent()).getItem();

				if (treeTable == availableTable)
				{
					int ok = checkFeatQualify(aFeat);

					if (ok == FEAT_OK)
					{
						FeatPopupMenu.this.removeAll();
						FeatPopupMenu.this.add(addMenu);
					}

					if (SettingsHandler.isExpertGUI() == false)
					{
						switch (ok)
						{
							case FEAT_NOT_QUALIFIED:
								FeatPopupMenu.this.removeAll();
								FeatPopupMenu.this.add(noQualifyMenuItem);
								break;

							case FEAT_DUPLICATE:
								FeatPopupMenu.this.removeAll();
								FeatPopupMenu.this.add(duplicateMenuItem);
								break;

							case FEAT_FULL_FEAT:
								FeatPopupMenu.this.removeAll();
								FeatPopupMenu.this.add(featFullMenuItem);
								break;

							case FEAT_OK:
								// Handled above
								break;

							default:
								Logging.errorPrint(getSingularTabName() + " " +
												   aFeat.getName() +
												   " is somehow in state " + ok
												   + " which is not handled" +
												   " in InfoFeats.FeatPopupMenu.show()");

								return;
						}
					}

					// The selected feat menu is prebuilt so only add to
					// available
				}

				super.show(source, x, y);
			}
			else
			{
				FeatPopupMenu.this.removeAll();

				super.show(source, x, y);
			}
		}
	}

	private void showRemainingFeatPoints()
	{
		if (pc != null)
		{
//			numFeatsField.setText(String.valueOf(pc.getFeats()));
			numFeatsField.setText(BigDecimalHelper.trimBigDecimal(new BigDecimal(pc.getFeats())).toString());
		}
	}
	private void clearAvailableQFilter()
	{
		availableModel.clearQFilter();
		if (saveAvailableViewMode != null)
		{
			viewAvailMode = saveAvailableViewMode.intValue();
			saveAvailableViewMode = null;
		}
		availableModel.resetModel(viewAvailMode, true, false);
		clearAvailableQFilterButton.setEnabled(false);
		viewAvailComboBox.setEnabled(true);
		forceRefresh();
	}

	private void clearSelectedQFilter()
	{
		selectedModel.clearQFilter();
		if (saveAvailableViewMode != null)
		{
			viewSelectMode = saveSelectedViewMode.intValue();
			saveSelectedViewMode = null;
		}
		selectedModel.resetModel(viewSelectMode, false, false);
		clearSelectedQFilterButton.setEnabled(false);
		viewSelectComboBox.setEnabled(true);
		forceRefresh();
	}

	private void setAvailableQFilter()
	{
		String aString = textAvailableQFilter.getText();

		if (aString.length() != 0)
		{
			availableModel.setQFilter(aString);
		}

		if (saveAvailableViewMode == null)
		{
			saveAvailableViewMode = new Integer(viewAvailMode);
		}
		viewAvailMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;
		availableModel.resetModel(viewAvailMode, true, false);
		clearAvailableQFilterButton.setEnabled(true);
		viewAvailComboBox.setEnabled(false);
		forceRefresh();
	}

	private void setSelectedQFilter()
	{
		String aString = textSelectedQFilter.getText();

		if (aString.length() != 0)
		{
			selectedModel.setQFilter(aString);
		}

		if (saveSelectedViewMode == null)
		{
			saveSelectedViewMode = new Integer(viewSelectMode);
		}
		viewSelectMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;
		selectedModel.resetModel(viewSelectMode, false, false);
		clearSelectedQFilterButton.setEnabled(true);
		viewSelectComboBox.setEnabled(false);
		forceRefresh();
	}
}
