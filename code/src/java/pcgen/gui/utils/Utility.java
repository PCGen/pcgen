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
package pcgen.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.apache.commons.lang.SystemUtils;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * Convenience methods from various sources.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
public final class Utility
{

	/**
	 * Put the description everywhere it belongs.
	 *
	 * @param component JComponent the component
	 * @param description String tool tip, etc.
	 */
	public static void setDescription(JComponent component, String description)
	{
		/*
		 * replaced complex and mysterious code for toolTip handling
		 * with a simple call to ToolTipManager
		 *
		 * To binkley:
		 * Hey, if I broke something - I do apologize in advance.
		 * My IDE (Idea) said this method wasn't used very often
		 * so I thought I'd give the simpler code a try.
		 * Contact me at ravenlock@gmx.de if you want to discuss
		 * these changes
		 *
		 * author: Thomas Behr 03-10-02
		 */
		component.setToolTipText(description);
	}

	/**
	 *
	 * @param obj
	 * @param in_String
	 */
	public static void setGuiTextInfo(Object obj, String in_String)
	{
		String text = LanguageBundle.getString(in_String);

		setTextAndMnemonic(obj, text);

		String tooltipKey = in_String + "_tip";
		String tooltip = LanguageBundle.getString(tooltipKey);

		if (tooltip.length() > 0
			&& !tooltip.equals(tooltipKey + LanguageBundle.UNDEFINED)
			&& obj instanceof JComponent)
		{
			setDescription((JComponent) obj, tooltip);
		}
	}

	/**
	 *
	 * @param aObj
	 * @param parentName
	 * @return Container
	 */
	public static Container getParentNamed(Container aObj, String parentName)
	{
		while (aObj != null)
		{
			if (aObj.getClass().getName().equals(parentName))
			{
				break;
			}

			aObj = aObj.getParent();
		}

		return aObj;
	}

	/**
	 * Creates a temporary preview file for display.
	 * @param pc The pc to be previewed.
	 * @param equipmentTemplate the alternative template from the equipment sub tab
	 * @return temporary preview file
	 **/
	public static File getTempPreviewFile(PlayerCharacter pc,
		String equipmentTemplate)
	{
		// Karianna - Fix for bug 966281
		final String template;
		if (equipmentTemplate == null || equipmentTemplate.equals(""))
		{
			template = SettingsHandler.getSelectedCharacterHTMLOutputSheet(pc);
		}
		else
		{
			template = equipmentTemplate;
		}
		// Karianna - End of Fix for bug 966281

		if ((template == null) || (template.trim().length() == 0))
		{
			ShowMessageDelegate.showMessageDialog(
				"No HTML template specified in preferences.", "PCGen",
				MessageType.ERROR);
			return null;
		}

		// include . in extension
		String extension = template.substring(template.lastIndexOf('.'));
		File tempFile = null;

		try
		{
			// create a temporary file to view the character output
			tempFile =
					File.createTempFile(Constants.TEMPORARY_FILE_NAME, extension,
						SettingsHandler.getTempPath());
		}
		catch (IOException ioe)
		{
			ShowMessageDelegate.showMessageDialog(
				"Could not create temporary preview file.", "PCGen",
				MessageType.ERROR);
			Logging.errorPrint("Could not create temporary preview file.", ioe);
		}

		return tempFile;
	}

	/**
	 *
	 * @param obj
	 * @param text
	 */
	public static void setTextAndMnemonic(Object obj, String text)
	{
		if (obj instanceof JLabel)
		{
			((JLabel) obj).setText(text);

			return;
		}

		int textLength = text.length();
		int idx = 0;
		char mnemonic = '\0';

		for (;;)
		{
			idx = text.indexOf('&', idx);

			if (idx < 0)
			{
				break;
			}

			if (idx < (textLength - 1))
			{
				if (text.charAt(idx + 1) == '&')
				{
					idx += 1;
				}
				else
				{
					mnemonic = text.charAt(idx + 1);
				}

				text = text.substring(0, idx) + text.substring(idx + 1);
				textLength -= 1;
			}
		}

		if (obj instanceof JButton)
		{
			((JButton) obj).setText(text);

			if (mnemonic != '\0')
			{
				((JButton) obj).setMnemonic(mnemonic);
			}
		}
		else if (obj instanceof JMenuItem)
		{
			((JMenuItem) obj).setText(text);

			if (mnemonic != '\0')
			{
				((JMenuItem) obj).setMnemonic(mnemonic);
			}
		}
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
		final Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();

		final Dimension dialogSize = dialog.getSize();

		if (dialogSize.height > screenSize.height)
		{
			dialogSize.height = screenSize.height;
		}

		if (dialogSize.width > screenSize.width)
		{
			dialogSize.width = screenSize.width;
		}

		dialog.setLocation(screenSize.x + (screenSize.width - dialogSize.width) / 2,
				screenSize.y + (screenSize.height - dialogSize.height) / 2);
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
	 * Create a new button menu item with all the contaminant
	 * expectations fulfilled.
	 *
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JButtonMenuItem the new  button menu item
	 */
	public static JButton createButton(ActionListener listener, String command,
		String description, String iconName, boolean enable)
	{
		final JButton button = new JButton();

		// Work around old JDK bug on Windows
		button.setMargin(new Insets(0, 0, 0, 0));

		if (listener != null)
		{
			button.addActionListener(listener);
		}

		if (command != null)
		{
			button.setActionCommand(command);
		}

		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(button, description);
		}

		IconUtilitities.maybeSetIcon(button, iconName);

		button.setEnabled(enable);

		return button;
	}

	/**
	 * Create a new menu with all the contaminant expectation
	 * fulfilled.
	 *
	 * @param prop String property to lookup in LanguageBundle
	 * @param iconName String icon name
	 * @param enable boolean menu enabled?
	 *
	 * @return JMenu the new menu
	 */
	public static JMenu createMenu(final String prop, String iconName,
		boolean enable)
	{
		final String label = LanguageBundle.getString("in_" + prop);
		final char mnemonic = (char)LanguageBundle.getMnemonic("in_mn_" + prop);
		final String description =
				LanguageBundle.getString("in_" + prop + "Tip");

		return createMenu(label, mnemonic, description, iconName, enable);
	}

	/**
	 * Create a new menu with all the contaminant expectation
	 * fulfilled.
	 *
	 * @param label String what to display?
	 * @param mnemonic int menu shortcut key, <code>0</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean menu enabled?
	 *
	 * @return JMenu the new menu
	 */
	public static JMenu createMenu(String label, char mnemonic,
		String description, String iconName, boolean enable)
	{
		final JMenu menu = new JMenu(label);

		if (mnemonic != 0)
		{
			menu.setMnemonic(mnemonic);
		}

		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(menu, description);
		}

		IconUtilitities.maybeSetIcon(menu, iconName);
		menu.setEnabled(enable);

		return menu;
	}

	/**
	 * Create a new menu item with all the contaminant expectations
	 * fulfilled.
	 *
	 * @param prop String property to lookup in LanguageBundle
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param accelerator String keyboard shortcut key, <code>0</code> for none
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JMenuItem the new menu item
	 */
	public static JMenuItem createMenuItem(final String prop,
		final ActionListener listener, final String command,
		final String accelerator, final String iconName, final boolean enable)
	{
		String label = LanguageBundle.getString("in_" + prop);
		char mnemonic = (char)LanguageBundle.getMnemonic("in_mn_" + prop);
		String description = LanguageBundle.getString("in_" + prop + "Tip");

		return createMenuItem(label, listener, command, mnemonic, accelerator,
			description, iconName, enable);
	}

	/**
	 * Create a new menu item with all the contaminant expectations
	 * fulfilled.
	 *
	 * @param label String what to display?
	 * @param listener ActionListener what to do as code, <code>null</code> for none
	 * @param command String menu command, <code>null</code> for none
	 * @param mnemonic char menu shortcut key, <code>0</code> for none
	 * @param accelerator String keyboard shortcut key, <code>0</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 *
	 * @return JMenuItem the new menu item
	 */
	public static JMenuItem createMenuItem(final String label,
		final ActionListener listener, final String command,
		final char mnemonic, final String accelerator,
		final String description, final String iconName, final boolean enable)
	{
		final JMenuItem item = new JMenuItem(label);

		if (listener != null)
		{
			item.addActionListener(listener);
		}

		if (command != null)
		{
			item.setActionCommand(command);
		}

		if (mnemonic != '\0')
		{
			item.setMnemonic(mnemonic);
		}

		if (accelerator != null)
		{
			// accelerator has three possible forms:
			// 1) shortcut +
			// 2) shortcut-alt +
			// 3) F1
			// (error checking is for the weak!)
			int iShortCut = Event.CTRL_MASK;
			StringTokenizer aTok = new StringTokenizer(accelerator);

			// get the first argument
			String aString = aTok.nextToken();

			if (aString.equalsIgnoreCase("shortcut"))
			{
				iShortCut =
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			}
			else if (aString.equalsIgnoreCase("alt"))
			{
				if (System.getProperty("mrj.version") != null)
				{
					iShortCut =
							Toolkit.getDefaultToolkit()
								.getMenuShortcutKeyMask()
								| Event.ALT_MASK;
				}
				else
				{
					iShortCut = Event.ALT_MASK;
				}
			}
			else if (aString.equalsIgnoreCase("shift-shortcut"))
			{
				iShortCut =
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
							| Event.SHIFT_MASK;
			}

			if (aTok.hasMoreTokens())
			{
				// get the second argument
				aString = aTok.nextToken();
			}

			KeyStroke aKey = KeyStroke.getKeyStroke(aString);

			if (aKey != null)
			{
				int iKeyCode = aKey.getKeyCode();
				item
					.setAccelerator(KeyStroke.getKeyStroke(iKeyCode, iShortCut));
			}
		}

		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(item, description);
		}

		IconUtilitities.maybeSetIcon(item, iconName);
		item.setEnabled(enable);

		return item;
	}

	/**
	 * Create a new radio button menu item with all the
	 * contaminant expectations fulfilled.  You need to fiddle
	 * with <code>ButtonGroup</code> yourself.
	 *
	 * @param group ButtonGroup what button group,
	 * <code>null</code> for none
	 * @param label String what to display?
	 * @param listener ActionListener what to do as code,
	 * <code>null</code> for none
	 * @param command String menu command, <code>null</code> for
	 * none
	 * @param mnemonic char menu shortcut key, <code>0</code> for
	 * none
	 * @param accelerator String keyboard shortcut key,
	 * <code>null</code> for none
	 * @param description String tooltip
	 * @param iconName String icon name
	 * @param enable boolean item enabled?
	 * @return JRadioButtonMenuItemMenuItem the new radio button
	 * menu item
	 */
	public static JRadioButtonMenuItem createRadioButtonMenuItem(
		ButtonGroup group, String label, ActionListener listener,
		String command, char mnemonic, String accelerator, String description,
		String iconName, boolean enable)
	{
		final JRadioButtonMenuItem button = new JRadioButtonMenuItem(label);

		if (group != null)
		{
			group.add(button);
		}

		if (listener != null)
		{
			button.addActionListener(listener);
		}

		if (command != null)
		{
			button.setActionCommand(command);
		}

		if (mnemonic != 0)
		{
			button.setMnemonic(mnemonic);
		}

		if (accelerator != null)
		{
			button.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		}

		if (SettingsHandler.isToolTipTextShown())
		{
			setDescription(button, description);
		}

		IconUtilitities.maybeSetIcon(button, iconName);

		button.setEnabled(enable);

		return button;
	}

	/**
	 * Flip the state of tooltips to that described by the global isToolTipTextShown.
	 */
	public static void handleToolTipShownStateChange()
	{
		ToolTipManager.sharedInstance().setEnabled(
			SettingsHandler.isToolTipTextShown());
	}

	/**
	 * Prints the character or party details to the writer specified.
	 *
	 * @param w  The writer to print the data to.
	 * @param aFile
	 * @param aPC
	 * @throws IOException  If any problems occur in writing the data
	 **/
	public static void printToWriter(Writer w, String aFile, PlayerCharacter aPC)
		throws IOException
	{
		final File template = new File(aFile);

		if (!template.exists())
		{
			throw new IOException(aFile + " does not exist!");
		}

		String fileName = template.getName().toLowerCase();

		BufferedWriter bw = new BufferedWriter(w);

		if (fileName.startsWith(Constants.CHARACTER_TEMPLATE_PREFIX)
			|| fileName.startsWith(Constants.EQUIPMENT_TEMPLATE_PREFIX)
			|| fileName.endsWith(".xml"))
		{
			(new ExportHandler(template)).write(aPC, bw);
		}
		else if (fileName.startsWith(Constants.PARTY_TEMPLATE_PREFIX))
		{
			(new ExportHandler(template)).write(Globals.getPCList(), bw);
		}
		else
		{
			throw new IOException(fileName
				+ " is not a valid template file name.");
		}

		bw.close();
	}

	/**
	 * Sets the default browser.
	 * @param parent The component to show the dialog over.
	 */
	public static void selectDefaultBrowser(Component parent)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your preferred html browser.");

		if (SystemUtils.IS_OS_MAC)
		{
			// On MacOS X, do not traverse file bundles
			fc
				.putClientProperty("JFileChooser.appBundleIsTraversable",
					"never");
		}

		if (SettingsHandler.getBrowserPath() == null)
		{
			//No action, as we have no idea what a good default would be...
		}
		else
		{
			fc.setCurrentDirectory(new File(SettingsHandler.getBrowserPath()));
		}

		final int returnVal = fc.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			final File file = fc.getSelectedFile();
			SettingsHandler.setBrowserPath(file.getAbsolutePath());
		}
	}

	/**
	 * Strip everything between <> out.
	 * @param htmlIn The text to strip
	 * @return The stripped text.
	 */
	public static String stripHTML(String htmlIn)
	{
		String stringOut = htmlIn;

		while (stringOut.indexOf('<') >= 0)
		{
			stringOut =
					stringOut.substring(0, stringOut.indexOf('<'))
						+ stringOut.substring(stringOut.indexOf('>') + 1,
							stringOut.length());
		}

		return stringOut;
	}

	/**
	 * Method to encapsulate Toolkit.getScreenSize().  This method is a hack to fix a fault
	 * in the Linux implementation of Java 5.
	 * 
	 * @param toolkit 
	 * @return Dimension screen size.
	 * @see java.awt.Toolkit
	 */
	public static Dimension getScreenSize(Toolkit toolkit)
	{
		// since the Toolkit.getScreenSize() method is broken in the Linux implementation
		// of Java 5  (it returns double the screen size under xinerama), this method is
		// encapsulated to accomodate this with a hack. This only works for xinerama displays
		// with two equally sized 4:3 resoltion displays.
		// TODO: remove the hack, once Java fixed this.
		Dimension screen = toolkit.getScreenSize();

		if (screen.getWidth() * 3 == screen.getHeight() * 8)
		{
			screen.setSize(screen.getWidth() / 2, screen.getHeight());
		}

		return screen;
	}

	/**
	 * Trim a string from the left to fit within the specified width.
	 * @param fm The font the text will be rendered in.
	 * @param string The string to trimmed.
	 * @param maxWidth The maximum width that the string is allowed to be.
	 * @return String The trimmed string.
	 */
	public static String shortenString(FontMetrics fm, String string, int maxWidth)
	{
		for (int i=string.length() ; i>0 ; i-=5)
		{
			String foo = "..." + string.substring( string.length()-i);

			int width = fm.stringWidth(foo);
			//System.out.println("testing '"+foo+"' = "+width);
			if (width < maxWidth)
			{
				return foo;
			}
		}
		return "";
	}

}
