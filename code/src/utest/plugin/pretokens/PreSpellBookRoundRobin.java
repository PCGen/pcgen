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

import org.junit.Before;
import org.junit.Test;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreSpellBookParser;
import plugin.pretokens.writer.PreSpellBookWriter;

public class PreSpellBookRoundRobin extends AbstractPreRoundRobin
{




	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		TokenRegistration.register(new PreSpellBookParser());
		TokenRegistration.register(new PreSpellBookWriter());
	}

	@Test
	public void testYes()
	{
		runRoundRobin("PRESPELLBOOK:YES");
	}

	@Test
	public void testNo()
	{
		runRoundRobin("PRESPELLBOOK:NO");
	}

}
