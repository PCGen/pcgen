/*
 * RandomNameDialog.java
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
 * Created on 20/06/2010 12:10:46 PM
 *
 * $Id$
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;

import pcgen.core.SettingsHandler;
import pcgen.gui2.doomsdaybook.NameGenPanel;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code RandomNameDialog} is a dialog in which the user can
 * generate a random name for their character.
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
@SuppressWarnings("serial")
public class RandomNameDialog extends JDialog
{
	private NameGenPanel nameGenPanel;
	private boolean cancelled;

	/**
	 * Create a new Random Name Dialog
	 * @param frame The parent frame. The dialog will be centred on this frame
	 * @param gender The current gender of the character.
	 */
	public RandomNameDialog(JFrame frame, String gender)
	{
		super(frame, LanguageBundle.getString("in_rndNameTitle"), true); //$NON-NLS-1$
		nameGenPanel = new NameGenPanel(new File(getDataDir()));
		nameGenPanel.setGender(gender);
		initUserInterface();
		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		if (frame != null)
		{
			Utility.setComponentRelativeLocation(frame, this);
		}
		cancelled = false;
		
		Utility.installEscapeCloseOperation(this);
	}

	private void initUserInterface()
	{
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(nameGenPanel, BorderLayout.CENTER);

		// Build the control panel (OK/Cancel buttons)
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton(LanguageBundle.getString("in_ok")); //$NON-NLS-1$
		okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok")); //$NON-NLS-1$
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
				new JButton(LanguageBundle.getString("in_cancel")); //$NON-NLS-1$
		cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel")); //$NON-NLS-1$
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
	}

	private void okButtonActionPerformed()
	{
		setVisible(false);
	}

	private void cancelButtonActionPerformed()
	{
		cancelled = true;
		setVisible(false);
	}
	
	/**
	 * @return The directory where the random name data is held
	 */
	private String getDataDir()
	{
		String pluginDirectory = SettingsHandler.getGmgenPluginDir().toString();

		return pluginDirectory + File.separator + "Random Names";
	}
	
	/**
	 * @return The name the user generated.
	 */
	public String getChosenName()
	{
		if (cancelled)
		{
			return StringUtils.EMPTY;
		}
		return nameGenPanel.getChosenName();
	}

	/**
	 * @return the gender
	 */
	public String getGender()
	{
		if (cancelled)
		{
			return StringUtils.EMPTY;
		}
		return nameGenPanel.getGender();
	}

}
