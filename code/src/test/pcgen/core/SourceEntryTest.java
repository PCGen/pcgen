/*
 * SourceEntryTest.java
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
 *
 * Created on 25/04/2008
 *
 * $Id$
 */

package pcgen.core;

import javax.xml.transform.Source;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;

/**
 * <code>SourceEntryTest</code> verifies the function of the SourceEntry class.
 *
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class SourceEntryTest
{

	Source source;
	Campaign campaign;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{

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
	 * Test method for {@link pcgen.core.SourceEntry#getFormattedString(pcgen.core.SourceEntry.SourceFormat, boolean)}.
	 */
	@Test
	public void testGetFormattedString()
	{
		campaign.put(StringKey.SOURCE_PAGE, "42");
		Assert.assertEquals("Web", "PubWeb - http://website", SourceFormat
				.getFormattedString(campaign, SourceFormat.WEB, true));
		Assert.assertEquals("Short", "ShortName, 42", SourceFormat.getFormattedString(
				campaign, SourceFormat.SHORT, true));
		Assert.assertEquals("Medium", "LongName", SourceFormat.getFormattedString(
				campaign, SourceFormat.MEDIUM, false));
		Assert.assertEquals("Long", "PubLong - LongName, 42", SourceFormat
				.getFormattedString(campaign, SourceFormat.LONG, true));
		campaign.put(StringKey.PUB_NAME_LONG, "");
		Assert.assertEquals("Long", "LongName, 42", SourceFormat.getFormattedString(
				campaign, SourceFormat.LONG, true));
	}
}
