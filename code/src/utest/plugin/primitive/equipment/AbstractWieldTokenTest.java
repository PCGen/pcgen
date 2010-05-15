/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.equipment;

import java.net.URISyntaxException;
import java.util.Arrays;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Deity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.EquipmentToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

public class AbstractWieldTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Deity>
{

	static ChooseLst token = new ChooseLst();
	static EquipmentToken subtoken = new EquipmentToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	private static final WieldCategoryToken WIELD_PRIMITIVE = new WieldCategoryToken();

	public AbstractWieldTokenTest()
	{
		super("WIELD", "Light", Arrays.asList(new String[] { "Light.1 Handed",
				"OneHanded" }));
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(WIELD_PRIMITIVE);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Deity> getTargetClass()
	{
		return Deity.class;
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

}