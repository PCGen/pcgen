/*
 *  gmgen.plugin.dicebag.gui - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 Ross Lodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  DiceBagMessage.java
 *
 *  Created on Oct 22, 2003, 8:41:40 AM
 */
package plugin.dicebag.gui;

/**
 * <p>This class is the message class that will be used to pass messages
 * from the model class to the view classes.  This class defines
 * the different messages that can be passed and declares data fields that can
 * be queried from the message.  The following types of messages are sent:</p>
 *
 * <ul>
 * <li>Model Initialized</li>
 * <li>Dice Bag Added</li>
 * <li>Dice Bag Removed</li>
 * <li>All Dice Bags Removed</li>
 * <li>Dice Bag Saved</li>
 * </ul>
 *
 * @author Ross Lodge
 */
public class DiceBagMessage
{
	/** Constant for message with no type. */
	public static final int NO_TYPE = 0;

	/** Constant for message indicating the model has been initialized. */
	public static final int MODEL_INITIALIZED = 1;

	/** Constant for message indicating a dice bag has been added. */
	public static final int DICE_BAG_ADDED = 2;

	/** Constant for message indicating a dice bag has been removed */
	public static final int DICE_BAG_REMOVED = 4;

	/** Constant for message indicating all dice bages have been removed. */
	public static final int ALL_DICE_BAGS_REMOVED = 8;

	/** Constant for message indicating a dice bag has been saved. */
	public static final int DICE_BAG_SAVED = 64;

	/** Dice bag that the message pertains to. */
	private DiceBagModel m_diceBag = null;

	/** Type of message. */
	private int m_type = NO_TYPE;

	/**
	 * <p>Constructs a message of type</p>
	 *
	 * @param type One of the constants . . .
	 */
	public DiceBagMessage(int type)
	{
		m_type = type;
	}

	/**
	 * <p>Construct a message of type "with bag".</p>
	 *
	 * @param type One of the constants
	 * @param bag Bag this message pertains to
	 */
	public DiceBagMessage(int type, DiceBagModel bag)
	{
		this(type);
		m_diceBag = bag;
	}

	/**
	 * <p>Gets the message's dice bag.</p>
	 *
	 * @return Dice bag the message pertains to, if any.
	 */
	public DiceBagModel getDiceBag()
	{
		return m_diceBag;
	}

	/**
	 * <p>Gets type of message.</p>
	 *
	 * @return One of the type contants.
	 */
	public int getType()
	{
		return m_type;
	}
}
