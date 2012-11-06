/*
 *  pcgen
 *  Copyright (C) 2003 Ross M. Lodge
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
 *  SkillDialog.java
 *
 *  Created on Nov 5, 2003, 3:37:59 PM
 */
package plugin.initiative.gui;

import gmgen.GMGenSystem;
import plugin.initiative.DiceRollModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import pcgen.core.RollingMethods;

/**
 * <p>
 * This dialog class manages a skill roll.
 * </p>
 *
 * @author Ross M. Lodge
 */
public class DiceRollDialog extends JDialog
{
	/** The skill model for this dialog. */
	DiceRollModel m_model = null;

	/** Button to exit the dialog */
	protected JButton m_ok;

	/** Button to roll the skill checks */
	protected JButton m_doRoll;

	/** Label to display the result of the check */
	protected JLabel m_result;

	/** Text field for the skill roll expression */
	protected JTextField m_roll;

	/** List of components for field panel */
	protected List<Component> m_fields = new ArrayList<Component>();

	/** List of components for label panel */
	protected List<Component> m_labels = new ArrayList<Component>();

	protected JPanel m_buttons;

	protected JPanel m_mainPanel;

	protected JPanel m_fieldPanel;

	protected JPanel m_labelPanel;

	/**
	 * <p>Construct a dialog for the specified roll.</p>
	 * @param model
	 *
	 * @throws java.awt.HeadlessException
	 */
	public DiceRollDialog(DiceRollModel model) throws HeadlessException
	{
		super();
		m_model = model;
		initComponents();
	}

	/**
	 * <p>
	 * Exits the dialog.
	 * </p>
	 *
	 * @param e Event which fired this handler
	 */
	protected void handleOk(ActionEvent e)
	{
		setVisible(false);
	}

	/**
	 * <p>
	 * Rolls the skill roll.
	 * </p>
	 *
	 * @param e Event which fired this handler
	 */
	protected void handleRoll(ActionEvent e)
	{
		setResult(RollingMethods.roll(m_roll.getText()));
	}

	/**
	 * <p>
	 * Initializes the dialog components, sizes and positions the dialog.
	 * </p>
	 */
	protected void initComponents()
	{
		/*
		 * Dialog will consist of
		 *  Roll:       [                 ]
		 *  Result:     [                 ]
		 *                  [ Roll ] [ Ok ]
		 */

		//Set basic properties
		setTitle(m_model.toString());
		addRollField("Roll:");
		initResult("Result:");
		initPanels();
		initButtons();
		initListeners();

		sizeAndLocate();
	}

	protected void sizeAndLocate()
	{
		//Size and position the dialog
		pack();
		setLocationRelativeTo(GMGenSystem.inst);
	}

	/**
	 * <p>Initializes the result field</p>
	 *
	 * @param labelText Text for label
	 */
	protected void initResult(String labelText)
	{
		m_result = new JLabel("<html><body><b>-</b></body></html>");
		m_result.setMinimumSize(new Dimension(100, (int) m_result
			.getMinimumSize().getWidth()));
		m_result.setPreferredSize(new Dimension(100, (int) m_result
			.getPreferredSize().getWidth()));
		JLabel label = null;
		label = new JLabel(labelText);
		label.setAlignmentX(Component.RIGHT_ALIGNMENT);
		addComponent(m_result, label);
	}

	/**
	 *
	 * <p>Builds the main panels for the dialog.  Does NOT initialize the buttons panel.</p>
	 *
	 */
	protected void initPanels()
	{
		//Construct the panels
		m_mainPanel = new JPanel(new BorderLayout(5, 5));
		m_labelPanel = new JPanel(new GridLayout(0, 1));
		m_fieldPanel = new JPanel(new GridLayout(0, 1));

		//Add the components
		for (Component label : m_labels)
		{
			m_labelPanel.add(label);
		}
		for (Component field : m_fields)
		{
			m_fieldPanel.add(field);
		}

		//Add the panels to the content pane
		m_mainPanel.add(m_labelPanel, BorderLayout.CENTER);
		m_mainPanel.add(m_fieldPanel, BorderLayout.EAST);
		getContentPane().add(m_mainPanel, BorderLayout.CENTER);
	}

	/**
	 * <p>
	 * Initializes the roll expression field.  Creates the roll label
	 * </p>
	 *
	 * @param labelText Label text
	 */
	protected void addRollField(String labelText)
	{
		m_roll = new JTextField(m_model.getExpression());
		JLabel label = new JLabel(labelText);
		label.setAlignmentX(Component.RIGHT_ALIGNMENT);
		addComponent(m_roll, label);
	}

	/**
	 *
	 * <p>Initializes the buttons and their panel</p>
	 *
	 */
	protected void initButtons()
	{
		m_buttons = new JPanel();
		m_buttons.setLayout(new BoxLayout(m_buttons, BoxLayout.X_AXIS));
		m_buttons.add(Box.createHorizontalGlue());
		m_doRoll = new JButton("Roll");
		m_buttons.add(m_doRoll);
		m_buttons.add(m_doRoll);
		m_buttons.add(Box.createHorizontalStrut(10));
		m_ok = new JButton("Ok");
		m_buttons.add(m_ok);
		getContentPane().add(m_buttons, BorderLayout.SOUTH);
	}

	/**
	 *
	 * <p>Initializes the button listeners</p>
	 *
	 */
	protected void initListeners()
	{
		//Initialize listeners
		m_doRoll.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent e)
			{
				handleRoll(e);
			}
		});
		m_ok.addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent e)
			{
				handleOk(e);
			}
		});
	}

	/**
	 *
	 * <p>Adds the specified component and label to the dialog's component lists.</p>
	 * @param field
	 * @param label
	 *
	 */
	protected void addComponent(Component field, Component label)
	{
		m_fields.add(field);
		m_labels.add(label);
	}

	/**
	 * <p>Sets the result string</p>
	 * @param result
	 */
	protected void setResult(int result)
	{
		m_result.setText("<html><body><b>" + result + "</b></body></html>");
	}

}
