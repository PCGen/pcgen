/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

/**
 * A CDOMCompatibilityToken is a CDOMToken that implements a tag syntax from a
 * previous version of PCGen. Once deprecated, a CDOMPrimaryToken would become a
 * CDOMCompatibilityToken.
 *
 * @param <T> The type of object on which this CDOMCompatibilityToken can be
 *            used
 */
public interface CDOMCompatibilityToken<T> extends CDOMToken<T>, CompatibilityToken
{
    //This is a unifying interface (extends multiple other interfaces)
}
