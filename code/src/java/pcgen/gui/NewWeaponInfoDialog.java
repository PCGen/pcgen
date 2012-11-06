/*
 * NewWeaponInfoDialog.java
 * @(#) $Id$
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
 * Created on May 26, 2002, 10:02 PM
 *
 */
package pcgen.gui;

import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author     ???
 * @version $Revision$
 */
final class NewWeaponInfoDialog extends JDialog
{
	static final long serialVersionUID = -1643480023547194976L;
	private JButton btnCancel;
	private JButton btnOK;
	private JComboBoxEx cmbCritMult;
	private JComboBoxEx cmbCritRange;
	private JComboBoxEx cmbDamageDieCount;
	private JComboBoxEx cmbDamageDieSides;
	private JLabel lblCritMult;
	private JLabel lblCritRange;
	private JLabel lblDamage;
	private JLabel lbld;
	private boolean wasCancelled = true;

	/** Creates new form NewWeaponInfoDialog
	 * @param parent
	 */
	public NewWeaponInfoDialog(JFrame parent)
	{
		super(parent);

		IconUtilitities.maybeSetIcon(parent,
			IconUtilitities.RESOURCE_APP_ICON);
		initComponents();
		setLocationRelativeTo(parent); // centre on parent (Canadian spelling eh?)
	}

	public String getCritMultiplier()
	{
		if (cmbCritMult.getSelectedIndex() >= 0)
		{
			return (cmbCritMult.getItemAt(cmbCritMult.getSelectedIndex())).toString();
		}

		return "";
	}

	public String getCritRange()
	{
		if (cmbCritRange.getSelectedIndex() >= 0)
		{
			return (cmbCritRange.getItemAt(cmbCritRange.getSelectedIndex())).toString();
		}

		return "";
	}

	public String getDamage()
	{
		String damage = "";

		if ((cmbDamageDieCount.getSelectedIndex() >= 0) && (cmbDamageDieSides.getSelectedIndex() >= 0))
		{
			damage = (cmbDamageDieCount.getItemAt(cmbDamageDieCount.getSelectedIndex())).toString() + "d"
				+ (cmbDamageDieSides.getItemAt(cmbDamageDieSides.getSelectedIndex())).toString();
		}

		return damage;
	}

	public boolean getWasCancelled()
	{
		return wasCancelled;
	}

	private void btnOKActionPerformed()
	{
		wasCancelled = false;
		setVisible(false);
		dispose();
	}

	/** Closes the dialog */
	private void closeDialog()
	{
		wasCancelled = true;
		setVisible(false);
		dispose();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		btnCancel = new JButton();
		btnOK = new JButton();
		cmbDamageDieCount = new JComboBoxEx();
		cmbDamageDieSides = new JComboBoxEx();
		cmbCritRange = new JComboBoxEx();
		cmbCritMult = new JComboBoxEx();
		lblDamage = new JLabel();
		lblCritRange = new JLabel();
		lblCritMult = new JLabel();
		lbld = new JLabel();

/*        jLabel1 = new JLabel();
   getContentPane().setLayout(new AbsoluteLayout());
   setTitle("Base Weapon Info");
   setModal(true);
   setResizable(false);
   addWindowListener(new WindowAdapter()
   {
       public void windowClosing(WindowEvent evt)
       {
           closeDialog(evt);
       }
   });
   btnCancel.setText("Cancel");
   btnCancel.addActionListener(new ActionListener()
   {
       public void actionPerformed(ActionEvent evt)
       {
           closeDialog(null);
       }
   });
   btnCancel.addMouseListener(new MouseAdapter()
   {
       public void mouseClicked(MouseEvent evt)
       {
           closeDialog(null);
       }
   });
   getContentPane().add(btnCancel, new AbsoluteConstraints(50, 130, 80, -1));
   btnOK.setText("OK");
   btnOK.addActionListener(new ActionListener()
   {
       public void actionPerformed(ActionEvent evt)
       {
           btnOKActionPerformed();
       }
   });
   btnOK.addMouseListener(new MouseAdapter()
   {
       public void mouseClicked(MouseEvent evt)
       {
           btnOKActionPerformed();
       }
   });
   getContentPane().add(btnOK, new AbsoluteConstraints(150, 130, 80, -1));
   lblDamage.setText("Damage");
   lblDamage.setDisplayedMnemonic('g');
   lblDamage.setLabelFor(cmbDamageDieCount);
   getContentPane().add(lblDamage, new AbsoluteConstraints(30, 25, 80, -1));
   lblCritRange.setText("Crit Range");
   lblCritRange.setDisplayedMnemonic('R');
   lblCritRange.setLabelFor(cmbCritRange);
   getContentPane().add(lblCritRange, new AbsoluteConstraints(30, 55, 80, -1));
   lblCritMult.setText("Crit Multiplier");
   lblCritMult.setDisplayedMnemonic('M');
   lblCritMult.setLabelFor(cmbCritMult);
   getContentPane().add(lblCritMult, new AbsoluteConstraints(30, 85, 80, -1));
   lbld.setHorizontalAlignment(SwingConstants.CENTER);
   lbld.setText("d");
   lbld.setDisplayedMnemonic('d');
   lbld.setLabelFor(cmbDamageDieSides);
   getContentPane().add(lbld, new AbsoluteConstraints(170, 25, 20, -1));
   getContentPane().add(cmbDamageDieCount, new AbsoluteConstraints(110, 20, 60, -1));
   getContentPane().add(cmbDamageDieSides, new AbsoluteConstraints(190, 20, 60, -1));
   getContentPane().add(cmbCritRange, new AbsoluteConstraints(110, 50, 60, -1));
   getContentPane().add(cmbCritMult, new AbsoluteConstraints(110, 80, 60, -1));
   //
   // Stick a dummy label in the bottom-right corner to maintain a uniform boundary after
   // packing
   //
   jLabel1.setText(" ");
   getContentPane().add(jLabel1, new AbsoluteConstraints(250, 160, -1, -1));
 */
		GridBagConstraints gridBagConstraints;

//		btnCancel = new javax.swing.JButton();
//		btnOK = new javax.swing.JButton();
//		lblDamage = new javax.swing.JLabel();
//		lblCritRange = new javax.swing.JLabel();
//		lblCritMult = new javax.swing.JLabel();
//		lbld = new javax.swing.JLabel();
//		cmbDamageDieCount = new javax.swing.JComboBoxEx();
//		cmbDamageDieSides = new javax.swing.JComboBoxEx();
//		cmbCritRange = new javax.swing.JComboBoxEx();
//		cmbCritMult = new javax.swing.JComboBoxEx();
		getContentPane().setLayout(new GridBagLayout());

		setTitle("Base Weapon Info");
		setModal(true);
		setResizable(false);
		addWindowListener(new WindowAdapter()
			{
            @Override
				public void windowClosing(WindowEvent evt)
				{
					closeDialog();
				}
			});

		btnCancel.setText("Cancel");
		btnCancel.addActionListener(new ActionListener()
			{
            @Override
				public void actionPerformed(ActionEvent evt)
				{
					closeDialog();
				}
			});
		btnCancel.addMouseListener(new MouseAdapter()
			{
            @Override
				public void mouseClicked(MouseEvent evt)
				{
					closeDialog();
				}
			});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		getContentPane().add(btnCancel, gridBagConstraints);

		btnOK.setText("OK");
		btnOK.addActionListener(new ActionListener()
			{
            @Override
				public void actionPerformed(ActionEvent evt)
				{
					btnOKActionPerformed();
				}
			});
		btnOK.addMouseListener(new MouseAdapter()
			{
            @Override
				public void mouseClicked(MouseEvent evt)
				{
					btnOKActionPerformed();
				}
			});
		btnOK.setPreferredSize(new Dimension(73, 26));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 6;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		getContentPane().add(btnOK, gridBagConstraints);

		lblDamage.setText("Damage");
		lblDamage.setDisplayedMnemonic('g');
		lblDamage.setLabelFor(cmbDamageDieCount);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 33;
		gridBagConstraints.insets = new Insets(0, 0, 8, 0);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		getContentPane().add(lblDamage, gridBagConstraints);

		lblCritRange.setText("Crit Range");
		lblCritRange.setDisplayedMnemonic('R');
		lblCritRange.setLabelFor(cmbCritRange);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 21;
		gridBagConstraints.insets = new Insets(0, 0, 8, 0);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		getContentPane().add(lblCritRange, gridBagConstraints);

		lblCritMult.setText("Crit Multiplier");
		lblCritMult.setDisplayedMnemonic('M');
		lblCritMult.setLabelFor(cmbCritMult);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 5;
		gridBagConstraints.insets = new Insets(0, 0, 8, 0);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		getContentPane().add(lblCritMult, gridBagConstraints);

		lbld.setHorizontalAlignment(SwingConstants.CENTER);
		lbld.setText("d");
		lbld.setDisplayedMnemonic('d');
		lbld.setLabelFor(cmbDamageDieSides);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 7;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 13;
		gridBagConstraints.insets = new Insets(0, 0, 8, 0);
		getContentPane().add(lbld, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 5;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 6;
		gridBagConstraints.insets = new Insets(0, 0, 7, 0);
		getContentPane().add(cmbDamageDieCount, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 9;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 6;
		gridBagConstraints.insets = new Insets(0, 0, 7, 0);
		getContentPane().add(cmbDamageDieSides, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 5;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 6;
		gridBagConstraints.insets = new Insets(0, 0, 7, 0);
		getContentPane().add(cmbCritRange, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 5;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 6;
		gridBagConstraints.insets = new Insets(0, 0, 7, 0);
		getContentPane().add(cmbCritMult, gridBagConstraints);

		//
		// Set up numbers in range 0 to 9
		//
		Integer[] numbers = new Integer[10];

		for (int i = 0; i <= 9; i++)
		{
			numbers[i] = Integer.valueOf(i);
		}

		cmbDamageDieCount.setModel(new DefaultComboBoxModel(numbers));

		numbers = new Integer[9];
		numbers[0] = Integer.valueOf(0);
		numbers[1] = Integer.valueOf(1);
		numbers[2] = Integer.valueOf(2);
		numbers[3] = Integer.valueOf(3);
		numbers[4] = Integer.valueOf(4);
		numbers[5] = Integer.valueOf(6);
		numbers[6] = Integer.valueOf(8);
		numbers[7] = Integer.valueOf(10);
		numbers[8] = Integer.valueOf(20);
		cmbDamageDieSides.setModel(new DefaultComboBoxModel(numbers));

		numbers = new Integer[4];
		numbers[0] = Integer.valueOf(1);
		numbers[1] = Integer.valueOf(2);
		numbers[2] = Integer.valueOf(3);
		numbers[3] = Integer.valueOf(4);
		cmbCritRange.setModel(new DefaultComboBoxModel(numbers));

		numbers = new Integer[3];
		numbers[0] = Integer.valueOf(2);
		numbers[1] = Integer.valueOf(3);
		numbers[2] = Integer.valueOf(4);
		cmbCritMult.setModel(new DefaultComboBoxModel(numbers));

		pack();
	}
}
