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
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreCampaignParser;
import plugin.pretokens.writer.PreCampaignWriter;

/**
 * The Class {@code PreCampaignRoundRobin} is responsible for testing
 * that PRECAMPAIGN tags can be read and written. 
 * 
 * 
 */
public class PreCampaignRoundRobin extends AbstractBasicRoundRobin
{

	/**
	 * Suite.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCampaignRoundRobin.class);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreCampaignParser());
		TokenRegistration.register(new PreCampaignWriter());
	}

	/**
	 * @see plugin.pretokens.AbstractBasicRoundRobin#getBaseString()
	 */
	@Override
	public String getBaseString()
	{
		return "CAMPAIGN";
	}

	/**
	 * @see plugin.pretokens.AbstractBasicRoundRobin#isTypeAllowed()
	 */
	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

	public void testNegateItem()
	{
		AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString() + ":1,Foo,[TYPE=Bar]",
				"PREMULT:2,[PRE" + getBaseString() + ":1,Foo],[!PRE"
						+ getBaseString() + ":1,TYPE=Bar]");
	}

}
