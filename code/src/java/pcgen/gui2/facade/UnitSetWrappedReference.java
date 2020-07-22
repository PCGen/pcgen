/*
 * Copyright (c) Thomas Parker, 2018-9.
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
package pcgen.gui2.facade;

import java.util.Objects;
import java.util.function.Function;

import pcgen.facade.util.AbstractReferenceFacade;
import pcgen.facade.util.WriteableReferenceFacade;

/**
 * UnitSetWrappedReference is a Decorator for a WriteableReferenceFacade that converts an
 * underlying value into a value that is converted to the current unit set.
 */
public final class UnitSetWrappedReference extends AbstractReferenceFacade<Number>
		implements WriteableReferenceFacade<Number>
{
	/**
	 * The underlying WriteableReferenceFacade containing the uncorrected value.
	 */
	private final WriteableReferenceFacade<Number> underlyingRef;

	/**
	 * The function used to convert the uncorrected value to the current unit set.
	 */
	private final Function<Number, Number> toUnitSet;

	/**
	 * The function used to convert a corrected value (in the current unit set) to an
	 * uncorrected value.
	 */
	private final Function<Number, Number> fromUnitSet;

	/**
	 * Constructs a new UnitSetWrappedReference with the given parameters.
	 * 
	 * @param underlyingRef
	 *            The underlying WriteableReferenceFacade containing the uncorrected value
	 * @param toUnitSet
	 *            The function used to convert the uncorrected value to the current unit
	 *            set
	 * @param fromUnitSet
	 *            The function used to convert a corrected value (in the current unit set)
	 *            to an uncorrected value
	 */
	private UnitSetWrappedReference(
		WriteableReferenceFacade<Number> underlyingRef,
		Function<Number, Number> toUnitSet,
		Function<Number, Number> fromUnitSet)
	{
		this.underlyingRef = Objects.requireNonNull(underlyingRef);
		this.toUnitSet = Objects.requireNonNull(toUnitSet);
		this.fromUnitSet = Objects.requireNonNull(fromUnitSet);
	}

	@Override
	public Number get()
	{
		Number underlying = underlyingRef.get();
		return (underlying == null) ? null : toUnitSet.apply(underlying);
	}

	@Override
	public void set(Number obj)
	{
		Number toSet = (obj == null) ? null : fromUnitSet.apply(obj);
		underlyingRef.set(toSet);
	}

	/**
	 * Returns a new UnitSetWrappedReference with the given parameters.
	 * 
	 * @param underlyingRef
	 *            The underlying WriteableReferenceFacade containing the uncorrected value
	 * @param toUnitSet
	 *            The function used to convert the uncorrected value to the current unit
	 *            set
	 * @param fromUnitSet
	 *            The function used to convert a corrected value (in the current unit set)
	 *            to an uncorrected value
	 */
	//Factory to avoid the constructor leaking "this" into another object.
	public static UnitSetWrappedReference getReference(
		WriteableReferenceFacade<Number> underlyingRef,
		Function<Number, Number> toUnitSet,
		Function<Number, Number> fromUnitSet)
	{
		UnitSetWrappedReference wrappedRef = new UnitSetWrappedReference(
			underlyingRef, toUnitSet, fromUnitSet);
		underlyingRef.addReferenceListener(refEvent -> {
			Number oldReference = refEvent.getOldReference();
			Number oldValue = (oldReference == null) ? null
				: toUnitSet.apply(oldReference);
			Number newReference = refEvent.getNewReference();
			Number newValue = (newReference == null) ? null
				: toUnitSet.apply(newReference);
			wrappedRef.fireReferenceChangedEvent(wrappedRef, oldValue,
				newValue);
		});
		return wrappedRef;
	}
}
