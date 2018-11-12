/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the StagingInfoFactory class
 */
public class StagingInfoFactoryTest
{

	private StagingInfoFactory factory = new StagingInfoFactory();

	@Before
	public void setUp()
	{
		factory.addProcessor(new ItemProcessor());
		factory.addProcessor(new ListProcessor());
		factory.addProcessor(new MapProcessor());
	}

	@Test
	public void testInvalidClass()
	{
		try
		{
			factory.produceStaging(Object.class, SetItemOnly.class, new Object());
			fail("factory should require an interface");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, Object.class, new GetItemOnly()
			{
			});
			fail("factory should require an interface");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(NoMethodInterface.class, SetItemOnly.class,
				new NoMethodInterface()
				{
				});
			fail("factory should require an interface with methods");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetListOnly.class, NoMethodInterface.class,
				new GetListOnly()
				{
				});
			fail("factory should require an interface with methods");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(SetItemOnly.class, SetItemOnly.class, new SetItemOnly()
			{
			});
			fail("Must have read methods on read interface");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetListOnly.class, null, new GetListOnly()
			{
			});
			fail("Can't take null item");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(null, AddListOnly.class, new Object());
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, SetShouldBeVoid.class,
				new GetItemOnly()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetListOnly.class, AddShouldBeVoid.class,
				new GetListOnly()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetMapOnly.class, PutShouldBeVoid.class,
				new GetMapOnly()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetShouldHaveNoParams.class, SetItemOnly.class,
				new GetShouldHaveNoParams()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, SetItemShouldHaveOneParam.class,
				new GetItemOnly()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, SetDupeName.class, new GetItemOnly()
			{
			});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetNumber.class, SetItemOnly.class, new GetNumber()
			{
			});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, LeftoverWrite.class,
				new GetItemOnly()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, LeftoverWrite.class, null);
			fail();
		}
		catch (NullPointerException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(LeftoverRead.class, SetItemOnly.class,
				new LeftoverRead()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, DupePropertyName.class,
				new GetItemOnly()
				{
				});
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testStagingSetup()
	{
		factory.produceStaging(GetItemOnly.class, SetItemOnly.class, new GetItemOnly()
		{
		});
		factory.produceStaging(GetListOnly.class, AddListOnly.class, new GetListOnly()
		{
		});
		factory.produceStaging(GetMapOnly.class, PutMapOnly.class, new GetMapOnly()
		{
		});
		factory.produceStaging(GetItemOnly.class, SetItem.class, new SetItem()
		{
		});
		factory.produceStaging(GetListOnly.class, AddList.class, new AddList()
		{
		});
		factory.produceStaging(GetMapOnly.class, PutMap.class, new PutMap()
		{
		});
		factory.produceStaging(ReadOnlyItem.class, SetItemOnly.class, new Settable());
	}

	@Test
	public void testStagingItem()
	{
		SetItem object = new SetItem()
		{

			String s;

			@Override
			public String getBasic()
			{
				return s;
			}

			@Override
			public void setBasic(String s)
			{
				this.s = s;
			}

		};
		StagingInfo<GetItemOnly, SetItem> staging =
				factory.produceStaging(GetItemOnly.class, SetItem.class, object);
		SetItem setter = staging.getWriteProxy();
		setter.setBasic("Wine");
		GetItemOnly getter = staging.getReadProxy();
		assertEquals("Wine", getter.getBasic());
		assertNull(object.getBasic());
		staging.getStagingObject().applyTo(object);
		assertEquals("Wine", object.getBasic());
	}

	@Test
	public void testStagingList()
	{
		AddList object = new AddList()
		{

			List<String> strings = new ArrayList<>();

			@Override
			public String[] getBasicArray()
			{
				return strings.toArray(new String[strings.size()]);
			}

			@Override
			public void addBasic(String s)
			{
				strings.add(s);
			}
		};
		StagingInfo<GetListOnly, AddList> staging =
				factory.produceStaging(GetListOnly.class, AddList.class, object);
		AddList setter = staging.getWriteProxy();
		setter.addBasic("Wine");
		GetListOnly getter = staging.getReadProxy();
		String[] array = getter.getBasicArray();
		assertEquals(1, array.length);
		setter.addBasic("Cheese");
		array = getter.getBasicArray();
		assertEquals(2, array.length);
		List<String> list = Arrays.asList(array);
		assertTrue(list.contains("Wine"));
		assertTrue(list.contains("Cheese"));
		assertEquals(0, object.getBasicArray().length);
		staging.getStagingObject().applyTo(object);
		assertEquals(2, array.length);
		list = Arrays.asList(array);
		assertTrue(list.contains("Wine"));
		assertTrue(list.contains("Cheese"));
	}

	@Test
	public void testStagingMap()
	{
		PutMap object = new PutMap()
		{

			Map<String, Object> map = new HashMap<>();

			@Override
			public Object get(String s)
			{
				return map.get(s);
			}

			@Override
			public void put(String s, Object value)
			{
				map.put(s, value);
			}

		};
		StagingInfo<GetMapOnly, PutMap> staging =
				factory.produceStaging(GetMapOnly.class, PutMap.class, object);
		PutMap setter = staging.getWriteProxy();
		setter.put("Wine", "Cheese");
		GetMapOnly getter = staging.getReadProxy();
		Object wineTarget = getter.get("Wine");
		assertEquals("Cheese", wineTarget);
		assertNull(object.get("Wine"));
		staging.getStagingObject().applyTo(object);
		assertEquals("Cheese", object.get("Wine"));
	}

	@Test
	public void testStagingParamMap()
	{
		PutParamMap object = new PutParamMap()
		{

			Map<String, Number> map = new HashMap<>();

			@Override
			public Number getNumber(String s)
			{
				return map.get(s);
			}

			@Override
			public void putNumber(String s, Number value)
			{
				map.put(s, value);
			}

		};
		StagingInfo<GetParamMapOnly, PutParamMap> staging =
				factory.produceStaging(GetParamMapOnly.class, PutParamMap.class, object);
		PutParamMap setter = staging.getWriteProxy();
		setter.putNumber("Wine", Integer.valueOf(1));
		GetParamMapOnly getter = staging.getReadProxy();
		Number wineTarget = getter.getNumber("Wine");
		assertEquals(1, wineTarget);
		assertNull(object.getNumber("Wine"));
		staging.getStagingObject().applyTo(object);
		assertEquals(1, object.getNumber("Wine"));
	}

	@Test
	public void testStagingItemReadOnly()
	{
		Settable object = new Settable()
		{

			String s;

			@Override
			public String getBasic()
			{
				return s;
			}

			@Override
			public void setBasic(String s)
			{
				this.s = s;
			}

			@Override
			public Number getReadable()
			{
				return 42;
			}
		};
		StagingInfo<ReadOnlyItem, SetItem> staging = factory
				.produceStaging(ReadOnlyItem.class, SetItem.class, object);
		SetItem setter = staging.getWriteProxy();
		setter.setBasic("Wine");
		ReadOnlyItem getter = staging.getReadProxy();
		assertEquals("Wine", getter.getBasic());
		assertNull(object.getBasic());
		staging.getStagingObject().applyTo(object);
		assertEquals("Wine", object.getBasic());
		assertEquals(42, getter.getReadable());
		assertEquals(42, object.getReadable());
	}

	public interface NoMethodInterface
	{

	}

	public interface SetItemOnly
	{
		public default void setBasic(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface GetItemOnly
	{
		public default String getBasic()
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface AddListOnly
	{
		public default void addBasic(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface GetListOnly
	{
		public default String[] getBasicArray()
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface PutMapOnly
	{
		public default void put(String s, Object value)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface GetMapOnly
	{
		public default Object get(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface SetShouldBeVoid
	{
		public boolean setBasic(String s);
	}

	public interface GetShouldHaveNoParams
	{
		public default String getBasic(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface GetNumber
	{
		public default Number getBasic()
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface SetItemShouldHaveOneParam
	{
		public default void setBasic(String name, String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface AddShouldBeVoid
	{
		public default boolean addBasic(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface PutShouldBeVoid
	{
		public default boolean put(String s, Object value)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface SetDupeName
	{
		public void setBasic(String s);

		public void setBasic(Number n);
	}

	public interface LeftoverWrite
	{
		public void setBasic(String s);

		public void doBasic(Number n);
	}

	public interface LeftoverRead
	{
		public default String getBasic()
		{
			throw new UnsupportedOperationException();
		}

		public default Number[] getBasicStuff()
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface DupePropertyName
	{
		public void setBasic(String s);

		public void addBasic(Number n);
	}

	public interface SetItem extends GetItemOnly
	{
		public default void setBasic(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface AddList extends GetListOnly
	{
		public default void addBasic(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface PutMap extends GetMapOnly
	{
		public default void put(String s, Object value)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface GetParamMapOnly
	{
		public default Number getNumber(String s)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface PutParamMap extends GetParamMapOnly
	{
		public default void putNumber(String s, Number value)
		{
			throw new UnsupportedOperationException();
		}
	}

	public interface ReadOnlyItem extends GetItemOnly
	{
		@ReadOnly
		public Number getReadable();
	}

	public class Settable implements SetItem, ReadOnlyItem
	{

		@Override
		public String getBasic()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void setBasic(String s)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Number getReadable()
		{
			throw new UnsupportedOperationException();
		}

	}
}
