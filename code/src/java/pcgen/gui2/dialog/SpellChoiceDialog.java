/*
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
import javax.swing.WindowConstants;

import pcgen.facade.core.SpellBuilderFacade;
import pcgen.gui2.equip.SpellChoicePanel;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code SpellChoiceDialog} provides a pop-up dialog that allows
 * the user to select a spell for inclusion in things like custom equipment 
 * items.  
 *
 * 
 */
@SuppressWarnings("serial")
public class SpellChoiceDialog extends JDialog implements ActionListener
{
	private final SpellChoicePanel spellChoicePanel;
	private final JPanel buttonPanel;
	private final JButton okButton;
	private final JButton cancelButton;
	private boolean cancelled;

	/**
	 * Create a new instance of SpellChoiceDialog
	 * @param frame The parent frame we are displaying over.
	 */
	public SpellChoiceDialog(JFrame frame, SpellBuilderFacade builder)
	{
		super(frame, true);
		setTitle(LanguageBundle.getString("in_csdChooseSpell")); //$NON-NLS-1$
		this.buttonPanel = new JPanel();
		this.okButton = new JButton(LanguageBundle.getString("in_ok")); //$NON-NLS-1$
		this.okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok")); //$NON-NLS-1$
		this.cancelButton = new JButton(LanguageBundle.getString("in_cancel")); //$NON-NLS-1$
		this.cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel")); //$NON-NLS-1$

		this.spellChoicePanel = new SpellChoicePanel(builder);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		initComponents();
		pack();
		Utility.resizeComponentToScreen(this);
	}

	private void initComponents()
	{
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		pane.add(spellChoicePanel, BorderLayout.CENTER);

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		Box buttons = Box.createHorizontalBox();
		buttons.add(buttonPanel);
		buttons.add(Box.createHorizontalGlue());
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
		cancelled = e.getSource() == cancelButton;

		setVisible(false);
	}

	public boolean isCancelled()
	{
		return cancelled;
	}

}
