/*
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
 */
package pcgen.core.npcgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pcgen.core.GameMode;
import pcgen.core.SystemCollections;

/**
 * Parse a generator options file.
 * 
 */
public class OptionsParser
{
	private final SAXParser theParser;
	private final GameMode theMode;
	
	/**
	 * Creates a new OptionsParser for the specified game mode.
	 * 
	 * @param aMode The game mode to parse options for.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public OptionsParser(final GameMode aMode) 
		throws ParserConfigurationException, SAXException
	{
		theMode = aMode;
		
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		theParser = parserFactory.newSAXParser();
	}
	
	/**
	 * @param aFileName File to parse.
	 * @return a list of generator options
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<GeneratorOption> parse( final File aFileName ) 
		throws SAXException, IOException
	{
		final List<GeneratorOption> ret = new ArrayList<>();
		
		try
		{
			theParser.parse(aFileName, new OptionHandler(theMode, ret));
		}
		catch (IllegalArgumentException ex )
		{
			// Do nothing, means we weren't the right game mode for this file.
		}
		return ret;
	}
}

class OptionHandler extends DefaultHandler
{
	private final List<GeneratorOption> theList;
	
	private GameMode theGameMode = null;
	private boolean theValidFlag = false;
	
	private GeneratorOption theCurrentOption = null;
	
	public OptionHandler( final GameMode aMode, final List<GeneratorOption> aList )
	{
		theGameMode = aMode;
		theList = aList;
	}
	
	/**
	 * @throws SAXException 
	 * @throws IOException 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(	final String uri, final String localName,
								final String aName, final Attributes anAttrs) 
		throws SAXException
	{
		if ( "npcgen_options".equals(aName) ) //$NON-NLS-1$
		{
			if ( anAttrs != null )
			{
				final String gm = anAttrs.getValue("game_mode"); //$NON-NLS-1$
				if ( ! SystemCollections.getGameModeNamed(gm).equals(theGameMode) )
				{
					throw new IllegalArgumentException("Incorrect game mode"); //$NON-NLS-1$
				}
				theValidFlag = true;
			}
			return;
		}
		
		if (!theValidFlag )
		{
			throw new SAXException("NPCGen.Options.InvalidFileFormat"); //$NON-NLS-1$
		}
		if ( "align".equals(aName) ) //$NON-NLS-1$
		{
			theCurrentOption = new AlignGeneratorOption();
		}
		else if ( "race".equals(aName) ) //$NON-NLS-1$
		{
			theCurrentOption = new RaceGeneratorOption();
		}
		else if ( "gender".equals(aName) ) //$NON-NLS-1$
		{
			theCurrentOption = new GenderGeneratorOption();
		}
		else if ( "class".equals(aName) ) //$NON-NLS-1$
		{
			theCurrentOption = new ClassGeneratorOption();
		}
		else if ( "level".equals(aName) ) //$NON-NLS-1$
		{
			theCurrentOption = new LevelGeneratorOption();
		}
		else if ( "choice".equals(aName) ) //$NON-NLS-1$
		{
			int weight = 1;
			final String weightStr = anAttrs.getValue("weight"); //$NON-NLS-1$
			if ( weightStr != null )
			{
				weight = Integer.parseInt( weightStr );
			}
			theCurrentOption.addChoice(weight, anAttrs.getValue("value")); //$NON-NLS-1$
		}

		if ( ! "choice".equals(aName) )
		{
			theCurrentOption.setName(anAttrs.getValue("name")); //$NON-NLS-1$
			theList.add(theCurrentOption);
		}
	}
}
