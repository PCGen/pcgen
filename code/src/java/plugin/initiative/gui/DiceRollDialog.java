/*
 * Copyright 2003 (C) Ross M. Lodge
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
package plugin.initiative.gui;

import gmgen.GMGenSystem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import pcgen.core.RollingMethods;
import plugin.initiative.DiceRollModel;

/**
 * <p>
 * This dialog class manages a skill roll.
 * </p>
 *
 */
class DiceRollDialog extends JDialog
{
	/** The skill model for this dialog. */
	DiceRollModel m_model = null;

	/** Button to exit the dialog */
	private JButton m_ok;

	/** Button to roll the skill checks */
	private JButton m_doRoll;

	/** Label to display the result of the check */
	JLabel m_result;

	/** Text field for the skill roll expression */
	private JTextField m_roll;

	/** List of components for field panel */
	private final Collection<Component> m_fields = new ArrayList<>();

	/** List of components for label panel */
	private final Collection<Component> m_labels = new ArrayList<>();

	private JPanel m_buttons;

	private JPanel m_mainPanel;

	private JPanel m_fieldPanel;

	private JPanel m_labelPanel;

	/**
	 * <p>Construct a dialog for the specified roll.</p>
	 * @param model
	 *
	 * @throws java.awt.HeadlessException
	 */
	DiceRollDialog(DiceRollModel model)
	{
		m_model = model;
		initComponents();
	}

	/**
	 * <p>
	 * Exits the dialog.
	 * </p>
	 *
	 */
	private void handleOk()
	{
		setVisible(false);
	}

	/**
	 * <p>
	 * Rolls the skill roll.
	 * </p>
	 *
	 */
	private void handleRoll()
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

	private void sizeAndLocate()
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
	private void initResult(String labelText)
	{
		m_result = new JLabel("<html><body><b>-</b></body></html>");
		m_result.setMinimumSize(new Dimension(100, (int) m_result
			.getMinimumSize().getWidth()));
		m_result.setPreferredSize(new Dimension(100, (int) m_result
			.getPreferredSize().getWidth()));
		JLabel label = new JLabel(labelText);
		label.setAlignmentX(Component.RIGHT_ALIGNMENT);
		addComponent(m_result, label);
	}

	/**
	 *
	 * <p>Builds the main panels for the dialog.  Does NOT initialize the buttons panel.</p>
	 *
	 */
	private void initPanels()
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
	private void addRollField(String labelText)
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
	private void initButtons()
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
		m_doRoll.addActionListener(e -> handleRoll());
		m_ok.addActionListener(e -> handleOk());
	}

	/**
	 *
	 * <p>Adds the specified component and label to the dialog's component lists.</p>
	 * @param field
	 * @param label
	 *
	 */
	void addComponent(Component field, Component label)
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
