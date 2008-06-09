/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  PreferencesMassiveDamagePanel.java
 *
 *  Created on July 10, 2003, 5:03 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.initiative.gui;

import pcgen.core.SettingsHandler;
import plugin.initiative.InitiativePlugin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author soulcatcher
 */
public class PreferencesMassiveDamagePanel extends gmgen.gui.PreferencesPanel
{

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

	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.Type", getType());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.Effect", getEffect());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.SizeMod", sizeCheck.isSelected());
	}

	public void initPreferences()
	{
		setType(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.Type", MASSIVE_OFF));
		setEffect(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.Effect", MASSIVE_EFFECT_KILL));
		setSizeMod(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".Damage.Massive.SizeMod", true));
	}

	@Override
	public String toString()
	{
		return "Massive Damage";
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

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		massivePanel.setLayout(new BoxLayout(massivePanel, BoxLayout.Y_AXIS));

		massivePanel.setBorder(new TitledBorder(null, "Massive Damage",
			TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
			new Font("Dialog", 1, 11)));
		massive1.setSelected(true);
		massive1.setText("Don't Track Massive Damage");
		massiveDamageGroup.add(massive1);
		massive1.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				massiveActionPerformed(evt);
			}
		});

		massivePanel.add(massive1);

		massive2.setText("Roll Fort for more than 50 damage (3rd Ed)");
		massiveDamageGroup.add(massive2);
		massive2.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				massiveActionPerformed(evt);
			}
		});

		massivePanel.add(massive2);

		massive3.setText("Roll Fort for CON Damage (Modern)");
		massiveDamageGroup.add(massive3);
		massive3.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				massiveActionPerformed(evt);
			}
		});

		massivePanel.add(massive3);

		massive4
			.setText("Roll Fort for more than half of total hit points (House)");
		massiveDamageGroup.add(massive4);
		massive4.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				massiveActionPerformed(evt);
			}
		});

		massivePanel.add(massive4);

		mainPanel.add(massivePanel);

		effectPanel.setLayout(new BoxLayout(effectPanel, BoxLayout.Y_AXIS));

		effectPanel.setBorder(new TitledBorder(null, "Massive Damage Failure",
			TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
			new Font("Dialog", 1, 11)));
		effect1.setSelected(true);
		effect1.setText("Failure kills character (3rd Ed)");
		effectGroup.add(effect1);
		effect1.setEnabled(false);
		effectPanel.add(effect1);

		effect2.setText("Failure takes character to -1 hit points (Modern)");
		effectGroup.add(effect2);
		effect2.setEnabled(false);
		effectPanel.add(effect2);

		effect3.setText("Failure does half total hit points (House)");
		effectGroup.add(effect3);
		effect3.setEnabled(false);
		effectPanel.add(effect3);

		effect4.setText("Failure does half current hit points (House)");
		effectGroup.add(effect4);
		effect4.setEnabled(false);
		effectPanel.add(effect4);

		mainPanel.add(effectPanel);

		miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));

		miscPanel.setBorder(new TitledBorder(null, "Misc",
			TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
			new Font("Dialog", 1, 11)));
		sizeCheck.setSelected(true);
		sizeCheck.setText("Take size into account (3rd Ed)");
		sizeCheck.setEnabled(false);
		miscPanel.add(sizeCheck);

		sizeLabel1.setText("(+10 for each size category larger than Medium)");
		sizeLabel1.setEnabled(false);
		miscPanel.add(sizeLabel1);

		sizeLabel2.setText("(-10 for each size category smaller)");
		sizeLabel2.setEnabled(false);
		miscPanel.add(sizeLabel2);

		mainPanel.add(miscPanel);

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