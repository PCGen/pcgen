/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ObjectGrouping;

/**
 * A RemoteModifier is a container for all the information necessary to modify a
 * remote variable.
 * 
 * This includes a VarModifier (containing the scope, the variable name, and the
 * Modifier to be applied) as well as an ObjectGrouping (indicating the items to
 * which this RemoteModifier should be applied.
 * 
 * This allows that grouping of information to be passed as a single unit of
 * information.
 * 
 * @param <T>
 *            The format of the variable modified by the Modifier in this
 *            VarModifier
 */
public class RemoteModifier<GT extends CDOMObject & VarScoped, MT>
{

	/**
	 * The VarModifier indicating the variable to which the Modifier should be
	 * applied.
	 */
	public final VarModifier<MT> varModifier;

	/**
	 * The ObjectGrouping indicating the objects upon which this RemoteModifier
	 * should be applied.
	 */
	public final ObjectGrouping<GT> grouping;

	/**
	 * Constructs a new RemoteModifier from the given ObjectGrouping and
	 * VarModifier.
	 * 
	 * @param grouping
	 *            the objects upon which this RemoteModifier should be applied
	 * @param modifier
	 *            the variable to which the Modifier should be applied
	 */
	public RemoteModifier(ObjectGrouping<GT> grouping, VarModifier<MT> modifier)
	{
		this.grouping = grouping;
		this.varModifier = modifier;
	}

	/**
	 * Returns the class of object to which this RemoteModifier might be
	 * applied.
	 * 
	 * Note that there is no restriction that objects within this grouping be
	 * exactly the returned class, it is legal for them to extend the returned
	 * class.
	 * 
	 * @return the class of object to which this RemoteModifier might be applied
	 */
	public Class<? extends GT> getGroupClass()
	{
		return grouping.getReferenceClass();
	}
}
