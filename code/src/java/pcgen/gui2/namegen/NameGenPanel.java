/*
 * Copyright 2003 (C) Devon Jones
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
 */
package pcgen.gui2.namegen;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import pcgen.core.namegen.DataElement;
import pcgen.core.namegen.DataElementComperator;
import pcgen.core.namegen.GeneratedName;
import pcgen.core.namegen.NameGenerator;
import pcgen.core.namegen.Rule;
import pcgen.core.namegen.RuleSet;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.FontManipulation;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * Main panel of the random name generator. Swing rendering only; all data
 * loading and rule evaluation is delegated to {@link NameGenerator}.
 */
@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.UseArrayListInsteadOfVector"})
public class NameGenPanel extends JPanel
{
	private JButton generateButton;
	private JButton jButton1;
	private JCheckBox chkStructure;
	private JComboBox<RuleSet> cbCatalog;
	private JComboBox<String> cbCategory;
	private JComboBox<String> cbGender;
	private JComboBox<DataElement> cbStructure;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;

	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel6;
	private JLabel meaning;
	private JLabel pronounciation;
	private JPanel buttonPanel;
	private JPanel genCtrlPanel;
	private JPanel jPanel10;
	private JPanel jPanel11;
	private JPanel jPanel12;
	private JPanel jPanel13;
	private JPanel jPanel14;
	private JPanel nameDisplayPanel;
	private JPanel namePanel;
	private JPanel jPanel4;
	private JPanel nameSubInfoPanel;
	private JPanel nameActionPanel;
	private JPanel jPanel7;
	private JPanel jPanel8;
	private JPanel jPanel9;
	private JSeparator jSeparator1;
	private JSeparator jSeparator2;
	private JSeparator jSeparator3;
	private JSeparator jSeparator4;
	private JTextField name;

	private NameGenerator nameGen;
	private Rule lastRule;

	/** Creates new form NameGenPanel */
	public NameGenPanel()
	{
		this(new File("."));
	}

	/**
	 * Constructs a NameGenPanel given a dataPath
	 *
	 * @param dataPath The path to the random name data files.
	 */
	public NameGenPanel(File dataPath)
	{
		initComponents();
		try
		{
			nameGen = new NameGenerator(dataPath);
			loadDropdowns();
		}
		catch (IOException e)
		{
			Logging.errorPrint(e.getMessage(), e);
			JOptionPane.showMessageDialog(this, "Failed to load name data: " + e.getMessage());
		}
	}

	/**
	 * Generate a Rule
	 * @return new Rule
	 */
	public Rule generate()
	{
		try
		{
			RuleSet rs = (RuleSet) cbCatalog.getSelectedItem();
			if (rs == null)
			{
				return null;
			}
			GeneratedName result;
			if (chkStructure.isSelected())
			{
				result = nameGen.generate(rs);
			}
			else
			{
				Rule rule = (Rule) cbStructure.getSelectedItem();
				result = nameGen.generateWithRule(rule);
			}
			name.setText(result.name());
			meaning.setText(result.meaning());
			pronounciation.setText(result.pronunciation());
			return result.rule();
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
			return null;
		}
	}

	private void NameButtonActionPerformed(ActionEvent evt)
	{
		try
		{
			NameButton nb = (NameButton) evt.getSource();
			DataElement element = nb.getDataElement();
			element.getData();

			Rule rule = this.lastRule;

			if (rule == null)
			{
				if (chkStructure.isSelected())
				{
					RuleSet rs = (RuleSet) cbCatalog.getSelectedItem();
					rule = rs.getLastRule();
				}
				else
				{
					rule = (Rule) cbStructure.getSelectedItem();
				}

				this.lastRule = rule;
			}

			List<pcgen.core.namegen.DataValue> aName = rule.getLastData();

			StringBuilder n = new StringBuilder();
			StringBuilder m = new StringBuilder();
			StringBuilder p = new StringBuilder();
			for (pcgen.core.namegen.DataValue v : aName)
			{
				n.append(v.getValue());
				String mm = v.getSubValue("meaning");
				m.append(mm == null ? v.getValue() : mm);
				String pp = v.getSubValue("pronounciation");
				p.append(pp == null ? v.getValue() : pp);
			}
			name.setText(n.toString());
			meaning.setText(m.toString());
			pronounciation.setText(p.toString());
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	private void cbCatalogActionPerformed(ActionEvent evt)
	{
		loadStructureDD();
		this.clearButtons();
	}

	private void cbStructureActionPerformed(ActionEvent evt)
	{
		this.clearButtons();
	}

	private void cbCategoryActionPerformed(ActionEvent evt)
	{
		this.loadGenderDD();
		loadCatalogDD();
		loadStructureDD();
		this.clearButtons();
	}

	private void cbGenderActionPerformed(ActionEvent evt)
	{
		loadCatalogDD();
		loadStructureDD();
		this.clearButtons();
	}

	private void chkStructureActionPerformed(ActionEvent evt)
	{
		loadStructureDD();
	}

	private void clearButtons()
	{
		buttonPanel.removeAll();
		buttonPanel.repaint();
	}

	private void displayButtons(Rule rule)
	{
		clearButtons();

		for (String key : rule)
		{
			try
			{
				DataElement ele = nameGen.getData().allVars().getDataElement(key);

				if (ele.getTitle() != null)
				{
					NameButton nb = new NameButton(ele);
					nb.addActionListener(this::NameButtonActionPerformed);
					buttonPanel.add(nb);
				}
			}
			catch (Exception e)
			{
				Logging.errorPrint(e.getMessage(), e);
			}
		}

		buttonPanel.repaint();
	}

	private void generateButtonActionPerformed(ActionEvent evt)
	{
		try
		{
			this.lastRule = generate();
			displayButtons(this.lastRule);
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		genCtrlPanel = new JPanel();
		jPanel4 = new JPanel();
		jPanel13 = new JPanel();
		jPanel10 = new JPanel();
		jLabel4 = new JLabel();
		cbCatalog = new JComboBox<>();
		jPanel8 = new JPanel();
		jLabel1 = new JLabel();
		cbCategory = new JComboBox<>();
		jPanel14 = new JPanel();
		jPanel11 = new JPanel();
		generateButton = new JButton();
		jPanel9 = new JPanel();
		jLabel5 = new JLabel();
		cbGender = new JComboBox<>();
		jPanel7 = new JPanel();
		jSeparator4 = new JSeparator();
		jPanel12 = new JPanel();
		jLabel6 = new JLabel();
		cbStructure = new JComboBox<>();
		chkStructure = new JCheckBox();
		buttonPanel = new JPanel();
		nameDisplayPanel = new JPanel();
		nameSubInfoPanel = new JPanel();
		jSeparator2 = new JSeparator();
		jLabel2 = new JLabel();
		meaning = new JLabel();
		jSeparator1 = new JSeparator();
		jLabel3 = new JLabel();
		pronounciation = new JLabel();
		jSeparator3 = new JSeparator();
		namePanel = new JPanel();
		name = new JTextField();
		nameActionPanel = new JPanel();
		jButton1 = new JButton();

		setLayout(new BorderLayout(0, 5));

		genCtrlPanel.setLayout(new BorderLayout());

		jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.X_AXIS));

		jPanel13.setLayout(new BorderLayout());

		jPanel10.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel4.setText(LanguageBundle.getString("in_rndNameCatalog")); //$NON-NLS-1$
		jPanel10.add(jLabel4);

		cbCatalog.addActionListener(this::cbCatalogActionPerformed);

		jPanel10.add(cbCatalog);

		jPanel13.add(jPanel10, BorderLayout.CENTER);

		jPanel8.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel1.setText(LanguageBundle.getString("in_rndNameCategory")); //$NON-NLS-1$
		jPanel8.add(jLabel1);

		cbCategory.addActionListener(this::cbCategoryActionPerformed);

		jPanel8.add(cbCategory);

		jPanel13.add(jPanel8, BorderLayout.NORTH);

		jPanel4.add(jPanel13);

		jPanel14.setLayout(new BorderLayout());

		jPanel11.setLayout(new FlowLayout(FlowLayout.LEFT));

		generateButton.setText(LanguageBundle.getString("in_rndNameGenerate")); //$NON-NLS-1$
		generateButton.addActionListener(this::generateButtonActionPerformed);

		jPanel11.add(generateButton);

		jPanel14.add(jPanel11, BorderLayout.CENTER);

		jPanel9.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel5.setText(LanguageBundle.getString("in_rndNameSex")); //$NON-NLS-1$
		jPanel9.add(jLabel5);

		cbGender.addActionListener(this::cbGenderActionPerformed);

		jPanel9.add(cbGender);

		jPanel14.add(jPanel9, BorderLayout.NORTH);

		jPanel4.add(jPanel14);

		genCtrlPanel.add(jPanel4, BorderLayout.NORTH);

		jPanel7.setLayout(new BorderLayout());

		jPanel7.add(jSeparator4, BorderLayout.NORTH);

		jPanel12.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel6.setText(LanguageBundle.getString("in_rndNameStructure")); //$NON-NLS-1$
		jPanel12.add(jLabel6);

		cbStructure.setEnabled(false);
		cbStructure.addActionListener(this::cbStructureActionPerformed);
		jPanel12.add(cbStructure);

		chkStructure.setSelected(true);
		chkStructure.setText(LanguageBundle.getString("in_randomButton")); //$NON-NLS-1$
		chkStructure.addActionListener(this::chkStructureActionPerformed);

		jPanel12.add(chkStructure);

		jPanel7.add(jPanel12, BorderLayout.CENTER);
		jPanel7.add(new JSeparator(), BorderLayout.SOUTH);

		JPanel adjustNamePanel = new JPanel();
		adjustNamePanel.setLayout(new BorderLayout());

		JLabel adjNameLabel = new JLabel(LanguageBundle.getString("in_rndNameAdjust")); //$NON-NLS-1$

		adjustNamePanel.add(adjNameLabel, BorderLayout.NORTH);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		// CODE-2099 Component needed to have correct vertical space available.
		JLabel nb = new JLabel(" "); //$NON-NLS-1$
		buttonPanel.add(nb);

		adjustNamePanel.add(buttonPanel, BorderLayout.CENTER);

		add(adjustNamePanel, BorderLayout.SOUTH);

		genCtrlPanel.add(jPanel7, BorderLayout.CENTER);

		// Name display
		nameDisplayPanel.setLayout(new BorderLayout());

		nameSubInfoPanel.setLayout(new BoxLayout(nameSubInfoPanel, BoxLayout.Y_AXIS));

		nameSubInfoPanel.add(jSeparator2);

		jLabel2.setText(LanguageBundle.getString("in_rndNameMeaning")); //$NON-NLS-1$
		nameSubInfoPanel.add(jLabel2);

		meaning.setText(LanguageBundle.getString("in_rndNmDefault")); //$NON-NLS-1$
		nameSubInfoPanel.add(meaning);

		nameSubInfoPanel.add(jSeparator1);

		jLabel3.setText(LanguageBundle.getString("in_rndNmPronounciation")); //$NON-NLS-1$
		nameSubInfoPanel.add(jLabel3);

		pronounciation.setText("nAm");
		nameSubInfoPanel.add(pronounciation);

		nameSubInfoPanel.add(jSeparator3);

		nameDisplayPanel.add(nameSubInfoPanel, BorderLayout.SOUTH);

		JLabel nameTitleLabel = new JLabel(LanguageBundle.getString("in_sumName")); //$NON-NLS-1$
		JPanel nameTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		nameTitlePanel.add(nameTitleLabel);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));

		FontManipulation.xxlarge(name);
		name.setText(LanguageBundle.getString("in_nameLabel")); //$NON-NLS-1$
		namePanel.add(name);

		nameActionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		jButton1.setIcon(Icons.Copy16.getImageIcon());
		jButton1.setAlignmentY(0.0F);
		jButton1.setIconTextGap(0);
		jButton1.setMargin(new Insets(2, 2, 2, 2));
		jButton1.addActionListener(this::jButton1ActionPerformed);
		nameActionPanel.add(jButton1);

		namePanel.add(nameActionPanel);

		topPanel.add(genCtrlPanel);
		topPanel.add(nameTitlePanel);
		topPanel.add(namePanel);
		topPanel.add(nameDisplayPanel);
		add(topPanel, BorderLayout.NORTH);
	}

	private void jButton1ActionPerformed(ActionEvent evt)
	{
		Clipboard cb = getToolkit().getSystemClipboard();
		StringSelection ss = new StringSelection(name.getText());
		cb.setContents(ss, ss);
	}

	private void loadCatalogDD()
	{
		try
		{
			String catKey = (String) cbCategory.getSelectedItem();
			String genderKey = (String) cbGender.getSelectedItem();
			RuleSet oldRS = (RuleSet) cbCatalog.getSelectedItem();
			String catalogKey = "";

			if (oldRS != null)
			{
				catalogKey = oldRS.getTitle();
			}

			List<RuleSet> cats = nameGen.getData().categories().getOrDefault(catKey, List.of());
			List<RuleSet> genders = nameGen.getData().categories().getOrDefault("Sex: " + genderKey, List.of());
			List<RuleSet> join = new ArrayList<>(cats);
			join.retainAll(genders);
			join.sort(new DataElementComperator());

			Vector<RuleSet> catalogs = new Vector<>();
			int oldSelected = -1;
			int n = 0;

			for (final RuleSet rs : join)
			{
				if (rs.getUsage().equals("final"))
				{
					catalogs.add(rs);

					if (rs.getTitle().equals(catalogKey))
					{
						oldSelected = n;
					}

					n++;
				}
			}

			ComboBoxModel<RuleSet> catalogModel = new DefaultComboBoxModel<>(catalogs);
			cbCatalog.setModel(catalogModel);
			if (oldSelected >= 0)
			{
				cbCatalog.setSelectedIndex(oldSelected);
			}
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	//	Load the gender drop dowd
	private void loadGenderDD()
	{
		String gender = (String) cbGender.getSelectedItem();
		String category = (String) cbCategory.getSelectedItem();

		// Build "available genders for this category" by scanning Sex: keys
		List<String> selectable = new ArrayList<>();
		List<RuleSet> categoryRules = nameGen.getData().categories().getOrDefault(category, List.of());
		for (var entry : nameGen.getData().categories().entrySet())
		{
			if (!entry.getKey().startsWith("Sex:"))
			{
				continue;
			}
			List<RuleSet> genderRules = entry.getValue();
			for (RuleSet categoryRule : categoryRules)
			{
				if (genderRules.contains(categoryRule))
				{
					selectable.add(entry.getKey().substring(5).trim());
					break;
				}
			}
		}
		Collections.sort(selectable);

		cbGender.setModel(new DefaultComboBoxModel<>(new Vector<>(selectable)));
		if (gender != null && selectable.contains(gender))
		{
			cbGender.setSelectedItem(gender);
		}
	}

	private void loadDropdowns()
	{
		Vector<String> cats = new Vector<>(nameGen.getCategories());
		cbCategory.setModel(new DefaultComboBoxModel<>(cats));

		this.loadGenderDD();
		this.loadCatalogDD();
	}

	private void loadStructureDD()
	{
		if (chkStructure.isSelected())
		{
			cbStructure.setModel(new DefaultComboBoxModel<>());
			cbStructure.setEnabled(false);
		}
		else
		{
			RuleSet selected = (RuleSet) cbCatalog.getSelectedItem();
			if (selected == null)
			{
				return;
			}
			Vector<DataElement> struct = new Vector<>();
			for (Rule r : nameGen.getRulesFor(selected))
			{
				struct.add(r);
			}
			DefaultComboBoxModel<DataElement> structModel = new DefaultComboBoxModel<>(struct);
			cbStructure.setModel(structModel);
			cbStructure.setEnabled(true);
		}
	}

	/**
	 * @return the generated name chosen by the user
	 */
	public String getChosenName()
	{
		return name.getText();
	}

	/**
	 * @return the gender
	 */
	public String getGender()
	{
		return (String) cbGender.getSelectedItem();
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender)
	{
		if (gender != null && cbGender != null)
		{
			cbGender.setSelectedItem(gender);
		}
	}
}
