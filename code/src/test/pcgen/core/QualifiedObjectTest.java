/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Guards against regression of issue #6978, where the Domains tab rendered
 * QualifiedObject's debug-format toString instead of the wrapped object's name.
 */
class QualifiedObjectTest
{
	@Test
	void toStringDelegatesToWrappedObject()
	{
		assertEquals("Air", new QualifiedObject<>("Air").toString());
	}

	@Test
	void toStringReturnsEmptyStringForNullWrappedObject()
	{
		assertEquals("", new QualifiedObject<>(null).toString());
	}
}
