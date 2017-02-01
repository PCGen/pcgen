/**
 * LoadContextTest.java
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
 *
 * Created on 28/06/2010 3:33:01 PM
 *
 * $Id$
 */
package pcgen.rules.context;

import java.util.Collection;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

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

/**
 * The Class <code>LoadContextTest</code> checks the fucntion fo the LoadCOntext class.
 *
 * <br/>
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class LoadContextTest
{

	/**
	 * Test method for {@link pcgen.rules.context.LoadContext#cloneInMasterLists(pcgen.cdom.base.CDOMObject, java.lang.String)}.
	 */
	@Test
	public final void testCloneInMasterListsSimple()
	{
		Spell testSpell = TestHelper.makeSpell("LoadContextTest");
		Spell newSpell = Globals.getContext().performCopy(testSpell, "New Spell");
		Assert.assertEquals(
				"Old spell name incorrect",
				"LoadContextTest",
				testSpell.getDisplayName()
		);
		Assert.assertEquals(
				"New spell name incorrect",
				"New Spell",
				newSpell.getDisplayName()
		);
	}

	/**
	 * Test method for {@link pcgen.rules.context.LoadContext#cloneInMasterLists(pcgen.cdom.base.CDOMObject, java.lang.String)}.
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
		Assert.assertTrue(context.getReferenceContext().resolveReferences(null));
		context.commit();
		
		Spell newSpell = context.performCopy(testSpell, "New Spell");
		context.commit();
		Assert.assertEquals(
				"Old spell name incorrect",
				"LoadContextTest",
				testSpell.getDisplayName()
		);
		Assert.assertEquals(
				"New spell name incorrect",
				"New Spell",
				newSpell.getDisplayName()
		);
		
		// Check associations
		MasterListInterface masterLists = SettingsHandler.getGame().getMasterLists();
		Collection<AssociatedPrereqObject> assoc =
				masterLists.getAssociations(ref, testSpell);
		Assert.assertEquals(
				"Incorrect size of assoc list for orig spell",
				1,
				assoc.size()
		);
		AssociatedPrereqObject apo = assoc.iterator().next();
		Assert.assertEquals("Incorrect level", 1, apo.getAssociation(
				AssociationKey.SPELL_LEVEL).intValue());

		assoc = masterLists.getAssociations(ref, newSpell);
		Assert.assertEquals("Incorrect size of assoc list for new spell", 1, assoc
				.size());
		apo = assoc.iterator().next();
		Assert.assertEquals("Incorrect level", 1, apo.getAssociation(
				AssociationKey.SPELL_LEVEL).intValue());
	}

}
