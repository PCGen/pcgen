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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package pcgen.gui3.sources;

import pcgen.core.Campaign;

/**
 * One row of the Advanced tab's "available campaigns" tree. The tree has three
 * levels — publisher group, campaign-setting group, campaign leaf — and the
 * sealed hierarchy here reflects that 1:1 so the TreeTableView's column
 * factories can pattern-match instead of carrying nullable fields.
 */
public sealed interface SourceTreeNode
{
	String displayLabel();

	record Publisher(String name) implements SourceTreeNode
	{
		@Override
		public String displayLabel()
		{
			return name;
		}
	}

	record Setting(String publisher, String name) implements SourceTreeNode
	{
		@Override
		public String displayLabel()
		{
			return name;
		}
	}

	record Leaf(Campaign campaign) implements SourceTreeNode
	{
		@Override
		public String displayLabel()
		{
			return campaign.getDisplayName();
		}
	}
}

