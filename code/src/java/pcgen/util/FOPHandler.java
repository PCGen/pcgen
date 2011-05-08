/*
 * FOPHandler.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOInputHandler;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.InputHandler;
import org.apache.fop.apps.XSLTInputHandler;
import org.apache.fop.render.Renderer;
import org.apache.fop.render.awt.AWTRenderer;
import org.apache.fop.viewer.SecureResourceBundle;
import org.xml.sax.XMLReader;

import pcgen.cdom.base.Constants;

/**
 * Title:        FOPHandler.java
 * Description:  Interface to the Apache FOP API (0.20.5);
 *               this class handles all the interaction
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision$
 */
public final class FOPHandler implements Runnable
{
	/** PDF_MODE = 0 */
	public static final int PDF_MODE = 0;

	/** AWT_MODE = 1 */
	public static final int AWT_MODE = 1;

	private Driver driver;
	private File outFile;
	private FileOutputStream fos;

	private InputHandler inputHandler;
	private Renderer renderer;
	private StringBuffer errBuffer;
	private int mode;

	/**
	 * Constructor, defaults us to PDF Mode
	 */
	public FOPHandler()
	{
		driver = new Driver();
		inputHandler = null;
		outFile = null;
		mode = PDF_MODE;
		errBuffer = new StringBuffer();
		renderer = null;
	}

	/**
	 * Get the error message
	 * @return error message
	 */
	public String getErrorMessage()
	{
		return errBuffer.toString();
	}

	/**
	 * Set the input file, e.g. immediately convert file into appropriate FOInputHandler type
	 * 
	 * @param in
	 */
	public void setInputFile(File in)
	{
		try
		{
			if (in == null)
			{
				throw new NullPointerException(
					"XML file must be specified for the tranform mode");
			}

			if (!in.exists())
			{
				throw new FileNotFoundException("xml file "
					+ in.getAbsolutePath() + " not found ");
			}

			inputHandler = new FOInputHandler(in);
		}
		catch (FileNotFoundException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
	}

	/**
	 * Immediately convert file into appropriate InputHandler type
	 * 
	 * @param xmlFile
	 * @param xsltFile
	 */
	public void setInputFile(File xmlFile, File xsltFile)
	{
		try
		{
			if (xmlFile == null)
			{
				throw new NullPointerException(
					"XML file must be specified for the tranform mode");
			}

			if (xsltFile == null)
			{
				throw new NullPointerException(
					"XSLT file must be specified for the tranform mode");
			}

			if (!xmlFile.exists())
			{
				throw new FileNotFoundException("xml file "
					+ xmlFile.getAbsolutePath() + " not found ");
			}

			if (!xsltFile.exists())
			{
				throw new FileNotFoundException("xsl file "
					+ xsltFile.getAbsolutePath() + " not found ");
			}

			inputHandler = new XSLTInputHandler(xmlFile, xsltFile);
		}
		catch (FileNotFoundException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
		catch (NullPointerException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
		catch (FOPException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
	}

	/**
	 * Set the mode
	 * @param m
	 */
	public void setMode(int m)
	{
		mode = m;
	}

	/**
	 * Set the output file
	 * @param out
	 */
	public void setOutputFile(File out)
	{
		outFile = out;
	}

	/**
	 * Get the Renderer
	 * @return Renderer
	 */
	public Renderer getRenderer()
	{
		return renderer;
	}

	/**
	 * Run the FO to PDF/AWT conversion
	 */
	public void run()
	{
		errBuffer.delete(0, errBuffer.length());

		if (inputHandler == null)
		{
			errBuffer.append("Export request failed. Please see console for details.").append(
				Constants.LINE_SEPARATOR);
			Logging.errorPrint("Error in FOPHandler:run - previous errors stopped intermediate file " +
					"from being produced. Could not produce output.");
			return;
		}
		
		// Reset the driver from the last run
		driver.reset();

		// PDF Mode
		if (mode == PDF_MODE)
		{
			fos = null;
			renderer = null;
			driver.setRenderer(Driver.RENDER_PDF);

			/* 
			 * Expanded functionality to be able to handle XSLT files.
			 * Now operates based on InputHandlers.
			 */
			try
			{
				XMLReader parser = inputHandler.getParser();

				Map<String, Boolean> rendererOptions =
						new HashMap<String, Boolean>();
				rendererOptions.put("fineDetail", Boolean.valueOf(false));
				driver.getRenderer().setOptions(rendererOptions);
				driver.getRenderer().setProducer("PC Gen Character Generator");
				fos = new FileOutputStream(outFile);
				driver.setOutputStream(fos);
				driver.render(parser, inputHandler.getInputSource());
			}
			catch (FOPException fopex)
			{
				errBuffer.append(fopex.getMessage()).append(
					Constants.LINE_SEPARATOR);
				Logging.errorPrint("Exception in FOPHandler:run", fopex);
			}
			catch (FileNotFoundException fnfex)
			{
				errBuffer.append(fnfex.getMessage()).append(
					Constants.LINE_SEPARATOR);
				Logging
					.errorPrint("Exception in FOPHandler:run, cannot find file: "
						+ fnfex.getMessage());
			}
			finally
			{
				if (fos != null)
				{
					try
					{
						fos.close();
					}
					catch (IOException ioex)
					{
						errBuffer.append(ioex.getMessage()).append(
							Constants.LINE_SEPARATOR);
						Logging.errorPrint("Exception in FOPHandler:run", ioex);
					}
				}
			}
		}
		else if (mode == AWT_MODE)
		{
			renderer = createAWTRenderer();
			driver.setRenderer(renderer);
			try
			{
				XMLReader parser;
				parser = inputHandler.getParser();
				driver.render(parser, inputHandler.getInputSource());
			}
			catch (FOPException fopex)
			{
				errBuffer.append(fopex.getMessage()).append(
					Constants.LINE_SEPARATOR);
				Logging.errorPrint("Exception in FOPHandler:run", fopex);
			}
		}
		else
		{
			Logging.errorPrint("Unsupported mode for file export.");
		}
	}

	/**
	 * Get the AWT Renderer
	 * @return AWTRenderer
	 */
	private static AWTRenderer createAWTRenderer()
	{
		final byte[] bytes = new byte[0];

		return new AWTRenderer(new SecureResourceBundle(
			new ByteArrayInputStream(bytes)));
	}
}
