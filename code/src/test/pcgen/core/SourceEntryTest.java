/*
 * Copyright 2008 (C) James Dempsey
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

import javax.xml.transform.Source;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;

import junit.framework.TestCase;

/**
 * {@code SourceEntryTest} verifies the function of the SourceEntry class.
 */
public class SourceEntryTest extends TestCase
{

	Source source;
	Campaign campaign;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		campaign = new Campaign();
		campaign.put(StringKey.PUB_NAME_WEB, "PubWeb");
		campaign.put(StringKey.PUB_NAME_SHORT, "PubShort");
		campaign.put(StringKey.PUB_NAME_LONG, "PubLong");
		campaign.put(StringKey.SOURCE_LONG, "LongName");
		campaign.put(StringKey.SOURCE_SHORT, "ShortName");
		campaign.put(StringKey.SOURCE_WEB, "http://website");
		campaign.put(ObjectKey.SOURCE_CAMPAIGN, campaign);
	}

	/**
	 * Test method for
	 * {@link SourceFormat#getFormattedString(CDOMObject, SourceFormat, boolean)}.
	 */
	public void testGetFormattedString()
	{
		campaign.put(StringKey.SOURCE_PAGE, "42");
		assertEquals("Web", "PubWeb - http://website", SourceFormat
				.getFormattedString(campaign, SourceFormat.WEB, true));
		assertEquals("Short", "ShortName, 42", SourceFormat.getFormattedString(
				campaign, SourceFormat.SHORT, true));
		assertEquals("Medium", "LongName", SourceFormat.getFormattedString(
				campaign, SourceFormat.MEDIUM, false));
		assertEquals("Long", "PubLong - LongName, 42", SourceFormat
				.getFormattedString(campaign, SourceFormat.LONG, true));
		campaign.put(StringKey.PUB_NAME_LONG, "");
		assertEquals("Long", "LongName, 42", SourceFormat.getFormattedString(
				campaign, SourceFormat.LONG, true));
	}
}
