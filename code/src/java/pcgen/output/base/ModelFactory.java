/*
 * Copyright (c) Thomas Parker, 2015.
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
package pcgen.output.base;

import pcgen.cdom.enumeration.CharID;

import freemarker.template.TemplateModel;

/**
 * A ModelFactory is a class that can generate TemplateModel objects when given
 * a CharID. Typically these will contain a Facet and the combination of the
 * Facet and the CharID is sufficient to produce the necessary information to
 * build a TemplateModel.
 */
public interface ModelFactory
{
	/**
	 * Generates a TemplateModel for the given CharID.
	 * 
	 * @param id
	 *            The CharID for which a TemplateModel should be produced by
	 *            this ModelFactory
	 * @return A TemplateModel produced by this ModelFactory for the given
	 *         CharID
	 */
	//TODO This is reckless, as T is not well enforced and can be avoided (see FactModelFactory)
	//TODO The usage in PREFACT/PREFACTSET is also reckless
    <T extends TemplateModel & Iterable<?>> T generate(CharID id);
}
