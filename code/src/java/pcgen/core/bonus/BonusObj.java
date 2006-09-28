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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.PrereqObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Delta;

/**
 * <code>BonusObj</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 **/
public abstract class BonusObj extends PrereqObject implements Serializable, Cloneable
{
	private List<Object>    bonusInfo       = new ArrayList<Object>();
	private Map<String, String>     dependMap  = new HashMap<String, String>();
	private Object  bonusValue;
	private Object  creatorObj;
	private Object  targetObj;
	/** The name of the bonus e.g. STAT or COMBAT */
	private String  bonusName            = Constants.EMPTY_STRING;
	/** The type of the bonus e.g. Enhancement or Dodge */
	private String  bonusType            = Constants.EMPTY_STRING;
	private String  varPart              = Constants.EMPTY_STRING;
	private boolean isApplied;
	private boolean valueIsStatic        = true;
	private int     pcLevel              = -1;
	private int     typeOfBonus          = Bonus.BONUS_UNDEFINED;
	private String  stringRepresentation = null;

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
	};
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
	 * 
	 * @param aBool <tt>true</tt> to mark this bonus as &quot;applied&quot;
	 */
	public void setApplied(final boolean aBool)
	{
		isApplied = aBool;
	}

	/**
	 * Returns the state of the Applied flag.
	 * 
	 * @return <tt>true</tt> if the applied flag is set.
	 * 
	 * @see #setApplied(boolean)
	 */
	public boolean isApplied()
	{
		return isApplied;
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
		if ( !hasPreReqs() )
		{
			return false;
		}
		
		// TODO - This should be handled better
		for ( final Prerequisite prereq : getPreReqList() )
		{
			if ( prereq.getKind() == null )
			{
				continue;
			}
			if ( prereq.getKind().equals(Prerequisite.APPLY_KIND) )
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
		if ( !isTempBonus() || !hasPreReqs() )
		{
			return false;
		}
		
		for ( final Prerequisite prereq : getPreReqList() )
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
		try
		{
			bonusValue = new Integer(bValue);
		}
		catch (NumberFormatException e1)
		{
			try
			{
				bonusValue = new Float(bValue);
			}
			catch (Exception e2)
			{
				bonusValue = bValue.toUpperCase();
				valueIsStatic = false;
				buildDependMap(bValue.toUpperCase());
			}
		}
	}

	/**
	 * Get the bonus value
	 * @return the value
	 */
	public String getValue()
	{
		return bonusValue.toString();
	}

	/**
	 * Get the bonus value as a double
	 * @return bonus value as a double
	 */
	public double getValueAsdouble()
	{
		return Double.parseDouble(bonusValue.toString());
	}


	/**
	 * Calculate the value fo the bonus object for the supplied
	 * character. If the value fo the bonus is a formula, this
	 * formula will be evaluated for the character. Other the
	 * static value will be returned.
	 *
	 * @param aPC The character the bonus is to be evaluated for.
	 * @return The value of the bonus.
	 */
	public double getCalculatedValue(final PlayerCharacter aPC)
	{
		double value = 0;
		if (bonusValue != null)
		{
			if (bonusValue instanceof Integer)
			{
				value = ((Integer)bonusValue).longValue();
			}
			else if (bonusValue instanceof Float)
			{
				value = ((Float)bonusValue).floatValue();
			}
			else if (bonusValue instanceof String && creatorObj instanceof PObject)
			{
				value = ((PObject) creatorObj).calcBonusFrom(this, aPC, "", aPC);
			}
			else
			{
				value = getValueAsdouble();
			}
		}

		return value;
	}

	/**
	 * is value static
	 * @return true if value is static
	 */
	public boolean isValueStatic()
	{
		return valueIsStatic;
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
	@Override
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
	
			if (bonusValue != null)
			{
				sb.append('|').append(bonusValue.toString());
			}
	
			sb.append(super.getPCCText());
	
			if (bonusType.length() != 0)
			{
				sb.append("|TYPE=").append(bonusType);
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
		final StringBuffer sb = new StringBuffer(50);

		if (bonusValue != null)
		{
			if (bonusValue instanceof Integer)
			{
				sb.append(Delta.toString((Integer) bonusValue));
			}
			else if (bonusValue instanceof Float)
			{
				sb.append(Delta.toString((Float) bonusValue));
			}
			else if (bonusValue instanceof String && creatorObj instanceof PObject)
			{
				sb.append(Delta.toString((float) ((PObject) creatorObj)
					.calcBonusFrom(this, aPC, "", aPC)));
			}
			else
			{
				sb.append(bonusValue.toString());
			}
		}
		else
		{
			sb.append("+0");
		}

		boolean bEmpty = true;
		sb.append('[');
		if (getPreReqList() != null)
		{
			for (int i = 0; i < getPreReqList().size(); ++i)
			{
				if (i > 0)
				{
					sb.append('|');
				}
				sb.append(this.getPreReq(i).getDescription(shortForm));
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
			sb.append(getTypeOfBonus());
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
	public Object clone() throws CloneNotSupportedException
	{
		final BonusObj bonusObj = (BonusObj)super.clone();

		bonusObj.bonusInfo = new ArrayList<Object>(bonusInfo);

		bonusObj.dependMap = new HashMap<String, String>();
		if (bonusValue != null)
		{
			bonusObj.setValue( bonusValue.toString() );
		}

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
		final String value = getValue();
		setValue( CoreUtility.replaceAll(value, token, tokenValue));

		if ( hasPreReqs() )
		{
			for ( final Prerequisite prereq : getPreReqList() )
			{
				prereq.expandToken(token, tokenValue);
			}
		}
	}

//	// TODO - Why do we need to do this processing all the time.
//	// It should be possible to parse almost all this stuff out once.
//	public Map<String, List<TypedBonus>> getTypedBonuses(final PlayerCharacter aPC)
//	{
//		final Map<String, List<TypedBonus>> ret = new HashMap<String, List<TypedBonus>>();
//		
//		// TODO - Can any other object type be a creator?  The code elsewhere
//		// seems to assume not, but then why isn't creator object a PObject?
//		if ( getCreatorObject() == null || ! (getCreatorObject() instanceof PObject) )
//		{
//			return Collections.emptyMap();
//		}
//		final PObject pObj = (PObject)getCreatorObject(); 
//
//		int listindex = 0; // Counter for number of replacements we have done
//
//		// We may have a bonus something like
//		// BONUS:SKILL|Hide,Move Silently|2
//		// This needs to become two independant bonuses for processing purposes.
//		final String bInfoString = getBonusInfo();
//		final StringTokenizer aTok = new StringTokenizer(bInfoString, ",");
//
//		while (aTok.hasMoreTokens())
//		{
//			String info = aTok.nextToken();
//
//			final TypedBonus.StackType stackType; 
//			if ( info.endsWith(".STACK") || info.endsWith(".REPLACE") )
//			{
//				info = info.substring( 0, info.lastIndexOf('.') );
//				if ( info.endsWith(".STACK") )
//				{
//					stackType = TypedBonus.StackType.STACK;
//				}
//				else
//				{
//					stackType = TypedBonus.StackType.REPLACE;
//				}
//			}
//			else
//			{
//				stackType = TypedBonus.StackType.NORMAL;
//			}
//
//			if (pObj.getAssociatedCount() > 0)
//			{
//				final String name = getBonusName();
//				if (name.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
//				{
//					// BONUS:%LIST|Foo|1
//					for (int i = 0; i < pObj.getAssociatedCount(); ++i)
//					{
//						final StringBuffer ab = new StringBuffer();
//						final String tName = CoreUtility.replaceFirst(name, VALUE_TOKEN_REPLACEMENT, pObj.getAssociated(i));
//						ab.append(tName).append('.');
//						ab.append(info);
//
//						final String key = ab.toString().toUpperCase();
//						final TypedBonus typedBonus = new TypedBonus(getCalculatedValue(aPC), key, stackType);
//						List<TypedBonus> bonusList = ret.get(key);
//						if ( bonusList == null )
//						{
//							bonusList = new ArrayList<TypedBonus>();
//							ret.put(key, bonusList);
//						}
//						bonusList.add(typedBonus);
//					}
//				}
//				else if (info.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
//				{
//					// BONUS:FOO|%LIST|1
//					for (int i = 0; i < pObj.getAssociatedCount(true); ++i)
//					{
//						final StringBuffer ab = new StringBuffer();
//						final String tName = CoreUtility.replaceFirst(info, VALUE_TOKEN_REPLACEMENT, pObj.getAssociated(i, true));
//						ab.append(getTypeOfBonus()).append('.');
//						ab.append(tName);
//
//						final String key = ab.toString().toUpperCase();
//						final TypedBonus typedBonus = new TypedBonus(getCalculatedValue(aPC), key, stackType);
//						List<TypedBonus> bonusList = ret.get(key);
//						if ( bonusList == null )
//						{
//							bonusList = new ArrayList<TypedBonus>();
//							ret.put(key, bonusList);
//						}
//						bonusList.add(typedBonus);
//					}
//				}
//				else
//				{
//					final int cnt = pObj.getAssociatedCount();
//
//					if (cnt <= listindex && info.equals(LIST_TOKEN_REPLACEMENT))
//					{
//						continue;
//					}
//
//					while (true)
//					{
//						final StringBuffer ab = new StringBuffer();
//						ab.append(getTypeOfBonus()).append('.');
//						if (info.equals(LIST_TOKEN_REPLACEMENT))
//						{
//							ab.append(pObj.getAssociated(listindex));
//						}
//						else
//						{
//							ab.append(info);
//						}
//
//						listindex++;
//
//						final String key = ab.toString().toUpperCase();
//						final TypedBonus typedBonus = new TypedBonus(getCalculatedValue(aPC), key, stackType);
//						List<TypedBonus> bonusList = ret.get(key);
//						if ( bonusList == null )
//						{
//							bonusList = new ArrayList<TypedBonus>();
//							ret.put(key, bonusList);
//						}
//						bonusList.add(typedBonus);
//						
//						// If we have processed all of the entries, or if this object
//						// has multiple bonuses, don't add any more copies.
//						if (aTok.countTokens() > 0
//							|| listindex >= cnt
//							|| pObj.getBonusList().size() > 1)
//						{
//							break;
//						}
//					}
//				}
//			}
//			else if (hasVariable())
//			{
//				// Some bonuses have a variable as part
//				// of their name, such as
//				//  BONUS:WEAPONPROF=AbcXyz|TOHIT|3
//				// so parse out the correct value
//				final StringBuffer ab = new StringBuffer();
//				ab.append(getTypeOfBonus());
//				ab.append(getVariable()).append('.');
//				ab.append(info);
//
//				final String key = ab.toString().toUpperCase();
//				final TypedBonus typedBonus = new TypedBonus(getCalculatedValue(aPC), key, stackType);
//				List<TypedBonus> bonusList = ret.get(key);
//				if ( bonusList == null )
//				{
//					bonusList = new ArrayList<TypedBonus>();
//					ret.put(key, bonusList);
//				}
//				bonusList.add(typedBonus);
//			}
//			else
//			{
//				final StringBuffer ab = new StringBuffer();
//				ab.append(getTypeOfBonus()).append('.');
//				ab.append(info);
//
//				final String key = ab.toString().toUpperCase();
//				final TypedBonus typedBonus = new TypedBonus(getCalculatedValue(aPC), key, stackType);
//				List<TypedBonus> bonusList = ret.get(key);
//				if ( bonusList == null )
//				{
//					bonusList = new ArrayList<TypedBonus>();
//					ret.put(key, bonusList);
//				}
//				bonusList.add(typedBonus);
//			}
//		}
//		return ret;
//	}

//	public List<BonusObj> getExpandedBonuses()
//	{
//		final List<BonusObj> ret = new ArrayList<BonusObj>();
//		
//		String bInfoString = getBonusInfo();
//		BonusObj workingCopy = null;
//		if ( this.creatorObj != null )
//		{
//			final PObject pobj = (PObject)creatorObj;
//			if ( pobj.getAssociatedCount() > 0 )
//			{
//				// 1) has %LIST in the bonusName
//				if (bonusName.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
//				{
//					try
//					{
//						workingCopy = (BonusObj)clone();
//					}
//					catch (CloneNotSupportedException e)
//					{
//						// This should never happen.
//					}
//					// Not sure how many choices could be valid here but...
//					for (int i = 0; i < pobj.getAssociatedCount(); ++i)
//					{
//						workingCopy.bonusName = workingCopy.getBonusName().replaceFirst(VALUE_TOKEN_REPLACEMENT, pobj.getAssociated(i));
//					}
//				}
//				// 2) has %LIST in the bonusInfo
//				else if (bInfoString.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
//				{
//					try
//					{
//						workingCopy = (BonusObj)clone();
//					}
//					catch (CloneNotSupportedException e)
//					{
//						// This should never happen.
//					}
//					final int numReplacements = pobj.getAssociatedCount(true);
//					int replaced = 0;
//					for ( Object info : bonusInfo )
//					{
//						// TODO - This is horrid
//						if ( info instanceof String )
//						{
//							while ( replaced < numReplacements )
//							{
//								info = ((String)info).replaceFirst(VALUE_TOKEN_REPLACEMENT, pobj.getAssociated(replaced++, true));
//							}
//						}
//					}
//					bInfoString = workingCopy.getBonusInfo();
//				}
//				// 3) has no %LIST at all
//				else
//				{
//					workingCopy = this;
//				}
//				if ( bInfoString.indexOf(LIST_TOKEN_REPLACEMENT) >= 0 )
//				{
//					final List<String> strList = new ArrayList<String>(pobj.getAssociatedCount());
//					for ( int i = 0; i < pobj.getAssociatedCount(); i++ )
//					{
//						strList.add(pobj.getAssociated(i));
//					}
//					bInfoString = bInfoString.replaceFirst(LIST_TOKEN_REPLACEMENT, CoreUtility.commaDelimit(strList));
//				}
//			}
//		}
//		// TODO - 
//		final StringTokenizer aTok = new StringTokenizer(bInfoString, Constants.COMMA);
//
//		while (aTok.hasMoreTokens())
//		{
//			final String info = aTok.nextToken();
//			
//		}
//	}
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
	public List<String> getStringListFromBonus(final PObject anObj)
	{
		final List<String> aList = new ArrayList<String>();

		final String bInfoString = getBonusInfo();
		final StringTokenizer aTok = new StringTokenizer(bInfoString, ",");
		int listindex = 0;

		while (aTok.hasMoreTokens())
		{
			final String info = aTok.nextToken();

			// Some BONUS statements use %LIST to represent
			// a possible list or selection made
			// Need to deconstruct for proper bonus stacking
			if (anObj.getAssociatedCount() > 0)
			{
				// There are three forms:
				// 1) has %LIST in the bonusName
				// 2) has %LIST in the bonusInfo
				// 3) has no %LIST at all


				// Must use getBonusName because it
				// contains the unaltered bonusType
				final String name = getBonusName();
				if (name.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
				{
					for (int i = 0; i < anObj.getAssociatedCount(); ++i)
					{
						final StringBuffer ab = new StringBuffer();
						final String tName = CoreUtility.replaceFirst(name, VALUE_TOKEN_REPLACEMENT, anObj.getAssociated(i));
						ab.append(tName).append('.');
						ab.append(info);

						if (hasTypeString())
						{
							ab.append(':').append(getTypeString());
						}

						aList.add(ab.toString().toUpperCase());
					}
				}
				else if (info.indexOf(VALUE_TOKEN_REPLACEMENT) >= 0)
				{
					for (int i = 0; i < anObj.getAssociatedCount(true); ++i)
					{
						final StringBuffer ab = new StringBuffer();
						final String tName = CoreUtility.replaceFirst(info, VALUE_TOKEN_REPLACEMENT, anObj.getAssociated(i, true));
						ab.append(getTypeOfBonus()).append('.');
						ab.append(tName);

						if (hasTypeString())
						{
							ab.append(':').append(getTypeString());
						}
						aList.add(ab.toString().toUpperCase());
					}
				}
				else
				{
					final int cnt = anObj.getAssociatedCount();

					if (cnt <= listindex && info.equals(LIST_TOKEN_REPLACEMENT))
					{
						continue;
					}

					while (true)
					{
						final StringBuffer ab = new StringBuffer();
						ab.append(getTypeOfBonus()).append('.');
						if (info.equals(LIST_TOKEN_REPLACEMENT))
						{
							ab.append(anObj.getAssociated(listindex));
						}
						else
						{
							ab.append(info);
						}

						if (hasTypeString())
						{
							ab.append(':').append(getTypeString());
						}

						listindex++;

						aList.add(ab.toString().toUpperCase());

						// If we have processed all of the entries, or if this object
						// has multiple bonuses, don't add any more copies.
						if (aTok.countTokens() > 0
							|| listindex >= cnt
							|| anObj.getBonusList().size() > 1)
						{
							break;
						}
					}
				}
			}
			else if (hasVariable())
			{
				// Some bonuses have a variable as part
				// of their name, such as
				//  BONUS:WEAPONPROF=AbcXyz|TOHIT|3
				// so parse out the correct value
				final StringBuffer ab = new StringBuffer();
				ab.append(getTypeOfBonus());
				ab.append(getVariable()).append('.');
				ab.append(info);

				if (hasTypeString())
				{
					ab.append(':').append(getTypeString());
				}

				aList.add(ab.toString().toUpperCase());
			}
			else
			{
				final StringBuffer ab = new StringBuffer();
				ab.append(getTypeOfBonus()).append('.');
				ab.append(info);

				if (hasTypeString())
				{
					ab.append(':').append(getTypeString());
				}

				aList.add(ab.toString().toUpperCase());
			}
		}

		return aList;
	}

}
