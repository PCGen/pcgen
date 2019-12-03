/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;

/**
 * A QualifierToken is an object that can select and filter zero or more objects
 * of a specific type of object.
 * 
 * This is typically processed as part of a CHOOSE. The syntax of a Qualifier is
 * !Key=Condition[Value]. The Key is returned from the LstToken interface, the
 * condition and value are provided to the initialize method.
 * 
 * @param <T>
 *            The Type of object processed by the QualifierToken
 */
public interface QualifierToken<T extends CDOMObject> extends LstToken, PrimitiveCollection<T>
{
	/**
	 * Initializes the QualifierToken with the content of the
	 * PrimitiveCollection as defined by the arguments.
	 * 
	 * This method returns true if initialization was successful. If
	 * initialization is not successful, then it should not be used as a
	 * PrimitiveCollection.
	 * 
	 * Note that any qualifier may or may not support conditions and negation;
	 * that is up to the implementation. However, any non-support should be
	 * identified by returning false, rather than throwing an exception.
	 * 
	 * @param context
	 *            The LoadContext to be used to get necessary information to
	 *            initialize the QualifierToken
	 * @param cl
	 *            The SelectionCreator used to get objects on which this
	 *            QualifierToken is operating
	 * @param condition
	 *            The condition of the qualifier; may be null if no condition
	 * @param value
	 *            The value of the qualifier
	 * @param negated
	 *            true if the PrimitiveCollection should be negated; false
	 *            otherwise
	 * @return true if initialization was successful; false otherwise
	 */
    boolean initialize(LoadContext context, SelectionCreator<T> cl, String condition, String value,
                       boolean negated);
}
