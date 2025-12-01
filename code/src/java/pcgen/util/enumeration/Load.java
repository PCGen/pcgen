/*
 * Copyright 2014 (C) Stefan Radermacher
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util.enumeration;

import java.awt.Font;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
import pcgen.core.utils.CoreUtility;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.FontManipulation;

public enum Load
{
	LIGHT(FontManipulation::plain, UIPropertyContext::getQualifiedColor),
	MEDIUM(FontManipulation::bold, UIPropertyContext::getAutomaticColor),
	HEAVY(FontManipulation::bold_italic, UIPropertyContext::getVirtualColor),
	OVERLOAD(FontManipulation::bold_italic, UIPropertyContext::getNotQualifiedColor);

	private static final DoubleFunction<Double> LIGHT_ENCUMBERED_MOVE = unencumberedMove -> unencumberedMove;
	private static final DoubleFunction<Double>  MEDIUM_HEAVY_ENCUMBERED_MOVE = unencumberedMove -> {
		if (CoreUtility.doublesEqual(unencumberedMove, 5) || CoreUtility.doublesEqual(unencumberedMove, 10))
		{
			return 5.0;
		}
		else
		{
			return (Math.floor(unencumberedMove / 15) * 10) + (((int) unencumberedMove) % 15);
		}
	};
	private static final DoubleFunction<Double>  OVERLOADED_ENCUMBERED_MOVE = unencumberedMove -> 0.0;

	static
	{
		LIGHT.encumberedMoveFunction = LIGHT_ENCUMBERED_MOVE;
		MEDIUM.encumberedMoveFunction = MEDIUM_HEAVY_ENCUMBERED_MOVE;
		HEAVY.encumberedMoveFunction = MEDIUM_HEAVY_ENCUMBERED_MOVE;
		OVERLOAD.encumberedMoveFunction = OVERLOADED_ENCUMBERED_MOVE;
	}

	private final Function<Font, Font> fontFunction;
	private final Supplier<Color> colorFunction;
	private DoubleFunction<Double>  encumberedMoveFunction;

	Load(Function<Font, Font> fontFunction, Supplier<Color> colorFunction)
	{
		this.fontFunction = fontFunction;
		this.colorFunction = colorFunction;
	}

	public Font getFont(Font font)
	{
		return fontFunction.apply(font);
	}

	public Color getColor()
	{
		return colorFunction.get();
	}


	@Override
	public String toString()
	{
		return name();
	}

	private boolean checkLtEq(Load x)
	{
		return ordinal() <= x.ordinal();
	}

	public Load max(Load x)
	{
		return checkLtEq(x) ? x : this;
	}

	/**
	 * @param val should be a string value to be checked for equality (case-insensitive) with
	 * 				one of the enum values for this enumeration
	 * @return the enumeration that matches the given string, or null if none match
	 */
	public static Load getLoadType(String val)
	{
		return valueOf(val.toUpperCase());
	}


	public double calcEncumberedMove(final double unencumberedMove)
	{
		return encumberedMoveFunction.apply(unencumberedMove);
	}
}
