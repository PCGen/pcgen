/*
 * HTMLUtils.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui;

/**
 * A utility class to simplify dealing with HTML controls.
 * 
 * @author boomer70
 */
public final class HTMLUtils
{
	/** Constant for HTML start tag */
	public static final String HTML = "<html>"; //$NON-NLS-1$
	/** Constant for HTML end tag */
	public static final String END_HTML = "</html>"; //$NON-NLS-1$
	/** Constant for HTML font size +1 tag */
	public static final String FONT_PLUS_1 = "<font size=+1>"; //$NON-NLS-1$
	/** Constant for HTML font end tag */
	public static final String END_FONT = "</font>"; //$NON-NLS-1$
	/** Constant for HTML paragraph tag */
	public static final String PARA = "<p>"; //$NON-NLS-1$
	/** Constant for HTML bold start tag */
	public static final String BOLD = "<b>"; //$NON-NLS-1$
	/** Constant for HTML bold end tag */
	public static final String END_BOLD = "</b>"; //$NON-NLS-1$
	/** Constant for HTML line break tag */
	public static final String BR = "<br>"; //$NON-NLS-1$
	/** Constant for HTML italic start tag */
	public static final String ITALIC = "<i>"; //$NON-NLS-1$
	/** Constant for HTML italic end tag */
	public static final String END_ITALIC = "</i>"; //$NON-NLS-1$
	/** Constant for HTML unordered (bulleted) list start tag */
	public static final String UL = "<ul>"; //$NON-NLS-1$
	/** Constant for HTML unordered (bulleted) list end tag */
	public static final String END_UL = "</ul>"; //$NON-NLS-1$
	/** Constant for HTML list item start tag */
	public static final String LI = "<li>"; //$NON-NLS-1$
	/** Constant for HTML list item end tag */
	public static final String END_LI = "</li>"; //$NON-NLS-1$

	/** Constant for 2 spaces in HTML */
	public static final String TWO_SPACES = " &nbsp;"; //$NON-NLS-1$

	/** Constant for 3 spaces in HTML */
	public static final String THREE_SPACES = " &nbsp; "; //$NON-NLS-1$
}
