/*
 * Copyright (c) Devon Jones, 2012.
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
package pcgen.cdom.facet;

import pcgen.core.Language;

/**
 * AutoLanguageFacet is a Facet that tracks the Languages that have been granted
 * to a Player Character through the AUTO:LANG|%LIST
 * 
 * @author Devon Jones (devon.jones [at] gmail.com)
 */
public class AutoLanguageListFacet extends AbstractSourcedListFacet<Language> {
}
