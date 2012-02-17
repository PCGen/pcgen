/*
 * PreferencesDialog.java
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
 *
 * Created on July 8th, 2002.
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui2.dialog;

import gmgen.pluginmgr.PluginManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.prefs.CharacterStatsPanel;
import pcgen.gui2.prefs.ColorsPanel;
import pcgen.gui2.prefs.CopySettingsPanel;
import pcgen.gui2.prefs.DisplayOptionsPanel;
import pcgen.gui2.prefs.EquipmentPanel;
import pcgen.gui2.prefs.ExperiencePanel;
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
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class PreferencesDialog extends JDialog
{
	private static final long serialVersionUID = 5042379023317257550L;

	// Resource strings
	private static String in_appearance =
			LanguageBundle.getString("in_Prefs_appearance");
	private static String in_character =
			LanguageBundle.getString("in_Prefs_character");
	private static String in_dialogTitle =
			LanguageBundle.getString("in_Prefs_title");
	private static String in_pcgen =
			LanguageBundle.getString("in_Prefs_pcgen");

	private DefaultTreeModel settingsModel;
	private FlippingSplitPane splitPane;

	private JPanel controlPanel;
	private JPanel settingsPanel;

	private JScrollPane settingsScroll;

	private JTree settingsTree;

	private List<PCGenPrefsPanel> panelList;
	
	// Character panels
	private PCGenPrefsPanel characterStatsPanel;
	private PCGenPrefsPanel hitPointsPanel;
	private PCGenPrefsPanel houseRulesPanel;
	private PCGenPrefsPanel monsterPanel;
	private PCGenPrefsPanel experiencePanel;

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

	//Plugins
	private PreferencesPluginsPanel pluginsPanel;

	private PreferencesDialog(JFrame parent, boolean modal)
	{
		super(parent, in_dialogTitle, modal);

		buildSettingsTreeAndPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(controlPanel, BorderLayout.SOUTH);

		applyOptionValuesToControls();
		settingsTree.setSelectionRow(1);

		pack();
	}

	public static void show(JFrame frame)
	{
		PreferencesDialog prefsDialog;

		prefsDialog = new PreferencesDialog(frame, true);
		prefsDialog.setLocationRelativeTo(frame);

		prefsDialog.setVisible(true);
	}

	private void addPluginPanes(DefaultMutableTreeNode rootNode,
		DefaultMutableTreeNode pluginNode)
	{
		if (pluginsPanel == null)
		{
			pluginsPanel = new PreferencesPluginsPanel();
		}
		JTabbedPane tpane = new JTabbedPane();
		tpane.add(pluginsPanel.toString(), pluginsPanel);
		settingsPanel.add(tpane, "Plugins");
		rootNode.add(pluginNode);
	}

	public void applyPluginPreferences()
	{
		pluginsPanel.applyPreferences();
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
			JOptionPane.showMessageDialog(getParent(), LanguageBundle
				.getString("in_Prefs_restartRequired"), LanguageBundle
				.getString("in_Prefs_pcgen"), JOptionPane.INFORMATION_MESSAGE);
		}
		
		// Now get any panels affected to refresh
//		CharacterInfo ci = PCGen_Frame1.getCharacterPane();
//		if (ci != null)
//		{
//			ci.refresh();
//		}
	}

	private void applyOptionValuesToControls()
	{
		for (PCGenPrefsPanel prefsPanel : panelList)
		{
			prefsPanel.applyOptionValuesToControls();
		}
		
		// Copy Settings
		copySettingsPanel.registerAffectedPanel(characterStatsPanel);
		copySettingsPanel.registerAffectedPanel(experiencePanel);
		copySettingsPanel.registerAffectedPanel(languagePanel);

	}

	private JPanel buildEmptyPanel(String title, String messageText)
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
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

	private void buildSettingsTreeAndPanel()
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode characterNode;
		DefaultMutableTreeNode pcGenNode;
		DefaultMutableTreeNode appearanceNode;
		DefaultMutableTreeNode gameModeNode;

		panelList = new ArrayList<PCGenPrefsPanel>(15);
		
		// Build the settings panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new CardLayout());

		// Build the selection tree
		characterNode = new DefaultMutableTreeNode(in_character);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_charTip")), in_character);

		characterStatsPanel = new CharacterStatsPanel(this);
		addPanelToTree(characterNode, characterStatsPanel);
		hitPointsPanel = new HitPointsPanel();
		addPanelToTree(characterNode, hitPointsPanel);
		houseRulesPanel = new HouseRulesPanel();
		addPanelToTree(characterNode, houseRulesPanel);
		monsterPanel = new MonsterPanel();
		addPanelToTree(characterNode, monsterPanel);
		experiencePanel = new ExperiencePanel();
		addPanelToTree(characterNode, experiencePanel);
		rootNode.add(characterNode);

		appearanceNode = new DefaultMutableTreeNode(in_appearance);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_appearanceTip")), in_appearance);

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

		pcGenNode = new DefaultMutableTreeNode(in_pcgen);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_pcgenTip")), in_pcgen);

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

		String in_gamemode =  LanguageBundle.getString("in_mnuSettingsCampaign");
		gameModeNode = new DefaultMutableTreeNode(in_gamemode);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_gameModeTip")), in_gamemode);

		copySettingsPanel = new CopySettingsPanel();
		addPanelToTree(gameModeNode, copySettingsPanel);
		rootNode.add(gameModeNode);
		
		DefaultMutableTreeNode pluginNode =
				new DefaultMutableTreeNode("Plugins");

		addPluginPanes(rootNode, pluginNode);

		settingsModel = new DefaultTreeModel(rootNode);
		settingsTree = new JTree(settingsModel);

		/*
		 * <!--
		 *    bug:     TreeView not displaying correctly with Kunststoff LaF
		 *    fix:     need to set a (wide enough) border
		 *    author:     Thomas Behr
		 *    date:     02/10/02
		 * -->
		 */
		if (UIManager.getLookAndFeel().getName()
			.equals("Kunststoff"))
		{
			settingsTree
				.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		}
		else
		{
			settingsTree.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		}

		settingsTree.setRootVisible(false);
		settingsTree.setShowsRootHandles(true);
		settingsTree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		settingsScroll =
				new JScrollPane(settingsTree,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
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
		settingsTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node =
						(DefaultMutableTreeNode) settingsTree
							.getLastSelectedPathComponent();

				if (node == null)
				{
					return;
				}

				CardLayout cl = (CardLayout) (settingsPanel.getLayout());
				cl.show(settingsPanel, String.valueOf(node));
			}
		});

		// Build the split pane
		splitPane =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					settingsScroll, settingsPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		// Build the control panel (OK/Cancel buttons)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton(LanguageBundle.getString("in_ok"));
		okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok"));
		controlPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed();
			}
		});

		JButton cancelButton =
				new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel"));
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});
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
		parent.add(new DefaultMutableTreeNode(prefsPanel
			.getTitle()));
		settingsPanel.add(prefsPanel, prefsPanel.getTitle());
	}

	private void cancelButtonActionPerformed()
	{
		resetOptionValues();
		
		setVisible(false);
		this.dispose();
	}
	
	private void resetOptionValues()
	{
		for (PCGenPrefsPanel prefsPanel : panelList)
		{
			prefsPanel.resetOptionValues();
		}
	}

	private void okButtonActionPerformed()
	{
		setOptionsBasedOnControls();
		applyPluginPreferences();
		setVisible(false);

		// We need to update the menus/toolbar since
		// some of those depend on the options
		//PCGen_Frame1.enableDisableMenuItems();
		this.dispose();
	}
}

/**
 *
 * @author  soulcatcher
 */
class PreferencesPluginsPanel extends gmgen.gui.PreferencesPanel {
	private final HashMap<String, PluginRef> pluginMap = new HashMap<String, PluginRef>();

	private JPanel mainPanel;
	private JScrollPane jScrollPane1;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesPluginsPanel() {
		for(PluginManager.PluginInfo info : PluginManager.getInstance().getPluginInfoList())
		{
			addPanel(info.logName, info.pluginName, info.pluginSystem);
		}
		initComponents();
		initPreferences();
	}

	public void applyPreferences() {
		for ( String key : pluginMap.keySet() )
		{
			pluginMap.get(key).applyPreferences();
		}
	}

	public void initPreferences() {
		for ( String key : pluginMap.keySet() )
		{
			pluginMap.get(key).initPreferences();
		}
	}

	@Override
	public String toString() {
		return "Plugin Launch";
	}

	private void initComponents() {
		jScrollPane1 = new JScrollPane();
		mainPanel = new JPanel();

		setLayout(new BorderLayout());

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		for ( String key : pluginMap.keySet() )
		{
			mainPanel.add( pluginMap.get(key) );
		}

		jScrollPane1.setViewportView(mainPanel);
		add(jScrollPane1, BorderLayout.CENTER);
		add(new JLabel("All changes will take effect the next time PCGen is restarted"), BorderLayout.SOUTH);
	}

	private void addPanel(String pluginName, String pluginTitle, String defaultSystem) {
		if(!pluginMap.containsKey(pluginName)) {
			PluginRef pluginRef = new PluginRef(pluginName, pluginTitle, defaultSystem);
			pluginMap.put(pluginName, pluginRef);
		}
	}

	private static class PluginRef extends JPanel {
		private String pluginName;
		private String pluginTitle;
		private String defaultSystem;
		private JCheckBox checkBox;
		private JRadioButton pcgenButton;
		private JRadioButton gmgenButton;

		public PluginRef(String pluginName, String pluginTitle, String defaultSystem) {
			this.pluginName = pluginName;
			this.pluginTitle = pluginTitle;
			this.defaultSystem = defaultSystem;
			initComponents();
		}

		private void initComponents() {
			checkBox = new JCheckBox();
			pcgenButton = new JRadioButton();
			gmgenButton = new JRadioButton();
			ButtonGroup pluginGroup = new ButtonGroup();

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(new TitledBorder(null, pluginTitle,
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 11)));

			checkBox.setText("Run this plugin?");
			add(checkBox);

			pcgenButton.setText("PCGen Window");
			pluginGroup.add(pcgenButton);
			add(pcgenButton);

			gmgenButton.setText("GMGen Window");
			pluginGroup.add(gmgenButton);
			add(gmgenButton);
		}

		public void initPreferences() {
			checkBox.setSelected(SettingsHandler.getGMGenOption(pluginName + ".Load", true));
			String system = SettingsHandler.getGMGenOption(pluginName + ".System", defaultSystem);
			if(system.equals(Constants.SYSTEM_PCGEN)) {
				pcgenButton.setSelected(true);
			}
			else {
				gmgenButton.setSelected(true);
			}
		}

		public void applyPreferences() {
			SettingsHandler.setGMGenOption(pluginName + ".Load", checkBox.isSelected());
			if(pcgenButton.isSelected()) {
				SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_PCGEN);
			}
			else {
				SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_PCGEN);
			}
		}
	}
}