/*
 * KitAlignment.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on October 15, 2005, 10:00 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

/**
 * Deals with the automatic assignment of an Alignment via a Kit
 */
public class KitAlignment extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long  serialVersionUID = 1;

	private String alignmentStr = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient int alignInd = -1;

	/**
	 * Constructor
	 * @param anAlign
	 */
	public KitAlignment(final String anAlign)
	{
		alignmentStr = anAlign;
	}

	/**
	 * Actually applies the alignment to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	public void apply(PlayerCharacter aPC)
	{
		aPC.setAlignment(alignInd, false);
	}

	/**
	 * testApply
	 *
	 * @param aPC PlayerCharacter
	 * @param aKit Kit
	 * @param warnings List
	 */
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		alignInd = -1;
		if (alignmentStr == null || Constants.s_NONESELECTED.equals(alignmentStr))
		{
			return false;
		}

		final String[] alignArray = SettingsHandler.getGame().getAlignmentListStrings(false);

		List<Integer> alignChoices = new ArrayList<Integer>();
		final StringTokenizer aTok = new StringTokenizer(alignmentStr, "|");
		while (aTok.hasMoreTokens())
		{
			String align = aTok.nextToken();
			try
			{
				int alignIndicator = Integer.parseInt(align);
				alignChoices.add(alignIndicator);
			}
			catch (NumberFormatException e)
			{
				for (int i = 0; i < alignArray.length; i++)
				{
					if (align.equalsIgnoreCase(alignArray[i]))
					{
						alignChoices.add( i );
						break;
					}
				}
			}
		}
		if (alignChoices.size() == 0)
		{
			if (warnings != null)
			{
				warnings.add("ALIGNMENT: Unknown alignment \"" + alignmentStr +
							 "\"");
			}
			return false;
		}
		if (alignChoices.size() == 1)
		{
			Integer intAlign = alignChoices.get(0);
			alignInd = intAlign.intValue();
		}
		else
		{
			// Build the string list.
			final String[] longAlignArray = SettingsHandler.getGame().getAlignmentListStrings(true);
			List<String> choices = new ArrayList<String>(alignChoices.size());

			for ( int choice : alignChoices )
			{
				choices.add( longAlignArray[choice] );
			}

			String align = null;
			while (true)
			{
				List<String> sel = new ArrayList<String>(1);
				Globals.getChoiceFromList("Choose alignment", choices, sel, 1);
				if (sel.size() == 1)
				{
					align = sel.get(0);
				}
				else
				{
					break;
				}
			}
			// Now we have to map it back to an integer.
			for (int i = 0; i < longAlignArray.length; i++)
			{
				if (align.equalsIgnoreCase(longAlignArray[i]))
				{
					alignInd = i;
					break;
				}
			}
		}

		aPC.setAlignment(alignInd, false);

		return true;
	}

	public Object clone()
	{
		KitAlignment aClone = (KitAlignment)super.clone();
		aClone.alignmentStr = alignmentStr;
		return aClone;
	}

	public String getObjectName()
	{
		return "Alignment";
	}

	public String toString()
	{
		if (alignmentStr == null || Constants.s_NONESELECTED.equals(alignmentStr))
		{
			return "";
		}

		final String[] alignArray = SettingsHandler.getGame().getAlignmentListStrings(false);
		List<Integer> alignChoices = new ArrayList<Integer>();

		final StringTokenizer aTok = new StringTokenizer(alignmentStr, "|");

		while (aTok.hasMoreTokens())
		{
			String align = aTok.nextToken();
			try
			{
				int alignIndicator = Integer.parseInt(align);
				alignChoices.add( alignIndicator );
			}
			catch (NumberFormatException e)
			{
				for (int i = 0; i < alignArray.length; i++)
				{
					if (align.equalsIgnoreCase(alignArray[i]))
					{
						alignChoices.add( i );
						break;
					}
				}
			}
		}
		if (alignChoices.size() == 0)
		{
			return "";
		}

		final String[] longAlignArray = SettingsHandler.getGame().getAlignmentListStrings(true);
		if (alignChoices.size() == 1)
		{
			return longAlignArray[ alignChoices.get(0) ];
		}
		// Build the string list.
		StringBuffer buf = new StringBuffer();
		buf.append("One of (");
		for ( int i : alignChoices )
		{
			if (i != 0)
			{
				buf.append(", ");
			}
			buf.append(longAlignArray[ i ]);
		}
		buf.append(")");
		return buf.toString();
	}
}
