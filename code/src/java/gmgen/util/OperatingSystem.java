/*
 *  OperatingSystem.java - OS detection
 *  :noTabs=false:
 *
 *  Copyright (C) 2002, 2003 Slava Pestov
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.util;

import javax.swing.UIManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

/**
 *  Operating system detection routines.
 *
 *@author     Devon Jones
 *@since    May 30, 2003
 *@version    $Id: OperatingSystem.java,v 1.2 2003/05/28 04:57:48 soulcatcher
 *      Exp $
 *@since        GMGen 3.3
 */
public class OperatingSystem
{
	/** IS_JAVA_14 - whether or not Java 1.4 is being used */
	public static final boolean IS_JAVA_14;
	private static final int UNIX = 0x31337;

	private static final int WINDOWS_9x = 0x640;
	private static final int WINDOWS_NT = 0x666;
	private static final int OS2 = 0xDEAD;
	private static final int MAC_OS_X = 0xABC;
	private static final int VMS = 0xDEAD2;
	private static final int UNKNOWN = 0xBAD;
	private static int os;
	private static int hasScreenMenuBar = -1;

	static
	{
		if (System.getProperty("mrj.version") != null)
		{
			os = MAC_OS_X;
		}
		else
		{
			String osName = System.getProperty("os.name");

			if ((osName.indexOf("Windows 9") != -1) || (osName.indexOf("Windows M") != -1))
			{
				os = WINDOWS_9x;
			}
			else if (osName.indexOf("Windows") != -1)
			{
				os = WINDOWS_NT;
			}
			else if (osName.indexOf("OS/2") != -1)
			{
				os = OS2;
			}
			else if (osName.indexOf("VMS") != -1)
			{
				os = VMS;
			}
			else if (File.separatorChar == '/')
			{
				os = UNIX;
			}
			else
			{
				os = UNKNOWN;

				//Log.log(Log.WARNING, OperatingSystem.class, "Unknown operating system: " + osName);
			}
		}

		IS_JAVA_14 = (System.getProperty("java.version").compareTo("1.4") >= 0)
		    && (System.getProperty("jedit.nojava14") == null);
	}

	/**
	 *  Returns if we're running Windows 95/98/ME/NT/2000/XP, or OS/2.
	 *
	 *@return    if we're running Windows 95/98/ME/NT/2000/XP, or OS/2.
	 *@since        GMGen 3.3
	 */
	public static final boolean isDOSDerived()
	{
		return isWindows() || isOS2();
	}

	/**
	 *  Returns if we're running MacOS X.
	 *
	 *@return    if we're running MacOS X.
	 *@since        GMGen 3.3
	 */
	public static final boolean isMacOS()
	{
		return os == MAC_OS_X;
	}

	/**
	 *  Returns if we're running MacOS X and using the native look and feel.
	 *
	 *@return    if we're running MacOS X and using the native look and feel.
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final boolean isMacOSLF()
	{
		return (isMacOS() && UIManager.getLookAndFeel().isNativeLookAndFeel());
	}

	/**
	 *  Returns if we're running OS/2.
	 *
	 *@return    if we're running OS/2.
	 *@since        GMGen 3.3
	 */
	public static final boolean isOS2()
	{
		return os == OS2;
	}

	/**
	 *  Returns the bounds of the default screen.
	 *
	 *@return    the bounds of the default screen.
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final Rectangle getScreenBounds()
	{
		int screenX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int x;
		int y;
		int w;
		int h;

		if (isMacOS())
		{
			x = 0;
			y = 22;
			w = screenX;
			h = screenY - y - 4;

			//shadow size
		}
		else if (isWindows())
		{
			x = -4;
			y = -4;
			w = screenX - (2 * x);
			h = screenY - (2 * y);
		}
		else
		{
			x = 0;
			y = 0;
			w = screenX;
			h = screenY;
		}

		return new Rectangle(x, y, w, h);
	}

	/**
	 *  Returns if we're running Unix (this includes MacOS X).
	 *
	 *@return    if we're running Unix (this includes MacOS X).
	 *@since        GMGen 3.3
	 */
	public static final boolean isUnix()
	{
		return (os == UNIX) || (os == MAC_OS_X);
	}

	/**
	 * Returns if we're unning VMS.  Yah, right... pull the other one, it's got
	 * bells on it.
	 *
	 *@return    if we're running VMS.
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final boolean isVMS()
	{
		return os == VMS;
	}

	/**
	 *  Returns if we're running Windows 95/98/ME/NT/2000/XP.
	 *
	 *@return    if we're running Windows 95/98/ME/NT/2000/XP.
	 *@since        GMGen 3.3
	 */
	public static final boolean isWindows()
	{
		return (os == WINDOWS_9x) || (os == WINDOWS_NT);
	}

	/**
	 *  Returns if we're running Windows 95/98/ME.
	 *
	 *@return    if we're running Windows 95/98/ME.
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final boolean isWindows9x()
	{
		return os == WINDOWS_9x;
	}

	/**
	 *  Returns if we're running Windows NT/2000/XP.
	 *
	 *@return    if we're running Windows NT/2000/XP.
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final boolean isWindowsNT()
	{
		return os == WINDOWS_NT;
	}

	/**
	 *  Returns if Java 2 version 1.4 is in use.
	 *
	 *@return    if Java 2 v 1.4 is being used
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final boolean isJava14()
	{
		return IS_JAVA_14;
	}

	/**
	 *  Returns whether the screen menu bar on Mac OS X is in use.
	 *
	 *@return    whether the screen menu bar on Mac OS X is in use.
	 *@since        GMGen 3.3
	 *
	 * @deprecated Unused
	 */
	public static final boolean hasScreenMenuBar()
	{
		if (!isMacOS())
		{
			return false;
		}
		else if (hasScreenMenuBar == -1)
		{
			String result = System.getProperty("apple.laf.useScreenMenuBar");

			if (result == null)
			{
				result = System.getProperty("com.apple.macos.useScreenMenuBar");
			}

			hasScreenMenuBar = (result.equals("true")) ? 1 : 0;
		}

		return (hasScreenMenuBar == 1);
	}
}
