/*
 * HPFrame.java
 * Copyright 2001 (C) Greg Bingleman
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

/**
 * Popup frame with export options
 *
 * @author  @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class HPFrame extends JFrame
{
	private MainHP mainHP = null;

	HPFrame()
	{
		super();
		setTitle(myGetTitle());

		IconUtilitities.maybeSetIcon(this, IconUtilitities.RESOURCE_APP_ICON);
		Toolkit kit = Toolkit.getDefaultToolkit();

		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		// Dimension screenSize = kit.getScreenSize();
		Dimension screenSize = Utility.getScreenSize(kit);
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		// center frame in screen
		setSize(screenWidth >> 1, screenHeight >> 1);
		setLocation(screenWidth >> 2, screenHeight >> 2);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		mainHP = new MainHP();

		Container contentPane = getContentPane();
		contentPane.add(mainHP);
		setVisible(true);
	}

	/**
	 * Set the PC for this Frame
	 * @param aPC
	 */
	public void setCharacter(PlayerCharacter aPC) {
	    mainHP.setCharacter(aPC);
	}

	/**
	 * Set the Preferred Size of the HP Frame
	 */
	public void setPSize()
	{
		if (mainHP != null)
		{
			mainHP.setPSize();
		}
	}

	private static String myGetTitle()
	{
		String title = LanguageBundle.getString("in_adjustHP");
		final int idx = title.indexOf("%s");

		if (idx >= 0)
		{
			title = title.substring(0, idx) + Globals.getGameModeHitPointText() + title.substring(idx + 2);
		}

		return title;
	}
}
 //end HPFrame
