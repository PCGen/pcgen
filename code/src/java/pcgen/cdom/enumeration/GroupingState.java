/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import pcgen.util.Logging;

/**
 * A GroupingState indicates how a PrimitiveChoiceSet or PrimitiveChoiceFilter
 * can be combined.
 */
public enum GroupingState
{

	/**
	 * INVALID indicates that the PrimitiveChoiceSet has been constructed in a
	 * way that means the result is non-sensical. For example, doing a logical
	 * OR between the ANY set and a TYPE is non-sensical, because the OR is
	 * wasteful (or with ANY is useless).
	 */
	INVALID
	{
		@Override
		public GroupingState add(GroupingState state)
		{
			return INVALID;
		}

		@Override
		public GroupingState negate()
		{
			return INVALID;
		}

		@Override
		public GroupingState reduce()
		{
			return INVALID;
		}

		@Override
		public GroupingState compound(GroupingState state)
		{
			return INVALID;
		}

		@Override
		public boolean isValid()
		{
			return false;
		}
	},

	/**
	 * ALLOWS_NONE indicates that the underlying Set cannot be logically
	 * combined with any other set. This is often the case for the ANY/ALL set
	 * (as any logical combination is useless)
	 */
	ALLOWS_NONE
	{
		@Override
		public GroupingState add(GroupingState state)
		{
			if (state != EMPTY)
			{
				Logging.errorPrint(
					"Attempt to add '" + state + "' grouping state to " + "'ALLOWS_NONE' resulted in 'INVALID'.");
				return INVALID;
			}
			return ALLOWS_NONE;
		}

		@Override
		public GroupingState negate()
		{
			return INVALID;
		}

		@Override
		public GroupingState reduce()
		{
			return ANY;
		}

		@Override
		public GroupingState compound(GroupingState state)
		{
			return ALLOWS_NONE;
		}

		@Override
		public boolean isValid()
		{
			return true;
		}
	},

	/**
	 * ALLOWS_INTERSECTION indicates that the underlying set can be combined in
	 * a logical AND (intersection), but not in a logical OR (union)
	 */
	ALLOWS_INTERSECTION
	{
		@Override
		public GroupingState add(GroupingState state)
		{
			if (state == this || state == EMPTY || state == ANY)
			{
				return this;
			}
			Logging.errorPrint(
				"Attempt to add '" + state + "' grouping state to 'ALLOWS_INTERSECTION' " + "resulted in 'INVALID'.");
			return INVALID;
		}

		@Override
		public GroupingState negate()
		{
			return ANY;
		}

		@Override
		public GroupingState reduce()
		{
			//TODO Need to check this logical behavior :)
			return ANY;
		}

		@Override
		public GroupingState compound(GroupingState state)
		{
			return state == ALLOWS_UNION ? INVALID : ANY;
		}

		@Override
		public boolean isValid()
		{
			return true;
		}
	},

	/**
	 * ALLOWS_UNION indicates that the underlying set can be combined in a
	 * logical OR (union), but not in a logical AND (intersection). This is
	 * often the case for a single reference (as a logical AND with any other
	 * set will return either only the object (useless) or nothing at all (less
	 * than useless?)
	 */
	ALLOWS_UNION
	{
		@Override
		public GroupingState add(GroupingState state)
		{
			if (state == this || state == EMPTY || state == ANY)
			{
				return this;
			}
			Logging
				.errorPrint("Attempt to add '" + state + "' grouping state to 'ALLOWS_UNION' resulted in 'INVALID'.");
			return INVALID;
		}

		@Override
		public GroupingState negate()
		{
			return ANY;
		}

		@Override
		public GroupingState reduce()
		{
			return this;
		}

		@Override
		public GroupingState compound(GroupingState state)
		{
			return state == ALLOWS_INTERSECTION ? INVALID : ANY;
		}

		@Override
		public boolean isValid()
		{
			return true;
		}
	},

	/**
	 * ANY indicates that any form of grouping (union or intersection) can be
	 * used with the set.
	 */
	ANY
	{
		@Override
		public GroupingState add(GroupingState state)
		{
			if (state == ALLOWS_NONE)
			{
				Logging.errorPrint("Attempt to add 'ALLOWS_NONE' " + "grouping state to 'ANY' resulted in 'INVALID'.");
				return INVALID;
			}
			return state == EMPTY ? ANY : state;
		}

		@Override
		public GroupingState negate()
		{
			return this;
		}

		@Override
		public GroupingState reduce()
		{
			return this;
		}

		@Override
		public GroupingState compound(GroupingState state)
		{
			return this;
		}

		@Override
		public boolean isValid()
		{
			return true;
		}
	},

	/**
	 * EMPTY is a starting state of not having any GroupingState. This is an
	 * invalid state (basically being an empty set), but can be logically
	 * combined with any valid GroupingState.
	 */
	EMPTY
	{
		@Override
		public GroupingState add(GroupingState state)
		{
			return state;
		}

		@Override
		public GroupingState negate()
		{
			return ALLOWS_NONE;
		}

		@Override
		public GroupingState reduce()
		{
			return this;
		}

		@Override
		public GroupingState compound(GroupingState state)
		{
			return EMPTY;
		}

		@Override
		public boolean isValid()
		{
			return false;
		}
	};

	/**
	 * Adds the given GroupingState to this GroupingState, returning the
	 * GroupingState produced by a combination of the given GroupingState and
	 * this GroupingState.
	 * 
	 * @param state
	 *            The GroupingState to be combined with this GroupingState
	 * @return The GroupingState produced by a combination of the given
	 *         GroupingState and this GroupingState
	 */
	public abstract GroupingState add(GroupingState state);

	/**
	 * Returns the GroupingState that represents the behavior when this
	 * GroupingState is negated. Note that calling negate() twice does not
	 * necessarily return the original GroupingState, as negate is not a 1:1
	 * relationship.
	 * 
	 * @return The GroupingState that represents the behavior when this
	 *         GroupingState is negated
	 */
	public abstract GroupingState negate();

	/**
	 * Returns the GroupingState represented when this GroupingState is placed
	 * into a combination represented by the given GroupingState.
	 * 
	 * Note: this method is only defined if the given GroupingState is
	 * ALLOWS_UNION or ALLOWS_INTERSECTION.
	 * 
	 * For example, if this GroupingState is ALLOWS_UNION, and the given
	 * GroupingState is ALLOWS_INTERSECTION, then this will return INVALID,
	 * since this GroupingState can only be combined under a union.
	 * 
	 * @param state
	 *            The GroupingState under which this GroupingState was combined.
	 * 
	 * @return the GroupingState represented when this GroupingState is placed
	 *         into a combination represented by the given GroupingState
	 */
	public abstract GroupingState compound(GroupingState state);

	/**
	 * Returns true if this GroupingState is valid for use.
	 * 
	 * @return true if this GroupingState is valid for use; false otherwise.
	 */
	public abstract boolean isValid();

	/**
	 * Returns the GroupingState used when this GroupingState is reduced
	 * 
	 * @return the GroupingState used when this GroupingState is reduced
	 */
	public abstract GroupingState reduce();
}
