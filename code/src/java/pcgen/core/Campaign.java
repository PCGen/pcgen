/*
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
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.NonInteractive;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.GameReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;

/**
 * {@code Campaign} is a source or campaign defined in a *.pcc file.
 */
public class Campaign extends PObject implements NonInteractive
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

        final List<Campaign> ret = new ArrayList<>(pccFiles.size());

        for (final CampaignSourceEntry fileName : pccFiles)
        {
            final Campaign campaign = Globals.getCampaignByURI(fileName.getURI(), true);
            if (campaign != null)
            {
                ret.add(campaign);
            }
        }
        return ret;
    }

    /**
     * Returns a list of the CampaignSourceEntry objects that were referenced
     * by this Campaign but that could nto be found.
     *
     * @return A list of <tt>CampaignSourceEntry</tt> objects that could not be found.
     */
    public List<CampaignSourceEntry> getNotFoundSubCampaigns()
    {
        final List<CampaignSourceEntry> pccFiles = getSafeListFor(ListKey.FILE_PCC);

        final List<CampaignSourceEntry> ret = new ArrayList<>();

        for (final CampaignSourceEntry cse : pccFiles)
        {
            final Campaign campaign = Globals.getCampaignByURI(cse.getURI(), true);
            if (campaign == null)
            {
                ret.add(cse);
            }
        }
        return ret;
    }

    private ConsolidatedListCommitStrategy masterLCS = new ConsolidatedListCommitStrategy();
    private GameReferenceContext gameRefContext = GameReferenceContext.createGameReferenceContext();
    private LoadContext context = new RuntimeLoadContext(gameRefContext, masterLCS);

    public LoadContext getCampaignContext()
    {
        return context;
    }

    public void applyTo(AbstractReferenceContext rc)
    {
        for (ReferenceManufacturer<?> rm : gameRefContext.getAllManufacturers())
        {
            GameMode.resolveReferenceManufacturer(rc, rm);
        }
    }

    private DefaultListFacade<GameMode> gameModes = null;

    public ListFacade<GameMode> getGameModes()
    {
        if (gameModes == null)
        {
            gameModes = new DefaultListFacade<>();
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
}
