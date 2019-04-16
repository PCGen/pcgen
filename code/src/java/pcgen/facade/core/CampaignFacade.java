/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.facade.core;

import java.util.List;

import pcgen.core.GameMode;
import pcgen.facade.util.ListFacade;

public interface CampaignFacade
{

	public boolean showInMenu();

	public ListFacade<GameMode> getGameModes();

	public String getName();

	public String getPublisher();

	public String getFormat();

	public String getSetting();

	/**
	 * @return A text description of the type of book the source represents.
	 */
	public String getBookTypes();

	/**
	 * @return A list of text descriptions of the type of book the source represents.
	 */
	public List<String> getBookTypeList();

	/**
	 * @return The name of the implementation status of the source.
	 */
	public String getStatus();

	public String getKeyName();

	/**
	 * @return The abbreviation for the source (e.g. APG)
	 */
	public String getSourceShort();

}
