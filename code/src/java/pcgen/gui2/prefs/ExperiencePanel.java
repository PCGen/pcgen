/*
 * ExperiencePanel.java
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
 * Created on 20/07/2008 14:21:40
 *
 * $Id: $
 */
package pcgen.gui2.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>ExperiencePanel</code> is responsible for 
 * displaying experience related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
@SuppressWarnings("serial")
public class ExperiencePanel extends PCGenPrefsPanel
{
	private static String in_experience =
		LanguageBundle.getString("in_Prefs_experience");
	private JComboBoxEx xpTableCombo = new JComboBoxEx();

	/**
	 * Instantiates a new monster panel.
	 */
	public ExperiencePanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_experience);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_xpTable")
					+ ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(xpTableCombo, c);
		this.add(xpTableCombo);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_experience;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();
		gameMode.setXpTableName(String.valueOf(xpTableCombo.getSelectedItem()));
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();
		final String xpTableName = gameMode.getXpTableName();

		List<String> xpTableNames = gameMode.getAvailXpTableNames();
		xpTableCombo.removeAllItems();
		
		for (String name : xpTableNames)
		{
			xpTableCombo.addItem(name);
		}
		xpTableCombo.setSelectedItem(xpTableName);
	}

}
