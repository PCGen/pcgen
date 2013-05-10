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
import java.io.FileOutputStream;

public class FOP11HandlerImpl implements FOPHandler {

	private File outFile;
	private FileOutputStream fos;

	private StringBuilder errBuffer;
	private int mode;

	/**
	 * Constructor, defaults us to PDF Mode
	 */
	FOP11HandlerImpl()
	{
		outFile = null;
		mode = PDF_MODE;
		errBuffer = new StringBuilder();
	}


	@Override
	public String getErrorMessage() {
		return errBuffer.toString();
	}

	@Override
	public void setInputFile(File in) {
		//TODO Unimplemented
	}

	@Override
	public void setInputFile(File xmlFile, File xsltFile) {
		//TODO Unimplemented
	}

	@Override
	public void setMode(int m) {
		//TODO Unimplemented
	}

	@Override
	public void setOutputFile(File out) {
		//TODO Unimplemented
	}

	@Override
	@Deprecated
	public Pageable getPageable() {
		return null;
	}

	@Override
	public void run() {
		//TODO Unimplemented
	}
}
