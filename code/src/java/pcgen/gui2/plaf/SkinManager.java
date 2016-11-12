package pcgen.gui2.plaf;

/*
 * SkinManager.java
 * Copyright 2001 (C) Jason Buchanan
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
 * Created on January 3, 2002
 */
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import javax.swing.LookAndFeel;

/**
 * {@code SkinManager} ???
 *
 * @author Jason Buchanan
 */
public final class SkinManager
{

	/**
	 * Apply a skin to PCGen GUI
	 *
	 * @param themePath a string describing the path to a theme file
	 * @return a LookAndFeel instance
	 */
	public static LookAndFeel createSkinLAF(String themePath) throws Exception
	{
		SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePack(themePath));
		return new SkinLookAndFeel();
	}

}
