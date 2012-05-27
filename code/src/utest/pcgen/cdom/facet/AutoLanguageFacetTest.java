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

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.testsupport.AbstractAutoTokenTestCase;

public class AutoLanguageFacetTest extends AbstractAutoTokenTestCase<Language> {

	private AutoLanguageFacet facet = new AutoLanguageFacet();
	private Language[] target;
	private CDOMObject[] source;
	static LangToken subtoken = new LangToken();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
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
		cdo1.addToListFor(ListKey.AUTO_LANGUAGE, new QualifiedObject(CDOMDirectSingleRef.getRef(st1)));
		cdo2.addToListFor(ListKey.AUTO_LANGUAGES, new QualifiedObject(CDOMDirectSingleRef.getRef(st2)));
		source = new CDOMObject[] { cdo1, cdo2 };
		target = new Language[] { st1, st2 };
	}

	public static int n = 0;

	@Override
	protected void loadProf(CDOMSingleRef<Language> ref)
	{
		primaryProf.addToListFor(ListKey.AUTO_LANGUAGES, new QualifiedObject<CDOMReference<Language>>(ref));
	}

	@Override
	protected void loadTypeProf(String... types)
	{
		CDOMGroupRef<Language> ref = primaryContext.ref.getCDOMTypeReference(Language.class, types);
		primaryProf.addToListFor(ListKey.AUTO_LANGUAGES, new QualifiedObject<CDOMReference<Language>>(ref));
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Language> getTargetClass()
	{
		return Language.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Override
	protected void loadAllReference()
	{
		throw new UnsupportedOperationException();
	}

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	protected ChooseResultActor getActor()
	{
		return subtoken;
	}

	@Override
	protected boolean allowsPrerequisite()
	{
		return true;
	}
}
