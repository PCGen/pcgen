package pcgen.testsupport;

import java.util.Objects;

import pcgen.base.formatmanager.ObjectDatabase;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

public class MockObjectDatabase implements ObjectDatabase
{
	public DoubleKeyMap<Class<?>, String, Object> map = new DoubleKeyMap<>();

	@Override
	public <T> T get(Class<T> cl, String name)
	{
		return (T) map.get(cl, name);
	}

	@Override
	public <T> Indirect<T> getIndirect(Class<T> cl, String name)
	{
		T underlying = get(cl, name);
		if (underlying == null)
		{
			throw new IllegalArgumentException("Does not contain " + cl.getName() + " " + name);
		}
		return new BasicIndirect<>(new Liar(this, underlying), underlying);
	}

	@Override
	public String getName(Object o)
	{
		return o.toString();
	}
	
	private class Liar implements FormatManager
	{

		private final MockObjectDatabase mockObjectDatabase;
		private final Object underlying;

		public Liar(MockObjectDatabase mockObjectDatabase, Object underlying)
		{
			this.mockObjectDatabase = Objects.requireNonNull(mockObjectDatabase);
			this.underlying = Objects.requireNonNull(underlying);
		}

		@Override
		public Object convert(String inputStr)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Indirect convertIndirect(String inputStr)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public String unconvert(Object obj)
		{
			return getName(obj);
		}

		@Override
		public Class getManagedClass()
		{
			return underlying.getClass();
		}

		@Override
		public String getIdentifierType()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public FormatManager getComponentManager()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isDirect()
		{
			return mockObjectDatabase.isDirect();
		}
		
	}

	@Override
	public boolean isDirect()
	{
		return false;
	}
}
