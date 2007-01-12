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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

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
public abstract class LstObjectFileLoader<T extends PObject> extends
		LstFileLoader
{
	/** The String that separates fields in the file. */
	public static final String FIELD_SEPARATOR = "\t"; //$NON-NLS-1$
	/** The String that separates individual objects */
	public static final String LINE_SEPARATOR = "\r\n"; //$NON-NLS-1$

	/** Tag used to include an object */
	public static final String INCLUDE_TAG = "INCLUDE"; //$NON-NLS-1$

	/** Tag used to exclude an object */
	public static final String EXCLUDE_TAG = "EXCLUDE"; //$NON-NLS-1$

	/** The suffix used to indicate this is a copy operation */
	public static final String COPY_SUFFIX = ".COPY"; //$NON-NLS-1$
	/** The suffix used to indicate this is a mod operation */
	public static final String MOD_SUFFIX = ".MOD"; //$NON-NLS-1$
	/** The suffix used to indicate this is a forget operation */
	public static final String FORGET_SUFFIX = ".FORGET"; //$NON-NLS-1$

	private CampaignSourceEntry currentSource = null;
	private List<String> copyLineList = new ArrayList<String>();
	private List<String> forgetLineList = new ArrayList<String>();
	private List<List<ModEntry>> modEntryList = new ArrayList<List<ModEntry>>();
	private Map<String, String> sourceMap = null;
	/** A list of objects that will not be included. */
	protected List<String> excludedObjects = new ArrayList<String>();

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
	 * @throws PersistenceLayerException 
	 */
	@Override
	public void loadLstFiles(List<?> fileList) throws PersistenceLayerException
	{
		// First sort the file list to optimize loads.
		sortFilesForOptimalLoad(fileList);

		// Track which sources have been loaded already
		TreeSet<String> loadedFiles = new TreeSet<String>();

		// Load the files themselves as thoroughly as possible
		for (Object testObj : fileList)
		{
			if (testObj == null)
			{
				continue;
			}

			if (!(testObj instanceof CampaignSourceEntry))
			{
				logError(PropertyFactory.getFormattedString(
					"Errors.LstFileLoader.NotCampaignSource", //$NON-NLS-1$
					testObj.getClass().getName(), testObj.toString()));
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
	 * MUST call <code>completeObject</code> with the original target prior to 
	 * returning the new value.
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
	public abstract T parseLine(T target, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException;

	/**
	 * This method is called by the loading framework to signify that the
	 * loading of this object is complete and the object should be added to the
	 * system.
	 * 
	 * <p>This method will check that the loaded object should be included via
	 * a call to <code>includeObject</code> and if not add it to the list of
	 * excluded objects.
	 * 
	 * <p>Once the object has been verified the method will call
	 * <code>finishObject</code> to give each object a chance to complete 
	 * processing.
	 * 
	 * <p>The object is then added to the system if it doesn't already exist.
	 * If the object exists, the object sources are compared by date and if the
	 * System setting allowing over-rides is set it will use the object from the
	 * newer source.
	 * 
	 * @param pObj The object that has just completed loading.
	 * 
	 * @see pcgen.persistence.lst.LstObjectFileLoader#includeObject(PObject)
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(PObject)
	 * @see pcgen.core.SettingsHandler#isAllowOverride()
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * @throws PersistenceLayerException 
	 * 
	 * @since 5.11
	 */
	public void completeObject(final PObject pObj)
		throws PersistenceLayerException
	{
		if (pObj == null)
		{
			return;
		}

		// Make sure the source info was set
		if (sourceMap != null)
		{
			try
			{
				pObj.setSourceMap(sourceMap);
			}
			catch (ParseException e)
			{
				throw new PersistenceLayerException(e.toString());
			}
		}

		if (includeObject(pObj))
		{
			finishObject(pObj);
			final T currentObj = getObjectKeyed(pObj.getKeyName());

			if (currentObj == null || !pObj.equals(currentObj))
			{
				addGlobalObject(pObj);
			}
			else
			{
				if (!currentObj.getSourceFile().equals(pObj.getSourceFile()))
				{
					if (SettingsHandler.isAllowOverride())
					{
						// If the new object is more recent than the current
						// one, use the new object
						final Date pObjDate =
								pObj.getSourceEntry().getSourceBook().getDate();
						final Date currentObjDate =
								currentObj.getSourceEntry().getSourceBook()
									.getDate();
						if ((pObjDate != null)
							&& ((currentObjDate == null) || ((pObjDate
								.compareTo(currentObjDate) > 0))))
						{
							performForget(currentObj);
							addGlobalObject(pObj);
						}
					}
					else
					{
						// Duplicate loading error
						Logging.errorPrintLocalised(
							"Warnings.LstFileLoader.DuplicateObject", //$NON-NLS-1$
							pObj.getKeyName(), currentObj.getSourceFile(), pObj
								.getSourceFile());
					}
				}
			}
		}
		else
		{
			excludedObjects.add(pObj.getKeyName());
		}
	}

	/**
	 * Adds an object to the global repository.
	 * 
	 * @param pObj The object to add.
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	protected abstract void addGlobalObject(final PObject pObj);

	/**
	 * This method is called when the end of data for a specific PObject
	 * is found.
	 * 
	 * <p>This method will only be called for objects that are to be included.
	 *
	 * @param target PObject to perform final operations on
	 */
	protected void finishObject(@SuppressWarnings("unused")
	PObject target)
	{
		// Placeholder implementation
	}

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
		if ((parsedObject == null) || (parsedObject.getDisplayName() == null)
			|| (parsedObject.getDisplayName().trim().length() == 0)
			|| (parsedObject.getKeyName() == null)
			|| (parsedObject.getKeyName().trim().length() == 0))
		{
			return false;
		}

		// If includes were present, check includes for given object
		List<String> includeItems = currentSource.getIncludeItems();

		if (!includeItems.isEmpty())
		{
			return includeItems.contains(parsedObject.getKeyName());
		}
		// If excludes were present, check excludes for given object
		List<String> excludeItems = currentSource.getExcludeItems();

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
	protected abstract T getObjectKeyed(String aKey);

	/**
	 * This method loads a single LST formatted file.
	 * @param sourceEntry CampaignSourceEntry containing the absolute file path
	 * or the URL from which to read LST formatted data.
	 */
	protected void loadLstFile(CampaignSourceEntry sourceEntry)
	{
		setChanged();
		String urlString = Constants.EMPTY_STRING;
		try
		{
			urlString = CoreUtility.fileToURL(sourceEntry.getFile());
			notifyObservers(new URL(urlString));
		}
		catch (MalformedURLException e)
		{
			try
			{
				// Notify of the failed file
				setChanged();
				notifyObservers(new Exception(PropertyFactory
					.getFormattedString("Exceptions.LstFileLoader.InvalidURL", //$NON-NLS-1$
						urlString)));
				// Notify of a dummy file, so that anyone counting files processed
				// for a progress dialog or something will get a consistent count
				setChanged();
				notifyObservers(new URL("http://f")); //$NON-NLS-1$
			}
			catch (MalformedURLException e1)
			{
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
			logError(PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.LoadError", //$NON-NLS-1$
				sourceEntry.getFile(), ple.getMessage()));
		}

		final String aString = dataBuffer.toString();
		final StringTokenizer fileLines =
				new StringTokenizer(aString, LINE_SEPARATOR);
		T target = null;
		ArrayList<ModEntry> classModLines = null;

		int currentLineNumber = 0;
		while (fileLines.hasMoreTokens())
		{
			++currentLineNumber;
			final String line = fileLines.nextToken().trim();
			if (isComment(line))
			{
				continue;
			}
			String[] tokens = line.split(FIELD_SEPARATOR);

			// Check for continuation of class mods
			if (classModLines != null)
			{
				// TODO - Figure out why we need to check CLASS: in this file.
				if (tokens[0].startsWith("CLASS:")) //$NON-NLS-1$
				{
					modEntryList.add(classModLines);
					classModLines = null;
				}
				else
				{
					// Add the line to the class mod and don't process it yet.
					classModLines.add(new ModEntry(sourceEntry, line,
						currentLineNumber, sourceMap));
					continue;
				}
			}

			// check for comments, copies, mods, and forgets
			if (isComment(line))
			{
				continue;
			}
			// TODO - Figure out why we need to check SOURCE in this file
			else if (line.startsWith("SOURCE")) //$NON-NLS-1$
			{
				sourceMap = SourceLoader.parseLine(line, sourceEntry.getFile());
			}
			else if (tokens[0].indexOf(COPY_SUFFIX) > 0)
			{
				copyLineList.add(line);
			}
			else if (tokens[0].indexOf(MOD_SUFFIX) > 0)
			{
				// TODO - Figure out why we need to check CLASS: in this file.
				if (tokens[0].startsWith("CLASS:")) //$NON-NLS-1$
				{
					// As CLASS:abc.MOD can be followed by level lines, we place the
					// lines into a list for processing in a group afterwards
					classModLines = new ArrayList<ModEntry>();
					classModLines.add(new ModEntry(sourceEntry, line,
						currentLineNumber, sourceMap));
				}
				else
				{
					List<ModEntry> modLines = new ArrayList<ModEntry>();
					modLines.add(new ModEntry(sourceEntry, line,
						currentLineNumber, sourceMap));
					modEntryList.add(modLines);
				}
			}
			else if (tokens[0].indexOf(FORGET_SUFFIX) > 0)
			{
				forgetLineList.add(line);
			}
			else
			{
				try
				{
					target = parseLine(target, line, sourceEntry);
					// TODO - This is kind of a hack but we need to make sure
					// that classes get added.
					completeObject(target);
				}
				catch (PersistenceLayerException ple)
				{
					logError(PropertyFactory.getFormattedString(
						"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
						sourceEntry.getFile(), currentLineNumber, ple
							.getMessage()));
					Logging.debugPrint("Parse error:", ple); //$NON-NLS-1$
				}
				catch (Throwable t)
				{
					logError(PropertyFactory.getFormattedString(
						"Errors.LstFileLoader.ParseError", //$NON-NLS-1$
						sourceEntry.getFile(), currentLineNumber, t
							.getMessage()));
					Logging.errorPrint(PropertyFactory
						.getString("Errors.LstFileLoader.Ignoring"), //$NON-NLS-1$
						t);
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
	protected void sortFilesForOptimalLoad(List<?> fileList)
	{
		if ((fileList.isEmpty()) || (fileList.get(0) instanceof String))
		{
			// avoid extra creation, sorting, etc if this is a generic
			// list of files
			return;
		}

		ArrayList<CampaignSourceEntry> fList =
				(ArrayList<CampaignSourceEntry>) fileList;

		ArrayList<CampaignSourceEntry> normalFiles =
				new ArrayList<CampaignSourceEntry>();
		ArrayList<CampaignSourceEntry> includeFiles =
				new ArrayList<CampaignSourceEntry>();
		ArrayList<CampaignSourceEntry> excludeFiles =
				new ArrayList<CampaignSourceEntry>();

		for (CampaignSourceEntry sourceEntry : fList)
		{
			String fileInfo = sourceEntry.getFile();

			if (fileInfo.indexOf(INCLUDE_TAG) > 0)
			{
				if (!includeFiles.contains(sourceEntry))
				{
					includeFiles.add(sourceEntry);
				}
			}
			else if (fileInfo.indexOf(EXCLUDE_TAG) > 0)
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

		fList.clear();

		// Optimal load:  Entire files, exclude files, include files
		// TODO: compare include/exclude file lists?
		fList.addAll(normalFiles);
		fList.addAll(excludeFiles);
		fList.addAll(includeFiles);
	}

	/**
	 * This method will perform a single .COPY operation.
	 *
	 * @param baseName String name of the object to copy
	 * @param copyName String name of the target object
	 * @throws PersistenceLayerException 
	 */
	private void performCopy(String baseKey, String copyName)
		throws PersistenceLayerException
	{
		T object = getObjectKeyed(baseKey);

		try
		{
			if (object == null)
			{
				logError(PropertyFactory.getFormattedString(
					"Errors.LstFileLoader.CopyObjectNotFound", //$NON-NLS-1$
					baseKey));

				return;
			}

			PObject clone = object.clone();
			clone.setName(copyName);
			clone.setKeyName(copyName);
			completeObject(clone);
		}
		catch (CloneNotSupportedException e)
		{
			logError(PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.CopyNotSupported", //$NON-NLS-1$
				object.getClass().getName(), baseKey, copyName));
		}
	}

	/**
	 * This method will perform a single .COPY operation based on the LST
	 * file content.
	 * @param lstLine String containing the LST source for the
	 * .COPY operation
	 * @throws PersistenceLayerException 
	 */
	private void performCopy(String lstLine) throws PersistenceLayerException
	{
		final int nameEnd = lstLine.indexOf(COPY_SUFFIX);
		final String baseName = lstLine.substring(0, nameEnd);
		final String copyName = lstLine.substring(nameEnd + 6);

		performCopy(baseName, copyName);
	}

	/**
	 * This method will perform a multi-line .MOD operation. This is used
	 * for example in MODs of CLASSES which can have multiple lines. Loaders
	 * can [typically] use the name without checking
	 * for (or stripping off) .MOD due to the implementation of
	 * PObject.setName()
	 * @param entryList
	 */
	private void performMod(List<ModEntry> entryList)
	{
		ModEntry entry = entryList.get(0);
		// get the name of the object to modify, trimming off the .MOD
		int nameEnd = entry.getLstLine().indexOf(MOD_SUFFIX);
		String key = entry.getLstLine().substring(0, nameEnd);

		// remove the leading tag, if any (i.e. CLASS:Druid.MOD
		int nameStart = key.indexOf(':');

		if (nameStart > 0)
		{
			key = key.substring(nameStart + 1);
		}

		if (excludedObjects.contains(key))
		{
			return;
		}
		// get the actual object to modify
		T object = getObjectKeyed(key);

		if (object == null)
		{
			logError(PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.ModObjectNotFound", //$NON-NLS-1$
				entry.getSource().getFile(), entry.getLineNumber(), key));
			return;
		}

		// modify the object
		try
		{
			for (ModEntry element : entryList)
			{
				try
				{
					boolean noSource = object.getSourceEntry() == null;
					int hashCode = 0;
					if (!noSource)
					{
						hashCode = object.getSourceEntry().hashCode();
					}

					parseLine(object, element.getLstLine(), element.getSource());

					if ((noSource && object.getSourceEntry() != null)
						|| (!noSource && hashCode != object.getSourceEntry()
							.hashCode()))
					{
						// We never had a source and now we do so set the source
						// map or we did have a source and now the hashCode is
						// different so the MOD line must have updated it.
						try
						{
							object.setSourceMap(element.getSourceMap());
						}
						catch (ParseException notUsed)
						{
							Logging.errorPrintLocalised(
								"Errors.LstFileLoader.ParseDate", sourceMap); //$NON-NLS-1$
						}
					}
				}
				catch (PersistenceLayerException ple)
				{
					logError(PropertyFactory.getFormattedString(
						"Errors.LstFileLoader.ModParseError", //$NON-NLS-1$
						element.getSource().getFile(), element.getLineNumber(),
						ple.getMessage()));
				}
			}
			completeObject(object);
		}
		catch (PersistenceLayerException ple)
		{
			logError(PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.ModParseError", //$NON-NLS-1$
				entry.getSource().getFile(), entry.getLineNumber(), ple
					.getMessage()));
		}
	}

	/**
	 * This method will process the lines containing a .COPY directive
	 * @throws PersistenceLayerException 
	 */
	private void processCopies() throws PersistenceLayerException
	{
		for (String objKey : copyLineList)
		{
			if (!excludedObjects.contains(objKey))
			{
				performCopy(objKey);
			}
		}
		copyLineList.clear();
	}

	/**
	 * This method will process the lines containing a .FORGET directive
	 */
	private void processForgets()
	{

		for (String forgetKey : forgetLineList)
		{
			forgetKey =
					forgetKey.substring(0, forgetKey.indexOf(FORGET_SUFFIX));

			if (excludedObjects.contains(forgetKey))
			{
				continue;
			}
			// Commented out so that deprcated method no longer used
			// performForget(forgetName);

			T objToForget = getObjectKeyed(forgetKey);
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
		for (List<ModEntry> modEntry : modEntryList)
		{
			performMod(modEntry);
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
		private Map<String, String> sourceMap = null;

		/**
		 * ModEntry constructor.
		 * @param aSource CampaignSourceEntry containing the MOD line
		 *         [must not be null]
		 * @param aLstLine LST syntax modification
		 *         [must not be null]
		 * @param aLineNumber
		 * @param aSourceMap
		 * 
		 * @throws IllegalArgumentException if aSource or aLstLine is null.
		 */
		public ModEntry(final CampaignSourceEntry aSource,
			final String aLstLine, final int aLineNumber,
			final Map<String, String> aSourceMap)
		{
			super();

			// These are programming errors so the msgs don't need to be 
			// internationalized.
			if (aSource == null)
			{
				throw new IllegalArgumentException("source must not be null"); //$NON-NLS-1$
			}

			if (aLstLine == null)
			{
				throw new IllegalArgumentException("lstLine must not be null"); //$NON-NLS-1$
			}

			this.source = aSource;
			this.lstLine = aLstLine;
			this.lineNumber = aLineNumber;
			this.sourceMap = aSourceMap;
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
		public Map<String, String> getSourceMap()
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
	public CampaignSourceEntry getCurrentSource()
	{
		return currentSource;
	}

	/**
	 * @param aCurrentSource The currentSource to set.
	 */
	public void setCurrentSource(CampaignSourceEntry aCurrentSource)
	{
		this.currentSource = aCurrentSource;
	}
}
