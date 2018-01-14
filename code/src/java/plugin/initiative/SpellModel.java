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
 * <p>This class models a spell.</p>
 */
public class SpellModel extends PObjectModel
{
	/** Constant for decoding incoming spell strings */
	private static final int SEGMENT_POSITION_DESC = 1;

	/** Constant for decoding incoming spell strings */
	private static final int SEGMENT_POSITION_RANGE = 2;

	/** Constant for decoding incoming spell strings */
	private static final int SEGMENT_POSITION_CASTINGTIME = 3;

	/** Constant for decoding incoming spell strings */
	private static final int SEGMENT_POSITION_SAVEINFO = 4;

	/** Constant for decoding incoming spell strings */
	private static final int SEGMENT_POSITION_DURATION = 5;

	/** Constant for decoding incoming spell strings */
	private static final int SEGMENT_POSITION_TARGET = 6;
	private String m_castingTime;
	private String m_desc;
	private String m_duration;
	private String m_range;
	private String m_saveInfo;
	private String m_target;

	/**
	 * <p>Constructs a new spell model based on a string.  The string should
	 * have the following tokens, in the following order, separated by
	 * backslashes:</p>
	 * <ol>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.NAME|</li>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.DESC|</li>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.RANGE|</li>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.CASTINGTIME|</li>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.SAVEINFO|</li>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.DURATION|</li>
	 * <li>|SPELLMEM.%class.%book.%level.%spell.TARGET|</li>
	 * </ol>
	 *
	 * @param objectString String description of spell
	 */
	public SpellModel(String objectString)
	{
		super(objectString);
		setDesc(getStringValue(outputTokens, SEGMENT_POSITION_DESC));
		setRange(getStringValue(outputTokens, SEGMENT_POSITION_RANGE));
		setCastingTime(getStringValue(outputTokens,
			SEGMENT_POSITION_CASTINGTIME));
		setSaveInfo(getStringValue(outputTokens, SEGMENT_POSITION_SAVEINFO));
		setDuration(getStringValue(outputTokens, SEGMENT_POSITION_DURATION));
		setTarget(getStringValue(outputTokens, SEGMENT_POSITION_TARGET));
	}

	/**
	 * <p>Sets the value of castingTime</p>
	 * @param castingTime The castingTime to set.
	 */
	public void setCastingTime(String castingTime)
	{
		m_castingTime = castingTime;
	}

	/**
	 * <p>Gets the value of castingTime</p>
	 * @return Returns the castingTime.
	 */
	public String getCastingTime()
	{
		return m_castingTime;
	}

	/**
	 * <p>Sets the value of desc</p>
	 * @param desc The desc to set.
	 */
	public void setDesc(String desc)
	{
		m_desc = desc;
	}

	/**
	 * <p>Gets the value of desc</p>
	 * @return Returns the desc.
	 */
	public String getDesc()
	{
		return m_desc;
	}

	/**
	 * <p>Sets the value of duration</p>
	 * @param duration The duration to set.
	 */
	public void setDuration(String duration)
	{
		m_duration = duration;
	}

	/**
	 * <p>Gets the value of duration</p>
	 * @return Returns the duration.
	 */
	public String getDuration()
	{
		return m_duration;
	}

	/**
	 * <p>Sets the value of range</p>
	 * @param range The range to set.
	 */
	public void setRange(String range)
	{
		m_range = range;
	}

	/**
	 * <p>Gets the value of range</p>
	 * @return Returns the range.
	 */
	public String getRange()
	{
		return m_range;
	}

	/**
	 * <p>Sets the value of saveInfo</p>
	 * @param saveInfo The saveInfo to set.
	 */
	public void setSaveInfo(String saveInfo)
	{
		m_saveInfo = saveInfo;
	}

	/**
	 * <p>Gets the value of saveInfo</p>
	 * @return Returns the saveInfo.
	 */
	public String getSaveInfo()
	{
		return m_saveInfo;
	}

	/**
	 * <p>Sets the value of target</p>
	 * @param target The target to set.
	 */
	public void setTarget(String target)
	{
		m_target = target;
	}

	/**
	 * <p>Gets the value of target</p>
	 * @return Returns the target.
	 */
	public String getTarget()
	{
		return m_target;
	}
}
