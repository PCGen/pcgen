/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.wrapper;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.output.base.PCGenObjectWrapper;
import pcgen.output.base.SimpleWrapperLibrary;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A CNAbilitySelectionWrapper is an ObjectWrapper capable of producing a
 * TemplateModel for CDOMReference objects.
 */
public class CDOMReferenceWrapper implements PCGenObjectWrapper
{
	private static final ObjectWrapperFacet WRAPPER_FACET = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	@Override
	public TemplateModel wrap(CharID id, Object o) throws TemplateModelException
	{
		if (o instanceof CDOMSingleRef<?> ref)
		{
			return WRAPPER_FACET.wrap(id, ref.get());
		}
		if (o instanceof CDOMReference<?> ref)
		{
			/*
			 * TODO is this correct? This would produce TYPE=Blah in some cases,
			 * and we may want to spell them out?? Shouldn't both be an option?
			 * Need a Model?
			 */
			String lstFormat = ref.getLSTformat(true);
			return SimpleWrapperLibrary.wrap(lstFormat);
		}
		throw new TemplateModelException("Object was not a CDOMReference");
	}
}
