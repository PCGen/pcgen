/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SizeAdjustment;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSizeParser;
import plugin.pretokens.writer.PreSizeWriter;

public class PreSizeRoundRobin extends AbstractComparatorRoundRobin
{
	protected SizeAdjustment medium;

	public static void main(String args[])
	{
		TestRunner.run(PreSizeRoundRobin.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSizeRoundRobin.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSizeParser());
		TokenRegistration.register(new PreSizeWriter());
		medium = BuildUtilities.createSize("Medium");
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
	}

	public void testSimpleInteger()
	{
		runRoundRobin("M");
	}

	@Override
	public String getBaseString()
	{
		return "SIZE";
	}

	@Override
	public boolean isBaseAllowed()
	{
		return true;
	}

}