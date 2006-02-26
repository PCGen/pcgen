/*
 * FeatMultipleChoice.java
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
 * Created on October 24, 2002, 12:35 AM
 *
 * $Id: FeatMultipleChoice.java,v 1.20 2005/10/12 14:16:15 karianna Exp $
 */
package pcgen.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Spell Mastery allows wizards a choice of a number of spells equal to their intelligence
 * modifier at the time the feat is taken. As this modifier can change, this class is used
 * to keep track of the maximum number of choices allowed and the specific choices made in
 * order to allow editing of the feat's selection(s) at later dates.
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.20 $
 */
public final class FeatMultipleChoice implements Serializable
{
	private static final long serialVersionUID = 1;
	private List choices = null;
	private int maxChoices = 0;

	/**
	 * Constructor
	 */
	public FeatMultipleChoice()
	{
	    // Empty Constructor
	}

	/**
	 *  Set the maximum number of choices allowed.
	 * @param argMaxChoices
	 */
	public void setMaxChoices(final int argMaxChoices)
	{
		maxChoices = argMaxChoices;
	}

	/**
	 * Add a choice to the list
	 * @param aChoice
	 */
	public void addChoice(final String aChoice)
	{
		if (choices == null)
		{
			choices = new ArrayList();
		}

		choices.add(aChoice);
	}

	/**
	 * Returns a string representation of the instance.
	 * <max choices>:<# choices>[:<choice 1>[:<choice 2>[:....[:<choice n>]]]]
	 *
	 * @return String
	 */
	public String toString()
	{
		final StringBuffer sb = new StringBuffer(50);
		sb.append(maxChoices).append(':');

		if (choices != null)
		{
			sb.append(choices.size());

			for (int i = 0; i < choices.size(); ++i)
			{
				sb.append(':').append(choices.get(i).toString());
			}
		}
		else
		{
			sb.append('0');
		}

		return sb.toString();
	}

	/**
	 * Get the choice at index 'idx' .
	 * @param idx
	 * @return String
	 */
	String getChoice(final int idx)
	{
		if ((choices != null) && (idx < choices.size()))
		{
			return (String) choices.get(idx);
		}

		return "";
	}

	/**
	 * Get the number of choices made.
	 * @return number of choices
	 */
	int getChoiceCount()
	{
		if (choices != null)
		{
			return choices.size();
		}

		return 0;
	}

	/**
	 * Get the list of chosen items.
	 * @return choices
	 */
	List getChoices()
	{
		return choices;
	}

	/**
	 * Get the maximum number of choices allowed.
	 * @return maximum choices
	 */
	int getMaxChoices()
	{
		return maxChoices;
	}
}
