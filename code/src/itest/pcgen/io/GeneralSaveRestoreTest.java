/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.io;

import java.util.List;

import pcgen.cdom.content.CNAbility;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.io.testsupport.AbstractSaveRestoreTest;
import pcgen.persistence.PersistenceLayerException;

import org.junit.Assert;
import plugin.lsttokens.testsupport.TokenRegistration;

public class GeneralSaveRestoreTest extends AbstractSaveRestoreTest
{

	public void testTemplateFeat()
	{
		//Represents CODE-2547
		TokenRegistration.register(new plugin.lsttokens.deprecated.TemplateFeatToken());
		TokenRegistration.register(new plugin.lsttokens.ability.StackToken());
		TokenRegistration.register(new plugin.exporttokens.deprecated.TemplateToken());
		Language lang = context.getReferenceContext().constructCDOMObject(Language.class, "English");
		Ability a = context.getReferenceContext().constructCDOMObject(Ability.class, "Ab");
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, a);
		PCTemplate pct = context.getReferenceContext().constructCDOMObject(PCTemplate.class, "Templ");
		try
		{
			Assert.assertTrue(context.processToken(a, "MULT", "YES"));
			Assert.assertTrue(context.processToken(a, "STACK", "YES"));
			Assert.assertTrue(context.processToken(a, "CHOOSE", "LANG|English"));
			Assert.assertTrue(context.processToken(a, "AUTO", "LANG|%LIST"));
			Assert.assertTrue(context.processToken(pct, "FEAT", "Ab"));
		}
		catch (PersistenceLayerException e)
		{
			Assert.fail (e.getMessage());
		}
		finishLoad();
		pc.addTemplate(pct);
		Assert.assertTrue(pc.hasLanguage(lang));
		runRoundRobin(null);
		Assert.assertTrue(reloadedPC.hasLanguage(lang));
		List<CNAbility> cnaList = pc.getMatchingCNAbilities(a);
		Assert.assertEquals(1, cnaList.size());
		List<String> assocs = pc.getAssociationList(cnaList.get(0));
		Assert.assertEquals(1, assocs.size());
		Assert.assertEquals("English", assocs.get(0));
		cnaList = reloadedPC.getMatchingCNAbilities(a);
		Assert.assertEquals(1, cnaList.size());
		assocs = reloadedPC.getAssociationList(cnaList.get(0));
		Assert.assertEquals(1, assocs.size());
		Assert.assertEquals("English", assocs.get(0));
		Assert.assertEquals("Ab(English)", ExportHandler.getTokenString(pc, "TEMPLATE.0.FEAT"));
		Assert.assertEquals("Ab(English)", ExportHandler.getTokenString(reloadedPC, "TEMPLATE.0.FEAT"));
		reloadedPC.removeTemplate(pct);
		Assert.assertFalse(reloadedPC.hasLanguage(lang));
	}
	
	
}
