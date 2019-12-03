/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence.token;

/**
 * A CDOMInterfaceToken is a token designed to run on an Interface of an object rather
 * than the direct class hierarchy. (CDOMPrimaryToken et al all operate assuming their
 * arguments are full classes that are exactly equal to or ancestors of the object being
 * processed).
 * 
 * Note: While the interface of this class only enforces T, it should be expected that the
 * objects processed by this CDOMInterfaceToken also extend Loadable.
 * 
 * @param <R>
 *            The read Interface being used for purposes of this CDOMInterfaceToken.
 * @param <W>
 *            The write Interface being used for purposes of this CDOMInterfaceToken.
 */
public interface CDOMInterfaceToken<R, W> extends CDOMToken<W>, CDOMWriteToken<R>
{

	/**
	 * Returns the Read Interface for this CDOMInterfaceToken.
	 * 
	 * Note: The write interface should be returned by the getTokenClass() method.
	 * 
	 * @return The Read Interface for this CDOMInterfaceToken
	 */
    Class<R> getReadInterface();

}
