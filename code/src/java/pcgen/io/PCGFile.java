package pcgen.io;

import pcgen.core.Constants;

import java.io.File;

/**
 * Common I/O methods specific to files.
 */
public final class PCGFile
{
	/**
	 * Checks if the given <var>file</var> is a PCGen character file based on the
	 * file extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if a PCGen character file
	 *
	 * @see Constants#s_PCGEN_CHARACTER_EXTENSION
	 */
	public static boolean isPCGenCharacterFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
				Constants.s_PCGEN_CHARACTER_EXTENSION);
	}

	/**
	 * Checks if the given <var>file</var> is a PCGen party file based on the file
	 * extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if a PCGen party file
	 *
	 * @see Constants#s_PCGEN_PARTY_EXTENSION
	 */
	public static boolean isPCGenPartyFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
				Constants.s_PCGEN_PARTY_EXTENSION);
	}

	/**
	 * Checks if the given <var>file</var> is a PCGen campaign file based on the
	 * file extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if a PCGen campaign file
	 *
	 * @see Constants#s_PCGEN_CAMPAIGN_EXTENSION
	 */
	public static boolean isPCGenCampaignFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
				Constants.s_PCGEN_CAMPAIGN_EXTENSION);
	}

	/**
	 * Checks if the given <var>file</var> is a PCGen list file based on the file
	 * extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if a PCGen list file
	 *
	 * @see Constants#s_PCGEN_LIST_EXTENSION
	 */
	public static boolean isPCGenListFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
				Constants.s_PCGEN_LIST_EXTENSION);
	}

	/**
	 * Checks if the given <var>file</var> is a PCGen character or party file based
	 * on the file extension.
	 *
	 * @param file the file to test
	 *
	 * @return {<code>true</code>} if a PCGen character or party file
	 *
	 * @see Constants#s_PCGEN_CHARACTER_EXTENSION
	 * @see Constants#s_PCGEN_PARTY_EXTENSION
	 */
	public static boolean isPCGenCharacterOrPartyFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		final String name = getWindowsSafeFilename(file);

		return name.endsWith(Constants.s_PCGEN_CHARACTER_EXTENSION)
				|| name.endsWith(Constants.s_PCGEN_PARTY_EXTENSION);
	}

	/**
	 * Checks if the given <var>file</var> is a PCGen character, party or campaign
	 * file based on the file extension.
	 *
	 * @param file the file to test
	 *
	 * @return {<code>true</code>} if a PCGen character, party or campaign file
	 *
	 * @see Constants#s_PCGEN_CAMPAIGN_EXTENSION
	 * @see Constants#s_PCGEN_CHARACTER_EXTENSION
	 * @see Constants#s_PCGEN_PARTY_EXTENSION
	 */
	public static boolean isPCGenCharacterPartyOrCampaignFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		final String name = getWindowsSafeFilename(file);

		return name.endsWith(Constants.s_PCGEN_CHARACTER_EXTENSION)
				|| name.endsWith(Constants.s_PCGEN_PARTY_EXTENSION)
				|| name.endsWith(Constants.s_PCGEN_CAMPAIGN_EXTENSION);
	}

	/**
	 * It may turn out to be the case that this should be optimized further to
	 * pull out the extension, but there doesn't seem to be such high use of
	 * filename checking to warrant further tinkering.
	 * 
	 * @param file
	 * @return windows safe file name
	 */
	private static String getWindowsSafeFilename(final File file)
	{
		return file.getName().toLowerCase();
	}
}
