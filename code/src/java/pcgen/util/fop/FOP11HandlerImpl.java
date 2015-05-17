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


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.render.awt.AWTRenderer;

import pcgen.cdom.base.Constants;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

public class FOP11HandlerImpl implements FOPHandler 
{
	private File outFile;

    private Transformer transformer = null;
    private FopFactory fopFactory = null;
    private TransformerFactory factory = null;
	
//	private InputHandler inputHandler;
    private Source src = null;
	private AWTRenderer renderer;
	private StringBuffer errBuffer;
	private int mode;

	/**
	 * Constructor, defaults us to PDF Mode
	 */
	FOP11HandlerImpl()
	{
        fopFactory = FopFactory.newInstance();
        fopFactory.setStrictValidation(false);
        
        // Allow optional customization with configuration file
        String configPath = ConfigurationSettings.getOutputSheetsDir() + File.separator + "fop.xconf";
    	Logging.log(Logging.INFO,"Checking for config file at " + configPath);
        File userConfigFile = new File(configPath);
		if (userConfigFile.exists())
		{
			Logging.log(Logging.INFO, "FOPHandler using config file "
				+ configPath);
			try
			{
				fopFactory.setUserConfig(userConfigFile);
			}
			catch (Exception e)
			{
				Logging.errorPrint("Problem with FOP configuration "
					+ configPath + ": ", e);
			}
		}
		factory = TransformerFactory.newInstance();

		outFile = null;
		mode = PDF_MODE;

		errBuffer = new StringBuffer(32);
		//renderer = null;
	}

	/**
	 * Get the error message
	 * @return error message
	 */
	@Override
	public String getErrorMessage()
	{
		return errBuffer.toString();
	}

	/**
	 * Set the input file, e.g. immediately convert file into appropriate FOInputHandler type
	 * 
	 * @param in
	 */
	@Override
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

            transformer = factory.newTransformer(); // identity transformer		

			Logging.log(Logging.WARNING,"FOP input file set to: " + in.getAbsolutePath());
            src = new StreamSource(in);
		}
		catch (FileNotFoundException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
		catch (TransformerConfigurationException e)
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
	@Override
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

			Logging.log(Logging.WARNING,"FOP input file set to: " + xmlFile.getAbsolutePath());
            src = new StreamSource(xmlFile);
            transformer = factory.newTransformer(new StreamSource(xsltFile));
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
		catch (TransformerConfigurationException e)
		{
			errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FOPHandler:setInputFile", e);
		}
	}

	/**
	 * Set the mode
	 * @param m
	 */
	@Override
	public void setMode(int m)
	{
		mode = m;
	}

	/**
	 * Set the output file
	 * @param out
	 */
	@Override
	public void setOutputFile(File out)
	{
		outFile = out;
	}

	/**
	 * Get the Renderer
	 * @return Renderer
	 */
	@Override
	public AWTRenderer getPageable()
	{
		return renderer;
	}

	/**
	 * Run the FO to PDF/AWT conversion
	 */
    @Override
	public void run()
	{
		errBuffer.delete(0, errBuffer.length());

		if (src == null)
		{
			errBuffer.append("Export request failed. Please see console for details.").append(
				Constants.LINE_SEPARATOR);
			Logging.log(Logging.WARNING,"Error in FOPHandler:run - previous errors stopped intermediate file " +
					"from being produced. Could not produce output.");
			return;
		}
		
        OutputStream out = null;

        try {
        	File tempOut = null;
					try
					{
        		tempOut = (outFile == null ? File.createTempFile("printOutput", ".pdf") : outFile);
					}
        	catch (Exception ex)
					{
        		Logging.errorPrint("Can't create temp file");
		}
        	Logging.log(Logging.WARNING,"FOPHandler outFile = " + (tempOut == null ? "null" : tempOut));
            out = new BufferedOutputStream(new FileOutputStream(tempOut));

            org.apache.fop.apps.FOUserAgent userAgent = fopFactory.newFOUserAgent();
            userAgent.setProducer("PC Gen Character Generator");
            userAgent.setAuthor(System.getProperty("user.name"));
            userAgent.setCreationDate(new Date());

        	Logging.log(Logging.WARNING,"FOPHandler mode = " + mode);
            if (mode == PDF_MODE) {
                userAgent.setKeywords("PCGEN FOP PDF");
//                renderer = new PDFRenderer();
//                userAgent.setRendererOverride(renderer);
            } else if (mode == AWT_MODE) {
                userAgent.setKeywords("PCGEN FOP AWT");
                renderer = new AWTRenderer();
                renderer.setUserAgent(userAgent);
                userAgent.setRendererOverride(renderer);
            } else {
                Logging.errorPrint("Unsupported mode for file export.");

                return;
			}

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

            Result res = new SAXResult(fop.getDefaultHandler());

            FOPErrorListener errListener = new FOPErrorListener();
            transformer.setErrorListener(errListener);
            transformer.transform(src, res);
        } catch (TransformerException e) {
            errBuffer.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
            Logging.errorPrint("Exception in FOPHandler:run", e);
        } catch (FOPException fopex) {
            errBuffer.append(fopex.getMessage()).append(Constants.LINE_SEPARATOR);
				Logging.errorPrint("Exception in FOPHandler:run", fopex);
        } catch (FileNotFoundException fnfex) {
            errBuffer.append(fnfex.getMessage()).append(Constants.LINE_SEPARATOR);
            Logging.errorPrint(
                "Exception in FOPHandler:run, cannot find file: " +
                fnfex.getMessage());
        } catch (Exception ex) {
            errBuffer.append(ex.getMessage()).append(Constants.LINE_SEPARATOR);
            Logging.errorPrint(
                "Unexpected exception in FOPHandler:run: " +
                ex.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioex) {
                    errBuffer.append(ioex.getMessage())
                             .append(Constants.LINE_SEPARATOR);
                    Logging.errorPrint("Exception in FOPHandler:run", ioex);
			}
		}
		}

        out = null;
	}
    
    
    /**
     * The Class <code>FOPErrorListener</code> listens for notifications of issues when generating PDF files and 
     * responds accordingly.
     */
    public class FOPErrorListener implements ErrorListener
    {

		/**
		 * @{inheritdoc}
		 */
		@Override
		public void error(TransformerException exception)
			throws TransformerException
		{
			SourceLocator locator = exception.getLocator();
			Logging.errorPrint("FOP Error " + exception.getMessage() + " at " + getLocation(locator));
			throw exception;
		}

		/**
		 * @{inheritdoc}
		 */
		@Override
		public void fatalError(TransformerException exception)
			throws TransformerException
		{
			SourceLocator locator = exception.getLocator();
			Logging.errorPrint("FOP Fatal Error " + exception.getMessage() + " at " + getLocation(locator));
			throw exception;
		}

		/**
		 * @{inheritdoc}
		 */
		@Override
		public void warning(TransformerException exception)
			throws TransformerException
		{
			SourceLocator locator = exception.getLocator();
			Logging.log(Logging.WARNING, getLocation(locator) + exception.getMessage());
		}
		
		private String getLocation(SourceLocator locator)
		{
			if (locator == null)
			{
				return "Unknown; ";
			}
			StringBuilder builder = new StringBuilder();
			if (locator.getSystemId() != null)
			{
				builder.append(locator.getSystemId());
				builder.append("; ");
			}
			if (locator.getLineNumber() > -1)
			{
				builder.append("Line#: ");
				builder.append(locator.getLineNumber());
				builder.append("; ");
			}
			if (locator.getColumnNumber() > -1)
			{
				builder.append("Column#: ");
				builder.append(locator.getColumnNumber());
				builder.append("; ");
			}
			return builder.toString();
		}
    	
    }
}
