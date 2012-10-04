/*
 * CategorizedPersistentChoiceActor.java
 * Copyright James Dempsey, 2012
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
 *
 * Created on 04/10/2012 5:16:58 PM
 *
 * $Id$
 */
package pcgen.cdom.base;

/**
 * A CategorizedPersistentChoiceActor is a ChoiceActor that is designed to be saved 
 * and restored with a PlayerCharacter. A Category is linked to the choice. This is 
 * used in situations where certain relationship information (e.g. associations) 
 * needs to be uniquely restored when a PlayerCharacter is loaded from a persistent 
 * state (such as a save file)
 * 
 * @param <T>
 *            The type of object that this CategorizedPersistentChoiceActor can apply to a
 *            PlayerCharacter
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public interface CategorizedPersistentChoiceActor<T extends CategorizedCDOMObject<?>> extends
		PersistentChoiceActor<T>
{

	/**
	 * Decodes a given String into a choice of the appropriate type. The String
	 * format to be passed into this method is defined solely by the return
	 * result of the encodeChoice method. There is no guarantee that the
	 * encoding is human readable, simply that the encoding is uniquely
	 * identifying such that this method is capable of decoding the String into
	 * the choice object.
	 * 
	 * @param persistentFormat
	 *            The String which should be decoded to provide the choice of
	 *            the appropriate type.
	 * @param category The fixed category of the choice.
	 * 
	 * @return A choice object of the appropriate type that was encoded in the
	 *         given String.
	 */
	public T decodeChoice(String persistentFormat, Category<?> category);

}
