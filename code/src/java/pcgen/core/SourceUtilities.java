/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Refactored out of PObject July 22, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import pcgen.util.Logging;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * Utilities related to SourcedObject
 */
public final class SourceUtilities
{
	private SourceUtilities()
	{
		// Can't instantiate utility class
	}

	/**
	 * Return the source in a particular form
	 * @param so
	 * @param sourceDisplay
	 * @param includePage
	 * @return the source in a particular form
	 */
	public static String returnSourceInForm(SourcedObject so, final int sourceDisplay, final boolean includePage)
	{
		final StringBuffer buf = new StringBuffer();
		final Campaign _sourceCampaign = so.getSourceCampaign();
		String key;
		String publisher = "";
	
		switch (sourceDisplay)
		{
			case Constants.SOURCELONG:
				key = "LONG";
	
				break;
	
			case Constants.SOURCEMEDIUM:
				key = "LONG";
	
				break;
	
			case Constants.SOURCESHORT:
				key = "SHORT";
	
				break;
	
			case Constants.SOURCEWEB:
				key = "WEB";
	
				break;
	
			case Constants.SOURCEPAGE:
				key = "PAGE";
	
				break;
	
			case Constants.SOURCEDATE:
				key = "DATE";
	
				break;
	
			default:
				Logging.errorPrint("Unknown source display form in returnSourceInForm: " + sourceDisplay);
				key = "LONG"; //A reasonable default.
	
				break;
		}
	
		// get SOURCE for this item with desired key
		String aSource = so.getSourceWithKey(key);
	
		if (_sourceCampaign != null)
		{
			// if sourceCampaign object exists, get it's publisher entry for same key
			publisher = _sourceCampaign.getPublisherWithKey(key);
	
			// if this item's source is null, try to get it from sourceCampaign object
			if (aSource == null)
			{
				aSource = _sourceCampaign.getSourceWithKey(key);
			}
		}
	
		if (aSource == null)
		{
			aSource = "";
		}
	
		// append the source entry to the return string
		if (sourceDisplay == Constants.SOURCESHORT || sourceDisplay == Constants.SOURCEMEDIUM)
		{
			buf.append(aSource);
		}
		else
		{
			if (publisher.length() > 0)
			{
				buf.append(publisher);
				buf.append(" - ");
			}
			buf.append(aSource);
		}
	
		// if the page is desired, append that entry to the returned string
		if (includePage && (sourceDisplay != Constants.SOURCEWEB))
		{
			aSource = so.getSourceWithKey("PAGE");
	
			if (aSource != null)
			{
				buf.append(", ").append(aSource);
			}
		}
	
		return buf.toString();
	}

}
