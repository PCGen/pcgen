/*
 *  Copyright (C) 2002 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * PreferencesExperiencePanel.java
 *
 */
package plugin.experience.gui;

import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;
import plugin.experience.ExperienceAdjusterPlugin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;

/**
 *
 * @author soulcatcher
 */
public class PreferencesExperiencePanel extends gmgen.gui.PreferencesPanel
{
	public static final int EXPERIENCE_3 = 1;
	public static final int EXPERIENCE_35 = 2;

	private static final String OPTION_NAME_EXP_TYPE = ExperienceAdjusterPlugin.LOG_NAME + ".ExperienceType"; //$NON-NLS-1$

	private JPanel mainPanel;
	private JPanel expPanel;
	private ButtonGroup experienceGroup;
	private JRadioButton experienceRB1;
	private JRadioButton experienceRB2;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesExperiencePanel()
	{
		initComponents();
		initPreferences();
	}

	public void setExperience(int exp)
	{
		if (exp == EXPERIENCE_3)
		{
			experienceRB1.setSelected(true);
		}
		else if (exp == EXPERIENCE_35)
		{
			experienceRB2.setSelected(true);
		}
	}

	public int getExperience()
	{
		int returnVal = 0;

		if (experienceRB1.isSelected())
		{
			returnVal = EXPERIENCE_3;
		}
		else if (experienceRB2.isSelected())
		{
			returnVal = EXPERIENCE_35;
		}

		return returnVal;
	}

    @Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME_EXP_TYPE, getExperience());
	}

    @Override
	public void initPreferences()
	{
		setExperience(SettingsHandler.getGMGenOption(
			OPTION_NAME_EXP_TYPE,
			EXPERIENCE_35));
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_xp"); //$NON-NLS-1$
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents
		mainPanel = new JPanel();

		expPanel = new JPanel();
		experienceGroup = new ButtonGroup();
		experienceRB1 = new JRadioButton();
		experienceRB2 = new JRadioButton();

		setLayout(new BorderLayout());

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		expPanel.setLayout(new BoxLayout(expPanel, BoxLayout.Y_AXIS));

		expPanel.setBorder(new TitledBorder(null,
			LanguageBundle.getString("in_plugin_xp_calc"), TitledBorder.DEFAULT_JUSTIFICATION, //$NON-NLS-1$
			TitledBorder.DEFAULT_POSITION));
		experienceRB1.setSelected(true);
		experienceRB1
			.setText(LanguageBundle.getString("in_plugin_xp_byParty")); //$NON-NLS-1$
		experienceGroup.add(experienceRB1);
		expPanel.add(experienceRB1);

		experienceRB2
			.setText(LanguageBundle.getString("in_plugin_xp_byPC")); //$NON-NLS-1$
		experienceGroup.add(experienceRB2);
		expPanel.add(experienceRB2);

		mainPanel.add(expPanel);

		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(mainPanel);
		add(jScrollPane1, BorderLayout.CENTER);
	}
	//GEN-END:initComponents
}
