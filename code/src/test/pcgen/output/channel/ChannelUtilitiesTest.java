/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.channel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.formula.VariableChannel;

import plugin.function.testsupport.AbstractFormulaTestCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ChannelUtilities}, in particular that reading a channel whose
 * variable was never asserted legal (e.g. a code control feature disabled in the
 * game mode, as with Alignment/Deity in d20 Modern) returns null rather than
 * throwing. See the Modern set-race/save crash.
 */
class ChannelUtilitiesTest extends AbstractFormulaTestCase
{

	private final ScopeFacet scopeFacet = FacetLibrary.getFacet(ScopeFacet.class);
	private final VariableStoreFacet variableStoreFacet =
			FacetLibrary.getFacet(VariableStoreFacet.class);
	private final SolverManagerFacet solverManagerFacet =
			FacetLibrary.getFacet(SolverManagerFacet.class);
	private CharID id;

	@BeforeEach
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		id = CharID.getID(context.getDataSetID());
		scopeFacet.set(id, getScopeInstanceFactory());
		variableStoreFacet.set(id, getVariableStore());
		solverManagerFacet.set(id,
			context.getVariableContext().generateSolverManager(getVariableStore()));
	}

	@Test
	void testReadDisabledChannelReturnsNull()
	{
		// "Deity" is never asserted legal here, mirroring DOMAINFEATURE:NO in Modern.
		assertNull(ChannelUtilities.readGlobalChannel(id, "Deity"));
	}

	@Test
	void testReadLegalChannelStillWorks()
	{
		context.getVariableContext().assertLegalVariableID(
			ChannelUtilities.createVarName("STR"), getGlobalScope(), numberManager);
		VariableChannel<Number> strChannel = (VariableChannel<Number>) context
			.getVariableContext().getGlobalChannel(id, "STR");
		assertEquals(0, ChannelUtilities.readGlobalChannel(id, "STR"));
		strChannel.set(2);
		assertEquals(2, ChannelUtilities.readGlobalChannel(id, "STR"));
	}
}
