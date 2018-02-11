package pcgen.base.proxy;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import pcgen.base.proxy.ItemProcessor;
import pcgen.base.proxy.ListProcessor;
import pcgen.base.proxy.MapProcessor;
import pcgen.base.proxy.StagingInfoFactory;

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
	public void testInvalidClass()
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
		try
		{
			factory.produceStaging(GetItemOnly.class, SetShouldBeVoid.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetListOnly.class, AddShouldBeVoid.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetMapOnly.class, PutShouldBeVoid.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetShouldHaveNoParams.class, SetItemOnly.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, SetItemShouldHaveOneParam.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, SetDupeName.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetNumber.class, SetItemOnly.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, LeftoverWrite.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(LeftoverRead.class, SetItemOnly.class);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			factory.produceStaging(GetItemOnly.class, DupePropertyName.class);
			fail();
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

}
