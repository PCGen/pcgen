/*
 * FopTask.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jan 3, 2016, 9:14:07 PM
 */
package pcgen.util.fop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.render.Renderer;
import pcgen.cdom.base.Constants;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class FopTask implements Runnable
{

	private static final TransformerFactory TRANS_FACTORY = TransformerFactory.newInstance();

	private static FopFactory createFopFactory()
	{
		FopFactory fopFactory = FopFactory.newInstance();
		fopFactory.setStrictValidation(false);

		// Allow optional customization with configuration file
		String configPath = ConfigurationSettings.getOutputSheetsDir() + File.separator + "fop.xconf";
		Logging.log(Logging.INFO, "Checking for config file at " + configPath);
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
		return fopFactory;
	}

	private final StreamSource inputSource;
	private final StreamSource xsltSource;
	private final Renderer renderer;
	private final OutputStream outputStream;

	private StringBuilder errorBuilder = new StringBuilder(32);

	private FopTask(StreamSource inputXml, StreamSource xsltSource, Renderer renderer, OutputStream outputStream)
	{
		this.inputSource = inputXml;
		this.xsltSource = xsltSource;
		this.renderer = renderer;
		this.outputStream = outputStream;
	}

	public static FopTask newFopTask(InputStream inputXml, File xsltFile, OutputStream outputPdf)
	{
		return new FopTask(new StreamSource(inputXml), new StreamSource(xsltFile), null, outputPdf);
	}

	public static FopTask newFopTask(File inputXmlFile, File xsltFile, OutputStream outputPdf)
			throws FileNotFoundException
	{
		if (inputXmlFile == null)
		{
			throw new NullPointerException(
					"XML file must be specified for the tranform mode");
		}
		if (!inputXmlFile.exists())
		{
			throw new FileNotFoundException("xml file "
					+ inputXmlFile.getAbsolutePath() + " not found ");
		}
		if (xsltFile == null)
		{
			throw new NullPointerException(
					"XSLT file must be specified for the tranform mode");
		}
		if (!xsltFile.exists())
		{
			throw new FileNotFoundException("xsl file "
					+ xsltFile.getAbsolutePath() + " not found ");
		}

		Logging.log(Logging.WARNING, "FOP input file set to: " + inputXmlFile.getAbsolutePath());

		return new FopTask(new StreamSource(inputXmlFile), new StreamSource(xsltFile), null, outputPdf);
	}

	public static FopTask newFopTask(InputStream inputXml, File xsltFile, Renderer renderer) throws FileNotFoundException
	{
		if (xsltFile == null)
		{
			throw new NullPointerException(
					"XSLT file must be specified for the tranform mode");
		}
		if (!xsltFile.exists())
		{
			throw new FileNotFoundException("xsl file "
					+ xsltFile.getAbsolutePath() + " not found ");
		}
		return new FopTask(new StreamSource(inputXml), new StreamSource(xsltFile), renderer, null);
	}

	public String getErrorMessages()
	{
		return errorBuilder.toString();
	}

	@Override
	public void run()
	{
		try
		{
			FopFactory factory = createFopFactory();

			FOUserAgent userAgent = factory.newFOUserAgent();
			userAgent.setProducer("PC Gen Character Generator");
			userAgent.setAuthor(System.getProperty("user.name"));
			userAgent.setCreationDate(new Date());

			String mimeType;
			if (renderer != null)
			{
				userAgent.setKeywords("PCGEN FOP RENDERER");
				userAgent.setRendererOverride(renderer);
				renderer.setUserAgent(userAgent);
				mimeType = MimeConstants.MIME_FOP_AWT_PREVIEW;
			}
			else
			{
				userAgent.setKeywords("PCGEN FOP PDF");
				mimeType = MimeConstants.MIME_PDF;
			}
			Fop fop;
			if (outputStream != null)
			{
				fop = factory.newFop(mimeType, userAgent, outputStream);
			}
			else
			{
				fop = factory.newFop(mimeType, userAgent);
			}

			Transformer transformer;
			if (xsltSource != null)
			{
				transformer = TRANS_FACTORY.newTransformer(xsltSource);
			}
			else
			{
				transformer = TRANS_FACTORY.newTransformer();// identity transformer		
			}
			transformer.setErrorListener(new FOPErrorListener());
			transformer.transform(inputSource, new SAXResult(fop.getDefaultHandler()));
		}
		catch (TransformerException e)
		{
			errorBuilder.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FopTask:run", e);
		}
		catch (FOPException fopex)
		{
			errorBuilder.append(fopex.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FopTask:run", fopex);
		}
		catch (RuntimeException ex)
		{
			errorBuilder.append(ex.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint(
					"Unexpected exception in FopTask:run: "
					+ ex.getMessage());
		}
		finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				}
				catch (IOException ex)
				{
					errorBuilder.append(ex.getMessage())
							.append(Constants.LINE_SEPARATOR);
					Logging.errorPrint("Exception in FOPHandler:run", ex);
				}
			}
		}
	}

	/**
	 * The Class <code>FOPErrorListener</code> listens for notifications of issues when generating
	 * PDF files and responds accordingly.
	 */
	public static class FOPErrorListener implements ErrorListener
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
