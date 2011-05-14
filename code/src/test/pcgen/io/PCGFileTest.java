package pcgen.io;

import java.io.File;
import pcgen.PCGenTestCase;
import pcgen.cdom.base.Constants;

/**
 * Tests {@link PCGFile}.
 */
public class PCGFileTest extends PCGenTestCase
{
	/**
	 * Constructs a new {@link PCGFileTest}.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PCGFileTest()
	{
		// Empty Constructor
	}

	/**
	 * Constructs a new {@link PCGFileTest} with the given <var>name</var>.
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PCGFileTest(final String name)
	{
		super(name);
	}

	/**
	 * Tests {@link PCGFile#isPCGenCharacterFile(File)} for the case of just an
	 * extension with no filename.
	 *
	 * @throws Exception
	 */
	public void testIsPCGenPartyFile() throws Exception
	{
		assertTrue("Extension without filename", PCGFile
			.isPCGenCharacterFile(new File(
				Constants.EXTENSION_CHARACTER_FILE)));
	}
}
