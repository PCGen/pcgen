/*
 * LanguagePanel.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 26/10/2008 14:51:48
 *
 * $Id: $
 */
package pcgen.gui.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.UnitSet;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.Utility;
import pcgen.util.PropertyFactory;

/**
 * The Class <code>LanguagePanel</code> is responsible for 
 * managing the language and unit set preferences.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
@SuppressWarnings("serial")
public class LanguagePanel extends PCGenPrefsPanel
{
	private static String in_language =
			PropertyFactory.getString("in_Prefs_language");
	private static String in_langEnglish =
			PropertyFactory.getString("in_Prefs_langEnglish");
	private static String in_langFrench =
			PropertyFactory.getString("in_Prefs_langFrench");
	private static String in_langGerman =
			PropertyFactory.getString("in_Prefs_langGerman");
	private static String in_langItalian =
			PropertyFactory.getString("in_Prefs_langItalian");
	private static String in_langSpanish =
			PropertyFactory.getString("in_Prefs_langSpanish");
	private static String in_langPortuguese =
			PropertyFactory.getString("in_Prefs_langPortuguese");
	private static String in_langSystem =
			PropertyFactory.getString("in_Prefs_langSystem");
	private static String in_unitSetType =
			PropertyFactory.getString("in_Prefs_unitSetType");

	private String[] unitSetNames = null;

	private JRadioButton langEng;
	private JRadioButton langFre;
	private JRadioButton langGer;
	private JRadioButton langIt;
	private JRadioButton langEs;
	private JRadioButton langPt;
	private JRadioButton langSystem;
	private JComboBoxEx unitSetType = new JComboBoxEx();

	/**
	 * Create a new LanguagePanel
	 */
	public LanguagePanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_language);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		int line = 0;

		// Use OS system language
		line =
				addLanguageOption(line, c, gridbag, this, langSystem =
						new JRadioButton(in_langSystem), exclusiveGroup);

		final SortedSet<JRadioButton> sorted =
				new TreeSet<JRadioButton>(new Comparator<JRadioButton>()
				{
					public int compare(final JRadioButton o1,
						final JRadioButton o2)
					{
						return o1.getText().compareToIgnoreCase(o2.getText());
					}
				});

		sorted.add(langEng = new JRadioButton(in_langEnglish));
		sorted.add(langFre = new JRadioButton(in_langFrench));
		sorted.add(langGer = new JRadioButton(in_langGerman));
		sorted.add(langIt = new JRadioButton(in_langItalian));
		sorted.add(langEs = new JRadioButton(in_langSpanish));
		sorted.add(langPt = new JRadioButton(in_langPortuguese));

		for (JRadioButton b : sorted)
		{
			line = addLanguageOption(line, c, gridbag, this, b, exclusiveGroup);
		}

		Utility.buildConstraints(c, 0, line++, 1, 1, 0, 0);
		label = new JLabel(in_unitSetType + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 1, line++, 2, 1, 0, 0);
		Map<String, UnitSet> unitSetList = SystemCollections.getUnitSetList();
		unitSetNames = new String[unitSetList.size()];
		int i = 0;
		for (UnitSet unitSet : unitSetList.values())
		{
			if (unitSet != null)
			{
				unitSetNames[i++] = unitSet.getName();
			}
		}

		unitSetType = new JComboBoxEx(unitSetNames);
		gridbag.setConstraints(unitSetType, c);
		this.add(unitSetType);

		Utility.buildConstraints(c, 5, line, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private static int addLanguageOption(int line,
		final GridBagConstraints constraints, final GridBagLayout gridbag,
		final JPanel panel, final JRadioButton button, final ButtonGroup group)
	{
		Utility.buildConstraints(constraints, 0, line++, 2, 1, 0, 0);
		gridbag.setConstraints(button, constraints);
		panel.add(button);
		group.add(button);

		return line;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.prefs.PCGenPrefsPanel#applyOptionValuesToControls()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		langEng.setSelected(false);
		langFre.setSelected(false);
		langGer.setSelected(false);
		langIt.setSelected(false);
		langEs.setSelected(false);
		langPt.setSelected(false);
		langSystem.setSelected(false);

		String language = Globals.getLanguage();
		if (language == null || language.equals(""))
		{
			langSystem.setSelected(true);
		}
		else if (Globals.getLanguage().equals("en"))
		{
			langEng.setSelected(true);
		}
		else if (Globals.getLanguage().equals("fr"))
		{
			langFre.setSelected(true);
		}
		else if (Globals.getLanguage().equals("de"))
		{
			langGer.setSelected(true);
		}
		else if (Globals.getLanguage().equals("it"))
		{
			langIt.setSelected(true);
		}
		else if (Globals.getLanguage().equals("es"))
		{
			langEs.setSelected(true);
		}
		else if (Globals.getLanguage().equals("pt"))
		{
			langPt.setSelected(true);
		}
		else
		{
			// Default to English
			langSystem.setSelected(true);
		}

		unitSetType.setSelectedIndex(0);
		for (int i = 0; i < SystemCollections.getUnitSetList().size(); ++i)
		{
			if (unitSetNames[i].equals(SettingsHandler.getGameModeUnitSet()
				.getName()))
			{
				unitSetType.setSelectedIndex(i);
			}
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_language;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.prefs.PCGenPrefsPanel#setOptionsBasedOnControls()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		if (langEng.isSelected())
		{
			Globals.setLanguage("en");
			Globals.setCountry("US");
		}
		else if (langFre.isSelected())
		{
			Globals.setLanguage("fr");
			Globals.setCountry("FR");
		}
		else if (langGer.isSelected())
		{
			Globals.setLanguage("de");
			Globals.setCountry("DE");
		}
		else if (langIt.isSelected())
		{
			Globals.setLanguage("it");
			Globals.setCountry("IT");
		}
		else if (langEs.isSelected())
		{
			Globals.setLanguage("es");
			Globals.setCountry("ES");
		}
		else if (langPt.isSelected())
		{
			Globals.setLanguage("pt");
			Globals.setCountry("PT");
		}
		else if (langSystem.isSelected())
		{
			Globals.setLanguage(null);
			Globals.setCountry(null);
		}

		SettingsHandler.getGame().selectUnitSet(
			(String) unitSetType.getSelectedItem());
	}

}
