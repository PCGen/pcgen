/*
 * EQFrame.java
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

import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.util.PropertyFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Popup frame which allows the user to customize equipment.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version    $Revision$
 */
public final class EQFrame extends JFrame
{
	private EqBuilder mainEq = null;
	private PlayerCharacter aPC;

	/**
	 * Constructor
	 * @param aPC
	 */
	public EQFrame(PlayerCharacter aPC)
	{
		super(PropertyFactory.getString("in_itemCustomizer"));

		this.aPC = aPC;

		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(getClass().getResource(IconUtilitities.RESOURCE_URL + "PcgenIcon.gif"));
		this.setIconImage(img);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		mainEq = new EqBuilder(aPC);
		mainEq.setParentWindow(this);

		Container contentPane = getContentPane();
		contentPane.add(mainEq);

		pack();

		final Dimension customizerDim = SettingsHandler.getCustomizerDimension();
		final Point customizerLoc = SettingsHandler.getCustomizerLeftUpperCorner();
		int x = -11;
		int y = -11;

		if (customizerLoc != null)
		{
			x = (int) customizerLoc.getX();
			y = (int) customizerLoc.getY();
		}

		if ((x < -10) || (y < -10) || (customizerDim == null) || (customizerDim.height == 0)
		    || (customizerDim.width == 0))
		{
			Utility.centerFrame(this, true);
		}
		else
		{
			setLocation(customizerLoc);
			setSize(customizerDim);
		}
	}

	/**
	 * Set the equipment
	 * @param aEq
	 * @return TRUE if OK
	 */
	public boolean setEquipment(Equipment aEq)
	{
		if (mainEq != null)
		{
			return mainEq.setEquipment(aEq);
		}

		return false;
	}

	public void toFront()
	{
		super.toFront();

		if (mainEq != null)
		{
			mainEq.toFront();
		}
	}

	//
	// Overridden so we can handle exit on System Close
	// by calling <code>handleQuit</code>.
	//
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			handleQuit(true);
		}
	}

	/**
	 * Closes the program by calling <code>handleQuit</code>
	 * @param bCancelled
	 */
	void exitItem_actionPerformed(boolean bCancelled)
	{
		handleQuit(bCancelled);
	}

	/**
	 * Does the real work in closing the program.
	 * Closes each character tab, giving user a chance to save.
	 * Saves options to file, then cleans up and exits.
	 * @param bCancelled
	 */
	private void handleQuit(boolean bCancelled)
	{
		if (!bCancelled)
		{
			SettingsHandler.setCustomizerLeftUpperCorner(getLocationOnScreen());
			SettingsHandler.setCustomizerDimension(getSize());
			SettingsHandler.writeOptionsProperties(aPC);
		}

		Globals.setCurrentFrame(null);
		this.dispose();
	}
}
 //end EQFrame
