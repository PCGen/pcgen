/*
 * Copyright 2003 (C) Ross M. Lodge
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
 */
package plugin.initiative;

/**
 * <p>
 * An abstract class used for the "model" classes for the plugins.  Basically
 * provides utility methods for the AttackModel, SkillModel, etc. classes.
 * </p>
 */
public abstract class PObjectModel
{
	/** Constant for decoding incoming object strings */
	protected static final int SEGMENT_POSITION_NAME = 0;

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_SKILL = "skill:";

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_CHECK = "check:";

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_ATTACK = "attack:";

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_SPELL = "spell:";

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_SAVE = "save:";

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_DICE_ROLL = "dice:";

	protected String m_name = null;
	protected String[] outputTokens = null;

	/**
	 * <p>
	 * Constructs a new skill model based on a string. The string should
	 * generally contain output tokens separated by backslashes.
	 * </p>
	 * <p>
	 * The default implementation of this constructor is to split the incoming
	 * string and save the results to outputTokens.  It also assumes the
	 * name of the incoming object is the first token, and sets that value.
	 * </p>
	 *
	 * @param objectString
	 *            The string description of the object.
	 */
	public PObjectModel(String objectString)
	{
		outputTokens = objectString.split("\\\\");
		m_name = getStringValue(outputTokens, SEGMENT_POSITION_NAME);
	}

	/**
	 * <p>Sets the value of name</p>
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	/**
	 * <p>Gets the value of name</p>
	 * @return Returns the name.
	 */
	public String getName()
	{
		return m_name;
	}

	/**
	 * <p>
	 * A factory method that tries to determine what kind of object the string represents
	 * and generate the appropriate subclass.
	 * </p>
	 *
	 * @param objectString
	 *             An appropriate object string, including the type prefix.
	 * @return
	 *             A new instance of a PObjectModel subclass.
	 */
	public static PObjectModel Factory(String objectString)
	{
		PObjectModel returnValue = null;

		if (objectString != null)
		{
			if (objectString.startsWith(TYPE_PREFIX_SKILL))
			{
				returnValue =
						new SkillModel(objectString.substring(TYPE_PREFIX_SKILL
							.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_CHECK))
			{
				returnValue =
						new CheckModel(objectString.substring(TYPE_PREFIX_CHECK
							.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_ATTACK))
			{
				returnValue =
						new AttackModel(objectString
							.substring(TYPE_PREFIX_ATTACK.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_SPELL))
			{
				returnValue =
						new SpellModel(objectString.substring(TYPE_PREFIX_SPELL
							.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_SAVE))
			{
				returnValue =
						new SaveModel(objectString.substring(TYPE_PREFIX_SAVE
							.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_DICE_ROLL))
			{
				returnValue =
						new DiceRollModel(objectString
							.substring(TYPE_PREFIX_DICE_ROLL.length()));
			}
		}

		return returnValue;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * <p>
	 * A save conversion from string to int.  This avoids NumberFormatExceptions,
	 * and also removes pluses from the incoming values.
	 * </p>
	 *
	 * @param value
	 *             String value to interpret.
	 * @return
	 *             The integer conversion of the incoming string
	 */
	protected static int getInt(String value)
	{
		int returnValue = 0;

		try
		{
			if (value.startsWith("+"))
			{
				returnValue = Integer.parseInt(value.substring(1));
			}
			else
			{
				returnValue = Integer.parseInt(value);
			}
		}
		catch (NumberFormatException e)
		{
			//Do Nothing
		}

		return returnValue;
	}

	/**
	 * <p>Provides an index-safe method of retrieving data
	 * from the array of strings generated by parsing the
	 * input weaponString.</p>
	 *
	 * @param values Array of strings
	 * @param index Index to get from array
	 * @return The requested string entry, or ""
	 */
	protected String getStringValue(String[] values, int index)
	{
		String returnValue = "";

		if (values.length > index)
		{
			returnValue = values[index];
		}

		return returnValue;
	}
}
