package pcgen.gui.utils;

/*
 * SkinManager.java
 * Copyright 2001 (C) Jason Buchanan
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
 * Created on January 3, 2002
 */
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * <code>SkinManager</code> ???
 *
 * @author Jason Buchanan
 * @version $Revision$
 */
public final class SkinManager
{
	/**
	 * Apply a skin to PCGen GUI
	 * @throws Exception
	 */
	public static void applySkin() throws Exception
	{
		try
		{
			SkinLookAndFeel.setSkin(SkinLookAndFeel
				.loadThemePack(SettingsHandler.getSkinLFThemePack()));

			SkinLookAndFeel lnf = new SkinLookAndFeel();
			UIManager.setLookAndFeel(lnf);
			SwingUtilities.updateComponentTreeUI(Globals.getRootFrame());
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * Load a LINUX skin
	 * @throws Exception
	 */
	public static void loadLinuxSkin() throws Exception
	{
		try
		{
			SkinLookAndFeel.loadThemePack(SettingsHandler
				.getPcgenThemePackDir().getAbsolutePath()
				+ "themepack.zip");
			UIManager.installLookAndFeel("Linux",
				"com.l2fprod.gui.plaf.skin.LinuxLookAndFeel");
		}
		catch (Exception e)
		{
			throw e;
		}
	}
}
