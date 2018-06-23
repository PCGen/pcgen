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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.WholeNumberField;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code HitPointsPanel} is responsible for
 * displaying hit points related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class HitPointsPanel extends PCGenPrefsPanel
{
	private static final String in_hp = LanguageBundle.getString("in_Prefs_hp");

	private final JCheckBox maxHpAtFirstLevel = new JCheckBox();
	private final JCheckBox maxHpAtFirstClassLevel = new JCheckBox();

	// "HP Roll Methods"
	private final JRadioButton hpAutomax = new JRadioButton(LanguageBundle.getString("in_Prefs_hpAutoMax"));
	private final JRadioButton hpAverage = new JRadioButton(LanguageBundle.getString("in_Prefs_hpAverage"));
	private final JRadioButton hpPercentage = new JRadioButton(LanguageBundle.getString("in_Prefs_hpPercentage"));
	private final JRadioButton hpStandard = new JRadioButton(LanguageBundle.getString("in_Prefs_hpStandard"));
	private final JRadioButton hpUserRolled = new JRadioButton(LanguageBundle.getString("in_Prefs_hpUserRolled"));
	private final JRadioButton hpAverageRoundedUp =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpAverageRoundedUp"));

	private final WholeNumberField hpPct = new WholeNumberField(0, 6);

	/**
	 * Instantiates a new hit points panel.
	 */
	public HitPointsPanel()
	{
		int iRow = 0;

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_hp);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		Utility.buildConstraints(c, 0, iRow, GridBagConstraints.REMAINDER, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_hpGenLabel")); //$NON-NLS-1$
		gridbag.setConstraints(label, c);
		this.add(label);

		//
		// Insert a blank label to indent the HP rolling choices
		//
		Utility.buildConstraints(c, 0, iRow++, 1, 1, 0, 0);
		label = new JLabel(BLANK_TEXT);
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 1, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		gridbag.setConstraints(hpUserRolled, c);
		this.add(hpUserRolled);
		exclusiveGroup.add(hpUserRolled);

		Utility.buildConstraints(c, 1, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		gridbag.setConstraints(hpStandard, c);
		this.add(hpStandard);
		exclusiveGroup.add(hpStandard);

		Utility.buildConstraints(c, 1, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		gridbag.setConstraints(hpAverage, c);
		this.add(hpAverage);
		exclusiveGroup.add(hpAverage);

		Utility.buildConstraints(c, 1, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		gridbag.setConstraints(hpAutomax, c);
		this.add(hpAutomax);
		exclusiveGroup.add(hpAutomax);

		Utility.buildConstraints(c, 1, iRow, 1, 1, 0, 0);
		gridbag.setConstraints(hpPercentage, c);
		this.add(hpPercentage);
		exclusiveGroup.add(hpPercentage);

		Utility.buildConstraints(c, 2, iRow++, 1, 1, 0, 0);
		gridbag.setConstraints(hpPct, c);
		this.add(hpPct);

		Utility.buildConstraints(c, 1, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		gridbag.setConstraints(hpAverageRoundedUp, c);
		this.add(hpAverageRoundedUp);
		exclusiveGroup.add(hpAverageRoundedUp);

		Utility.buildConstraints(c, 0, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		maxHpAtFirstLevel.setText(LanguageBundle.getString("in_Prefs_hpMaxAtFirst")); //$NON-NLS-1$
		this.add(maxHpAtFirstLevel, c);

		Utility.buildConstraints(c, 1, iRow++, GridBagConstraints.REMAINDER, 1, 0, 0);
		maxHpAtFirstClassLevel.setText(LanguageBundle.getString("in_Prefs_hpMaxAtFirstClass")); //$NON-NLS-1$
		this.add(maxHpAtFirstClassLevel, c);

		Utility.buildConstraints(c, 0, iRow, 4, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel();
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_hp;
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#setOptionsBasedOnControls()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		if (hpStandard.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_STANDARD);
		}
		else if (hpAutomax.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AUTO_MAX);
		}
		else if (hpAverage.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AVERAGE);
		}
		else if (hpPercentage.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_PERCENTAGE);
		}
		else if (hpUserRolled.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_USER_ROLLED);
		}
		else if (hpAverageRoundedUp.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AVERAGE_ROUNDED_UP);
		}

		SettingsHandler.setHPPercent(hpPct.getValue());
		SettingsHandler.setHPMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());
		SettingsHandler.setHPMaxAtFirstClassLevel(maxHpAtFirstClassLevel.isSelected());
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#applyOptionValuesToControls()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_AUTO_MAX:
				hpAutomax.setSelected(true);

				break;

			case Constants.HP_AVERAGE:
				hpAverage.setSelected(true);

				break;

			case Constants.HP_PERCENTAGE:
				hpPercentage.setSelected(true);

				break;

			case Constants.HP_USER_ROLLED:
				hpUserRolled.setSelected(true);

				break;

			case Constants.HP_AVERAGE_ROUNDED_UP:
				hpAverageRoundedUp.setSelected(true);

				break;

			case Constants.HP_STANDARD:
				//No break
			default:
				hpStandard.setSelected(true);

				break;
		}

		hpPct.setValue(SettingsHandler.getHPPercent());
		maxHpAtFirstLevel.setSelected(SettingsHandler.isHPMaxAtFirstLevel());
		maxHpAtFirstClassLevel.setSelected(SettingsHandler.isHPMaxAtFirstClassLevel());
	}

}
