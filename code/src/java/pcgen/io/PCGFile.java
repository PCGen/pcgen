package pcgen.io;

import pcgen.cdom.base.Constants;

import java.io.File;
import java.net.URI;

/**
 * Common I/O methods specific to files.
 */
public final class PCGFile
{
	/** prevent instantiation of this utility class. */
	private PCGFile()
	{
	}

	/**
	 * Checks if the given <code>file</code> is a PCGen character file based on the
	 * file extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if the file exists and is a PCGen character file
	 *
	 * @see Constants#EXTENSION_CHARACTER_FILE
	 */
	public static boolean isPCGenCharacterFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (!file.isFile())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
			Constants.EXTENSION_CHARACTER_FILE);
	}

	/**
	 * Checks if the given <code>file</code> is a PCGen party file based on the file
	 * extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if the file exists and is a PCGen party file
	 *
	 * @see Constants#EXTENSION_PARTY_FILE
	 */
	public static boolean isPCGenPartyFile(final File file)
	{
		// A directory strangely named "fred.pcp" is not a party file.
		if (!file.isFile())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
			Constants.EXTENSION_PARTY_FILE);
	}

	/**
	 * Checks if the given <code>file</code> is a PCGen campaign file based on the
	 * file extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if a PCGen campaign file
	 *
	 * @see Constants#EXTENSION_CAMPAIGN_FILE
	 */
	public static boolean isPCGenCampaignFile(final File file)
	{
		// A directory strangely named "fred.pcc" is not a campaign file.
		if (!file.isFile())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
			Constants.EXTENSION_CAMPAIGN_FILE);
	}

	/**
	 * Checks if the given <code>uri</code> is a PCGen campaign file based on the
	 * file extension.
	 *
	 * @param uri the uri to test
	 * @return {<code>true</code>} if a PCGen campaign file
	 *
	 * @see Constants#EXTENSION_CAMPAIGN_FILE
	 */
	public static boolean isPCGenCampaignFile(final URI uri)
	{
		if ("file".equals(uri.getScheme()))
		{
			return isPCGenCampaignFile(new File(uri));
		}

		return uri.getPath() != null
			&& uri.getPath().toLowerCase().endsWith(
				Constants.EXTENSION_CAMPAIGN_FILE);
	}

	/**
	 * Checks if the given <code>file</code> is a PCGen list file based on the file
	 * extension.
	 *
	 * @param file the file to test
	 * @return {<code>true</code>} if a PCGen list file
	 *
	 * @see Constants#EXTENSION_LIST_FILE
	 */
	public static boolean isPCGenListFile(final File file)
	{
		// A directory strangely named "fred.lst" is not a list file.
		if (!file.isFile())
		{
			return false;
		}

		return getWindowsSafeFilename(file).endsWith(
			Constants.EXTENSION_LIST_FILE);
	}

	/**
	 * Checks if the given <code>file</code> is a PCGen character or party file based
	 * on the file extension.
	 *
	 * @param file the file to test
	 *
	 * @return {<code>true</code>} if a PCGen character or party file
	 *
	 * @see Constants#EXTENSION_CHARACTER_FILE
	 * @see Constants#EXTENSION_PARTY_FILE
	 */
	public static boolean isPCGenCharacterOrPartyFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (file.isDirectory())
		{
			return false;
		}

		final String name = getWindowsSafeFilename(file);

		return name.endsWith(Constants.EXTENSION_CHARACTER_FILE)
			|| name.endsWith(Constants.EXTENSION_PARTY_FILE);
	}

	/**
	 * Checks if the given <var>file</var> is a PCGen character, party or campaign
	 * file based on the file extension.
	 *
	 * @param file the file to test
	 *
	 * @return {<code>true</code>} if a PCGen character, party or campaign file
	 *
	 * @see Constants#EXTENSION_CAMPAIGN_FILE
	 * @see Constants#EXTENSION_CHARACTER_FILE
	 * @see Constants#EXTENSION_PARTY_FILE
	 */
	public static boolean isPCGenCharacterPartyOrCampaignFile(final File file)
	{
		// A directory strangely named "fred.pcg" is not a character file.
		if (!file.isFile())
		{
			return false;
		}

		final String name = getWindowsSafeFilename(file);

		return name.endsWith(Constants.EXTENSION_CHARACTER_FILE)
			|| name.endsWith(Constants.EXTENSION_PARTY_FILE)
			|| name.endsWith(Constants.EXTENSION_CAMPAIGN_FILE);
	}

	/**
	 * It may turn out to be the case that this should be optimized further to
	 * pull out the extension, but there doesn't seem to be such high use of
	 * filename checking to warrant further tinkering.
	 * 
	 * @param file The filename to sanitise.
	 * @return windows safe file name
	 */
	private static String getWindowsSafeFilename(final File file)
	{
		return file.getName().toLowerCase();
	}
}
