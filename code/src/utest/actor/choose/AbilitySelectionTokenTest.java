/*
 * AbilitySelectionTokenTest.java
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
 * Created on 06/06/2013
 *
 * $Id$
 */
package actor.choose;

import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import plugin.lsttokens.choose.AbilitySelectionToken;

/**
 * Unit test of the class AbilitySelectionToken.
 * 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilitySelectionTokenTest extends TestCase
{

	static AbilitySelectionToken pca = new AbilitySelectionToken();

	protected LoadContext context;

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		Globals.emptyLists();
		SettingsHandler.getGame().clearLoadContext();
		context = Globals.getContext();
		
		context.ref.importObject(AbilityCategory.FEAT);
	}

	@Test
	public void testEncodeChoice()
	{
		Ability item = construct("ItemName");
		CategorizedAbilitySelection as =
				new CategorizedAbilitySelection(AbilityCategory.FEAT, item,
					Nature.NORMAL);
		assertEquals("CATEGORY=FEAT|NATURE=NORMAL|ItemName", pca.encodeChoice(as));
		Ability paren = construct("ParenName (test)");
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, paren, Nature.NORMAL);
		assertEquals("CATEGORY=FEAT|NATURE=NORMAL|ParenName (test)", pca.encodeChoice(as));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, sel, Nature.NORMAL, "selection");
		assertEquals("CATEGORY=FEAT|NATURE=NORMAL|ChooseName|selection", pca.encodeChoice(as));
	}

	@Test
	public void testDecodeChoice()
	{
		try
		{
			pca.decodeChoice(context, "Category=Special Ability|ItemName");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		Ability item = construct("ItemName");
		CategorizedAbilitySelection as =
				new CategorizedAbilitySelection(AbilityCategory.FEAT, item,
					Nature.NORMAL);
		assertEquals(as, pca.decodeChoice(context, "CATEGORY=FEAT|NATURE=NORMAL|ItemName"));
		Ability paren = construct("ParenName (test)");
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, paren, Nature.NORMAL);
		assertEquals(as, pca.decodeChoice(context, "CATEGORY=Feat|NATURE=NORMAL|ParenName (test)"));
		Ability sel = construct("ChooseName");
		sel.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		as = new CategorizedAbilitySelection(AbilityCategory.FEAT, sel, Nature.NORMAL, "selection");
		assertEquals(as, pca.decodeChoice(context, "CATEGORY=Feat|NATURE=NORMAL|ChooseName|selection"));
	}

	protected Ability construct(String one)
	{
		Ability obj = context.ref.constructCDOMObject(Ability.class, one);
		context.ref.reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}
}
