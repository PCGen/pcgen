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
 *  AddCombatant.java
 *
 *  Created on January 4, 2002, 2:10 PM
 */
package plugin.experience.gui;

import pcgen.core.SettingsHandler;
import plugin.experience.DefeatedCombatant;
import plugin.experience.ExperienceAdjusterModel;
import plugin.experience.ExperienceAdjusterPlugin;
import plugin.experience.ExperienceListItem;

/**
 *@author     devon
 *@since    April 7, 2003
 */
public class AddDefeatedCombatant extends javax.swing.JDialog
{
	// End of variables declaration//GEN-END:variables

	/**  Description of the Field */
	public ExperienceAdjusterModel model;
	private javax.swing.JButton bCancel;
	private javax.swing.JButton bSave;
	private javax.swing.JLabel lBuffer;
	private javax.swing.JLabel lCR;
	private javax.swing.JLabel lCR2;
	private javax.swing.JLabel lName;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel lNumber;
	private javax.swing.JSlider sNumber;
	private javax.swing.JTextField tCR;
	private javax.swing.JTextField tName;
	private javax.swing.JTextField tNumber;

	/**
	 *  Creates new dialog for Adding a new Combatant This contructor is used if
	 *  you know what frame you are launching from
	 *
	 *@param  parent      Description of the Parameter
	 *@param  modal       Description of the Parameter
	 *@param  model  Description of the Parameter
	 */
	public AddDefeatedCombatant(java.awt.Frame parent, boolean modal, ExperienceAdjusterModel model)
	{
		super(parent, modal);
		initComponents();
		setLocation(parent.getX() + 100, parent.getY() + 100);
		this.model = model;

		int maxNum = SettingsHandler.getGMGenOption(ExperienceAdjusterPlugin.LOG_NAME + ".dbMaxNum", 20);
		sNumber.setMaximum(maxNum);
	}

	/**  Sets the number label of the dialog */
	private void setNumber()
	{
		tNumber.setText("" + sNumber.getValue());
	}

	//GEN-LAST:event_sNumberPropertyChange
	private void bCancelActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_bCancelActionPerformed
		setVisible(false);
		dispose();
	}

	//GEN-LAST:event_bCancelActionPerformed
	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_bSaveActionPerformed
		// If we are creating multiple guys, loop
		if (checkNumberField(tNumber, 1) > 1)
		{
			for (int i = 1; i <= checkNumberField(tNumber, 1); i++)
			{
				StringBuffer name = new StringBuffer();

				if (tName.getText().equals(""))
				{
					name.append("Defeated Enemy CR: ");
				}
				else
				{
					name.append(tName.getText() + " CR: ");
				}

				name.append(checkCRField(tCR, 0) + " ");
				name.append("(" + i + ")");
				model.addEnemy(new ExperienceListItem(new DefeatedCombatant(name.toString(), checkCRField(tCR, 0))));
			}
		}

		// If if not, just add one
		else
		{
			StringBuffer name = new StringBuffer();

			if (tName.getText().equals(""))
			{
				name.append("Defeated Enemy CR: ");
			}
			else
			{
				name.append(tName.getText() + " CR: ");
			}

			name.append(checkCRField(tCR, 0) + "");
			model.addEnemy(new ExperienceListItem(new DefeatedCombatant(name.toString(), checkCRField(tCR, 0))));
		}

		setVisible(false);
		dispose();
	}

	private float checkCRField(javax.swing.JTextField tf, float def)
	{
		float value;

		try
		{
			value = Float.parseFloat(tf.getText());

			if (value > 1.0)
			{
				value = ((int) value / 1);
			}

			return value;
		}
		catch (NumberFormatException e)
		{
			tf.setText(def + "");

			return def;
		}
	}

	//GEN-LAST:event_closeDialog
	private float checkNumberField(javax.swing.JTextField tf, int def)
	{
		try
		{
			return Integer.parseInt(tf.getText());
		}
		catch (NumberFormatException e)
		{
			tf.setText(def + "");

			return def;
		}
	}

	//GEN-LAST:event_bSaveActionPerformed

	/**
	 *  Closes the dialog
	 *
	 *@param  evt  The close event
	 */
	private void closeDialog(java.awt.event.WindowEvent evt)
	{
		//GEN-FIRST:event_closeDialog
		setVisible(false);
		dispose();
	}

	/**
	 *  This method is called from within the constructor to initialize the form.
	 *  WARNING: Do NOT modify this code. The content of this method is always
	 *  regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents

		java.awt.GridBagConstraints gridBagConstraints;

		lName = new javax.swing.JLabel();
		tName = new javax.swing.JTextField();
		lNumber = new javax.swing.JLabel();
		sNumber = new javax.swing.JSlider();
		bSave = new javax.swing.JButton();
		bCancel = new javax.swing.JButton();
		tNumber = new javax.swing.JTextField();
		lBuffer = new javax.swing.JLabel();
		lCR = new javax.swing.JLabel();
		lCR2 = new javax.swing.JLabel();
		tCR = new javax.swing.JTextField();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(java.awt.event.WindowEvent evt)
				{
					closeDialog(evt);
				}
			});

		lCR.setText("CR");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
		getContentPane().add(lCR, gridBagConstraints);

		lNumber.setText("Number");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		getContentPane().add(lNumber, gridBagConstraints);

		lName.setText("Name");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		getContentPane().add(lName, gridBagConstraints);

		lCR2.setText("(Note, use decimal fractions for CR. Ex: .5 = 1/2)");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		getContentPane().add(lCR2, gridBagConstraints);

		lBuffer.setText("        ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
		getContentPane().add(lBuffer, gridBagConstraints);

		tCR.setText("1.0");
		tCR.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					tCRActionPerformed(evt);
				}
			});

		tCR.addFocusListener(new java.awt.event.FocusAdapter()
			{
				public void focusLost(java.awt.event.FocusEvent evt)
				{
					tCRFocusLost(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		getContentPane().add(tCR, gridBagConstraints);

		sNumber.setMaximum(20);
		sNumber.setMinimum(1);
		sNumber.setValue(1);
		sNumber.addKeyListener(new java.awt.event.KeyAdapter()
			{
				public void keyPressed(java.awt.event.KeyEvent evt)
				{
					sNumberKeyPressed(evt);
				}

				public void keyReleased(java.awt.event.KeyEvent evt)
				{
					sNumberKeyReleased(evt);
				}
			});

		sNumber.addMouseListener(new java.awt.event.MouseAdapter()
			{
				public void mousePressed(java.awt.event.MouseEvent evt)
				{
					sNumberMousePressed(evt);
				}

				public void mouseReleased(java.awt.event.MouseEvent evt)
				{
					sNumberMouseReleased(evt);
				}
			});

		sNumber.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
			{
				public void mouseDragged(java.awt.event.MouseEvent evt)
				{
					sNumberMouseDragged(evt);
				}

				/*public void mouseMoved(java.awt.event.MouseEvent evt) {
				   sNumberMouseMoved(evt);
				   }*/
			});

		sNumber.addPropertyChangeListener(new java.beans.PropertyChangeListener()
			{
				public void propertyChange(java.beans.PropertyChangeEvent evt)
				{
					sNumberPropertyChange(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		getContentPane().add(sNumber, gridBagConstraints);

		tNumber.setText("1");
		tNumber.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					tNumberActionPerformed(evt);
				}
			});

		tNumber.addFocusListener(new java.awt.event.FocusAdapter()
			{
				public void focusLost(java.awt.event.FocusEvent evt)
				{
					tNumberFocusLost(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		getContentPane().add(tNumber, gridBagConstraints);

		tName.setMinimumSize(new java.awt.Dimension(100, 21));
		tName.setPreferredSize(new java.awt.Dimension(200, 21));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		getContentPane().add(tName, gridBagConstraints);

		bSave.setText("Save");
		bSave.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					bSaveActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
		getContentPane().add(bSave, gridBagConstraints);

		bCancel.setText("Cancel");
		bCancel.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					bCancelActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		getContentPane().add(bCancel, gridBagConstraints);

		pack();
	}
	 //GEN-END:initComponents

	//GEN-LAST:event_sNumberKeyReleased
	private void sNumberKeyPressed(java.awt.event.KeyEvent evt)
	{
		//GEN-FIRST:event_sNumberKeyPressed
		setNumber();
	}

	private void sNumberKeyReleased(java.awt.event.KeyEvent evt)
	{
		//GEN-FIRST:event_sNumberKeyReleased
		setNumber();
	}

	//GEN-LAST:event_sNumberMousePressed
	private void sNumberMouseDragged(java.awt.event.MouseEvent evt)
	{
		//GEN-FIRST:event_sNumberMouseDragged
		setNumber();
	}

	//GEN-LAST:event_sNumberMouseReleased
	private void sNumberMousePressed(java.awt.event.MouseEvent evt)
	{
		//GEN-FIRST:event_sNumberMousePressed
		setNumber();
	}

	//GEN-LAST:event_sNumberKeyPressed
	private void sNumberMouseReleased(java.awt.event.MouseEvent evt)
	{
		//GEN-FIRST:event_sNumberMouseReleased
		setNumber();
	}

	//GEN-LAST:event_sNumberMouseDragged
	private void sNumberPropertyChange(java.beans.PropertyChangeEvent evt)
	{
		//GEN-FIRST:event_sNumberPropertyChange
		setNumber();
	}

	private void tCRActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_tCRActionPerformed
		tCR.setText(Float.toString(checkCRField(tCR, 1)));
	}
	 //GEN-LAST:event_tCRActionPerformed

	private void tCRFocusLost(java.awt.event.FocusEvent evt)
	{ //GEN-FIRST:event_tCRFocusLost
		tCR.setText(Float.toString(checkCRField(tCR, 1)));
	}
	 //GEN-LAST:event_tCRFocusLost

	private void tNumberActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_tNumberActionPerformed
		checkNumberField(tNumber, 1);
	}
	 //GEN-LAST:event_tNumberActionPerformed

	private void tNumberFocusLost(java.awt.event.FocusEvent evt)
	{ //GEN-FIRST:event_tNumberFocusLost
		checkNumberField(tNumber, 1);
	}
	 //GEN-LAST:event_tNumberFocusLost

	//private Preferences initPrefs = Preferences.userNodeForPackage(Initiative.class);
}
