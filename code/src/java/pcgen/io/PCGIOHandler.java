/*
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 *
 */
package pcgen.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.EquipSet;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenPropBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.FileHelper;
import pcgen.util.Logging;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * {@code PCGIOHandler}<br>
 * Reading and Writing PlayerCharacters in PCGen's own format (PCG).
 */
public final class PCGIOHandler extends IOHandler
{

    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    /**
     * Selector
     * <p>
     * <br>author: Thomas Behr 18-03-02
     *
     * @return a list of error messages
     */
    public List<String> getErrors()
    {
        return errors;
    }

    /**
     * Convenience Method
     * <p>
     * <br>author: Thomas Behr 18-03-02
     *
     * @return a list of messages
     */
    public List<String> getMessages()
    {
        final List<String> messages = new ArrayList<>();

        messages.addAll(errors);
        messages.addAll(warnings);

        return messages;
    }

    /**
     * Selector
     * <p>
     * <br>author: Thomas Behr 15-03-02
     *
     * @return a list of warning messages
     */
    public List<String> getWarnings()
    {
        return warnings;
    }

    /**
     * Reads the contents of the given PlayerCharacter from a stream
     * <p>
     * <br>author: Thomas Behr 11-03-02
     *
     * @param pcToBeRead the PlayerCharacter to store the read data
     * @param in         the stream to be read from
     * @param validate
     */
    @Override
    public void read(PlayerCharacter pcToBeRead, InputStream in, final boolean validate)
    {
        warnings.clear();

        final List<String> lines = readPcgLines(in);
        boolean isPCGVersion2 = isPCGCersion2(lines);

        pcToBeRead.setImporting(true);

        final String[] pcgLines = lines.toArray(new String[0]);
        if (isPCGVersion2)
        {
            final PCGParser parser = new PCGVer2Parser(pcToBeRead);
            try
            {
                // parse it all
                parser.parsePCG(pcgLines);
            } catch (PCGParseException pcgex)
            {
                Logging.errorPrint("Error loading character: " + pcgex.getMessage() + "\n Method " + pcgex.getMethod()
                        + " was unable to parse line " + pcgex.getLine());
                errors.add(LanguageBundle.getFormattedString("in_pcgIoErrorReport", pcgex.getMessage())); //$NON-NLS-1$
            }

            warnings.addAll(parser.getWarnings());

            // we are now all done with the import parsing, so turn off
            // the Importing flag and then do some sanity checks
            pcToBeRead.setImporting(false);

            try
            {
                sanityChecks(pcToBeRead, parser);
            } catch (NumberFormatException ex)
            {
                errors.add(ex.getMessage() + Constants.LINE_SEPARATOR + "Method: sanityChecks");
            }

            pcToBeRead.setDirty(false);
        } else
        {
            errors.add("Cannot open PCG file");
        }
    }

    private boolean isPCGCersion2(List<String> lines)
    {
        for (String aLine : lines)
        {
            if (aLine.startsWith(IOConstants.TAG_PCGVERSION))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param in
     * @return String lines
     */
    private List<String> readPcgLines(InputStream in)
    {
        final List<String> lines = new ArrayList<>();

        // try reading in all the lines in the .pcg file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {

            String aLine;

            while ((aLine = br.readLine()) != null)
            {
                lines.add(aLine);
                //isPCGVersion2 |= aLine.startsWith(IOConstants.TAG_PCGVERSION);
            }
        } catch (IOException ioe)
        {
            Logging.errorPrint("Exception in PCGIOHandler::read", ioe);
        }
        return lines;
    }

    /**
     * Writes the contents of the given PlayerCharacter to a stream
     * <p>
     * <br>author: Thomas Behr 11-03-02
     *
     * @param pcToBeWritten the PlayerCharacter to write
     * @param out           the stream to be written to
     * @deprecated The write to a file method should be used in preference as it has safe backup handling.
     */
    @Deprecated
    @Override
    public void write(PlayerCharacter pcToBeWritten, GameMode mode, List<Campaign> campaigns, OutputStream out)
    {
        final String pcgString;
        pcgString = (new PCGVer2Creator(pcToBeWritten, mode, campaigns)).createPCGString();

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)))
        {
            ;
            bw.write(pcgString);
            bw.flush();

            pcToBeWritten.setDirty(false);
        } catch (IOException ioe)
        {
            Logging.errorPrint("Exception in PCGIOHandler::write", ioe);
        }
    }

    /**
     * Writes the contents of the given PlayerCharacter to a file. This method also includes
     * safely backing up the original character file, but only once we know we have
     * successfully exported the character to a string ready for writing. This means that if
     * the save fails, the file system is untouched.
     *
     * @param pcToBeWritten the PlayerCharacter to write
     * @param mode          The character's game mode.
     * @param campaigns     The character's sources.
     * @param outFile       The file to write the character to.
     */
    public void write(PlayerCharacter pcToBeWritten, GameMode mode, List<Campaign> campaigns, File outFile)
    {
        final String pcgString;
        pcgString = (new PCGVer2Creator(pcToBeWritten, mode, campaigns)).createPCGString();

        // Do backup now that we have the character ready to save
        createBackupForFile(outFile);

        // Now save the character

        try (FileWriter fileWriter = new FileWriter(outFile, StandardCharsets.UTF_8);
             Writer bw = new BufferedWriter(fileWriter))
        {
            pcToBeWritten.setDirty(false);
            bw.write(pcgString);
        } catch (IOException ioe)
        {
            Logging.errorPrint("Exception in PCGIOHandler::write", ioe);
        }
    }

    /*
     * ###############################################################
     * private helper methods
     * ###############################################################
     */
    private void sanityChecks(PlayerCharacter currentPC, PCGParser parser)
    {
        // Hit point sanity check
        boolean fixMade = false;

        resolveDuplicateEquipmentSets(currentPC);

        // First make sure the "working" equipment list
        // is in effect for all the bonuses it may add
        currentPC.setCalcEquipmentList();

        // make sure the bonuses from companions are applied
        currentPC.setCalcFollowerBonus();

        // pre-calculate all the bonuses
        currentPC.calcActiveBonuses();

        final int oldHp = currentPC.hitPoints();

        // Recalc the feat pool if required
        if (parser.isCalcFeatPoolAfterLoad())
        {
            double baseFeatPool = parser.getBaseFeatPool();
            double featPoolBonus = currentPC.getRemainingFeatPoints(true);
            baseFeatPool -= featPoolBonus;
            currentPC.setUserPoolBonus(AbilityCategory.FEAT, new BigDecimal(String.valueOf(baseFeatPool)));
        }

        for (CNAbility aFeat : currentPC.getPoolAbilities(AbilityCategory.FEAT, Nature.NORMAL))
        {
            if (aFeat.getAbility().getSafe(ObjectKey.MULTIPLE_ALLOWED) && !currentPC.hasAssociations(aFeat))
            {
                warnings.add("Multiple selection feat found with no selections (" + aFeat.getAbility().getDisplayName()
                        + "). Correct on Feat tab.");
            }
        }

        // Get templates - give it the biggest HD
        // sk4p 11 Dec 2002

        //PCTemplate aTemplate = null;
        if (currentPC.hasClass())
        {
            for (PCClass pcClass : currentPC.getClassSet())
            {
                // Ignore if no levels
                if (currentPC.getLevel(pcClass) < 1)
                {
                    continue;
                }

                // Walk through the levels for this class

                for (int i = 1; i <= currentPC.getLevel(pcClass); i++)
                {
                    int baseSides = currentPC.getLevelHitDie(pcClass, i).getDie();
                    //TODO i-1 is strange see CODE-1925
                    PCClassLevel pcl = currentPC.getActiveClassLevel(pcClass, i - 1);
                    Integer hp = currentPC.getHP(pcl);
                    int iRoll = hp == null ? 0 : hp;
                    int iSides = baseSides + (int) pcClass.getBonusTo("HD", "MAX", i, currentPC);

                    if (iRoll > iSides)
                    {
                        currentPC.setHP(pcl, iSides);
                        fixMade = true;
                    }
                }
            }
        }

        if (fixMade)
        {
            final String message = "Fixed illegal value in hit points. " + "Current character hit points: "
                    + currentPC.hitPoints() + " not " + oldHp;
            warnings.add(message);
        }

        // Sometimes another class, feat, item, whatever can affect
        // what spells or whatever would have been available for a
        // class, so this simply lets the level advancement routine
        // take into account all the details known about a character
        // now that the import is completed. The level isn't affected.
        //  merton_monk@yahoo.com 2/15/2002
        //
        for (PCClass pcClass : currentPC.getClassSet())
        {
            currentPC.calcActiveBonuses();
            currentPC.calculateKnownSpellsForClassLevel(pcClass);
        }

        //
        // need to calc the movement rates
        currentPC.adjustMoveRates();

        // re-calculate all the bonuses
        currentPC.calcActiveBonuses();

        // make sure we are not dirty
        currentPC.setDirty(false);
    }

    /**
     * Check all equipment sets to ensure there are no duplicate paths. Where a
     * duplicate path is found, report it and try to move one non-container to
     * a new path.
     *
     * @param currentPC The character being loaded.
     */
    private void resolveDuplicateEquipmentSets(PlayerCharacter currentPC)
    {
        boolean anyMoved = false;
        Iterable<EquipSet> equipSetList = new ArrayList<>(currentPC.getDisplay().getEquipSet());
        Map<String, EquipSet> idMap = new HashMap<>();
        for (final EquipSet es : equipSetList)
        {
            String idPath = es.getIdPath();
            if (idMap.containsKey(idPath))
            {
                EquipSet existingEs = idMap.get(idPath);
                EquipSet esToBeMoved = chooseItemToBeMoved(existingEs, es);
                if (esToBeMoved == null)
                {
                    warnings.add(
                            String.format("Found two equipment items equipped to the " + "path %s. Items were %s and %s.",
                                    idPath, es.getItem(), existingEs.getItem()));
                    continue;
                }

                // change the item's location
                currentPC.moveEquipSetToNewPath(esToBeMoved);
                EquipSet esStaying = esToBeMoved == es ? existingEs : es;

                // As we always move the non container, move any items it
                // erroneously held to the item remaining in place
                for (int j = esToBeMoved.getItem().getContainedEquipmentCount() - 1; j >= 0; j--)
                {
                    Equipment containedItem = esToBeMoved.getItem().getContainedEquipment(j);
                    esToBeMoved.getItem().removeChild(currentPC, containedItem);
                    esStaying.getItem().insertChild(currentPC, containedItem);
                }

                Logging.log(Logging.WARNING,
                        String.format("Moved item %s from path %s to %s as it " + "clashed with %s", esToBeMoved.getItem(),
                                idPath, esToBeMoved.getIdPath(), esToBeMoved == es ? existingEs.getItem() : es.getItem()));
                idMap.put(es.getIdPath(), es);
                idMap.put(existingEs.getIdPath(), existingEs);
                anyMoved = true;

            } else
            {
                idMap.put(idPath, es);
            }
        }

        if (anyMoved)
        {
            warnings.add("Some equipment was moved as it was incorrectly stored." + " Please see the log for details.");
        }
    }

    /**
     * Pick one of two equipment sets sharing a path to be moved to a new path.
     * Only non containers will be moved to avoid issues with contents.
     *
     * @param equipSet1 The first equipment set at a path.
     * @param equipSet2 The second equipment set at a path.
     * @return The equipment set that should be move,d or null if none are safe.
     */
    private EquipSet chooseItemToBeMoved(EquipSet equipSet1, EquipSet equipSet2)
    {
        if (!equipSet2.getItem().isContainer())
        {
            return equipSet2;
        }
        if (!equipSet1.getItem().isContainer())
        {
            return equipSet1;
        }
        // Currently be really conservative
        return null;
    }

    /**
     * reads from the given partyFile and returns the list of
     * character files for this party
     *
     * @param partyFile a .pcp party file
     * @return a list of files containing the characters in this party
     */
    @SuppressWarnings("PMD.UnusedLocalVariable")
    public static List<File> readCharacterFileList(File partyFile)
    {
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(partyFile, StandardCharsets.UTF_8)))
        {
            lines = bufferedReader.lines().collect(Collectors.toList());
        } catch (IOException ex)
        {
            Logging.errorPrint("Exception in IOHandler::read when reading", ex);
            return null;
        }
        if (lines.size() < 2)
        {
            Logging.errorPrint("Character files missing in " + partyFile.getAbsolutePath());
            return null;
        }
        //Read and throw away version info. May change to actually use later
        String versionInfo = lines.get(0);
        //read character filename data
        String charFiles = lines.get(1);
        String[] files = charFiles.split(",");

        List<File> fileList = new ArrayList<>();
        for (final String fileName : files)
        {
            // try to find it in the party's directory
            File characterFile = new File(partyFile.getParent(), fileName);
            if (!characterFile.exists())
            {
                // try using the global pcg path
                characterFile = new File(PCGenSettings.getPcgDir(), fileName);
            }
            if (!characterFile.exists())
            {
                // try it as an absolute path
                characterFile = new File(fileName);
            }
            if (characterFile.exists())
            {
                fileList.add(characterFile);
            } else
            {
                Logging.errorPrint("Character file does not exist: " + fileName);
            }
        }
        return fileList;
    }

    public static void write(File partyFile, List<File> characterFiles)
    {
        String versionLine = "VERSION:" + PCGenPropBundle.getVersionNumber();
        String[] files = new String[characterFiles.size()];
        Arrays.setAll(files, i -> FileHelper.findRelativePath(partyFile, characterFiles.get(i)));
        String filesLine = StringUtils.join(files, ',');
        try
        {
            FileUtils.writeLines(partyFile, "UTF-8", Arrays.asList(versionLine, filesLine));
        } catch (IOException ex)
        {
            Logging.errorPrint("Could not save the party file: " + partyFile.getAbsolutePath(), ex);
        }
    }

    /**
     * Read in the list of sources required for the character.
     *
     * @param pcgFile The character file
     * @return The list of sources
     */
    public SourceSelectionFacade readSources(File pcgFile)
    {

        try (InputStream in = new FileInputStream(pcgFile))
        {
            return internalReadSources(in);
        } catch (IOException ex)
        {
            Logging.errorPrint("Exception in IOHandler::read when reading", ex);
        }
        return null;
    }

    @Nullable
    private SourceSelectionFacade internalReadSources(InputStream in)
    {
        // Read lines from file
        final List<String> lines = readPcgLines(in);

        // Verify it is ver2
        boolean isPCGVersion2 = isPCGCersion2(lines);

        final String[] pcgLines = lines.toArray(new String[0]);

        if (isPCGVersion2)
        {
            //PlayerCharacter aPC = new PlayerCharacter();
            final PCGParser parser = new PCGVer2Parser(null);
            try
            {
                // Extract list of sources
                return parser.parcePCGSourceOnly(pcgLines);
            } catch (PCGParseException pcgex)
            {
                errors.add(pcgex.getMessage() + Constants.LINE_SEPARATOR + "Method: " + pcgex.getMethod() + '\n'
                        + "Line: " + pcgex.getLine());
            }

            warnings.addAll(parser.getWarnings());
        } else
        {
            errors.add("Cannot open PCG file");
        }

        return null;
    }

}
