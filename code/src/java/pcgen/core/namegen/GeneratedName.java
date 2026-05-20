/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.core.namegen;

import java.util.List;

/**
 * Result of a single name generation: the assembled name plus the meaning
 * and pronunciation strings produced from the same data, and the {@link Rule}
 * that was used (so callers can re-trigger or inspect the structure).
 */
public record GeneratedName(String name, String meaning, String pronunciation,
		Rule rule, List<DataValue> parts)
{
}
