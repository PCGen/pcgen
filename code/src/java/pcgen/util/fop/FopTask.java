/*
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
 */
package pcgen.util.fop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import pcgen.cdom.base.Constants;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.events.Event;
import org.apache.fop.events.EventFormatter;
import org.apache.fop.events.EventListener;
import org.apache.fop.events.model.EventSeverity;
import org.apache.fop.render.Renderer;

/**
 * This class is used to generate pdf files from an xml source. There are two ways to define the
 * source of the task: files or inputstreams. The output of this task can either be an OutputStream
 * which you can point to a file, or a Renderer. The Renderer is used by print preview and for
 * direct printing.
 */
public final class FopTask implements Runnable
{
	private static final FopFactory FOP_FACTORY = createFopFactory();
	private static FOUserAgent userAgent;

	private static final TransformerFactory TRANS_FACTORY = TransformerFactory.newInstance();

	private static FopFactory createFopFactory()
	{

		// Allow optional customization with configuration file
		String configPath = ConfigurationSettings.getOutputSheetsDir() + File.separator + "fop.xconf";
		Logging.log(Logging.INFO, "FoPTask checking for config file at " + configPath);
		File userConfigFile = new File(configPath);
		FopFactoryBuilder builder;
		if (userConfigFile.exists())
		{
			Logging.log(Logging.INFO, "FoPTask using config file " + configPath);
			FopConfParser parser;
			try
			{
				parser = new FopConfParser(userConfigFile);
			}
			catch (Exception e)
			{
				Logging.errorPrint("FoPTask encountered a problem with FOP configuration " + configPath + ": ", e);
				return null;
			}
			builder = parser.getFopFactoryBuilder();
		}
		else
		{
			Logging.log(Logging.INFO, "FoPTask using default config");
			builder = new FopFactoryBuilder(new File(".").toURI());
			builder.setStrictFOValidation(false);
		}
		return builder.build();
	}

	private final StreamSource inputSource;
	private final StreamSource xsltSource;
	private final Renderer renderer;
	private final OutputStream outputStream;

	private final StringBuilder errorBuilder = new StringBuilder(32);

	private FopTask(StreamSource inputXml, StreamSource xsltSource, Renderer renderer, OutputStream outputStream)
	{
		this.inputSource = inputXml;
		this.xsltSource = xsltSource;
		this.renderer = renderer;
		this.outputStream = outputStream;
	}

	private static StreamSource createXsltStreamSource(File xsltFile) throws FileNotFoundException
	{
		if (xsltFile == null)
		{
			return null;
		}
		if (!xsltFile.exists())
		{
			throw new FileNotFoundException("xsl file " + xsltFile.getAbsolutePath() + " not found ");
		}
		return new StreamSource(xsltFile);
	}

	public static FopFactory getFactory()
	{
		return FOP_FACTORY;
	}

	/**
	 * Creates a new FopTask that transforms the input stream using the given xsltFile and outputs a
	 * pdf document to the given output stream. The output can be saved to a file if a
	 * FileOutputStream is used.
	 *
	 * @param inputXmlStream the fop xml input stream
	 * @param xsltFile the transform template file, if null then the identity transformer is used
	 * @param outputPdf output stream for pdf document
	 * @return a FopTask to be executed
	 * @throws FileNotFoundException if xsltFile is not null and does not exist
	 */
	public static FopTask newFopTask(InputStream inputXmlStream, File xsltFile, OutputStream outputPdf)
		throws FileNotFoundException
	{
		StreamSource xsltSource = createXsltStreamSource(xsltFile);
		userAgent = FOP_FACTORY.newFOUserAgent();
		return new FopTask(new StreamSource(inputXmlStream), xsltSource, null, outputPdf);
	}

	/**
	 * Creates a new FopTask that transforms the input stream using the given xsltFile and outputs a
	 * pdf document to the given Renderer. This task can be used for both previewing a pdf
	 * document as well as printing a pdf
	 *
	 * @param inputXmlStream the fop xml input stream
	 * @param xsltFile the transform template file, if null then the identity transformer is used
	 * @param renderer the Renderer to output a pdf document to.
	 * @return a FopTask to be executed
	 * @throws FileNotFoundException if xsltFile is not null and does not exist
	 */
	public static FopTask newFopTask(InputStream inputXmlStream, File xsltFile, Renderer renderer)
		throws FileNotFoundException
	{
		StreamSource xsltSource = createXsltStreamSource(xsltFile);
		userAgent = renderer.getUserAgent();
		return new FopTask(new StreamSource(inputXmlStream), xsltSource, renderer, null);
	}

	public String getErrorMessages()
	{
		return errorBuilder.toString();
	}

	/**
	 * Run the FO to PDF/AWT conversion. This automatically closes any provided OutputStream for
	 * this FopTask.
	 */
	@Override
	public void run()
	{
		try (OutputStream out = outputStream)
		{
			userAgent.setProducer("PC Gen Character Generator");
			userAgent.setAuthor(System.getProperty("user.name"));
			userAgent.setCreationDate(Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0))));

			userAgent.getEventBroadcaster().addEventListener(new FOPEventListener());

			String mimeType;
			if (renderer != null)
			{
				userAgent.setKeywords("PCGEN FOP PREVIEW");
				mimeType = MimeConstants.MIME_FOP_AWT_PREVIEW;
			}
			else
			{
				userAgent.setKeywords("PCGEN FOP PDF");
				mimeType = org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
			}
			Fop fop;
			if (out != null)
			{
				fop = FOP_FACTORY.newFop(mimeType, userAgent, out);
			}
			else
			{
				fop = FOP_FACTORY.newFop(mimeType, userAgent);
			}

			Transformer transformer;
			if (xsltSource != null)
			{
				transformer = TRANS_FACTORY.newTransformer(xsltSource);
			}
			else
			{
				transformer = TRANS_FACTORY.newTransformer(); // identity transformer		
			}
			transformer.setErrorListener(new FOPErrorListener());
			transformer.transform(inputSource, new SAXResult(fop.getDefaultHandler()));
		}
		catch (TransformerException | FOPException | IOException e)
		{
			errorBuilder.append(e.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Exception in FopTask:run", e);
		}
		catch (RuntimeException ex)
		{
			errorBuilder.append(ex.getMessage()).append(Constants.LINE_SEPARATOR);
			Logging.errorPrint("Unexpected exception in FopTask:run: ", ex);
		}
	}

	/**
	 * The Class {@code FOPErrorListener} listens for notifications of issues when generating
	 * PDF files and responds accordingly.
	 */
	private static class FOPErrorListener implements ErrorListener
	{

		@Override
		public void error(TransformerException exception) throws TransformerException
		{
			SourceLocator locator = exception.getLocator();
			Logging.errorPrint("FOP Error " + exception.getMessage() + " at " + getLocation(locator));
			throw exception;
		}

		@Override
		public void fatalError(TransformerException exception) throws TransformerException
		{
			SourceLocator locator = exception.getLocator();
			Logging.errorPrint("FOP Fatal Error " + exception.getMessage() + " at " + getLocation(locator));
			throw exception;
		}

		@Override
		public void warning(TransformerException exception)
        {
			SourceLocator locator = exception.getLocator();
			Logging.log(Logging.WARNING, getLocation(locator) + exception.getMessage());
		}

		private static String getLocation(SourceLocator locator)
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

	private static class FOPEventListener implements EventListener
	{
		/**
		 * @{inheritdoc}
		 */
		@Override
		public void processEvent(final Event event)
		{
			String msg = "[FOP] " + EventFormatter.format(event);

			// filter out some erroneous FOP warnings about not finding internal fonts
			// this is an ancient, but still unfixed FOP bug
			// see https://issues.apache.org/jira/browse/FOP-1667
			if (msg.contains("not found") && (msg.contains("Symbol,normal") || msg.contains("ZapfDingbats,normal")))
			{
				return;
			}

			EventSeverity severity = event.getSeverity();
			if (severity == EventSeverity.INFO)
			{
				Logging.log(Logging.INFO, msg);
			}
			else if (severity == EventSeverity.WARN)
			{
				Logging.log(Logging.WARNING, msg);
			}
			else if (severity == EventSeverity.ERROR || severity == EventSeverity.FATAL)
			{
				Logging.log(Logging.ERROR, msg);
			}
			else
			{
				assert false;
			}
		}
	}
}
