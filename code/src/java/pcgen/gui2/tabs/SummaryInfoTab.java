/*
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
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.TodoFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
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
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.ManagedField;
import pcgen.gui2.util.SignIcon;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.SimpleTextIcon;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.SimpleHtmlPanelController;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

import org.apache.commons.lang3.StringUtils;

/**
 * This component displays a basic summary of a character such as name,
 * alignment, race, class, and stat information.
 */
@SuppressWarnings("serial")
public class SummaryInfoTab extends JPanel implements CharacterInfoTab, TodoHandler
{

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
    private final InfoBoxRenderer infoBoxRenderer;
    private final ClassBoxRenderer classBoxRenderer;
    private final JButton generateRollsButton;
    private final JButton rollMethodButton;
    private final JButton createMonsterButton;
    private final JButton expaddButton;
    private final JButton expsubtractButton;
    private final JButton addLevelsButton;
    private final JButton removeLevelsButton;
    private final JButton hpButton;
    private final JLabel totalHPLabel;
    private final JFXPanelFromResource<SimpleHtmlPanelController> infoPane;
    private final JLabel statTotalLabel;
    private final JLabel statTotal;
    private final JLabel modTotalLabel;
    private final JLabel modTotal;
    private final JEditorPane todoPane;
    private final JButton random;
    private JScrollPane langScroll;

    SummaryInfoTab()
    {
        this.tabTitle = new TabTitle(Tab.SUMMARY);
        this.basicsPanel = new JPanel();
        this.todoPanel = new JPanel();
        this.scoresPanel = new JPanel();
        this.racePanel = new JPanel();
        this.classPanel = new JPanel();
        this.characterNameField = new JTextField();
        this.characterTypeComboBox = new JComboBox<>();
        this.random = new JButton();
        FontManipulation.xsmall(random);
        this.playerNameField = new JTextField();
        this.expField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        this.nextlevelField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        this.xpTableComboBox = new JComboBox<>();
        this.expmodField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        this.addLevelsField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        this.removeLevelsField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        this.statsTable = new JTable();
        this.classLevelTable = new JTable();
        this.languageTable = new JTable();
        this.genderComboBox = new JComboBox<>();
        this.handsComboBox = new JComboBox<>();
        this.alignmentComboBox = new JComboBox<>();
        this.deityComboBox = new JComboBox<>();
        this.raceComboBox = new JComboBox<>();
        this.ageComboBox = new JComboBox<>();
        this.classComboBox = new JComboBox<>();
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
        this.infoPane = new JFXPanelFromResource<>(
                SimpleHtmlPanelController.class,
                "SimpleHtmlPanel.fxml"
        );
        this.statTotalLabel = new JLabel();
        this.statTotal = new JLabel();
        this.modTotalLabel = new JLabel();
        this.modTotal = new JLabel();
        this.todoPane = new JEditorPane();
        this.infoBoxRenderer = new InfoBoxRenderer();
        this.classBoxRenderer = new ClassBoxRenderer();
        initComponents();
    }

    private void initComponents()
    {
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicyProvider(true);
        this.setFocusTraversalPolicy(new SummaryTabFocusTraversalPolicy());

        LanguageTableModel.initializeTable(languageTable);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        setPanelTitle(basicsPanel, LanguageBundle.getString("in_sumCharacterBasics")); //$NON-NLS-1$
        basicsPanel.setLayout(new GridBagLayout());
        deityComboBox.setRenderer(infoBoxRenderer);
        raceComboBox.setRenderer(infoBoxRenderer);
        classComboBox.setRenderer(classBoxRenderer);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.weighty = 0.7;
        add(basicsPanel, gbc);

        setPanelTitle(todoPanel, LanguageBundle.getString("in_tipsString")); //$NON-NLS-1$
        initTodoPanel(todoPanel);
        gbc.gridy = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(todoPanel, gbc);

        initMiddlePanel(scoresPanel);
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1;
        add(scoresPanel, gbc);

        JPanel rightPanel = new JPanel();
        setPanelTitle(racePanel, LanguageBundle.getString("in_raceString")); //$NON-NLS-1$
        setPanelTitle(classPanel, LanguageBundle.getString("in_sumClassLevel")); //$NON-NLS-1$
        initRightPanel(rightPanel);
        gbc.weightx = 0.1;
        gbc.weighty = 1;
        add(rightPanel, gbc);
    }

    private static void setPanelTitle(JComponent panel, String title)
    {
        panel.setBorder(
                BorderFactory.createTitledBorder(null, title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
    }

    /**
     * Initialise the "Things to be Done" panel. Creates the required components
     * and places them in the panel.
     *
     * @param panel The panel to be initialised
     */
    private void initTodoPanel(JPanel panel)
    {
        panel.setLayout(new BorderLayout());
        todoPane.setOpaque(false);
        todoPane.setContentType("text/html"); //$NON-NLS-1$
        todoPane.setEditable(false);

        JScrollPane scroll = new JScrollPane(todoPane);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);
    }

    private void initMiddlePanel(JPanel middlePanel)
    {
        middlePanel.setLayout(new GridLayout(2, 1));

        JPanel statsPanel = new JPanel();
        setPanelTitle(statsPanel, LanguageBundle.getString("in_sumAbilityScores")); //$NON-NLS-1$
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        StatTableModel.initializeTable(statsTable);
        JScrollPane pane = new JScrollPane(statsTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
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
        JPanel statsBox = new JPanel();
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
        FontManipulation.title(statTotalLabel);
        statTotalPanel.add(statTotalLabel);
        statTotalPanel.add(statTotal);
        FontManipulation.title(modTotalLabel);
        statTotalPanel.add(modTotalLabel);
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

        pane = new JScrollPane(infoPane);
        setPanelTitle(pane, LanguageBundle.getString("in_sumStats")); //$NON-NLS-1$
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
        JLabel darrowLabel = new JLabel(Icons.button_arrow_down.getImageIcon());
        JLabel uarrowLabel = new JLabel(Icons.button_arrow_up.getImageIcon());

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
        return new JLabel(LanguageBundle.getString(text));
    }

    private void resetBasicsPanel()
    {
        basicsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        {
            JLabel label = createLabel("in_sumName"); //$NON-NLS-1$
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 0, 3, 0);
            basicsPanel.add(label, gbc);

            random.setText(LanguageBundle.getString("in_randomButton")); //$NON-NLS-1$
            random.setMargin(new Insets(0, 0, 0, 0));
            gbc.insets = new Insets(0, 2, 3, 2);
            basicsPanel.add(random, gbc);

            gbc.insets = new Insets(0, 0, 3, 2);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            basicsPanel.add(characterNameField, gbc);
        }
        Insets insets = new Insets(0, 0, 3, 2);
        Font labelFont = null;
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
        gbc.anchor = GridBagConstraints.WEST;
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

    @Override
    public void adviseTodo(String fieldName)
    {
        if ("Name".equals(fieldName)) //$NON-NLS-1$
        {
            characterNameField.requestFocusInWindow();
            characterNameField.selectAll();
        } else if ("Race".equals(fieldName)) //$NON-NLS-1$
        {
            raceComboBox.requestFocusInWindow();
            highlightBorder(raceComboBox);
        } else if ("Class".equals(fieldName)) //$NON-NLS-1$
        {
            classComboBox.requestFocusInWindow();
            highlightBorder(classComboBox);
        } else if ("Languages".equals(fieldName)) //$NON-NLS-1$
        {
            highlightBorder(langScroll);
        } else if ("Ability Scores".equals(fieldName)) //$NON-NLS-1$
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

        SwingUtilities.invokeLater(() -> {
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException e)
            {
                // Ignored as we'll exit shortly anyway.
            }
            comp.setBorder(oldBorder);
        });
    }

    @Override
    public ModelMap createModels(final CharacterFacade character)
    {
        ModelMap models = new ModelMap();

        models.put(LabelAndFieldHandler.class, new LabelAndFieldHandler(character));
        models.put(ComboBoxRendererHandler.class, new ComboBoxRendererHandler(character));
        models.put(ComboBoxModelHandler.class, new ComboBoxModelHandler(character));

        models.put(RandomNameAction.class,
                new RandomNameAction(character, (JFrame) SwingUtilities.getWindowAncestor(this)));
        models.put(ClassLevelTableModel.class, new ClassLevelTableModel(character, classLevelTable, classComboBox));

        models.put(GenerateRollsAction.class, new GenerateRollsAction(character));
        models.put(RollMethodAction.class,
                new RollMethodAction(character, (JFrame) SwingUtilities.getWindowAncestor(this))
        );
        models.put(CreateMonsterAction.class,
                new CreateMonsterAction(character, (JFrame) SwingUtilities.getWindowAncestor(this)));
        models.put(AddLevelsAction.class, new AddLevelsAction(character));
        models.put(RemoveLevelsAction.class, new RemoveLevelsAction(character));
        models.put(StatTableModel.class, new StatTableModel(character, statsTable));
        models.put(LanguageTableModel.class, new LanguageTableModel(character, languageTable));
        models.put(InfoPaneHandler.class, new InfoPaneHandler(character, infoPane));
        models.put(ExpAddAction.class, new ExpAddAction(character));
        models.put(ExpSubtractAction.class, new ExpSubtractAction(character));
        models.put(TodoListHandler.class, new TodoListHandler(character));
        models.put(HPHandler.class, new HPHandler(character));
        return models;
    }

    @Override
    public TabTitle getTabTitle()
    {
        return tabTitle;
    }

    @Override
    public void storeModels(ModelMap models)
    {
        models.get(LabelAndFieldHandler.class).uninstall();
        models.get(ComboBoxModelHandler.class).uninstall();

        models.get(LanguageTableModel.class).uninstall();
        models.get(ClassLevelTableModel.class).uninstall();
        models.get(InfoPaneHandler.class).uninstall();
        models.get(StatTableModel.class).uninstall();
        models.get(TodoListHandler.class).uninstall();
        models.get(GenerateRollsAction.class).uninstall();
        models.get(HPHandler.class).uninstall();

        models.get(ComboBoxRendererHandler.class).uninstall();
    }

    @Override
    public void restoreModels(ModelMap models)
    {
        models.get(LabelAndFieldHandler.class).install();
        models.get(ComboBoxRendererHandler.class).install();
        models.get(ComboBoxModelHandler.class).install();

        models.get(InfoPaneHandler.class).install();
        models.get(LanguageTableModel.class).install();
        models.get(StatTableModel.class).install();
        models.get(ClassLevelTableModel.class).install();
        models.get(TodoListHandler.class).install();
        models.get(GenerateRollsAction.class).install();
        models.get(HPHandler.class).install();

        random.setAction(models.get(RandomNameAction.class));
        generateRollsButton.setAction(models.get(GenerateRollsAction.class));
        rollMethodButton.setAction(models.get(RollMethodAction.class));
        createMonsterButton.setAction(models.get(CreateMonsterAction.class));
        AddLevelsAction addLevelsAction = models.get(AddLevelsAction.class);
        addLevelsButton.setAction(addLevelsAction);
        addLevelsField.setAction(addLevelsAction);
        RemoveLevelsAction removeLevelsAction = models.get(RemoveLevelsAction.class);
        removeLevelsButton.setAction(removeLevelsAction);
        removeLevelsField.setAction(removeLevelsAction);
        ExpAddAction expAddAction = models.get(ExpAddAction.class);
        expaddButton.setAction(expAddAction);
        expmodField.setAction(expAddAction);
        expsubtractButton.setAction(models.get(ExpSubtractAction.class));
        addLevelsAction.install();

        resetBasicsPanel();
    }

    private class LabelAndFieldHandler
    {

        private final LabelHandler statTotalLabelHandler;
        private final LabelHandler statTotalHandler;
        private final LabelHandler modTotalLabelHandler;
        private final LabelHandler modTotalHandler;

        /**
         * Field for Character Name
         */
        private final ManagedField charNameHandler;

        /**
         * Field for PlayerName
         */
        private final ManagedField playerNameHandler;

        /**
         * Field for Tab Name
         */
        private final ManagedField tabNameHandler;
        private final ManagedField ageHandler;
        private final ManagedField expHandler;
        private final ManagedField nextLevelHandler;

        LabelAndFieldHandler(final CharacterFacade character)
        {

            statTotalLabelHandler = new LabelHandler(statTotalLabel, character.getStatTotalLabelTextRef());
            statTotalHandler = new LabelHandler(statTotal, character.getStatTotalTextRef());
            modTotalLabelHandler = new LabelHandler(modTotalLabel, character.getModTotalLabelTextRef());
            modTotalHandler = new LabelHandler(modTotal, character.getModTotalTextRef());

            //Manages the character name text field
            charNameHandler = new TextFieldHandler(characterNameField, character.getNameRef())
            {

                @Override
                protected void textChanged(String text)
                {
                    character.setName(text);
                }

            };

            //Manages the player name text field.
            playerNameHandler = new TextFieldHandler(playerNameField, character.getPlayersNameRef())
            {

                @Override
                protected void textChanged(String text)
                {
                    character.setPlayersName(text);
                }

            };
            //Manages the tab name text field.
            tabNameHandler = new TextFieldHandler(tabLabelField, character.getTabNameRef())
            {

                @Override
                protected void textChanged(String text)
                {
                    character.setTabName(text);
                }

            };

            /*
             * Handler for the Age field. This listens for and processes both
             * changes to the value from the character and modifications to the
             * field made by the user.
             */
            ageHandler = new FormattedFieldHandler(ageField, character.getAgeRef())
            {
                @Override
                protected void valueChanged(int value)
                {
                    character.setAge(value);
                }

            };

            /*
             * Handler for the Current Experience field. This listens for and
             * processes both changes to the value from the character and
             * modifications to the field made by the user.
             */
            expHandler = new FormattedFieldHandler(expField, character.getXPRef())
            {
                @Override
                protected void valueChanged(int value)
                {
                    character.setXP(value);
                }

            };

            /*
             * Handler for the Next Level field. This is a read-only field so
             * the handler only listens for changes to the value from the
             * character.
             */
            nextLevelHandler = new FormattedFieldHandler(nextlevelField, character.getXPForNextLevelRef())
            {

                @Override
                protected void valueChanged(int value)
                {
                    //This will never be called
                }

            };
        }

        public void install()
        {
            charNameHandler.install();
            playerNameHandler.install();
            tabNameHandler.install();
            ageHandler.install();
            expHandler.install();
            nextLevelHandler.install();
            statTotalLabelHandler.install();
            statTotalHandler.install();
            modTotalLabelHandler.install();
            modTotalHandler.install();
        }

        public void uninstall()
        {
            charNameHandler.uninstall();
            playerNameHandler.uninstall();
            tabNameHandler.uninstall();
            ageHandler.uninstall();
            expHandler.uninstall();
            nextLevelHandler.uninstall();
            statTotalLabelHandler.uninstall();
            statTotalHandler.uninstall();
            modTotalLabelHandler.uninstall();
            modTotalHandler.uninstall();
        }
    }

    private class ComboBoxModelHandler
    {
        private final CharacterFacade character;
        private final CharacterComboBoxModel<Gender> genderModel;
        private final CharacterComboBoxModel<Handed> handsModel;
        private CharacterComboBoxModel<PCAlignment> alignmentModel;
        private CharacterComboBoxModel<Deity> deityModel;
        private final DeferredCharacterComboBoxModel<Race> raceModel;
        private final CharacterComboBoxModel<String> ageCatModel;
        private final FacadeComboBoxModel<PCClass> classModel;
        private final CharacterComboBoxModel<String> xpTableModel;
        private final CharacterComboBoxModel<String> characterTypeModel;

        ComboBoxModelHandler(final CharacterFacade character)
        {
            this.character = character;
            DataSetFacade dataset = character.getDataSet();

            //initialize character type model
            characterTypeModel =
                    new CharacterComboBoxModel<>(dataset.getCharacterTypes(), character.getCharacterTypeRef())
                    {

                        @Override
                        public void setSelectedItem(Object anItem)
                        {
                            character.setCharacterType((String) anItem);
                        }

                    };

            //initialize gender model
            genderModel =
                    new CharacterComboBoxModel<>(character.getAvailableGenders(), character.getGenderRef())
                    {

                        @Override
                        public void setSelectedItem(Object anItem)
                        {
                            character.setGender((Gender) anItem);
                        }

                    };

            //initialize handed model
            handsModel =
                    new CharacterComboBoxModel<>(character.getAvailableHands(), character.getHandedRef())
                    {

                        @Override
                        public void setSelectedItem(Object anItem)
                        {
                            character.setHanded((Handed) anItem);
                        }

                    };

            if (!dataset.getAlignments().isEmpty())
            {
                //initialize alignment model
                alignmentModel =
                        new CharacterComboBoxModel<>(dataset.getAlignments(), character.getAlignmentRef())
                        {

                            @Override
                            public void setSelectedItem(Object anItem)
                            {
                                character.setAlignment((PCAlignment) anItem);
                            }

                        };
            }

            if (character.isFeatureEnabled(CControl.DOMAINFEATURE))
            {
                //initialize deity model
                deityModel = new CharacterComboBoxModel<>(dataset.getDeities(), character.getDeityRef())
                {

                    @Override
                    public void setSelectedItem(Object anItem)
                    {
                        character.setDeity((Deity) anItem);
                    }

                };
            }

            //initialize race model
            raceModel = new DeferredCharacterComboBoxModel<>(dataset.getRaces(), character.getRaceRef())
            {

                @Override
                public void commitSelectedItem(Object anItem)
                {
                    character.setRace((Race) anItem);
                }

            };

            //initialize age category model
            ageCatModel = new CharacterComboBoxModel<>(
                    character.getAgeCategories(),
                    character.getAgeCategoryRef()
            )
            {

                @Override
                public void setSelectedItem(Object anItem)
                {
                    character.setAgeCategory((String) anItem);
                }

            };

            //initialize XP table model
            xpTableModel = new CharacterComboBoxModel<>(dataset.getXPTableNames(), character.getXPTableNameRef())
            {

                @Override
                public void setSelectedItem(Object anItem)
                {
                    character.setXPTable((String) anItem);
                }

            };

            classModel = new FacadeComboBoxModel<>(dataset.getClasses(), null);
        }

        public void install()
        {
            characterTypeComboBox.setModel(characterTypeModel);
            genderComboBox.setModel(genderModel);
            handsComboBox.setModel(handsModel);
            if (character.getDataSet().getAlignments().isEmpty())
            {
                alignmentComboBox.setVisible(false);
            } else
            {
                alignmentComboBox.setModel(alignmentModel);
                alignmentComboBox.setVisible(true);
            }
            boolean hasDeityDomain = character.isFeatureEnabled(CControl.DOMAINFEATURE);
            deityComboBox.setVisible(hasDeityDomain);
            if (hasDeityDomain)
            {
                deityComboBox.setModel(deityModel);
            }
            raceComboBox.setModel(raceModel);
            raceComboBox.addFocusListener(raceModel);
            ageComboBox.setModel(ageCatModel);
            classComboBox.setModel(classModel);
            xpTableComboBox.setModel(xpTableModel);
        }

        public void uninstall()
        {
            raceComboBox.removeFocusListener(raceModel);
        }
    }

    private class ComboBoxRendererHandler
    {

        private final CharacterFacade character;

        ComboBoxRendererHandler(CharacterFacade character)
        {
            this.character = character;
        }

        public void install()
        {
            infoBoxRenderer.setCharacter(character);
            classBoxRenderer.setCharacter(character);
        }

        public void uninstall()
        {
            infoBoxRenderer.setCharacter(null);
            classBoxRenderer.setCharacter(null);
        }
    }

    private static class CharacterComboBoxRenderer extends DefaultListCellRenderer
    {

        protected CharacterFacade character;

        public void setCharacter(CharacterFacade character)
        {
            this.character = character;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setToolTipText(value == null ? null : value.toString());
            return this;
        }

    }

    private static class InfoBoxRenderer extends CharacterComboBoxRenderer
    {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ((character != null) && (value instanceof InfoFacade) && !character.isQualifiedFor((InfoFacade) value))
            {
                if (index == -1)
                {// this is a hack to prevent the combobox from overwriting the text color
                    setText("");
                    setIcon(new SimpleTextIcon(list, value.toString(),
                            ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor())));
                } else
                {
                    setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
                }
            }
            return this;
        }

    }

    private static class ClassBoxRenderer extends CharacterComboBoxRenderer
    {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PCClass && !character.isQualifiedFor((PCClass) value))
            {
                if (index == -1)
                {// this is a hack to prevent the combobox from overwriting the text color
                    setText("");
                    setIcon(new SimpleTextIcon(list, value.toString(),
                            ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor())));
                } else
                {
                    setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
                }
            }
            return this;
        }

    }

    private class HPHandler extends AbstractAction implements ReferenceListener<Integer>
    {

        private final CharacterFacade character;
        private final ReferenceFacade<Integer> ref;

        HPHandler(CharacterFacade character)
        {
            this.character = character;
            this.ref = character.getTotalHPRef();
            putValue(NAME, LanguageBundle.getString("in_edit")); //$NON-NLS-1$
        }

        public void install()
        {
            hpButton.setAction(this);
            totalHPLabel.setText(ref.get().toString());
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
            totalHPLabel.setText(ref.get().toString());
        }

    }

    private static class RandomNameAction extends AbstractAction
    {

        private final CharacterFacade character;
        private final JFrame frame;

        RandomNameAction(CharacterFacade character, JFrame frame)
        {
            this.character = character;
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            String gender =
                    character.getGenderRef().get()
                            != null ? character.getGenderRef().get().toString() : ""; //$NON-NLS-1$
            RandomNameDialog dialog = new RandomNameDialog(frame, gender);
            dialog.setVisible(true);
            String chosenName = dialog.getChosenName();
            if (chosenName != null && !chosenName.isEmpty()
                    && !chosenName.equals(LanguageBundle.getString("in_rndNmDefault"))) //$NON-NLS-1$
            {
                character.setName(chosenName);
            }
            String chosenGender = dialog.getGender();
            character.setGender(chosenGender);
        }

    }

    /**
     * Handler for actions from the generate rolls button. Also defines the
     * appearance of the button.
     */
    private static final class GenerateRollsAction extends AbstractAction
            implements ListListener<CharacterLevelFacade>, ReferenceListener<Integer>
    {

        private final CharacterFacade character;

        GenerateRollsAction(CharacterFacade character)
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
         * Detach the handler from the on screen button. e.g. when the character
         * is no longer being displayed.
         */
        public void uninstall()
        {
            character.getCharacterLevelsFacade().removeListListener(this);
            character.getRollMethodRef().removeReferenceListener(this);
        }

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

        @Override
        public void elementAdded(ListEvent<CharacterLevelFacade> e)
        {
            update();
        }

        @Override
        public void elementRemoved(ListEvent<CharacterLevelFacade> e)
        {
            update();
        }

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
     * Handler for actions from the generate rolls button. Also defines the
     * appearance of the button.
     */
    private static final class RollMethodAction extends AbstractAction
    {

        private final JFrame parent;
        private final CharacterFacade character;

        private RollMethodAction(CharacterFacade character, JFrame parent)
        {
            putValue(NAME, LanguageBundle.getString("in_sumRoll_Method")); //$NON-NLS-1$
            putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_sumRoll_Method_Tip")); //$NON-NLS-1$
            this.parent = parent;
            this.character = character;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            CharacterStatsPanel charStatsPanel = new CharacterStatsPanel();
            SinglePrefDialog prefsDialog = new SinglePrefDialog(parent, charStatsPanel);
            prefsDialog.setLocationRelativeTo(parent);
            prefsDialog.setVisible(true);
            character.refreshRollMethod();
        }

    }

    private static class CreateMonsterAction extends AbstractAction
    {

        private final CharacterFacade character;
        private final JFrame frame;

        CreateMonsterAction(CharacterFacade character, JFrame frame)
        {
            putValue(NAME, LanguageBundle.getString("in_sumCreateMonster")); //$NON-NLS-1$
            putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_sumCreateMonster_Tip")); //$NON-NLS-1$
            this.character = character;
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            KitSelectionDialog kitDialog = new KitSelectionDialog(frame, character);
            kitDialog.setLocationRelativeTo(frame);
            kitDialog.setVisible(true);
        }

    }

    private class AddLevelsAction extends AbstractAction
    {

        private final CharacterFacade character;

        AddLevelsAction(CharacterFacade character)
        {
            this.character = character;
            putValue(SMALL_ICON, new SignIcon(Sign.Plus));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            PCClass c = (PCClass) classComboBox.getSelectedItem();
            if (c != null)
            {
                Number levels = (Number) addLevelsField.getValue();
                if (levels.intValue() >= 0)
                {
                    PCClass[] classes = new PCClass[levels.intValue()];
                    Arrays.fill(classes, c);
                    character.addCharacterLevels(classes);
                }
            }
        }

        public void install()
        {
            CharacterLevelsFacade characterLevelsFacade = character.getCharacterLevelsFacade();
            int maxLvl = characterLevelsFacade.getSize();
            if (maxLvl > 0)
            {
                PCClass classTaken =
                        characterLevelsFacade.getClassTaken(characterLevelsFacade.getElementAt(maxLvl - 1));
                classComboBox.setSelectedItem(classTaken);
            }
        }
    }

    private class RemoveLevelsAction extends AbstractAction
    {

        private final CharacterFacade character;

        RemoveLevelsAction(CharacterFacade character)
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
     * Handler for actions from the add experience button. Also defines the
     * appearance of the button.
     */
    private class ExpAddAction extends AbstractAction
    {

        private final CharacterFacade character;

        ExpAddAction(CharacterFacade character)
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
     * Handler for actions from the subtract experience button. Also defines the
     * appearance of the button.
     */
    private class ExpSubtractAction extends AbstractAction
    {

        private final CharacterFacade character;

        ExpSubtractAction(CharacterFacade character)
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
     * The Class {@code LabelHandler} manages the text displayed in a
     * JLabel field for a character. The text will be updated each time a
     * reference is updated. The handler also knows how to react to install and
     * uninstall actions when the displayed character changes.
     */
    private static class LabelHandler implements ReferenceListener<String>
    {

        private ReferenceFacade<String> reference = null;
        private final JLabel label;

        /**
         * Create a new label handler.
         *
         * @param label The label to be managed
         * @param ref   the ReferenceFacade that this handler should listen to.
         */
        LabelHandler(JLabel label, ReferenceFacade<String> ref)
        {
            this.label = label;
            setReference(ref);
        }

        /**
         * @param ref The new reference to be watched
         */
        private void setReference(ReferenceFacade<String> ref)
        {
            if (reference != null)
            {
                reference.removeReferenceListener(this);
            }
            reference = ref;
            if (reference != null)
            {
                reference.addReferenceListener(this);
                label.setText(reference.get());
            }
        }

        /**
         * Attach the handler to the on-screen field. e.g. When the character is
         * made active.
         */
        public void install()
        {
            reference.addReferenceListener(this);
            label.setText(reference.get());
        }

        /**
         * Detach the handler from the on-screen field. e.g. when the character
         * is no longer being displayed.
         */
        public void uninstall()
        {
            reference.removeReferenceListener(this);
        }

        @Override
        public void referenceChanged(ReferenceEvent<String> e)
        {
            label.setText(e.getNewReference());
        }

    }

    /**
     * The Class {@code TodoListHandler} manages the text displayed in the
     * things to be done panel for the character. The text will be updated each
     * time the character's todo list changes. The handler also knows how to
     * react to install and uninstall actions when the displayed character
     * changes.
     */
    @SuppressWarnings("TodoComment")
    private class TodoListHandler implements ListListener<TodoFacade>, HyperlinkListener
    {

        private final CharacterFacade character;
        private String lastDest = ""; //$NON-NLS-1$

        /**
         * Create a new instance for the character.
         *
         * @param character The character being managed.
         */
        TodoListHandler(CharacterFacade character)
        {
            this.character = character;
        }

        /**
         * Attach the handler to the on-screen field. e.g. When the character is
         * made active.
         */
        public void install()
        {
            character.getTodoList().addListListener(this);
            todoPane.addHyperlinkListener(this);
            lastDest = ""; //$NON-NLS-1$
            refreshTodoList();
        }

        /**
         * Detach the handler from the on-screen field. e.g. when the character
         * is no longer being displayed.
         */
        public void uninstall()
        {
            todoPane.removeHyperlinkListener(this);
            character.getTodoList().removeListListener(this);
        }

        @Override
        public void elementAdded(ListEvent<TodoFacade> e)
        {
            refreshTodoList();
        }

        @Override
        public void elementRemoved(ListEvent<TodoFacade> e)
        {
            refreshTodoList();
        }

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
         * Recreate the "Things to be Done" list based on the character's todo
         * list.
         */
        private void refreshTodoList()
        {
            StringBuilder todoText = new StringBuilder("<html><body>"); //$NON-NLS-1$

            int i = 1;
            Collection<TodoFacade> sortedTodos = new TreeSet<>();
            character.getTodoList().iterator().forEachRemaining(sortedTodos::add);

            for (TodoFacade item : sortedTodos)
            {
                todoText.append(i++).append(". "); //$NON-NLS-1$
                String fieldLoc = item.getTab().name() + "/" + item.getFieldName(); //$NON-NLS-1$
                if (StringUtils.isNotEmpty(item.getSubTabName()))
                {
                    fieldLoc += "/" + item.getSubTabName(); //$NON-NLS-1$
                }
                todoText.append("<a href=\"").append(fieldLoc).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
                if (item.getMessageKey().startsWith("in_")) //$NON-NLS-1$
                {
                    todoText.append(LanguageBundle.getFormattedString(item.getMessageKey(), item.getFieldName()));
                } else
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
                int column = statsTable.getColumn(StatTableModel.EDITABLE_COLUMN_ID).getModelIndex();
                statsTable.editCellAt(0, column);
                JSpinner spinner = (JSpinner) statsTable.getEditorComponent();
                return ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            }
            return super.getComponentAfter(aContainer, aComponent);
        }
    }

}
