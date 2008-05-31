/*
 * Spell.java
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.core.CharacterDomain;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.util.SpellPointCostInfo;
import pcgen.core.bonus.util.SpellPointCostInfo.SpellPointFilterType;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Spell extends PObject
{
	private HashMap<String, Integer> levelInfo = null;
	private Map<String, Prerequisite> preReqMap = null;
	private String fixedCasterLevel = null;
	private String fixedDC = null;

	private HashMap<String, Integer> spellPointCost = new HashMap<String,  Integer>();

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

	public BigDecimal getCost()
	{
		BigDecimal cost = get(ObjectKey.COST);
		return cost == null ? BigDecimal.ZERO : cost;
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
			for (String aType : getTypeList(false))
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

	public int getFirstLevelForKey(final String key, final PlayerCharacter aPC)
	{
		final int[] levelInt = levelForKey(key, aPC);
		int result = -1;

		if (levelInt.length > 0)
		{
			for (int i=0; i < levelInt.length; i++)
				if (levelInt[i]>-1)
					return levelInt[i];
		}

		return result;
	}

	protected void doGlobalTypeUpdate(final String aType)
	{
		Globals.addTypeForSpells(aType);
	}

	/**
	 * appends aString to the existing levelString
	 * if key=".CLEAR" then clear the levelString
	 * else levelString should be in form of source|name|level
	 * where source is CLASS or DOMAIN
	 * name is the name of the CLASS or DOMAIN
	 * and level is an integer representing the level of the spell for the named CLASS or DOMAIN
	 * @param key
	 * @param aLevel
	 */
	public void setLevelInfo(final String key, final String aLevel)
	{
		try
		{
			setLevelInfo(key, Integer.parseInt(aLevel));
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Could not set level info.", exc);
		}
	}

	public void clearLevelInfo(String type)
	{
		if (levelInfo != null)
		{
			String typeBar = type + "|";
			for (Iterator<String> it = levelInfo.keySet().iterator(); it
				.hasNext();)
			{
				if (it.next().startsWith(typeBar))
				{
					it.remove();
				}
			}
		}
	}

	public void setLevelInfo(final String key, final int level)
	{
		if (level == -1)
		{
			if (levelInfo != null)
			{
				levelInfo.remove(key);
			}
		}
		else
		{
			if (levelInfo == null)
			{
				levelInfo = new HashMap<String, Integer>();
			}

			levelInfo.put(key, Integer.valueOf(level));
		}
	}

	/**
	 * This method gets the information about the levels at which classes
	 * and domains may cast the spell.
	 *
	 * Modified 8 Sept 2003 by Sage_Sam for bug #801469
	 *
	 * @return Map containing the class levels and domains that
	 *     may cast the spell
	 * @param aPC
	 */
	public Map<String, Integer> getLevelInfo(final PlayerCharacter aPC)
	{
		Map<String, Integer> wLevelInfo = null;

		if (levelInfo != null)
		{
			wLevelInfo = new HashMap<String, Integer>(levelInfo);
		}

		if (aPC != null)
		{
			if (wLevelInfo == null)
			{
				wLevelInfo = new HashMap<String, Integer>();
			}

			wLevelInfo.putAll(aPC.getSpellInfoMap("CLASS", getKeyName()));
			wLevelInfo.putAll(aPC.getSpellInfoMap("DOMAIN", getKeyName()));
		}

		return wLevelInfo;
	}


	public String getPCCText()
	{
		String aString;

		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));

		//CLASSES:
		//DOMAINS:
		if (getLevelInfo(null) != null)
		{
			final List<String> classList = new ArrayList<String>();
			final List<String> domainList = new ArrayList<String>();
			final List<String> miscList = new ArrayList<String>();

			for ( Map.Entry<String, Integer> entry : getLevelInfo(null).entrySet() )
			{
				aString = entry.getKey();

				if (aString.startsWith("CLASS|"))
				{
					classList.add(aString.substring(6) + '=' + entry.getValue().toString());
				}
				else if (aString.startsWith("DOMAIN|"))
				{
					domainList.add(aString.substring(7) + '=' + entry.getValue().toString());
				}
				else
				{
					miscList.add(aString + '|' + entry.getValue().toString());
				}
			}

			if (classList.size() != 0)
			{
				txt.append("\tCLASSES:").append(StringUtil.join(classList, "|"));
			}

			if (domainList.size() != 0)
			{
				txt.append("\tDOMAINS:").append(StringUtil.join(domainList, "|"));
			}

			if (miscList.size() != 0)
			{
				txt.append("\tSPELLLEVEL:").append(StringUtil.join(miscList, "|"));
			}
		}

		if (hasSpellPointCost())
		{
			txt.append(getSpellPointCostActual());
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

	public String getTarget()
	{
		String target = get(StringKey.TARGET_AREA);
		return target == null ? Constants.EMPTY_STRING : target;
	}

	public void addPreReqMapEntry(final String type, final Prerequisite preReq)
	{
		if (preReqMap == null)
		{
			preReqMap = new HashMap<String, Prerequisite>();
		}

		preReqMap.put(type, preReq);

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

			if (spellPointCost != null)
			{
				aSpell.spellPointCost = new HashMap<String,Integer>(spellPointCost);
				
			}

			if (levelInfo != null)
			{
				aSpell.levelInfo = new HashMap<String, Integer>(levelInfo);
			}
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

	public String getLevelString()
	{
		if (levelInfo == null)
			return "";
		StringBuffer s = new StringBuffer();
		for (String key : levelInfo.keySet())
		{
			String val = levelInfo.get(key).toString();
			StringTokenizer aTok = new StringTokenizer(key, "|", false);
			aTok.nextToken();
			if (s.toString().length()>0)
				s.append(", ");
			s.append(aTok.nextToken()).append(" ");
			s.append(val);
		}
		return s.toString();
	}

	/**
	 * isLevel(int aLevel)
	 *
	 * @param aLevel level of the spell
	 * @param aPC
	 * @return true if the spell is of the given level in any spell list
	 */
	public boolean isLevel(final int aLevel, final PlayerCharacter aPC)
	{
		if (levelInfo == null)
		{
			return false;
		}
		final Integer levelKey = Integer.valueOf(aLevel);
		for (PCClass cls : aPC.getClassList())
		{
			if (levelKey.equals(levelInfo.get("CLASS|" + cls.getKeyName())))
			{
				return true;
			}
		}
		for (CharacterDomain domain : aPC.getCharacterDomainList())
		{
			if (levelKey.equals(levelInfo.get("DOMAIN|" + domain.getDomain().getKeyName())))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Assess if this spell is of the requested level for any class.
	 * @param level The level to be checked.
	 * @return True if the spell is the requested level.
	 */
	public boolean isLevel(final int level)
	{
		if (levelInfo == null)
		{
			return false;
		}
		for (String key : levelInfo.keySet())
		{
			if (level == levelInfo.get(key))
			{
				return true;
			}
		}
		return false;
	}

	public int levelForKey(final String mType, final String sType, final PlayerCharacter aPC)
	{
		int result = -1;
		final Map<String, Integer> wLevelInfo = getLevelInfo(aPC);

		if ((wLevelInfo != null) && (wLevelInfo.size() != 0))
		{
			Integer lvl = wLevelInfo.get(mType + "|" + sType);

			if (lvl == null)
			{
				lvl = wLevelInfo.get(mType + "|ALL");
			}

			if ((lvl == null) && mType.equals("CLASS"))
			{
				final PCClass aClass = Globals.getClassKeyed(sType);

				if (aClass != null)
				{
					final StringTokenizer aTok = new StringTokenizer(aClass.getType(), ".", false);

					while (aTok.hasMoreTokens() && (lvl == null))
					{
						lvl = wLevelInfo.get(mType + "|TYPE." + aTok.nextToken());
					}
				}
			}

			if (lvl != null)
			{
				result = lvl.intValue();
			}
		}

		return result;
	}

	public int[] levelForKey(final String key, final PlayerCharacter aPC)
	{
		if ((levelInfo == null) || (levelInfo.size() == 0))
		{
			final int[] temp = new int[1];

			//If it's not regularly on the list, check if some SPELLLEVEL tag added it.
			if (aPC != null)
			{
				temp[0] = aPC.getSpellLevelforKey(key + "|" + getKeyName(), -1);
			}
			else
			{
				temp[0] = -1;
			}
			return temp;
		}

		// should consist of CLASS|name and DOMAIN|name pairs
		final StringTokenizer aTok = new StringTokenizer(key, "|", false);
		final int[] levelInt = new int[aTok.countTokens() / 2];
		int i = 0;

		while (aTok.hasMoreTokens())
		{
			final String objectType = aTok.nextToken();

			if (aTok.hasMoreTokens())
			{
				final String objectName = aTok.nextToken();
				levelInt[i++] = levelForKey(objectType, objectName, aPC);
			}
		}

		return levelInt;
	}

	public boolean levelForKeyContains(final String key, final int levelMatch, final PlayerCharacter aPC)
	{
		if ((preReqMap != null) && preReqMap.containsKey(key))
		{
			final List<Prerequisite> qList = new ArrayList<Prerequisite>();
			qList.add(preReqMap.get(key));

			if (!PrereqHandler.passesAll(qList, aPC, this))
			{
				return false;
			}
		}

		final int[] levelInt = levelForKey(key, aPC);

		for (int i = 0; i < levelInt.length; ++i)
		{
			// always match if levelMatch==-1
			if (((levelMatch == -1) && (levelInt[i] >= 0)) || ((levelMatch >= 0) && (levelInt[i] == levelMatch)))
			{
				return true;
			}
		}

		//If it's not regularly on the list, check if some SPELLLEVEL tag added it.
		if (aPC != null)
		{
			return (aPC.isSpellLevelforKey(key + "|" + getKeyName(), levelMatch));
		}
		return false;
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
			return levelInfo == null && otherSpell.levelInfo == null
				|| levelInfo != null && levelInfo.equals(otherSpell.levelInfo);
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
	
	public void clearSpellPointCost()
	{
		spellPointCost.clear();
	}
	
	public void setParsedSpellPointCost(String component, final int value)
	{
		hasSpellPointCost = true;
		if (spellPointCost.containsKey(component))
		{
			int val = spellPointCost.get(component);
			spellPointCost.put(component, value + val);
		}
		else
		{
			spellPointCost.put(component, value);
		}
	}
	public static boolean hasSpellPointCost()
	{
		return hasSpellPointCost;
	}
	public Map<String,Integer> getSpellPointCostActualParts()
	{
		Map<String,Integer> spCost = new HashMap<String, Integer>();
		int RunningTotal =0;
				
		for (String spComponent: spellPointCost.keySet())
		{
			int value = spellPointCost.get(spComponent);
			int translatedValue =  value;
			spCost.put(spComponent, translatedValue);
		}
		return spCost;		
	}
	public String getSPCostStrings()
	{
		Map<String,Integer> spCost = getSpellPointCostActualParts();
		int totalSpellPoints =  getSpellPointCostActual();
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		
		sb2.append("");
		sb.append(totalSpellPoints); 
		if (spCost.size() ==0)
		{
			return sb.toString();
		}
		if(spCost.size()==1 && spCost.containsKey("TOTAL"))
		{
			return sb.toString();
		}
		sb.append(" [");
		
		// Using a TreeSet so they are sorted no matter what order the data is input 
		// by the lst coder
		TreeSet<String> fields = new TreeSet<String>();
		fields.addAll(spCost.keySet());

		
		for (String aComponent: fields)
		{
			if (aComponent.equalsIgnoreCase("Range"))
			{
				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent));
				sb2.append("/");
			}
			else if(aComponent.equalsIgnoreCase("Area of Effect"))
			{
				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent));
				sb2.append("/");
			}
			else if (aComponent.equalsIgnoreCase("Duration"))
			{
				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent));
				sb2.append("/");
			}
			else
			{
				sb.append(aComponent);
				sb.append(" ");
				sb.append(spCost.get(aComponent));
				sb.append("/");
			}
			
		}
		if(sb2.length() < 1)
		{
			sb.replace(sb.length()-1, sb.length(), "");
		}
		sb2.replace(sb2.length()-1, sb2.length(), "");
		
		sb.append(sb2.toString());
		sb.append("]");
		return sb.toString();	
	}
	public String getSPCostStrings(PlayerCharacter aPC)
	{
		Map<String,Integer> spCost = getSpellPointCostActualParts();
		int totalSpellPoints =  getSpellPointCostActual(aPC);
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer sb3 = new StringBuffer();
 
		int bonus =0;
		int tempbonus =0;
		tempbonus =	getBonusForSpellPointCostComponent(aPC, "TOTAL");
		if (spCost.size()==0)
		{
			sb.append(totalSpellPoints + bonus);
			return sb.toString();
		}
		else if(spCost.size()==1 && spCost.containsKey("TOTAL"))
		{
			sb.append(totalSpellPoints + bonus);
			return sb.toString();
		}
		
		//sb.append(totalSpellPoints);
		
		// Using a TreeSet so they are sorted no matter what order the data is input 
		// by the lst coder
		TreeSet<String> fields = new TreeSet<String>();
		fields.addAll(spCost.keySet());
		
		for (String aComponent: fields)
		{
			if (aComponent.equalsIgnoreCase("Range"))
			{
				bonus =	getBonusForSpellPointCostComponent(aPC, aComponent);
				
				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent)+ bonus);
				sb2.append("/");
			}
			else if(aComponent.equalsIgnoreCase("Area of Effect"))
			{
				bonus =	getBonusForSpellPointCostComponent(aPC, aComponent);
				
				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent)+ bonus);
				sb2.append("/");
			}
			else if (aComponent.equalsIgnoreCase("Duration"))
			{
				bonus =	getBonusForSpellPointCostComponent(aPC, aComponent);
				
				sb2.append(aComponent);
				sb2.append(" ");
				sb2.append(spCost.get(aComponent)+ bonus);
				sb2.append("/");
			}
			else
			{
				bonus =	getBonusForSpellPointCostComponent(aPC, aComponent);
				
				sb3.append(aComponent);
				sb3.append(" ");
				sb3.append(spCost.get(aComponent) + bonus);
				sb3.append("/");
			}	
			bonus = 0;
		}
		int total = totalSpellPoints + tempbonus;  
		
		if(sb2.length() < 1)
		{
			sb.replace(sb.length()-1, sb.length(), "");
		}
		sb2.replace(sb2.length()-1, sb2.length(), "");
		sb.append(total); 
		sb.append(" ["); 
		sb.append(sb3.toString());
		sb.append(sb2.toString());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * For a passed component name and PC, this returns any bonus from 
	 * SCHOOL, SUBSCHOOL, or SPELL name
	 * 
	 * @param aPC
	 * @param aComponent
	 * @return aBonus
	 */
	private int getBonusForSpellPointCostComponent(final PlayerCharacter aPC,
		final String aComponent)
	{
		int aBonus = 0;
		for (String school: getSafeListFor(ListKey.SPELL_SCHOOL))
		{
			aBonus += (int)aPC.getTotalBonusTo("SPELLPOINTCOST", "SCHOOL." + school.toUpperCase() +";"+ aComponent.toUpperCase());
		}
		for (String subSchool: getSafeListFor(ListKey.SPELL_SUBSCHOOL))
		{
			aBonus += (int)aPC.getTotalBonusTo("SPELLPOINTCOST", "SUBSCHOOL." + subSchool.toUpperCase() +";"+ aComponent.toUpperCase());
		}
		aBonus += (int)aPC.getTotalBonusTo("SPELLPOINTCOST", "SPELL." + this.getKeyName() +";"+ aComponent.toUpperCase());
		return aBonus;
	}
	public int getSpellPointCostActual()
	{	
		int runningTotal = 0;
		Map<String,Integer> spCost = getSpellPointCostActualParts();
		for (String aComponent: spCost.keySet())
		{
			runningTotal += spCost.get(aComponent);
		}
		return runningTotal;
	}
	public int getSpellPointCostActual(PlayerCharacter aPC)
	{	
		int runningTotal = 0;
		List<BonusObj> bonusList = aPC.getActiveBonusList();
		Set<BonusObj> bonuses = new HashSet<BonusObj>();
		bonuses.addAll(bonusList);

		
		Map<String,Integer> spCost = getSpellPointCostActualParts();
		for (String aComponent: spCost.keySet())
		{
			runningTotal += spCost.get(aComponent);
		}
		if (!aPC.hasSpellInSpellbook(this, aPC.getSpellBookNameToAutoAddKnown()))
		{
			return runningTotal;
		}
		for (BonusObj b: bonuses)
		{
			if (b.toString().contains("SPELLPOINTCOST"))
			{
				try {
					List<SpellPointCostInfo> spBonusInfo = (List<SpellPointCostInfo>) b.getBonusInfoList();
					for (SpellPointCostInfo info: spBonusInfo)
					{
						if (!info.isVirtual())
						{
							boolean getBonus = false;
							if(info.getSpellPointPartFilter() == SpellPointFilterType.SCHOOL)
							{
								for (String aSchool: getSafeListFor(ListKey.SPELL_SCHOOL))
								{
									if (info.getSpellPointPartFilterValue().equalsIgnoreCase(aSchool))
									getBonus = true;
								}
							}
							else if(info.getSpellPointPartFilter() == SpellPointFilterType.SUBSCHOOL)
							{
								for (String aSchool: getSafeListFor(ListKey.SPELL_SUBSCHOOL))
								{
									if (info.getSpellPointPartFilterValue().equalsIgnoreCase(aSchool))
									getBonus = true;
								}
							}
							else if(info.getSpellPointPartFilter() == SpellPointFilterType.SPELL 
									&& this.getDisplayName().equalsIgnoreCase(info.getSpellPointPartFilterValue().toUpperCase()))
							{
								getBonus = true;
							}
							if(getBonus)
							{
								String value = b.getValue();
								if(value != null)
								{
									runningTotal += Integer.parseInt(value);
								}
							}
						}
					}
				} 
				catch (Exception e) 
				{
					
				}			
			}
		}
		return runningTotal;
	}
	
	public int getSpellPointCostElementTotal()
	{
		return getSpellPointCostActualParts().size();
	}
	public String getSpellPointCostPartName(final int elementNumber )
	{
		Map<String,Integer> spCosts = getSpellPointCostActualParts();
		Set<String> spKeys = new TreeSet<String>();
		spKeys.addAll(spCosts.keySet());
		String [] theKeys = (String[]) spKeys.toArray();
		int size = spKeys.size();
		if (elementNumber < size)
		{
			return theKeys[elementNumber];
		}
		
		return "";
	}
	public String getSpellPointCostPartValue(final int elementNumber )
	{
		Map<String,Integer> spCosts =getSpellPointCostActualParts();
		Set<String> spKeys = new TreeSet<String>();
		spKeys.addAll(spCosts.keySet());
		String [] theKeys = (String[]) spKeys.toArray();
		int size = spKeys.size();
		if (elementNumber < size)
		{
			return spCosts.get( theKeys[elementNumber]).toString();
		}
		
		return "";
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

}
