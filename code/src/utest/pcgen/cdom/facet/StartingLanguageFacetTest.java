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
package pcgen.cdom.facet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.BeforeEach;

public class StartingLanguageFacetTest extends
		AbstractExtractingFacetTest<CDOMObject, Language>
{

	private StartingLanguageFacet facet = new StartingLanguageFacet();
	private Language[] target;
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
		Language l1 = new Language();
		l1.setName("Language1");
		Language l2 = new Language();
		l2.setName("Language2");
		CDOMDirectSingleRef<Language> ref1 = new CDOMDirectSingleRef<>(l1);
		SimpleAssociatedObject apo1 = new SimpleAssociatedObject();
		cdo1.putToList(Language.STARTING_LIST, ref1, apo1);
		CDOMDirectSingleRef<Language> ref2 = new CDOMDirectSingleRef<>(l2);
		SimpleAssociatedObject apo2 = new SimpleAssociatedObject();
		cdo2.putToList(Language.STARTING_LIST, ref2, apo2);
		source = new CDOMObject[]{cdo1, cdo2};
		target = new Language[]{l1, l2};
	}

	@Override
	protected AbstractSourcedListFacet<CharID, Language> getFacet()
	{
		return facet;
	}

	public static int n = 0;

	@Override
	protected Language getObject()
	{
		Language wp = new Language();
		wp.setName("WP" + n++);
		return wp;
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
	protected Language getTargetObject(int i)
	{
		return target[i];
	}
}
