/*
 * NameGenPanel.java
 *
 * Created on April 24, 2003, 1:03 PM
 */
package plugin.doomsdaybook.gui;

import gmgen.util.LogUtilities;
import org.jdom.DataConversionException;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import pcgen.util.Logging;
import pcgen.gui.utils.IconUtilitities;
import plugin.doomsdaybook.RandomNamePlugin;
import plugin.doomsdaybook.util.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;

/**
 *
 * @author  devon
 */
public class NameGenPanel extends JPanel
{
	// End of variables declaration//GEN-END:variables
	public Preferences namePrefs = Preferences.userNodeForPackage(NameGenPanel.class);
	private HashMap categories = new HashMap();
	private JButton generateButton;
	private JButton jButton1;
	private JCheckBox chkStructure;
	private JComboBox cbCatalog;
	private JComboBox cbCategory;
	private JComboBox cbSex;
	private JComboBox cbStructure;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel6;
	private JLabel meaning;
	private JLabel pronounciation;
	private JPanel buttonPanel;
	private JPanel jPanel1;
	private JPanel jPanel10;
	private JPanel jPanel11;
	private JPanel jPanel12;
	private JPanel jPanel13;
	private JPanel jPanel14;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JPanel jPanel4;
	private JPanel jPanel5;
	private JPanel jPanel6;
	private JPanel jPanel7;
	private JPanel jPanel8;
	private JPanel jPanel9;
	private JSeparator jSeparator1;
	private JSeparator jSeparator2;
	private JSeparator jSeparator3;
	private JSeparator jSeparator4;
	private JTextField name;
	private VariableHashMap allVars = new VariableHashMap();

	/** Creates new form NameGenPanel */
	public NameGenPanel()
	{
		initComponents();
		initPrefs();
		loadData(new File("."));
	}

	public NameGenPanel(File dataPath)
	{
		initComponents();
		initPrefs();
		loadData(dataPath);
	}

	public void setExitPrefs()
	{
	    // TODO:  Method doesn't do anything?
	}

	public Rule generate()
	{
		try
		{
			if (chkStructure.isSelected())
			{
				RuleSet rs = (RuleSet) cbCatalog.getSelectedItem();
				Rule rule = rs.getRule();
				ArrayList aName = rule.getData();
				setNameText(aName);
				setMeaningText(aName);
				setPronounciationText(aName);

				return rule;
			}
			Rule rule = (Rule) cbStructure.getSelectedItem();
			ArrayList aName = rule.getData();
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

	/**
	 *  Initialization of the bulk of preferences.  sets the defaults
	 *  if this is the first time you have used this version
	 */
	public void initPrefs()
	{
		boolean prefsSet = namePrefs.getBoolean("arePrefsSet", false);

		if (!prefsSet)
		{
			namePrefs.putBoolean("arePrefsSet", true);
		}

		double version = namePrefs.getDouble("Version", 0);

		if ((version < 0.5) || !prefsSet)
		{
			namePrefs.putDouble("Version", 0.5);
		}

		namePrefs.putDouble("SubVersion", 0);
	}

	private void setMeaningText(String meaning)
	{
		this.meaning.setText(meaning);
	}

	private void setMeaningText(ArrayList data)
	{
		StringBuffer meaningBuffer = new StringBuffer();

		for (int i = 0; i < data.size(); i++)
		{
			DataValue val = (DataValue) data.get(i);
			String aMeaning = val.getSubValue("meaning");

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
		LogUtilities.inst().logMessage(RandomNamePlugin.LOG_NAME, name);
	}

	private void setNameText(ArrayList data)
	{
		StringBuffer nameBuffer = new StringBuffer();

		for (int i = 0; i < data.size(); i++)
		{
			DataValue val = (DataValue) data.get(i);
			nameBuffer.append(val.getValue());
		}

		setNameText(nameBuffer.toString());
	}

	private void setPronounciationText(String pronounciation)
	{
		this.pronounciation.setText(pronounciation);
	}

	private void setPronounciationText(ArrayList data)
	{
		StringBuffer proBuffer = new StringBuffer();

		for (int i = 0; i < data.size(); i++)
		{
			DataValue val = (DataValue) data.get(i);
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

			RuleSet rs = (RuleSet) cbCatalog.getSelectedItem();
			Rule rule = rs.getLastRule();
			ArrayList aName = rule.getLastData();
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
	}
	 //GEN-LAST:event_cbCatalogActionPerformed

	private void cbCategoryActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_cbCategoryActionPerformed
		loadCatalogDD();
		loadStructureDD();
	}
	 //GEN-LAST:event_cbCategoryActionPerformed

	private void cbSexActionPerformed(ActionEvent evt)
	{ //GEN-FIRST:event_cbSexActionPerformed
		loadCatalogDD();
		loadStructureDD();
	}
	 //GEN-LAST:event_cbSexActionPerformed

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

		for (int i = 0; i < rule.size(); i++)
		{
			String key = (String) rule.get(i);

			try
			{
				DataElement ele = allVars.getDataElement(key);

				if (ele.getTitle() != null)
				{
					NameButton nb = new NameButton(ele);
					nb.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent evt)
							{
								NameButtonActionPerformed(evt);
							}
						});
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
			Rule rule = generate();
			displayButtons(rule);
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
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents
		jPanel1 = new JPanel();
		jPanel4 = new JPanel();
		jPanel13 = new JPanel();
		jPanel10 = new JPanel();
		jLabel4 = new JLabel();
		cbCatalog = new JComboBox();
		jPanel8 = new JPanel();
		jLabel1 = new JLabel();
		cbCategory = new JComboBox();
		jPanel14 = new JPanel();
		jPanel11 = new JPanel();
		generateButton = new JButton();
		jPanel9 = new JPanel();
		jLabel5 = new JLabel();
		cbSex = new JComboBox();
		jPanel7 = new JPanel();
		jSeparator4 = new JSeparator();
		jPanel12 = new JPanel();
		jLabel6 = new JLabel();
		cbStructure = new JComboBox();
		chkStructure = new JCheckBox();
		buttonPanel = new JPanel();
		jPanel2 = new JPanel();
		jPanel5 = new JPanel();
		jSeparator2 = new JSeparator();
		jLabel2 = new JLabel();
		meaning = new JLabel();
		jSeparator1 = new JSeparator();
		jLabel3 = new JLabel();
		pronounciation = new JLabel();
		jSeparator3 = new JSeparator();
		jPanel3 = new JPanel();
		name = new JTextField();
		jPanel6 = new JPanel();
		jButton1 = new JButton();

		setLayout(new BorderLayout());

		jPanel1.setLayout(new BorderLayout());

		jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.X_AXIS));

		jPanel13.setLayout(new BorderLayout());

		jPanel10.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel4.setText("Catalog");
		jPanel10.add(jLabel4);

		cbCatalog.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					cbCatalogActionPerformed(evt);
				}
			});

		jPanel10.add(cbCatalog);

		jPanel13.add(jPanel10, BorderLayout.CENTER);

		jPanel8.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel1.setText("Category");
		jPanel8.add(jLabel1);

		cbCategory.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					cbCategoryActionPerformed(evt);
				}
			});

		jPanel8.add(cbCategory);

		jPanel13.add(jPanel8, BorderLayout.NORTH);

		jPanel4.add(jPanel13);

		jPanel14.setLayout(new BorderLayout());

		jPanel11.setLayout(new FlowLayout(FlowLayout.LEFT));

		generateButton.setText("Generate");
		generateButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					generateButtonActionPerformed(evt);
				}
			});

		jPanel11.add(generateButton);

		jPanel14.add(jPanel11, BorderLayout.CENTER);

		jPanel9.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel5.setText("Sex");
		jPanel9.add(jLabel5);

		cbSex.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					cbSexActionPerformed(evt);
				}
			});

		jPanel9.add(cbSex);

		jPanel14.add(jPanel9, BorderLayout.NORTH);

		jPanel4.add(jPanel14);

		jPanel1.add(jPanel4, BorderLayout.NORTH);

		jPanel7.setLayout(new BorderLayout());

		jPanel7.add(jSeparator4, BorderLayout.NORTH);

		jPanel12.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel6.setText("Structure");
		jPanel12.add(jLabel6);

		cbStructure.setEnabled(false);
		jPanel12.add(cbStructure);

		chkStructure.setSelected(true);
		chkStructure.setText("Random");
		chkStructure.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					chkStructureActionPerformed(evt);
				}
			});

		jPanel12.add(chkStructure);

		jPanel7.add(jPanel12, BorderLayout.CENTER);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		jPanel7.add(buttonPanel, BorderLayout.SOUTH);

		jPanel1.add(jPanel7, BorderLayout.CENTER);

		add(jPanel1, BorderLayout.CENTER);

		jPanel2.setLayout(new BorderLayout());

		jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));

		jPanel5.add(jSeparator2);

		jLabel2.setText("Meaning:");
		jPanel5.add(jLabel2);

		meaning.setText("Name");
		jPanel5.add(meaning);

		jPanel5.add(jSeparator1);

		jLabel3.setText("Pronounciation:");
		jPanel5.add(jLabel3);

		pronounciation.setText("nAm");
		jPanel5.add(pronounciation);

		jPanel5.add(jSeparator3);

		jPanel2.add(jPanel5, BorderLayout.SOUTH);

		jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

		name.setFont(new Font("Dialog", 1, 18));
		name.setText("Name");
		jPanel3.add(name);

		jPanel6.setLayout(new FlowLayout(FlowLayout.RIGHT));

		jButton1.setIcon(IconUtilitities.getImageIcon("Copy16.gif"));
		jButton1.setAlignmentY(0.0F);
		jButton1.setIconTextGap(0);
		jButton1.setMargin(new Insets(2, 2, 2, 2));
		jButton1.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButton1ActionPerformed(evt);
				}
			});

		jPanel6.add(jButton1);

		jPanel3.add(jPanel6);

		jPanel2.add(jPanel3, BorderLayout.NORTH);

		add(jPanel2, BorderLayout.NORTH);
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
			String sexKey = (String) cbSex.getSelectedItem();
			RuleSet oldRS = (RuleSet) cbCatalog.getSelectedItem();
			String catalogKey = "";

			if (oldRS != null)
			{
				catalogKey = oldRS.getTitle();
			}

			ArrayList cats = (ArrayList) categories.get(catKey);
			ArrayList sexes = (ArrayList) categories.get("Sex: " + sexKey);
			ArrayList join = new ArrayList();
			join.addAll(cats);
			join.retainAll(sexes);

			Vector catalogs = new Vector();
			int oldSelected = 0;
			int n = 0;

			for (int i = 0; i < join.size(); i++)
			{
				RuleSet rs = (RuleSet) join.get(i);

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

			Collections.sort(catalogs, new DataElementComperator());

			DefaultComboBoxModel catalogModel = new DefaultComboBoxModel(catalogs);
			cbCatalog.setModel(catalogModel);
			cbCatalog.setSelectedIndex(oldSelected);
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	private void loadCategory(Element category, RuleSet rs)
	{
		Object cat = categories.get(category.getAttributeValue("title"));
		ArrayList thiscat;

		if (cat == null)
		{
			thiscat = new ArrayList();
			categories.put(category.getAttributeValue("title"), thiscat);
		}
		else
		{
			thiscat = (ArrayList) cat;
		}

		thiscat.add(rs);
	}

	private void loadData(File path)
	{
		if (path.isDirectory())
		{
			File[] dataFiles = path.listFiles(new XMLFilter());
			SAXBuilder builder = new SAXBuilder();

			for (int i = 0; i < dataFiles.length; i++)
			{
				try
				{
					Document nameSet = builder.build(dataFiles[i].toURL());
					DocType dt = nameSet.getDocType();

					if (dt.getElementName().equals("GENERATOR"))
					{
						loadFromDocument(nameSet);
					}

					nameSet = null;
					dt = null;
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "XML Error with file " + dataFiles[i].getName());
					Logging.errorPrint(e.getMessage(), e);
				}
			}

			loadDropdowns();
		}
		else
		{
			JOptionPane.showMessageDialog(this, "No data files in directory " + path.getPath());
		}
	}

	private void loadDropdowns()
	{
		Vector cats = new java.util.Vector();
		Vector sexes = new java.util.Vector();
		Set keySet = categories.keySet();
		Iterator itr = keySet.iterator();

		while (itr.hasNext())
		{
			String key = (String) itr.next();

			if (key.startsWith("Sex:"))
			{
				sexes.add(key.substring(5));
			}
			else
			{
				cats.add(key);
			}
		}

		Collections.sort(cats);
		Collections.sort(sexes);

		DefaultComboBoxModel catModel = new DefaultComboBoxModel(cats);
		DefaultComboBoxModel sexModel = new DefaultComboBoxModel(sexes);
		cbCategory.setModel(catModel);
		cbSex.setModel(sexModel);

		loadCatalogDD();
	}

	private void loadFromDocument(Document nameSet) throws DataConversionException
	{
		Element generator = nameSet.getRootElement();
		java.util.List rulesets = generator.getChildren("RULESET");
		java.util.List lists = generator.getChildren("LIST");
		ListIterator listIterator = lists.listIterator();
		RuleSet rs = new RuleSet(allVars);

		while (listIterator.hasNext())
		{
			Element list = (Element) listIterator.next();
			loadList(list);
		}

		ListIterator rulesetIterator = rulesets.listIterator();

		while (rulesetIterator.hasNext())
		{
			Element ruleSet = (Element) rulesetIterator.next();
			rs = loadRuleSet(ruleSet);
			allVars.addDataElement(rs);
		}
	}

	private String loadList(Element list) throws DataConversionException
	{
		plugin.doomsdaybook.util.List dataList = new plugin.doomsdaybook.util.List(allVars,
			    list.getAttributeValue("title"), list.getAttributeValue("id"));
		java.util.List elements = list.getChildren();
		ListIterator elementsIterator = elements.listIterator();

		while (elementsIterator.hasNext())
		{
			Element child = (Element) elementsIterator.next();
			String elementName = child.getName();

			if (elementName.equals("VALUE"))
			{
				WeightedDataValue dv = new WeightedDataValue(child.getText(), child.getAttribute("weight").getIntValue());
				java.util.List subElements = child.getChildren("SUBVALUE");
				ListIterator subElementsIterator = subElements.listIterator();

				while (subElementsIterator.hasNext())
				{
					Element subElement = (Element) subElementsIterator.next();
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
		java.util.List elements = rule.getChildren();
		ListIterator elementsIterator = elements.listIterator();

		while (elementsIterator.hasNext())
		{
			Element child = (Element) elementsIterator.next();
			String elementName = child.getName();

			if (elementName.equals("GETLIST"))
			{
				String listId = child.getAttributeValue("idref");
				dataRule.add(listId);
			}
			else if (elementName.equals("SPACE"))
			{
				SpaceRule sp = new SpaceRule();
				allVars.addDataElement(sp);
				dataRule.add(sp.getId());
			}
			else if (elementName.equals("HYPHEN"))
			{
				HyphenRule hy = new HyphenRule();
				allVars.addDataElement(hy);
				dataRule.add(hy.getId());
			}
			else if (elementName.equals("CR"))
			{
				CRRule cr = new CRRule();
				allVars.addDataElement(cr);
				dataRule.add(cr.getId());
			}
			else if (elementName.equals("GETRULE"))
			{
				String ruleId = child.getAttributeValue("idref");
				dataRule.add(ruleId);
			}
		}

		allVars.addDataElement(dataRule);

		return dataRule.getId();
	}

	private RuleSet loadRuleSet(Element ruleSet) throws DataConversionException
	{
		RuleSet rs = new RuleSet(allVars, ruleSet.getAttributeValue("title"), ruleSet.getAttributeValue("id"),
			    ruleSet.getAttributeValue("usage"));
		java.util.List elements = ruleSet.getChildren();
		ListIterator elementsIterator = elements.listIterator();
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
			cbStructure.setModel(new DefaultComboBoxModel());
			cbStructure.setEnabled(false);
		}
		else
		{
			Vector struct = new Vector();
			RuleSet rs = (RuleSet) cbCatalog.getSelectedItem();

			for (int i = 0; i < rs.size(); i++)
			{
				String key = (String) rs.get(i);

				try
				{
					struct.add(allVars.getDataElement(key));
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}
			}

			DefaultComboBoxModel structModel = new DefaultComboBoxModel(struct);
			cbStructure.setModel(structModel);
			cbStructure.setEnabled(true);
		}
	}
}
