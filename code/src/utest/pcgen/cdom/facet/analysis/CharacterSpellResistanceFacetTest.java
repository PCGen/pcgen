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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

public class CharacterSpellResistanceFacetTest extends
		AbstractExtractingFacetTest<CDOMObject, Formula>
{

	private CharacterSpellResistanceFacet facet =
			new CharacterSpellResistanceFacet();
	private Formula[] target;
	private CDOMObject[] source;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		CDOMObject cdo1 = new PCTemplate();
		cdo1.setName("Templ");
		CDOMObject cdo2 = new Race();
		cdo2.setName("Race");
		PCStat pcs1 = new PCStat();
		pcs1.setName("Stat1");
		PCStat pcs2 = new PCStat();
		pcs2.setName("Stat2");
		Formula st1 = FormulaFactory.getFormulaFor(4);
		Formula st2 = FormulaFactory.getFormulaFor(2);
		cdo1.put(ObjectKey.SR, new SpellResistance(st1));
		cdo2.put(ObjectKey.SR, new SpellResistance(st2));
		source = new CDOMObject[]{cdo1, cdo2};
		target = new Formula[]{st1, st2};
	}

	@Override
	protected AbstractSourcedListFacet<CharID, Formula> getFacet()
	{
		return facet;
	}

	private static int n = 0;

	@Override
	protected Formula getObject()
	{
		return FormulaFactory.getFormulaFor(n++);
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
	protected Formula getTargetObject(int i)
	{
		return target[i];
	}
}
