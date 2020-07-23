/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.PCTemplate;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.Vision;
import pcgen.util.enumeration.VisionType;

import org.junit.jupiter.api.BeforeEach;

public class VisionFacetTest extends
		AbstractExtractingFacetTest<CDOMObject, QualifiedObject<Vision>>
{

	private VisionFacet facet = new VisionFacet();
	private QualifiedObject<Vision>[] target;
	private CDOMObject[] source;

	@BeforeEach
	@Override
	public void setUp()
	{
		super.setUp();
		CDOMObject cdo1 = new PCTemplate();
		cdo1.setName("Template1");
		CDOMObject cdo2 = new Race();
		cdo2.setName("Race1");
		Vision vision1 = new Vision(VisionType.getVisionType("Normal"),
				FormulaFactory.getFormulaFor(30));
		Vision vision2 = new Vision(VisionType.getVisionType("Darkvision"),
				FormulaFactory.getFormulaFor(20));
		CDOMDirectSingleRef<Vision> ref1 = new CDOMDirectSingleRef<>(vision1);
		SimpleAssociatedObject apo1 = new SimpleAssociatedObject();
		cdo1.putToList(Vision.VISIONLIST, ref1, apo1);
		CDOMDirectSingleRef<Vision> ref2 = new CDOMDirectSingleRef<>(vision2);
		SimpleAssociatedObject apo2 = new SimpleAssociatedObject();
		cdo2.putToList(Vision.VISIONLIST, ref2, apo2);
		QualifiedObject<Vision> st1 = new QualifiedObject<>(vision1);
		QualifiedObject<Vision> st2 = new QualifiedObject<>(vision2);
		source = new CDOMObject[]{cdo1, cdo2};
		target = new QualifiedObject[]{st1, st2};
	}

	@Override
	protected AbstractSourcedListFacet<CharID, QualifiedObject<Vision>> getFacet()
	{
		return facet;
	}

	public static int n = 0;

	@Override
	protected QualifiedObject<Vision> getObject()
	{
		return new QualifiedObject<>(new Vision(VisionType
                .getVisionType("Normal" + n++), FormulaFactory.getFormulaFor(30)));
	}

	@Override
	protected CDOMObject getContainingObject(int i)
	{
		return source[i];
	}

	@Override
	protected DataFacetChangeListener<CharID, CDOMObject> getListener()
	{
		return facet;
	}

	@Override
	protected QualifiedObject<Vision> getTargetObject(int i)
	{
		return target[i];
	}
}
