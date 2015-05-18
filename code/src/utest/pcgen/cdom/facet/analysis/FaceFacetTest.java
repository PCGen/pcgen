/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.facet.analysis;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.analysis.FaceFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

public class FaceFacetTest extends TestCase
{
	private static final Double SIZE_3_2 = new Point2D.Double(3, 2);
	private static final Double SIZE_10_5 = new Point2D.Double(10, 5);
	private static final Point2D.Double SIZE_11_12 = new Point2D.Double(11, 12);

	/*
	 * NOTE: This is not literal unit testing - it is leveraging the existing
	 * RaceFacet and TemplateFacet frameworks. This class trusts that
	 * RaceFacetTest and TemplateFacetTest has fully vetted RaceFacet and
	 * TemplateFacet. PLEASE ensure all tests there are working before
	 * investigating tests here.
	 */
	private CharID id;
	private CharID altid;
	private FaceFacet facet;
	private RaceFacet rfacet = new RaceFacet();
	private TemplateFacet tfacet = new TemplateFacet();

	@Override
	public void setUp() throws Exception
	{
		facet = new FaceFacet();
		super.setUp();
		facet.setRaceFacet(rfacet);
		facet.setTemplateFacet(tfacet);
		DataSetID cid = DataSetID.getID();
		id = CharID.getID(cid);
		altid = CharID.getID(cid);
	}

	@Test
	public void testRaceTypeUnset5Square()
	{
		assertEquals(new Point2D.Double(5, 0), facet.getFace(id));
	}

	@Test
	public void testWithNothingInRace()
	{
		rfacet.set(id, new Race());
		assertEquals(new Point2D.Double(5, 0), facet.getFace(id));
	}

	@Test
	public void testAvoidPollution()
	{
		Race r = new Race();
		r.put(IntegerKey.LEGS, 5);
		rfacet.set(id, r);
		assertEquals(new Point2D.Double(5, 0), facet.getFace(altid));
	}

	@Test
	public void testGetFromRace()
	{
		Race r = new Race();
		r.put(ObjectKey.FACE_WIDTH, new BigDecimal(10));
		r.put(ObjectKey.FACE_HEIGHT, new BigDecimal(5));
		rfacet.set(id, r);
		assertEquals(SIZE_10_5, facet.getFace(id));
		rfacet.remove(id);
		assertEquals(new Point2D.Double(5, 0), facet.getFace(id));
	}

	@Test
	public void testGetFromTemplate()
	{
		rfacet.set(id, new Race());
		PCTemplate t = new PCTemplate();
		t.put(ObjectKey.FACE_WIDTH, new BigDecimal(10));
		t.put(ObjectKey.FACE_HEIGHT, new BigDecimal(5));
		tfacet.add(id, t, this);
		assertEquals(SIZE_10_5, facet.getFace(id));
		tfacet.remove(id, t, this);
		assertEquals(new Point2D.Double(5, 0), facet.getFace(id));
	}

	@Test
	public void testGetFromTemplateSecondOverrides()
	{
		Race r = new Race();
		r.put(ObjectKey.FACE_WIDTH, new BigDecimal(10));
		r.put(ObjectKey.FACE_HEIGHT, new BigDecimal(5));
		rfacet.set(id, r);
		assertEquals(SIZE_10_5, facet.getFace(id));
		PCTemplate t = new PCTemplate();
		t.setName("PCT");
		t.put(ObjectKey.FACE_WIDTH, new BigDecimal(11));
		t.put(ObjectKey.FACE_HEIGHT, new BigDecimal(12));
		tfacet.add(id, t, this);
		assertEquals(SIZE_11_12, facet.getFace(id));
		PCTemplate t5 = new PCTemplate();
		t5.setName("Other");
		t5.put(ObjectKey.FACE_WIDTH, new BigDecimal(3));
		t5.put(ObjectKey.FACE_HEIGHT, new BigDecimal(2));
		tfacet.add(id, t5, this);
		assertEquals(SIZE_3_2, facet.getFace(id));
		tfacet.remove(id, t, this);
		assertEquals(SIZE_3_2, facet.getFace(id));
		tfacet.add(id, t, this);
		assertEquals(SIZE_11_12, facet.getFace(id));
		tfacet.remove(id, t, this);
		assertEquals(SIZE_3_2, facet.getFace(id));
		tfacet.remove(id, t5, this);
		assertEquals(SIZE_10_5, facet.getFace(id));
	}
}
