/*
 * Copyright (c) Thomas Parker, 2019.
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
package pcgen.cdom.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.inst.SimpleScopeInstance;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.VariableChangeEvent;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.facade.util.event.ReferenceEvent;
import plugin.function.testsupport.TransparentFormatManager;

import org.junit.jupiter.api.Test;

public class BridgeListenerTest
{

	@SuppressWarnings("unused")
	@Test
	void testBadConstructionFirstArg()
	{
		AbstractSourcedListFacet<CharID, PCGenScoped> target = new Target();
		assertThrows(NullPointerException.class, () -> new BridgeListener(null, target, this));
	}

	@SuppressWarnings("unused")
	@Test
	void testBadConstructionSecondArg()
	{
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		assertThrows(NullPointerException.class, () -> new BridgeListener(id, null, this));
	}

	@Test
	void testEqualsHash()
	{
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		AbstractSourcedListFacet<CharID, PCGenScoped> target = new Target();
		BridgeListener bridge1 = new BridgeListener(id, target, this);
		BridgeListener bridge2 = new BridgeListener(id, target, this);
		assertEquals(bridge1.hashCode(), bridge2.hashCode());
		assertTrue(bridge1.equals(bridge2));
		assertTrue(bridge2.equals(bridge1));
		//Not
		CharID altID = CharID.getID(dsID);
		BridgeListener bridge3 = new BridgeListener(altID, target, this);
		assertNotEquals(bridge1.hashCode(), bridge3.hashCode());
		assertFalse(bridge1.equals(bridge3));
		assertFalse(bridge3.equals(bridge1));
		//Should not fail with an exception (should check before casting)
		assertFalse(bridge3.equals(1));
	}

	@Test
	void testVariableBasic()
	{
		Race owner = new Race();
		owner.setName("Race");
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		AbstractSourcedListFacet<CharID, PCGenScoped> target = new Target();
		BridgeListener bridge = new BridgeListener(id, target, this);
		GlobalPCScope scope = new GlobalPCScope();
		ScopeInstance instance =
				new SimpleScopeInstance(Optional.empty(), scope, owner);
		TransparentFormatManager<PCTemplate> formatManager =
				new TransparentFormatManager<PCTemplate>(PCTemplate.class,
					"PCTEMPLATE");
		VariableID<PCTemplate> varID =
				new VariableID<>(instance, formatManager, "VarName");
		PCTemplate t1 = new PCTemplate();
		t1.setName("Template1");
		VariableChangeEvent vce = new VariableChangeEvent<>(this, varID, null, t1);
		assertEquals(0, target.getCount(id));
		bridge.variableChanged(vce);
		assertEquals(1, target.getCount(id));
		assertEquals(t1, target.getSet(id).iterator().next());

		PCTemplate t2 = new PCTemplate();
		t2.setName("Template2");
		VariableChangeEvent vce2 = new VariableChangeEvent<>(this, varID, t1, t2);
		bridge.variableChanged(vce2);
		assertEquals(1, target.getCount(id));
		assertEquals(t2, target.getSet(id).iterator().next());
	}

	@Test
	void testVariableArray()
	{
		Race owner = new Race();
		owner.setName("Race");
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		AbstractSourcedListFacet<CharID, PCGenScoped> target = new Target();
		BridgeListener bridge = new BridgeListener(id, target, this);
		GlobalPCScope scope = new GlobalPCScope();
		ScopeInstance instance =
				new SimpleScopeInstance(Optional.empty(), scope, owner);
		TransparentFormatManager<PCTemplate[]> formatManager =
				new TransparentFormatManager<PCTemplate[]>(PCTemplate[].class,
					"PCTEMPLATE");
		VariableID<PCTemplate[]> varID =
				new VariableID<>(instance, formatManager, "VarName");
		PCTemplate t1 = new PCTemplate();
		t1.setName("Template1");
		VariableChangeEvent vce = new VariableChangeEvent<>(this, varID,
			new PCTemplate[]{}, new PCTemplate[]{t1});
		assertEquals(0, target.getCount(id));
		bridge.variableChanged(vce);
		assertEquals(1, target.getCount(id));
		assertEquals(t1, target.getSet(id).iterator().next());

		PCTemplate t2 = new PCTemplate();
		t2.setName("Template2");
		VariableChangeEvent vce2 = new VariableChangeEvent<>(this, varID,
			new PCTemplate[]{t1}, new PCTemplate[]{t1, t2});
		bridge.variableChanged(vce2);
		assertEquals(2, target.getCount(id));
		assertTrue(target.getSet(id).contains(t1));
		assertTrue(target.getSet(id).contains(t2));
		
		VariableChangeEvent vce3 = new VariableChangeEvent<>(this, varID,
			new PCTemplate[]{t1, t2}, new PCTemplate[]{t2});
		bridge.variableChanged(vce3);
		assertEquals(1, target.getCount(id));
		assertTrue(target.getSet(id).contains(t2));
	}

	@Test
	void testReferenceBasic()
	{
		Race owner = new Race();
		owner.setName("Race");
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		AbstractSourcedListFacet<CharID, PCGenScoped> target = new Target();
		BridgeListener bridge = new BridgeListener(id, target, this);
		PCTemplate t1 = new PCTemplate();
		t1.setName("Template1");
		ReferenceEvent<Object> re = new ReferenceEvent<>(this, null, t1);
		assertEquals(0, target.getCount(id));
		bridge.referenceChanged(re);
		assertEquals(1, target.getCount(id));
		assertEquals(t1, target.getSet(id).iterator().next());

		PCTemplate t2 = new PCTemplate();
		t2.setName("Template2");
		ReferenceEvent<Object> re2 = new ReferenceEvent<>(this, t1, t2);
		bridge.referenceChanged(re2);
		assertEquals(1, target.getCount(id));
		assertEquals(t2, target.getSet(id).iterator().next());
	}

	@Test
	void testReferenceArray()
	{
		Race owner = new Race();
		owner.setName("Race");
		DataSetID dsID = DataSetID.getID();
		CharID id = CharID.getID(dsID);
		AbstractSourcedListFacet<CharID, PCGenScoped> target = new Target();
		BridgeListener bridge = new BridgeListener(id, target, this);
		PCTemplate t1 = new PCTemplate();
		t1.setName("Template1");
		ReferenceEvent<Object> re = new ReferenceEvent<>(this,
			new PCTemplate[]{}, new PCTemplate[]{t1});
		assertEquals(0, target.getCount(id));
		bridge.referenceChanged(re);
		assertEquals(1, target.getCount(id));
		assertEquals(t1, target.getSet(id).iterator().next());

		PCTemplate t2 = new PCTemplate();
		t2.setName("Template2");
		ReferenceEvent<Object> re2 = new ReferenceEvent<>(this, 
			new PCTemplate[]{t1}, new PCTemplate[]{t1, t2});
		bridge.referenceChanged(re2);
		assertEquals(2, target.getCount(id));
		assertTrue(target.getSet(id).contains(t1));
		assertTrue(target.getSet(id).contains(t2));
		
		ReferenceEvent<Object> re3 = new ReferenceEvent<>(this, 
			new PCTemplate[]{t1, t2}, new PCTemplate[]{t2});
		bridge.referenceChanged(re3);
		assertEquals(1, target.getCount(id));
		assertTrue(target.getSet(id).contains(t2));
	}

	private static class Target extends AbstractSourcedListFacet<CharID, PCGenScoped>
	{

	}
}
