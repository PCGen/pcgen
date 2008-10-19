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
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * Deals with CONTAINS token
 */
public class ContainsToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "CONTAINS";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		/*
		 * TODO I believe this needs to clear the List and if it already
		 * contains something, then should it provide an informational message?
		 */
		String weightCapacity = pipeTok.nextToken();

		boolean hadAsterisk = false;
		if (weightCapacity.charAt(0) == Constants.CHAR_ASTERISK)
		{
			hadAsterisk = true;
			context.getObjectContext().put(eq,
					ObjectKey.CONTAINER_CONSTANT_WEIGHT, Boolean.TRUE);
			weightCapacity = weightCapacity.substring(1);
		}

		int percentLoc = weightCapacity.indexOf(Constants.PERCENT);
		if (percentLoc != weightCapacity.lastIndexOf(Constants.PERCENT))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Cannot have two weight reduction "
							+ "characters (indicated by %): " + value);
			return false;
		}
		if (percentLoc != -1)
		{
			if (hadAsterisk)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Cannot have Constant Weight (indicated by *) "
								+ "and weight reduction (indicated by %): "
								+ value);
				return false;
			}
			String redString = weightCapacity.substring(0, percentLoc);
			weightCapacity = weightCapacity.substring(percentLoc + 1);

			try
			{
				context.getObjectContext().put(eq,
						IntegerKey.CONTAINER_REDUCE_WEIGHT,
						Integer.valueOf(redString));
			}
			catch (NumberFormatException ex)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Weight Reduction (indicated by %) must be an integer: "
								+ value);
				return false;
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
				weightCap = BigDecimalHelper.trimBigDecimal(new BigDecimal(
						weightCapacity));
				if (BigDecimal.ZERO.compareTo(weightCap) > 0)
				{
					Logging
							.addParseMessage(
									Logging.LST_ERROR,
									"Weight Capacity must be >= 0: "
											+ weightCapacity
											+ "\n  Use 'UNLIM' (not -1) for unlimited Count");
					return false;
				}
			}
			catch (NumberFormatException ex)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
						"Weight Capacity must be 'UNLIM or a number >= 0: "
								+ weightCapacity);
				return false;
			}
		}
		context.getObjectContext().put(eq, ObjectKey.CONTAINER_WEIGHT_CAPACITY,
				weightCap);

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
				return false;
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
						itemNumber = BigDecimalHelper
								.trimBigDecimal(new BigDecimal(itemNumString));
					}
					catch (NumberFormatException ex)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"Item Number for " + itemType
										+ " must be 'UNLIM' or a number > 0: "
										+ itemNumString);
						return false;
					}
					if (BigDecimal.ZERO.compareTo(itemNumber) >= 0)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"Cannot have negative quantity of " + itemType
										+ ": " + value);
						return false;
					}
				}
				if (limited)
				{
					limitedCapacity = limitedCapacity.add(itemNumber);
				}
				context.getObjectContext().addToList(eq, ListKey.CAPACITY,
						new Capacity(itemType, itemNumber));
			}
		}

		if (totalCap == null)
		{
			BigDecimal totalCapacity = limited ? limitedCapacity
					: Capacity.UNLIMITED;
			totalCap = Capacity.getTotalCapacity(totalCapacity);
		}

		context.getObjectContext().put(eq, ObjectKey.TOTAL_CAPACITY, totalCap);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Changes<Capacity> changes = context.getObjectContext().getListChanges(
				eq, ListKey.CAPACITY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();

		Boolean b = context.getObjectContext().getObject(eq,
				ObjectKey.CONTAINER_CONSTANT_WEIGHT);
		if (b != null && b.booleanValue())
		{
			sb.append(Constants.CHAR_ASTERISK);
		}

		Integer reducePercent = context.getObjectContext().getInteger(eq,
				IntegerKey.CONTAINER_REDUCE_WEIGHT);
		if (reducePercent != null)
		{
			sb.append(reducePercent).append(Constants.PERCENT);
		}

		BigDecimal cap = context.getObjectContext().getObject(eq,
				ObjectKey.CONTAINER_WEIGHT_CAPACITY);
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
		if (capacityList.size() == 1)
		{
			for (Capacity c : capacityList)
			{
				if (c.getType() == null
						&& Capacity.UNLIMITED.equals(c.getCapacity()))
				{
					// Special Case: Nothing additional
					return new String[] { sb.toString() };
				}
			}
		}
		BigDecimal limitedCapacity = BigDecimal.ZERO;
		boolean limited = true;
		Capacity total = null;
		for (Capacity c : capacityList)
		{
			String capType = c.getType();
			if (capType == null)
			{
				total = c;
			}
			else
			{
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
		}
		if (total == null)
		{
			// Error
			return null;
		}
		if (!limitedCapacity.equals(total.getCapacity())
				&& !Capacity.UNLIMITED.equals(total.getCapacity()))
		{
			// Need to write out total
			sb.append("Total").append(Constants.EQUALS).append(
					total.getCapacity());
		}
		return new String[] { sb.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
