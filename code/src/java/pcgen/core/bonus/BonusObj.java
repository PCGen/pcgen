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

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

/**
 * <code>BonusObj</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 **/
public abstract class BonusObj implements Serializable, Cloneable
{
	private List    bonusInfo            = new ArrayList();
	private List    prereqList;
	private Map     dependMap            = new HashMap();
	private Object  bonusValue;
	private Object  creatorObj;
	private Object  targetObj;
	private String  bonusName            = "";
	private String  bonusType            = "";
	private String  varPart              = "";
	private boolean isApplied;
	private boolean valueIsStatic        = true;
	private int     pcLevel              = -1;
	private int     typeOfBonus          = Bonus.BONUS_UNDEFINED;
    private String  stringRepresentation;

	/**
	 * Set Applied
	 * @param aBool
	 */
    public void setApplied(final boolean aBool)
	{
		isApplied = aBool;
	}

    /**
     * isApplied
     * @return True if applied
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
				sb.append(i == 0 ? "" : ",").append(unparseToken(bonusInfo.get(i)));
			}
		}
		else
		{
			sb.append("|ERROR");
		}

		return sb.toString().toUpperCase();
	}

	/**
	 * get Bonus Info List
	 * @return Bonus Info List
	 */
	public List getBonusInfoList()
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
	 * Get the pre req list
	 * @return pre req list
	 */
	public List getPrereqList()
	{
		return prereqList;
	}

	/**
	 * Get a clone of the pre req list
	 * @return a clone of the pre req list
	 * @throws CloneNotSupportedException
	 */
	public List getClonePrereqList() throws CloneNotSupportedException
	{
		final List newList = new ArrayList(prereqList.size());
		for (Iterator iter = prereqList.iterator(); iter.hasNext();) {
			final Prerequisite element = (Prerequisite) iter.next();
			newList.add( element.clone());
		}
		return newList;
	}

	/**
	 * Set the pre req list
	 * @param prereqList
	 */
	public void setPrereqList(final List prereqList)
	{
		this.prereqList = prereqList;
	}

	/**
	 * Get the pre req String
	 * @return pre req String
	 */
	private String getPrereqString()
	{
		final StringWriter writer = new StringWriter();

		if (prereqList != null)
		{
			final PrerequisiteWriter preReqWriter = new PrerequisiteWriter();
			for (int i = 0; i < prereqList.size(); i++)
			{
				final Prerequisite preReq = (Prerequisite) prereqList.get(i);

				try
				{
					if (i != 0)
					{
						writer.write('|');
					}
					preReqWriter.write(writer, preReq);
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint("Caught PersistenceLayerException in BonusObj.", ple);
				}
			}
		}

		return writer.toString().toUpperCase();
	}

	/**
	 * is a pre req given a kind
	 * @param aKind
	 * @return true if a pre req
	 */
	public boolean isPreReqKind(final String aKind)
	{
		if (prereqList != null)
		{
			for (int i = 0; i < prereqList.size(); i++)
			{
				final Prerequisite prereq = (Prerequisite) prereqList.get(i);
				if (prereq == null)
				{
					continue;
				}
				if (prereq.getKind() == null)
				{
					continue;
				}
				if (prereq.getKind().equalsIgnoreCase(aKind))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * is pre req given a key
	 * @param aKey
	 * @return true if a pre req
	 */
	public boolean isPreReqTarget(final String aKey)
	{
		if (prereqList != null)
		{
			for (int i = 0; i < prereqList.size(); i++)
			{
				final Prerequisite prereq = (Prerequisite) prereqList.get(i);
				if (prereq.getOperand().equalsIgnoreCase(aKey))
				{
					return true;
				}
				if (prereq.getPrerequisites().size() > 0)
				{
					final List aList = prereq.getPrerequisites();
					for (Iterator iter = aList.iterator(); iter.hasNext();)
					{
						final Prerequisite element = (Prerequisite) iter.next();
						if (element.getOperand().equalsIgnoreCase(aKey))
						{
							return true;
						}
					}
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
	 * has pre reqs
	 * @return true if it has pre reqs
	 */
	public boolean hasPreReqs()
	{
		return prereqList != null;
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

		if (prereqList != null)
		{
			final StringWriter writer = new StringWriter();
			for (int i = 0; i < prereqList.size(); ++i)
			{
				final Prerequisite prereq = (Prerequisite) prereqList.get(i);
				final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
				writer.write("|");
				try
				{
					prereqWriter.write(writer, prereq);
				}
				catch (PersistenceLayerException e)
				{
					e.printStackTrace();
				}
			}
			sb.append(writer);
		}

		if (bonusType.length() != 0)
		{
			sb.append("|TYPE=").append(bonusType);
		}

		return sb.toString();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString()
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
					sb.append(i == 0 ? '|' : ',').append(unparseToken(bonusInfo.get(i)));
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

			if (prereqList != null)
			{
				sb.append('|');
				sb.append(getPrereqString());
				/*for (int i = 0; i < prereqList.size(); ++i)
				{
					sb.append('|').append(prereqList.get(i));
				}*/
			}

			if (bonusType.length() != 0)
			{
				sb.append("|TYPE=").append(bonusType);
			}

			stringRepresentation = sb.toString();
		}
		return stringRepresentation;
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
		if (prereqList != null)
		{
			for (int i = 0; i < prereqList.size(); ++i)
			{
				if (i > 0)
				{
					sb.append('|');
				}
				sb.append(((Prerequisite)prereqList.get(i)).getDescription(shortForm));
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

	/**
	 * Add the pre req
	 * @param prereq
	 */
	public void addPreReq(final Prerequisite prereq)
	{
		if (prereqList == null)
		{
			prereqList = new ArrayList();
		}

		if (!prereqList.contains(prereq))
		{
			prereqList.add(prereq);
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
			final StringTokenizer mTok = new StringTokenizer(controlString, "+-/*>=<");

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
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		final BonusObj bonusObj = (BonusObj)super.clone();

		bonusObj.bonusInfo = new ArrayList(bonusInfo);

		if (prereqList != null)
		{
			bonusObj.prereqList = new ArrayList();
			for (Iterator iter = prereqList.iterator(); iter.hasNext();)
			{
				final Prerequisite element = (Prerequisite) iter.next();
				bonusObj.prereqList.add( element.clone());
			}
		}
		bonusObj.dependMap = new HashMap();
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

		if (prereqList !=null)
		{
			for (Iterator iter = prereqList.iterator(); iter.hasNext();) {
				final Prerequisite prereq = (Prerequisite) iter.next();
				prereq.expandToken(token, tokenValue);
			}
		}
	}

}
