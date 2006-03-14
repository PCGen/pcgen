/*
 * Names.java
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
 *
 * $Id$
 */
package pcgen.core;

import pcgen.core.prereq.PrereqHandler;
import pcgen.gui.NameElement;
import pcgen.util.Logging;

import java.io.*;
import java.util.*;

/**
 * <code>Names</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision$
 */
public final class Names
{
	private static final Names theInstance = new Names();
	private static final String TAB_CHARACTER = "\t";
	//private static final File NAMES_DIRECTORY = new File(SettingsHandler.getPcgenSystemDir() + File.separator + "bio"
	//	+ File.separator + "names" + File.separator);
	private final List ruleDefinitions = new ArrayList();
	private final Map allTheSyllablesForEachRule = new HashMap(); // this is a map of syllable name to list of possible syllables.
	private File sourceFile;

	private PlayerCharacter pc = null;

	//don't ever call this, ya hear?
	private Names()
	{
	    // Empty Constructor
	}

	/**
	 * make sure you init this instance after getting access to it.
	 * @return Names
	 */
	public static Names getInstance()
	{
		return theInstance;
	}

	public static List findAllNamesFiles()
	{
		return findAllNamesFiles(SettingsHandler.getPcgenSystemDir());
	}

	public static List findAllNamesFiles(File dir)
	{
		final File[] fileNames = dir.listFiles(new NamFilter());

		final List result = new ArrayList();

		for (int i = 0; i < fileNames.length; ++i)
		{
			String name = fileNames[i].getName().substring(0, fileNames[i].getName().length() - 4);
			result.add(new NameElement(fileNames[i], name));
		}

		final File[] directories = dir.listFiles(new DirectoryFilter());
		for (int i = 0; i < directories.length; ++i)
		{
			result.addAll(findAllNamesFiles(directories[i]));
		}

		return result;
	}

	/**
	 * This randomly generates a name based on the current name file.
	 * @throws RuntimeException if init() has not been called with a valid name file.
	 * @return random name
	 */
	public String getRandomName()
	{
		final NameRule ruleToUse = chooseARandomRule(buildTheRuleSyllableMapping());

		if (ruleToUse == null)
		{
			Logging.errorPrint("Couldn't find a name rule to use.");
			throw new RuntimeException("No random name available. Try again.");
		}

		return constructTheName(ruleToUse);
	}

	public void init(final NameElement name, final PlayerCharacter aPc)
	{
		this.pc = aPc;
		clearAllRules();
		parseFile(name.getSource());
	}

	//should be private but the NamesTest.java makes a call to this so it is package-private for testing
	String[] getRuleDefinitions()
	{
		return (String[]) ruleDefinitions.toArray(new String[ruleDefinitions.size()]);
	}

	//should be private but the NamesTest.java makes a call to this so it is package-private for testing
	String[] getSyllablesByName(final String name)
	{
		final List syllables = (List) allTheSyllablesForEachRule.get(name);

		return (String[]) syllables.toArray(new String[syllables.size()]);
	}

	private List buildTheRuleSyllableMapping()
	{
		final List rules = new ArrayList();

		if (getRuleDefinitions().length > 0)
		{
			for (int i = 0; i < getRuleDefinitions().length; ++i)
			{
				final String rule = getRuleDefinitions()[i];
				final StringTokenizer newlineStr = new StringTokenizer(rule, TAB_CHARACTER, false);

				//the first token is the "chance" for this rule...
				final NameRule newRule = new NameRule(Integer.parseInt(newlineStr.nextToken()));

				// then we add all the syllable names to the list for this rule.
				while (newlineStr.hasMoreTokens())
				{
					final String syllableName = newlineStr.nextToken();
					newRule.addSyllable(syllableName);
				}

				rules.add(newRule);
			}
		}

		return rules;
	}

	private static NameRule chooseARandomRule(final List rules)
	{
		NameRule ruleToUse = null;
		final int roll;
		int y;
		roll = RollingMethods.roll(1, 100);

		for (y = 0; y < rules.size(); ++y)
		{
			if (roll <= ((NameRule) rules.get(y)).getChance())
			{
				ruleToUse = (NameRule) rules.get(y);

				break;
			}
		}

		return ruleToUse;
	}

	private String getRandomSyllableByName(final String name)
	{
		if (getSyllablesByName(name) != null)
		{
			if (getSyllablesByName(name).length > 0)
			{
				final int roll = RollingMethods.roll(1, getSyllablesByName(name).length);

				return getSyllablesByName(name)[roll - 1];
			}
		}

		return "";
	}

	private void clearAllRules()
	{
		ruleDefinitions.clear();
		allTheSyllablesForEachRule.clear();
		sourceFile = null;
	}

	private String constructTheName(final NameRule ruleToUse)
	{
		final StringBuffer buf = new StringBuffer(30);
		final String[] ruleSyllables = ruleToUse.getRuleSyllables();

		for (int i = 0; i < ruleSyllables.length; ++i)
		{
			if (ruleSyllables[i].startsWith("[{") && ruleSyllables[i].endsWith("}]"))
			{
				final String fileName = ruleSyllables[i].substring(2, ruleSyllables[i].length() - 2);
				File newFile = new File(sourceFile.getPath() + File.separator + fileName + ".nam");
				final Names otherFile = new Names();
				otherFile.init(new NameElement(newFile, fileName), pc);

				final String Name = otherFile.getRandomName();
				buf.append(Name);
			}
			else
			{
				buf.append(getRandomSyllableByName(ruleSyllables[i]));
			}
		}

		return buf.toString();
	}

	private void parseFile(final File aSourceFile)
	{
		String currentLine = "";

		BufferedReader br = null;

		int lineNumber = 0;

		try
		{
			String currentSyllable = null;

			boolean canWrite = true;
			boolean inRulesSection = false;

			br = new BufferedReader(new InputStreamReader(new FileInputStream(aSourceFile)));

			while ((currentLine = br.readLine()) != null)
			{
				++lineNumber;

				if (((currentLine.length() > 0) && (currentLine.charAt(0) == '#')) || currentLine.startsWith("//")
				    || "".equals(currentLine.trim()))
				{
					continue;
				}

				if (currentLine.startsWith("[/PRE]"))
				{
					canWrite = true;
				}

				if (currentLine.startsWith("[PRE") && (currentLine.indexOf(':') >= 0))
				{
					final StringTokenizer tabTok = new StringTokenizer(currentLine.substring(1, currentLine.length()
						        - 1), "\t", false);
					final List aList = new ArrayList();

					while (tabTok.hasMoreTokens())
					{
						aList.add(tabTok.nextToken());
					}
					canWrite = PrereqHandler.passesAll(aList, pc, null);
					continue;
				}

				if (!canWrite)
				{
					if ((currentLine.length() > 0) && (currentLine.charAt(0) == '['))
					{
						System.err.println("Line #" + Integer.toString(lineNumber) + " prereqs not met: " + currentLine);
					}
					continue;
				}

				if (currentLine.startsWith("[RULES]"))
				{
					inRulesSection = true;

					continue;
				}

				if ((currentLine.length() > 0) && (currentLine.charAt(0) == '['))
				{
					inRulesSection = false;
				}

				if (inRulesSection)
				{
					ruleDefinitions.add(currentLine);

					continue;
				}

				//This is where the syllable types are saved to sylRuleList and the list themselves
				//are read into a corresponding list.
				if (((currentLine.length() > 0) && (currentLine.charAt(0) == '[')) && currentLine.endsWith("]"))
				{
					currentSyllable = currentLine;
					allTheSyllablesForEachRule.put(currentLine, new ArrayList());

					continue;
				}

				// if we make it here, then we actually have a syllable fragment in hand.
				((List) allTheSyllablesForEachRule.get(currentSyllable)).add(currentLine);
			}
		}
		catch (FileNotFoundException exception)
		{
			if (!"pcgen.ini".equals(aSourceFile.getName()))
			{
				Logging.errorPrint("ERROR:" + aSourceFile.getName() + " error " + currentLine + " Exception type:"
				    + exception.getClass().getName() + " Message:" + exception.getMessage(), exception);
			}
		}
		catch (IOException exception)
		{
			if (!("pcgen.ini".equals(aSourceFile.getName())))
			{
				Logging.errorPrint("ERROR:" + aSourceFile.getName() + " error " + currentLine + " Exception type:"
				    + exception.getClass().getName() + " Message:" + exception.getMessage(), exception);
			}
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException ignore)
				{
					Logging.errorPrint("Couldn't close the reader for " + aSourceFile.getName(), ignore);
				}
			}
		}
	}

	private static class DirectoryFilter implements FileFilter
	{
		public boolean accept(final File file)
		{
			if (file.isDirectory())
			{
				return true;
			}

			return false;
		}
	}

	private static class NamFilter implements FilenameFilter
	{
		public boolean accept(final File dir, final String name)
		{
			if (name.toLowerCase().endsWith(".nam"))
			{
				return true;
			}

			return false;
		}
	}
}
