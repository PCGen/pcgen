/*
 * PreAgeSetRoundRobin.java
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 1/04/2008
 *
 * $Id: PreAgeSetRoundRobin.java 8147 2008-10-19 19:45:26Z thpr $
 */
package plugin.pretokens;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSpecialAbilityParser;
import plugin.pretokens.writer.PreSpecialAbilityWriter;

/**
 * <code>PreAgeSetRoundRobin</code> verifies that preageset tags can be 
 * read and written.
 *
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2008-10-19 15:45:26 -0400 (Sun, 19 Oct 2008) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 8147 $
 */
public class PreSARoundRobin extends AbstractBasicRoundRobin
{
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String args[])
	{
		TestRunner.run(PreSARoundRobin.class);
	}

	/**
	 * Suite.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSARoundRobin.class);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSpecialAbilityParser());
		TokenRegistration.register(new PreSpecialAbilityWriter());
	}

	/* (non-Javadoc)
	 * @see plugin.pretokens.AbstractBasicRoundRobin#getBaseString()
	 */
	@Override
	public String getBaseString()
	{
		return "SA";
	}

	@Override
	public boolean isTypeAllowed()
	{
		return false;
	}

}
