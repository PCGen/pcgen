package pcgen.base.proxy;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class StagingProxyFactoryTest
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
	public void testInvalidClassInterfaceRequired()
	{
		try
		{
			factory.produceStaging(Object.class, SetItemOnly.class);
			fail("factory should require an interface");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, Object.class);
			fail("factory should require an interface");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassAvoidNulls()
	{
		try
		{
			factory.produceStaging(GetListOnly.class, null);
			fail("Can't take null item");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(null, AddListOnly.class);
			fail();
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassMethodsRequired()
	{
		try
		{
			factory.produceStaging(NoMethodInterface.class, SetItemOnly.class);
			fail("factory should require an interface with methods");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetListOnly.class, NoMethodInterface.class);
			fail("factory should require an interface with methods");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(SetItemOnly.class, SetItemOnly.class);
			fail("Must have read methods on read interface");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassCheckReturnTypes()
	{
		try
		{
			factory.produceStaging(GetItemOnly.class, SetShouldBeVoid.class);
			fail("Set Methods Should be void");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetListOnly.class, AddShouldBeVoid.class);
			fail("Add methods should be void");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetMapOnly.class, PutShouldBeVoid.class);
			fail("Put method should be void");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassParameterCountCheck()
	{
		try
		{
			factory.produceStaging(GetShouldHaveNoParams.class, SetItemOnly.class);
			fail("Get Should have no parameters");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, SetItemShouldHaveOneParam.class);
			fail("Set Should have one parameter");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassMismatch()
	{
		try
		{
			factory.produceStaging(GetItemOnly.class, SetDupeName.class);
			fail("Cannot have method set methods with two types");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetNumber.class, SetItemOnly.class);
			fail("Set and Get Methods did not match format");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassAvoidExtras()
	{
		try
		{
			factory.produceStaging(GetItemOnly.class, LeftoverWrite.class);
			fail("Leftover Method on write interface prohibited");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(LeftoverRead.class, SetItemOnly.class);
			fail("Leftover method on read interface prohibited");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void testInvalidClassMixedTypes()
	{
		try
		{
			factory.produceStaging(GetItemOnly.class, DupePropertyName.class);
			fail("Cannot have both set and add for a given property");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

	@Test
	public void test()
	{
		factory.produceStaging(GetItemOnly.class, SetItemOnly.class);
		factory.produceStaging(GetListOnly.class, AddListOnly.class);
		factory.produceStaging(GetMapOnly.class, PutMapOnly.class);
		factory.produceStaging(GetItemOnly.class, SetItem.class);
		factory.produceStaging(GetListOnly.class, AddList.class);
		factory.produceStaging(GetMapOnly.class, PutMap.class);
	}

	public interface NoMethodInterface
	{

	}

	public interface SetItemOnly
	{
		public void setBasic(String s);
	}

	public interface GetItemOnly
	{
		public String getBasic();
	}

	public interface AddListOnly
	{
		public void addBasic(String s);
	}

	public interface GetListOnly
	{
		public String[] getBasicArray();
	}

	public interface PutMapOnly
	{
		public void put(String s, Object value);
	}

	public interface GetMapOnly
	{
		public Object get(String s);
	}

	public interface SetShouldBeVoid
	{
		public boolean setBasic(String s);
	}

	public interface GetShouldHaveNoParams
	{
		public String getBasic(String s);
	}

	public interface GetNumber
	{
		public Number getBasic();
	}

	public interface SetItemShouldHaveOneParam
	{
		public void setBasic(String name, String s);
	}

	public interface AddShouldBeVoid
	{
		public boolean addBasic(String s);
	}

	public interface PutShouldBeVoid
	{
		public boolean put(String s, Object value);
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
		public String getBasic();
		public Number[] getBasicStuff();
	}

	public interface DupePropertyName
	{
		public void setBasic(String s);
		public void addBasic(Number n);
	}

	public interface SetItem extends GetItemOnly
	{
		public void setBasic(String s);
	}

	public interface AddList extends GetListOnly
	{
		public void addBasic(String s);
	}

	public interface PutMap extends GetMapOnly
	{
		public void put(String s, Object value);
	}
}
