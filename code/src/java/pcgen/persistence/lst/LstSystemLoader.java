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
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.lang.StringUtil;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.persistence.SystemLoader;

/**
 * ???
 * @deprecated
 */
@Deprecated
public final class LstSystemLoader implements SystemLoader
{

	private final Map<String, List<URI>> chosenCampaignSourcefiles =
			new HashMap<>();

	/**
	 * @see pcgen.persistence.SystemLoader#setChosenCampaignSourcefiles(java.util.List, pcgen.core.GameMode)
	 * 
	 * CODE-1889 to remove use of this method
	 */
    @Override
	public void setChosenCampaignSourcefiles(List<URI> l, GameMode game)
	{
		List<URI> files = chosenCampaignSourcefiles.get(game.getName());
		if (files == null)
		{
			files = new ArrayList<>();
			chosenCampaignSourcefiles.put(game.getName(), files);
		}
		files.clear();
		files.addAll(l);
		SettingsHandler.getOptions().setProperty(
			"pcgen.files.chosenCampaignSourcefiles." + game.getName(),
			StringUtil.join(files, ", "));
	}

	/**
	 * @see pcgen.persistence.SystemLoader#getChosenCampaignSourcefiles(pcgen.core.GameMode)
	 * 
	 * CODE-1889 to remove use of this method
	 */
    @Override
	public List<URI> getChosenCampaignSourcefiles(GameMode game)
	{
		List<URI> files = chosenCampaignSourcefiles.get(game.getName());
		if (files == null)
		{
			files = new ArrayList<>();
			chosenCampaignSourcefiles.put(game.getName(), files);
		}
		return files;
	}
}
