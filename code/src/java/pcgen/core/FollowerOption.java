/*
 * FollowerOption.java
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import pcgen.cdom.base.ConcretePrereqObject;

/**
 * This class represents a possible choice for a follower.  This is basically
 * a Race with a "FOLLOWERADJUSTMENT" that modifies the owner's effective
 * level when selecting a follower of this type.  Prereqs can also be specified
 * 
 * @author boomer70
 */
public class FollowerOption extends ConcretePrereqObject implements Comparable<FollowerOption>
{
	private String theRaceKey;
	private Race theRace = null;
	private int theAdjustment = 0;
	private String theType = null;
	
	private static final String ANY_RACE = "ANY"; //$NON-NLS-1$
	private static final String RACETYPE = "RACETYPE"; //$NON-NLS-1$

	/**
	 * Creates a FollowerOption of the specified race with no adjustment and
	 * an unknown type.
	 * @param aRace The race key of this companion
	 */
	public FollowerOption( final String aRace )
	{
		theRaceKey = aRace.toUpperCase();
		theRace = Globals.getRaceKeyed( theRaceKey );
	}
	
	/**
	 * Creates a FollowerOption of the specified race with no adjustment and
	 * an unknown type.
	 * @param aRace The race of this companion
	 */
	public FollowerOption( final Race aRace )
	{
		theRace = aRace;
		theRaceKey = aRace.getKeyName();
	}
	
	/**
	 * Returns the race associated with this option.  If this option represents 
	 * a group of races this method will return null.
	 * @return The Race associated or null
	 */
	public Race getRace()
	{
		if ( theRace == null )
		{
			if ( theRaceKey.startsWith( RACETYPE ) 
			  || theRaceKey.equals(ANY_RACE) )
			{
				return null;
			}
			theRace = Globals.getRaceKeyed( theRaceKey );
		}
		return theRace;
	}
	
	/**
	 * Separates into indivual FollowerOptions this option.  If this option
	 * does not represent multiple races it will simply return itself.  
	 * Otherwise the method returns a list of FollowerOptions with the same
	 * options one for each Race that qualifies.
	 * @return The expanded list of FollowerOptions
	 */
	public Collection<FollowerOption> getExpandedOptions()
	{
		final List<FollowerOption> options = new ArrayList<FollowerOption>();
		if ( theRace != null )
		{
			options.add( this );
			return options;
		}
		Collection<Race> raceSet = null;
		if ( theRaceKey.startsWith( RACETYPE ) )
		{
			raceSet = new HashSet<Race>();
			final String raceType = theRaceKey.substring(9);
			final Collection<Race> allRaces = Globals.getAllRaces();
			for ( final Race r : allRaces )
			{
				if ( raceType.equalsIgnoreCase(r.getRaceType()) )
				{
					raceSet.add( r );
				}
			}
		}
		else if ( theRaceKey.equals( ANY_RACE ) )
		{
			raceSet = new HashSet<Race>();
			raceSet.addAll( Globals.getAllRaces() );
		}
		if ( raceSet != null )
		{
			for ( final Race r : raceSet )
			{
				final FollowerOption opt = new FollowerOption( r );
				opt.setAdjustment( getAdjustment() );
				opt.addPrerequisites( getPreReqList() );
				options.add( opt );
			}
		}
		return options;
	}
	
	/**
	 * Sets the Follower type for this option.
	 * @param aType The follower type to set e.g. Familiar 
	 */
	public void setType( final String aType )
	{
		theType = aType;
	}
	
	/**
	 * Gets the Follower type for this option.
	 * @return The Follower type e.g. Familiar 
	 */
	public String getType()
	{
		return theType;
	}
	
	/**
	 * Sets the variable adjustment for a master selecting this option.  For
	 * example an adjustment of -3 would mean the master's level would be 3
	 * lower for purposes of applying companion mods.
	 * @param anAdjustment Amount to modify the master's level by
	 */
	public void setAdjustment( final int anAdjustment )
	{
		theAdjustment = anAdjustment;
	}
	
	/**
	 * Returns the adjustment to the master's level for this option.
	 * @return The adjustment to the master's level
	 */
	public int getAdjustment()
	{
		return theAdjustment;
	}

	/**
	 * This method is overridden to also check that a master has enough 
	 * effective levels to have a positive level after applying any adjustment
	 * for this follower.  For example, if a follower has an adjustment of -3
	 * then the master must have at least 4 levels to qualify for this follower
	 * (4 - 3 &gt; 0)
	 * @see pcgen.cdom.base.PrereqObject#qualifies(pcgen.core.PlayerCharacter)
	 */
	@Override
	public boolean qualifies( final PlayerCharacter aPC )
	{
		if ( theAdjustment != 0 )
		{
			final int lvl = aPC.getEffectiveCompanionLevel( theType );
			if ( lvl + theAdjustment <= 0 )
			{
				return false;
			}
		}
		
		return super.qualifies( aPC );
	}
	
	/**
	 * Compares this FollowerOption to another.  This uses the race name of the
	 * option to do the comparison.
	 * @param anO The FollowerOption to compare to.
	 * @return The comparison between the objects
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FollowerOption anO)
	{
		final Collator col = Collator.getInstance();
		String s1 = theRaceKey;
		String s2 = anO.theRaceKey;
		if ( this.theRace != null )
		{
			s1 = theRace.getDisplayName();
		}
		if ( anO.theRace != null )
		{
			s2 = anO.theRace.getDisplayName();
		}
		return col.compare( s1, s2);
	}
}
