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

import pcgen.core.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

import java.math.BigDecimal;
import java.util.*;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Spell extends PObject
{
	private BigDecimal cost = BigDecimalHelper.ZERO;
	private HashMap levelInfo = null;
	private List<String> descriptorList = new ArrayList<String>();
	private List variantList = null; //Lazy initialization, it's rarely, if ever, used.
	private Map preReqMap = null;
	private SortedSet castingTime = new TreeSet();
	private SortedSet componentList = new TreeSet();
	private SortedSet duration = new TreeSet();
	private SortedSet range = new TreeSet();
	private SortedSet saveInfo = new TreeSet();
	private SortedSet<String> school = new TreeSet<String>();
	private SortedSet spellResistance = new TreeSet();
	private SortedSet<String> subschool = new TreeSet<String>();
	private String fixedCasterLevel = null;
	private String fixedDC = null;


	//private int minLVL = 0;
	//private int maxLVL = 9;
	private String creatableItem = "";
	private String spellStat = "";
	private String target = "";
	private int castingThreshold = 0;
	private int xpCost = 0;
	private int ppCost = 0;

	static boolean hasPPCost = false;

	public static boolean hasPPCost()
	{
		return hasPPCost;
	}

	///////////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////////
	public Spell()
	{
		super();
	}

	public void setCastingThreshold(final int arg)
	{
		castingThreshold = arg;
	}

	public int getCastingThreshold()
	{
		return castingThreshold;
	}

	public void setCastingTime(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			castingTime.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				castingTime.add(aString);
				Globals.addSpellCastingTimesSet(aString);
			}
		}
	}

	public String getCastingTime()
	{
		final String s = castingTime.toString();

		return s.substring(1, s.length() - 1);
	}

	public void setComponentList(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			componentList.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				componentList.add(aString);
				Globals.addSpellComponentSet(aString);
			}
		}
	}

	public String getComponentList()
	{
		final String s = componentList.toString();

		return s.substring(1, s.length() - 1);
	}

	public void setCost(final String aString)
	{
		try
		{
			cost = new BigDecimal(aString);
		}
		catch (NumberFormatException ignore)
		{
			// ignore
		}
		catch (StringIndexOutOfBoundsException ignore)
		{
			//thrown when aString is ""
		}
	}

	public BigDecimal getCost()
	{
		return cost;
	}

	public void setCreatableItem(final String creatableItem)
	{
		this.creatableItem = creatableItem;
	}

	public String getCreatableItem()
	{
		return creatableItem;
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

			if (si.getFeatList() != null)
			{
				for (Iterator i = si.getFeatList().iterator(); i.hasNext();)
				{
					final Ability aFeat = (Ability) i.next();
					spellLevel -= aFeat.getAddSpellLevel();
					metaDC += aFeat.bonusTo("DC", "FEATBONUS", aPC, aPC);
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
			final String statName = getStat();

			if (statName.length() > 0)
			{
				dc += aPC.getStatList().getStatModFor(statName);
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

		Iterator i = getSafeListFor(ListKey.TYPE).iterator();

		if (spellType.equals("ALL"))
		{
			while (i.hasNext())
			{
				final String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "TYPE." + aType);
			}
		}

		i = school.iterator();

		if (i != null)
		{
			while (i.hasNext())
			{
				final String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "SCHOOL." + aType);
			}
		}

		i = subschool.iterator();

		if (i != null)
		{
			while (i.hasNext())
			{
				final String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "SUBSCHOOL." + aType);
			}
		}

		i = descriptorList.iterator();

		if (i != null)
		{
			while (i.hasNext())
			{
				final String aType = (String) i.next();
				dc += (int) aPC.getTotalBonusTo("DC", "DESCRIPTOR." + aType);
			}
		}

		aPC.setSpellLevelTemp(0); // reset

		return dc;
	}

	public List<String> getDescriptorList()
	{
		return descriptorList;
	}

	public void setDuration(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			duration.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				duration.add(aString);
				Globals.addDurationSet(aString);
			}
		}
	}

	public String getDuration()
	{
		final String s = duration.toString();

		return s.substring(1, s.length() - 1);
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

	public void setLevelInfo(final String key, final int level)
	{
		if (".CLEAR".equals(key))
		{
			levelInfo = null;
		}
		else
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
					levelInfo = new HashMap();
				}

				levelInfo.put(key, new Integer(level));
			}
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
	public Map<String, String> getLevelInfo(final PlayerCharacter aPC)
	{
		Map<String, String> wLevelInfo = null;

		if (levelInfo != null)
		{
			wLevelInfo = (Map<String, String>) levelInfo.clone();
		}

		if (aPC != null)
		{
			if (wLevelInfo == null)
			{
				wLevelInfo = new HashMap<String, String>();
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

		appendPCCText(txt, castingTime, "CASTTIME");

		appendPCCText(txt, componentList, "COMPS");

		if (getCost().compareTo(BigDecimalHelper.ZERO) != 0)
		{
			txt.append("\tCOST:").append(getCost().toString());
		}

		//CLASSES:
		//DOMAINS:
		if (getLevelInfo(null) != null)
		{
			final List classList = new ArrayList();
			final List domainList = new ArrayList();
			final List miscList = new ArrayList();

			for (Iterator e = getLevelInfo(null).entrySet().iterator(); e.hasNext();)
			{
				final Map.Entry entry = (Map.Entry) e.next();
				aString = entry.getKey().toString();

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
				txt.append("\tCLASSES:").append(CoreUtility.join(classList, "|"));
			}

			if (domainList.size() != 0)
			{
				txt.append("\tDOMAINS:").append(CoreUtility.join(domainList, "|"));
			}

			if (miscList.size() != 0)
			{
				txt.append("\tSPELLLEVEL:").append(CoreUtility.join(miscList, "|"));
			}
		}

		if (getCastingThreshold() != 0)
		{
			txt.append("\tCT:").append(getCastingThreshold());
		}

		aString = getDescriptor("|");

		if (aString.length() != 0)
		{
			txt.append("\tDESCRIPTOR:").append(aString);
		}

		appendPCCText(txt, duration, "DURATION");

		aString = getCreatableItem();

		if (aString.length() != 0)
		{
			txt.append("\tITEM:").append(aString);
		}

		appendPCCText(txt, range, "RANGE");
		appendPCCText(txt, saveInfo, "SAVEINFO");
		appendPCCText(txt, school, "SCHOOL");

		aString = getStat();
		if (aString.length() != 0)
		{
			txt.append("\tSTAT:").append(aString);
		}

		appendPCCText(txt, spellResistance, "SPELLRES");
		appendPCCText(txt, subschool, "SUBSCHOOL");

		aString = getTarget();

		if (aString.length() != 0)
		{
			txt.append("\tTARGETAREA:").append(aString);
		}

		if ((variantList != null) && (variantList.size() != 0))
		{
			txt.append("\tVARIANTS:").append(CoreUtility.join(variantList, "|"));
		}

		if (getXPCost() != 0)
		{
			txt.append("\tXPCOST:").append(getXPCost());
		}

		if (getPPCost() != 0)
		{
			txt.append("\tPPCOST:").append(getPPCost());
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	public void setRange(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			range.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				range.add(aString);
				Globals.addSpellRangesSet(aString);
			}
		}
	}

	public String getRange()
	{
		final String s = range.toString();

		return s.substring(1, s.length() - 1);
	}

	public void setSaveInfo(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			saveInfo.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				saveInfo.add(aString);
				Globals.addSpellSaveInfoSet(aString);
			}
		}
	}

	public String getSaveInfo()
	{
		final String s = saveInfo.toString();

		return s.substring(1, s.length() - 1);
	}

	public void addSchool(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			school.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				school.add(aString);
			}
		}
	}

	public String getSchool()
	{
		final String s = school.toString();

		return s.substring(1, s.length() - 1);
	}

	public SortedSet<String> getSchools()
	{
		return school;
	}

	public void setSpellResistance(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			spellResistance.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				spellResistance.add(aString);
				Globals.addSpellSrSet(aString);
			}
		}
	}

	public String getSpellResistance()
	{
		final String s = spellResistance.toString();

		return s.substring(1, s.length() - 1);
	}

	public void setStat(final String aStat)
	{
		spellStat = aStat;
		Globals.addSpellStatSet(aStat);
	}

	public String getStat()
	{
		return spellStat;
	}

	public void addSubschool(final String aString)
	{
		if (aString.equals(".CLEAR"))
		{
			subschool.clear();
		}
		else
		{
			if (aString.length() != 0)
			{
				subschool.add(aString);

				if (aString.length() != 0)
				{
					Globals.getSubschools().add(aString);
				}
			}
		}
	}

	public String getSubschool()
	{
		final String s = subschool.toString();

		return s.substring(1, s.length() - 1);
	}

	public SortedSet<String> getSubschools()
	{
		return subschool;
	}

	public void setTarget(final String aString)
	{
		target = aString;

		if (aString.length() != 0)
		{
			Globals.addSpellTargetSet(aString);
		}

//		hmmm.... not sure what to do with effectTypes (merton_monk@yahoo.com, 12/10/02)
//		Globals.getEffectTypes().add(aString);
	}

	public String getTarget()
	{
		return target;
	}


	public List getVariants()
	{
		//Initialize lazily
		if (variantList == null)
		{
			variantList = new ArrayList();
		}

		return (ArrayList) variantList;
	}

	public void setXPCost(final String aString)
	{
		try
		{
			xpCost = Integer.parseInt(aString);
		}
		catch (NumberFormatException ignore)
		{
			//ignore
		}
	}

	public int getXPCost()
	{
		return xpCost;
	}

	public void setPPCost(final int argCost)
	{
		hasPPCost = true;

		ppCost = argCost;
	}

	public int getPPCost()
	{
		return ppCost;
	}

	public void addDescriptor(final String descriptor)
	{
		if (descriptor.equals(".CLEAR"))
		{
			descriptorList.clear();
		}
		else
		{
			descriptorList.add(descriptor);
		}
	}

	public void addPreReqMapEntry(final String type, final Prerequisite preReq)
	{
		if (preReqMap == null)
		{
			preReqMap = new HashMap();
		}

		preReqMap.put(type, preReq);

	}

	public void addVariant(final String variant)
	{
		if (variantList == null)
		{
			variantList = new ArrayList();
		}

		if (variant.length() != 0)
		{
			variantList.add(variant);
		}
	}

	public void clearLevelInfo()
	{
		levelInfo = null;
	}

	public void clearVariants()
	{
		variantList = null;
	}

	////////////////////////////////////////////////////////////
	// Public method(s)
	////////////////////////////////////////////////////////////
	public Object clone()
	{
		Spell aSpell = null;

		try
		{
			aSpell = (Spell) super.clone();
			aSpell.school = school;
			aSpell.subschool = subschool;
			aSpell.componentList = componentList;
			aSpell.castingTime = castingTime;
			aSpell.range = range;
			aSpell.target = target;
			aSpell.duration = duration;
			aSpell.saveInfo = saveInfo;
			aSpell.spellResistance = spellResistance;
			aSpell.descriptorList = descriptorList;
			aSpell.setCastingThreshold(castingThreshold);

			//aSpell.setMinLVL(minLVL);
			//aSpell.setMaxLVL(maxLVL);
			aSpell.creatableItem = creatableItem;
			aSpell.cost = cost;
			aSpell.xpCost = xpCost;
			aSpell.variantList = variantList;
			aSpell.ppCost = ppCost;

			if (levelInfo != null)
			{
				aSpell.levelInfo = (HashMap) levelInfo.clone();
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
		return getDescriptor(", ");
	}

	public boolean descriptorListContains(final List aList)
	{
		return CoreUtility.containsAny(descriptorList, aList);
	}

	public String getLevelString()
	{
		if (levelInfo == null)
			return "";
		StringBuffer s = new StringBuffer();
		for (Iterator i = levelInfo.keySet().iterator(); i.hasNext();)
		{
			String key = (String) i.next();
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
		final Map wLevelInfo = getLevelInfo(aPC);
		final Integer levelKey = new Integer(aLevel);
		for (Iterator i = wLevelInfo.keySet().iterator(); i.hasNext();)
		{
			final String key = (String)i.next();
			if (wLevelInfo.get(key).equals(levelKey))
				return true;
		}
		return false;
	}

	public int levelForKey(final String mType, final String sType, final PlayerCharacter aPC)
	{
		int result = -1;
		final Map wLevelInfo = getLevelInfo(aPC);

		if ((wLevelInfo != null) && (wLevelInfo.size() != 0))
		{
			Integer lvl = (Integer) wLevelInfo.get(mType + "|" + sType);

			if (lvl == null)
			{
				lvl = (Integer) wLevelInfo.get(mType + "|ALL");
			}

			if ((lvl == null) && mType.equals("CLASS"))
			{
				final PCClass aClass = Globals.getClassKeyed(sType);

				if (aClass != null)
				{
					final StringTokenizer aTok = new StringTokenizer(aClass.getType(), ".", false);

					while (aTok.hasMoreTokens() && (lvl == null))
					{
						lvl = (Integer) wLevelInfo.get(mType + "|TYPE." + aTok.nextToken());
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
			final List qList = new ArrayList();
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

	public boolean schoolContains(final List aList)
	{
		return CoreUtility.containsAny(school, aList);
	}

	public boolean subschoolContains(final List aList)
	{
		return CoreUtility.containsAny(subschool, aList);
	}

	public boolean descriptorContains(final String descriptor)
	{
		return descriptorList.contains(descriptor);
	}

	private String getDescriptor(final String delimiter)
	{
		final StringBuffer retVal = new StringBuffer(descriptorList.size() * 5);
		final Iterator i = descriptorList.iterator();

		while (i.hasNext())
		{
			final String aString = (String) i.next();

			if (retVal.length() > 0)
			{
				retVal.append(delimiter);
			}

			retVal.append(aString);
		}

		return retVal.toString();
	}

	private void appendPCCText(final StringBuffer sb, final Set ts, final String tag)
	{
		for (Iterator e = ts.iterator(); e.hasNext();)
		{
			sb.append('\t').append(tag).append(':').append(e.next().toString());
		}
	}
}
