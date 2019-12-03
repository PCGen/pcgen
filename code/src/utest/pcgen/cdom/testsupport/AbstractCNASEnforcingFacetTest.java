package pcgen.cdom.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractCNASEnforcingFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.CNAbilitySelectionUtilities;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractCNASEnforcingFacetTest
{

	private CharID id;
	private CharID altid;
	private AbilityCategory feat;
	private AbilityCategory fighterfeat;
	private AbilityCategory specialty;
	private Ability nomult;
	private Ability multyes;
	private Ability stackyes;
	private Ability othernomult;

	private final Listener listener = new Listener();

	private static class Listener implements
			DataFacetChangeListener<CharID, CNAbilitySelection>
	{

		private int addEventCount;
		private int removeEventCount;

		@Override
		public void dataAdded(
			DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
		{
			addEventCount++;
		}

		@Override
		public void dataRemoved(
			DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
		{
			removeEventCount++;
		}

	}

	protected void assertEventCount(int a, int r)
	{
		assertEquals(a, listener.addEventCount);
		assertEquals(r, listener.removeEventCount);
	}

	@BeforeEach
	void setUp() {
		getFacet().addDataFacetChangeListener(listener);
		CNAbilityFactory.reset();
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
		feat = new AbilityCategory();
		feat.setName("FEAT");
		fighterfeat = new AbilityCategory();
		fighterfeat.setName("Fighter Feat");
		fighterfeat.setAbilityCategory(CDOMDirectSingleRef.getRef(feat));
		specialty = new AbilityCategory();
		specialty.setName("Specialty");
		nomult = new Ability();
		nomult.setName("NoMult");
		nomult.setCDOMCategory(feat);
		multyes = new Ability();
		multyes.setName("MultYes");
		multyes.setCDOMCategory(feat);
		multyes.put(ObjectKey.MULTIPLE_ALLOWED, true);
		stackyes = new Ability();
		stackyes.setName("MultYes");
		stackyes.setCDOMCategory(feat);
		stackyes.put(ObjectKey.MULTIPLE_ALLOWED, true);
		stackyes.put(ObjectKey.STACKS, true);
		othernomult = new Ability();
		othernomult.setName("OtherNoMult");
		othernomult.setCDOMCategory(feat);
	}

	@Test
	public void testTypeUnsetZeroCount()
	{
		assertEquals(0, getFacet().getCount(id));
	}

	@Test
	public void testTypeUnsetEmpty()
	{
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testTypeUnsetEmptySet()
	{
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
	}

	@Test
	public void testTypeAddNull()
	{
		Object source1 = new Object();
		assertThrows(NullPointerException.class,
				() -> getFacet().add(id, null, source1)
		);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeAddNullID()
	{
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		Object source1 = new Object();
		assertThrows(NullPointerException.class,
				() -> getFacet().add(null, getObject(), source1)
		);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeAddNullSource()
	{
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		assertThrows(NullPointerException.class,
				() -> getFacet().add(id, null, null)
		);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeAddSingleGet()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getObject();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// No cross-pollution
		assertEquals(0, getFacet().getCount(altid));
		assertTrue(getFacet().isEmpty(altid));
		assertNotNull(getFacet().getSet(altid));
		assertTrue(getFacet().getSet(altid).isEmpty());
	}

	@Test
	public void testTypeAddSingleSourceTwiceGet()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getObject();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddSingleTwiceTwoSourceGet()
	{
		Object source1 = new Object();
		Object source2 = new Object();
		CNAbilitySelection t1 = getObject();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Add same, still only once in set (and only one event)
		getFacet().add(id, t1, source2);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddMultGet()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getMultObject("English");
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<CNAbilitySelection> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertEquals(t1, setofone.iterator().next());
		assertEventCount(1, 0);
		CNAbilitySelection t2 = getMultObject("German");
		getFacet().add(id, t2, source1);
		assertEquals(2, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<CNAbilitySelection> setoftwo = getFacet().getSet(id);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		assertEventCount(2, 0);
	}

	@Test
	public void testTypeRemoveUseless()
	{
		//Remove to try to avoid any event being formed
		getFacet().removeDataFacetChangeListener(listener);
		Object source1 = new Object();
		assertThrows(NullPointerException.class,
				() -> getFacet().add(null, getObject(), source1)
		);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
		assertEventCount(0, 0);
	}

	@Test
	public void testTypeRemoveUselessSource()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getObject();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		Object source2 = new Object();
		getFacet().remove(id, t1, source2);
		// No change (wrong source)
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddSingleRemove()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getObject();
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Remove
		getFacet().remove(id, t1, source1);
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertTrue(getFacet().getSet(id).isEmpty());
		assertEventCount(1, 1);
	}

	@Test
	public void testTypeAddUselessRemove()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getMultObject("English");
		getFacet().add(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
		// Useless Remove
		getFacet().remove(id, getMultObject("German"), source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertNotNull(getFacet().getSet(id));
		assertEquals(1, getFacet().getSet(id).size());
		assertEquals(t1, getFacet().getSet(id).iterator().next());
		assertEventCount(1, 0);
	}

	@Test
	public void testTypeAddMultRemove()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getMultObject("English");
		CNAbilitySelection t2 = getMultObject("German");
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		getFacet().remove(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<CNAbilitySelection> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));
		assertEventCount(2, 1);
	}

	@Test
	public void testGetSetIndependence()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getObject();
		CNAbilitySelection t2 = getObject();
		getFacet().add(id, t1, source1);
		Collection<CNAbilitySelection> set = getFacet().getSet(id);
		try
		{
			set.add(t2);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.remove(t1);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		List<CNAbilitySelection> pct = new ArrayList<>();
		pct.add(t1);
		pct.add(t2);
		try
		{
			set.addAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.removeAll(pct);
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		try
		{
			set.retainAll(new ArrayList<CNAbilitySelection>());
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
		getFacet().add(id, t1, source1);
		try
		{
			set.clear();
			// If we can modify, then make sure it's independent of the facet
			assertEquals(1, getFacet().getCount(id));
			assertFalse(getFacet().isEmpty(id));
			assertNotNull(getFacet().getSet(id));
			assertEquals(1, getFacet().getSet(id).size());
			assertEquals(t1, getFacet().getSet(id).iterator().next());
		}
		catch (UnsupportedOperationException e)
		{
			// This is ok too
		}
	}

	@Test
	public void testCopyContentsNone()
	{
		getFacet().copyContents(altid, id);
		testTypeUnsetZeroCount();
		testTypeUnsetEmpty();
		testTypeUnsetEmptySet();
	}

	@Test
	public void testCopyContents()
	{
		Object source1 = new Object();
		CNAbilitySelection t1 = getMultObject("English");
		CNAbilitySelection t2 = getMultObject("German");
		getFacet().add(id, t1, source1);
		getFacet().add(id, t2, source1);
		assertEquals(2, getFacet().getCount(id));
		assertEquals(0, getFacet().getCount(altid));
		getFacet().copyContents(id, altid);
		assertEquals(2, getFacet().getCount(altid));
		assertFalse(getFacet().isEmpty(altid));
		Collection<CNAbilitySelection> setoftwo = getFacet().getSet(altid);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove independence (remove from id)
		getFacet().remove(id, t1, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		Collection<CNAbilitySelection> setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));

		assertEquals(2, getFacet().getCount(altid));
		assertFalse(getFacet().isEmpty(altid));
		setoftwo = getFacet().getSet(altid);
		assertNotNull(setoftwo);
		assertEquals(2, setoftwo.size());
		assertTrue(setoftwo.contains(t1));
		assertTrue(setoftwo.contains(t2));
		// Prove Independence (remove from altid)

		getFacet().remove(altid, t2, source1);
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		setofone = getFacet().getSet(id);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t2));

		assertEquals(1, getFacet().getCount(altid));
		assertFalse(getFacet().isEmpty(altid));
		setofone = getFacet().getSet(altid);
		assertNotNull(setofone);
		assertEquals(1, setofone.size());
		assertTrue(setofone.contains(t1));
	}

	@Test
	public void testDifferentCategory()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility nomultCNA = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		Ability trickster = new Ability();
		trickster.setName("NoMult");
		trickster.setCDOMCategory(specialty);
		CNAbility othernomultCNA =
				CNAbilityFactory.getCNAbility(specialty, Nature.NORMAL, trickster);
		CNAbilitySelection cnas1 = new CNAbilitySelection(nomultCNA);
		CNAbilitySelection cnas2 = new CNAbilitySelection(othernomultCNA);
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().add(id, cnas2, source1));
		assertEquals(2, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		//Useless remove
		assertFalse(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//Now a real remove
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		//Useless remove
		assertFalse(getFacet().remove(id, cnas2, source1));
	}

	@Test
	public void testDifferentAbility()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility nomultCNA = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		CNAbility othernomultCNA =
				CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, othernomult);
		CNAbilitySelection cnas1 = new CNAbilitySelection(nomultCNA);
		CNAbilitySelection cnas2 = new CNAbilitySelection(othernomultCNA);
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().add(id, cnas2, source1));
		assertEquals(2, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testDifferentNature()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility normal = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		CNAbility virtual = CNAbilityFactory.getCNAbility(feat, Nature.VIRTUAL, nomult);
		CNAbilitySelection cnas1 = new CNAbilitySelection(normal);
		CNAbilitySelection cnas2 = new CNAbilitySelection(virtual);
		assertFalse(CNAbilitySelectionUtilities.canCoExist(cnas1, cnas2));
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//LIFO
		assertFalse(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//FIFO
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testDetectSameParentCategory()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, nomult);
		CNAbility n2 = CNAbilityFactory.getCNAbility(fighterfeat, Nature.NORMAL, nomult);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1);
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2);
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//LIFO
		assertFalse(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//FIFO
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		CNAbility virtual = CNAbilityFactory.getCNAbility(fighterfeat, Nature.VIRTUAL, nomult);
		CNAbilitySelection cnas3 = new CNAbilitySelection(virtual);
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas3, source1));
		assertEquals(1, getFacet().getCount(id));
		//LIFO
		assertFalse(getFacet().remove(id, cnas3, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas3, source1));
		assertEquals(1, getFacet().getCount(id));
		//FIFO
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas3, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		//Three, Midoutfirst
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertFalse(getFacet().add(id, cnas3, source1));
		assertFalse(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas3, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		//Test different source (LIFO)
		Object source2 = new Object();
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().add(id, cnas1, source2));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().remove(id, cnas1, source2));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		//Test different source (FIFO - counter-intuitive)
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().add(id, cnas1, source2));
		assertEquals(1, getFacet().getCount(id));
		//Note: This is FALSE since the CNAS didn't change - don't want to fire an event
		assertFalse(getFacet().remove(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas1, source2));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testDetectSameSelection()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		//Note: This is also an identity tests
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "English");
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//LIFO
		assertFalse(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().add(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		//FIFO
		//This assertFalse is the identity test, since it *actually* removes cnas2 and thus returns false
		assertFalse(getFacet().remove(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testDifferentSelection()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "German");
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().add(id, cnas2, source1));
		assertEquals(2, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testDifferentSelectionAndNature()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility normal = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, multyes);
		CNAbility virtual = CNAbilityFactory.getCNAbility(feat, Nature.VIRTUAL, multyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(normal, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(virtual, "German");
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().add(id, cnas2, source1));
		assertEquals(2, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testDifferentSelectionStack()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "German");
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().add(id, cnas2, source1));
		assertEquals(2, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testStack()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "English");
		assertTrue(getFacet().add(id, cnas1, source1));
		assertFalse(getFacet().isEmpty(id));
		assertEquals(1, getFacet().getCount(id));
		assertTrue(getFacet().add(id, cnas2, source1));
		assertEquals(2, getFacet().getCount(id));
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(1, getFacet().getCount(id));
		assertFalse(getFacet().isEmpty(id));
		assertTrue(getFacet().remove(id, cnas1, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	@Test
	public void testIdentity()
	{
		Object source1 = new Object();
		assertTrue(getFacet().isEmpty(id));
		assertEquals(0, getFacet().getCount(id));
		CNAbility n1 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbility n2 = CNAbilityFactory.getCNAbility(feat, Nature.NORMAL, stackyes);
		CNAbility ff = CNAbilityFactory.getCNAbility(fighterfeat, Nature.NORMAL, stackyes);
		CNAbility virtual = CNAbilityFactory.getCNAbility(feat, Nature.VIRTUAL, stackyes);
		CNAbilitySelection cnas1 = new CNAbilitySelection(n1, "English");
		CNAbilitySelection cnas2 = new CNAbilitySelection(n2, "English");
		CNAbilitySelection cnas3 = new CNAbilitySelection(virtual, "English");
		CNAbilitySelection cnas4 = new CNAbilitySelection(ff, "English");
		//Check identity is .equals not ==
		assertTrue(getFacet().add(id, cnas1, source1));
		assertEquals(1, getFacet().getCount(id));
		//Avoid false positives;
		assertFalse(getFacet().remove(id, cnas3, source1));
		assertFalse(getFacet().remove(id, cnas4, source1));
		//Should succeed (.equals identity)
		assertTrue(getFacet().remove(id, cnas2, source1));
		assertEquals(0, getFacet().getCount(id));
		assertTrue(getFacet().isEmpty(id));
	}

	protected abstract AbstractCNASEnforcingFacet getFacet();

	protected CNAbilitySelection getObject()
	{
		Ability a1 = new Ability();
		a1.setName("Abil");
		a1.setCDOMCategory(BuildUtilities.getFeatCat());
		return new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.VIRTUAL, a1));
	}

	private static CNAbilitySelection getMultObject(String tgt)
	{
		Ability a1 = new Ability();
		a1.setName("Abil");
		a1.setCDOMCategory(BuildUtilities.getFeatCat());
		a1.put(ObjectKey.MULTIPLE_ALLOWED, true);
		return new CNAbilitySelection(CNAbilityFactory
			.getCNAbility(BuildUtilities.getFeatCat(), Nature.VIRTUAL, a1), tgt);
	}

	//TODO Need to test events :/
}
