/*
 * LstLineFileLoader.java
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
 * Created on November 17, 2003, 12:00 PM
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * This class is an extension of the LstFileLoader that loads items
 * that are PObjects and have a source campaign associated with them.
 * Objects loaded by implementations of this class inherit the core
 * MOD/COPY/FORGET funcationality needed for core PObjects used
 * to directly create characters.
 *
 * <p>
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 *
 * @author AD9C15
 */
public abstract class LstObjectFileLoader extends LstFileLoader
{
	private CampaignSourceEntry currentSource = null;
	private List copyLineList = new ArrayList();
	private List forgetLineList = new ArrayList();
	private List modEntryList = new ArrayList();
	private Map sourceMap = null;

	/**
	 * LstObjectFileLoader constructor.
	 */
	public LstObjectFileLoader()
	{
		super();
	}

	/**
	 * This method loads the given list of LST files.
	 * @param fileList containing the list of files to read
	 */
	public void loadLstFiles(List fileList)
	{
		// First sort the file list to optimize loads.
		sortFilesForOptimalLoad(fileList);

		// Track which sources have been loaded already
		TreeSet loadedFiles = new TreeSet();

		// Load the files themselves as thoroughly as possible
		Iterator fileIter = fileList.iterator();

		while (fileIter.hasNext())
		{
			Object testObj = fileIter.next();

			if (testObj == null)
			{
				continue;
			}

			if (!(testObj instanceof CampaignSourceEntry))
			{
				logError(
					"Found "
						+ testObj.getClass().getName()
						+ " - "
						+ testObj.toString()
						+ " when expecting a CampaignSourceEntry.");
				continue;
			}

			// Get the next source entry
			CampaignSourceEntry sourceEntry = (CampaignSourceEntry) testObj;

			// Check if the file has already been loaded before loading it
			String fileName = sourceEntry.getFile();

			if (!loadedFiles.contains(fileName))
			{
				loadLstFile(sourceEntry);
				loadedFiles.add(fileName);
			}
		}

		// Next we perform copy operations
		processCopies();

		// Now handle .MOD items
		sourceMap = null;
		processMods();

		// Finally, forget the .FORGET items
		processForgets();
	}

	/**
	 * This method parses the LST file line, applying it to the provided target
	 * object.  If the line indicates the start of a new target object, a new
	 * PObject of the appropriate type will be created prior to applying the
	 * line contents.  Because of this behavior, it is necessary for this
	 * method to return the new object.  Implementations of this method also
	 * MUST call finishObject with the original target prior to returning the
	 * new value.
	 *
	 * @param lstLine String LST formatted line read from the source URL
	 * @param target PObject to apply the line to, barring the start of a
	 *         new object
	 * @param source CampaignSourceEntry indicating the file that the line was
	 *         read from as well as the Campaign object that referenced the file
	 * @return PObject that was either created or modified by the provided
	 *         LST line
	 * @throws PersistenceLayerException if there is a problem with the LST syntax
	 */
	public abstract PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException;




	/**
	 * This method is called when the end of data for a specific PObject
	 * is found, typically in order to add it to Globals.  This method MUST
	 * invoke includeObject in order to properly handle includes and excludes!
	 *
	 * @param target PObject to perform final operations on
	 */
	protected abstract void finishObject(PObject target);

	/**
	 * This method should be called by finishObject implementations in
	 * order to check if the parsed object is affected by an INCLUDE or
	 * EXCLUDE request.
	 *
	 * @param parsedObject PObject to determine whether to include in
	 *         Globals etc.
	 * @return boolean true if the object should be included, else false
	 *         to exclude it
	 */
	protected final boolean includeObject(PObject parsedObject)
	{
		// Null check; never add nulls or objects without a name/key name
		if ((parsedObject == null) || (parsedObject.getDisplayName() == null) || (parsedObject.getDisplayName().trim().length() == 0)
			|| (parsedObject.getKeyName() == null) || (parsedObject.getKeyName().trim().length() == 0))
		{
			return false;
		}

		// Make sure the source info was set
		if (sourceMap != null)
		{
			parsedObject.setSourceMap(sourceMap);
		}

		// If includes were present, check includes for given object
		List includeItems = currentSource.getIncludeItems();

		if (!includeItems.isEmpty())
		{
			return includeItems.contains(parsedObject.getKeyName());
		}
		// If excludes were present, check excludes for given object
		List excludeItems = currentSource.getExcludeItems();

		if (!excludeItems.isEmpty())
		{
			return !excludeItems.contains(parsedObject.getKeyName());
		}

		return true;
	}

	/**
	 * This method retrieves a PObject from globals by its key.
	 * This is used to avoid duplicate loads, get objects to forget or
	 * modify, etc.
	 * @param aKey String key of PObject to retrieve
	 * @return PObject from Globals
	 */
	protected abstract PObject getObjectKeyed(String aKey);

	/**
	 * This method loads a single LST formatted file.
	 * @param sourceEntry CampaignSourceEntry containing the absolute file path
	 * or the URL from which to read LST formatted data.
	 */
	protected void loadLstFile(CampaignSourceEntry sourceEntry)
	{
		setChanged();
		String urlString="";
		try {
			urlString = CoreUtility.fileToURL(sourceEntry.getFile());
			notifyObservers(new URL(urlString));
		}
		catch (MalformedURLException e) {
			try {
				// Notify of the failed file
				setChanged();
				notifyObservers(new Exception("Can not create URL for: "+urlString));
				// Notify of a dummy file, so that anyone counting files processed
				// for a progress dialog or something will get a consistent count
				setChanged();
				notifyObservers(new URL("http://f"));
			}
			catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}



		sourceMap = null;
		currentSource = sourceEntry;

		StringBuffer dataBuffer = new StringBuffer();

		try
		{
			readFileGetURL(sourceEntry.getFile(), dataBuffer);
		}
		catch (PersistenceLayerException ple)
		{
			logError("Unable to load the file: '" + sourceEntry.getFile()+ "': " + ple.getMessage());
		}

		final String newlinedelim = "\r\n";
		final String aString = dataBuffer.toString();
		final StringTokenizer fileLines = new StringTokenizer(aString, newlinedelim);
		PObject target = null;
		ArrayList classModLines = null;

		int currentLineNumber = 0;
		while (fileLines.hasMoreTokens())
		{
			++currentLineNumber;
			final String line = fileLines.nextToken().trim();
			if ((line.length() == 0) || (line.charAt(0) == '#'))
			{
				continue;
			}
			String[] tokens = line.split("\t");

			// Check for continuation of class mods
			if (classModLines != null)
			{
				if (tokens[0].startsWith("CLASS:"))
				{
					modEntryList.add(classModLines);
					classModLines = null;
				}
				else
				{
					// Add the line to the class mod and don't process it yet.
					classModLines.add(new ModEntry(sourceEntry, line, currentLineNumber, sourceMap));
					continue;
				}
			}

			// check for comments, copies, mods, and forgets
			if ((line.length() == 0) || (line.startsWith("#")))
			{
				continue;
			}
			else if (line.startsWith("SOURCE"))
			{
				sourceMap = SourceLoader.parseLine(line, sourceEntry.getFile());
			}
			else if (tokens[0].indexOf(".COPY") > 0)
			{
				copyLineList.add(line);
			}
			else if (tokens[0].indexOf(".MOD") > 0)
			{
				if (tokens[0].startsWith("CLASS:"))
				{
					// As CLASS:abc.MOD can be followed by level lines, we place the
					// lines into a list for processing in a group afterwards
					classModLines = new ArrayList();
					classModLines.add(new ModEntry(sourceEntry, line, currentLineNumber, sourceMap));
				}
				else
				{
					modEntryList.add(new ModEntry(sourceEntry, line, currentLineNumber, sourceMap));
				}
			}
			else if (tokens[0].indexOf(".FORGET") > 0)
			{
				forgetLineList.add(line);
			}
			else
			{
				try
				{
					target = parseLine(target, line, sourceEntry);
					finishObject(target);
				}
				catch (PersistenceLayerException ple)
				{
					logError("Error parsing file '" + sourceEntry.getFile() + "' line '"+ currentLineNumber + "': " + ple.getMessage());
					Logging.debugPrint("Parse error:", ple);
				}
				catch (Throwable t) {
					logError("Error parsing file '" + sourceEntry.getFile() + "' line '"+ currentLineNumber + "': " + t.getMessage());
					Logging.errorPrint("Ignoring error ", t);
				}
			}
		}

		if (classModLines != null)
		{
			modEntryList.add(classModLines);
		}
	}

	/**
	 * This method, when implemented, will perform a single .FORGET
	 * operation.
	 *
	 * @param objToForget containing the object to forget
	 */
	protected abstract void performForget(PObject objToForget);

	/**
	 * This method will sort the list of files into an order such that
	 * loads will be optimized.
	 * <br>
	 * Unless overridden, this method will sort files such that files
	 * to be loaded in entirety are loaded first, then files performing
	 * excludes of individual objects, then files including only specific
	 * objects within the files.
	 *
	 * @param fileList list of String file names to optimize
	 */
	protected void sortFilesForOptimalLoad(List fileList)
	{
		if ((fileList.isEmpty()) || (fileList.get(0) instanceof String))
		{
			// avoid extra creation, sorting, etc if this is a generic
			// list of files
			return;
		}

		ArrayList normalFiles = new ArrayList();
		ArrayList includeFiles = new ArrayList();
		ArrayList excludeFiles = new ArrayList();

		Iterator iter = fileList.iterator();

		while (iter.hasNext())
		{
			CampaignSourceEntry sourceEntry = (CampaignSourceEntry) iter.next();
			String fileInfo = sourceEntry.getFile();

			if (fileInfo.indexOf("INCLUDE") > 0)
			{
				if (!includeFiles.contains(sourceEntry))
				{
					includeFiles.add(sourceEntry);
				}
			}
			else if (fileInfo.indexOf("EXCLUDE") > 0)
			{
				if (!excludeFiles.contains(sourceEntry))
				{
					excludeFiles.add(sourceEntry);
				}
			}
			else
			{
				if (!normalFiles.contains(sourceEntry))
				{
					normalFiles.add(sourceEntry);
				}
			}
		}

		fileList.clear();

		// Optimal load:  Entire files, exclude files, include files
		// TODO: compare include/exclude file lists?
		fileList.addAll(normalFiles);
		fileList.addAll(excludeFiles);
		fileList.addAll(includeFiles);
	}

	/**
	 * This method will perform a single .COPY operation.
	 *
	 * @param baseName String name of the object to copy
	 * @param copyName String name of the target object
	 */
	private void performCopy(String baseKey, String copyName)
	{
		PObject object = getObjectKeyed(baseKey);

		try
		{
			if (object == null)
			{
				logError("PObject '" + baseKey + "' not found; .COPY skipped.");

				return;
			}

			PObject clone = (PObject) object.clone();
			clone.setName(copyName);
			clone.setKeyName(copyName);
			finishObject(clone);
		}
		catch (CloneNotSupportedException e)
		{
			logError(object.getClass().getName() + " clone error; .COPY of " + baseKey + " to " + copyName
				+ " skipped.");
		}
	}

	/**
	 * This method will perform a single .COPY operation based on the LST
	 * file content.
	 * @param lstLine String containing the LST source for the
	 * .COPY operation
	 */
	private void performCopy(String lstLine)
	{
		final int nameEnd = lstLine.indexOf(".COPY");
		final String baseName = lstLine.substring(0, nameEnd);
		final String copyName = lstLine.substring(nameEnd + 6);

		performCopy(baseName, copyName);
	}

	/**
	 * This method will perform a single .MOD
	 * operation.  Loaders can [typically] use the name without checking
	 * for (or stripping off) .MOD due to the implementation of
	 * PObject.setName()
	 *
	 * @param entry ModEntry containing the LST source and source
	 * campaign information for the requested .MOD operation
	 */
	private void performMod(ModEntry entry)
	{
		// get the name of the object to modify, trimming off the .MOD
		int nameEnd = entry.getLstLine().indexOf(".MOD");
		String key = entry.getLstLine().substring(0, nameEnd);

		// remove the leading tag, if any (i.e. CLASS:Druid.MOD
		int nameStart = key.indexOf(':');

		if (nameStart > 0)
		{
			key = key.substring(nameStart + 1);
		}

		// get the actual object to modify
		PObject object = getObjectKeyed(key);

		if (object == null)
		{
			logError("Cannot apply .MOD; PObject '" + key + "' not found. '" + entry.getSource().getFile() + ":"+ entry.getLineNumber()+"'");
			return;
		}

		// modify the object
		try
		{
			object.setModSourceMap(entry.getSourceMap());
			parseLine(object, entry.getLstLine(), entry.getSource());
			finishObject(object);
		}
		catch (PersistenceLayerException ple)
		{
			logError("Unable to MOD the object '" + key + "' as it is not possible to parse '" + entry.getSource().getFile() + ":" + entry.getLineNumber()+"': " + ple.getMessage());
		}
	}

	/**
	 * This method will perform a multi-line .MOD operation. This is used
	 * for example in MODs of CLASSES which can have multiple lines. Loaders
	 * can [typically] use the name without checking
	 * for (or stripping off) .MOD due to the implementation of
	 * PObject.setName()
	 * @param entryList
	 */
	private void performMod(List entryList)
	{
		ModEntry entry = (ModEntry) entryList.get(0);
		// get the name of the object to modify, trimming off the .MOD
		int nameEnd = entry.getLstLine().indexOf(".MOD");
		String key = entry.getLstLine().substring(0, nameEnd);

		// remove the leading tag, if any (i.e. CLASS:Druid.MOD
		int nameStart = key.indexOf(':');

		if (nameStart > 0)
		{
			key = key.substring(nameStart + 1);
		}

		// get the actual object to modify
		PObject object = getObjectKeyed(key);

		if (object == null)
		{
			logError("Cannot apply .MOD; PObject '" + key + "' not found. '" + entry.getSource().getFile() + ":"+ entry.getLineNumber()+"'");
			return;
		}

		// modify the object
		try
		{
			for (Iterator entryIter = entryList.iterator(); entryIter.hasNext();)
			{
				ModEntry element = (ModEntry) entryIter.next();
				object.setModSourceMap(element.getSourceMap());
				parseLine(object, element.getLstLine(), element.getSource());
			}
			finishObject(object);
		}
		catch (PersistenceLayerException ple)
		{
			logError("Unable to MOD the object '" + key + "' as it is not possible to parse '" + entry.getSource().getFile() + ":" + entry.getLineNumber()+"': " + ple.getMessage());
		}
	}

	/**
	 * This method will process the lines containing a .COPY directive
	 */
	private void processCopies()
	{
		Iterator copyIter = copyLineList.iterator();

		while (copyIter.hasNext())
		{
			performCopy((String) copyIter.next());
		}
		copyLineList.clear();
	}

	/**
	 * This method will process the lines containing a .FORGET directive
	 */
	private void processForgets()
	{
		Iterator forgetIter = forgetLineList.iterator();

		while (forgetIter.hasNext())
		{
			String forgetKey = (String) forgetIter.next();
			forgetKey = forgetKey.substring(0, forgetKey.indexOf(".FORGET"));

			// Commented out so that deprcated method no longer used
			// performForget(forgetName);

			PObject objToForget = getObjectKeyed(forgetKey);
			if (objToForget != null)
			{
				performForget(objToForget);
			}
		}
		forgetLineList.clear();
	}

	/**
	 * This method will process the lines containing a .MOD directive
	 */
	private void processMods()
	{
		Iterator modIter = modEntryList.iterator();

		while (modIter.hasNext())
		{
			Object modEntry = modIter.next();
			if (modEntry instanceof ModEntry)
			{
				performMod((ModEntry) modEntry);
			}
			else if (modEntry instanceof List)
			{
				performMod((List) modEntry);
			}
			else
			{
				Logging.errorPrint("Unexpected mod entry of type "
					+ modEntry.getClass().getName() + " ignored.");
			}
		}
		modEntryList.clear();
	}

	/**
	 * This class is an entry mapping a mod to its source.
	 * Once created, instances of this class are immutable.
	 */
	public static class ModEntry
	{
		private CampaignSourceEntry source = null;
		private String lstLine = null;
		private int lineNumber = 0;
		private Map sourceMap = null;

		/**
		 * ModEntry constructor.
		 * @param source CampaignSourceEntry containing the MOD line
		 *         [must not be null]
		 * @param lstLine LST syntax modification
		 *         [must not be null]
		 * @param lineNumber
		 * @param sourceMap
		 */
		public ModEntry(CampaignSourceEntry source, String lstLine, int lineNumber, Map sourceMap)
		{
			super();

			if (source == null)
			{
				throw new IllegalArgumentException("source must not be null");
			}

			if (lstLine == null)
			{
				throw new IllegalArgumentException("lstLine must not be null");
			}

			this.source = source;
			this.lstLine = lstLine;
			this.lineNumber = lineNumber;
			this.sourceMap = sourceMap;
		}

		/**
		 * This method gets the LST formatted source line for the .MOD
		 * @return String in LST format, unmodified from the source file
		 */
		public String getLstLine()
		{
			return lstLine;
		}

		/**
		 * This method gets the source of the .MOD operation
		 * @return CampaignSourceEntry indicating where the .MOD came from
		 */
		public CampaignSourceEntry getSource()
		{
			return source;
		}

		/**
		 *
		 * @return The source map for this MOD entry
		 */
		public Map getSourceMap()
		{
			return sourceMap;
		}

		/**
		 *
		 * @return The line number of the original file for this MOD entry
		 */
		public int getLineNumber()
		{
			return lineNumber;
		}
	}
	/**
	 * @return Returns the currentSource.
	 */
	public CampaignSourceEntry getCurrentSource() {
		return currentSource;
	}

	/**
	 * @param currentSource The currentSource to set.
	 */
	public void setCurrentSource(CampaignSourceEntry currentSource) {
		this.currentSource = currentSource;
	}
}
