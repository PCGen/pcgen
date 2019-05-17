/*
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
 */
package pcgen.gui2.plaf;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 * {@code UIFactory}.
 */
public final class LookAndFeelManager
{

	private static final String SYSTEM_LAF_CLASS = UIManager.getSystemLookAndFeelClassName();
	private static final String CROSS_LAF_CLASS = UIManager.getCrossPlatformLookAndFeelClassName();
	private static final LookAndFeelHandler[] LAF_HANDLERS;
	private static final Map<String, LookAndFeelHandler> LAF_MAP = new HashMap<>();

	static
	{
		LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();

		int length = lafInfo.length;
		LAF_HANDLERS = new LookAndFeelHandler[length];
		for (int i = 0; i < lafInfo.length; i++)
		{
			LookAndFeelInfo info = lafInfo[i];
			String name;
			String tooltip;
			if (info.getClassName().equals(SYSTEM_LAF_CLASS))
			{
				name = "System"; //TODO: internationalize this
				tooltip = "Sets the look to that of the System you are using";
			}
			else if (info.getClassName().equals(CROSS_LAF_CLASS))
			{
				name = "Java"; //TODO: internationalize this
				tooltip = "Sets the look to that of Java's cross platform look";
			}
			else
			{
				name = info.getName(); //TODO: internationalize this
				tooltip = "Sets the look to " + name + " look";
			}
			LookAndFeelHandler handler = new LookAndFeelHandler(name, info.getClassName(), tooltip);
			LAF_HANDLERS[i] = handler;
			LAF_MAP.put(name, handler);
		}
		UIManager.setInstalledLookAndFeels(lafInfo);
	}

	private static String selectedTheme = null;
	private static String currentLAF = null;

	private LookAndFeelManager()
	{
	}

	/**
	 * Initialise the look and feel to be used for this session. The look and 
	 * feel used will be the one saved in the preferences, or if none is 
	 * selected, nimbus will be used if present and the screen is a decent 
	 * size, otherwise Java will be used. Nimbus doesn't work so well on small 
	 * screens, hence the test for screen size.
	 */
	public static void initLookAndFeel()
	{
		//set Java as the fallback look and feel
		currentLAF = "Java";
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		LookAndFeelInfo nimbus = getNimbusLaf();
		if (screenSize.height > 800 && nimbus != null)
		{
			currentLAF = nimbus.getName();
		}
		String laf = ConfigurationSettings.initSystemProperty("lookAndFeel", currentLAF);
		selectedTheme = ConfigurationSettings.getSystemProperty("selectedThemePack");

		setLookAndFeel(laf);
	}

	private static LookAndFeelInfo getNimbusLaf()
	{
		LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		return Arrays.stream(lafInfo).filter(lookAndFeelInfo -> "nimbus".equalsIgnoreCase(lookAndFeelInfo.getName()))
			.findFirst().orElse(null);
	}

	public static Action[] getActions()
	{
		return LAF_HANDLERS;
	}

	public static String getCurrentThemePack()
	{
		return ConfigurationSettings.getSystemProperty("selectedThemePack");
	}

	public static String getCurrentLAF()
	{
		return ConfigurationSettings.getSystemProperty("lookAndFeel");
	}

	public static void setSelectedThemePack(String themePack)
	{
		selectedTheme = themePack;
		ConfigurationSettings.setSystemProperty("selectedThemePack", selectedTheme);
	}

	public static void setLookAndFeel(String name)
	{
		LookAndFeelHandler handler = LAF_MAP.get(name);
		if (handler == null)
		{
			Logging.errorPrint("Look and Feel " + name + " cannot be found");
			return;
		}
		String className = handler.getClassName();
		try
		{
			UIManager.setLookAndFeel(className);
			ConfigurationSettings.setSystemProperty("lookAndFeel", name);
			currentLAF = name;
		}
		catch (Exception ex)
		{
			setLookAndFeel(currentLAF);
		}
	}

	public static class LookAndFeelHandler extends AbstractAction
	{

		private final String className;

		LookAndFeelHandler(String name, String className, String tooltip)
		{
			super(name);
			this.className = className;
			putValue(SHORT_DESCRIPTION, tooltip);
		}

		public String getClassName()
		{
			return className;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			//This is the default operation
			String name = (String) getValue(NAME);
			ConfigurationSettings.setSystemProperty("lookAndFeel", name);
		}

	}

}
