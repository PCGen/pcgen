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
package plugin.qualifier.weaponprof;

import java.net.URISyntaxException;

import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.choose.WeaponProficiencyToken;
import plugin.lsttokens.testsupport.AbstractPCQualifierTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;

public class PCQualifierTokenTest extends
		AbstractPCQualifierTokenTestCase<WeaponProf>
{

	private static final WeaponProficiencyToken SUBTOKEN = new WeaponProficiencyToken();

	private static final plugin.qualifier.weaponprof.PCToken PC_TOKEN = new plugin.qualifier.weaponprof.PCToken();

	@BeforeEach
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(PC_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return SUBTOKEN;
	}

	@Override
	public Class<WeaponProf> getTargetClass()
	{
		return WeaponProf.class;
	}

	@Override
	protected void addToPCSet(TransparentPlayerCharacter pc, WeaponProf item)
	{
		pc.weaponProfSet.add(item);
	}

	@Override
	protected Class<? extends QualifierToken<?>> getQualifierClass()
	{
		return plugin.qualifier.weaponprof.PCToken.class;
	}
}
