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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.io.Serializable;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.KeyedListContainer;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
/**
 * @author Joe.Frazier
 *
 */
public class PObject extends CDOMObject implements Cloneable, Serializable, Comparable<Object>,
	KeyedListContainer
{
	/** Standard serialVersionUID for Serializable objects */
	private static final long serialVersionUID = 1;

	/** a boolean for whether something should recurse, default is false */
	private static boolean dontRecurse = false;

	/** The name to display to the user.  This should be internationalized. */
	private String displayName = Constants.EMPTY_STRING;

	private URI sourceURI = null;
	
	private final Class<?> myClass = getClass();
	
	/* ************
	 * Methods
	 * ************/

	/**
	 * if a class implements the Cloneable interface then it should have a
	 * public" 'clone ()' method It should be declared to throw
	 * CloneNotSupportedException', but subclasses do not need the "throws"
	 * declaration unless their 'clone ()' method will throw the exception
	 * Thus subclasses can decide to not support 'Cloneable' by implementing
	 * the 'clone ()' method to throw 'CloneNotSupportedException'
	 * If this rule were ignored and the parent did not have the "throws"
	 * declaration then subclasses that should not be cloned would be forced
	 * to implement a trivial 'clone ()' to satisfy inheritance
	 * final" classes implementing 'Cloneable' should not be declared to
	 * throw 'CloneNotSupportedException" because their implementation of
	 * clone ()' should be a fully functional method that will not
	 * throw the exception.
	 * @return cloned object
	 * @throws CloneNotSupportedException
	 */
	@Override
	public PObject clone() throws CloneNotSupportedException
	{
		final PObject retVal = (PObject) super.clone();

		retVal.setName(displayName);
		retVal.put(StringKey.KEY_NAME, get(StringKey.KEY_NAME));

		return retVal;
	}

	/**
	 * Compares the keys of the object.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.getKeyName().compareToIgnoreCase(((PObject) obj).getKeyName());
		}
		return 1;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( obj == null )
		{
			return false;
		}
		final String thisKey;
		final String otherKey;
		if ( obj instanceof PObject )
		{
			thisKey = getKeyName();
			otherKey = ((PObject)obj).getKeyName();
		}
		else
		{
			thisKey = toString();
			otherKey = obj.toString();
		}
		return thisKey.equalsIgnoreCase( otherKey );
	}

	/**
	 * Get the choices for this PC
	 * @param aChoice
	 * @param aPC
	 */
	public final void getChoices(
			final String          aChoice,
			final PlayerCharacter aPC)
	{
		final List availableList = new ArrayList();
		final List selectedList  = new ArrayList();
		ChooserUtilities.getChoices(this, aChoice, availableList, selectedList, aPC);
	}

	/**
	 * Set the name (sets keyname also)
	 * @param aString
	 */
	@Override
	public void setName(final String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			fireNameChanged(displayName, aString);
			displayName = aString;
			put(StringKey.KEY_NAME, aString);
		}
	}

	/**
	 * This method sets only the name not the key.
	 * @param aName Name to use for display
	 */
	public void setDisplayName( final String aName )
	{
		displayName = aName;
	}
	
	/**
	 * Get name
	 * @return name
	 */
	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////

	/**
	 * Get the output name of the item
	 * @return the output name of the item
	 */
	public final String getOutputName()
	{
		String outputName = get(StringKey.OUTPUT_NAME);
		// if no OutputName has been defined, just return the regular name
		if (outputName == null)
		{
			return displayName;
		}
		else if (outputName.equalsIgnoreCase("[BASE]") && displayName.indexOf('(') != -1)
		{
			outputName = displayName.substring(0, displayName.indexOf('(')).trim();
		}
		if (outputName.indexOf("[NAME]") >= 0)
		{
			outputName = outputName.replaceAll("\\[NAME\\]",
					getPreFormatedOutputName());
		}
		return outputName;
	}

	/**
	 * Set the source file for this object
	 * @param sourceFile
	 */
	public final void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	/**
	 * Get the source file for this object
	 * @return the source file for this object
	 */
	public final URI getSourceURI()
	{
		return sourceURI;
	}

	/**
	 * Gets the Source string for this object using the default source display
	 * mode.
	 * 
	 * @return The Source string.
	 */
	public String getDefaultSourceString()
	{
		return SourceFormat.getFormattedString(this,
				Globals.getSourceDisplay(), true);
	}
	
	/**
	 * Get the type of PObject
	 * 
	 * @return the type of PObject
	 */
	public String getType()
	{
		return StringUtil.join(getTrueTypeList(false), ".");
	}

	public List<Type> getTrueTypeList(final boolean visibleOnly)
	{
		final List<Type> ret = getSafeListFor(ListKey.TYPE);
		if (visibleOnly)
		{
			for (Iterator<Type> it = ret.iterator(); it.hasNext();)
			{
				if (SettingsHandler.getGame().isTypeHidden(myClass, it.next().toString()))
				{
					it.remove();
				}
			}
		}
		return Collections.unmodifiableList(ret);
	}
	
	/**
	 * If aType begins with an &#34; (Exclamation Mark) the &#34; will be
	 * removed before checking the type.
	 *
	 * @param aType
	 * @return Whether the item is of this type
	 * 
	 * Note:  This method is overridden in Equipment.java
	 */
	@Override
	public boolean isType(final String aType)
	{
		final String myType;

		if (aType.length() == 0)
		{
			return false;
		}
		else if (aType.charAt(0) == '!')
		{
			myType = aType.substring(1).toUpperCase();
		}
		else if (aType.startsWith("TYPE=") || aType.startsWith("TYPE."))	//$NON-NLS-1$ //$NON-NLS-2$
		{
			myType = aType.substring(5).toUpperCase();
		}
		else
		{
			myType = aType.toUpperCase();
		}
		
		//
		// Must match all listed types in order to qualify
		//
		StringTokenizer tok = new StringTokenizer(myType, ".");
		while (tok.hasMoreTokens())
		{
			if (!containsInList(ListKey.TYPE, Type.getConstant(tok.nextToken())))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Set the campaign source
	 * @param arg
	 */
	public void setSourceCampaign(final Campaign arg)
	{
		put(ObjectKey.SOURCE_CAMPAIGN, arg);
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from
	 *
	 * @return Campaign instance referencing the file containing the
	 *         source for this object
	 */
	public Campaign getSourceCampaign()
	{
		return get(ObjectKey.SOURCE_CAMPAIGN);
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	/**
	 * Get the PCC text with the saved name
	 * @return the PCC text with the saved name
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		txt.append("\t");
		txt.append(getPCCText(false));
		return txt.toString();
	}

	/**
	 * Get the PCC text
	 * @param saveName
	 * @return PCC text
	 */
	protected String getPCCText(final boolean saveName)
	{
//		Iterator e;
		String aString;
		final StringBuffer txt = new StringBuffer(200);

		if (saveName)
		{
			txt.append(getDisplayName());
		}

		if (hasPrerequisites())
		{
			final StringWriter writer = new StringWriter();
			for (Prerequisite prereq : getPrerequisiteList())
			{
				final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
				try
				{
					writer.write("\t");
					prereqWriter.write(writer, prereq);
				}
				catch (PersistenceLayerException e1)
				{
					e1.printStackTrace();
				}
			}
			txt.append(writer);
		}

		return txt.toString();
	}

	public List<SpecialAbility> addSpecialAbilitiesToList(final List<SpecialAbility> aList, final PlayerCharacter aPC)
	{
		List<SpecialAbility> salist = aPC.getAssocList(this,
				AssociationListKey.SPECIAL_ABILITY);
		if (salist == null)
		{
			return aList;
		}
		for ( SpecialAbility sa : salist )
		{
			if (sa.pcQualifiesFor(aPC))
			{
				final String key = sa.getKeyName();
				final int idx = key.indexOf("%CHOICE");

				if (idx >= 0)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(key.substring(0, idx));

					if (aPC.hasAssociations(this))
					{
						sb.append(StringUtil.joinToStringBuffer(aPC.getAssociationList(this), ", "));
					}
					else
					{
						sb.append("<undefined>");
					}

					sb.append(key.substring(idx + 7));
					sa = new SpecialAbility(sb.toString(), sa.getSADesc());
				}

				aList.add(sa);
			}
		}

		return aList;
	}

	public void globalChecks(final PlayerCharacter aPC)
	{
		globalChecks(false, aPC);
	}

	protected void globalChecks(final boolean flag, final PlayerCharacter aPC)
	{
		aPC.setDirty(true);
		for (TransitionChoice<Kit> kit : getSafeListFor(ListKey.KIT_CHOICE))
		{
			kit.act(kit.driveChoice(aPC), this, aPC);
		}
		TransitionChoice<Region> region = get(ObjectKey.REGION_CHOICE);
		if (region != null)
		{
			region.act(region.driveChoice(aPC), this, aPC);
		}

		if (flag)
		{
			getChoices(getSafe(StringKey.CHOICE_STRING), aPC);
		}

		if (this instanceof PCClass)
		{
			final PCClass aClass = (PCClass) this;
			PCClassLevel classLevel = aClass.getClassLevel(aClass.getLevel(aPC));
			CDOMObjectUtilities.addAdds(classLevel, aPC);
			CDOMObjectUtilities.checkRemovals(classLevel, aPC);
		}
		else
		{
			CDOMObjectUtilities.addAdds(this, aPC);
			CDOMObjectUtilities.checkRemovals(this, aPC);
		}
		activateBonuses(aPC);
	}

	/*
	 * REFACTOR Get this OUT of PObject's interface since this is ONLY in PCClass.
	 * Not to mention that the overload code will probably be removed from PCClass.
	 */
	void fireNameChanged(final String oldName, final String newName)
	{
		// This method currently does nothing so it may be overriden in PCClass.
	}

	int numberInList(PlayerCharacter pc, final String aType)
	{
		return 0;
	}

	/**
	 * rephrase parenthetical name components, if appropriate
	 * @return pre formatted output name
	 */
	private String getPreFormatedOutputName()
	{
		//if there are no () to pull from, just return the name
		if ((displayName.indexOf('(') < 0) || (displayName.indexOf(')') < 0))
		{
			return displayName;
		}

		//we just take from the first ( to the first ), typically there should only be one of each
		final String subName = displayName.substring(displayName.indexOf('(') + 1, displayName.indexOf(')')); //the stuff inside the ()
		final StringTokenizer tok = new StringTokenizer(subName, "/");
		final StringBuffer newNameBuff = new StringBuffer();

		while (tok.hasMoreTokens())
		{
			//build this new string from right to left
			newNameBuff.insert(0, tok.nextToken());

			if (tok.hasMoreTokens())
			{
				newNameBuff.insert(0, " ");
			}
		}

		return newNameBuff.toString();
	}

	/**
	 * Get the list of bonuses for this object
	 * @param as TODO
	 * @return the list of bonuses for this object
	 */
	public List<BonusObj> getRawBonusList(PlayerCharacter pc)
	{
		List<BonusObj> bonusList = getSafeListFor(ListKey.BONUS);
		if (pc != null)
		{
			List<BonusObj> listToo = pc.getAssocList(this, AssociationListKey.BONUS);
			if (listToo != null)
			{
				bonusList.addAll(listToo);
			}
		}
		return bonusList;
	}

	/**
	 * Get the list of bounuses of a particular type for this object
	 * @param as TODO
	 * @param aType
	 * @param aName
	 * @return the list of bounuses of a particular type for this object
	 */
	public List<BonusObj> getBonusListOfType(AssociationStore as, final String aType, final String aName)
	{
		return BonusUtilities.getBonusFromList(getBonusList(as), aType, aName);
	}

	/**
	 * Apply the bonus to a PC, pass through object's default bonuslist
	 *
	 * @param aType
	 * @param aName
	 * @param obj
	 * @param aPC
	 * @return the bonus
	 */
	public double bonusTo(final String aType, final String aName, final AssociationStore obj, final PlayerCharacter aPC)
	{
		return bonusTo(aType, aName, obj, getBonusList(obj), aPC);
	}

	/**
	 * Apply the bonus to a PC
	 *
	 * @param aType
	 * @param aName
	 * @param obj
	 * @param aBonusList
	 * @param aPC
	 * @return the bonus
	 */
	public double bonusTo(String aType, String aName, final Object obj, final List<BonusObj> aBonusList, final PlayerCharacter aPC)
	{
		if ((aBonusList == null) || (aBonusList.size() == 0))
		{
			return 0;
		}

		double retVal = 0;

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final String aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();

		if (!dontRecurse && (this instanceof Ability) && !Globals.checkRule(RuleConstants.FEATPRE))
		{
			// SUCK!  This is horrid, but bonusTo is actually recursive with respect to
			// passesPreReqToGain and there is no other way to do this without decomposing the
			// dependencies.  I am loathe to break working code.
			// This addresses bug #709677 -- Feats give bonuses even if you no longer qualify
			dontRecurse = true;

			boolean returnZero = false;

			if (!PrereqHandler.passesAll(getPrerequisiteList(), aPC, this))
			{
				returnZero = true;
			}

			dontRecurse = false;

			if (returnZero)
			{
				return 0;
			}
		}

		int iTimes = 1;

		if (aPC != null && "VAR".equals(aType))
		{
			iTimes = Math.max(1, aPC.getDetailedAssociationCount(this));
		}

		for ( BonusObj bonus : aBonusList )
		{
			String bString = bonus.toString().toUpperCase();

			if (aPC != null && aPC.hasAssociations(this))
			{
				int span = 4;
				int idx = bString.indexOf("%VAR");

				if (idx == -1)
				{
					idx = bString.indexOf("%LIST|");
					span = 5;
				}

				if (idx >= 0)
				{
					final String firstPart = bString.substring(0, idx);
					final String secondPart = bString.substring(idx + span);

					for (String assoc : aPC.getAssociationList(this))
					{
						final String xString = new StringBuffer().append(firstPart).append(assoc).append(secondPart)
							.toString().toUpperCase();
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
					}
				}
			}
			else
			{
				retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
			}
		}

		return retVal;
	}

	/**
	 * returns all BonusObj's that are "active"
	 * @param aPC A PlayerCharacter object.
	 * @return active bonuses
	 */
	public List<BonusObj> getActiveBonuses(final PlayerCharacter aPC)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getRawBonusList(aPC) )
		{
			if (aPC.isApplied(bonus))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	/**
	 * Get the list of bonuses as a String
	 * @param pc TODO
	 * @param aString
	 * @return the list of bonuses as a String
	 */
	public boolean hasBonusWithInfo(PlayerCharacter pc, final String aString)
	{
		for ( BonusObj bonus : getRawBonusList(pc) )
		{
			if (bonus.getBonusInfo().equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets all the BonusObj's to "active"
	 * @param aPC
	 */
	public void activateBonuses(final PlayerCharacter aPC)
	{
		for (Iterator<BonusObj> ab = getRawBonusList(aPC).iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = ab.next();
			aBonus.setApplied(aPC, false);

			if (aBonus.qualifies(aPC)
				&& aBonus.getPCLevel() <= aPC.getTotalLevels())
			{
				aBonus.setApplied(aPC, true);
			}
		}
	}

	/**
	 * Deactivate all of the bonuses
	 */
	public void deactivateBonuses(PlayerCharacter aPC)
	{
		for (BonusObj bonus : getRawBonusList(aPC))
		{
			bonus.setApplied(aPC, false);
		}
	}

	/**
	 * calcBonus adds together all the bonuses for aType of aName
	 *
	 * @param bString       Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param aType         Such as "COMBAT"
	 * @param aName         Such as "AC"
	 * @param aTypePlusName "COMBAT.AC."
	 * @param obj           The object to get the bonus from
	 * @param iTimes        multiply bonus * iTimes
	 * @param aBonusObj
	 * @param aPC
	 * @return bonus
	 */
	private double calcBonus(final String bString, final String aType, final String aName, String aTypePlusName, final Object obj, final int iTimes,
							 final BonusObj aBonusObj, final PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|");

		if (aTok.countTokens() < 3)
		{
			Logging.errorPrint("Badly formed BONUS:" + bString);

			return 0;
		}

		String aString = aTok.nextToken();

		if ((!aString.equalsIgnoreCase(aType) && !aString.endsWith("%LIST"))
			|| (aString.endsWith("%LIST") && (numberInList(aPC, aType) == 0)) || (aName.equals("ALL")))
		{
			return 0;
		}

		final String aList = aTok.nextToken();

		if (!aList.equals("LIST") && !aList.equals("ALL") && (aList.toUpperCase().indexOf(aName.toUpperCase()) < 0))
		{
			return 0;
		}

		if (aList.equals("ALL")
			&& ((aName.indexOf("STAT=") >= 0) || (aName.indexOf("TYPE=") >= 0) || (aName.indexOf("LIST") >= 0)
			|| (aName.indexOf("VAR") >= 0)))
		{
			return 0;
		}

		if (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
		}

		double iBonus = 0;

		if (obj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) obj).getVariableValue(aString, "").doubleValue();
		}
		else if (obj instanceof Equipment)
		{
			iBonus = ((Equipment) obj).getVariableValue(aString, "", aPC).doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aString);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				Logging.errorPrint("calcBonus NumberFormatException in BONUS: " + aString, e);
			}
		}

		final String possibleBonusTypeString = aBonusObj.getTypeString();

		// must meet criteria before adding any bonuses
		if (obj instanceof PlayerCharacter)
		{
			if ( !aBonusObj.qualifies((PlayerCharacter)obj) )
			{
				return 0;
			}
		}
		else
		{
			if ( !PrereqHandler.passesAll(aBonusObj.getPrerequisiteList(), ((Equipment)obj), aPC) )
			{
				return 0;
			}
		}

		double bonus = 0;

		if ("LIST".equalsIgnoreCase(aList))
		{
			final int iCount = numberInList(aPC, aName);

			if (iCount != 0)
			{
				bonus += (iBonus * iCount);
			}
		}

		String bonusTypeString = null;

		final StringTokenizer bTok = new StringTokenizer(aList, ",");

		if (aList.equalsIgnoreCase("LIST"))
		{
			bTok.nextToken();
		}
		else if (aList.equalsIgnoreCase("ALL"))
		{
			// aTypePlusName looks like: "SKILL.ALL."
			// so we need to reset it to "SKILL.Hide."
			aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();
			bonus = iBonus;
			bonusTypeString = possibleBonusTypeString;
		}

		while (bTok.hasMoreTokens())
		{
			if (bTok.nextToken().equalsIgnoreCase(aName))
			{
				bonus += iBonus;
				bonusTypeString = possibleBonusTypeString;
			}
		}

		if (obj instanceof Equipment)
		{
			((Equipment) obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}

		// The "ALL" subtag is used to build the stacking bonusMap
		// not to get a bonus value, so just return
		if (aList.equals("ALL"))
		{
			return 0;
		}

		return bonus * iTimes;
	}

	public List<BonusObj> getBonusList(AssociationStore as)
	{
		if (as instanceof PlayerCharacter)
		{
			return getRawBonusList((PlayerCharacter) as);
		}
		else
		{
			return getRawBonusList(null);
		}
	}

	public List<? extends CDOMList<Spell>> getSpellLists(PlayerCharacter pc)
	{
		return null;
	}

	public String getVariableSource()
	{
		return "POBJECT|" + this.getKeyName();
	}

	public void clearSpellListInfo()
	{
		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> modLists = getModifiedLists();
		for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : modLists)
		{
			if (ref.getReferenceClass().equals(ClassSpellList.class)
					|| ref.getReferenceClass().equals(DomainSpellList.class))
			{
				removeAllFromList(ref);
			}
		}
	}
	
	public boolean hasChooseToken()
	{
		String oldchoice = get(StringKey.CHOICE_STRING);
		return oldchoice != null && oldchoice.length() > 0
				|| get(ObjectKey.CHOOSE_INFO) != null;
	}
}
