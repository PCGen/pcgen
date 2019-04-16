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
	public void testIsPCGenCharacterFile() throws Exception
	{
		//file must exist for it to be true
		assertFalse(PCGFile.isPCGenCharacterFile(new File(
				Constants.EXTENSION_CHARACTER_FILE)), "Extension without filename");
		File temp = File.createTempFile("PCT", Constants.EXTENSION_CHARACTER_FILE);
		assertTrue( PCGFile.isPCGenCharacterFile(temp), "File existence");
		temp.delete();
	}

	@Test
	public void testIsPCGenPartyFile() throws Exception
	{
		//file must exist for it to be true
		assertFalse(PCGFile.isPCGenPartyFile(new File(
				Constants.EXTENSION_PARTY_FILE)), "Extension without filename");
		File temp = File.createTempFile("PCT", Constants.EXTENSION_PARTY_FILE);
		assertTrue(PCGFile.isPCGenPartyFile(temp), "File existence");
		temp.delete();
	}

}
