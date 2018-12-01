/*
 * Copyright James Dempsey, 2015
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
package pcgen.core.prereq;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * {@code PreFactTest} tests that the PREFACT tag is
 * working correctly.
 */
public class PreFactTest extends AbstractCharacterTestCase
{

	@Override
	protected void additionalSetUp() throws Exception
	{
		LoadContext context = Globals.getContext();
		BuildUtilities.createFact(context, "Abb", Race.class);
		
		super.additionalSetUp();
	}

	/**
	 * Test the PREFACT code.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void testFact() throws PersistenceLayerException
	{
		final PlayerCharacter character = getCharacter();
		Race race = new Race();
		BuildUtilities.setFact(race, "ABB", "Hgln");
		race.setName("Hobgoblin");

		character.setRace(race);

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();
		prereq = factory.parse("PREFACT:1,RACE,ABB=Hum");

		assertFalse("Character should not be a matching race", PrereqHandler.passes(
			prereq, character, null));

		prereq = factory.parse("PREFACT:1,RACE,ABB=Hgln");

		assertTrue("Character should be a matching race", PrereqHandler.passes(prereq,
			character, null));
	}
}
