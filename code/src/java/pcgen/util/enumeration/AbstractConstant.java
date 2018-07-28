/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
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
 * 
 */
package pcgen.util.enumeration;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class AbstractConstant implements Serializable
{

	private transient String fieldName;

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		Field[] f = getClass().getDeclaredFields();
		for (final Field aF : f)
		{
			try
			{
				int mod = aF.getModifiers();
				if (Modifier.isStatic(mod) && Modifier.isFinal(mod) && Modifier.isPublic(mod))
				{
					//Use == to get exact object match (do not use .equals())
					if (this == aF.get(null))
					{
						out.writeObject(aF.getName());
					}
				}
			}
			catch (IllegalAccessException e)
			{
				throw new IOException(e.getLocalizedMessage());
			}
		}
	}

	private void readObject(ObjectInputStream in) throws IOException
	{
		try
		{
			fieldName = (String) in.readObject();
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException(e.getLocalizedMessage());
		}
	}

	protected Object readResolve() throws ObjectStreamException
	{
		try
		{
			return getClass().getField(fieldName).get(null);
		}
		catch (SecurityException | NoSuchFieldException | IllegalAccessException e)
		{
			throw new InvalidObjectException("Failed to resolve object: " + e.getLocalizedMessage());
		}
	}
}
