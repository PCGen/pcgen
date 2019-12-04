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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.doomsdaybook;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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

import pcgen.core.doomsdaybook.CRRule;
import pcgen.core.doomsdaybook.DataElement;
import pcgen.core.doomsdaybook.DataElementComperator;
import pcgen.core.doomsdaybook.DataValue;
import pcgen.core.doomsdaybook.HyphenRule;
import pcgen.core.doomsdaybook.Rule;
import pcgen.core.doomsdaybook.RuleSet;
import pcgen.core.doomsdaybook.SpaceRule;
import pcgen.core.doomsdaybook.VariableHashMap;
import pcgen.core.doomsdaybook.WeightedDataValue;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.FontManipulation;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import org.jdom2.DataConversionException;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Main panel of the random name generator.
 */
@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceVectorWithList", "PMD.UseArrayListInsteadOfVector"})
public class NameGenPanel extends JPanel
{
	private final Map<String, List<RuleSet>> categories = new HashMap<>();
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
	private final VariableHashMap allVars = new VariableHashMap();

	private Rule lastRule = null;

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
		loadData(dataPath);
	}

	/**
	 * Generate a Rule
	 * @return new Rule
	 */
	public Rule generate()
	{
		try
		{
			Rule rule;

			if (chkStructure.isSelected())
			{
				RuleSet rs = (RuleSet) cbCatalog.getSelectedItem();
				rule = rs.getRule();
			}
			else
			{
				rule = (Rule) cbStructure.getSelectedItem();
			}

			List<DataValue> aName = rule.getData();
			setNameText(aName);
			setMeaningText(aName);
			setPronounciationText(aName);

			return rule;
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);

			return null;
		}
	}

	private void setMeaningText(String meaning)
	{
		this.meaning.setText(meaning);
	}

	private void setMeaningText(Iterable<DataValue> data)
	{
		StringBuilder meaningBuffer = new StringBuilder();

		for (DataValue val : data)
		{
			String aMeaning = val.getSubValue("meaning"); //$NON-NLS-1$ // XML attribute no translation

			if (aMeaning == null)
			{
				aMeaning = val.getValue();
			}

			meaningBuffer.append(aMeaning);
		}

		setMeaningText(meaningBuffer.toString());
	}

	private void setNameText(String name)
	{
		this.name.setText(name);
	}

	private void setNameText(Iterable<DataValue> data)
	{
		StringBuilder nameBuffer = new StringBuilder();

		for (DataValue val : data)
		{
			nameBuffer.append(val.getValue());
		}

		setNameText(nameBuffer.toString());
	}

	private void setPronounciationText(String pronounciation)
	{
		this.pronounciation.setText(pronounciation);
	}

	private void setPronounciationText(Iterable<DataValue> data)
	{
		StringBuilder proBuffer = new StringBuilder();

		for (DataValue val : data)
		{
			String aPronounciation = val.getSubValue("pronounciation");

			if (aPronounciation == null)
			{
				aPronounciation = val.getValue();
			}

			proBuffer.append(aPronounciation);
		}

		setPronounciationText(proBuffer.toString());
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

			List<DataValue> aName = rule.getLastData();

			setNameText(aName);
			setMeaningText(aName);
			setPronounciationText(aName);
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	private void cbCatalogActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_cbCatalogActionPerformed
		loadStructureDD();
		this.clearButtons();
	}

	//GEN-LAST:event_cbCatalogActionPerformed

	private void cbStructureActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_cbStructureActionPerformed
		this.clearButtons();
	}

	//GEN-LAST:event_cbStructureActionPerformed

	private void cbCategoryActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_cbCategoryActionPerformed
		this.loadGenderDD();
		loadCatalogDD();
		loadStructureDD();
		this.clearButtons();
	}

	//GEN-LAST:event_cbCategoryActionPerformed

	private void cbGenderActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_cbGenderActionPerformed
		loadCatalogDD();
		loadStructureDD();
		this.clearButtons();
	}

	//GEN-LAST:event_cbGenderActionPerformed

	private void chkStructureActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_chkStructureActionPerformed
		loadStructureDD();
	}

	//GEN-LAST:event_chkStructureActionPerformed

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
				DataElement ele = allVars.getDataElement(key);

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
	{ //GEN-FIRST:event_generateButtonActionPerformed

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

	//GEN-LAST:event_generateButtonActionPerformed

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

	//GEN-END:initComponents

	private void jButton1ActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_jButton1ActionPerformed
		Clipboard cb = getToolkit().getSystemClipboard();
		StringSelection ss = new StringSelection(name.getText());
		cb.setContents(ss, ss);
	}

	//GEN-LAST:event_jButton1ActionPerformed

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

			List<RuleSet> cats = categories.get(catKey);
			List<RuleSet> genders = categories.get("Sex: " + genderKey);
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

	//	Get a list of all the gender categories in the category map
	private Vector<String> getGenderCategoryNames()
	{
		Vector<String> genders = new Vector<>();
		Set<String> keySet = categories.keySet();

		//	Loop through the keys in the categories
		for (final String key : keySet)
		{
			//	if the key starts with "Sex" then save it
			if (key.startsWith("Sex:"))
			{
				genders.add(key.substring(5));
			}
		}

		//	Return all the found gender types
		return genders;
	}

	//	Load the gender drop dowd
	private void loadGenderDD()
	{
		List<String> genders = getGenderCategoryNames();
		Vector<String> selectable = new Vector<>();
		String gender = (String) cbGender.getSelectedItem();

		//	Get the selected category name
		String category = (String) cbCategory.getSelectedItem();

		//	Get the set of rules for selected category
		List<RuleSet> categoryRules = categories.get(category);

		//	we need to determine if the selected category is supported by the 
		//	available genders
		//	loop through the available genders
		for (String genderString : genders)
		{
			//	Get the list of rules for the current gender
			List<RuleSet> genderRules = categories.get("Sex: " + genderString);

			//	now loop through all the rules from the selected category
			for (RuleSet categoryRule : categoryRules)
			{
				//	if the category rule is in the list of gender rules
				//	add the current gender to the selectable gender list
				//	we can stop processing the list once we find a match
				if (genderRules.contains(categoryRule))
				{
					selectable.add(genderString);
					break;
				}
			}
		}

		//	Sort the genders
		Collections.sort(selectable);

		//	Create a new model for the combobox and set it
		cbGender.setModel(new DefaultComboBoxModel<>(selectable));
		if (gender != null && selectable.contains(gender))
		{
			cbGender.setSelectedItem(gender);
		}
	}

	private void loadCategory(Element category, RuleSet rs)
	{
		List<RuleSet> cat = categories.get(category.getAttributeValue("title"));
		List<RuleSet> thiscat;

		if (cat == null)
		{
			thiscat = new ArrayList<>();
			categories.put(category.getAttributeValue("title"), thiscat);
		}
		else
		{
			thiscat = cat;
		}

		thiscat.add(rs);
	}

	private void loadData(File path)
	{
		if (path.isDirectory())
		{
			File[] dataFiles = path.listFiles(new XMLFilter());
			SAXBuilder builder = new SAXBuilder();
			GeneratorDtdResolver resolver = new GeneratorDtdResolver(path);
			builder.setEntityResolver(resolver);

			for (File dataFile : dataFiles)
			{
				try
				{
					URL url = dataFile.toURI().toURL();
					Document nameSet = builder.build(url);
					DocType dt = nameSet.getDocType();

					if (dt.getElementName().equals("GENERATOR"))
					{
						loadFromDocument(nameSet);
					}
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
					JOptionPane.showMessageDialog(this, "XML Error with file " + dataFile.getName());
				}
			}

			loadDropdowns();
		}
		else
		{
			JOptionPane.showMessageDialog(this, "No data files in directory " + path.getPath());
		}
	}

	//	Get a list of category names from the categories map
	private Vector<String> getCategoryNames()
	{
		Vector<String> cats = new Vector<>();
		Set<String> keySet = categories.keySet();

		for (final String key : keySet)
		{
			//	Ignore any category that starts with this
			if (key.startsWith("Sex:"))
			{
				continue;
			}

			cats.add(key);
		}

		//	Sor the selected categories before returning it
		Collections.sort(cats);

		return cats;
	}

	private void loadDropdowns()
	{
		//	This method now just loads the category dropdown from the list of 
		//	category names
		Vector<String> cats = this.getCategoryNames();
		cbCategory.setModel(new DefaultComboBoxModel<>(cats));

		this.loadGenderDD();
		this.loadCatalogDD();
	}

	private void loadFromDocument(Document nameSet) throws DataConversionException
	{
		Element generator = nameSet.getRootElement();
		java.util.List<?> rulesets = generator.getChildren("RULESET");
		java.util.List<?> lists = generator.getChildren("LIST");

        for (Object o : lists) {
            Element list = (Element) o;
            loadList(list);
        }

		for (final Object ruleset : rulesets)
		{
			Element ruleSet = (Element) ruleset;
			RuleSet rs = loadRuleSet(ruleSet);
			allVars.addDataElement(rs);
		}
	}

	private String loadList(Element list) throws DataConversionException
	{
		pcgen.core.doomsdaybook.DDList dataList = new pcgen.core.doomsdaybook.DDList(allVars,
			list.getAttributeValue("title"), list.getAttributeValue("id"));
		java.util.List<?> elements = list.getChildren();

		for (final Object element : elements)
		{
			Element child = (Element) element;
			String elementName = child.getName();

			if (elementName.equals("VALUE"))
			{
				WeightedDataValue dv =
						new WeightedDataValue(child.getText(), child.getAttribute("weight").getIntValue());
				List<?> subElements = child.getChildren("SUBVALUE");

				for (final Object subElement1 : subElements)
				{
					Element subElement = (Element) subElement1;
					dv.addSubValue(subElement.getAttributeValue("type"), subElement.getText());
				}

				dataList.add(dv);
			}
		}

		allVars.addDataElement(dataList);

		return dataList.getId();
	}

	private String loadRule(Element rule, String id) throws DataConversionException
	{
		Rule dataRule = new Rule(allVars, id, id, rule.getAttribute("weight").getIntValue());
		java.util.List<?> elements = rule.getChildren();

		for (final Object element : elements)
		{
			Element child = (Element) element;
			String elementName = child.getName();

            switch (elementName) {
                case "GETLIST":
                    String listId = child.getAttributeValue("idref");
                    dataRule.add(listId);
                    break;
                case "SPACE":
                    SpaceRule sp = new SpaceRule();
                    allVars.addDataElement(sp);
                    dataRule.add(sp.getId());
                    break;
                case "HYPHEN":
                    HyphenRule hy = new HyphenRule();
                    allVars.addDataElement(hy);
                    dataRule.add(hy.getId());
                    break;
                case "CR":
                    CRRule cr = new CRRule();
                    allVars.addDataElement(cr);
                    dataRule.add(cr.getId());
                    break;
                case "GETRULE":
                    String ruleId = child.getAttributeValue("idref");
                    dataRule.add(ruleId);
                    break;
            }
		}

		allVars.addDataElement(dataRule);

		return dataRule.getId();
	}

	private RuleSet loadRuleSet(Element ruleSet) throws DataConversionException
	{
		RuleSet rs = new RuleSet(allVars, ruleSet.getAttributeValue("title"), ruleSet.getAttributeValue("id"),
			ruleSet.getAttributeValue("usage"));
		java.util.List<?> elements = ruleSet.getChildren();
		ListIterator<?> elementsIterator = elements.listIterator();
		int num = 0;

		while (elementsIterator.hasNext())
		{
			Element child = (Element) elementsIterator.next();
			String elementName = child.getName();

			if (elementName.equals("CATEGORY"))
			{
				loadCategory(child, rs);
			}
			else if (elementName.equals("RULE"))
			{
				rs.add(loadRule(child, rs.getId() + num));
			}

			num++;
		}

		return rs;
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
			Vector<DataElement> struct = new Vector<>();

			for (String key : ((RuleSet) cbCatalog.getSelectedItem()))
			{
				try
				{
					struct.add(allVars.getDataElement(key));
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}
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

	/**
	 * The Class {@code GeneratorDtdResolver} is an EntityResolver implementation
	 * for use with a SAX parser. It forces the generator.dtd to be read from a 
	 * known location.
	 */
	public static class GeneratorDtdResolver implements EntityResolver
	{

		private final File parent;

		/**
		 * Create a new instance of GeneratorDtdResolver to read the 
		 * generator.dtd from a specific directory.
		 * @param parent The parent directory holding generator.dtd
		 */
		GeneratorDtdResolver(File parent)
		{
			this.parent = parent;

		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
		{
			if (systemId.endsWith("generator.dtd"))
			{
				// return a special input source
				InputStream dtdIn;
				try
				{
					dtdIn = new FileInputStream(new File(parent, "generator.dtd"));
				}
				catch (FileNotFoundException e)
				{
					Logging.errorPrint("GeneratorDtdResolver.resolveEntity failed", e);
					return null;

				}
				return new InputSource(dtdIn);
			}
			else
			{
				// use the default behaviour
				return null;
			}
		}
	}
}
