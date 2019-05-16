/*
 * Copyright 2010(C) James Dempsey
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
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code EquipmentPanel} is responsible for
 * displaying equipment related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public final class EquipmentPanel extends PCGenPrefsPanel
{
	private static final String IN_EQUIPMENT =
			LanguageBundle.getString("in_Prefs_equipment"); //$NON-NLS-1$

	// Used to create the entries for the max spell level combos
	private static final int SPELLLVLMIN = 0;
	private static final int SPELLLVLMAX = 9;

	private static final String IN_POTION_MAX =
			LanguageBundle.getString("in_Prefs_potionMax"); //$NON-NLS-1$
	private static final String IN_WAND_MAX =
			LanguageBundle.getString("in_Prefs_wandMax"); //$NON-NLS-1$

	private final JSpinner potionMaxLevel = new JSpinner();
	private final SpinnerNumberModel potionModel;
	private final JSpinner wandMaxLevel = new JSpinner();
	private final SpinnerNumberModel wandModel;

	/**
	 * Instantiates a new equipment panel.
	 */
	public EquipmentPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_EQUIPMENT);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(IN_POTION_MAX);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);

		potionModel = new SpinnerNumberModel(SPELLLVLMIN, SPELLLVLMIN, SPELLLVLMAX, 1);

		potionMaxLevel.setModel(potionModel);

		gridbag.setConstraints(potionMaxLevel, c);
		this.add(potionMaxLevel);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(IN_WAND_MAX);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);

		wandModel = new SpinnerNumberModel(SPELLLVLMIN, SPELLLVLMIN, SPELLLVLMAX, 1);
		wandMaxLevel.setModel(wandModel);
		gridbag.setConstraints(wandMaxLevel, c);
		this.add(wandMaxLevel);

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label = new JLabel(BLANK_TEXT);
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	@Override
	public String getTitle()
	{
		return IN_EQUIPMENT;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		SettingsHandler.setMaxPotionSpellLevel(potionModel.getNumber().intValue());
		SettingsHandler.setMaxWandSpellLevel(wandModel.getNumber().intValue());
	}

	@Override
	public void applyOptionValuesToControls()
	{
		potionModel.setValue(SettingsHandler.getMaxPotionSpellLevel());
		wandModel.setValue(SettingsHandler.getMaxWandSpellLevel());
	}

}
