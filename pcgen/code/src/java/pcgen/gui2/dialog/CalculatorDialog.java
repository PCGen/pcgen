/*
 * Copyright 2011 Stefan Radermacher <zaister@users.sourceforge.net>
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
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import pcgen.core.VariableProcessor;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * A dialog to allow character variables and expressions to be evaluated 
 * interactively by the user.
 * 
 */
public class CalculatorDialog extends JDialog
{
	private final PCGenFrame pcgenFrame;
	private final FormulaPanel formulaPanel;
	private final JTextArea outputText;

	public CalculatorDialog(PCGenFrame parent)
	{
		super(parent);
		this.pcgenFrame = parent;
		setTitle(LanguageBundle.getString("in_mnuToolsCalculator"));
		outputText = new JTextArea();
		formulaPanel = new FormulaPanel(outputText);
		initComponents();
		pack();
		setSize(700, 500);
		
		Utility.installEscapeCloseOperation(this);
	}

	private void initComponents()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		outputText.setEditable(false);
		contentPane.add(formulaPanel, BorderLayout.NORTH);
		contentPane.add(outputText, BorderLayout.CENTER);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private class ButtonPanel extends JPanel implements ActionListener
	{
		private final JButton calcButton;
		private final JButton clearButton;
		private final JTextField formulaText;
		private final JTextArea outputText;
		
		public ButtonPanel(JTextField formulaText, JTextArea outputText)
		{
			calcButton = new JButton(LanguageBundle.getString("in_calculate"));
			clearButton = new JButton(LanguageBundle.getString("in_clear"));
			this.formulaText = formulaText;
			this.outputText = outputText;
			initComponents();
		}
		
		private void initComponents()
		{
			setLayout(new BorderLayout());
			
			calcButton.setActionCommand("CALCULATE");
			calcButton.addActionListener(this);
			clearButton.setActionCommand("CLEAR");
			clearButton.addActionListener(this);
			
			add(calcButton, BorderLayout.WEST);
			add(clearButton, BorderLayout.EAST);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if ("CALCULATE".equals(e.getActionCommand()))
			{
				String formula = formulaText.getText();
				CharacterFacade currentPC = pcgenFrame.getSelectedCharacterRef().get();

				if (currentPC != null)
				{
					VariableProcessor vp = currentPC.getVariableProcessor();
					vp.pauseCache();
					outputText.append(currentPC.getNameRef() + ": " + formula + " = "
						+ currentPC.getVariable(formula, true) + "\n");
					vp.restartCache();
				}
				else
				{
					outputText.append("No character currently selected.\n");
				}
				formulaText.requestFocus();

			}
			else if  ("CLEAR".equals(e.getActionCommand()))
			{
				outputText.setText("");
			}
		}
	}
	
	private class FormulaPanel extends JPanel
	{
		private final JTextField formulaText;
		private final ButtonPanel buttonPanel;
		
		public FormulaPanel(JTextArea outputText)
		{
			formulaText = new JTextField();
			buttonPanel = new ButtonPanel(formulaText, outputText);
			initComponents();

		}
		
		private void initComponents()
		{
			setLayout(new BorderLayout());
			
			add(formulaText, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.EAST);
		}

	}
}
