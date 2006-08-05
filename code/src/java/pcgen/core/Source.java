/*
 * Source.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class represents the generally unchanging information about the source
 * of an objcet.
 * 
 * <p>This includes information about the name and date of the source material
 * the object came from.  Page information is not included since that is likely
 * to be different for each object.
 * 
 * <p>The class caches a list of sources used since most of the objects from
 * a single source will have the same source parameters.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11
 *
 */
public class Source
{
	private Campaign theCampaign = null;
	private String theLongName = null;
	private String theShortName = null;
	private String theWebsite = null;
	private Date theDate = null;

	private static List<Source> theSources = null;
	
	/**
	 * Default constructor.
	 */
	public Source()
	{
		// Nothing to do.
	}
	
	/**
	 * Returns a the cached version of a source corrisponding to the
	 * <tt>Source</tt> passed in.
	 * 
	 * @param aSource The source to find
	 * 
	 * @return The global source object or <tt>null</tt> if no source yet exists
	 */
	private static Source getSource( final Source aSource )
	{
		if ( theSources == null )
		{
			return null;
		}
		for ( final Source source : theSources )
		{
			if ( source.equals( aSource ) )
			{
				return source;
			}
		}
		return null;
	}

	/**
	 * Construct a <tt>Source</tt> object from a <tt>Map</tt> of source entries
	 * passed in.
	 * 
	 * @param aSourceMap The map of source entries to use.
	 * 
	 * @return A <tt>Source</tt> object corresponding to the Map.
	 * 
	 * @throws ParseException If an invalid date is found.
	 */
	public static Source getSource( final Map<String, String> aSourceMap ) 
		throws ParseException
	{
		final Source testSource = new Source();
		
		testSource.theLongName = aSourceMap.get(SourceEntry.SourceFormat.LONG.toString());
		testSource.theShortName = aSourceMap.get(SourceEntry.SourceFormat.SHORT.toString());
		testSource.theWebsite = aSourceMap.get(SourceEntry.SourceFormat.WEB.toString());
		testSource.setDate( aSourceMap.get(SourceEntry.SourceFormat.DATE.toString()) );
		
		Source globalSource = getSource( testSource );
		if ( globalSource == null )
		{
			if ( theSources == null )
			{
				theSources = new ArrayList<Source>();
			}
			theSources.add( testSource );
			globalSource = testSource;
		}
		return globalSource;
	}

	/**
	 * Tests if a <tt>Source</tt> object corrisponding to the passed in one
	 * exists in the cache.
	 * 
	 * @param aSource The Source to test for.
	 * 
	 * @return <tt>true</tt> if the source exists.
	 */
	public static boolean hasSource( final Source aSource )
	{
		return theSources.contains( aSource );
	}
	
	/**
	 * Adds the specified Source to the cache if it is not already present.
	 *  
	 * @param aSource The <tt>Source</tt> to add.
	 */
	public static void addSource( final Source aSource )
	{
		if ( !theSources.contains(aSource) )
		{
			theSources.add( aSource );
		}
	}

	/**
	 * Returns the SOURCESHORT parameter for the source.
	 * 
	 * <p>A parameter can be specified to force the name to be a maximum of the
	 * specified number of characters.
	 * 
	 * @param aMaxLen Max characters to return in the name
	 * 
	 * @return The Short source name or an empty String.
	 */
	public String getShortName( final int aMaxLen )
	{
		if ( theShortName != null )
		{
			final int maxLen = Math.min( aMaxLen, theShortName.length() );
			return theShortName.substring( 0, maxLen );
		}
		return Constants.EMPTY_STRING;
	}
	
	/**
	 * Gets the <tt>Campaign</tt> this source information came from.
	 * 
	 * @return The Campaign.
	 */
	public Campaign getCampaign()
	{
		return theCampaign;
	}
	
	/**
	 * Sets the <tt>Campaign</tt> this source information came from.
	 * 
	 * @param aCampaign The <tt>Campaign</tt> to set.
	 */
	public void setCampaign( final Campaign aCampaign )
	{
		theCampaign = aCampaign;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return theDate;
	}
	
	/**
	 * Sets the date the source was published.
	 * 
	 * <p>This information is used by the system to determine if the object
	 * should be replaced by an object with the same key.  Objects can be
	 * replaced by objects in a newer source.
	 * 
	 * <p>This method uses <tt.DateFormat</tt> to parse the date. See that
	 * class for a description of legal date strings.
	 * 
	 * @param aDateStr the date to set
	 * 
	 * @throws ParseException If the date is not valid.
	 */
	public void setDate(final String aDateStr) throws ParseException
	{
		if ( aDateStr == null )
		{
			return;
		}
		
		final DateFormat df = DateFormat.getDateInstance();
		theDate = df.parse(aDateStr);
	}
	
	/**
	 * @return the longName
	 */
	public String getLongName()
	{
		return theLongName;
	}
	
	/**
	 * @param aLongName the longName to set
	 */
	public void setLongName(final String aLongName)
	{
		theLongName = aLongName;
	}
	
	/**
	 * @return the shortName
	 */
	public String getShortName()
	{
		return theShortName;
	}
	
	/**
	 * @param aShortName the shortName to set
	 */
	public void setShortName(String aShortName)
	{
		theShortName = aShortName;
	}
	
	/**
	 * @return the website
	 */
	public String getWebsite()
	{
		return theWebsite;
	}
	
	/**
	 * @param aWebsite the website to set
	 */
	public void setWebsite(final String aWebsite)
	{
		theWebsite = aWebsite;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((theDate == null) ? 0 : theDate.hashCode());
		result = PRIME * result + ((theLongName == null) ? 0 : theLongName.hashCode());
		result = PRIME * result + ((theShortName == null) ? 0 : theShortName.hashCode());
		result = PRIME * result + ((theWebsite == null) ? 0 : theWebsite.hashCode());
		return result;
	}

	/**
	 * This method checks for strict equality of this object.  All the fields
	 * for the Source must match for the objects to be considered equal.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Source other = (Source) obj;
		if (theDate == null)
		{
			if (other.theDate != null)
				return false;
		}
		else if (!theDate.equals(other.theDate))
			return false;
		if (theLongName == null)
		{
			if (other.theLongName != null)
				return false;
		}
		else if (!theLongName.equals(other.theLongName))
			return false;
		if (theShortName == null)
		{
			if (other.theShortName != null)
				return false;
		}
		else if (!theShortName.equals(other.theShortName))
			return false;
		if (theWebsite == null)
		{
			if (other.theWebsite != null)
				return false;
		}
		else if (!theWebsite.equals(other.theWebsite))
			return false;
		return true;
	}
}
