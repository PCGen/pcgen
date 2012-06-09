/*
 * CoreUtility.java
 * Copyright 2002-2003 (C) B. K. Oxley (binkley)
 * <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on Februrary 4th, 2002.
 */
package pcgen.gui2.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import pcgen.system.PCGenSettings;

/**
 * Convenience methods from various sources.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 14613 $
 */
public final class Utility
{

	private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(
		KeyEvent.VK_ESCAPE, 0);

	/** An action map key for the user requesting a dialog close via the ESC key. */
	public static final String dispatchWindowClosingActionMapKey =
			"pcgen:WINDOW_CLOSING"; //$NON-NLS-1$

	/**
	 * Set up GridBag Constraints.
	 * @param gbc The gridbagconstraints to set up
	 * @param gx  cols from left (left-most col for multi-column cell)
	 * @param gy  rows from top (top-most row for multi-row cell)
	 * @param gw  cols wide
	 * @param gh  rows high
	 * @param wx  weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 */
	public static void buildConstraints(GridBagConstraints gbc, int gx, int gy,
										int gw, int gh, double wx, double wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	/**
	 * Set up GridBag Constraints.
	 * @param gbc The gridbagconstraints to set up
	 * @param gx  cols from left (left-most col for multi-column cell)
	 * @param gy  rows from top (top-most row for multi-row cell)
	 * @param gw  cols wide
	 * @param gh  rows high
	 * @param wx  weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 * @param fill How should the component be resized if smaller than the space.
	 * @param anchor Where should the component be placed if smaller than the space.
	 */
	public static void buildConstraints(GridBagConstraints gbc, int gx, int gy,
										int gw, int gh, double wx, double wy, int fill, int anchor)
	{
		buildConstraints(gbc, gx, gy, gw, gh, wx, wy);
		gbc.fill = fill;
		gbc.anchor = anchor;
	}

	/**
	 * Set up GridBag Constraints in a relative pattern. Components must be 
	 * added in order row by row.
	 * 
	 * @param gbc The gridbagconstraints to set up
	 * @param gw  cols wide
	 * @param gh  rows high
	 * @param wx  weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 * @param fill How should the component be resized if smaller than the space.
	 * @param anchor Where should the component be placed if smaller than the space.
	 */
	public static void buildRelativeConstraints(GridBagConstraints gbc,
												int gw, int gh, double wx, double wy, int fill, int anchor)
	{
		buildConstraints(gbc, GridBagConstraints.RELATIVE,
						 GridBagConstraints.RELATIVE, gw, gh, wx, wy);
		gbc.fill = fill;
		gbc.anchor = anchor;
	}

	/**
	 * Set up GridBag Constraints in a relative pattern. Components must be 
	 * added in order row by row.
	 * 
	 * @param gbc The gridbagconstraints to set up
	 * @param gw  cols wide
	 * @param gh  rows high
	 * @param wx  weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy  weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 */
	public static void buildRelativeConstraints(GridBagConstraints gbc,
												int gw, int gh, double wx, double wy)
	{
		buildConstraints(gbc, GridBagConstraints.RELATIVE,
						 GridBagConstraints.RELATIVE, gw, gh, wx, wy);
	}

	/**
	 * Centers a <code>JDialog</code> to the screen.
	 *
	 * @param dialog JDialog dialog to center
	 */
	public static void centerDialog(JDialog dialog)
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		// final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle screenSize =
				GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().getDefaultConfiguration()
					.getBounds();

		final Dimension dialogSize = dialog.getSize();

		if (dialogSize.height > screenSize.height)
		{
			dialogSize.height = screenSize.height;
		}

		if (dialogSize.width > screenSize.width)
		{
			dialogSize.width = screenSize.width;
		}
		dialog.setSize(dialogSize);
		
		dialog.setLocation(screenSize.x + (screenSize.width - dialogSize.width) / 2,
						   screenSize.y + (screenSize.height - dialogSize.height) / 2);
	}

	/**
	 * Update the size of the dialog to ensure it will fit on the screen.
	 * @param dialog The dialog to be resized.
	 */
	public static void resizeDialogToScreen(JDialog dialog)
	{
		Rectangle screenBounds = dialog.getGraphicsConfiguration().getBounds();

		final Dimension dialogSize = dialog.getSize();

		if (dialogSize.height > screenBounds.height)
		{
			dialogSize.height = screenBounds.height;
		}

		if (dialogSize.width > screenBounds.width)
		{
			dialogSize.width = screenBounds.width;
		}
		dialog.setSize(dialogSize);
	}
	
	/**
	 * Centers a <code>JFrame</code> to the screen.
	 *
	 * @param frame JFrame frame to center
	 * @param isPopup boolean is the frame a popup dialog?
	 */
	public static void centerFrame(JFrame frame, boolean isPopup)
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack.
		// TODO: remove the hack, once Java fixed this.
		// final Dimension screenSize = getScreenSize(Toolkit.getDefaultToolkit());
		final Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();

		if (isPopup)
		{
			frame.setSize(screenSize.width / 2, screenSize.height / 2);
		}

		final Dimension frameSize = frame.getSize();

		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}

		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}

		frame.setLocation(screenSize.x + (screenSize.width - frameSize.width) / 2,
						  screenSize.y + (screenSize.height - frameSize.height) / 2);
	}

	/**
	 * Sets the default browser.
	 * @param parent The component to show the dialog over.
	 */
	public static void selectDefaultBrowser(Component parent)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your preferred html browser.");

		if (System.getProperty("os.name").startsWith("Mac OS"))
		{
			// On MacOS X, do not traverse file bundles
			fc.putClientProperty("JFileChooser.appBundleIsTraversable",
								 "never");
		}

		if (PCGenSettings.getBrowserPath() == null)
		{
			//No action, as we have no idea what a good default would be...
		}
		else
		{
			fc.setCurrentDirectory(new File(PCGenSettings.getBrowserPath()));
		}

		final int returnVal = fc.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			final File file = fc.getSelectedFile();
			PCGenSettings.OPTIONS_CONTEXT.setProperty(PCGenSettings.BROWSER_PATH, file.getAbsolutePath());
		}
	}

	/**
	 * View a URL in a browser.  Uses BrowserLauncher class.
	 *
	 * @param url URL to display in browser.
	 * @throws IOException 
	 * @see pcgen.gui2.tools.BrowserLauncher
	 */
	public static void viewInBrowser(String url) throws IOException
	{
		final String osName = System.getProperty("os.name");

		// Windows tends to lock up or not actually
		// display anything unless we've specified a
		// default browser, so at least make the user
		// aware that (s)he needs one. If they don't
		// pick one and it doesn't work, at least they
		// might know enough to try selecting one the
		// next time.
		if (osName.startsWith("Windows ") && (PCGenSettings.getBrowserPath() == null))
		{
			Utility.selectDefaultBrowser(null);
		}

		BrowserLauncher.openURL(url);

	}

	/**
	 * Add a keyboard shortcut to allow ESC to close the dialog.
	 * @param dialog The dialog to be updated.
	 */
	public static void installEscapeCloseOperation(final JDialog dialog)
	{
		Action dispatchClosing = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				dialog.dispatchEvent(new WindowEvent(dialog,
					WindowEvent.WINDOW_CLOSING));
			}
		};
		JRootPane root = dialog.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke,
			dispatchWindowClosingActionMapKey);
		root.getActionMap().put(dispatchWindowClosingActionMapKey,
			dispatchClosing);
	}

}
