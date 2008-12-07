/*
 * CampaignURL.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 28/01/2008
 *
 * $Id$
 */

package pcgen.core;

import java.net.URL;

/**
 * <code>CampaignURL</code> encapsulates a typed and labelled URL for a 
 * campaign.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CampaignURL implements Comparable<CampaignURL>
{
	/** An enum for the various types of Campaign URLs. */
	public enum URLKind {
		/** Link is to a web site */
		WEBSITE,
		/** Link is to a survey */
		SURVEY,
		/** Link is to an eCommerce site to purchase the source */
		PURCHASE
	}

	/** The kind of url */
	private final URLKind urlKind;
	/** The name of the url (only for ecommerce sites) */
	private final String urlName;
	/** The URL itself */
	private final URL url;
	/** The displayed description of the url */
	private final String urlDesc;
	
	/**
	 * Instantiates a new campaign url.
	 * 
	 * @param urlKind the kind of url
	 * @param urlName the name of the url (only for ecommerce sites) 
	 * @param url The URL itself.
	 * @param urlDesc the displayed description of the url 
	 */
	public CampaignURL(URLKind urlKind, String urlName, URL url,
		String urlDesc)
	{
		this.urlKind = urlKind;
		this.urlName = urlName;
		this.url = url;
		this.urlDesc = urlDesc;
	}

	/**
	 * @return the urlKind
	 */
	public URLKind getUrlKind()
	{
		return urlKind;
	}

	/**
	 * @return the urlName
	 */
	public String getUrlName()
	{
		return urlName;
	}

	/**
	 * @return the url
	 */
	public URL getUrl()
	{
		return url;
	}

	/**
	 * @return the urlDesc
	 */
	public String getUrlDesc()
	{
		return urlDesc;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final CampaignURL that)
	{
	    if (this == that)
	    {
			return 0;
	    }

	    if (that.getUrlKind() != urlKind)
		{
			return urlKind.compareTo(that.getUrlKind());
		}

	    if (!that.getUrlName().equals(urlName))
	    {
			return urlName.compareTo(that.getUrlName());
	    }

	    if (!that.getUrl().equals(url))
	    {
			return url.toString().compareTo(that.getUrl().toString());
	    }

		return urlDesc.compareTo(that.getUrlDesc());
	}

	
}
