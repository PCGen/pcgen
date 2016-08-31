/*
 *  Initiative - A role playing utility to track turns
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package gmgen.plugin.dice;

import java.util.stream.IntStream;

@FunctionalInterface
interface ResultModifier
{


	/**
	 * Given a sequence of values, produces a new sequence of values
	 * This allows the modification of a value to be abstracted from the production of the original value.
	 * @param in sequence of original values
	 * @return sequence of values
	 */
	IntStream apply(IntStream in);

	static IntStream modify(final ResultModifier... modifiers)
	{
		IntStream result = IntStream.empty();
		for(final ResultModifier rm: modifiers)
		{
			result = rm.apply(result);
		}
		return result;
	}
}
