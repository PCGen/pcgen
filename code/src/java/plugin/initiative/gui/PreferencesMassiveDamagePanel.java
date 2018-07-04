/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
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
 *  PreferencesMassiveDamagePanel.java
 */
package plugin.initiative.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;
import plugin.initiative.InitiativePlugin;

public class PreferencesMassiveDamagePanel extends gmgen.gui.PreferencesPanel
{

	private static final String OPTION_NAME_TYPE = InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.Type"; //$NON-NLS-1$
	private static final String OPTION_NAME_EFFECT = InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.Effect"; //$NON-NLS-1$
	private static final String OPTION_NAME_USESIZE = InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.SizeMod"; //$NON-NLS-1$

	public static final int MASSIVE_OFF = 1;
	public static final int MASSIVE_DND = 2;
	public static final int MASSIVE_D20_MODERN = 3;
	public static final int MASSIVE_HOUSE_HALF = 4;
	public static final int MASSIVE_EFFECT_KILL = 1;
	public static final int MASSIVE_EFFECT_NEGATIVE = 2;
	public static final int MASSIVE_EFFECT_HALF_TOTAL = 3;
	public static final int MASSIVE_EFFECT_HALF_CURRENT = 4;

	private JPanel mainPanel;

	private JPanel massivePanel;
	private ButtonGroup massiveDamageGroup;
	private JRadioButton massive1;
	private JRadioButton massive2;
	private JRadioButton massive3;
	private JRadioButton massive4;

	private JPanel effectPanel;
	private ButtonGroup effectGroup;
	private JRadioButton effect1;
	private JRadioButton effect2;
	private JRadioButton effect3;
	private JRadioButton effect4;

	private JPanel miscPanel;
	private JCheckBox sizeCheck;
	private JLabel sizeLabel1;
	private JLabel sizeLabel2;

	public PreferencesMassiveDamagePanel()
	{
		initComponents();
		initPreferences();
	}

	@Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME_TYPE, getType());
		SettingsHandler.setGMGenOption(OPTION_NAME_EFFECT, getEffect());
		SettingsHandler.setGMGenOption(OPTION_NAME_USESIZE, sizeCheck.isSelected());
	}

	@Override
	public void initPreferences()
	{
		setType(SettingsHandler.getGMGenOption(OPTION_NAME_TYPE, MASSIVE_OFF));
		setEffect(SettingsHandler.getGMGenOption(OPTION_NAME_EFFECT, MASSIVE_EFFECT_KILL));
		setSizeMod(SettingsHandler.getGMGenOption(OPTION_NAME_USESIZE, true));
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_init_massive_massive"); //$NON-NLS-1$
	}

	private void setEffect(int choice)
	{
		if (choice == MASSIVE_EFFECT_KILL)
		{
			effect1.setSelected(true);
		}
		else if (choice == MASSIVE_EFFECT_NEGATIVE)
		{
			effect2.setSelected(true);
		}
		else if (choice == MASSIVE_EFFECT_HALF_TOTAL)
		{
			effect3.setSelected(true);
		}
		else if (choice == MASSIVE_EFFECT_HALF_CURRENT)
		{
			effect4.setSelected(true);
		}
	}

	private int getEffect()
	{
		int returnVal = 0;

		if (effect1.isSelected())
		{
			returnVal = MASSIVE_EFFECT_KILL;
		}
		else if (effect2.isSelected())
		{
			returnVal = MASSIVE_EFFECT_NEGATIVE;
		}
		else if (effect3.isSelected())
		{
			returnVal = MASSIVE_EFFECT_HALF_TOTAL;
		}
		else if (effect4.isSelected())
		{
			returnVal = MASSIVE_EFFECT_HALF_CURRENT;
		}

		return returnVal;
	}

	private void setSizeMod(boolean selected)
	{
		sizeCheck.setSelected(selected);
	}

	private void setType(int choice)
	{
		if (choice == MASSIVE_OFF)
		{
			massive1.setSelected(true);
		}
		else if (choice == MASSIVE_DND)
		{
			massive2.setSelected(true);
		}
		else if (choice == MASSIVE_D20_MODERN)
		{
			massive3.setSelected(true);
		}
		else if (choice == MASSIVE_HOUSE_HALF)
		{
			massive4.setSelected(true);
		}
	}

	private int getType()
	{
		int returnVal = 0;

		if (massive1.isSelected())
		{
			returnVal = MASSIVE_OFF;
		}
		else if (massive2.isSelected())
		{
			returnVal = MASSIVE_DND;
		}
		else if (massive3.isSelected())
		{
			returnVal = MASSIVE_D20_MODERN;
		}
		else if (massive4.isSelected())
		{
			returnVal = MASSIVE_HOUSE_HALF;
		}

		return returnVal;
	}

	private void initComponents()
	{
		mainPanel = new JPanel();

		massiveDamageGroup = new ButtonGroup();
		massivePanel = new JPanel();
		massive1 = new JRadioButton();
		massive2 = new JRadioButton();
		massive3 = new JRadioButton();
		massive4 = new JRadioButton();

		effectPanel = new JPanel();
		effectGroup = new ButtonGroup();
		effect1 = new JRadioButton();
		effect2 = new JRadioButton();
		effect3 = new JRadioButton();
		effect4 = new JRadioButton();

		miscPanel = new JPanel();
		sizeCheck = new JCheckBox();
		sizeLabel1 = new JLabel();
		sizeLabel2 = new JLabel();

		setLayout(new BorderLayout());

		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;

		massivePanel.setLayout(new BoxLayout(massivePanel, BoxLayout.Y_AXIS));

		massivePanel.setBorder(
			new TitledBorder(null, LanguageBundle.getString("in_plugin_init_massive_massive"), //$NON-NLS-1$
			TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));
		massive1.setSelected(true);
		massive1.setText(LanguageBundle.getString("in_plugin_init_massive_noTrack")); //$NON-NLS-1$
		massiveDamageGroup.add(massive1);
		massive1.addActionListener(this::massiveActionPerformed);

		massivePanel.add(massive1);

		massive2.setText(LanguageBundle.getString("in_plugin_init_massive_50damage")); //$NON-NLS-1$
		massiveDamageGroup.add(massive2);
		massive2.addActionListener(this::massiveActionPerformed);

		massivePanel.add(massive2);

		massive3.setText(LanguageBundle.getString("in_plugin_init_massive_ConDamage")); //$NON-NLS-1$
		massiveDamageGroup.add(massive3);
		massive3.addActionListener(this::massiveActionPerformed);

		massivePanel.add(massive3);

		massive4.setText(LanguageBundle.getString("in_plugin_init_massive_Half")); //$NON-NLS-1$
		massiveDamageGroup.add(massive4);
		massive4.addActionListener(this::massiveActionPerformed);

		massivePanel.add(massive4);

		mainPanel.add(massivePanel, c);

		effectPanel.setLayout(new BoxLayout(effectPanel, BoxLayout.Y_AXIS));

		effectPanel.setBorder(
			new TitledBorder(null, LanguageBundle.getString("in_plugin_init_massive_failure"), //$NON-NLS-1$
			TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));
		effect1.setSelected(true);
		effect1.setText(LanguageBundle.getString("in_plugin_init_massive_kill")); //$NON-NLS-1$
		effectGroup.add(effect1);
		effect1.setEnabled(false);
		effectPanel.add(effect1);

		effect2.setText(LanguageBundle.getString("in_plugin_init_massive_minusOne")); //$NON-NLS-1$
		effectGroup.add(effect2);
		effect2.setEnabled(false);
		effectPanel.add(effect2);

		effect3.setText(LanguageBundle.getString("in_plugin_init_massive_halfTotal")); //$NON-NLS-1$
		effectGroup.add(effect3);
		effect3.setEnabled(false);
		effectPanel.add(effect3);

		effect4.setText(LanguageBundle.getString("in_plugin_init_massive_halfCurrent")); //$NON-NLS-1$
		effectGroup.add(effect4);
		effect4.setEnabled(false);
		effectPanel.add(effect4);

		mainPanel.add(effectPanel, c);

		miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));

		miscPanel.setBorder(new TitledBorder(null, LanguageBundle.getString("in_plugin_init_misc"), //$NON-NLS-1$
			TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));
		sizeCheck.setSelected(true);
		sizeCheck.setText(LanguageBundle.getString("in_plugin_init_massive_size")); //$NON-NLS-1$
		sizeCheck.setEnabled(false);
		miscPanel.add(sizeCheck);

		sizeLabel1.setText(LanguageBundle.getString("in_plugin_init_massive_sizeL")); //$NON-NLS-1$
		sizeLabel1.setEnabled(false);
		miscPanel.add(sizeLabel1);

		sizeLabel2.setText(LanguageBundle.getString("in_plugin_init_massive_sizeS")); //$NON-NLS-1$
		sizeLabel2.setEnabled(false);
		miscPanel.add(sizeLabel2);

		mainPanel.add(miscPanel, c);

		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(mainPanel);
		add(jScrollPane1, BorderLayout.CENTER);
	}

	private void massiveActionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == massive1)
		{
			effect1.setEnabled(false);
			effect2.setEnabled(false);
			effect3.setEnabled(false);
			effect4.setEnabled(false);
			sizeCheck.setEnabled(false);
			sizeLabel1.setEnabled(false);
			sizeLabel2.setEnabled(false);
		}
		else if (evt.getSource() == massive2)
		{
			effect1.setEnabled(true);
			effect2.setEnabled(true);
			effect3.setEnabled(true);
			effect4.setEnabled(true);
			sizeCheck.setEnabled(true);
			sizeLabel1.setEnabled(true);
			sizeLabel2.setEnabled(true);
		}
		else if ((evt.getSource() == massive3) || (evt.getSource() == massive4))
		{
			effect1.setEnabled(true);
			effect2.setEnabled(true);
			effect3.setEnabled(true);
			effect4.setEnabled(true);
			sizeCheck.setEnabled(false);
			sizeLabel1.setEnabled(false);
			sizeLabel2.setEnabled(false);
		}
	}
}
