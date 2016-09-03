/*
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
package pcgen.cdom.enumeration;

import pcgen.cdom.facet.fact.AgeFacet;
import pcgen.cdom.facet.fact.HeightFacet;
import pcgen.cdom.facet.fact.WeightFacet;

public enum NumericPCAttribute implements GenericPCAttribute
{
	AGE(true, AgeFacet.class),
	HEIGHT(false, HeightFacet.class),
	WEIGHT(false, WeightFacet.class),
	;


	private final boolean recalcActiveBonuses;
	private final Class myClass;

	NumericPCAttribute(final boolean recalcActiveBonuses, final Class myClass)
	{
		this.recalcActiveBonuses = recalcActiveBonuses;
		this.myClass = myClass;
	}

	public boolean shouldRecalcActiveBonuses()
	{
		return recalcActiveBonuses;
	}

	public Class getMyClass() {
		return myClass;
	}

	@Override
	public String toString()
	{
		return "NumericPCAttribute{" +
				"recalcActiveBonuses=" + recalcActiveBonuses +
				'}';
	}
}
