/*
 * BonusObj.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.bonus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.base.util.FixedStringList;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * <code>BonusObj</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 **/
public abstract class BonusObj extends ConcretePrereqObject implements Serializable, Cloneable
{
	private List<Object>    bonusInfo       = new ArrayList<Object>();
	private Map<String, String>     dependMap  = new HashMap<String, String>();
	private Formula bonusFormula = FormulaFactory.ZERO;
	private Object  creatorObj;
	private Object  targetObj;
	/** The name of the bonus e.g. STAT or COMBAT */
	private String  bonusName            = Constants.EMPTY_STRING;
	/** The type of the bonus e.g. Enhancement or Dodge */
	private String  bonusType            = Constants.EMPTY_STRING;
	private String  varPart              = Constants.EMPTY_STRING;
	private int     pcLevel              = -1;
	private int     typeOfBonus          = Bonus.BONUS_UNDEFINED;
	private String  stringRepresentation = null;
	private boolean addOnceOnly          = false;
	private String tokenSource = null;
	private boolean saveToPCG = true;

	/** An enum for the possible stacking modifiers a bonus can have */
	public enum StackType {
		/** This bonus will follow the normal stacking rules. */
		NORMAL, 
		/** This bonus will always stack regardless of its type. */
		STACK, 
		/** 
		 * This bonus will stack with other bonuses of its own type but not
		 * with bonuses of other types.
		 */
		REPLACE 
	}
	private StackType theStackingFlag = StackType.NORMAL;

	/** %LIST - Replace one value selected into this spot */
	private static final String VALUE_TOKEN_REPLACEMENT = "%LIST"; //$NON-NLS-1$
	/** LIST - Replace all the values selected into this spot */
	private static final String LIST_TOKEN_REPLACEMENT = "LIST"; //$NON-NLS-1$
	
	/**
	 * Sets the Applied flag on the bonus.
	 * 
	 * <p><b>Note</b>: This flag is not used in the bonus object.  Therefore,
	 * what being applied means is up to the setter and getter of the flag.
	 * 
	 * <p>TODO - This method does not belong here.
	 * @param pc TODO
	 * @param aBool <tt>true</tt> to mark this bonus as &quot;applied&quot;
	 */
	public void setApplied(PlayerCharacter pc, final boolean aBool)
	{
		pc.setAssoc(this, AssociationKey.IS_APPLIED, aBool);
	}

	/**
	 * Returns the state of the Applied flag.
	 * @param pc TODO
	 * 
	 * @return <tt>true</tt> if the applied flag is set.
	 * 
	 * @see #setApplied(PlayerCharacter, boolean)
	 */
	public boolean isApplied(PlayerCharacter pc)
	{
		Boolean applied = pc.getAssoc(this, AssociationKey.IS_APPLIED);
		return applied == null ? false : applied;
	}

	/**
	 * Get Bonus Info
	 * @return Bonus info
	 */
	public String getBonusInfo()
	{
		final StringBuffer sb = new StringBuffer(50);

		if (bonusInfo.size() > 0)
		{
			for (int i = 0; i < bonusInfo.size(); ++i)
			{
				sb.append(i == 0 ? Constants.EMPTY_STRING : Constants.COMMA);
				sb.append(unparseToken(bonusInfo.get(i)));
			}
		}
		else
		{
			sb.append("|ERROR"); //$NON-NLS-1$
		}

		return sb.toString().toUpperCase();
	}

	/**
	 * Return a list of the unparsed (converted back to strings) 
	 * bonus info entries.
	 * @return The unparsed bonus info list
	 */
	public List<String> getUnparsedBonusInfoList()
	{
		List<String> list = new ArrayList<String>();
		for (Object info : bonusInfo)
		{
			list.add(unparseToken(info));
		}
		return list;
	}
			
	/**
	 * get Bonus Info List
	 * @return Bonus Info List
	 */
	public List<?> getBonusInfoList()
	{
		return bonusInfo;
	}

	/**
	 * Get Bonus Name
	 * @return bonus name
	 */
	public String getBonusName()
	{
		return bonusName;
	}

	/**
	 * Set Creator Object
	 * @param anObj
	 */
	public void setCreatorObject(final Object anObj)
	{
		creatorObj = anObj;
	}

	/**
	 * Get Creator Object
	 * @return creator object
	 */
	public Object getCreatorObject()
	{
		return creatorObj;
	}

	/**
	 * Get depends on given a key
	 * @param aString
	 * @return true if it depends on
	 */
	public boolean getDependsOn(final String aString)
	{
		return dependMap.containsKey(aString);
	}

	/**
	 * Get depends on given a set of keys
	 * @param aList List of bonus keys
	 * @return true if it any of the keys are depended on
	 */
	public boolean getDependsOn(final List<String> aList)
	{
		for (String key : aList)
		{
			if (dependMap.containsKey(key))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Report on the dependencies of the bonus.
	 * @return String the dependancies
	 */
	public String listDependsMap()
	{
		StringBuffer buff = new StringBuffer("[");
		for (String key : dependMap.keySet())
		{
			if (buff.length()> 1)
			{
				buff.append(", ");
			}
			buff.append(key);
		}
		buff.append("]");
		return buff.toString();
	}

	/**
	 * Returns a String which can be used to display in the GUI
	 * @return name
	 */
	public String getName()
	{
		final StringBuffer buffer = new StringBuffer();

		if (creatorObj instanceof PlayerCharacter)
		{
			buffer.append(((PlayerCharacter) creatorObj).getName());
		}
		else if (creatorObj instanceof PObject)
		{
			buffer.append(creatorObj.toString());
		}
		else
		{
			buffer.append("NONE");
		}

		buffer.append(" [");

		if (targetObj instanceof PlayerCharacter)
		{
			buffer.append("PC");
		}
		else if (targetObj instanceof Equipment)
		{
			buffer.append(((Equipment) targetObj).getName());
		}
		else
		{
			buffer.append("NONE");
		}

		buffer.append(']');

		return buffer.toString();
	}

	/**
	 * Set the PC Level
	 * @param anInt
	 */
	public void setPCLevel(final int anInt)
	{
		pcLevel = anInt;
	}

	/**
	 * Get the PCLevel
	 * @return pcLevel
	 */
	public int getPCLevel()
	{
		return pcLevel;
	}

	/**
	 * Checks if this bonus is a &quot;Temporary&quot; bonus.
	 * 
	 * <p>Temporary bonuses are applied and removed from the TempBonuses tab
	 * in the application.
	 * 
	 * TODO - This should be set as a flag on the bonus.
	 * 
	 * @return <tt>true</tt> if this is a temporary bonus.
	 */
	public boolean isTempBonus()
	{
		if ( !hasPrerequisites() )
		{
			return false;
		}
		
		// TODO - This should be handled better
		for ( final Prerequisite prereq : getPrerequisiteList() )
		{
			if ( prereq.getKind() == null )
			{
				continue;
			}
			if ( prereq.getKind().equalsIgnoreCase(Prerequisite.APPLY_KIND) )
			{
				return true;
			}
		}
		return false;
	}
	
	/** An enum for the target of a temp bonus */
	public enum TempBonusTarget {
		/** This bonus applies only to if the PC has the owner of this bonus */
		PC,
		/** Any PC can apply this bonus */
		ANYPC
	}
	
	/**
	 * Tests if this bonus' target is the same as the passed in one.
	 * 
	 * @param aTarget A TempBonusTarget to test for.
	 * 
	 * @return <tt>true</tt> if this bonus has that target.
	 * 
	 * <p><b>TODO</b> - This should be set as a flag on bonus creation.
	 */
	public boolean isTempBonusTarget( final TempBonusTarget aTarget )
	{
		if ( !isTempBonus() || !hasPrerequisites() )
		{
			return false;
		}
		
		for ( final Prerequisite prereq : getPrerequisiteList() )
		{
			if ( prereq.getOperand().equalsIgnoreCase(aTarget.toString()) )
			{
				return true;
			}
			for ( final Prerequisite premult : prereq.getPrerequisites() )
			{
				if ( premult.getOperand().equalsIgnoreCase(aTarget.toString()) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Set Target Object
	 * @param anObj
	 */
	public void setTargetObject(final Object anObj)
	{
		targetObj = anObj;
	}

	/**
	 * Get target object
	 * @return target object
	 */
	public Object getTargetObject()
	{
		return targetObj;
	}

	////////////////////////////////////////////////
	//        Public Accessors and Mutators       //
	////////////////////////////////////////////////

	/**
	 * @return type of Bonus
	 */
	public String getTypeOfBonus()
	{
		return Bonus.getBonusNameFromType(typeOfBonus);
	}

	/**
	 * Get the type of bonus as an int
	 * @return type of bouns as int
	 */
	public int getTypeOfBonusAsInt()
	{
		return typeOfBonus;
	}

	/**
	 * Get the bonus type
	 * @return the bonus type
	 */
	public String getTypeString()
	{
		return bonusType;
	}

	/**
	 * Set value
	 * @param bValue
	 */
	public void setValue(final String bValue)
	{
		bonusFormula = FormulaFactory.getFormulaFor(bValue);
		if (!bonusFormula.isStatic())
		{
			buildDependMap(bValue.toUpperCase());
		}
	}

	/**
	 * Get the bonus value
	 * @return the value
	 */
	public String getValue()
	{
		return bonusFormula.toString();
	}

	/**
	 * Get the bonus value as a double
	 * @param string 
	 * @return bonus value as a double
	 */
	public Number resolve(PlayerCharacter pc, String string)
	{
		return bonusFormula.resolve(pc, string);
	}

	/**
	 * is value static
	 * @return true if value is static
	 */
	public boolean isValueStatic()
	{
		return bonusFormula.isStatic();
	}

	/**
	 * Set the variable
	 * @param aString
	 */
	public void setVariable(final String aString)
	{
		varPart = aString.toUpperCase();
	}

	/**
	 * Get the variable
	 * @return the variable
	 */
	public String getVariable()
	{
		return varPart;
	}

	/**
	 * Has bonus type string
	 * @return true if bonus type string exists
	 */
	public boolean hasTypeString()
	{
		return bonusType.length() > 0;
	}

	/**
	 * has variable
	 * @return true if bonus has variable
	 */
	public boolean hasVariable()
	{
		return varPart.length() > 0;
	}

	/**
	 * Retrieve the persistent text for this bonus. This text
	 * when reparsed will recreate the bonus. PCC Text is used
	 * as a name here as this output could also be used by the
	 * LST editors.
	 * @return The text to be saved for example in a character.
	 */
	public String getPCCText()
	{
		if (stringRepresentation == null)
		{
			final StringBuffer sb = new StringBuffer(50);
	
			if (pcLevel >= 0)
			{
				sb.append(pcLevel).append('|');
			}
	
			sb.append(getTypeOfBonus());
			if (varPart != null && varPart.length() > 0)
			{
				sb.append(varPart);
			}
	
			if (bonusInfo.size() > 0)
			{
				for (int i = 0; i < bonusInfo.size(); ++i)
				{
					sb.append(i == 0 ? '|' : ',').append(
						unparseToken(bonusInfo.get(i)));
				}
			}
			else
			{
				sb.append("|ERROR");
			}
	
			sb.append('|').append(bonusFormula.toString());

			if (bonusType.length() != 0)
			{
				sb.append("|TYPE=").append(bonusType);
			}
			
			// And put the prereqs at the end of the string.
			PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			try
			{
				String prerequisiteString = prereqWriter
						.getPrerequisiteString(getPrerequisiteList());
				if (prerequisiteString != null)
				{
					sb.append(Constants.PIPE);
					sb.append(prerequisiteString);
				}
			}
			catch (PersistenceLayerException e)
			{
				Logging.errorPrint("Error writing Prerequisite: " + e);
			}
	
			stringRepresentation = sb.toString();
		}
		return stringRepresentation;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString()
	{
		return getPCCText();
	}


	/**
	 * Build and return a description of this bonus for the supplied
	 * character. This can either be in long form
	 * '+2[skill TUMBLE gteq 5|TYPE=SYNERGY.STACK]' or in short form
	 * '+2[TUMBLE]'. If the value of the bonus is a formula, it will
	 * be evaluated for the supplied character.
	 *
	 * @param shortForm True if the abbreviated form should be used.
	 * @param aPC The character associated with this skill bonus.
	 * @return A description of the bonus.
	 */
	public String getDescription(final boolean shortForm, final PlayerCharacter aPC)
	{
		final StringBuilder sb = new StringBuilder(50);

		sb.append(Delta.toString(bonusFormula.resolve(aPC, "").floatValue()));

		return sb.append(getBonusContext(shortForm)).toString();
	}

	public String getBonusContext(final boolean shortForm)
	{
		final StringBuilder sb = new StringBuilder(50);
		
		boolean bEmpty = true;
		sb.append('[');
		if (hasPrerequisites()) {
			for (Prerequisite p : getPrerequisiteList()) {
				if (!bEmpty)
				{
					sb.append('|');
				}
				sb.append(p.getDescription(shortForm));
				bEmpty = false;
			}
		}

		if (bonusType.length() != 0)
		{
			if (!shortForm)
			{
				if (!bEmpty)
				{
					sb.append('|');
				}
				sb.append("TYPE=");
				bEmpty = false;
			}
			if (!shortForm || sb.charAt(sb.length()-1) == '[')
			{
				sb.append(bonusType);
				bEmpty = false;
			}
		}

		//
		// If there is nothing shown in between the [], then show the Bonus's type
		//
		if (bEmpty)
		{
			sb.append(String.valueOf(getCreatorObject()));
		}
		sb.append(']');

		return sb.toString();
	}

	protected void setBonusName(final String aName)
	{
		bonusName = aName;
	}

	protected void setTypeOfBonus(final int type)
	{
		typeOfBonus = type;
	}

	protected void addBonusInfo(final Object obj)
	{
		bonusInfo.add(obj);
	}

	protected void replaceBonusInfo(final Object oldObj, final Object newObj)
	{
		for (int i = 0; i < bonusInfo.size(); ++i)
		{
			final Object curObj = bonusInfo.get(i);
			if (curObj == oldObj)
			{
				bonusInfo.set(i, newObj);
				break;
			}
		}
	}

	protected boolean addType(final String typeString)
	{
		if (bonusType.length() == 0)
		{
			bonusType = typeString.toUpperCase();

			return true;
		}

		return false;
	}

	/**
	 * Sets the stacking flag for this bonus.
	 * 
	 * @param aFlag A <tt>StackType</tt> to set.
	 */
	public void setStackingFlag( final StackType aFlag )
	{
		theStackingFlag = aFlag;
	}
	
	/**
	 * Gets the stacking flag for this bonus.
	 * 
	 * @return A <tt>StackType</tt>.
	 */
	public StackType getStackingFlag()
	{
		return theStackingFlag;
	}
	
	/**
	 * Should this bonus only be added once no matter how many associated 
	 * values are present in the PObject owning this bonus?
	 *  
	 * @return the addOnceOnly
	 */
	public boolean isAddOnceOnly()
	{
		return addOnceOnly;
	}

	/**
	 * Should this bonus only be added once no matter how many associated 
	 * values are present in the PObject owning this bonus?
	 * 
	 * @param addOnceOnly the addOnceOnly to set
	 */
	public void setAddOnceOnly(boolean addOnceOnly)
	{
		this.addOnceOnly = addOnceOnly;
	}

	protected boolean parseToken(final String token)
	{
		System.err.println("Need to override parseToken in " + getClass().getName());
		return false;
	}

	protected String unparseToken(final Object obj)
	{
		System.err.println("Need to override unparseToken in " + getClass().getName());
		return "";
	}

	protected String[] getBonusesHandled()
	{
		System.err.println("Need to override getBonusesHandled " + getClass().getName());
		return new String[]{ "" };
	}

	private void buildDependMap(String aString)
	{
		if (aString.indexOf("SKILLINFO(") >= 0)
		{
			dependMap.put("JEPFORMULA", "1");
		}
		if (aString.indexOf("HP") >= 0)
		{
			dependMap.put("CURRENTMAX", "1");
		}

		// First wack out all the () pairs to find variable names
		while (aString.lastIndexOf('(') >= 0)
		{
			final int x = CoreUtility.innerMostStringStart(aString);
			final int y = CoreUtility.innerMostStringEnd(aString);

			if (y < x)
			{
				return;
			}

			final String bString = aString.substring(x + 1, y);
			buildDependMap(bString);
			aString = new StringBuffer().append(aString.substring(0, x))
					.append(aString.substring(y + 1)).toString();
		}

		if (aString.indexOf("(") >= 0 || aString.indexOf(")") >= 0 ||
				aString.indexOf("%") >= 0)
		{
			return;
		}

		// We now have the substring we want to work on
		final StringTokenizer cTok = new StringTokenizer(aString, ".,");

		while (cTok.hasMoreTokens())
		{
			final String controlString = cTok.nextToken();

			// skip flow control tags
			if ("IF".equals(controlString) || "THEN".equals(controlString) || "ELSE"
					.equals(controlString)
				|| "GT".equals(controlString) || "GTEQ".equals(controlString) || "EQ"
					.equals(controlString)
				|| "LTEQ".equals(controlString) || "LT".equals(controlString))
			{
				continue;
			}

			// Now remove math strings: + - / *
			// and comparison strings: > = <
			// remember, a StringTokenizer will tokenize
			// on any of the found delimiters
			final StringTokenizer mTok = new StringTokenizer(controlString, "+-/*>=<\"");

			while (mTok.hasMoreTokens())
			{
				String newString = mTok.nextToken();
				String testString = newString;
				boolean found = false;

				// now Check for MIN or MAX
				while (!found)
				{
					if (newString.indexOf("MAX") >= 0)
					{
						testString = newString.substring(0, newString.indexOf("MAX"));
						newString = newString.substring(newString.indexOf("MAX") + 3);
					}
					else if (newString.indexOf("MIN") >= 0)
					{
						testString = newString.substring(0, newString.indexOf("MIN"));
						newString = newString.substring(newString.indexOf("MIN") + 3);
					}
					else
					{
						found = true;
					}

					// check to see if it's a number
					try
					{
						Float.parseFloat(testString);
					}
					catch (NumberFormatException e)
					{
						// It's a Variable!
						if (testString.length() > 0)
						{
							if (testString.startsWith("MOVE[")) {
								testString = new StringBuffer().append("TYPE.")
										.append(testString.substring(5,
												testString.length() - 1))
										.toString();
							}
							dependMap.put(testString, "1");
						}
					}
				}
			}
		}
	}
	
    /**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public BonusObj clone() throws CloneNotSupportedException
	{
		final BonusObj bonusObj = (BonusObj)super.clone();

		bonusObj.bonusInfo = new ArrayList<Object>(bonusInfo);

		bonusObj.dependMap = new HashMap<String, String>();
		bonusObj.setValue( bonusFormula.toString() );

		// we want to keep the same references to these objects
		// creatorObj
		// targetObj

		// These objects are immutable and do not need explicit cloning
		// bonusName
		// bonusType
		// choiceString
		// varPart
		// isApplied
		// valueIsStatic
		// pcLevel
		// typeOfBonus
		return bonusObj;
	}


	/**
	 * This method will expand the given token in the "value" of this
	 * BonusObj, it will also expand the token in any Prerequisites
	 * that this bonusObj may have
	 * @param token The string representing the token to be replaces (i.e. "%CHOICE")
	 * @param tokenValue The String representing the new value to be used (i.e. "+2")
	 */
	public void expandToken(final String token, final String tokenValue)
	{
		final String value = bonusFormula.toString();
		setValue(value.replaceAll(Pattern.quote(token), tokenValue));

		if ( hasPrerequisites() )
		{
			for ( final Prerequisite prereq : getPrerequisiteList() )
			{
				prereq.expandToken(token, tokenValue);
			}
		}
	}

	private static final String VALUE_TOKEN_PATTERN = Pattern.quote(VALUE_TOKEN_REPLACEMENT);
	private static final String VAR_TOKEN_REPLACEMENT = "%VAR"; //$NON-NLS-1$
	private static final String VAR_TOKEN_PATTERN = Pattern.quote(VAR_TOKEN_REPLACEMENT);
	
	private static final FixedStringList NO_ASSOC = new FixedStringList("");

	private static final List<FixedStringList> NO_ASSOC_LIST = Collections
			.singletonList(NO_ASSOC);
	
	/**
	 * TODO - This method should be changed to not return a string.
	 * <p>
	 * This method builds a string of the form:
	 * <code>BONUSNAME.BONUSTYPE:TYPEOFBONUS </code>
	 * 
	 * @param anObj The bonus owner.
	 * 
	 * @return List of bonus strings
	 */
	public List<BonusPair> getStringListFromBonus(PlayerCharacter pc)
	{
		List<BonusPair> bonusList = new ArrayList<BonusPair>();

		List<FixedStringList> associatedList;
		PObject anObj = null;
		if (creatorObj instanceof PObject)
 		{
			anObj = (PObject) creatorObj;
			associatedList = pc.getDetailedAssociations(anObj);
			if (associatedList == null || associatedList.isEmpty())
 			{
				associatedList = NO_ASSOC_LIST;
			}
		}
		else
		{
			associatedList = NO_ASSOC_LIST;
		}

		// Must use getBonusName because it contains the unaltered bonusType
		String name = getBonusName();
		String[] infoArray = getBonusInfo().split(",");
		String thisType = getTypeString();

		if (addOnceOnly)
		{
			String thisName = name;
			Formula newFormula;
			if (bonusFormula.isStatic())
			{
				newFormula = bonusFormula;
			}
			else
			{
				newFormula = FormulaFactory.getFormulaFor(bonusFormula.toString());
			}
			for (String thisInfo : infoArray)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(thisName).append('.').append(thisInfo);
				if (hasTypeString())
				{
					sb.append(':').append(thisType);
				}
				bonusList.add(new BonusPair(sb.toString(), newFormula));
			}
		}
		else
		{
			for (FixedStringList assoc : associatedList)
			{
				StringBuilder asb = new StringBuilder();
				int size = assoc.size();
				if (size == 1)
				{
					asb.append(assoc.get(0));
				}
				else
				{
					asb.append(size).append(':');
					int loc = asb.length();
					int count = 0;
					for (String s : assoc)
					{
						if (s != null)
						{
							count++;
							asb.append(':').append(s);
						}
					}
					asb.insert(loc, count);
				}
				String assocString = asb.toString();
				
				String thisName;
				if (name.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
				{
					thisName =
							name.replaceAll(VALUE_TOKEN_PATTERN, assocString);
				}
				else
				{
					thisName = name;
				}
				List<String> infoList = new ArrayList<String>(4);
				for (String info : infoArray)
				{
					if (info.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
					{
						for (String expInfo : assoc)
						{
							infoList.add(info.replaceAll(VALUE_TOKEN_PATTERN,
								expInfo));
						}
					}
					else if (info.indexOf(VAR_TOKEN_REPLACEMENT) >= 0)
					{
						infoList.add(name.replaceAll(VAR_TOKEN_PATTERN, assocString));
					}
					else if (info.equals(LIST_TOKEN_REPLACEMENT))
					{
						infoList.add(assocString);
					}
					else
					{
						infoList.add(info);
					}
				}
				Formula newFormula;
				if (bonusFormula.isStatic())
				{
					newFormula = bonusFormula;
				}
				else
				{
					String value = bonusFormula.toString();

					// A %LIST substitution also needs to be done in the val section
					int listIndex = value.indexOf(VALUE_TOKEN_REPLACEMENT);
					String thisValue = value;
					if (listIndex >= 0)
					{
						thisValue =
								value.replaceAll(VALUE_TOKEN_PATTERN, assocString);
					}
					newFormula = FormulaFactory.getFormulaFor(thisValue);
				}
				for (String thisInfo : infoList)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(thisName).append('.').append(thisInfo);
					if (hasTypeString())
					{
						sb.append(':').append(thisType);
					}
					bonusList.add(new BonusPair(sb.toString(), newFormula));
				}
			}
		}
		return bonusList;
	}

	public class BonusPair
	{
		private Formula formula;
		public String bonusKey;

		public BonusPair(String key, Formula f)
		{
			bonusKey = key;
			formula = f;
		}

		public Number resolve(PlayerCharacter aPC)
		{
			String source;
			if (creatorObj instanceof PObject)
			{
				source = ((PObject) creatorObj).getQualifiedKey();
			}
			else
			{
				source = Constants.EMPTY_STRING;
			}
			return formula.resolve(aPC, source);
		}
	}

	public void setTokenSource(String tokenName)
	{
		tokenSource = tokenName;
	}
	
	public String getTokenSource()
	{
		return tokenSource;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof BonusObj))
		{
			return false;
		}
		BonusObj other = (BonusObj) obj;
		return equalsPrereqObject(other)
				&& bonusFormula.equals(other.bonusFormula)
				&& bonusName.equals(other.bonusName)
				&& bonusType.equals(other.bonusType)
				&& pcLevel == other.pcLevel && addOnceOnly == other.addOnceOnly
				&& theStackingFlag.equals(other.theStackingFlag)
				&& bonusInfo.equals(other.bonusInfo);
	}

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public void setSaveToPCG(boolean b)
	{
		saveToPCG = b;
	}
	
	public boolean saveToPCG()
	{
		return saveToPCG;
	}

	/*
	 * This makes an editor a bit more difficult, but since CHOOSE is an early
	 * target of 5.17, this probably isn't a big deal.
	 */
	private String originalString;

	public void putOriginalString(String bonusString)
	{
		originalString = bonusString;
	}
	
	public String getLSTformat()
	{
		return originalString;
	}
	
}
