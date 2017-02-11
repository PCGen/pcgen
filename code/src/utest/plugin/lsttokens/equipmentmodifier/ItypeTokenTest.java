/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ItypeTokenTest extends
		AbstractTypeSafeListTestCase<EquipmentModifier, String>
{

	static ItypeToken token = new ItypeToken();
	static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<>();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public CDOMLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Override
	public String getConstant(String string)
	{
		return string;
	}

	@Override
	public char getJoinCharacter()
	{
		return '.';
	}

	@Override
	public ListKey<String> getListKey()
	{
		return ListKey.ITEM_TYPES;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	@Override
	public boolean clearsByDefault()
	{
		return true;
	}

	//TODO 514 behavior, to be changed after 5.16?
	@Override
	public void testReplacementInputs() throws PersistenceLayerException
	{
	}

	@Override
	public void testReplacementInputsTwo() throws PersistenceLayerException
	{
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return false;
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}


	@Test
	public void testInvalidInputDouble() throws PersistenceLayerException
	{
		assertFalse(parse("Double"));
		assertNoSideEffects();
	}
}
