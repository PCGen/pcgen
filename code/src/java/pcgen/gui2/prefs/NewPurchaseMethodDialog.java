/*
 * NewPurchaseMethodDialog.java
 *
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 *
 */
package pcgen.gui2.prefs;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.tools.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 */
class NewPurchaseMethodDialog extends JDialog
{
	static final long serialVersionUID = -5321303573914291162L;
	private JButton cancelButton;
	private JButton okButton;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JPanel buttonPanel;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JTextField nameEdit;
	private JTextField pointsEdit;
	private boolean wasCancelled = true;

	/** Creates new form JDialog
	 * @param parent
	 * @param modal
	 */
	public NewPurchaseMethodDialog(JDialog parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		Utility.setComponentRelativeLocation(parent, this);
	}

	/** Creates new form JDialog
	 * @param parent
	 * @param modal
	 */
	private NewPurchaseMethodDialog(Frame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		Utility.setComponentRelativeLocation(parent, this);
	}

	public String getEnteredName()
	{
		return nameEdit.getText().trim();
	}

	public int getEnteredPoints()
	{
		try
		{
			final int points = Integer.parseInt(pointsEdit.getText());

			return points;
		}
		catch (Exception exc)
		{
			//TODO Really ignore?
		}

		return -1;
	}

	public boolean getWasCancelled()
	{
		return wasCancelled;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		new NewPurchaseMethodDialog(new JFrame(), true).setVisible(true);
	}

	private void cancelButtonActionPerformed()
	{
		wasCancelled = true;
		setVisible(false);
		this.dispose();
	}

	/** Closes the dialog */
	private void closeDialog()
	{
		setVisible(false);
		dispose();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		jPanel1 = new JPanel();
		jLabel1 = new JLabel();
		nameEdit = new JTextField();
		jPanel2 = new JPanel();
		jLabel2 = new JLabel();
		pointsEdit = new JTextField();
		buttonPanel = new JPanel();
		cancelButton = new JButton();
		okButton = new JButton();

		getContentPane().setLayout(new GridBagLayout());

		setTitle("Enter name and points for Purchase Method");
		addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent evt)
				{
					closeDialog();
				}
			});

		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel1.setText("Name:");
		jLabel1.setPreferredSize(new Dimension(140, 15));
		jPanel1.add(jLabel1);

		nameEdit.setPreferredSize(new Dimension(140, 20));
		jPanel1.add(nameEdit);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel1, gridBagConstraints);

		jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));

		jLabel2.setText("Points:");
		jLabel2.setPreferredSize(new Dimension(140, 15));
		jPanel2.add(jLabel2);

		pointsEdit.setPreferredSize(new Dimension(30, 20));
		jPanel2.add(pointsEdit);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel2, gridBagConstraints);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		cancelButton.setMnemonic('C');
		cancelButton.setText("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(evt -> cancelButtonActionPerformed());

		okButton.setMnemonic('O');
		okButton.setText("OK");
		buttonPanel.add(okButton);
		okButton.addActionListener(evt -> okButtonActionPerformed());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(buttonPanel, gridBagConstraints);

		pack();
	}

	private void okButtonActionPerformed()
	{
		if (getEnteredName().isEmpty())
		{
			ShowMessageDelegate.showMessageDialog(
				"Please enter a name for this method.",
				Constants.APPLICATION_NAME, MessageType.ERROR);

			return;
		}

		if (getEnteredPoints() <= 0)
		{
			ShowMessageDelegate.showMessageDialog(
				"Invalid points value. Please try again.",
				Constants.APPLICATION_NAME, MessageType.ERROR);

			return;
		}

		wasCancelled = false;
		setVisible(false);
		this.dispose();
	}
}
