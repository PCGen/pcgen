/*
 * Copyright 2003 (C) Devon Jones
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
 * $Id$
 */
 package pcgen.gui2.doomsdaybook;

import pcgen.gui2.dialog.RandomNameDialog;

/**
 * A standalone window to run the name generator. It is not used directly by 
 * PCGen but instead can be invoked on its own from the command line.
 * 
 * @author  devon
 */
@SuppressWarnings("serial")
public class NameGenerator extends javax.swing.JFrame
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		RandomNameDialog dialog = new RandomNameDialog(null, null);
		dialog.setVisible(true);
		System.exit(0);
	}
}
