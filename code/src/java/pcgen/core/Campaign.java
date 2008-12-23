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
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.TransparentReferenceManufacturer;
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
	private boolean isD20;
	private boolean isLoaded;
	private boolean isInitted;

	/**
	 * Constructor
	 */
	public Campaign() {
		super();
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
	 * Sets whether the campaign is loaded.
	 * @param isLoaded
	 */
	public void setIsLoaded(final boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}

	/**
	 * @return true if the campaign (source file set) is loaded.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from.  In this case, it will return (this).
	 * @return Campaign instance referencing the file containing the
	 *         source for this object
	 */
	@Override
	public Campaign getSourceCampaign()
	{
		return this;
	}

	/**
	 * Returns a list of the Campaign objects that were loaded by this Campaign.
	 * 
	 * @return A list of <tt>Campaign</tt>s loaded by this Campaign.
	 */
	public List<Campaign> getSubCampaigns()
	{
		final List<URI> pccFiles = getSafeListFor(ListKey.FILE_PCC);

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
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return newCampaign;
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
