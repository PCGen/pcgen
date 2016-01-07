/*
 * FOP11HandlerImpl.java
 * Copyright 2013 (C) Jonas Karlsson <jk@xdy.se>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.util.fop;

import java.awt.print.Pageable;
import java.io.File;

/*
General plan (with - at least - a commit after each stage.) If I fail to complete all tasks below the FOP code will at least not be in a worse state than now.
1) FopHandler > Interface, Factory and (current) FOP0205HandlerImpl.
2) Add necessary libs for FOP 1.1, and change the build files to include them as well as create a non-working FOP11HandlerImpl and change factory to switch implementation based on property, add aforementioned property.
3) This might be a good point to also add the config file support mentioned in a comment below.
4) Convert a d20 pdf sheet (csheet_fantasy_simple_blackandwhite.xslt) to
5) Work on FOP11HandlerImpl (and probably the sheet at the same time) until it seems to work for my selected example.
7) Possibly convert more sheets. Unless conversion is found to be pretty much an easily discovered and fairly simple set of search and replace actions I'm unlikely to complete this, at least not unassisted. My graphic design skills are pretty much non-existent. :)

 */


public interface FOPHandler extends Runnable {
	/** PDF_MODE = 0 */
	int PDF_MODE = 0;
	/** AWT_MODE = 1 */
	int AWT_MODE = 1;

	String getErrorMessage();

	void setInputFile(File in);

	void setInputFile(File xmlFile, File xsltFile);

	void setMode(int m);

	void setOutputFile(File out);

	Pageable getPageable();

	@Override
	void run();
}
