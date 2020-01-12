/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.equipment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.Capacity;
import pcgen.core.Equipment;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.BigDecimalHelper;

/**
 * Deals with CONTAINS token
 */
public class ContainsToken extends AbstractTokenWithSeparator<Equipment> implements CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "CONTAINS";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, Equipment eq, String value)
	{
		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		context.getObjectContext().removeList(eq, ListKey.CAPACITY);
		String weightCapacity = pipeTok.nextToken();

		boolean hadAsterisk = false;
		if (weightCapacity.charAt(0) == Constants.CHAR_ASTERISK)
		{
			hadAsterisk = true;
			context.getObjectContext().put(eq, ObjectKey.CONTAINER_CONSTANT_WEIGHT, Boolean.TRUE);
			weightCapacity = weightCapacity.substring(1);
		}

		int percentLoc = weightCapacity.indexOf(Constants.PERCENT);
		if (percentLoc != weightCapacity.lastIndexOf(Constants.PERCENT))
		{
			return new ParseResult.Fail("Cannot have two weight reduction " + "characters (indicated by %): " + value);
		}
		if (percentLoc != -1)
		{
			if (hadAsterisk)
			{
				return new ParseResult.Fail("Cannot have Constant Weight (indicated by *) "
					+ "and weight reduction (indicated by %): " + value);
			}
			String redString = weightCapacity.substring(0, percentLoc);
			weightCapacity = weightCapacity.substring(percentLoc + 1);

			try
			{
				context.getObjectContext().put(eq, IntegerKey.CONTAINER_REDUCE_WEIGHT, Integer.valueOf(redString));
			}
			catch (NumberFormatException ex)
			{
				return new ParseResult.Fail("Weight Reduction (indicated by %) must be an integer: " + value);
			}
		}

		BigDecimal weightCap;
		if ("UNLIM".equals(weightCapacity))
		{
			weightCap = Capacity.UNLIMITED;
		}
		else
		{
			try
			{
				weightCap = BigDecimalHelper.trimBigDecimal(new BigDecimal(weightCapacity));
				if (BigDecimal.ZERO.compareTo(weightCap) > 0)
				{
					return new ParseResult.Fail("Weight Capacity must be >= 0: " + weightCapacity
						+ "\n  Use 'UNLIM' (not -1) for unlimited Count");
				}
			}
			catch (NumberFormatException ex)
			{
				return new ParseResult.Fail("Weight Capacity must be 'UNLIM or a number >= 0: " + weightCapacity);
			}
		}
		context.getObjectContext().put(eq, ObjectKey.CONTAINER_WEIGHT_CAPACITY, weightCap);

		Capacity totalCap = null;
		boolean limited = true;
		if (!pipeTok.hasMoreTokens())
		{
			limited = false;
			totalCap = Capacity.ANY;
		}

		BigDecimal limitedCapacity = BigDecimal.ZERO;

		while (pipeTok.hasMoreTokens())
		{
			String typeString = pipeTok.nextToken();
			int equalLoc = typeString.indexOf(Constants.EQUALS);
			if (equalLoc != typeString.lastIndexOf(Constants.EQUALS))
			{
				return new ParseResult.Fail("Two many = signs");
			}
			if (equalLoc == -1)
			{
				limited = false;
				context.getObjectContext().addToList(eq, ListKey.CAPACITY,
					new Capacity(typeString, Capacity.UNLIMITED));
			}
			else
			{
				String itemType = typeString.substring(0, equalLoc);
				String itemNumString = typeString.substring(equalLoc + 1);
				BigDecimal itemNumber;
				if ("UNLIM".equals(itemNumString))
				{
					limited = false;
					itemNumber = Capacity.UNLIMITED;
				}
				else
				{
					try
					{
						itemNumber = BigDecimalHelper.trimBigDecimal(new BigDecimal(itemNumString));
					}
					catch (NumberFormatException ex)
					{
						return new ParseResult.Fail(
							"Item Number for " + itemType + " must be 'UNLIM' or a number > 0: " + itemNumString);
					}
					if (BigDecimal.ZERO.compareTo(itemNumber) >= 0)
					{
						return new ParseResult.Fail("Cannot have negative quantity of " + itemType + ": " + value);
					}
				}
				if (limited)
				{
					limitedCapacity = limitedCapacity.add(itemNumber);
				}
				context.getObjectContext().addToList(eq, ListKey.CAPACITY, new Capacity(itemType, itemNumber));
			}
		}

		if (totalCap == null)
		{
			BigDecimal totalCapacity = limited ? limitedCapacity : Capacity.UNLIMITED;
			totalCap = Capacity.getTotalCapacity(totalCapacity);
		}

		context.getObjectContext().put(eq, ObjectKey.TOTAL_CAPACITY, totalCap);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		Changes<Capacity> changes = context.getObjectContext().getListChanges(eq, ListKey.CAPACITY);
		Capacity totalCapacity = context.getObjectContext().getObject(eq, ObjectKey.TOTAL_CAPACITY);
		if (totalCapacity == null && (changes == null || changes.isEmpty()))
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();

		Boolean b = context.getObjectContext().getObject(eq, ObjectKey.CONTAINER_CONSTANT_WEIGHT);
		if (b != null && b)
		{
			sb.append(Constants.CHAR_ASTERISK);
		}

		Integer reducePercent = context.getObjectContext().getInteger(eq, IntegerKey.CONTAINER_REDUCE_WEIGHT);
		if (reducePercent != null)
		{
			sb.append(reducePercent).append(Constants.PERCENT);
		}

		BigDecimal cap = context.getObjectContext().getObject(eq, ObjectKey.CONTAINER_WEIGHT_CAPACITY);
		if (cap == null)
		{
			// CONSIDER ERROR??
			return null;
		}
		if (Capacity.UNLIMITED.equals(cap))
		{
			sb.append("UNLIM");
		}
		else
		{
			sb.append(cap);
		}

		Collection<Capacity> capacityList = changes.getAdded();
		if (capacityList == null)
		{
			if (Capacity.UNLIMITED.equals(totalCapacity.getCapacity()))
			{
				// Special Case: Nothing additional
				return new String[]{sb.toString()};
			}
		}
		BigDecimal limitedCapacity = BigDecimal.ZERO;
		boolean limited = true;
		for (Capacity c : capacityList)
		{
			String capType = c.getType();
			sb.append(Constants.PIPE);
			BigDecimal thisCap = c.getCapacity();
			sb.append(capType);
			if (Capacity.UNLIMITED.equals(thisCap))
			{
				limited = false;
			}
			else
			{
				if (limited)
				{
					limitedCapacity = limitedCapacity.add(thisCap);
				}
				sb.append(Constants.EQUALS).append(thisCap);
			}
		}
		if (!limitedCapacity.equals(totalCapacity.getCapacity())
			&& !Capacity.UNLIMITED.equals(totalCapacity.getCapacity()))
		{
			// Need to write out total
			sb.append("Total").append(Constants.EQUALS).append(totalCapacity.getCapacity());
		}
		return new String[]{sb.toString()};
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
