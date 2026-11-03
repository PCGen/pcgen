package pcgen.testsupport;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;

public final class TestSafe implements TypeSafeConstant
{

	public static final TestSafe TS1 = new TestSafe("TS1");
	public static final TestSafe TS2 = new TestSafe("TS2");
	public static final TestSafe TS3 = new TestSafe("TS3");

	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static CaseInsensitiveMap<TestSafe> typeMap;

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	/**
	 * The name of this Constant
	 */
	private final String name;

	/**
	 * The ordinal of this Constant
	 */
	private final int ordinal;

	private TestSafe(String name)
	{
		Objects.requireNonNull(name, "Name for TestSafe cannot be null");
		ordinal = ordinalCount++;
		this.name = name;
	}

	/**
	 * Converts this Constant to a String (returns the name of this Constant)
	 * 
	 * @return The string representation (name) of this Constant
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Gets the ordinal of this Constant
	 */
	@Override
	public int getOrdinal()
	{
		return ordinal;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is case
	 * insensitive). If the constant does not already exist, a new Constant is created
	 * with the given String as the name of the Constant.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static TestSafe getConstant(String name)
	{
		if (typeMap == null)
		{
			buildMap();
		}
		TestSafe key = typeMap.get(name);
		if (key == null)
		{
			key = new TestSafe(name);
			typeMap.put(name, key);
		}
		return key;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is case
	 * insensitive). If the constant does not already exist, an IllegalArgumentException
	 * is thrown.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 * @throws IllegalArgumentException
	 *             if the given String is not a previously defined TestSafe
	 */
	public static TestSafe valueOf(String name)
	{
		if (typeMap == null)
		{
			buildMap();
		}
		TestSafe key = typeMap.get(name);
		if (key == null)
		{
			throw new IllegalArgumentException(
				name + " is not a previously defined TestSafe");
		}
		return key;
	}

	/**
	 * Returns a Collection of all of the Constants in this Class.
	 * 
	 * This collection maintains a reference to the Constants in this Class, so if a new
	 * Constant is created, the Collection returned by this method will be modified.
	 * (Beware of ConcurrentModificationExceptions)
	 * 
	 * @return a Collection of all of the Constants in this Class.
	 */
	public static Collection<TestSafe> getAllConstants()
	{
		if (typeMap == null)
		{
			buildMap();
		}
		return Collections.unmodifiableCollection(typeMap.values());
	}

	/**
	 * Clears all of the Constants in this Class (forgetting the mapping from the String
	 * to the Constant).
	 */
	/*
	 * Note: this does not (and really cannot) reset the ordinal count... Does this method
	 * need to be renamed, such that it is clearConstantMap?
	 */
	public static void clearConstants()
	{
		typeMap.clear();
	}

	private static void buildMap()
	{
		typeMap = new CaseInsensitiveMap<>();
		Field[] fields = TestSafe.class.getDeclaredFields();
		for (Field field : fields)
		{
			int mod = field.getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
				&& Modifier.isPublic(mod))
			{
				try
				{
					Object obj = field.get(null);
					if (obj instanceof TestSafe)
					{
						typeMap.put(field.getName(), (TestSafe) obj);
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new UnreachableError(e);
				}
			}
		}
	}
}
