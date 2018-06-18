package pcgen.io;

import java.io.File;
import junit.framework.TestCase;
import pcgen.cdom.base.Constants;

/**
 * Tests {@link PCGFile}.
 */
public class PCGFileTest extends TestCase
{

	/**
	 * Constructs a new {@link PCGFileTest}.
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase()
	 */
	public PCGFileTest()
	{
		// Empty Constructor
	}

	/**
	 * Constructs a new {@link PCGFileTest} with the given <var>name</var>.
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase(String)
	 */
	public PCGFileTest(final String name)
	{
		super(name);
	}

	/** 	 
	 * Tests {@link PCGFile#isPCGenCharacterFile(File)}
	 * @throws Exception 	 
	 */
	public void testIsPCGenCharacterFile() throws Exception
	{
		//file must exist for it to be true
		assertFalse("Extension without filename", PCGFile.isPCGenCharacterFile(new File(
				Constants.EXTENSION_CHARACTER_FILE)));
		File temp = File.createTempFile("PCT", Constants.EXTENSION_CHARACTER_FILE);
		assertTrue("File existence", PCGFile.isPCGenCharacterFile(temp));
		temp.delete();
	}

	public void testIsPCGenPartyFile() throws Exception
	{
		//file must exist for it to be true
		assertFalse("Extension without filename", PCGFile.isPCGenPartyFile(new File(
				Constants.EXTENSION_PARTY_FILE)));
		File temp = File.createTempFile("PCT", Constants.EXTENSION_PARTY_FILE);
		assertTrue("File existence", PCGFile.isPCGenPartyFile(temp));
		temp.delete();
	}

}
