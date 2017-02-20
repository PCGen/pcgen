/*
 * EquipCustomizerDialog.java
 * Copyright James Dempsey, 2013
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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentBuilderFacade;
import pcgen.gui2.equip.EquipCustomPanel;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code EquipCustomizerDialog} provides a pop-up dialog that allows
 * the user to build up custom equipment items by adding equipment modifiers and
 * setting the name, cost etc.  
 *
 * <br>
 * 
 */
@SuppressWarnings("serial")
public class EquipCustomizerDialog extends JDialog
		implements ActionListener
{
	private EquipCustomPanel equipCustomPanel;
	private JPanel buttonPanel;
	private JButton buyButton;
	private JButton okButton;
	private JButton cancelButton;
	private boolean purchase;
	private boolean cancelled;

	/**
	 * Create a new instance of KitSelectionDialog
	 * @param frame The parent frame we are displaying over.
	 * @param character The character being displayed.
	 */
	public EquipCustomizerDialog(JFrame frame, CharacterFacade character, EquipmentBuilderFacade builder)
	{
		super(frame, true);
		setTitle(LanguageBundle.getString("in_itemCustomizer")); //$NON-NLS-1$
		this.buttonPanel = new JPanel();
		this.buyButton = new JButton(LanguageBundle.getString("in_buy")); //$NON-NLS-1$
		this.buyButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_buy")); //$NON-NLS-1$
		this.okButton = new JButton(LanguageBundle.getString("in_ok")); //$NON-NLS-1$
		this.okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok")); //$NON-NLS-1$
		this.cancelButton = new JButton(LanguageBundle.getString("in_cancel")); //$NON-NLS-1$
		this.cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel")); //$NON-NLS-1$

		this.equipCustomPanel = new EquipCustomPanel(character, builder);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		initComponents();
		pack();
		Utility.resizeComponentToScreen(this);
	}

	private void initComponents()
	{
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		pane.add(equipCustomPanel, BorderLayout.CENTER);
		
		buyButton.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		Box buttons = Box.createHorizontalBox();
		buttons.add(buttonPanel);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(buyButton);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(okButton);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(cancelButton);
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pane.add(buttons, BorderLayout.SOUTH);
		
		Utility.installEscapeCloseOperation(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		purchase = e.getSource() == buyButton;
		cancelled = e.getSource() == cancelButton;

		setVisible(false);
	}

	/**
	 * @return
	 */
	public boolean isPurchase()
	{
		return purchase;
	}

	public boolean isCancelled()
	{
		return cancelled;
	}

}
