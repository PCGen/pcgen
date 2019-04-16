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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code InputPanel} is responsible for
 * displaying input related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class InputPanel extends PCGenPrefsPanel
{
	private static final String IN_INPUT = LanguageBundle.getString("in_Prefs_input");

	private static final String IN_PRINT_DEPRECATION = LanguageBundle.getString("in_Prefs_printDeprecation");
	private static final String IN_PRINT_UNCONSTRUCTED = LanguageBundle.getString("in_Prefs_printUnconstructed");

	private JCheckBox printDeprecationMessages = new JCheckBox();
	private JCheckBox printUnconstructedDetail = new JCheckBox();

	/**
	 * Instantiates a new input panel.
	 */
	public InputPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_INPUT);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		printDeprecationMessages = new JCheckBox(IN_PRINT_DEPRECATION, SettingsHandler.outputDeprecationMessages());
		gridbag.setConstraints(printDeprecationMessages, c);
		this.add(printDeprecationMessages);

		Utility.buildConstraints(c, 0, 1, 3, 1, 0, 0);
		printUnconstructedDetail = new JCheckBox(IN_PRINT_UNCONSTRUCTED, SettingsHandler.inputUnconstructedMessages());
		gridbag.setConstraints(printUnconstructedDetail, c);
		this.add(printUnconstructedDetail);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		JLabel label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	@Override
	public String getTitle()
	{
		return IN_INPUT;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		SettingsHandler.setOutputDeprecationMessages(printDeprecationMessages.isSelected());
		SettingsHandler.setInputUnconstructedMessages(printUnconstructedDetail.isSelected());
	}

	@Override
	public void applyOptionValuesToControls()
	{
		printDeprecationMessages.setSelected(SettingsHandler.outputDeprecationMessages());
		printUnconstructedDetail.setSelected(SettingsHandler.inputUnconstructedMessages());
	}

}
