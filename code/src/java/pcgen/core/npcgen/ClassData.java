/*
 * ClassData.java
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
package pcgen.core.npcgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Categorisable;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SubClass;
import pcgen.core.spell.Spell;
import pcgen.util.WeightedList;
import pcgen.util.enumeration.Visibility;

/**
 * Stores information about how to randomly generate selections for a class.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class ClassData
{
	private String theClassKey = null;
	
	// TODO Can this be a PCStat?
	private WeightedList<String> theStatWeights = null;
	private WeightedList<SkillChoice> theSkillWeights = null;
	private Map<AbilityCategory, WeightedList<Ability>> theAbilityWeights = null;
	private WeightedList<Deity> theDeityWeights = null;
	private Map<String, WeightedList<Domain>> theDomainWeights = null;
	private Map<Integer, WeightedList<Spell>> theKnownSpellWeights = null;
	private Map<Integer, WeightedList<Spell>> thePreparedSpellWeights = null;
	private WeightedList<String> theSubClassWeights = null;
	
	/**
	 * Creates an empty <tt>ClassData</tt> object
	 * 
	 * @param aClassKey The key of the class this data is for
	 */
	public ClassData( final String aClassKey )
	{
		theClassKey = aClassKey;
	}
	
	/**
	 * @return The key of the class this data is for
	 */
	public String getClassKey()
	{
		return theClassKey;
	}
	
	/**
	 * @param aStatAbbr The stat abbreviation to add
	 * @param aWeight The weight to associate with it.
	 */
	public void addStat( final String aStatAbbr, final int aWeight )
	{
		if ( theStatWeights == null )
		{
			theStatWeights = new WeightedList<String>();
		}
		theStatWeights.add(aWeight, aStatAbbr);
	}
	
	/**
	 * @return <tt>WeightedList</tt> of stat abbreviations.
	 */
	public WeightedList<String> getStatWeights()
	{
		// Make sure that we have all the stats
		final List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();
		for ( final PCStat stat : statList )
		{
			if ( theStatWeights == null || theStatWeights.contains(stat.getAbb()) == false )
			{
				addStat(stat.getAbb(), 1);
			}
		}
		return theStatWeights;
	}
	
	/**
	 * @param aKey
	 * @param aWeight
	 */
	public void addSkill( final String aKey, final int aWeight )
	{
		if ( theSkillWeights == null )
		{
			theSkillWeights = new WeightedList<SkillChoice>();
		}
		for ( final SkillChoice sc : theSkillWeights )
		{
			if ( sc.hasSkill(aKey) )
			{
				return;
			}
		}
		theSkillWeights.add(aWeight, new SkillChoice(aKey));
	}
	
	/**
	 * @param aKey
	 */
	public void removeSkill( final String aKey )
	{
		if ( theSkillWeights == null )
		{
			return;
		}
		theSkillWeights.remove(new SkillChoice(aKey));
	}
	
	/**
	 * @return <tt>WeightedList</tt> of Skill keys
	 */
	public WeightedList<SkillChoice> getSkillWeights()
	{
		return theSkillWeights;
	}
	
	/**
	 * @param aCategory
	 * @param anAbility
	 * @param aWeight
	 */
	public void addAbility( final AbilityCategory aCategory, final Ability anAbility, final int aWeight )
	{
		if ( theAbilityWeights == null )
		{
			theAbilityWeights = new HashMap<AbilityCategory, WeightedList<Ability>>();
		}
		WeightedList<Ability> abilities = theAbilityWeights.get(aCategory);
		if ( abilities == null )
		{
			abilities = new WeightedList<Ability>();
			theAbilityWeights.put(aCategory, abilities);
		}
		if ( ! abilities.contains(anAbility) )
		{
			abilities.add(aWeight, anAbility);
		}
	}
	
	/**
	 * Removes an Ability from the list of abilities.
	 * 
	 * @param aCategory The AbilityCategory to remove the ability for
	 * @param anAbility The Ability to remove
	 */
	public void removeAbility( final AbilityCategory aCategory, final Ability anAbility )
	{
		if ( theAbilityWeights == null )
		{
			return;
		}
		final WeightedList<Ability> abilities = theAbilityWeights.get(aCategory);
		if ( abilities == null )
		{
			return;
		}
		abilities.remove(anAbility);
	}
	
	/**
	 * Gets the Abilities of the specified category.
	 * 
	 * <p>If there is no data for this category, all Abilities for the category
	 * will be added to the list with the same weight.
	 * 
	 * @param aCategory The category of ability to retrieve
	 * @return A <tt>WeightedList</tt> of Ability objects
	 */
	public WeightedList<Ability> getAbilityWeights( final AbilityCategory aCategory )
	{
		if ( theAbilityWeights == null )
		{
			return null;
		}
		return theAbilityWeights.get(aCategory);
	}
	
	public void addDeity( final Deity aDeity, final int aWeight )
	{
		if ( theDeityWeights == null )
		{
			theDeityWeights = new WeightedList<Deity>();
		}
		
		theDeityWeights.add(aWeight, aDeity);
	}
	
	public WeightedList<Deity> getDeityWeights()
	{
		if ( theDeityWeights == null )
		{
			for ( final Deity deity : Globals.getDeityList() )
			{
				addDeity(deity, 1);
			}
		}
		return theDeityWeights;
	}

	public void addDomain( final String aDeityKey, final Domain aDomain, final int aWeight )
	{
		if ( theDomainWeights == null )
		{
			theDomainWeights  = new HashMap<String, WeightedList<Domain>>();
		}
		WeightedList<Domain> domains = theDomainWeights.get(aDeityKey);
		if ( domains == null )
		{
			domains = new WeightedList<Domain>();
			theDomainWeights.put( aDeityKey, domains );
		}
		domains.add(aWeight, aDomain);
	}
	
	public WeightedList<Domain> getDomainWeights( final String aDeityKey ) 
	{
		if ( theDomainWeights == null )
		{
			theDomainWeights  = new HashMap<String, WeightedList<Domain>>();
		}
		WeightedList<Domain> domains = theDomainWeights.get(aDeityKey);
		if ( domains == null )
		{
			domains = new WeightedList<Domain>();
			
			final Deity deity = Globals.getDeityKeyed(aDeityKey);
			final List<Domain> deityDomains = deity.getDomainList();
			for ( final Domain domain : deityDomains )
			{
				domains.add(1, domain);
			}
		}
		return domains;
	}

	public void addKnownSpell( final int aLevel, final Spell aSpell, final int aWeight )
	{
		if ( theKnownSpellWeights == null )
		{
			theKnownSpellWeights = new HashMap<Integer, WeightedList<Spell>>();
		}
		WeightedList<Spell> spells = theKnownSpellWeights.get(aLevel);
		if ( spells == null )
		{
			spells = new WeightedList<Spell>();
			theKnownSpellWeights.put(aLevel, spells);
		}
		if ( ! spells.contains(aSpell) )
		{
			spells.add(aWeight, aSpell);
		}
	}
	
	public void removeKnownSpell( final int aLevel, final Spell aSpell )
	{
		if ( theKnownSpellWeights == null )
		{
			return;
		}
		
		final WeightedList<Spell> spells = theKnownSpellWeights.get(aLevel);
		if ( spells != null )
		{
			spells.remove( aSpell );
		}
	}
	
	public WeightedList<Spell> getKnownSpellWeights( final int aLevel ) 
	{
		if ( theKnownSpellWeights == null )
		{
			theKnownSpellWeights = new HashMap<Integer, WeightedList<Spell>>();
		}
		WeightedList<Spell> spells = theKnownSpellWeights.get(aLevel);
		if ( spells == null )
		{
			spells = new WeightedList<Spell>();
			
			for ( final Spell spell : Globals.getSpellsIn(aLevel, theClassKey, Constants.EMPTY_STRING) )
			{
				spells.add(1, spell);
			}
		}
		return spells;
	}

	public void addPreparedSpell( final int aLevel, final Spell aSpell, final int aWeight )
	{
		if ( thePreparedSpellWeights == null )
		{
			thePreparedSpellWeights = new HashMap<Integer, WeightedList<Spell>>();
		}
		WeightedList<Spell> spells = thePreparedSpellWeights.get(aLevel);
		if ( spells == null )
		{
			spells = new WeightedList<Spell>();
			thePreparedSpellWeights.put(aLevel, spells);
		}
		if ( ! spells.contains(aSpell) )
		{
			spells.add(aWeight, aSpell);
		}
	}
	
	public void removePreparedSpell( final int aLevel, final Spell aSpell )
	{
		if ( thePreparedSpellWeights == null )
		{
			return;
		}
		
		final WeightedList<Spell> spells = thePreparedSpellWeights.get(aLevel);
		if ( spells != null )
		{
			spells.remove( aSpell );
		}
	}
	
	public WeightedList<Spell> getPreparedSpellWeights( final int aLevel ) 
	{
		if ( thePreparedSpellWeights == null )
		{
			thePreparedSpellWeights = new HashMap<Integer, WeightedList<Spell>>();
		}
		WeightedList<Spell> spells = thePreparedSpellWeights.get(aLevel);
		if ( spells == null )
		{
			spells = new WeightedList<Spell>();
			
			for ( final Spell spell : Globals.getSpellsIn(aLevel, theClassKey, Constants.EMPTY_STRING) )
			{
				spells.add(1, spell);
			}
		}
		return spells;
	}

	public void addSubClass( final String aKey, final int aWeight )
	{
		if ( theSubClassWeights == null )
		{
			theSubClassWeights = new WeightedList<String>();
		}
		theSubClassWeights.add( aWeight, aKey );
	}
	
	public WeightedList<String> getSubClassWeights()
	{
		if ( theSubClassWeights == null )
		{
			final PCClass pcClass = Globals.getClassKeyed( theClassKey );
			final List<SubClass> subClasses = pcClass.getSubClassList();
			for ( final SubClass subClass : subClasses )
			{
				addSubClass( subClass.getKeyName(), 1 );
			}
		}
		return theSubClassWeights;
	}
}
