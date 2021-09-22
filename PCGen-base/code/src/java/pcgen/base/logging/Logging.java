/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.logging;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * The Logging class represents a simple logging system for use in PCGen's base libraries.
 * This is done so the PCGen libraries do not have external dependencies. It is intended
 * that the processor is overridden by larger systems to integrate into more formal
 * logging frameworks.
 */
public final class Logging
{

	/**
	 * The Logging processor. By default will print to System.err or System.out.
	 */
	@SuppressWarnings("PMD.SystemPrintln")
	private static BiConsumer<Severity, Supplier<String>> loggingProcessor =
			(severity, message) -> {
				if (severity == Severity.ERROR)
				{
					System.err.println(message.get());
				}
				else
				{
					System.out.println(message.get());
				}
			};

	/**
	 * Do not construct a utility class
	 */
	private Logging()
	{
		//Do not construct utility class
	}

	/**
	 * Sets the Logging processor for this Logging system.
	 * 
	 * @param processor
	 *            The Processor to be used to consume messages produced by the PCGen
	 *            libraries.
	 */
	public static void setProcessor(
		BiConsumer<Severity, Supplier<String>> processor)
	{
		loggingProcessor = Objects.requireNonNull(processor);
	}

	/**
	 * Log an issue for the Logging Processor.
	 * 
	 * @param severity
	 *            The Severity of the issue to be logged
	 * @param message
	 *            A Supplier of the message to be logged
	 */
	public static void log(Severity severity, Supplier<String> message)
	{
		loggingProcessor.accept(severity, message);
	}
}
