/*
 * PCGVer2ParserCharacterTest.java
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 10/11/2013
 *
 * $Id$
 */
package pcgen.io;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;

/**
 * PCGVer2ParserCharacterTest runs tests on PCGVer2Parser which require a 
 * character to be supplied. 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class PCGVer2ParserCharacterTest extends AbstractCharacterTestCase
{

	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * Check that a racial ADD:SPELLCASTER happens exactly once on character 
	 * load. Duplication of the association has occurred a couple of times in 
	 * the past.
	 * @throws Exception
	 */
	public void testRaceAddSpellcaster() throws Exception
	{
		LoadContext context = Globals.getContext();
		Race rakshasha =
				context.ref.constructCDOMObject(Race.class, "Rakshasa");
		context
			.unconditionallyProcess(rakshasha, "ADD", "SPELLCASTER|Sorcerer");
		PCClass sorcerer =
				context.ref.constructCDOMObject(PCClass.class, "Sorcerer");

		PlayerCharacter pc = getCharacter();
		pc.setImporting(true);
		PCGVer2Parser pcgParser = new PCGVer2Parser(pc);
		
		String[] pcgLines =
				new String[]{"RACE:Rakshasa|ADD:[SPELLCASTER:Sorcerer|CHOICE:Sorcerer]"};
		pcgParser.parsePCG(pcgLines);
		
		PersistentTransitionChoice<?> tc = rakshasha.getListFor(ListKey.ADD).get(0);
		List<Object> assocList = pc.getAssocList(tc, AssociationListKey.ADD);
		assertEquals("Number of associations for ADD " + assocList, 1, assocList.size());
	}
}
