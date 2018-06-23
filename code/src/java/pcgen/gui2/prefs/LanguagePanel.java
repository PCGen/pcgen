/*
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
 */
package pcgen.gui2.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.UnitSet;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code LanguagePanel} is responsible for
 * managing the language and unit set preferences.
 * 
 * 
 */
@SuppressWarnings("serial")
public class LanguagePanel extends PCGenPrefsPanel
{
	private static final String in_language = LanguageBundle.getString("in_Prefs_language");
	private static final String in_langEnglish = LanguageBundle.getString("in_Prefs_langEnglish");
	private static final String in_langFrench = LanguageBundle.getString("in_Prefs_langFrench");
	private static final String in_langGerman = LanguageBundle.getString("in_Prefs_langGerman");
	private static final String in_langItalian = LanguageBundle.getString("in_Prefs_langItalian");
	private static final String in_langSpanish = LanguageBundle.getString("in_Prefs_langSpanish");
	private static final String in_langPortuguese = LanguageBundle.getString("in_Prefs_langPortuguese");
	private static final String in_langSystem = LanguageBundle.getString("in_Prefs_langSystem");

	private String[] unitSetNames = null;

	private final JRadioButton langEng;
	private final JRadioButton langFre;
	private final JRadioButton langGer;
	private final JRadioButton langIt;
	private final JRadioButton langEs;
	private final JRadioButton langPt;
	private final JRadioButton langSystem;
	private JComboBoxEx<String> unitSetType = new JComboBoxEx<>();
	private String origLanguage;
	private String origUnitSet;

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
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_language);

		title1.setTitleJustification(TitledBorder.LEADING);
		this.setBorder(title1);
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		int line = 0;

		// Use OS system language
		line = addLanguageOption(line, c, gridbag, this, langSystem = new JRadioButton(in_langSystem), exclusiveGroup);

		final SortedSet<JRadioButton> sorted =
				new TreeSet<>((o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()));

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

		Utility.buildConstraints(c, 0, line++, 3, 1, 0, 0);
		label = new JLabel();
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 0, line++, 3, 1, 0, 0);
		label = new JLabel();
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 0, line, 1, 1, 0, 0);
		final GameMode gameMode = SettingsHandler.getGame();
		label = new JLabel(
			LanguageBundle.getFormattedString(
				"in_Prefs_unitSetType", gameMode.getDisplayName())); //$NON-NLS-1$
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 1, line++, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		Collection<UnitSet> unitSets = SettingsHandler.getGame().getModeContext().getReferenceContext()
			.getConstructedCDOMObjects(UnitSet.class);
		unitSetNames = new String[unitSets.size()];
		int i = 0;
		for (UnitSet unitSet : unitSets)
		{
			if (unitSet != null)
			{
				unitSetNames[i++] = unitSet.getDisplayName();
			}
		}

		unitSetType = new JComboBoxEx<>(unitSetNames);
		gridbag.setConstraints(unitSetType, c);
		this.add(unitSetType);

		Utility.buildConstraints(c, 0, line++, 3, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_restartInfo")); //$NON-NLS-1$
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 5, line, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel();
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private static int addLanguageOption(int line, final GridBagConstraints constraints, final GridBagLayout gridbag,
		final JPanel panel, final JRadioButton button, final ButtonGroup group)
	{
		Utility.buildConstraints(constraints, 0, line++, 2, 1, 0, 0);
		gridbag.setConstraints(button, constraints);
		panel.add(button);
		group.add(button);

		return line;
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#applyOptionValuesToControls()
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

		origLanguage = ConfigurationSettings.getLanguage();
		if (origLanguage == null || origLanguage.equals(""))
		{
			langSystem.setSelected(true);
		}
		else if (origLanguage.equals("en"))
		{
			langEng.setSelected(true);
		}
		else if (origLanguage.equals("fr"))
		{
			langFre.setSelected(true);
		}
		else if (origLanguage.equals("de"))
		{
			langGer.setSelected(true);
		}
		else if (origLanguage.equals("it"))
		{
			langIt.setSelected(true);
		}
		else if (origLanguage.equals("es"))
		{
			langEs.setSelected(true);
		}
		else if (origLanguage.equals("pt"))
		{
			langPt.setSelected(true);
		}
		else
		{
			// Default to system default
			langSystem.setSelected(true);
		}

		origUnitSet = SettingsHandler.getGame() != null && SettingsHandler.getGame().getUnitSet() != null
			? SettingsHandler.getGame().getUnitSet().getDisplayName() : "";
		if (unitSetType.getItemCount() > 0)
		{
			unitSetType.setSelectedIndex(0);
			Collection<UnitSet> unitSets = SettingsHandler.getGame().getModeContext().getReferenceContext()
				.getConstructedCDOMObjects(UnitSet.class);

			for (int i = 0; i < unitSets.size(); ++i)
			{
				if (unitSetNames[i].equals(SettingsHandler.getGame().getUnitSet().getDisplayName()))
				{
					unitSetType.setSelectedIndex(i);
				}
			}
		}
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_language;
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#setOptionsBasedOnControls()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		String[] langCountry = getSelectedLangCountry();
		ConfigurationSettings.setLanguage(langCountry[0]);
		ConfigurationSettings.setCountry(langCountry[1]);

		SettingsHandler.getGame().selectUnitSet((String) unitSetType.getSelectedItem());
	}

	/**
	 * Extract the language and country that have been selected.
	 * @return A String array with two elements, [0] language code and [1] country code.
	 */
	private String[] getSelectedLangCountry()
	{
		String[] langCountry = new String[2];
		if (langEng.isSelected())
		{
			langCountry[0] = "en";
			langCountry[1] = "US";
		}
		else if (langFre.isSelected())
		{
			langCountry[0] = "fr";
			langCountry[1] = "FR";
		}
		else if (langGer.isSelected())
		{
			langCountry[0] = "de";
			langCountry[1] = "DE";
		}
		else if (langIt.isSelected())
		{
			langCountry[0] = "it";
			langCountry[1] = "IT";
		}
		else if (langEs.isSelected())
		{
			langCountry[0] = "es";
			langCountry[1] = "ES";
		}
		else if (langPt.isSelected())
		{
			langCountry[0] = "pt";
			langCountry[1] = "PT";
		}
		else
		{
			langCountry[0] = "";
			langCountry[1] = "";
		}
		return langCountry;
	}

	@Override
	public boolean needsRestart()
	{
		String[] langCountry = getSelectedLangCountry();

		boolean needsRestart = !langCountry[0].equals(origLanguage);

		String unitSet = (String) unitSetType.getSelectedItem();
		if (unitSet == null)
		{
			unitSet = "";
		}

		needsRestart |= !unitSet.equals(origUnitSet);

		return needsRestart;
	}

}
