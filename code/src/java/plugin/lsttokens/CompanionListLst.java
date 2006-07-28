/*
 * CompanionListLst.java
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashSet;

import pcgen.core.FollowerOption;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstUtils;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * This class implments the parsing for the COMPANIONLIST token.
 * <p /><b>Tag Name</b>: <code>COMPANIONLIST</code>:x|y,y|z
 * <p /><b>Variables Used (x)</b>: <i>Text</i> (The type of companion list to 
 * add to).<br />
 * <b>Variables Used (y)</b>: <i>Text</i> (A race of companion to allow to 
 * the character).<br />
 * <b>Variables Used (y)</b>: <code>RACETYPE</code>=<i>Text</i> (all races with 
 * the specified <code>RACETYPE</code> are available as this type of companion).
 * <br />
 * <b>Variables Used (y)</b>: <code>ANY</code> (Any race can be a companion of 
 * this type).<br />
 * <b>Variables Used (z)</b>: <code>FOLLOWERADJUSTMENT</code>=<i>Number</i> 
 * (Adjustment to the follower level variable).
 * <p /><b>What it does:</b>
 * <ul>
 * <li>Adds a specific race or races to the list of available companions for
 * the specified companion type.</li>
 * <li>PRExxx tags can be added at the end of COMPANIONLIST tags, PRExxx tags
 * are checked against the master.</li>
 * <li>If the master does not meet the prereqs the companion will be displayed 
 * in the list but will be listed in red and cannot be added as a companion.
 * </li>
 * </ul>
 * <p /><b>Examples:</b><br />
 * <code>COMPANIONLIST:Familiar|Bat,Cat,Hawk,Lizard,Owl,Rat,Raven,
 * Snake (Tiny/Viper),Toad,Weasel</code><br />
 * Would build the list of standard familiars available to a Sorcerer or 
 * Wizard.
 * <p /><code>COMPANIONLIST:Pet|RACETYPE=Animal</code><br />
 * Would build a list of all animals to available as a Pet.
 * <p /><code>COMPANIONLIST:Familiar|Quasit|PREFEAT:1,Special Familiar|
 * PREALIGN:CE</code><br />
 * A Quasit can be chosen as a Familiar but only if the master is evil and has
 * the Special Familiar feat.
 * <p /><code>COMPANIONLIST:Animal Companion|Ape|FOLLOWERADJUSTMENT:-3</code>
 * <br />An Ape companion to a 4th level Druid gains the benefits normally
 * granted to a companion of a 1st level Druid.
 *  
 * @author divaa01
 *
 */
public class CompanionListLst implements GlobalLstToken
{
	private static final String COMPANIONLIST = "COMPANIONLIST"; //$NON-NLS-1$
	private static final String FOLLOWERADJUSTMENT = "FOLLOWERADJUSTMENT"; //$NON-NLS-1$
	private static final String PRE = "PRE"; //$NON-NLS-1$

	/**
	 * Parses the COMPANIONLIST tag.
	 * @param anObj The object this tag was found on
	 * @param aValue The token with the COMPANIONLIST portion striped off.
	 * @param anInt The level at which this tag should apply
	 * @return true if the tag is successfully parsed.
	 * @throws PersistenceLayerException
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject, java.lang.String, int)
	 */
	public boolean parse(final PObject anObj, final String aValue, @SuppressWarnings("unused")
	final
	int anInt)
			throws PersistenceLayerException
	{
		final StringTokenizer tok = new StringTokenizer(aValue, LstUtils.PIPE);
		if ( tok.hasMoreTokens() )
		{
			final String companionType = tok.nextToken();
			if ( tok.hasMoreTokens()) 
			{
				final Set<String> races = new HashSet<String>();
				
				final String list = tok.nextToken();
				final StringTokenizer subTok = new StringTokenizer( list, LstUtils.COMMA );
				while ( subTok.hasMoreTokens() )
				{
					// We can't expand races here since this is a global tag 
					// and races may not have been processed yet.
					// TODO Need to figure out how to deal with this issue.
					races.add( subTok.nextToken() );
				}

				int followerAdjustment = 0;
				final List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
				
				// The remainder of the elements are optional.
				while ( tok.hasMoreTokens() )
				{
					final String optArg = tok.nextToken();
					if ( optArg.startsWith(FOLLOWERADJUSTMENT) )
					{
						if ( followerAdjustment != 0 )
						{
							Logging.debugPrint( getTokenName() + ": Multiple "  //$NON-NLS-1$
									+ FOLLOWERADJUSTMENT 
									+ " tags specified.  Will use last one." ); //$NON-NLS-1$
						}

						final String adj = optArg.
									substring(FOLLOWERADJUSTMENT.length() + 1);
						
						followerAdjustment = Integer.parseInt( adj );
					}
					else if ( optArg.startsWith(PRE) )
					{
						final PreParserFactory factory = PreParserFactory.getInstance();
						final Prerequisite prereq = factory.parse( optArg );
						prereqs.add( prereq );
					}
					else
					{
						Logging.debugPrint( getTokenName() 
								+ ": Unknown optional argument: "  //$NON-NLS-1$
								+ optArg );
					}
				}
				for ( final String r : races )
				{
					final FollowerOption option = new FollowerOption( r );
					option.setType( companionType );
					if ( prereqs.size() > 0 )
					{
						option.addPrerequisites(prereqs);
					}
					if ( followerAdjustment != 0 )
					{
						option.setAdjustment( followerAdjustment );
					}
					anObj.addToFollowerList( companionType, option );
				}
				return true;
			}
		}

		throw new PersistenceLayerException(
				PropertyFactory.getFormattedString(
						"Errors.LstTokens.InvalidTokenFormat",  //$NON-NLS-1$
						getTokenName(), aValue));
	}

	/**
	 * Returns the name of the token this class can process.
	 * @return Token name 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return COMPANIONLIST;
	}

}
