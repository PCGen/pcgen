package pcgen.gui2.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.WriteableReferenceFacade;

import org.junit.jupiter.api.Test;

class UnitSetWrappedReferenceTest
{

	@Test
	void testFromUnderlying()
	{
		WriteableReferenceFacade<Number> underlyingRef =
				new DefaultReferenceFacade<Number>();
		UnitSetWrappedReference inchesToCentimeters = UnitSetWrappedReference.getReference(
			underlyingRef, number -> number.doubleValue() * 2.54,
			number -> number.doubleValue() / 2.54);
		assertEquals(null, inchesToCentimeters.get());
		underlyingRef.set(0);
		assertEquals(0.0, inchesToCentimeters.get());
		underlyingRef.set(1);
		assertEquals(2.54, inchesToCentimeters.get());
		//This is, sadly, legal
		underlyingRef.set(null);
		assertEquals(null, inchesToCentimeters.get());
		underlyingRef.set(2);
		assertEquals(5.08, inchesToCentimeters.get());
	}

	@Test
	void testToUnderlying()
	{
		WriteableReferenceFacade<Number> underlyingRef =
				new DefaultReferenceFacade<Number>();
		UnitSetWrappedReference inchesToCentimeters = UnitSetWrappedReference.getReference(
			underlyingRef, number -> number.doubleValue() * 2.54,
			number -> number.doubleValue() / 2.54);
		assertEquals(null, underlyingRef.get());
		inchesToCentimeters.set(0);
		assertEquals(0.0, underlyingRef.get());
		inchesToCentimeters.set(2.54);
		assertEquals(1.0, underlyingRef.get());
		//This is, sadly, legal
		inchesToCentimeters.set(null);
		assertEquals(null, underlyingRef.get());
		inchesToCentimeters.set(5.08);
		assertEquals(2.0, underlyingRef.get());
	}

}
