/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.core;
import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.output.channel.compat.DeityCompat;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.Test;

public class SizeAdjustmentTest extends AbstractCharacterTestCase
{

	@Test
	public void testSizeMod() throws Exception
	{
		Race race = new Race();
		race.setName("Race");
		PCTemplate template = new PCTemplate();
		template.setName("Template");
		Deity deity = new Deity();
		deity.setName("Deity");

		LoadContext context = Globals.getContext();
		context.getReferenceContext().importObject(template);
		context.unconditionallyProcess(race, "SIZE", "S");
		context.unconditionallyProcess(template, "SIZE", "D");
		context.unconditionallyProcess(deity, "BONUS", "SIZEMOD|NUMBER|1");
		context.resolveDeferredTokens();
		context.getReferenceContext().resolveReferences(null);

		PlayerCharacter pc = getCharacter();
		assertEquals("M", pc.getSizeAdjustment().getKeyName());
		pc.setRace(race);
		assertEquals("S", pc.getSizeAdjustment().getKeyName());
		pc.addTemplate(template);
		assertEquals("D", pc.getSizeAdjustment().getKeyName());
		DeityCompat.setCurrentDeity(pc.getCharID(), deity);
		pc.calcActiveBonuses();
		assertEquals("T", pc.getSizeAdjustment().getKeyName());
	}

}
