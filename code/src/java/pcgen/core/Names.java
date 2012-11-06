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

import org.apache.commons.lang.StringUtils;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.gui.NameElement;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.io.*;
import java.util.*;
import pcgen.system.ConfigurationSettings;

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
    private final List<String> ruleDefinitions = new ArrayList<String>();
    private final Map<String, List<String>> allTheSyllablesForEachRule = new HashMap<String, List<String>>(); // this is a map of syllable name to list of possible syllables.
    private File sourceFile;

    private PlayerCharacter pc = null;

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

    public static List<NameElement> findAllNamesFiles()
    {
        return findAllNamesFiles(new File(ConfigurationSettings.getSystemsDir()));
    }

    public static List<NameElement> findAllNamesFiles(File dir)
    {
        final File[] fileNames = dir.listFiles(new NamFilter());

        final List<NameElement> result = new ArrayList<NameElement>();

        for (File fileName : fileNames)
        {
            String name = fileName.getName().substring(0, fileName.getName().length() - 4);
            result.add(new NameElement(fileName, name));
        }

        final File[] directories = dir.listFiles(new DirectoryFilter());
        for (File directory : directories)
        {
            result.addAll(findAllNamesFiles(directory));
        }

        return result;
    }

    /**
     * This randomly generates a name based on the current name file.
     * @return random name
     * @throws RuntimeException if init() has not been called with a valid name file.
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

    public List<String> getRandomNames(final int count)
    {
        final List<String> names = new ArrayList<String>(count);
        for(int i=0; i< count;i++){
            names.add(getRandomName());
        }
        return names;
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
        return ruleDefinitions.toArray(new String[ruleDefinitions.size()]);
    }

    //should be private but the NamesTest.java makes a call to this so it is package-private for testing
    String[] getSyllablesByName(final String name)
    {
        final List<String> syllables = allTheSyllablesForEachRule.get(name);

        return syllables.toArray(new String[syllables.size()]);
    }

    private List<NameRule> buildTheRuleSyllableMapping()
    {
        final List<NameRule> rules = new ArrayList<NameRule>();

        final String[] ruleDefs = getRuleDefinitions();
        if (ruleDefs.length > 0)
        {
            for (final String rule : ruleDefs)
            {
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

    private static NameRule chooseARandomRule(final List<NameRule> rules)
    {
        NameRule ruleToUse = null;

        final int roll = RollingMethods.roll(1, 100);

        for (NameRule rule : rules)
        {
            if (roll <= rule.getChance())
            {
                ruleToUse = rule;

                break;
            }
        }

        return ruleToUse;
    }

    private String getRandomSyllableByName(final String name)
    {
        final String[] syllables = getSyllablesByName(name);
        if (syllables != null)
        {
            if (syllables.length > 0)
            {
                final int roll = RollingMethods.roll(1, syllables.length);

                return syllables[roll - 1];
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

        for (String ruleSyllable : ruleSyllables) {
            if (ruleSyllable.startsWith("[{") && ruleSyllable.endsWith("}]"))
            {
                final String fileName = ruleSyllable.substring(2, ruleSyllable.length() - 2);
                File newFile = new File(sourceFile.getPath() + File.separator + fileName + ".nam");
                final Names otherFile = new Names();
                otherFile.init(new NameElement(newFile, fileName), pc);

                final String Name = otherFile.getRandomName();
                buf.append(Name);
            }
            else
            {
                buf.append(getRandomSyllableByName(ruleSyllable));
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
                    final List<String> aList = new ArrayList<String>();

                    while (tabTok.hasMoreTokens())
                    {
                        aList.add(tabTok.nextToken());
                    }
                    List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
                    try
                    {
                        PreParserFactory factory = PreParserFactory.getInstance();
                        prereqs = factory.parse(aList);
                    }
                    catch (PersistenceLayerException ple)
                    {
                        // Do nothing
                    }
                    canWrite = PrereqHandler.passesAll(prereqs, pc, null);
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
                    allTheSyllablesForEachRule.put(currentLine, new ArrayList<String>());

                    continue;
                }

                // if we make it here, then we actually have a syllable fragment in hand.
                allTheSyllablesForEachRule.get(currentSyllable).add(currentLine);
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
        @Override
        public boolean accept(final File file)
        {
            return file.isDirectory();

        }
    }

    private static class NamFilter implements FilenameFilter {
        @Override
        public boolean accept(final File dir, final String name)
        {
            return name.regionMatches(true, name.length() - 4, ".nam", 0, 4);

        }
    }

    public static void main(String[] args)
    {
        final List<NameElement> allNamesFiles = Names.findAllNamesFiles();
        Collections.sort(allNamesFiles);

        class Arguments
        {

            Integer count;
            NameElement nameTemplate;
            List<NameElement> allNamesFiles;
            List<String> errors = new LinkedList<String>();

            private Arguments(final String[] args, final List<NameElement> allNamesFiles)
            {
                this.allNamesFiles = allNamesFiles;
                if (args.length >= 2)
                {
                    parseTemplate(args[0]);
                    parseCount(args[1]);
                }
            }

            private void parseTemplate(final String requestedTemplate)
            {
                for (NameElement template : allNamesFiles)
                {
                    if (template.getName().equalsIgnoreCase(requestedTemplate))
                    {
                        nameTemplate = template;
                        break;
                    }
                }
                if (nameTemplate == null)
                {
                    errors.add("Unknown name template. Check spelling. Got: " + requestedTemplate);
                }
            }

            private void parseCount(final String stringCount)
            {
                try
                {
                    count = Integer.parseInt(stringCount);
                }
                catch (NumberFormatException ex)
                {
                    errors.add("Got an invalid number for the amount of names to generate: " + stringCount);
                }
            }

            public boolean isValid()
            {
                return nameTemplate != null && count != null;
            }

        }

        final Arguments parsedArguments = new Arguments(args, allNamesFiles);

        if (!parsedArguments.isValid())
        {
            if(!parsedArguments.errors.isEmpty())
            {
                System.err.println("Invalid arguments:\n" +
                        StringUtils.join(parsedArguments.errors.toArray(), "\n" + "\n"));
            }
            System.out.println("Available name templates are:\n" + StringUtils.join(allNamesFiles.toArray(), "\n"));
            System.out.println("\nArguments are <name template> <number of names to generate>");
            System.out.println("\nExample: orc 100");
            return;
        }

        Names.getInstance().init(parsedArguments.nameTemplate, null);

        for(String name: Names.getInstance().getRandomNames(parsedArguments.count))
        {
            System.out.println(name);
        }

    }


}
