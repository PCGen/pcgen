package pcgen.io;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import pcgen.cdom.base.Constants;

import org.junit.jupiter.api.Test;


/**
 * Tests {@link PCGFile}.
 */
class PCGFileTest
{
    /**
     * Tests {@link PCGFile#isPCGenCharacterFile(File)}
     */
    @Test
    void testIsPCGenCharacterFile() throws Exception
    {
        //file must exist for it to be true
        assertFalse(PCGFile.isPCGenCharacterFile(new File(
                Constants.EXTENSION_CHARACTER_FILE)), "Extension without filename");
        File temp = File.createTempFile("PCT", Constants.EXTENSION_CHARACTER_FILE);
        temp.deleteOnExit();
        assertTrue(PCGFile.isPCGenCharacterFile(temp), "File existence");
        assertTrue(PCGFile.isPCGenCharacterOrPartyFile(temp), "File existence");
    }

    @Test
    void testIsPCGenPartyFile() throws Exception
    {
        //file must exist for it to be true
        assertFalse(PCGFile.isPCGenPartyFile(new File(
                Constants.EXTENSION_PARTY_FILE)), "Extension without filename");
        File temp = File.createTempFile("PCT", Constants.EXTENSION_PARTY_FILE);
        temp.deleteOnExit();
        assertTrue(PCGFile.isPCGenPartyFile(temp), "File existence");
        assertTrue(PCGFile.isPCGenCharacterOrPartyFile(temp), "File existence");
    }

    @Test
    void testIsPCGenLstFile() throws Exception
    {
        //file must exist for it to be true
        assertFalse(PCGFile.isPCGenListFile(new File(
                Constants.EXTENSION_LIST_FILE)), "Extension without filename");
        File temp = File.createTempFile("LST", Constants.EXTENSION_LIST_FILE);
        temp.deleteOnExit();
        assertTrue(PCGFile.isPCGenListFile(temp), "File existence");
        assertFalse(PCGFile.isPCGenCharacterOrPartyFile(temp), "File existence");
    }

}
