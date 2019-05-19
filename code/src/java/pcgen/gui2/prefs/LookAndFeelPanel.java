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

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.gui2.plaf.LookAndFeelManager;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code LookAndFeelPanel} is responsible for
 * displaying look and feel related preferences and allowing the 
 * preferences to be edited by the user.
 */
public class LookAndFeelPanel extends PCGenPrefsPanel
{
	private static final String IN_LOOK_AND_FEEL = LanguageBundle.getString("in_Prefs_lookAndFeel");

	private final JRadioButton[] laf;
	private String oldLAF;
	private String oldThemePack;

	/**
	 * Instantiates a new look and feel panel.
	 */
	public LookAndFeelPanel()
	{

		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_LOOK_AND_FEEL);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		Action[] actions = LookAndFeelManager.getActions();
		laf = new JRadioButton[actions.length - 1];

		for (int i = 0; i < laf.length; ++i)
		{
			laf[i] = new JRadioButton(actions[i]);

			int whichChar = (laf[i].getText().charAt(0) == 'C') ? 1 : 0;
			laf[i].setMnemonic(laf[i].getText().charAt(whichChar));

			Utility.buildConstraints(c, 0, i, 3, 1, 0, 0);
			gridbag.setConstraints(laf[i], c);
			this.add(laf[i]);
			exclusiveGroup.add(laf[i]);
		}

		Utility.buildConstraints(c, 0, laf.length + 1, 5, 1, 0, 0);
		label = new JLabel("");
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 0, laf.length + 2, 5, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_restartInfo"));
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 0, 20, 5, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	@Override
	public String getTitle()
	{
		return IN_LOOK_AND_FEEL;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		//NB: options are already set using the actions defined in the PCGenUIManager
	}

	@Override
	public void resetOptionValues()
	{
		LookAndFeelManager.setSelectedThemePack(oldThemePack);
		LookAndFeelManager.setLookAndFeel(oldLAF);
	}

	@Override
	public boolean needsRestart()
	{
		boolean needsRestart = false;
		needsRestart |= (oldLAF != LookAndFeelManager.getCurrentLAF());
		needsRestart |= (oldThemePack != LookAndFeelManager.getCurrentThemePack());

		return needsRestart;
	}

	@Override
	public void applyOptionValuesToControls()
	{
		oldLAF = LookAndFeelManager.getCurrentLAF();
		oldThemePack = LookAndFeelManager.getCurrentThemePack();
		for (int i = 0; i < laf.length; i++)
		{
			laf[i].setSelected(oldLAF.equals(laf[i].getText()));
		}
	}

}
