/*
 * SinglePrefDialog.java
 * Copyright James Dempsey, 2010
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
 * Created on 29 Dec 2010
 *
 * $$Id: SinglePrefDialog.java 14348 2011-01-12 08:01:55Z jdempsey $$
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>SinglePrefDialog</code> displays a single 
 * preference panel to the user.
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2011-01-12 00:01:55 -0800 (Wed, 12 Jan 2011) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 14348 $
 */
public class SinglePrefDialog extends JDialog
{
	private PCGenPrefsPanel prefsPanel;
	private JPanel controlPanel;
	
	/**
	 * Create a new modal SinglePrefDialog to display a particular panel.
	 *  
	 * @param parent The parent frame, used for positioning and to be modal 
	 * @param prefsPanel The panel to be displayed.
	 */
	public SinglePrefDialog(JFrame parent, PCGenPrefsPanel prefsPanel)
	{
		super(parent, prefsPanel.getTitle(), true);
		
		this.prefsPanel = prefsPanel;

		initComponents();
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(prefsPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlPanel, BorderLayout.SOUTH);

		prefsPanel.applyOptionValuesToControls();

		pack();
		
		Utility.installEscapeCloseOperation(this);
	}

	private void initComponents()
	{
		// Build the control panel (OK/Cancel buttons)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton(LanguageBundle.getString("in_ok"));
		okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok"));
		controlPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed();
			}
		});

		JButton cancelButton =
				new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel"));
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});
	}
	
	private void cancelButtonActionPerformed()
	{
		setVisible(false);
		this.dispose();
	}

	private void okButtonActionPerformed()
	{
		prefsPanel.setOptionsBasedOnControls();
		setVisible(false);

		this.dispose();
	}
	
}
