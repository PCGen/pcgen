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
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import pcgen.system.PCGenSettings;

import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Contract;

/**
 * Convenience methods from various sources.
 *
 * @author &lt;a href="mailto:binkley@alumni.rice.edu"&gt;B. K. Oxley (binkley)&lt;/a&gt;
 */
public final class Utility
{

	private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(
			KeyEvent.VK_ESCAPE, 0);

	/**
	 * An action map key for the user requesting a dialog close via the ESC key.
	 */
	private static final String dispatchWindowClosingActionMapKey =
			"pcgen:WINDOW_CLOSING"; //$NON-NLS-1$

	private Utility()
	{
	}

	/**
	 * Set up GridBag Constraints.
	 *
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
	 *
	 * @param gbc    The gridbagconstraints to set up
	 * @param gx     cols from left (left-most col for multi-column cell)
	 * @param gy     rows from top (top-most row for multi-row cell)
	 * @param gw     cols wide
	 * @param gh     rows high
	 * @param wx     weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy     weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 * @param fill   How should the component be resized if smaller than the space.
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
	 * @param gbc    The gridbagconstraints to set up
	 * @param gw     cols wide
	 * @param gh     rows high
	 * @param wx     weight of x, I typically put in percentile, only need to specify this once for each column, other values in same column are 0.0
	 * @param wy     weight of y, same as weight for cols, just specify a non-zero value for one cell in each row.
	 * @param fill   How should the component be resized if smaller than the space.
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
	 * Centers a {@code Component} to the screen.
	 *
	 * @param dialog JDialog dialog to center
	 */
	public static void centerComponent(Component dialog)
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

		dialog.setLocation(screenSize.x + ((screenSize.width - dialogSize.width) / 2),
		                   screenSize.y + ((screenSize.height - dialogSize.height) / 2));
	}

	/**
	 * Update the size of the dialog to ensure it will fit on the screen.
	 *
	 * @param dialog The dialog to be resized.
	 */
	public static void resizeComponentToScreen(Component dialog)
	{
		// Get the maximum window size to account for taskbars etc
		Rectangle screenBounds =
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getMaximumWindowBounds();

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
	 * Centres the dialog over the component ensuring that the dialog will be
	 * within the usable area of the screen (i.e. excluding native task bars,
	 * menus bars etc).
	 *
	 * @param parent The component over which the dialog should be centred.
	 * @param dialog The dialog to be positioned.
	 */
	public static void setComponentRelativeLocation(Component parent, Component dialog)
	{
		// First make sure it is not too big
		resizeComponentToScreen(dialog);

		// Get the maximum window size to account for taskbars etc
		Rectangle screenBounds =
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getMaximumWindowBounds();
		Point centreOfParent = new Point(parent.getWidth() / 2, parent.getHeight() / 2);
		SwingUtilities.convertPointToScreen(centreOfParent, parent);
		// Default to centre of parent
		Point location =
				new Point(centreOfParent.x - (dialog.getWidth() / 2),
				          centreOfParent.y - (dialog.getHeight() / 2));
		// Adjust so it fits on the screen
		if ((location.x + dialog.getWidth()) > (screenBounds.width
				                                        + screenBounds.x))
		{
			location.x -=
					(location.x + dialog.getWidth())
							- (screenBounds.width + screenBounds.x);
		}
		if (location.x < screenBounds.x)
		{
			location.x = screenBounds.x;
		}
		if ((location.y + dialog.getHeight()) > (screenBounds.height
				                                         + screenBounds.y))
		{
			location.y -=
					(location.y + dialog.getHeight())
							- (screenBounds.height + screenBounds.y);
		}
		if (location.y < screenBounds.y)
		{
			location.y = screenBounds.y;
		}
		dialog.setLocation(location);
	}

	/**
	 * Centers a {@code JFrame} to the screen.
	 *
	 * @param frame   JFrame frame to center
	 * @param isPopup boolean is the frame a popup dialog?
	 */
	public static void centerComponent(Component frame, boolean isPopup)
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

		frame.setLocation(screenSize.x + ((screenSize.width - frameSize.width) / 2),
		                  screenSize.y + ((screenSize.height - frameSize.height) / 2));
	}

	/**
	 * Sets the default browser.
	 *
	 * @param parent The component to show the dialog over.
	 */
	public static void selectDefaultBrowser(Component parent)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your preferred html browser.");

		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
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
	 * @throws IOException if file doesn't exist
	 * @see DesktopBrowserLauncher
	 */
	public static void viewInBrowser(String url) throws IOException
	{
		viewInBrowser(new URL(url));
	}

	/**
	 * View a file (should be browsable) in a browser.
	 *
	 * @param file Path of the file to display in browser.
	 * @throws IOException if file doesn't exist
	 * @see DesktopBrowserLauncher
	 */
	public static void viewInBrowser(File file) throws IOException
	{
		viewInBrowser(file.toURI());
	}

	/**
	 * View a URL in a browser
	 *
	 * @param url URL to display in browser.
	 * @throws IOException if the URL is bad or the browser can not be launched
	 * @see DesktopBrowserLauncher
	 */
	static void viewInBrowser(URL url) throws IOException
	{
		try
		{
			viewInBrowser(url.toURI());
		}
		catch (final URISyntaxException e)
		{
			throw new MalformedURLException(e.getMessage());
		}
	}


	/**
	 * View a URI in a browser.
	 *
	 * @param uri URI to display in browser.
	 * @throws IOException if browser can not be launched
	 * @see DesktopBrowserLauncher
	 */
	private static void viewInBrowser(URI uri) throws IOException
	{
		// Windows tends to lock up or not actually
		// display anything unless we've specified a
		// default browser, so at least make the user
		// aware that (s)he needs one. If they don't
		// pick one and it doesn't work, at least they
		// might know enough to try selecting one the
		// next time.
		if (!DesktopBrowserLauncher.isBrowseSupported()
				&& SystemUtils.IS_OS_WINDOWS
				&& (PCGenSettings.getBrowserPath() == null))
		{
			selectDefaultBrowser(null);
		}

		DesktopBrowserLauncher.browse(uri);

	}

	/**
	 * Add a keyboard shortcut to allow ESC to close the dialog.
	 *
	 * @param dialog The dialog to be updated.
	 */
	public static void installEscapeCloseOperation(final JDialog dialog)
	{
		JRootPane root = dialog.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke,
		                                                        dispatchWindowClosingActionMapKey);
		Action dispatchClosing = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				dialog.dispatchEvent(new WindowEvent(dialog,
				                                     WindowEvent.WINDOW_CLOSING));
			}
		};
		root.getActionMap().put(dispatchWindowClosingActionMapKey,
		                        dispatchClosing);
	}

	/**
	 * Adjust the crop rectangle to fit within the image it is cropping. Also
	 * ensure the area is square.
	 *
	 * @param image    The image being cropped
	 * @param cropRect The rectangle defining the cropping area. This may be updated.
	 */
	public static void adjustRectToFitImage(RenderedImage image, Rectangle cropRect)
	{
		// Make sure the rectangle is not too big
		if (cropRect.width > image.getWidth())
		{
			cropRect.width = image.getWidth();
		}
		if (cropRect.height > image.getHeight())
		{
			cropRect.height = image.getHeight();
		}

		// Make it square
		int dimension = Math.min(cropRect.width, cropRect.height);
		cropRect.setSize(dimension, dimension);

		// Now adjust the origin point so the box is within the image 
		if ((cropRect.x + cropRect.width) > image.getWidth())
		{
			cropRect.x = image.getWidth() - cropRect.width;
		}
		if ((cropRect.y + cropRect.height) > image.getHeight())
		{
			cropRect.y = image.getHeight() - cropRect.height;
		}
	}

	/**
	 * This method is used to set the name of the application for the window manager, especially X11.
	 *
	 * @param title Title to use
	 */
	public static void setApplicationTitle(String title)
	{
		Toolkit xToolkit = Toolkit.getDefaultToolkit();

		try
		{
			Field awtAppClassNameField =
					xToolkit.getClass().getDeclaredField("awtAppClassName"); //$NON-NLS-1$
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, title);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			// Rather than do a OS system condition, just ignore this expected exception
			//Logging.log(Level.FINEST, "Can not set name of application for window manager", e);
		}
	}

	/**
	 * Trim a string from the left to fit within the specified width.
	 *
	 * @param fm       The font the text will be rendered in.
	 * @param str   The string to trimmed.
	 * @param maxWidth The maximum width that the string is allowed to be.
	 * @return String The trimmed string.
	 */
	public static String shortenString(FontMetrics fm, String str, int maxWidth)
	{
		for (int i = str.length(); i > 0; i -= 5)
		{
			String shortedString = "..." + str.substring(str.length() - i);

			int width = fm.stringWidth(shortedString);
			//System.out.println("testing '"+foo+"' = "+width);
			if (width < maxWidth)
			{
				return shortedString;
			}
		}
		return "";
	}

	/**
	 * Get the tabbed pane for a component
	 *
	 * @param c
	 * @return the tabbed pane for a component
	 */
	@Contract("null -> null")
	public static JTabbedPane getTabbedPaneFor(Component c)
	{
		if (c == null)
		{
			return null;
		}
		if (c instanceof JTabbedPane)
		{
			return (JTabbedPane) c;
		}
		return getTabbedPaneFor(c.getParent());
	}
}
