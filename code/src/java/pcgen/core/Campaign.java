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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.GameModeFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.GameReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import pcgen.rules.context.RuntimeLoadContext;

/**
 * <code>Campaign</code> is a source or campaign defined in a *.pcc file.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class Campaign extends PObject implements CampaignFacade
{

	public Campaign()
	{
		put(ObjectKey.SOURCE_CAMPAIGN, this);
	}

	/**
	 * Returns a list of the Campaign objects that were loaded by this Campaign.
	 * 
	 * @return A list of <tt>Campaign</tt>s loaded by this Campaign.
	 */
	public List<Campaign> getSubCampaigns()
	{
		final List<CampaignSourceEntry> pccFiles = getSafeListFor(ListKey.FILE_PCC);

		final List<Campaign> ret = new ArrayList<Campaign>(pccFiles.size());
		
		for ( final CampaignSourceEntry fileName : pccFiles )
		{
			final Campaign campaign = Globals.getCampaignByURI(fileName.getURI(), true);
			ret.add(campaign);
		}
		return ret;
	}

	private ConsolidatedListCommitStrategy masterLCS = new ConsolidatedListCommitStrategy();
	private GameReferenceContext gameRefContext = new GameReferenceContext();
	private LoadContext context = new RuntimeLoadContext(gameRefContext, masterLCS);

	public LoadContext getCampaignContext()
	{
		return context;
	}
	
	public void applyTo(ReferenceContext rc)
	{
		for (ReferenceManufacturer<?> rm : gameRefContext.getAllManufacturers())
		{
			GameMode.resolveReferenceManufacturer(rc, rm);
		}
	}

	public boolean showInMenu()
	{
		return getSafe(ObjectKey.SHOW_IN_MENU);
	}

	private DefaultListFacade<GameModeFacade> gameModes = null;

	public ListFacade<GameModeFacade> getGameModes()
	{
		if (gameModes == null)
		{
			gameModes = new DefaultListFacade<GameModeFacade>();
			List<String> modes = getSafeListFor(ListKey.GAME_MODE);
			for (String string : modes)
			{
				for (GameMode game : SystemCollections.getUnmodifiableGameModeList())
				{
					if (game.getAllowedModes().contains(string))
					{
						gameModes.addElement(game);
					}
				}
			}
		}
		return gameModes;
	}

	public String getName()
	{
		return getKeyName();
	}

	public String getPublisher()
	{
		return get(StringKey.DATA_PRODUCER);
	}

	public String getFormat()
	{
		return get(StringKey.DATA_FORMAT);
	}

	public String getSetting()
	{
		return get(StringKey.CAMPAIGN_SETTING);
	}
}
