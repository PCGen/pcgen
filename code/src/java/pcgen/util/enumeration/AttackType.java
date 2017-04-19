/*
 * AttackType.java
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.util.enumeration;

import java.util.Optional;
import java.util.stream.Stream;

public enum AttackType
{

	MELEE("BAB"),

	RANGED("RAB"),

	UNARMED("UAB"),

	GRAPPLE("GAB");

	private final String identifier;

	AttackType(String ident)
	{
		identifier = ident;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public static AttackType getInstance(String ident)
	{
		Optional<AttackType> at = Stream.of(AttackType.values())
				.filter(v -> v.identifier.equals(ident))
				.findFirst();
		if (at.isPresent()) {
			return at.get();
		}
		throw new IllegalArgumentException("Illegal AttackType identifier: "
			+ ident);
	}
}
