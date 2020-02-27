/**
 * Copyright James Dempsey, 2010
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
package pcgen.rules.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import pcgen.base.format.StringManager;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.Test;

/**
 * The Class {@code LoadContextTest} checks the function of the LoadCOntext class.
 */
public class LoadContextTest
{

	/**
	 * Test CloneInMasterListsSimple
	 */
	@Test
	public final void testCloneInMasterListsSimple()
	{
		Spell testSpell = TestHelper.makeSpell("LoadContextTest");
		Spell newSpell = Globals.getContext().performCopy(testSpell, "New Spell");
		assertEquals("LoadContextTest", testSpell.getDisplayName(), "Old spell name incorrect");
		assertEquals("New Spell", newSpell.getDisplayName(), "New spell name incorrect");
	}

	/**
	 * Verify that associations from other objects to the object being cloned 
	 * are copied over. 
	 */
	@Test
	public final void testCloneInMasterListsAssoc()
	{
		final LoadContext context = Globals.getContext();
		FactKey.getConstant("ClassType", new StringManager());
		FactKey.getConstant("SpellType", new StringManager());

		Spell testSpell = TestHelper.makeSpell("LoadContextTest");
		PCClass wiz = TestHelper.makeClass("Wizard");
		CDOMReference<ClassSpellList> ref = TokenUtilities.getTypeOrPrimitive(context,
			ClassSpellList.class, wiz.getKeyName());
		AssociatedPrereqObject edge =
				context.getListContext().addToMasterList("CLASSES", testSpell,
					ref, testSpell);
		edge.setAssociation(AssociationKey.SPELL_LEVEL, 1);
		context.getReferenceContext().buildDerivedObjects();
		assertTrue(context.getReferenceContext().resolveReferences(null));
		context.commit();
		
		Spell newSpell = context.performCopy(testSpell, "New Spell");
		context.commit();
		assertEquals("LoadContextTest", testSpell.getDisplayName(), "Old spell name incorrect");
		assertEquals("New Spell", newSpell.getDisplayName(), "New spell name incorrect");
		
		// Check associations
		MasterListInterface masterLists = SettingsHandler.getGameAsProperty().get().getMasterLists();
		Collection<AssociatedPrereqObject> assoc =
				masterLists.getAssociations(ref, testSpell);
		assertEquals(1, assoc.size(), "Incorrect size of assoc list for orig spell");
		AssociatedPrereqObject apo = assoc.iterator().next();
		assertEquals(1, apo.getAssociation(
				AssociationKey.SPELL_LEVEL).intValue(), "Incorrect level");

		assoc = masterLists.getAssociations(ref, newSpell);
		assertEquals(1, assoc
				.size(), "Incorrect size of assoc list for new spell");
		apo = assoc.iterator().next();
		assertEquals(1, apo.getAssociation(
				AssociationKey.SPELL_LEVEL).intValue(), "Incorrect level");
	}

}
