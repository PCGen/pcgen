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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test the StagingInfoFactory class
 */
public class StagingInfoFactoryTest
{

	private StagingInfoFactory getFactory()
	{
		StagingInfoFactory factory = new StagingInfoFactory();
		factory.addProcessor(new ItemProcessor());
		factory.addProcessor(new ListProcessor());
		factory.addProcessor(new MapProcessor());
		return factory;
	}

	@Test
	public void testInvalidClass()
	{
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(Object.class, SetItemOnly.class, new Object()));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetItemOnly.class, Object.class, new GetItemOnly() {}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(NoMethodInterface.class, SetItemOnly.class,
				new NoMethodInterface()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetListOnly.class, NoMethodInterface.class,
				new GetListOnly()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(SetItemOnly.class, SetItemOnly.class, new SetItemOnly()
			{
			}));
		assertThrows(NullPointerException.class, () -> getFactory().produceStaging(GetListOnly.class, null, new GetListOnly()
			{
			}));
		assertThrows(NullPointerException.class, () -> getFactory().produceStaging(null, AddListOnly.class, new Object()));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetItemOnly.class, SetShouldBeVoid.class,
				new GetItemOnly()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetListOnly.class, AddShouldBeVoid.class,
				new GetListOnly()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetMapOnly.class, PutShouldBeVoid.class,
				new GetMapOnly()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetShouldHaveNoParams.class, SetItemOnly.class,
				new GetShouldHaveNoParams()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetItemOnly.class, SetItemShouldHaveOneParam.class,
				new GetItemOnly()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetItemOnly.class, SetDupeName.class, new GetItemOnly()
			{
			}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetNumber.class, SetItemOnly.class, new GetNumber()
			{
			}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetItemOnly.class, LeftoverWrite.class,
				new GetItemOnly()
				{
				}));
		assertThrows(NullPointerException.class, () -> getFactory().produceStaging(GetItemOnly.class, LeftoverWrite.class, null));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(LeftoverRead.class, SetItemOnly.class,
				new LeftoverRead()
				{
				}));
		assertThrows(IllegalArgumentException.class, () -> getFactory().produceStaging(GetItemOnly.class, DupePropertyName.class,
				new GetItemOnly()
				{
				}));
	}

	@Test
	public void testStagingSetup()
	{
		getFactory().produceStaging(GetItemOnly.class, SetItemOnly.class, new GetItemOnly()
		{
		});
		getFactory().produceStaging(GetListOnly.class, AddListOnly.class, new GetListOnly()
		{
		});
		getFactory().produceStaging(GetMapOnly.class, PutMapOnly.class, new GetMapOnly()
		{
		});
		getFactory().produceStaging(GetItemOnly.class, SetItem.class, new SetItem()
		{
		});
		getFactory().produceStaging(GetListOnly.class, AddList.class, new AddList()
		{
		});
		getFactory().produceStaging(GetMapOnly.class, PutMap.class, new PutMap()
		{
		});
		getFactory().produceStaging(ReadOnlyItem.class, SetItemOnly.class, new Settable());
	}

	@Test
	public void testStagingItem()
	{
		StagingInfoFactory factory = getFactory();
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
		StagingInfoFactory factory = getFactory();
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
		StagingInfoFactory factory = getFactory();
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
		StagingInfoFactory factory = getFactory();
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
		StagingInfoFactory factory = getFactory();
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

	public interface SetItem extends ReadOnlyItem
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
		public default Number getReadable()
		{
			throw new UnsupportedOperationException();
		}
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
