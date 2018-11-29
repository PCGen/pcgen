/*
 * Copyright 2014 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 */package pcgen.gui2.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JMenu;

/**
 * PCGMenu extends JMenu to allow us to customise it. In particular we 
 * are overriding the location of the tooltips for the menu.
 */
@SuppressWarnings("serial")
public class PCGMenu extends JMenu
{

	public PCGMenu(Action a)
	{
		super(a);
	}

	@Override
	public Point getToolTipLocation(MouseEvent event)
	{
		Dimension size = getSize();
		double oneRowUpHeight = (size.getHeight() * -1) - 5;
		return new Point((int) size.getWidth(), (int) oneRowUpHeight);
	}
}
