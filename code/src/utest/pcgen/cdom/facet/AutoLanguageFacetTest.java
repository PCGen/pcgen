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
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

public class AutoLanguageFacetTest extends
		AbstractExtractingFacetTest<CDOMObject, Language>
{

	private AutoLanguageFacet facet = new AutoLanguageFacet();
	private Language[] target;
	private CDOMObject[] source;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		CDOMObject cdo1 = new PCTemplate();
		cdo1.setName("Template1");
		CDOMObject cdo2 = new Race();
		cdo2.setName("Race1");
		Language st1 = new Language();
		st1.setName("Prof1");
		Language st2 = new Language();
		st1.setName("Prof2");
		cdo1.addToListFor(ListKey.AUTO_LANGUAGE, CDOMDirectSingleRef.getRef(st1));
		cdo2.addToListFor(ListKey.AUTO_LANGUAGES, CDOMDirectSingleRef.getRef(st2));
		source = new CDOMObject[]{cdo1, cdo2};
		target = new Language[]{st1, st2};
	}

	@Override
	protected AbstractSourcedListFacet<Language> getFacet()
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
	protected DataFacetChangeListener<CDOMObject> getListener()
	{
		return facet;
	}

	@Override
	protected Language getTargetObject(int i)
	{
		return target[i];
	}

}
