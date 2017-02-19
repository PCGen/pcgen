/*
 * KitSelectionDialog.java
 * Copyright James Dempsey, 2012
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
import pcgen.gui2.kits.KitPanel;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code KitSelectionDialog} provides a pop-up dialog that allows
 * the user to add kits to a character. Kits are prepared groups of equipment and 
 * other rules items.  
 *
 * <br>
 * 
 */
@SuppressWarnings("serial")
public class KitSelectionDialog extends JDialog
		implements ActionListener
{
	private KitPanel kitPanel;
	private JPanel buttonPanel;
	private JButton closeButton;

	/**
	 * Create a new instance of KitSelectionDialog
	 * @param frame The parent frame we are displaying over.
	 * @param character The character being displayed.
	 */
	public KitSelectionDialog(JFrame frame, CharacterFacade character)
	{
		super(frame, true);
		setTitle(LanguageBundle.getString("in_mnuEditAddKit")); //$NON-NLS-1$
		this.buttonPanel = new JPanel();
		this.closeButton = new JButton(LanguageBundle.getString("in_close")); //$NON-NLS-1$
		this.closeButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_close")); //$NON-NLS-1$
		this.kitPanel = new KitPanel(character);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		initComponents();
		pack();
		Utility.resizeComponentToScreen(this);
	}

	private void initComponents()
	{
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		pane.add(kitPanel, BorderLayout.CENTER);
		
		closeButton.addActionListener(this);

		Box buttons = Box.createHorizontalBox();
		buttons.add(buttonPanel);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(closeButton);
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pane.add(buttons, BorderLayout.SOUTH);
		
		Utility.installEscapeCloseOperation(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//must be the ok command
		setVisible(false);
	}

}
