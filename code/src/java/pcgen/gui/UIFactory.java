/*
 * UIFactory.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on xxxx xx, xxxx, xx:xx PM
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.SkinManager;
import pcgen.util.Logging;
import pcgen.util.SkinLFResourceChecker;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.lang.SystemUtils;

/**
 * <code>UIFactory</code>.
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public final class UIFactory
{
	private static String[][] lafData;
	private static final int NAME = 0;
	private static final int CLASSNAME = 1;
	private static final int TOOLTIP = 2;
	private static final int crossPlatformIndex = 1;
	private static final boolean windowsPlatform = SystemUtils.IS_OS_WINDOWS;
	//private static final boolean macPlatform = SystemUtils.IS_OS_MAC;

	static
	{
		// Add the Kunststoff L&F before asking the UIManager.
		UIManager.installLookAndFeel("Kunststoff", "com.incors.plaf.kunststoff.KunststoffLookAndFeel");

		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();

		lafData = new String[lafInfo.length][3];

		lafData[0][NAME] = "System";
		lafData[0][CLASSNAME] = UIManager.getSystemLookAndFeelClassName();
		lafData[0][TOOLTIP] = "Sets the look to that of the System you are using";

		int j = 1;

		if (!lafData[0][CLASSNAME].equals(UIManager.getCrossPlatformLookAndFeelClassName()))
		{
			lafData[1][NAME] = "Java";
			lafData[1][CLASSNAME] = UIManager.getCrossPlatformLookAndFeelClassName();
			lafData[1][TOOLTIP] = "Sets the look to that of Java's cross platform look";
			j++;
		}

		for (int i = 0; (i < lafInfo.length) && (j < lafData.length); i++)
		{
			lafData[j][CLASSNAME] = lafInfo[i].getClassName();

			if (!lafData[j][CLASSNAME].equals(UIManager.getSystemLookAndFeelClassName())
				&& !lafData[j][CLASSNAME].equals(UIManager.getCrossPlatformLookAndFeelClassName()))
			{
				lafData[j][NAME] = lafInfo[i].getName();
				lafData[j][TOOLTIP] = "Sets the look to " + lafData[j][NAME] + " look";
				j++;
			}
		}

		if (!isWindowsPlatform())
		{
			// Replace the broken Windows L&F which will
			// only run on M$ platforms with one that will
			// run everywhere.  No difference otherwise.
			for (int i = 0; i < lafInfo.length; ++i)
			{
				if (lafInfo[i].getClassName().endsWith("WindowsLookAndFeel"))
				{
					lafInfo[i] = new UIManager.LookAndFeelInfo(lafInfo[i].getName(), FakeWindowsLookAndFeel.class.getName());

					break;
				}
			}
		}

		UIManager.setInstalledLookAndFeels(lafInfo);

		if (!isWindowsPlatform())
		{
			for (int i = 0; i < lafData.length; ++i)
			{
				if (lafData[i][1].endsWith("WindowsLookAndFeel"))
				{
					lafData[i][1] = FakeWindowsLookAndFeel.class.getName();

					break;
				}
			}
		}
	}

	/**
	 * Set the look and feel.
	 * 
	 * @param looknfeel look and feel array index.
	 */
	public static void setLookAndFeel(int looknfeel)
	{
		internalSetLookAndFeel(Integer.valueOf(looknfeel));
	}

	/**
	 * Returns the number of elements in look and feel array.
	 * 
	 * @return number of elements in look and feel array.
	 */
	public static int getLookAndFeelCount()
	{
		return lafData.length;
	}

	/**
	 * Get the look and feel name in the look and feel array at index.
	 * 
	 * @param index index into the look and feel array.
	 * @return look and feel name.
	 */
	public static String getLookAndFeelName(int index)
	{
		if (index == lafData.length)
		{
			return "Skinned";
		}
		return lafData[index][NAME];
	}

	/**
	 * Get the tooltip text in the look at feel array at index.
	 * 
	 * @param index index into the look and feel array.
	 * @return Tooltip text.
	 */
	public static String getLookAndFeelTooltip(int index)
	{
		if (index == lafData.length)
		{
			return "Sets the look to skinned";
		}
		return lafData[index][TOOLTIP];
	}

	/**
	 * Returns true if it is a windows UI.
	 * @return boolean 
	 */
	public static boolean isWindowsUI()
	{
		final String lnfName = getLookAndFeelName(SettingsHandler.getLookAndFeel());

		return (lnfName.equals("Windows") || (lnfName.equals("System") && isWindowsPlatform()));
	}

	/**
	 * Return the index of the cross platform look and feel.
	 * 
	 * @return cross platform index.
	 */
	public static int indexOfCrossPlatformLookAndFeel()
	{
		return crossPlatformIndex;
	}

	/**
	 * Initialize the look and feel of the interface.
	 */
	public static void initLookAndFeel()
	{
		if (SettingsHandler.getLookAndFeel() < lafData.length)
		{
			internalSetLookAndFeel(Integer.valueOf(SettingsHandler.getLookAndFeel()));
		}
		else if (SettingsHandler.getLookAndFeel() == lafData.length)
		{
			try
			{
				//to get this case you should have already had skinlf.jar installed...
				if (SkinLFResourceChecker.getMissingResourceCount() == 0)
				{
					SkinManager.applySkin();

					//but just to be safe...
				}
				else
				{
					Logging.errorPrint(SkinLFResourceChecker.getMissingResourceMessage());
					internalSetLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
				}
			}
			catch (Exception e)
			{
				SettingsHandler.setLookAndFeel(0);
				internalSetLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
				ShowMessageDelegate.showMessageDialog("There was a problem setting the skinned look and feel.\n"
				+ "The look and feel has been reset to cross-platform.\nError: " + e.toString(),
					"PCGen", MessageType.ERROR);
			}
		}
		else
		{
			SettingsHandler.setLookAndFeel(0);
			internalSetLookAndFeel(lafData[crossPlatformIndex][CLASSNAME]);
		}
	}

	/**
	 * Refreshes the current look and feel.  Sometimes this is necessary for UI
	 * changes that need (e.g., anti-aliasing changes) even when the LAF has not
	 * changed.  Note that there may be some delay in the components reflecting
	 * the update.
	 *
	 * TODO Is there a bug in the refresh that AA takes so long?
	 */
	private static void refreshFullUI()
	{
		SwingUtilities.updateComponentTreeUI(Globals.getRootFrame());
	}

	private static void internalSetLookAndFeel(Object looknfeel)
	{
		try
		{
			if (looknfeel instanceof String)
			{
				UIManager.setLookAndFeel((String) looknfeel);
			}
			else if (looknfeel instanceof LookAndFeel)
			{
				UIManager.setLookAndFeel((LookAndFeel) looknfeel);
			}
			else if (looknfeel instanceof Integer)
			{
				UIManager.setLookAndFeel(lafData[((Integer) looknfeel).intValue()][CLASSNAME]);
			}

			// Fix colors; themes which inherit from
			// MetalTheme change the colors because it's a
			// static member of MetalTheme (!), so when you
			// change back & forth, colors get wonked.
			final LookAndFeel laf = UIManager.getLookAndFeel();

			if (laf instanceof MetalLookAndFeel)
			{
				MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
			}

			// Expect exception for updating helpMenu before
			// it exists.
			//PCGen_Frame1.getInst().getPcgenMenuBar().separateHelpMenu(!isWindowsUI());
		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			Logging.errorPrint("Exception in UIFactory::internalSetLookAndFeel", e);
		}

		refreshFullUI();
	}

	private static boolean isWindowsPlatform()
	{
		return windowsPlatform;
	}
}
