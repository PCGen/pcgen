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
package plugin.qualifier.deity;

import java.net.URISyntaxException;

import pcgen.ControlTestSupport;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.output.channel.compat.DeityCompat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;

import plugin.lsttokens.choose.DeityToken;
import plugin.lsttokens.testsupport.AbstractPCQualifierTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;
import util.FormatSupport;

public class PCQualifierTokenTest extends
		AbstractPCQualifierTokenTestCase<Deity>
{

	private static final DeityToken SUBTOKEN = new DeityToken();

	private static final plugin.qualifier.deity.PCToken PC_TOKEN =
			new plugin.qualifier.deity.PCToken();

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
	public Class<Deity> getTargetClass()
	{
		return Deity.class;
	}

	@Override
	protected boolean typeAllowsMult()
	{
		return false;
	}

	@Override
	protected boolean typeHasDefault()
	{
		return true;
	}

	@Override
	protected void addToPCSet(TransparentPlayerCharacter pc, Deity item)
	{
		DeityCompat.setCurrentDeity(pc.getCharID(), item);
	}

	@Override
	protected Class<? extends QualifierToken<?>> getQualifierClass()
	{
		return plugin.qualifier.deity.PCToken.class;
	}

	@Override
	protected void additionalSetup(LoadContext context)
	{
		ControlTestSupport.enableFeature(context, CControl.DOMAINFEATURE);
		Deity none = new Deity();
		none.setName("None");
		AbstractReferenceContext ref = context.getReferenceContext();
		ref.importObject(none);
		FormatSupport.addNoneAsDefault(context,
			ref.getManufacturer(Deity.class));
		super.additionalSetup(context);
	}
}
