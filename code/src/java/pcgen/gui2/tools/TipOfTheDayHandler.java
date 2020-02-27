/*
 * Copyright James Dempsey, 2012
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
 *
 */
package pcgen.gui2.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.SettingsHandler;
import pcgen.gui2.UIPropertyContext;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PropertyContext;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

/**
 * The singleton class {@code TipOfTheDayHandler} manages the list of tips.
 */
public final class TipOfTheDayHandler
{
	private static final PropertyContext PROPERTY_CONTEXT = UIPropertyContext.createContext("TipOfTheDay");

	private static TipOfTheDayHandler INSTANCE = null;

	private List<String> tipList = null;
	private int lastNumber;

	/**
	 * Create a new instance of TipOfTheDayHandler
	 */
	private TipOfTheDayHandler()
	{
		lastNumber = PROPERTY_CONTEXT.initInt("lastTip", -1);
	}

	public static synchronized TipOfTheDayHandler getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new TipOfTheDayHandler();
		}
		return INSTANCE;
	}

	/**
	 * @return the lastNumber
	 */
	public int getLastNumber()
	{
		return lastNumber;
	}

	public synchronized void loadTips()
	{
		tipList = new ArrayList<>(20);
		String systemDir = ConfigurationSettings.getSystemsDir();
		String tipsFileName = LanguageBundle.getString("in_tipsFileName"); //$NON-NLS-1$
		String tipsFileNameDefault = "tips.txt"; //$NON-NLS-1$
		final String tipsFilePath = systemDir + File.separator + "gameModes" + File.separator //$NON-NLS-1$
			+ SettingsHandler.getGameAsProperty().get().getName() + File.separator;
		final String tipsDefaultPath = systemDir + File.separator + "gameModes" + File.separator //$NON-NLS-1$
			+ "default" + File.separator; //$NON-NLS-1$
		String[] tipFiles = {tipsFilePath + tipsFileName, tipsDefaultPath + tipsFileName,
			tipsFilePath + tipsFileNameDefault, tipsDefaultPath + tipsFileNameDefault};

		boolean loaded = false;
		for (String path : tipFiles)
		{
			try
			{
				loadTipFile(path);
				Logging.log(Logging.INFO, "Loaded tips from " + path); //$NON-NLS-1$
				loaded = true;
				break;
			}
			catch (IOException e)
			{
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("Unable to load tips file " + path, e); //$NON-NLS-1$
				}
			}
		}

		if (!loaded)
		{
			Logging.errorPrint("Warning: game mode " + SettingsHandler.getGameAsProperty().get().getName()
				+ " is missing tips. Tried all of " + StringUtils.join(tipFiles, "\n"));
		}

	}

	private void loadTipFile(String tipsFilePath) throws IOException
	{
		final File tipsFile = new File(tipsFilePath);

		final char[] inputLine;
		try (Reader tipsReader = new BufferedReader(new InputStreamReader(new FileInputStream(tipsFile),
				StandardCharsets.UTF_8
		)))
		{
			final int length = (int) tipsFile.length();
			inputLine = new char[length];
			tipsReader.read(inputLine, 0, length);
		}

		final StringTokenizer aTok = new StringTokenizer(new String(inputLine), "\r\n", false);

		while (aTok.hasMoreTokens())
		{
			String line = aTok.nextToken();
			// Skip comments and blank lines.
			if (!line.trim().isEmpty() && (line.charAt(0) != LstFileLoader.LINE_COMMENT_CHAR))
			{
				tipList.add(line);
			}
		}
	}

	public synchronized boolean hasTips()
	{
		return (tipList != null) && (!tipList.isEmpty());
	}

	public synchronized String getNextTip()
	{
		if (hasTips())
		{
			if (++lastNumber >= tipList.size())
			{
				lastNumber = 0;
			}
			PROPERTY_CONTEXT.setInt("lastTip", lastNumber);

			return tipList.get(lastNumber);
		}

		return "";
	}

	public synchronized String getPrevTip()
	{
		if (hasTips())
		{
			if (--lastNumber < 0)
			{
				lastNumber = tipList.size() - 1;
			}
			PROPERTY_CONTEXT.setInt("lastTip", lastNumber);

			return tipList.get(lastNumber);
		}

		return "";
	}

	public static boolean shouldShowTipOfTheDay()
	{
		return PROPERTY_CONTEXT.getBoolean("showTipOfTheDay", true);
	}

}
