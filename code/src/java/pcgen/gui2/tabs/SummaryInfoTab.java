/*
 * SummaryInfoTab.java
 * Copyright 2010 (C) Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Mar 22, 2010, 2:45:43 AM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.lang.StringUtils;

import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.GenderFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.SimpleFacade;
import pcgen.core.facade.TodoFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.dialog.CharacterHPDialog;
import pcgen.gui2.dialog.KitSelectionDialog;
import pcgen.gui2.dialog.RandomNameDialog;
import pcgen.gui2.dialog.SinglePrefDialog;
import pcgen.gui2.prefs.CharacterStatsPanel;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tabs.models.DeferredCharacterComboBoxModel;
import pcgen.gui2.tabs.models.FormattedFieldHandler;
import pcgen.gui2.tabs.models.TextFieldHandler;
import pcgen.gui2.tabs.summary.ClassLevelTableModel;
import pcgen.gui2.tabs.summary.InfoPaneHandler;
import pcgen.gui2.tabs.summary.LanguageTableModel;
import pcgen.gui2.tabs.summary.StatTableModel;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.FacadeComboBoxModel;
import pcgen.gui2.util.SignIcon;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.SimpleTextIcon;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
@SuppressWarnings("serial")
public class SummaryInfoTab extends JPanel implements CharacterInfoTab, TodoHandler
{

	private static final Font titleFont = new Font("Verdana", Font.BOLD, 15); //$NON-NLS-1$
	private static final Font labelFont = new Font("Verdana", Font.BOLD, 12); //$NON-NLS-1$
	private static final Font textFont = new Font("Verdana", Font.PLAIN, 12); //$NON-NLS-1$
	private static final Font smallFont = new Font("Verdana", Font.PLAIN, 9); //$NON-NLS-1$
	private final TabTitle tabTitle;
	private final JPanel basicsPanel;
	private final JPanel todoPanel;
	private final JPanel scoresPanel;
	private final JPanel racePanel;
	private final JPanel classPanel;
	private final JTextField characterNameField;
	private final JComboBox characterTypeComboBox;
	private final JTextField playerNameField;
	private final JTextField tabLabelField;
	private final JFormattedTextField ageField;
	private final JFormattedTextField expField;
	private final JFormattedTextField nextlevelField;
	private final JComboBox xpTableComboBox;
	private final JFormattedTextField expmodField;
	private final JFormattedTextField addLevelsField;
	private final JFormattedTextField removeLevelsField;
	private final JTable statsTable;
	private final JTable classLevelTable;
	private final JTable languageTable;
	private final JComboBox genderComboBox;
	private final JComboBox handsComboBox;
	private final JComboBox alignmentComboBox;
	private final JComboBox deityComboBox;
	private final JComboBox raceComboBox;
	private final JComboBox ageComboBox;
	private final JComboBox classComboBox;
	private final JButton generateRollsButton;
	private final JButton rollMethodButton;
	private final JButton createMonsterButton;
	private final JButton expaddButton;
	private final JButton expsubtractButton;
	private final JButton addLevelsButton;
	private final JButton removeLevelsButton;
	private final JButton hpButton;
	private final JLabel totalHPLabel;
	private final JEditorPane infoPane;
	private final JLabel statTotalLabel;
	private final JLabel statTotal;
	private final JLabel modTotalLabel;
	private final JLabel modTotal;
	private final JEditorPane todoPane;
	private final JButton random;
	private JScrollPane langScroll;

	public SummaryInfoTab()
	{
		this.tabTitle = new TabTitle("in_summary"); //$NON-NLS-1$
		this.basicsPanel = new JPanel();
		this.todoPanel = new JPanel();
		this.scoresPanel = new JPanel();
		this.racePanel = new JPanel();
		this.classPanel = new JPanel();
		this.characterNameField = new JTextField();
		this.characterTypeComboBox = new JComboBox();
		this.random = new JButton();
		this.playerNameField = new JTextField();
		this.expField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.nextlevelField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.xpTableComboBox = new JComboBox();
		this.expmodField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.addLevelsField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.removeLevelsField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.statsTable = new JTable();
		this.classLevelTable = new JTable();
		this.languageTable = new JTable();
		this.genderComboBox = new JComboBox();
		this.handsComboBox = new JComboBox();
		this.alignmentComboBox = new JComboBox();
		this.deityComboBox = new JComboBox();
		this.raceComboBox = new JComboBox();
		this.ageComboBox = new JComboBox();
		this.classComboBox = new JComboBox();
		this.tabLabelField = new JTextField();
		this.ageField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		this.generateRollsButton = new JButton();
		this.rollMethodButton = new JButton();
		this.createMonsterButton = new JButton();
		this.addLevelsButton = new JButton();
		this.removeLevelsButton = new JButton();
		this.expaddButton = new JButton();
		this.expsubtractButton = new JButton();
		this.hpButton = new JButton();
		this.totalHPLabel = new JLabel();
		this.infoPane = new JEditorPane();
		this.statTotalLabel = new JLabel();
		this.statTotal = new JLabel();
		this.modTotalLabel = new JLabel();
		this.modTotal = new JLabel();
		this.todoPane = new JEditorPane();
		initComponents();
	}

	private void initComponents()
	{
		this.setFocusCycleRoot(true);
		this.setFocusTraversalPolicyProvider(true);
		this.setFocusTraversalPolicy(new SummaryTabFocusTraversalPolicy());
		setFont(textFont);

		LanguageTableModel.initializeTable(languageTable);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		setPanelTitle(basicsPanel, LanguageBundle.getString("in_sumCharacterBasics")); //$NON-NLS-1$
		basicsPanel.setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.1;
		gbc.weighty = .7;
		add(basicsPanel, gbc);

		setPanelTitle(todoPanel, LanguageBundle.getString("in_tipsString")); //$NON-NLS-1$
		initTodoPanel(todoPanel);
		gbc.gridy = 1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		add(todoPanel, gbc);

		setPanelTitle(scoresPanel, LanguageBundle.getString("in_sumAbilityScores")); //$NON-NLS-1$
		initMiddlePanel(scoresPanel);
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weightx = 1;
		add(scoresPanel, gbc);

		JPanel rightPanel = new JPanel();
		setPanelTitle(racePanel, LanguageBundle.getString("in_raceString")); //$NON-NLS-1$
		setPanelTitle(classPanel, LanguageBundle.getString("in_sumClassLevel")); //$NON-NLS-1$
		initRightPanel(rightPanel);
		gbc.weightx = .1;
		gbc.weighty = 1;
		add(rightPanel, gbc);
	}

	private void setPanelTitle(JComponent panel, String title)
	{
		panel.setBorder(BorderFactory.createTitledBorder(null,
														 title,
														 TitledBorder.CENTER,
														 TitledBorder.DEFAULT_POSITION,
														 titleFont));
	}

	/**
	 * Initialise the "Things to be Done" panel. Creates the required 
	 * components and places them in the panel.
	 * @param panel The panel to be initialised
	 */
	private void initTodoPanel(JPanel panel)
	{
		panel.setLayout(new BorderLayout());
		todoPane.setOpaque(false);
		todoPane.setContentType("text/html"); //$NON-NLS-1$
		String bodyRule =
				"body { font-family: " + textFont.getFamily() + "; " + "font-size: " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				textFont.getSize() + "pt; }"; //$NON-NLS-1$
		((HTMLDocument) todoPane.getDocument()).getStyleSheet().addRule(
				bodyRule);
		todoPane.setEditable(false);

		JScrollPane scroll = new JScrollPane(todoPane);
		scroll.setBorder(BorderFactory.createEmptyBorder());

		panel.add(scroll, BorderLayout.CENTER);
	}

	private void initMiddlePanel(JPanel middlePanel)
	{
		middlePanel.setLayout(new GridLayout(2, 1));

		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

		StatTableModel.initializeTable(statsTable);
		JScrollPane pane =
				new JScrollPane(statsTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
		{

			@Override
			public Dimension getMaximumSize()
			{
				//This prevents the scroll pane from taking up more space than it needs.
				return super.getPreferredSize();
			}

		};
		pane.setBorder(BorderFactory.createEmptyBorder());
		JPanel statsBox =new JPanel();
		statsBox.setLayout(new BoxLayout(statsBox, BoxLayout.X_AXIS));
		statsBox.add(Box.createHorizontalGlue());
		statsBox.add(pane);
		statsBox.add(Box.createHorizontalGlue());
		statsPanel.add(statsBox);

		JPanel statTotalPanel = new JPanel();
		statTotalPanel.setLayout(new BoxLayout(statTotalPanel, BoxLayout.X_AXIS));
		//this makes box layout use the statTotalPanel to distribute extra space
		statTotalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		statTotalPanel.add(Box.createHorizontalGlue());
		statTotalLabel.setFont(labelFont);
		statTotalPanel.add(statTotalLabel);
		statTotal.setFont(textFont);
		statTotalPanel.add(statTotal);
		modTotalLabel.setFont(labelFont);
		statTotalPanel.add(modTotalLabel);
		modTotal.setFont(textFont);
		statTotalPanel.add(modTotal);
		statTotalPanel.add(Box.createHorizontalGlue());
		generateRollsButton.setText(LanguageBundle.getString("in_sumGenerate_Rolls")); //$NON-NLS-1$
		statTotalPanel.add(generateRollsButton);
		rollMethodButton.setText(LanguageBundle.getString("in_sumRoll_Method")); //$NON-NLS-1$
		statTotalPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		statTotalPanel.add(rollMethodButton);
		statTotalPanel.add(Box.createHorizontalGlue());
		statsPanel.add(statTotalPanel);

		middlePanel.add(statsPanel);

		InfoPaneHandler.initializeEditorPane(infoPane);

		pane = new JScrollPane(infoPane);
		pane.setBorder(BorderFactory.createEmptyBorder());
		middlePanel.add(pane);
	}

	private void initRightPanel(JPanel rightPanel)
	{
		rightPanel.setLayout(new GridBagLayout());
		/*
		 * initialize Components
		 */
		racePanel.setOpaque(false);
		classPanel.setOpaque(false);
		ageField.setHorizontalAlignment(SwingConstants.RIGHT);
		expField.setHorizontalAlignment(SwingConstants.RIGHT);
		nextlevelField.setHorizontalAlignment(SwingConstants.RIGHT);
		nextlevelField.setEnabled(false);
		expmodField.setHorizontalAlignment(SwingConstants.RIGHT);

		raceComboBox.setPrototypeDisplayValue("PrototypeDisplayValue"); //$NON-NLS-1$
		classComboBox.setPrototypeDisplayValue("PrototypeDisplayValue"); //$NON-NLS-1$

		expaddButton.setMargin(new Insets(0, 8, 0, 8));
		expsubtractButton.setMargin(new Insets(0, 8, 0, 8));
		hpButton.setMargin(new Insets(0, 0, 0, 0));

		JPanel expmodPanel = new JPanel(new GridBagLayout());
		JPanel levelPanel = new JPanel();
		JLabel raceLabel = createLabel("in_sumRace"); //$NON-NLS-1$
		JLabel ageLabel = createLabel("in_sumAge"); //$NON-NLS-1$
		JLabel classLabel = createLabel("in_sumClass"); //$NON-NLS-1$
		JLabel hpLabel = createLabel("in_sumTotalHP"); //$NON-NLS-1$
		JLabel expLabel = createLabel("in_sumCurrentXp"); //$NON-NLS-1$
		JLabel nextlevelLabel = createLabel("in_sumNextlevel"); //$NON-NLS-1$
		JLabel xpTableLabel = createLabel("in_sumXpTable"); //$NON-NLS-1$
		JLabel expmodLabel = createLabel("in_sumExpMod"); //$NON-NLS-1$
		expmodLabel.setHorizontalAlignment(SwingConstants.CENTER);
		initLevelPanel(levelPanel);
		/*
		 * initialize constant variables
		 */
		Insets racePanelInsets = racePanel.getInsets();
		Insets classPanelInsets = classPanel.getInsets();
		/*
		 * racePanel
		 */
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(racePanelInsets.top, racePanelInsets.left, 0, 0);
		gbc.gridwidth = 2;
		rightPanel.add(raceLabel, gbc);
		gbc.insets = new Insets(racePanelInsets.top, 1, 1, racePanelInsets.right);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		rightPanel.add(raceComboBox, gbc);
		gbc.insets = new Insets(0, racePanelInsets.left, 0, 1);
		gbc.gridwidth = 1;
		rightPanel.add(ageLabel, gbc);
		gbc.insets = new Insets(1, 1, 1, 1);
		rightPanel.add(ageField, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(1, 1, 1, racePanelInsets.right);
		rightPanel.add(ageComboBox, gbc);
		gbc.insets = new Insets(1, racePanelInsets.left, racePanelInsets.bottom, racePanelInsets.right);
		rightPanel.add(createMonsterButton, gbc);
		/*
		 * classPanel
		 */
		gbc.gridwidth = 2;
		gbc.insets = new Insets(classPanelInsets.top, classPanelInsets.left, 0, 0);
		rightPanel.add(classLabel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(classPanelInsets.top, 0, 0, classPanelInsets.right);
		rightPanel.add(classComboBox, gbc);

		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(7, classPanelInsets.left, 0, classPanelInsets.right);
		rightPanel.add(levelPanel, gbc);
		gbc.insets.top = 0;
		gbc.insets.bottom = 10;
		gbc.weighty = 0;
		{
			JPanel hpPanel = new JPanel(new FlowLayout());
			hpPanel.add(hpLabel);
			hpPanel.add(Box.createHorizontalStrut(3));
			hpPanel.add(totalHPLabel);
			hpPanel.add(hpButton);
			rightPanel.add(hpPanel, gbc);
		}
		gbc.insets.bottom = 0;
		//gbc.ipady = 20;
		//rightPanel.add(new TitledSeparator("Experience", TitledBorder.CENTER, TitledSeparator.DEFAULT_POSITION, labelFont), gbc);
		GridBagConstraints leftgbc = new GridBagConstraints();
		leftgbc.insets = new Insets(0, classPanelInsets.left, 0, 0);
		leftgbc.gridwidth = 2;
		leftgbc.fill = GridBagConstraints.BOTH;

		GridBagConstraints rightgbc = new GridBagConstraints();
		rightgbc.insets = new Insets(0, 0, 0, classPanelInsets.right);
		rightgbc.gridwidth = GridBagConstraints.REMAINDER;
		rightgbc.fill = GridBagConstraints.BOTH;

		rightPanel.add(expLabel, leftgbc);
		rightPanel.add(expField, rightgbc);
		rightPanel.add(nextlevelLabel, leftgbc);
		rightPanel.add(nextlevelField, rightgbc);
		rightPanel.add(xpTableLabel, leftgbc);
		rightPanel.add(xpTableComboBox, rightgbc);

		gbc.insets.top = 10;
		rightPanel.add(expmodLabel, gbc);
		{
			GridBagConstraints gbc2 = new GridBagConstraints();
			gbc2.fill = GridBagConstraints.HORIZONTAL;
			gbc2.weightx = 1.0;
			gbc2.insets = new Insets(0, 1, 0, 1);
			expmodPanel.add(expaddButton, gbc2);
			expmodPanel.add(expsubtractButton, gbc2);
		}
		leftgbc.insets.bottom = classPanelInsets.bottom;
		leftgbc.weightx = 0.3;
		rightPanel.add(expmodPanel, leftgbc);
		rightgbc.insets.bottom = classPanelInsets.bottom;
		rightgbc.weightx = 0.7;
		rightPanel.add(expmodField, rightgbc);

		gbc = new GridBagConstraints();
		gbc.gridx = gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 3;
		gbc.fill = GridBagConstraints.BOTH;
		rightPanel.add(racePanel, gbc);

		gbc.gridy = 3;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		rightPanel.add(classPanel, gbc);
	}

	private void initLevelPanel(JPanel panel)
	{
		panel.setLayout(new GridBagLayout());
		JLabel addLabel = createLabel("in_sumAddLevels"); //$NON-NLS-1$
		JLabel removeLabel = createLabel("in_sumRemoveLevels"); //$NON-NLS-1$
		JLabel darrowLabel = new JLabel(Icons.createImageIcon("button_arrow_down.png")); //$NON-NLS-1$
		JLabel uarrowLabel = new JLabel(Icons.createImageIcon("button_arrow_up.png")); //$NON-NLS-1$

		addLevelsButton.setMargin(new Insets(0, 8, 0, 8));
		addLevelsField.setValue(1);
		addLevelsField.setHorizontalAlignment(SwingConstants.RIGHT);
		removeLevelsButton.setMargin(new Insets(0, 8, 0, 8));
		removeLevelsField.setValue(1);
		removeLevelsField.setHorizontalAlignment(SwingConstants.RIGHT);

		GridBagConstraints gbc1 = new GridBagConstraints();
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc1.weightx = gbc2.weightx = 0.5;
		gbc1.insets = new Insets(1, 0, 1, 0);
		gbc2.insets = new Insets(1, 0, 1, 0);
		gbc2.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(addLabel, gbc1);
		panel.add(removeLabel, gbc2);
		gbc1.ipadx = 30;
		panel.add(addLevelsField, gbc1);
		gbc2.ipadx = 30;
		panel.add(removeLevelsField, gbc2);
		gbc1.ipadx = 0;
		panel.add(addLevelsButton, gbc1);
		gbc2.ipadx = 0;
		panel.add(removeLevelsButton, gbc2);
		panel.add(darrowLabel, gbc1);
		panel.add(uarrowLabel, gbc2);

		ClassLevelTableModel.initializeTable(classLevelTable);
		gbc2.weightx = 0;
		gbc2.weighty = 1;
		gbc2.fill = GridBagConstraints.BOTH;
		panel.add(new JScrollPane(classLevelTable), gbc2);
	}

	private static JLabel createLabel(String text)
	{
		JLabel label = new JLabel(LanguageBundle.getString(text));
		label.setFont(labelFont);
		return label;
	}

	private void resetBasicsPanel()
	{
		basicsPanel.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		{
			JLabel label = createLabel("in_sumName"); //$NON-NLS-1$
			gbc.anchor = java.awt.GridBagConstraints.WEST;
			gbc.insets = new Insets(0, 0, 3, 0);
			basicsPanel.add(label, gbc);

			random.setText(LanguageBundle.getString("in_randomButton")); //$NON-NLS-1$
			random.setMargin(new Insets(0, 0, 0, 0));
			random.setFont(smallFont);
			gbc.insets = new Insets(0, 2, 3, 2);
			basicsPanel.add(random, gbc);

			gbc.insets = new Insets(0, 0, 3, 2);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			basicsPanel.add(characterNameField, gbc);
		}
		Insets insets = new Insets(0, 0, 3, 2);
		addGridBagLayer(basicsPanel, labelFont, insets, "in_sumCharType", characterTypeComboBox); //$NON-NLS-1$
		addGridBagLayer(basicsPanel, labelFont, insets, "in_sumPlayer", playerNameField); //$NON-NLS-1$
		addGridBagLayer(basicsPanel, labelFont, insets, "in_sumTabLabel", tabLabelField); //$NON-NLS-1$
		if (genderComboBox.getModel().getSize() != 0)
		{
			addGridBagLayer(basicsPanel, labelFont, insets, "in_sumGender", genderComboBox); //$NON-NLS-1$
		}
		if (handsComboBox.getModel().getSize() != 0)
		{
			addGridBagLayer(basicsPanel, labelFont, insets, "in_sumHanded", handsComboBox); //$NON-NLS-1$
		}
		if (alignmentComboBox.getModel().getSize() != 0)
		{
			addGridBagLayer(basicsPanel, labelFont, insets, "in_sumAlignment", alignmentComboBox); //$NON-NLS-1$
		}
		if (deityComboBox.getModel().getSize() != 0)
		{
			addGridBagLayer(basicsPanel, labelFont, insets, "in_domDeityLabel", deityComboBox); //$NON-NLS-1$
		}

		gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.insets = new Insets(6, 2, 2, 2);
		langScroll = new JScrollPane(languageTable);
		basicsPanel.add(langScroll, gbc);
		basicsPanel.revalidate();
	}

	private void addGridBagLayer(JPanel panel, Font font, Insets insets, String text, JComponent comp)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel label = new JLabel(LanguageBundle.getString(text));
		label.setFont(font);
		gbc.anchor = java.awt.GridBagConstraints.WEST;
		gbc.gridwidth = 2;
		panel.add(label, gbc);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		if (insets != null)
		{
			gbc.insets = insets;
		}
		panel.add(comp, gbc);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		if ("Name".equals(fieldName)) //$NON-NLS-1$
		{
			characterNameField.requestFocusInWindow();
			characterNameField.selectAll();
		}
		else if ("Race".equals(fieldName)) //$NON-NLS-1$
		{
			raceComboBox.requestFocusInWindow();
			highlightBorder(raceComboBox);
		}
		else if ("Class".equals(fieldName)) //$NON-NLS-1$
		{
			classComboBox.requestFocusInWindow();
			highlightBorder(classComboBox);
		}
		else if ("Languages".equals(fieldName)) //$NON-NLS-1$
		{
			highlightBorder(langScroll);
		}
		else if ("Ability Scores".equals(fieldName)) //$NON-NLS-1$
		{
			deityComboBox.requestFocusInWindow();
			deityComboBox.transferFocus();
		}

	}

	private void highlightBorder(final JComponent comp)
	{
		final Border oldBorder = comp.getBorder();
		Border highlightBorder = BorderFactory.createLineBorder(Color.GREEN, 3);
		comp.setBorder(highlightBorder);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
					// Ignored as we'll exit shortly anyway.
				}
				comp.setBorder(oldBorder);
			}
		});
	}

	@Override
	public Hashtable<Object, Object> createModels(final CharacterFacade character)
	{
		final CharacterComboBoxModel<GenderFacade> genderModel;
		final CharacterComboBoxModel<SimpleFacade> handsModel;
		final CharacterComboBoxModel<AlignmentFacade> alignmentModel;
		final CharacterComboBoxModel<DeityFacade> deityModel;
		final DeferredCharacterComboBoxModel<RaceFacade> raceModel;
		final CharacterComboBoxModel<SimpleFacade> ageCatModel;
		final FacadeComboBoxModel<ClassFacade> classModel;
		final CharacterComboBoxModel<String> xpTableModel;
		final CharacterComboBoxModel<String> characterTypeModel;

		characterTypeModel = new CharacterComboBoxModel<String>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setCharacterType((String) anItem);
			}

		};
		genderModel = new CharacterComboBoxModel<GenderFacade>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setGender((GenderFacade) anItem);
			}

		};
		handsModel = new CharacterComboBoxModel<SimpleFacade>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setHanded((SimpleFacade) anItem);
			}

		};
		alignmentModel = new CharacterComboBoxModel<AlignmentFacade>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setAlignment((AlignmentFacade) anItem);
			}

		};
		deityModel = new CharacterComboBoxModel<DeityFacade>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setDeity((DeityFacade) anItem);
			}

		};
		raceModel = new DeferredCharacterComboBoxModel<RaceFacade>()
		{

			@Override
			public void commitSelectedItem(Object anItem)
			{
				character.setRace((RaceFacade) anItem);
			}

		};
		ageCatModel = new CharacterComboBoxModel<SimpleFacade>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setAgeCategory((SimpleFacade) anItem);
			}

		};
		xpTableModel = new CharacterComboBoxModel<String>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setXPTable((String) anItem);
			}

		};

		classModel = new FacadeComboBoxModel<ClassFacade>();

		LabelHandler statTotalLabelHandler = new LabelHandler(statTotalLabel);
		LabelHandler statTotalHandler = new LabelHandler(statTotal);
		LabelHandler modTotalLabelHandler = new LabelHandler(modTotalLabel);
		LabelHandler modTotalHandler = new LabelHandler(modTotal);

		DataSetFacade dataset = character.getDataSet();

		//initialize character type model
		characterTypeModel.setListFacade(dataset.getCharacterTypes());
		characterTypeModel.setReference(character.getCharacterTypeRef());
		//initialize alignment model
		alignmentModel.setListFacade(dataset.getAlignments());
		alignmentModel.setReference(character.getAlignmentRef());
		//initialize deity model
		deityModel.setListFacade(dataset.getDeities());
		deityModel.setReference(character.getDeityRef());

		genderModel.setReference(character.getGenderRef());

		raceModel.setListFacade(dataset.getRaces());
		ageCatModel.setListFacade(character.getAgeCategories());
		classModel.setListFacade(dataset.getClasses());

		//initialize gender and hands models
		handsModel.setReference(character.getHandedRef());
		ageCatModel.setReference(character.getAgeCategoryRef());

		statTotalLabelHandler.setReference(character.getStatTotalLabelTextRef());
		statTotalHandler.setReference(character.getStatTotalTextRef());
		modTotalLabelHandler.setReference(character.getModTotalLabelTextRef());
		modTotalHandler.setReference(character.getModTotalTextRef());

		//initialize XP table model
		xpTableModel.setListFacade(dataset.getXPTableNames());
		xpTableModel.setReference(character.getXPTableNameRef());

		ReferenceFacade<RaceFacade> raceRef = character.getRaceRef();
		ReferenceListener<Object> raceListener = new ReferenceListener<Object>()
		{

			@Override
			public void referenceChanged(ReferenceEvent<Object> e)
			{
				RaceFacade race = character.getRaceRef().getReference();
				if (race != null)
				{
					genderModel.setListFacade(race.getGenders());
					handsModel.setListFacade(race.getHands());
					resetBasicsPanel();
				}
				else
				{
					genderModel.setListFacade(new DefaultListFacade<GenderFacade>());
					handsModel.setListFacade(new DefaultListFacade<SimpleFacade>());
				}
			}

		};
		raceListener.referenceChanged(null);
		raceRef.addReferenceListener(raceListener);
		raceModel.setReference(raceRef);

		//Manages the character name text field
		TextFieldHandler charNameHandler = new TextFieldHandler(characterNameField, character.getNameRef())
		{

			@Override
			protected void textChanged(String text)
			{
				character.setName(text);
			}

		};

		//Manages the player name text field.
		TextFieldHandler playerNameHandler = new TextFieldHandler(playerNameField, character.getPlayersNameRef())
		{

			@Override
			protected void textChanged(String text)
			{
				character.setPlayersName(text);
			}

		};
		//Manages the tab name text field.
		TextFieldHandler tabNameHandler = new TextFieldHandler(tabLabelField, character.getTabNameRef())
		{

			@Override
			protected void textChanged(String text)
			{
				character.setTabName(text);
			}

		};

		/**
		 * Handler for the Age field. This listens for and
		 * processes both changes to the value from the character and
		 * modifications to the field made by the user.
		 */
		FormattedFieldHandler ageHandler = new FormattedFieldHandler(ageField, character.getAgeRef())
		{

			@Override
			protected void valueChanged(int value)
			{
				character.setAge(value);
			}

		};

		/**
		 * Handler for the Current Experience field. This listens for and
		 * processes both changes to the value from the character and
		 * modifications to the field made by the user.
		 */
		FormattedFieldHandler expHandler = new FormattedFieldHandler(expField, character.getXPRef())
		{

			@Override
			protected void valueChanged(int value)
			{
				character.setXP(value);
			}

		};

		/**
		 * Handler for the Next Level field. This is a read-only field so the
		 * handler only listens for changes to the value from the character.
		 */
		FormattedFieldHandler nextLevelHandler = new FormattedFieldHandler(nextlevelField, character.getXPForNextLevelRef())
		{

			@Override
			protected void valueChanged(int value)
			{
				//This will never be called
			}

		};
		Hashtable<Object, Object> stateTable = new Hashtable<Object, Object>();
		stateTable.put(Models.CharacterNameHandler, charNameHandler);
		stateTable.put(Models.CharacterTypeComboBoxModel, characterTypeModel);
		stateTable.put(Models.PlayerNameHandler, playerNameHandler);
		stateTable.put(Models.TabNameHandler, tabNameHandler);
		stateTable.put(Models.GenderComboBoxModel, genderModel);
		stateTable.put(Models.HandsComboBoxModel, handsModel);
		stateTable.put(Models.AlignmentComboBoxModel, alignmentModel);
		stateTable.put(Models.DeityComboBoxModel, deityModel);
		stateTable.put(Models.RaceComboBoxModel, raceModel);
		stateTable.put(Models.AgeCatComboBoxModel, ageCatModel);
		stateTable.put(Models.ClassComboBoxModel, classModel);

		stateTable.put(Models.RandomNameAction, new RandomNameAction(character,
																	 (JFrame) SwingUtilities.getWindowAncestor(this)));
		stateTable.put(Models.ClassLevelTableModel, new ClassLevelTableModel(character));
		stateTable.put(Models.GenerateRollsAction, new GenerateRollsAction(character));
		stateTable.put(Models.RollMethodAction, new RollMethodAction(
				(JFrame) SwingUtilities.getWindowAncestor(this), character));
		stateTable.put(Models.CreateMonsterAction, new CreateMonsterAction(
			character, (JFrame) SwingUtilities.getWindowAncestor(this)));
		stateTable.put(Models.AddLevelsAction, new AddLevelsAction(character));
		stateTable.put(Models.RemoveLevelsAction, new RemoveLevelsAction(character));
		stateTable.put(Models.StatTableModel, new StatTableModel(character, statsTable));
		stateTable.put(Models.LanguageTableModel, new LanguageTableModel(character));
		stateTable.put(Models.InfoPaneHandler, new InfoPaneHandler(character, infoPane));
		stateTable.put(Models.ClassComboBoxRenderer, new ClassBoxRenderer(character));
		stateTable.put(Models.InfoComboBoxRenderer, new InfoBoxRenderer(character));
		stateTable.put(Models.AgeHandler, ageHandler);
		stateTable.put(Models.ExpHandler, expHandler);
		stateTable.put(Models.NextLevelHandler, nextLevelHandler);
		stateTable.put(Models.XPTableComboBoxModel, xpTableModel);
		stateTable.put(Models.ExpAddAction, new ExpAddAction(character));
		stateTable.put(Models.ExpSubtractAction, new ExpSubtractAction(character));
		stateTable.put(Models.StatTotalLabelHandler, statTotalLabelHandler);
		stateTable.put(Models.StatTotalHandler, statTotalHandler);
		stateTable.put(Models.ModTotalLabelHandler, modTotalLabelHandler);
		stateTable.put(Models.ModTotalHandler, modTotalHandler);
		stateTable.put(Models.TodoListHandler, new TodoListHandler(character));
		stateTable.put(Models.HPHandler, new HPHandler(character));
		return stateTable;
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private enum Models
	{

		CharacterNameHandler,
		RandomNameAction,
		PlayerNameHandler,
		TabNameHandler,
		CharacterTypeComboBoxModel,
		GenderComboBoxModel,
		HandsComboBoxModel,
		AlignmentComboBoxModel,
		DeityComboBoxModel,
		InfoComboBoxRenderer,
		RaceComboBoxModel,
		ClassComboBoxModel,
		ClassComboBoxRenderer,
		ClassLevelTableModel,
		GenerateRollsAction,
		RollMethodAction,
		AddLevelsAction,
		RemoveLevelsAction,
		StatTableModel,
		LanguageTableModel,
		InfoPaneHandler,
		AgeHandler,
		AgeCatComboBoxModel,
		ExpHandler,
		ExpAddAction,
		ExpSubtractAction,
		NextLevelHandler,
		XPTableComboBoxModel,
		StatTotalLabelHandler,
		StatTotalHandler, 
		ModTotalLabelHandler, 
		ModTotalHandler,
		TodoListHandler,
		HPHandler, 
		CreateMonsterAction
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((TextFieldHandler) state.get(Models.CharacterNameHandler)).uninstall();
		((TextFieldHandler) state.get(Models.PlayerNameHandler)).uninstall();
		((TextFieldHandler) state.get(Models.TabNameHandler)).uninstall();
		((LanguageTableModel) state.get(Models.LanguageTableModel)).uninstall();
		((ClassLevelTableModel) state.get(Models.ClassLevelTableModel)).uninstall();
		((InfoPaneHandler) state.get(Models.InfoPaneHandler)).uninstall();
		((StatTableModel) state.get(Models.StatTableModel)).uninstall();
		((FormattedFieldHandler) state.get(Models.AgeHandler)).uninstall();
		((FormattedFieldHandler) state.get(Models.ExpHandler)).uninstall();
		((FormattedFieldHandler) state.get(Models.NextLevelHandler)).uninstall();
		((LabelHandler) state.get(Models.StatTotalLabelHandler)).uninstall();
		((LabelHandler) state.get(Models.StatTotalHandler)).uninstall();
		((TodoListHandler) state.get(Models.TodoListHandler)).uninstall();
		((GenerateRollsAction) state.get(Models.GenerateRollsAction)).uninstall();
		((RollMethodAction) state.get(Models.RollMethodAction)).uninstall();
		((HPHandler) state.get(Models.HPHandler)).uninstall();
		
		raceComboBox.removeFocusListener((DeferredCharacterComboBoxModel<RaceFacade>) state.get(Models.RaceComboBoxModel));
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		((TextFieldHandler) state.get(Models.CharacterNameHandler)).install();
		((TextFieldHandler) state.get(Models.PlayerNameHandler)).install();
		((TextFieldHandler) state.get(Models.TabNameHandler)).install();
		((InfoPaneHandler) state.get(Models.InfoPaneHandler)).install();
		((LanguageTableModel) state.get(Models.LanguageTableModel)).install(languageTable);
		((StatTableModel) state.get(Models.StatTableModel)).install();
		((ClassLevelTableModel) state.get(Models.ClassLevelTableModel)).install(classLevelTable, classComboBox);
		((FormattedFieldHandler) state.get(Models.AgeHandler)).install();
		((FormattedFieldHandler) state.get(Models.ExpHandler)).install();
		((FormattedFieldHandler) state.get(Models.NextLevelHandler)).install();
		((LabelHandler) state.get(Models.StatTotalLabelHandler)).install();
		((LabelHandler) state.get(Models.StatTotalHandler)).install();
		((LabelHandler) state.get(Models.ModTotalLabelHandler)).install();
		((LabelHandler) state.get(Models.ModTotalHandler)).install();
		((TodoListHandler) state.get(Models.TodoListHandler)).install();
		((GenerateRollsAction) state.get(Models.GenerateRollsAction)).install();
		((RollMethodAction) state.get(Models.RollMethodAction)).install();
		((HPHandler) state.get(Models.HPHandler)).install();

		characterTypeComboBox.setModel((ComboBoxModel) state.get(Models.CharacterTypeComboBoxModel));
		genderComboBox.setModel((ComboBoxModel) state.get(Models.GenderComboBoxModel));
		handsComboBox.setModel((ComboBoxModel) state.get(Models.HandsComboBoxModel));
		alignmentComboBox.setModel((ComboBoxModel) state.get(Models.AlignmentComboBoxModel));
		deityComboBox.setModel((ComboBoxModel) state.get(Models.DeityComboBoxModel));
		deityComboBox.setRenderer((ListCellRenderer) state.get(Models.InfoComboBoxRenderer));
		raceComboBox.setModel((ComboBoxModel) state.get(Models.RaceComboBoxModel));
		raceComboBox.addFocusListener((DeferredCharacterComboBoxModel<RaceFacade>) state.get(Models.RaceComboBoxModel));
		raceComboBox.setRenderer((ListCellRenderer) state.get(Models.InfoComboBoxRenderer));
		ageComboBox.setModel((ComboBoxModel) state.get(Models.AgeCatComboBoxModel));
		classComboBox.setModel((ComboBoxModel) state.get(Models.ClassComboBoxModel));
		classComboBox.setRenderer((ListCellRenderer) state.get(Models.ClassComboBoxRenderer));
		random.setAction((Action) state.get(Models.RandomNameAction));
		GenerateRollsAction genRollsAction = (GenerateRollsAction) state.get(Models.GenerateRollsAction);
		generateRollsButton.setAction(genRollsAction);
		RollMethodAction rollMethodAction = (RollMethodAction) state.get(Models.RollMethodAction);
		rollMethodButton.setAction(rollMethodAction);
		createMonsterButton.setAction((Action) state.get(Models.CreateMonsterAction));
		addLevelsButton.setAction((Action) state.get(Models.AddLevelsAction));
		removeLevelsButton.setAction((Action) state.get(Models.RemoveLevelsAction));
		xpTableComboBox.setModel((ComboBoxModel) state.get(Models.XPTableComboBoxModel));

		expaddButton.setAction((Action) state.get(Models.ExpAddAction));
		expsubtractButton.setAction((Action) state.get(Models.ExpSubtractAction));
		((AddLevelsAction) state.get(Models.AddLevelsAction)).install();
		resetBasicsPanel();
	}

	private static class ComboBoxRenderer extends DefaultListCellRenderer
	{

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setToolTipText(value == null ? null : value.toString());
			return this;
		}

	}

	private class InfoBoxRenderer extends ComboBoxRenderer
	{

		private CharacterFacade character;

		public InfoBoxRenderer(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof InfoFacade && !character.isQualifiedFor((InfoFacade) value))
			{
				if (index == -1)
				{// this is a hack to prevent the combobox from overwriting the text color
					setText("");
					setIcon(new SimpleTextIcon(list, value.toString(), UIPropertyContext.getNotQualifiedColor()));
				}
				else
				{
					setForeground(UIPropertyContext.getNotQualifiedColor());
				}
			}
			return this;
		}

	}

	private class ClassBoxRenderer extends ComboBoxRenderer
	{

		private CharacterFacade character;

		public ClassBoxRenderer(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof ClassFacade && !character.isQualifiedFor((ClassFacade) value))
			{
				if (index == -1)
				{// this is a hack to prevent the combobox from overwriting the text color
					setText("");
					setIcon(new SimpleTextIcon(list, value.toString(), UIPropertyContext.getNotQualifiedColor()));
				}
				else
				{
					setForeground(UIPropertyContext.getNotQualifiedColor());
				}
			}
			return this;
		}

	}

	private class HPHandler extends AbstractAction implements ReferenceListener<Integer>
	{

		private CharacterFacade character;
		private ReferenceFacade<Integer> ref;

		public HPHandler(CharacterFacade character)
		{
			this.character = character;
			this.ref = character.getTotalHPRef();
			putValue(NAME, LanguageBundle.getString("in_edit")); //$NON-NLS-1$
		}

		public void install()
		{
			hpButton.setAction(this);
			totalHPLabel.setText(ref.getReference().toString());
			ref.addReferenceListener(this);
		}

		public void uninstall()
		{
			ref.removeReferenceListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			CharacterHPDialog.showHPDialog(SummaryInfoTab.this, character);
		}

		@Override
		public void referenceChanged(ReferenceEvent<Integer> e)
		{
			totalHPLabel.setText(ref.getReference().toString());
		}

	}

	private class RandomNameAction extends AbstractAction
	{

		private CharacterFacade character;
		private JFrame frame;

		public RandomNameAction(CharacterFacade character, JFrame frame)
		{
			this.character = character;
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String gender =
					character.getGenderRef().getReference() != null ? character.getGenderRef().getReference().toString() : ""; //$NON-NLS-1$
			RandomNameDialog dialog = new RandomNameDialog(frame, gender);
			dialog.setVisible(true);
			String chosenName = dialog.getChosenName();
			if (chosenName != null && chosenName.length() > 0 && !chosenName.equals(LanguageBundle.getString("in_rndNmDefault"))) //$NON-NLS-1$
			{
				character.setName(chosenName);
			}
			String chosenGender = dialog.getGender();
			character.setGender(chosenGender);
		}

	}

	/**
	 * Handler for actions from the generate rolls button. Also defines 
	 * the appearance of the button.
	 */
	private final class GenerateRollsAction extends AbstractAction implements ListListener<CharacterLevelFacade>, ReferenceListener<Integer>
	{

		private CharacterFacade character;

		public GenerateRollsAction(CharacterFacade character)
		{
			this.character = character;
			putValue(NAME, LanguageBundle.getString("in_sumGenerate_Rolls")); //$NON-NLS-1$
			update();
		}

		/**
		 * Attach the handler to the screen button. e.g. When the character is
		 * made active.
		 */
		public void install()
		{
			// Listen to the total levels
			character.getCharacterLevelsFacade().addListListener(this);

			// Listen to the roll method
			character.getRollMethodRef().addReferenceListener(this);
		}

		/**
		 * Detach the handler from the on screen button. e.g. when the
		 * character is no longer being displayed.
		 */
		public void uninstall()
		{
			character.getCharacterLevelsFacade().removeListListener(this);
			character.getRollMethodRef().removeReferenceListener(this);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.rollStats();
		}

		/**
		 * Update the state of the button.
		 */
		public void update()
		{
			setEnabled(character.isStatRollEnabled());
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ListListener#elementAdded(pcgen.core.facade.event.ListEvent)
		 */
		@Override
		public void elementAdded(ListEvent<CharacterLevelFacade> e)
		{
			update();
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ListListener#elementRemoved(pcgen.core.facade.event.ListEvent)
		 */
		@Override
		public void elementRemoved(ListEvent<CharacterLevelFacade> e)
		{
			update();
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ListListener#elementsChanged(pcgen.core.facade.event.ListEvent)
		 */
		@Override
		public void elementsChanged(ListEvent<CharacterLevelFacade> e)
		{
			update();
		}

		@Override
		public void referenceChanged(ReferenceEvent<Integer> e)
		{
			update();
		}

		@Override
		public void elementModified(ListEvent<CharacterLevelFacade> e)
		{
			update();
		}

	}

	/**
	 * Handler for actions from the generate rolls button. Also defines 
	 * the appearance of the button.
	 */
	private class RollMethodAction extends AbstractAction
	{

		private JFrame parent;
		private CharacterFacade character;

		public RollMethodAction(JFrame parent, CharacterFacade character)
		{
			putValue(NAME, LanguageBundle.getString("in_sumRoll_Method")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_sumRoll_Method_Tip")); //$NON-NLS-1$
			this.parent = parent;
			this.character = character;
		}

		/**
		 * Attach the handler to the screen button. e.g. When the character is
		 * made active.
		 */
		public void install()
		{
		}

		/**
		 * Detach the handler from the on screen button. e.g. when the
		 * character is no longer being displayed.
		 */
		public void uninstall()
		{
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			CharacterStatsPanel charStatsPanel = new CharacterStatsPanel(null);
			SinglePrefDialog prefsDialog = new SinglePrefDialog(parent, charStatsPanel);
			charStatsPanel.setParent(prefsDialog);
			prefsDialog.setLocationRelativeTo(parent);
			prefsDialog.setVisible(true);
			character.refreshRollMethod();
		}

	}

	private class CreateMonsterAction extends AbstractAction
	{

		private CharacterFacade character;
		private JFrame frame;

		public CreateMonsterAction(CharacterFacade character, JFrame frame)
		{
			putValue(NAME, LanguageBundle.getString("in_sumCreateMonster")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_sumCreateMonster_Tip")); //$NON-NLS-1$
			this.character = character;
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			KitSelectionDialog kitDialog =
					new KitSelectionDialog(frame, character);
			kitDialog.setLocationRelativeTo(frame);
			kitDialog.setVisible(true);			
		}

	}

	private class AddLevelsAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddLevelsAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, new SignIcon(Sign.Plus));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ClassFacade c = (ClassFacade) classComboBox.getSelectedItem();
			if (c != null)
			{
				Number levels = (Number) addLevelsField.getValue();
				ClassFacade[] classes = new ClassFacade[levels.intValue()];
				Arrays.fill(classes, c);
				character.addCharacterLevels(classes);
			}
		}

		public void install()
		{
			CharacterLevelsFacade characterLevelsFacade =
					character.getCharacterLevelsFacade();
			int maxLvl = characterLevelsFacade.getSize();
			if (maxLvl > 0)
			{
				ClassFacade classTaken =
						characterLevelsFacade
							.getClassTaken(characterLevelsFacade
								.getElementAt(maxLvl - 1));
				classComboBox.setSelectedItem(classTaken);
			}
		}
	}

	private class RemoveLevelsAction extends AbstractAction
	{

		private CharacterFacade character;

		public RemoveLevelsAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, new SignIcon(Sign.Minus));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Number levels = (Number) removeLevelsField.getValue();
			character.removeCharacterLevels(levels.intValue());
		}

	}

	/**
	 * Handler for actions from the add experience button. Also defines 
	 * the appearance of the button.
	 */
	private class ExpAddAction extends AbstractAction
	{

		private CharacterFacade character;

		public ExpAddAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, new SignIcon(Sign.Plus));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object value = expmodField.getValue();
			if (value == null)
			{
				return;
			}
			int modVal = ((Number) value).intValue();
			character.adjustXP(modVal);
		}

	}

	/**
	 * Handler for actions from the subtract experience button. Also defines 
	 * the appearance of the button.
	 */
	private class ExpSubtractAction extends AbstractAction
	{

		private CharacterFacade character;

		public ExpSubtractAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, new SignIcon(Sign.Minus));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object value = expmodField.getValue();
			if (value == null)
			{
				return;
			}
			int modVal = ((Number) value).intValue();
			character.adjustXP(modVal * -1);
		}

	}

	/**
	 * The Class <code>LabelHandler</code> manages the text displayed in a
	 * JLabel field for a character. The text will be updated each time a 
	 * reference is updated. The handler also knows how to react to install and 
	 * uninstall actions when the displayed character changes. 
	 */
	private class LabelHandler
			implements ReferenceListener<String>
	{

		private ReferenceFacade<String> reference = null;
		private JLabel label;

		/**
		 * Create a new label handler.
		 * @param label The label to be managed
		 */
		public LabelHandler(JLabel label)
		{
			this.label = label;
		}

		/**
		 * @param ref The new reference to be watched
		 */
		public void setReference(ReferenceFacade<String> ref)
		{
			if (reference != null)
			{
				reference.removeReferenceListener(this);
			}
			reference = ref;
			if (reference != null)
			{
				reference.addReferenceListener(this);
				label.setText(reference.getReference());
			}
		}

		/**
		 * Attach the handler to the on-screen field. e.g. When the character
		 * is made active. 
		 */
		public void install()
		{
			reference.addReferenceListener(this);
			label.setText(reference.getReference());
		}

		/**
		 * Detach the handler from the on-screen field. e.g. when the 
		 * character is no longer being displayed. 
		 */
		public void uninstall()
		{
			reference.removeReferenceListener(this);
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ReferenceListener#referenceChanged(pcgen.core.facade.event.ReferenceEvent)
		 */
		@Override
		public void referenceChanged(ReferenceEvent<String> e)
		{
			label.setText(e.getNewReference());
		}

	}

	/**
	 * The Class <code>TodoListHandler</code> manages the text displayed in the
	 * things to be done panel for the character. The text will be updated each 
	 * time the character's todo list changes. The handler also knows how to react
	 * to install and uninstall actions when the displayed character changes.
	 */
	private class TodoListHandler
			implements ListListener<TodoFacade>, HyperlinkListener
	{

		private CharacterFacade character;
		private String lastDest = ""; //$NON-NLS-1$

		/**
		 * Create a new instance for the character.
		 * @param character The character being managed.
		 */
		public TodoListHandler(CharacterFacade character)
		{
			this.character = character;
		}

		/**
		 * Attach the handler to the on-screen field. e.g. When the character
		 * is made active. 
		 */
		public void install()
		{
			character.getTodoList().addListListener(this);
			todoPane.addHyperlinkListener(this);
			lastDest = ""; //$NON-NLS-1$
			refreshTodoList();
		}

		/**
		 * Detach the handler from the on-screen field. e.g. when the 
		 * character is no longer being displayed. 
		 */
		public void uninstall()
		{
			todoPane.removeHyperlinkListener(this);
			character.getTodoList().removeListListener(this);
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ListListener#elementAdded(pcgen.core.facade.event.ListEvent)
		 */
		@Override
		public void elementAdded(ListEvent<TodoFacade> e)
		{
			refreshTodoList();
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ListListener#elementRemoved(pcgen.core.facade.event.ListEvent)
		 */
		@Override
		public void elementRemoved(ListEvent<TodoFacade> e)
		{
			refreshTodoList();
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.event.ListListener#elementsChanged(pcgen.core.facade.event.ListEvent)
		 */
		@Override
		public void elementsChanged(ListEvent<TodoFacade> e)
		{
			refreshTodoList();
		}

		@Override
		public void elementModified(ListEvent<TodoFacade> e)
		{
			refreshTodoList();
		}
		/**
		 * Recreate the "Things to be Done" list based on the character's todo list.
		 */
		private void refreshTodoList()
		{
			StringBuilder todoText = new StringBuilder("<html><body>"); //$NON-NLS-1$

			int i = 1;
			SortedSet<TodoFacade> sortedTodos = new TreeSet<TodoFacade>();
			for (TodoFacade item : character.getTodoList())
			{
				sortedTodos.add(item);
			}

			for (TodoFacade item : sortedTodos)
			{
				todoText.append(i++).append(". "); //$NON-NLS-1$
				String fieldLoc =  item.getTab().name() + "/" + item.getFieldName(); //$NON-NLS-1$
				if (StringUtils.isNotEmpty(item.getSubTabName()))
				{
					fieldLoc +=  "/" + item.getSubTabName(); //$NON-NLS-1$
				}
				todoText.append("<a href=\"" + fieldLoc + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
				if (item.getMessageKey().startsWith("in_")) //$NON-NLS-1$
				{
					todoText.append(LanguageBundle.getFormattedString(item.getMessageKey(), item.getFieldName()));
				}
				else
				{
					todoText.append(item.getMessageKey());
				}
				todoText.append("</a><br>"); //$NON-NLS-1$
			}
			todoText.append("</body></html>"); //$NON-NLS-1$
			todoPane.setText(todoText.toString());
		}

		@Override
		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			{
				// We get two messages on a click, so ignore duplicates
				if (lastDest.equals(e.getDescription()))
				{
					lastDest = ""; //$NON-NLS-1$
					return;
				}
				lastDest = e.getDescription();
				firePropertyChange(TodoFacade.SWITCH_TABS, "", e.getDescription()); //$NON-NLS-1$
			}
		}

	}

	private class SummaryTabFocusTraversalPolicy extends LayoutFocusTraversalPolicy
	{

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent)
		{
			if (aComponent == deityComboBox)
			{
				int column = statsTable.getColumn("EDITABLE").getModelIndex(); //$NON-NLS-1$
				statsTable.editCellAt(0, column);
				JSpinner spinner = (JSpinner) statsTable.getEditorComponent();
				return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
			}
			return super.getComponentAfter(aContainer, aComponent);
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent)
		{
//			if (aComponent == generateRollsButton)
//			{
//				int column = statsTable.getColumn("EDITABLE").getModelIndex();
//				statsTable.editCellAt(statsTable.getRowCount()-1, column);
//				JSpinner spinner = (JSpinner) statsTable.getEditorComponent();
//				return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
//			}
			return super.getComponentBefore(aContainer, aComponent);
		}

	}

}
