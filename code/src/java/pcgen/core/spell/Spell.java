/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core.spell;


import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.list.SpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.CharacterDomain;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.SpellPoint;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Spell extends PObject
{
	public static final CDOMReference<SpellList> SPELLS;

	static
	{
		SpellList wpl = new SpellList();
		wpl.setName("*Spells");
		SPELLS = CDOMDirectSingleRef.getRef(wpl);
	}

	private String fixedCasterLevel = null;
	private String fixedDC = null;

	static boolean hasSpellPointCost = false;

	/** An enumeration of &quot;Standard&quot; spell components */
	public enum Component {
		/** Verbal Component &quot;V&quot; */
		VERBAL("V", "Spell.Components.Verbal"), //$NON-NLS-1$ //$NON-NLS-2$
		/** Somatic (movement) Component &quot;S&quot; */
		SOMATIC("S", "Spell.Components.Somatic"), //$NON-NLS-1$ //$NON-NLS-2$
		/** Material Component &quot;M&quot; */
		MATERIAL("M", "Spell.Components.Material"), //$NON-NLS-1$ //$NON-NLS-2$
		/** Divine Focus Component (usually holy symbol) &quot;DF&quot; */
		DIVINEFOCUS("DF", "Spell.Components.DivineFocus"), //$NON-NLS-1$ //$NON-NLS-2$
		/** Non-divine Focus Component &quot;F&quot; */
		FOCUS("F", "Spell.Components.Focus"), //$NON-NLS-1$ //$NON-NLS-2$
		/** Experience Point cost &quot;XP&quot; */
		EXPERIENCE("XP", "Spell.Components.Experience"), //$NON-NLS-1$ //$NON-NLS-2$
		/** Anything other than the standard components */
		OTHER("See text", "Spell.Components.SeeText"); //$NON-NLS-1$ //$NON-NLS-2$
		
		private String theKey;
		private String theName;
		
		Component(final String aKey, final String aName)
		{
			theKey = aKey;
			theName = aName;
		}
		
		/**
		 * Returns the String key of the component.
		 * 
		 * @return The key.
		 */
		public String getKey()
		{
			return theKey;
		}
		
		/**
		 * Factory method to get a Component from a string key.
		 * 
		 * @param aKey The component key to get a Component for (e.g. V or S)
		 * 
		 * @return A Component object.  If no object matches <tt>OTHER</tt> is 
		 * returned.
		 */
		public static Component getComponentFromKey( final String aKey )
		{
			for ( Component c : Component.values() )
			{
				if ( c.getKey().equalsIgnoreCase(aKey) )
				{
					return c;
				}
			}
			return OTHER;
		}
		/**
		 * Returns the string abbreviation of this component.
		 * 
		 * @return The abbreviation
		 */
		@Override
		public String toString()
		{
			return theName;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////////
	public Spell()
	{
		super();
	}

	public String getCastingTime()
	{
		return StringUtil.join(getListFor(ListKey.CASTTIME), ", ");
	}

	public String getComponentList()
	{
		return StringUtil.join(getListFor(ListKey.COMPONENTS), ", ");
	}

	/**
	 * @return Returns the fixedCasterLevel.
	 */
	public String getFixedCasterLevel()
	{
		return fixedCasterLevel;
	}

	/**
	 * @param fixedCasterLevel The fixedCasterLevel to set.
	 */
	public void setFixedCasterLevel(final String fixedCasterLevel)
	{
		this.fixedCasterLevel = fixedCasterLevel;
	}


	/**
	 * @return Returns the fixedDC.
	 */
	public String getFixedDC()
	{
		return fixedDC;
	}

	/**
	 * @param fixedDC The fixedDC to set.
	 */
	public void setFixedDC(final String fixedDC)
	{
		this.fixedDC = fixedDC;
	}

	/**
	 * Returns DC for a spell for aPC and SpellInfo.
	 * @param aPC the pc to return the dc for
	 * @param si the spell
	 * @return DC for a spell for aPC and SpellInfo
	 */
	public int getDCForPlayerCharacter(final PlayerCharacter aPC, final SpellInfo si)
	{
		return getDCForPlayerCharacter(aPC, si, null, 0);
	}

	/**
	 * returns DC for a spell for aPC and either SpellInfo or PCClass
	 * SPELLLEVEL variable is set to inLevel
	 * @param aPC
	 * @param si
	 * @param aClass
	 * @param inLevel
	 * @return DC
	 */
	public int getDCForPlayerCharacter(final PlayerCharacter aPC, final SpellInfo si, PCClass aClass, final int inLevel)
	{
		CharacterSpell cs;
		PObject ow = null;
		int spellLevel = inLevel;
		String bonDomain = "";
		String bonClass = "";
		String spellType = "";
		String classKey = "";
		int metaDC = 0;
		int spellIndex = 0;

		// TODO Temp fix for 1223858, better fix would be to move fixedDC to spellInfo
		if(fixedDC != null && si != null && "INNATE".equalsIgnoreCase(si.getBook())) {
			return aPC.getVariableValue(fixedDC, "").intValue();
		}

		if (si != null)
		{
			cs = si.getOwner();

			if (cs != null)
			{
				spellLevel = si.getActualLevel();
				ow = cs.getOwner();
			}

			// Check for a non class based fixed DC
			if (fixedDC != null && ow != null && !(ow instanceof PCClass))
			{
				return aPC.getVariableValue(fixedDC, "").intValue();
			}

			if (si.getFeatList() != null)
			{
				for ( Ability metaFeat : si.getFeatList() )
				{
					spellLevel -= metaFeat.getSafe(IntegerKey.ADD_SPELL_LEVEL);
					metaDC += metaFeat.bonusTo("DC", "FEATBONUS", aPC, aPC);
				}
			}
		}
		else
		{
			ow = aClass;
		}

		if (ow instanceof Domain)
		{
			bonDomain = "DOMAIN." + ow.getKeyName();

			final CharacterDomain aCD = aPC.getCharacterDomainForDomain(ow.getKeyName());

			if ((aCD != null) && aCD.isFromPCClass())
			{
				final String a = aCD.getObjectName();
				aClass = aPC.getClassKeyed(a);
			}
		}

		if ((aClass != null) || (ow instanceof PCClass))
		{
			if ((aClass == null) || (ow instanceof PCClass))
			{
				aClass = (PCClass) ow;
			}

			bonClass = "CLASS." + aClass.getKeyName();
			classKey = "CLASS:" + aClass.getKeyName();
			spellType = aClass.getSpellType();
			spellIndex = aClass.baseSpellIndex();
		}

		if (!(ow instanceof PCClass) && !(ow instanceof Domain))
		{
			// get BASESPELLSTAT from spell itself
			spellIndex = -2;
		}

		// set the spell Level used in aPC.getVariableValue()
		aPC.setSpellLevelTemp(spellLevel);

		// must be done after spellLevel is set above
		int dc = aPC.getVariableValue(Globals.getGameModeBaseSpellDC(), classKey).intValue() + metaDC;
		dc += (int) aPC.getTotalBonusTo("DC", "ALLSPELLS");

		if (spellIndex == -2)
		{
			// get the BASESPELLSTAT from the spell itself
			PCStat stat = get(ObjectKey.SPELL_STAT);
			if (stat != null)
			{
				dc += aPC.getStatList().getStatModFor(stat.getAbb());
			}
		}

		if (getKeyName().length() > 0)
		{
			dc += (int) aPC.getTotalBonusTo("DC", "SPELL." + getKeyName());
		}

		// DOMAIN.name
		if (bonDomain.length() > 0)
		{
			dc += (int) aPC.getTotalBonusTo("DC", bonDomain);
		}

		// CLASS.name
		if (bonClass.length() > 0)
		{
			dc += (int) aPC.getTotalBonusTo("DC", bonClass);
		}

		dc += (int) aPC.getTotalBonusTo("DC", "TYPE." + spellType);

		if (spellType.equals("ALL"))
		{
			for (Type aType : getTrueTypeList(false))
			{
				dc += (int) aPC.getTotalBonusTo("DC", "TYPE." + aType);
			}
		}

		for (String aType : getSafeListFor(ListKey.SPELL_SCHOOL))
		{
			dc += (int) aPC.getTotalBonusTo("DC", "SCHOOL." + aType);
		}

		for (String aType : getSafeListFor(ListKey.SPELL_SUBSCHOOL))
		{
			dc += (int) aPC.getTotalBonusTo("DC", "SUBSCHOOL." + aType);
		}

		for (String aType : getSafeListFor(ListKey.SPELL_DESCRIPTOR))
		{
			dc += (int) aPC.getTotalBonusTo("DC", "DESCRIPTOR." + aType);
		}

		aPC.setSpellLevelTemp(0); // reset

		return dc;
	}

	public String getDuration()
	{
		return StringUtil.join(getListFor(ListKey.DURATION), ", ");
	}

	@Override
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));

		if (hasSpellPointCost())
		{
			txt.append(SpellPoint.getSpellPointCostActual(this));
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	public String getRange()
	{
		return StringUtil.join(getListFor(ListKey.RANGE), ", ");
	}

	public String getSaveInfo()
	{
		return StringUtil.join(getListFor(ListKey.SAVE_INFO), ", ");
	}

	public String getSchool()
	{
		return StringUtil.join(getListFor(ListKey.SPELL_SCHOOL), ", ");
	}

	public String getSpellResistance()
	{
		return StringUtil.join(getListFor(ListKey.SPELL_RESISTANCE), ", ");
	}

	public String getSubschool()
	{
		return StringUtil.join(getListFor(ListKey.SPELL_SUBSCHOOL), ", ");
	}

	////////////////////////////////////////////////////////////
	// Public method(s)
	////////////////////////////////////////////////////////////
	@Override
	public Spell clone()
	{
		Spell aSpell = null;

		try
		{
			aSpell = (Spell) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return aSpell;
	}

	public String descriptor()
	{
		return StringUtil.join(getListFor(ListKey.SPELL_DESCRIPTOR), ", ");
	}

	/**
	 * Tests to see if two Spell objects are equal.
	 * 
	 * @param other Spell to compare to.
	 * 
	 * @return <tt>true</tt> if the Spells are the same.
	 */
	@Override
	public boolean equals( final Object other )
	{
		if ( other == null )
		{
			return false;
		}
		if ( ! (other instanceof Spell) )
		{
			return false;
		}
		if ( other == this ) {
			return true;
		}
		final Spell otherSpell = (Spell)other;
		if ( getKeyName().equals( otherSpell.getKeyName() ) )
		{
			return isCDOMEqual(otherSpell);
		}
		return false;
	}
	
	/**
	 * Need something consistent with equals - this causes conflicts with the same name
	 * but that's ok, it's only a hashcode.
	 */
	@Override
	public int hashCode() {
		return getKeyName().hashCode();
	}
	
	public static boolean hasSpellPointCost()
	{
		return hasSpellPointCost;
	}

	public boolean isAllowed(String string)
	{
		/*
		 * Due to case insensitivity and lack of type safety so far on ITEM &
		 * PROHIBITED_ITEM we need this method in order to properly calculate
		 * what is allowed
		 */
		for (String s : getSafeListFor(ListKey.ITEM))
		{
			if (s.equalsIgnoreCase(string))
			{
				return true;
			}
		}
		if ("potion".equalsIgnoreCase(string))
		{
			return false;
		}
		for (String s : getSafeListFor(ListKey.PROHIBITED_ITEM))
		{
			if (s.equalsIgnoreCase(string))
			{
				return false;
			}
		}
		return true;
	}

	// TODO Not sure how to do this (and if it's necessary?)
	// Overall, grants is still an interesting behavior in Spells, so no need in 5.x
	// @Override
	// public PObject getActiveEquivalent(PlayerCharacter pc)
	// {
	// }
}
