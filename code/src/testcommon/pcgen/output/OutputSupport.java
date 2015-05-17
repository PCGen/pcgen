/*
 * Copyright (c) 2014-15 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output;

import pcgen.output.library.ObjectWrapperLibrary;
import pcgen.output.wrapper.CDOMObjectWrapper;
import pcgen.output.wrapper.CDOMReferenceWrapper;
import pcgen.output.wrapper.TypeSafeConstantWrapper;
import freemarker.template.ObjectWrapper;

public class OutputSupport
{

	public static void setUpObjectWrapper()
	{
		ObjectWrapperLibrary owl = ObjectWrapperLibrary.getInstance();
		owl.add(ObjectWrapper.SIMPLE_WRAPPER);
		owl.add(CDOMObjectWrapper.getInstance());
		owl.add(new CDOMReferenceWrapper());
		owl.add(new TypeSafeConstantWrapper());
	}

}
