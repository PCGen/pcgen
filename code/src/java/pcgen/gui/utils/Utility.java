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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import pcgen.core.SettingsHandler;
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
	 * Flip the state of tooltips to that described by the global isToolTipTextShown.
	 */
	public static void handleToolTipShownStateChange()
	{
		ToolTipManager.sharedInstance().setEnabled(
			SettingsHandler.isToolTipTextShown());
	}

}
