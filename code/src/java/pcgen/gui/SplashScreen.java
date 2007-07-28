/*
 * SplashScreen.java
 * Copyright 2001 (C) Mario Bonassin
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

import pcgen.gui.utils.IconUtilitities;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.net.URL;

/**
 * <code>SplashScreen</code> creates a splash screen
 *
 * @author zebuleon
 * @version $Revision$
 */
final class SplashScreen extends JWindow
{
	/**
	 * Constructor
	 */
	public SplashScreen()
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		JPanel splash = new JPanel(new BorderLayout(12, 12));
		splash.setBorder(new CompoundBorder(new MatteBorder(1, 1, 1, 1, Color.black), new EmptyBorder(12, 12, 12, 12)));
		splash.setBackground(Color.black);

		URL url = getClass().getResource(IconUtilitities.RESOURCE_URL + "SplashPcgen-Alpha.png");

		if (url != null)
		{
			JLabel label = new JLabel(new ImageIcon(url));
			splash.add(label, BorderLayout.CENTER);
		}

		setContentPane(splash);

		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
		GraphicsConfiguration defaultConfiguration = defaultScreenDevice.getDefaultConfiguration();
		Rectangle screen = defaultConfiguration.getBounds();
//		Dimension screen = Utility.getScreenSize(getToolkit());
		pack();
		setLocation(screen.x + (screen.width - getSize().width) / 2, screen.y + (screen.height - getSize().height) / 2);
		setVisible(true);
	}
}
