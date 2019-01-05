package pcgen.io;

import static org.junit.Assert.assertTrue;

import java.io.File;

import pcgen.cdom.base.Constants;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link PCGFile}.
 */
public class PCGFileTest
{
	/** 	 
	 * Tests {@link PCGFile#isPCGenCharacterFile(File)}
	 */
	@Test
	public void testIsPCGenCharacterFile() throws Exception
	{
		//file must exist for it to be true
		Assert.assertFalse("Extension without filename", PCGFile.isPCGenCharacterFile(new File(
				Constants.EXTENSION_CHARACTER_FILE)));
		File temp = File.createTempFile("PCT", Constants.EXTENSION_CHARACTER_FILE);
		assertTrue("File existence", PCGFile.isPCGenCharacterFile(temp));
		temp.delete();
	}

	@Test
	public void testIsPCGenPartyFile() throws Exception
	{
		//file must exist for it to be true
		Assert.assertFalse("Extension without filename", PCGFile.isPCGenPartyFile(new File(
				Constants.EXTENSION_PARTY_FILE)));
		File temp = File.createTempFile("PCT", Constants.EXTENSION_PARTY_FILE);
		assertTrue("File existence", PCGFile.isPCGenPartyFile(temp));
		temp.delete();
	}

}
