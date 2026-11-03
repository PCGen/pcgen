package pcgen.testsupport;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formatmanager.ObjectDatabase;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

public class MockObjectDatabase implements ObjectDatabase
{
	/**
	 * The underlying Map of identifiers to objects
	 */
	public DoubleKeyMap<Class<?>, String, Object> map = new DoubleKeyMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> cl, String name)
	{
		T underlying = (T) map.get(cl, name);
		if (underlying == null)
		{
			throw new IllegalArgumentException("Does not contain " + cl.getName() + " " + name);
		}
		return underlying;
	}

	@Override
	public <T> Indirect<T> getIndirect(Class<T> cl, String name)
	{
		T underlying = get(cl, name);
		if (underlying == null)
		{
			throw new IllegalArgumentException("Does not contain " + cl.getName() + " " + name);
		}
		return new BasicIndirect<>(new Liar<>(this, underlying), underlying);
	}

	@Override
	public String getName(Object o)
	{
		return o.toString();
	}
	
	private class Liar<T> implements FormatManager<T>
	{

		private final MockObjectDatabase mockObjectDatabase;
		private final Object underlying;

		public Liar(MockObjectDatabase mockObjectDatabase, T underlying)
		{
			this.mockObjectDatabase = Objects.requireNonNull(mockObjectDatabase);
			this.underlying = Objects.requireNonNull(underlying);
		}

		@Override
		public T convert(String inputStr)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Indirect<T> convertIndirect(String inputStr)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public String unconvert(Object obj)
		{
			return getName(obj);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<T> getManagedClass()
		{
			return (Class<T>) underlying.getClass();
		}

		@Override
		public String getIdentifierType()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<FormatManager<?>> getComponentManager()
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
