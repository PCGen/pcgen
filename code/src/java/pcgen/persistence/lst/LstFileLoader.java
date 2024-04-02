/*
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 */
package pcgen.persistence.lst;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * This class is a base class for LST file loaders.
 *
 * <p>
 * This class lays out a skeleton for LST file loading, setting
 * up shared features and functions for loading and parsing of files.
 *
 * <p>
 * This class extends the <tt>Observable</tt> class so interested observers
 * will be notified of the progress of loading files.
 *
 * <p>
 * Instances of LstFileLoader or its subclasses are not thread-safe,
 * so any thread should only acccess a single loader (or group of loaders)
 * at a time.
 */
public final class LstFileLoader
{
	private LstFileLoader()
	{
		//Utility class
	}

	/** The String that represents the start of a line comment. */
	public static final char LINE_COMMENT_CHAR = '#';

	/** The String that separates individual objects */
	public static final String LINE_SEPARATOR_REGEXP = "(\r\n?|\n)"; //$NON-NLS-1$

	/** BOM prefix, used to warn the user that BOM-strings are not supported */
	private static final String BOM = "\uFEFF";

	/**
	 * This method reads the given URI and returns its content as a string. If an error occurs, we don't throw an
	 * exception, but log the error in the logger. It is possible to read file content from the remote link, but
	 * a corresponding option must be enabled in settings.
	 *
	 * @param uri	URI of the remote content
	 * @return String	file content
	 * @throws PersistenceLayerException	is thrown when a null URI is provided
	 */
	@Nullable
	public static String readFromURI(URI uri) throws PersistenceLayerException
	{
		if (uri == null)
		{
			// We have a problem!
			throw new PersistenceLayerException("LstFileLoader.readFromURI() received a null URI parameter!");
		}

		try
		{
			if (!CoreUtility.isNetURI(uri)) // only load local URIs
			{
				Path path = Path.of(uri);
				String result = Files.readString(path);
				if (result.startsWith(BOM))
				{
					Logging.log(Logging.WARNING,
							"The file %s uses UTF-8-BOM encoding. LST files must be UTF-8".formatted(uri));
					result = result.substring(1);
				}
				return result;
			}
			else if (SettingsHandler.isLoadURLs()) // load from remote URIs
			{
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
						.uri(uri)
						.build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				return response.body();
			}
			else
			{
				// Just to protect people from using web
				// sources without their knowledge,
				// we added a preference.
				ShowMessageDelegate.showMessageDialog("Preferences are currently set to NOT allow\nloading of "
					+ "sources from web links.\n" + uri + " is a web link", Constants.APPLICATION_NAME,
					MessageType.ERROR);
			}
		} catch (MalformedInputException ie)
		{
			Logging.errorPrint("ERROR: " + uri + "\nThe file doesn't use UTF-8 encoding. LST files must be UTF-8", ie);
		}
		catch (IOException | InterruptedException e)
		{
			// Don't throw an exception here because a simple
			// file not found will prevent ANY other files from
			// being loaded/processed -- NOT what we want
			Logging.errorPrint("ERROR: " + uri + '\n' + "Exception type: " + e.getClass().getName() + "\n" + "Message: "
				+ e.getMessage(), e);
		}
		return null;
	}
}
