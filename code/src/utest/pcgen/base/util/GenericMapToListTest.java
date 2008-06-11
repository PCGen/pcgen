/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

public class GenericMapToListTest extends TestCase
{
	@Test
	public void testConstructorNoZeroArg()
	{
		try
		{
			new GenericMapToList(Foo.class);
			fail("Expected InstantiationException");
		}
		catch (InstantiationException e)
		{
			// OK
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorPrivate()
	{
		try
		{
			new GenericMapToList(Bar.class);
			fail("Expected InstantiationException");
		}
		catch (InstantiationException e)
		{
			// OK
		}
		catch (IllegalAccessException e)
		{
			fail(e.getMessage());
		}
	}

	public class Foo<K, V> implements Map<K, V>
	{

		public Foo(String s)
		{
			// Just need to avoid a zero argument constructor
		}

		public void clear()
		{
			throw new UnsupportedOperationException();
		}

		public boolean containsKey(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public boolean containsValue(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public Set<java.util.Map.Entry<K, V>> entrySet()
		{
			throw new UnsupportedOperationException();
		}

		public V get(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public boolean isEmpty()
		{
			throw new UnsupportedOperationException();
		}

		public Set<K> keySet()
		{
			throw new UnsupportedOperationException();
		}

		public V put(K arg0, V arg1)
		{
			throw new UnsupportedOperationException();
		}

		public void putAll(Map<? extends K, ? extends V> arg0)
		{
			throw new UnsupportedOperationException();
		}

		public V remove(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public int size()
		{
			throw new UnsupportedOperationException();
		}

		public Collection<V> values()
		{
			throw new UnsupportedOperationException();
		}

	}

	public class Bar<K, V> implements Map<K, V>
	{

		private Bar()
		{
			// Just need to avoid a public zero argument constructor
		}

		public void clear()
		{
			throw new UnsupportedOperationException();
		}

		public boolean containsKey(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public boolean containsValue(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public Set<java.util.Map.Entry<K, V>> entrySet()
		{
			throw new UnsupportedOperationException();
		}

		public V get(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public boolean isEmpty()
		{
			throw new UnsupportedOperationException();
		}

		public Set<K> keySet()
		{
			throw new UnsupportedOperationException();
		}

		public V put(K arg0, V arg1)
		{
			throw new UnsupportedOperationException();
		}

		public void putAll(Map<? extends K, ? extends V> arg0)
		{
			throw new UnsupportedOperationException();
		}

		public V remove(Object arg0)
		{
			throw new UnsupportedOperationException();
		}

		public int size()
		{
			throw new UnsupportedOperationException();
		}

		public Collection<V> values()
		{
			throw new UnsupportedOperationException();
		}

	}
}
