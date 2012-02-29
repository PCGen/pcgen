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
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;

import pcgen.core.SettingsHandler;
import pcgen.gui2.doomsdaybook.NameGenPanel;
import pcgen.gui2.tools.Utility;

/**
 * The Class <code>RandomNameDialog</code> is a dialog in which the user can 
 * generate a random name for their character.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class RandomNameDialog extends JDialog
{
	private NameGenPanel nameGenPanel;

	/**
	 * Create a new Random Name Dialog
	 * @param frame The parent frame. The dialog will be centred on this frame
	 */
	public RandomNameDialog(JFrame frame, String gender)
	{
		super(frame, "Generate Random Name", true);
		getContentPane().setLayout(new BorderLayout());
		nameGenPanel = new NameGenPanel(new File(getDataDir()), true);
		nameGenPanel.setGender(gender);
		getContentPane().add(nameGenPanel, BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setLocationRelativeTo(frame);
		
		Utility.installEscapeCloseOperation(this);
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
		return nameGenPanel.getChosenName();
	}

	/**
	 * @return the gender
	 */
	public String getGender()
	{
		return nameGenPanel.getGender();
	}

}
