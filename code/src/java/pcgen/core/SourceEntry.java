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

import java.text.ParseException;
import java.util.Map;

import pcgen.util.PropertyFactory;

/**
 * This class represents the information about the source
 * of an objcet.
 * 
 * <p>This includes the <tt>Source</tt> information as well as Page information.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11
 *
 */
public class SourceEntry
{
	private Source theSourceBook = new Source();
	private String thePageNumber = null;
	
	/**
	 * Default constructor.  Creates an empty source.
	 *
	 */
	public SourceEntry()
	{
		// Nothing to do.
	}
	
	public SourceEntry( final Source aSource )
	{
		theSourceBook = aSource;
	}
	
	/**
	 * Constructs a new <tt>SourceEntry</tt> from the specified <tt>Map</tt>
	 * 
	 * <p>The Map should contain key values for the fields and the data for the
	 * values.
	 * 
	 * @param aSourceMap A Map of source format - source value pairs.
	 * 
	 * @throws ParseException If an invalid date is specified.
	 */
	public SourceEntry( final Map<String, String> aSourceMap ) 
		throws ParseException
	{
		theSourceBook = Source.getSource( aSourceMap );
		thePageNumber = aSourceMap.get( SourceFormat.PAGE );
	}
	
	public void setFromMap( final Map<String, String> aSourceMap )
		throws ParseException
	{
		theSourceBook = Source.getSource( aSourceMap );
		if ( thePageNumber == null )
		{
			thePageNumber = aSourceMap.get( SourceFormat.PAGE );
		}
	}
	
	/**
	 * @return the pageNumber
	 */
	public String getPageNumber()
	{
		return thePageNumber;
	}
	
	/**
	 * @param aPageNumber the pageNumber to set
	 */
	public void setPageNumber(final String aPageNumber)
	{
		thePageNumber = aPageNumber;
	}
	
	/**
	 * @return the sourceBook
	 */
	public Source getSourceBook()
	{
		return theSourceBook;
	}
	
	/**
	 * @param aSourceBook the sourceBook to set
	 */
	public void setSourceBook(final Source aSourceBook)
	{
		theSourceBook = aSourceBook;
	}

	public enum SourceFormat 
	{ 
		/** Long format = Publisher - Source Long */
		LONG("LONG"),
		/** Medium format = Source Long */
		MEDIUM("LONG"),
		/** Short format = Source Short */
		SHORT("SHORT"),
		/** Website format = Publisher - Source Web */
		WEB("WEB"),
		/** Date format = Publisher - Source date */
		DATE("DATE"),
		/** Page number format - Publisher - Source page */
		PAGE("PAGE");
		
		private final String theKey;
		
		private SourceFormat( final String aKey )
		{
			theKey = aKey;
		}
		
		/**
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return theKey;
		}
	
		/**
		 * Should this format include the publisher name if it is available?
		 * 
		 * @return <tt>true</tt> if the publisher info should be included. 
		 */
		public final boolean includesPublisher()
		{
			return this != SHORT && this != MEDIUM;
		}
		
		/**
		 * Does this format allow page information?
		 * 
		 * <p>If a format does not allow page information then page
		 * information will not be included in the formatted output even if 
		 * it is requested.  This is used primarily to prevent silly 
		 * combinations like website, page number.
		 * 
		 * @return <tt>true</tt> if the page information can be included.
		 */
		public final boolean allowsPage()
		{
			return this != WEB;
		}
		
		/**
		 * Returns a <tt>SourceFormat</tt> for the ordinal provided.
		 * 
		 * @param anOrdinal The index into the list of <tt>SourceFormat</tt>s
		 * 
		 * @return The <tt>SourceFormat</tt> at the index. 
		 */
		public static SourceFormat valueOf( final int anOrdinal )
		{
			return SourceFormat.values()[anOrdinal];
		}
	} 

	/**
	 * Returns a formatted string representation for this source based on the
	 * <tt>SourceFormat</tt> passed in.
	 * 
	 * @param aFormat The format to display the source in
	 * 
	 * @return A formatted string.
	 * 
	 * @see pcgen.core.SourceEntry.SourceFormat
	 */
	public String getFormattedString( final SourceFormat aFormat, final boolean includePage )
	{
		final StringBuffer ret = new StringBuffer();
		
		String source = getFieldByType( aFormat );
		String publisher = null;
		if ( theSourceBook.getCampaign() != null )
		{
			// If sourceCampaign object exists, get it's publisher entry for 
			// the same key
			publisher = theSourceBook.getCampaign().getPublisherWithKey( aFormat.toString() );
		
			// if this item's source is null, try to get it from theCampaign
			if (source == null)
			{
				source = theSourceBook.getCampaign().getSourceEntry().getFieldByType( aFormat );
			}
		}
		if ( source == null )
		{
			source = Constants.EMPTY_STRING;
		}
	
		if ( aFormat.includesPublisher() && publisher != null )
		{
			ret.append( publisher );
			ret.append( " - " ); //$NON-NLS-1$
		}
		ret.append( source );
		
		if ( includePage && aFormat.allowsPage() )
		{
			if ( thePageNumber != null)
			{
				if ( ret.length() != 0 )
				{
					ret.append(", ");
				}
				ret.append(thePageNumber);
			}

		}
		return ret.toString();
	}

	public String getFieldByType( final SourceFormat aFormat )
	{
		switch ( aFormat )
		{
		case LONG:
			return theSourceBook.getLongName();
		case MEDIUM:
			return theSourceBook.getLongName();
		case SHORT:
			return theSourceBook.getShortName();
		case WEB:
			return theSourceBook.getWebsite();
		case DATE:
			if ( theSourceBook.getDate() != null )
			{
				return theSourceBook.getDate().toString();
			}
			return Constants.EMPTY_STRING;
		case PAGE:
			return thePageNumber;
		default:
			return theSourceBook.getLongName();
		}
	}
	
	/**
	 * Returns a formatted string version of the source based on the global
	 * setting for source display.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getFormattedString( Globals.getSourceDisplay(), true );
	}
}
