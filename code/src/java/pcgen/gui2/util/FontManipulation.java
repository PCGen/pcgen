/*
 * FontManipulation.java
 * Copyright 2013 (C) Vincent Lhote
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
package pcgen.gui2.util;

import java.awt.Container;
import java.awt.Font;

/**
 * This class regroups font manipulation methods, like having bold on a label.
 * Font manipulation should be done in very few cases because it usually breaks
 * the Look-and-feel. Using fixed point value for font shouldn’t be done because
 * it might make text unreadable on system where default size is quite big (or
 * small).
 * 
 * @author Vincent Lhote
 */
public class FontManipulation {

	public static Font title(Font f)
	{
		// XXX In Japanese, bold is not used and hardly readeable if small, just
		// use gothic/non gothic instead.
		return f.deriveFont(Font.BOLD);
	}

	/**
	 * For title font
	 * 
	 * @param container
	 *            element to change the font of
	 */
	public static void title(Container container)
	{
		container.setFont(title(container.getFont()));
	}

	/**
	 * For bigger font.
	 * 
	 * @param container
	 *            element to change the font of
	 */
	public static void bigger(Container container)
	{
		Font font = container.getFont();
		container.setFont(bigger(font));
	}

	/**
	 * For bigger font.
	 * 
	 * @param font
	 *            base font
	 */
	public static Font bigger(Font f)
	{
		return f.deriveFont(f.getSize() * 1.8f);
	}

	/**
	 * For slightly bigger font.
	 * 
	 * @param font
	 *            base font
	 */
	public static Font big(Font f)
	{
		return f.deriveFont(f.getSize() * 1.4f);
	}

	/**
	 * For slightly bigger font.
	 * 
	 * @param container
	 *            element to change the font of
	 */
	public static void big(Container container)
	{
		Font font = container.getFont();
		container.setFont(big(font));
	}

	/**
	 * For a bit smaller font.
	 * 
	 * @param font
	 *            base font
	 */
	public static Font small(Font f)
	{
		return f.deriveFont(f.getSize() * 0.9f);
	}


	/**
	 * Change font of container for a bit smaller font.
	 * 
	 * @param container
	 *            element to change font size of
	 */
	public static void small(Container container)
	{
		Font font = container.getFont();
		container.setFont(small(font));
	}

	/**
	 * For smaller font.
	 * 
	 * @param font
	 *            base font
	 */
	public static Font smaller(Font f)
	{
		return f.deriveFont(f.getSize() * 0.8f);
	}

	/**
	 * For less important text, like grayed out italic.
	 * 
	 * @param container
	 *            element to change the font of
	 */
	public static Font less(Font f)
	{
		return f.deriveFont(Font.ITALIC);
	}

	public static Font size170(Font f)
	{
		return f.deriveFont(f.getSize() * 1.7f);
	}

	public static Font size160(Font f)
	{
		return f.deriveFont(f.getSize() * 1.6f);
	}

	public static void size160(Container container)
	{
		Font font = container.getFont();
		container.setFont(font.deriveFont(font.getSize() * 1.6f));
	}

	public static void size140(Container container)
	{
		Font font = container.getFont();
		container.setFont(font.deriveFont(font.getSize() * 1.4f));
	}

	public static Font size130(Font f)
	{
		return f.deriveFont(f.getSize() * 1.3f);
	}
	
	public static void size120(Container container)
	{
		Font font = container.getFont();
		container.setFont(font.deriveFont(font.getSize() * 1.2f));
	}
	
	public static void size110(Container container)
	{
		Font font = container.getFont();
		container.setFont(font.deriveFont(font.getSize() * 1.1f));
	}

	public static void size90(Container container)
	{
		Font font = container.getFont();
		container.setFont(font.deriveFont(font.getSize() * 0.9f));
	}

	public static void size80(Container container)
	{
		Font font = container.getFont();
		container.setFont(font.deriveFont(font.getSize() * 0.8f));
	}
}
