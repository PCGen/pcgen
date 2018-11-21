/*
 *
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.dialog;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pcgen.cdom.base.Constants;
import pcgen.gui2.prefs.CharacterStatsPanel;
import pcgen.gui2.prefs.ColorsPanel;
import pcgen.gui2.prefs.CopySettingsPanel;
import pcgen.gui2.prefs.DefaultsPanel;
import pcgen.gui2.prefs.DisplayOptionsPanel;
import pcgen.gui2.prefs.EquipmentPanel;
import pcgen.gui2.prefs.HitPointsPanel;
import pcgen.gui2.prefs.HouseRulesPanel;
import pcgen.gui2.prefs.InputPanel;
import pcgen.gui2.prefs.LanguagePanel;
import pcgen.gui2.prefs.LevelUpPanel;
import pcgen.gui2.prefs.LocationPanel;
import pcgen.gui2.prefs.LookAndFeelPanel;
import pcgen.gui2.prefs.MonsterPanel;
import pcgen.gui2.prefs.OutputPanel;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui2.prefs.SourcesPanel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 *  PCGen preferences dialog
 */
public final class PreferencesDialog extends AbstractPreferencesDialog
{
	private static final long serialVersionUID = 5042379023317257550L;

	// Resource strings
	private static final String IN_APPERANCE = LanguageBundle.getString("in_Prefs_appearance"); //$NON-NLS-1$
	private static final String IN_CHARACTER = LanguageBundle.getString("in_Prefs_character"); //$NON-NLS-1$

	private DefaultTreeModel settingsModel;
	private FlippingSplitPane splitPane;

	private JPanel settingsPanel;

	private JScrollPane settingsScroll;

	private JTree settingsTree;

	private List<PCGenPrefsPanel> panelList;

	// Character panels
	private PCGenPrefsPanel characterStatsPanel;
	private PCGenPrefsPanel hitPointsPanel;
	private PCGenPrefsPanel houseRulesPanel;
	private PCGenPrefsPanel monsterPanel;
	private PCGenPrefsPanel defaultsPanel;

	// Appearance panels
	private PCGenPrefsPanel colorsPanel;
	private PCGenPrefsPanel displayOptionsPanel;
	private PCGenPrefsPanel levelUpPanel;
	private PCGenPrefsPanel lookAndFeelPanel;
	//	private PCGenPrefsPanel tabsPanel;

	// PCGen panels
	private PCGenPrefsPanel equipmentPanel;
	private LanguagePanel languagePanel;
	private PCGenPrefsPanel locationPanel;
	private PCGenPrefsPanel inputPanel;
	private PCGenPrefsPanel outputPanel;
	private PCGenPrefsPanel sourcesPanel;

	// "Copy Settings"
	private CopySettingsPanel copySettingsPanel;

	private PreferencesDialog(JFrame parent, boolean modal)
	{
		super(parent, Constants.APPLICATION_NAME, modal);

		applyOptionValuesToControls();
		settingsTree.setSelectionRow(1);

		pack();
		Utility.setComponentRelativeLocation(getParent(), this);
	}

	public static void show(JFrame frame)
	{
		PreferencesDialog prefsDialog;
		prefsDialog = new PreferencesDialog(frame, true);
		prefsDialog.setVisible(true);
	}

	private void setOptionsBasedOnControls()
	{
		boolean needsRestart = false;
		for (PCGenPrefsPanel prefsPanel : panelList)
		{
			prefsPanel.setOptionsBasedOnControls();
			needsRestart |= prefsPanel.needsRestart();
		}

		if (needsRestart)
		{
			JOptionPane.showMessageDialog(
				getParent(), LanguageBundle.getString("in_Prefs_restartRequired"), //$NON-NLS-1$
				Constants.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void applyOptionValuesToControls()
	{
		for (PCGenPrefsPanel prefsPanel : panelList)
		{
			prefsPanel.applyOptionValuesToControls();
		}

		// Copy Settings
		copySettingsPanel.registerAffectedPanel(characterStatsPanel);
		copySettingsPanel.registerAffectedPanel(defaultsPanel);
		copySettingsPanel.registerAffectedPanel(languagePanel);

	}

	private static JPanel buildEmptyPanel(String title, String messageText)
	{
		GridBagLayout gridbag;
		GridBagConstraints c;
		JLabel label;
		JPanel panel = new JPanel();
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, title);

		title1.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(title1);
		gridbag = new GridBagLayout();
		panel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(messageText, SwingConstants.CENTER);
		gridbag.setConstraints(label, c);
		panel.add(label);

		return panel;
	}

	@Override
	protected JComponent getCenter()
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode characterNode;
		DefaultMutableTreeNode pcGenNode;
		DefaultMutableTreeNode appearanceNode;
		DefaultMutableTreeNode gameModeNode;

		panelList = new ArrayList<>(15);

		// Build the settings panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new CardLayout());
		settingsPanel.setPreferredSize(new Dimension(780, 420));

		// Build the selection tree
		characterNode = new DefaultMutableTreeNode(IN_CHARACTER);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle.getString("in_Prefs_charTip")), IN_CHARACTER);

		characterStatsPanel = new CharacterStatsPanel(this);
		addPanelToTree(characterNode, characterStatsPanel);
		hitPointsPanel = new HitPointsPanel();
		addPanelToTree(characterNode, hitPointsPanel);
		houseRulesPanel = new HouseRulesPanel();
		addPanelToTree(characterNode, houseRulesPanel);
		monsterPanel = new MonsterPanel();
		addPanelToTree(characterNode, monsterPanel);
		defaultsPanel = new DefaultsPanel();
		addPanelToTree(characterNode, defaultsPanel);
		rootNode.add(characterNode);

		appearanceNode = new DefaultMutableTreeNode(IN_APPERANCE);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle.getString("in_Prefs_appearanceTip")), IN_APPERANCE);

		colorsPanel = new ColorsPanel();
		addPanelToTree(appearanceNode, colorsPanel);
		displayOptionsPanel = new DisplayOptionsPanel();
		addPanelToTree(appearanceNode, displayOptionsPanel);
		levelUpPanel = new LevelUpPanel();
		addPanelToTree(appearanceNode, levelUpPanel);
		lookAndFeelPanel = new LookAndFeelPanel(this);
		addPanelToTree(appearanceNode, lookAndFeelPanel);
		//		tabsPanel = new TabsPanel();
		//		addPanelToTree(appearanceNode, tabsPanel);
		rootNode.add(appearanceNode);

		pcGenNode = new DefaultMutableTreeNode(Constants.APPLICATION_NAME);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle.getString("in_Prefs_pcgenTip")),
			Constants.APPLICATION_NAME);

		equipmentPanel = new EquipmentPanel();
		addPanelToTree(pcGenNode, equipmentPanel);
		languagePanel = new LanguagePanel();
		addPanelToTree(pcGenNode, languagePanel);
		locationPanel = new LocationPanel();
		addPanelToTree(pcGenNode, locationPanel);
		inputPanel = new InputPanel();
		addPanelToTree(pcGenNode, inputPanel);
		outputPanel = new OutputPanel();
		addPanelToTree(pcGenNode, outputPanel);
		sourcesPanel = new SourcesPanel();
		addPanelToTree(pcGenNode, sourcesPanel);
		rootNode.add(pcGenNode);

		String in_gamemode = LanguageBundle.getString("in_mnuSettingsCampaign");
		gameModeNode = new DefaultMutableTreeNode(in_gamemode);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle.getString("in_mnuSettingsCampaignTip")), in_gamemode);

		copySettingsPanel = new CopySettingsPanel();
		addPanelToTree(gameModeNode, copySettingsPanel);
		rootNode.add(gameModeNode);

		DefaultMutableTreeNode pluginNode =
				new DefaultMutableTreeNode(LanguageBundle.getString("in_Prefs_plugins")); //$NON-NLS-1$

		settingsModel = new DefaultTreeModel(rootNode);
		settingsTree = new JTree(settingsModel);

		settingsTree.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));

		settingsTree.setRootVisible(false);
		settingsTree.setShowsRootHandles(true);
		settingsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		settingsScroll = new JScrollPane(settingsTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Turn off the icons
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		settingsTree.setCellRenderer(renderer);

		// Expand all of the branch nodes
		settingsTree.expandPath(new TreePath(characterNode.getPath()));
		settingsTree.expandPath(new TreePath(pcGenNode.getPath()));
		settingsTree.expandPath(new TreePath(appearanceNode.getPath()));
		settingsTree.expandPath(new TreePath(gameModeNode.getPath()));
		settingsTree.expandPath(new TreePath(pluginNode.getPath()));

		// Add the listener which switches panels when a node of the tree is selected
		settingsTree.addTreeSelectionListener(e -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) settingsTree.getLastSelectedPathComponent();

			if (node == null)
			{
				return;
			}

			CardLayout cl = (CardLayout) (settingsPanel.getLayout());
			cl.show(settingsPanel, String.valueOf(node));
		});

		// Build the split pane
		splitPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, settingsScroll, settingsPanel, "Prefs");
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		return splitPane;
	}

	/**
	 * Add the panel to the tree as a child of the provided node. Also 
	 * add the panel to the settings panel indexed by title and to the 
	 * list of panels.
	 * 
	 * @param parent The node to add the panel to.
	 * @param prefsPanel The panel to be added.
	 */
	private void addPanelToTree(DefaultMutableTreeNode parent, PCGenPrefsPanel prefsPanel)
	{
		panelList.add(prefsPanel);
		parent.add(new DefaultMutableTreeNode(prefsPanel.getTitle()));
		JScrollPane rightScroll = new JScrollPane(prefsPanel);
		settingsPanel.add(rightScroll, prefsPanel.getTitle());
	}

	@Override
	public void cancelButtonActionPerformed()
	{
		resetOptionValues();
		super.cancelButtonActionPerformed();
	}

	private void resetOptionValues()
	{
		panelList.forEach(PCGenPrefsPanel::resetOptionValues);
	}

	@Override
	public void applyButtonActionPerformed()
	{
		setOptionsBasedOnControls();

		// We need to update the menus/toolbar since
		// some of those depend on the options
	}
}

