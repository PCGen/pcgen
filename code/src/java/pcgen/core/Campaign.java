/*
 * Campaign.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.TransparentReferenceManufacturer;
import pcgen.core.CampaignURL.URLKind;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.GameReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;

/**
 * <code>Campaign</code> is a source or campaign defined in a *.pcc file.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class Campaign extends PObject
{
	private Map<String, String> publisherMap = new HashMap<String, String>();
	private Properties options = new Properties();
	private List<CampaignURL> urlList = new ArrayList<CampaignURL>();
	private boolean isD20;
	private boolean isLicensed;
	private boolean isLoaded;
	private boolean isOGL;
	private boolean isMature;
	private boolean showInMenu;
	private boolean isInitted;

	/**
	 * Constructor
	 */
	public Campaign() {
		super();
	}

	/**
	 * This method is used to addText a brief segment of license text to the
	 * OGL license information for the campaign.
	 * @param license String piece of information to addText to the OGL license.
	 */
	public void addLicense(final String license)
	{
		if (license.equals(".CLEAR"))
		{
			removeListFor(ListKey.LICENSE);
		}
		else
		{
			addToListFor(ListKey.LICENSE, license);
		}
	}

	/**
	 * This method is used to addText an external file to the
	 * license information required for the source.  The added
	 * file may be either relative to the default directory or
	 * URL syntax; at present, however, the only URL syntax that
	 * is honored by the GUI are those refering to the file system
	 * i.e. file:/etc/etc/etc URLs.
	 * @param licenseFile String location of a license file
	 */
	public void addLicenseFile(final URI licenseFile)
	{
		addToListFor(ListKey.LICENSE_FILE, licenseFile);
	}

	/**
	 *
	 * @param file
	 */
	public void addPccFile(final URI file)
	{
		addToListFor(ListKey.FILE_PCC, file);
	}

	/**
	 * Add section 15 info
	 * @param section15
	 */
	public void addSection15(final String section15)
	{
		if (section15.equals(".CLEAR"))
		{
			removeListFor(ListKey.SECTION_15);
		}
		else
		{
			addToListFor(ListKey.SECTION_15, section15);
		}
	}

	/**
	 * Get the book type
	 * @return bookType
	 */
	public String getBookType()
	{
		String characteristic = get(StringKey.BOOK_TYPE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the destination
	 * @return destination
	 */
	public String getDestination()
	{
		String characteristic = get(StringKey.DESTINATION);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Returns the name of the game this campaign is intended for.
	 * @return the name of the game
	 */
	public List<String> getGameModes()
	{
		return getSafeListFor(ListKey.GAME_MODE);
	}

	/**
	 * Returns the game modes in a Human readable format
	 *
	 * @return game mode as a String
	 **/
	public String getGameModeString()
	{
		final StringBuffer sb = new StringBuffer();
		List<String> gameModeList = getSafeListFor(ListKey.GAME_MODE);

		for (Iterator<String> i = gameModeList.iterator(); i.hasNext();)
		{
			final String gameMode = i.next();
			sb.append(gameMode);

			if (i.hasNext())
			{
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	/**
	 * Get the genre
	 * @return genre
	 */
	public String getGenre()
	{
		String characteristic = get(StringKey.GENRE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the help
	 * @return help
	 */
	public String getHelp()
	{
		String characteristic = get(StringKey.HELP);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the info on this campaign
	 */
	public String getInfoText()
	{
		String characteristic = get(StringKey.INFO_TEXT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the licenses list
	 * @return license
	 */
	public List<String> getLicenses()
	{
		return getSafeListFor(ListKey.LICENSE);
	}

	/**
	 * Returns the license info for this campaign's source(book).
	 * @return the license
	 */
	public String getLicenseString()
	{
		StringBuffer sb = new StringBuffer();

		for ( String license : getSafeListFor(ListKey.LICENSE) )
		{
			sb.append(license).append("<br>");
		}

		return sb.toString();
	}

	/**
	 * Get the license files
	 * @return license files
	 */
	public List<URI> getLicenseFiles()
	{
		return getSafeListFor(ListKey.LICENSE_FILE);
	}

	/**
	 * Returns the pccFileList.
	 * @return List
	 */
	public List<URI> getPccFiles()
	{
		return getSafeListFor(ListKey.FILE_PCC);
	}

	/**
	 * Get the publisher longname
	 * @return publisher long name
	 */
	public String getPubNameLong()
	{
		String characteristic = get(StringKey.PUB_NAME_LONG);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * get the publisher short name
	 * @return publisher short name
	 */
	public String getPubNameShort()
	{
		String characteristic = get(StringKey.PUB_NAME_SHORT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get the publisher web name
	 * @return publisher web name
	 */
	public String getPubNameWeb()
	{
		String characteristic = get(StringKey.PUB_NAME_WEB);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Get section 15 as a List
	 * @return section 15
	 */
	public List<String> getSection15s()
	{
		return getSafeListFor(ListKey.SECTION_15);
	}

	/**
	 * Returns the section 15 info for this campaign's source(book).
	 * @return the section 15 info
	 */
	public String getSection15String()
	{
		StringBuffer sb = new StringBuffer();

		for ( String license : getSafeListFor(ListKey.SECTION_15) )
		{
			sb.append(license).append("<br>");
		}

		return sb.toString();
	}

	/**
	 * Get the setting
	 * @return setting
	 */
	public String getSetting()
	{
		String characteristic = get(StringKey.SETTING);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Queries to see if this campaign is of a gameMode
	 * @param gameMode    name of gameMode to test for
	 * @return        boolean if present
	 **/
	public boolean isGameMode(final String gameMode)
	{
		return containsInList(ListKey.GAME_MODE, gameMode);
	}

	/**
	 * Queries to see if this campaign is of a gameMode
	 * @param gameModeList    list of gameModes to test for
	 * @return        boolean if present
	 **/
	public boolean isGameMode(final List<String> gameModeList)
	{
		for ( String gameMode : gameModeList )
		{
			if (containsInList(ListKey.GAME_MODE, gameMode))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the name of the game this campaign is intended for.
	 * @param gameMode name or '|' delimited list of names
	 */
	public void setGameMode(final String gameMode)
	{
		final StringTokenizer aTok = new StringTokenizer(gameMode, "|");
		removeListFor(ListKey.GAME_MODE);

		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();
			if (!(isGameMode(tok)))
			{
				addToListFor(ListKey.GAME_MODE, tok);
			}
		}
	}

	/**
	 * Set the publisher long name
	 * @param pubNameLong
	 */
	public void setPubNameLong(final String pubNameLong)
	{
		addPublisher("LONG:" + pubNameLong);
		put(StringKey.PUB_NAME_LONG, pubNameLong);
	}

	/**
	 * Set the publisher short name
	 * @param pubNameShort
	 */
	public void setPubNameShort(final String pubNameShort)
	{
		addPublisher("SHORT:" + pubNameShort);
		put(StringKey.PUB_NAME_SHORT, pubNameShort);
	}

	/**
	 * Set the publisher web name
	 * @param pubNameWeb
	 */
	public void setPubNameWeb(final String pubNameWeb)
	{
		addPublisher("WEB:" + pubNameWeb);
		put(StringKey.PUB_NAME_WEB, pubNameWeb);
	}

	/**
	 * @return whether or not the d20 info will pop up when this campaign is loaded
	 */
	public boolean isD20()
	{
		return isD20;
	}

	/**
	 * Set the isd20 flag
	 * @param isD20
	 */
	public void setIsD20(final boolean isD20)
	{
		this.isD20 = isD20;
	}

	/**
	 * @return Has the campaign been initialised?
	 */
	public boolean isInitted()
	{
		return isInitted;
	}

	/**
	 * Set the campaign initialised flag.
	 * @param isInitted The new flag value
	 */
	public void setInitted(boolean isInitted)
	{
		this.isInitted = isInitted;
	}

	/**
	 * Sets whether this campaign is licensed.
	 * @param isLicensed
	 */
	public void setIsLicensed(final boolean isLicensed)
	{
		this.isLicensed = isLicensed;
	}

	/**
	 * Sets whether the campaign is loaded.
	 * @param isLoaded
	 */
	public void setIsLoaded(final boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}

	/**
	 * Set the isOGL flag
	 * @param isOGL
	 */
	public void setIsOGL(final boolean isOGL)
	{
		this.isOGL = isOGL;
	}

	/**
	 * Set the isMature flag
	 * @param isMature
	 */
	public void setIsMature(final boolean isMature)
	{
		this.isMature = isMature;
	}

	/**
	 * Returns whether this campaign is licensed
	 * @return true if this campaign is licensed
	 */
	public boolean isLicensed()
	{
		return isLicensed;
	}

	/**
	 * @return true if the campaign (source file set) is loaded.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * @return whether or not the OGL will pop up when this campaign is loaded
	 */
	public boolean isOGL()
	{
		return isOGL;
	}

	/**
	 * @return whether or not the Mature dataset warning will pop up when this campaign is loaded
	 */
	public boolean isMature()
	{
		return isMature;
	}

	/**
	 * Set the campaign options
	 * @param options
	 */
	public void setOptions(final Properties options)
	{
		this.options = options;
	}

	/**
	 * @return the options which are to apply to this campaign
	 */
	public Properties getOptions()
	{
		return options;
	}

	/**
	 * Get the campaign options as a List
	 * @return campaign options
	 */
	public List<String> getOptionsList()
	{
		final List<String> aList = new ArrayList<String>();

		if (options != null)
		{
			for (Enumeration<?> e = options.propertyNames(); e.hasMoreElements();)
			{
				aList.add(e.nextElement().toString());
			}
		}

		return aList;
	}

	/**
	 * Returns the publisherMap.
	 * @return Map
	 */
	public Map<String, String> getPublisherMap()
	{
		return publisherMap;
	}

	/**
	 * Get the publisher with key
	 * @param key
	 * @return publisher
	 */
	public String getPublisherWithKey(final String key)
	{
		final String val = publisherMap.get(key);

		return (val != null) ? val : "";
	}

	/**
	 * Sets whether this campaign should be listed in the campaigns menu.
	 * @param showInMenu
	 */
	public void setShowInMenu(final boolean showInMenu)
	{
		this.showInMenu = showInMenu;
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from.  In this case, it will return (this).
	 * @return Campaign instance referencing the file containing the
	 *         source for this object
	 */
	public Campaign getSourceCampaign()
	{
		return this;
	}

	/**
	 * Add a publisher
	 * @param argPublisher
	 */
	public void addPublisher(final String argPublisher)
	{
		final String publisher;

		if (argPublisher.startsWith("PUBNAME"))
		{
			publisher = argPublisher.substring(7);
		}
		else
		{
			publisher = argPublisher;
		}

		final String key = publisher.substring(0, publisher.indexOf(":"));
		publisherMap.put(key, publisher.substring(publisher.indexOf(":") + 1));
	}

	/**
	 * Returns whether this campaign should be listed in the campaigns menu
	 * @return true if this campaign should be listed in the campaigns menu
	 */
	public boolean canShowInMenu()
	{
		return showInMenu;
	}

	/**
	 * Returns a list of the Campaign objects that were loaded by this Campaign.
	 * 
	 * @return A list of <tt>Campaign</tt>s loaded by this Campaign.
	 */
	public List<Campaign> getSubCampaigns()
	{
		final List<URI> pccFiles = getPccFiles();

		final List<Campaign> ret = new ArrayList<Campaign>(pccFiles.size());
		
		for ( final URI fileName : pccFiles )
		{
			final Campaign campaign = Globals.getCampaignByURI(fileName, true);
			ret.add(campaign);
		}
		return ret;
	}

	@Override
	public Campaign clone()
	{
		Campaign newCampaign = null;

		try
		{
			newCampaign = (Campaign) super.clone();
			newCampaign.options = (Properties) options.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return newCampaign;
	}

	/**
	 * Adds the url.
	 * 
	 * @param campUrl the url to be added
	 */
	public void addURL(CampaignURL campUrl)
	{
		urlList.add(campUrl);
	}

	/**
	 * @return the urlList
	 */
	public List<CampaignURL> getUrlList()
	{
		return Collections.unmodifiableList(urlList);
	}

	/**
	 * Returnr a list of urls of the specified kind.
	 * @return the urlList
	 */
	public List<CampaignURL> getUrlListForKind(URLKind kind)
	{
		List<CampaignURL> kindList = new ArrayList<CampaignURL>();
		for (CampaignURL url : urlList)
		{
			if (url.getUrlKind() == kind)
			{
				kindList.add(url);
			}
		}
		return kindList;
	}

	private ConsolidatedListCommitStrategy masterLCS = new ConsolidatedListCommitStrategy();
	private GameReferenceContext gameRefContext = new GameReferenceContext();
	private LoadContext context = new RuntimeLoadContext(gameRefContext, masterLCS);

	public LoadContext getCampaignContext()
	{
		return context;
	}
	
	public void applyTo(AbstractReferenceContext rc)
	{
		for (TransparentReferenceManufacturer<? extends CDOMObject> rm : gameRefContext
				.getAllManufacturers())
		{
			resolveReferenceManufacturer(rc, rm);
		}
	}

	private <T extends CDOMObject> void resolveReferenceManufacturer(
			AbstractReferenceContext rc, TransparentReferenceManufacturer<T> rm)
	{
		rm.resolveUsing(rc.getManufacturer(rm.getReferenceClass()));
	}

}
