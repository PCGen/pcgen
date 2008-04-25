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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * <code>SourceEntryTest</code> verifies the function of the SourceEntry class.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class SourceEntryTest extends TestCase
{

	Source source;
	Campaign campaign;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		campaign = new Campaign();
		campaign.setPubNameWeb("PubWeb");
		campaign.setPubNameShort("PubShort");
		campaign.setPubNameLong("PubLong");
		source = new Source();
		source.setCampaign(campaign);
		source.setWebsite("http://website");
		source.setShortName("ShortName");
		source.setLongName("LongName");
	}

	/**
	 * Test method for {@link pcgen.core.SourceEntry#getFormattedString(pcgen.core.SourceEntry.SourceFormat, boolean)}.
	 */
	public void testGetFormattedString()
	{
		SourceEntry se = new SourceEntry(source);
		se.setPageNumber("42");
		assertEquals("Web", "PubWeb - http://website", se.getFormattedString(
			SourceEntry.SourceFormat.WEB, true));
		assertEquals("Short", "ShortName, 42", se.getFormattedString(
			SourceEntry.SourceFormat.SHORT, true));
		assertEquals("Medium", "LongName", se.getFormattedString(
			SourceEntry.SourceFormat.MEDIUM, false));
		assertEquals("Long", "PubLong - LongName, 42", se.getFormattedString(
			SourceEntry.SourceFormat.LONG, true));
		campaign.setPubNameLong("");
		assertEquals("Long", "LongName, 42", se.getFormattedString(
			SourceEntry.SourceFormat.LONG, true));
	}

	public void testSetFromMapCleanStart() throws ParseException
	{
		SourceEntry se = new SourceEntry();
		se.setFromMap(null);
		assertEquals("Unchanged after null set", null, 
			se.getFieldByType(SourceEntry.SourceFormat.SHORT));

		Map<String, String> aSourceMap = new HashMap<String, String>();
		aSourceMap.put(SourceEntry.SourceFormat.SHORT.toString(), "NewShortName");
		se.setFromMap(aSourceMap);
		assertEquals("Changed after set", "NewShortName", 
			se.getFieldByType(SourceEntry.SourceFormat.SHORT));
		assertEquals("Still empty after set", null, 
			se.getFieldByType(SourceEntry.SourceFormat.LONG));
		assertEquals("Campaign still null after set", null, 
			se.getSourceBook().getCampaign());
	}

	public void testSetFromMapExistingSource() throws ParseException
	{
		SourceEntry se = new SourceEntry(source);
		se.setFromMap(null);
		assertEquals("Unchanged after null set", "ShortName", 
			se.getFieldByType(SourceEntry.SourceFormat.SHORT));

		Map<String, String> aSourceMap = new HashMap<String, String>();
		aSourceMap.put(SourceEntry.SourceFormat.SHORT.toString(), "NewShortName");
		se.setFromMap(aSourceMap);
		assertEquals("Changed after set", "NewShortName", 
			se.getFieldByType(SourceEntry.SourceFormat.SHORT));
		assertEquals("Old value erased after set", null, 
			se.getFieldByType(SourceEntry.SourceFormat.LONG));
		assertEquals("Publisher still present after set", "PubLong", 
			se.getSourceBook().getCampaign().getPubNameLong());
	}
}
