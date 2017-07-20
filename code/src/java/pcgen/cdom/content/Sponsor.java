/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import java.net.URI;
import java.net.URL;

import pcgen.cdom.base.Loadable;

public class Sponsor implements Loadable
{

	private URI sourceURI;
	//private URL smallImage;
	private URL largeImage;
	private URL bannerImage;
	private String sponsorName;
	private String sponsorKey;
	private String sponsorText;

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	@Override
	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	@Override
	public void setName(String name)
	{
		sponsorKey = name;
	}

	@Override
	public String getDisplayName()
	{
		return sponsorKey;
	}

	public void setSponsorName(String name)
	{
		sponsorName = name;
	}

	public String getSponsorName()
	{
		return sponsorName;
	}

	@Override
	public String getKeyName()
	{
		return sponsorKey;
	}

	@Override
	public boolean isInternal()
	{
		return false;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}

	public void setSmallImage(URL url)
	{
		//smallImage = url;
	}

	public void setLargeImage(URL url)
	{
		largeImage = url;
	}

	public void setBannerImage(URL url)
	{
		bannerImage = url;
	}

	public URL getBannerImage()
	{
		return bannerImage;
	}

	public URL getLargeImage()
	{
		return largeImage;
	}

	public void setText(String text)
	{
		sponsorText = text;
	}

	public String getText()
	{
		return sponsorText;
	}

}
