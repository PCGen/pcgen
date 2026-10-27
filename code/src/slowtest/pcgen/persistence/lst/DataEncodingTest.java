/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Checks all bundled LST and PCC files without loading campaigns or initializing PCGen. */
class DataEncodingTest
{
	@ParameterizedTest
	@ValueSource(strings = {"data", "system"})
	void dataFiles_should_useUtf8(String directory) throws IOException
	{
		try (var files = Files.walk(Path.of(directory)))
		{
			var dataFiles = files.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".lst") || path.toString().endsWith(".pcc"))
					.sorted().toList();
			assertFalse(dataFiles.isEmpty(), () -> "No LST or PCC files found in " + directory);

			assertAll("Invalid UTF-8 data files in " + directory, dataFiles.stream().map(path ->
					() -> assertDoesNotThrow(() -> Files.readString(path, StandardCharsets.UTF_8), path::toString)));
		}
	}
}
