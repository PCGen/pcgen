/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package pcgen.util;

/**
 * Escapes plain text for HTML element content and double-quoted attributes.
 *
 * <p>These methods do not encode URLs, JavaScript, or CSS.
 */
public final class Html
{
	private Html()
	{
	}

	/**
	 * Escapes {@code &} and {@code <} for text between HTML tags.
	 *
	 * @param text the plain text, or {@code null}
	 * @return the escaped text, or an empty string for {@code null}
	 */
	public static String escape(String text)
	{
		if (text == null)
		{
			return "";
		}
		var out = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			switch (c)
			{
				case '&' -> out.append("&amp;");
				case '<' -> out.append("&lt;");
				default -> out.append(c);
			}
		}
		return out.toString();
	}

	/**
	 * Escapes text for a double-quoted HTML attribute.
	 *
	 * @param text the attribute value, or {@code null}
	 * @return the escaped value, or an empty string for {@code null}
	 */
	public static String escapeAttr(String text)
	{
		return escape(text).replace(">", "&gt;").replace("\"", "&quot;");
	}
}
