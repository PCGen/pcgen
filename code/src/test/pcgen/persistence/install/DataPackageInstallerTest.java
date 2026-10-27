/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.persistence.install;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Exercises package inspection and extraction against real temporary ZIP files. */
class DataPackageInstallerTest
{
	private static final String MANIFEST = "CAMPAIGN:Caf\u00e9 package\nMINVER:1.0.0\nDEST:DATA\n";

	@TempDir
	Path directory;
	private Path data;
	private Path sheets;
	private Path application;

	@BeforeEach
	void setUp() throws IOException
	{
		data = Files.createDirectory(directory.resolve("data"));
		sheets = Files.createDirectory(directory.resolve("outputsheets"));
		application = Files.createDirectory(directory.resolve("application"));
	}

	@Test
	void read_should_findTheManifestRegardlessOfCase() throws IOException
	{
		var archive = writeArchive(Map.of("INSTALL.LST", MANIFEST, "data/book.lst", "book"));
		var contents = DataPackageInstaller.read(archive);
		assertAll(
				() -> assertEquals(MANIFEST, contents.installInfo()),
				() -> assertEquals(List.of("data/book.lst"),
						contents.entries().stream().map(DataPackageInstaller.ArchiveEntry::name).toList()));
	}

	@Test
	void read_should_rejectAnArchiveWithoutInstallationDetails() throws IOException
	{
		var archive = writeArchive(Map.of("readme.txt", "not a data package"));
		assertThrows(DataPackageInstaller.MissingInstallFileException.class,
				() -> DataPackageInstaller.read(archive));
	}

	@Test
	void install_should_routeEntriesAndCreateMissingParents() throws IOException
	{
		var archive = writePackage(Map.of(
				"data/publisher/book.lst", "book",
				"outputsheets/custom/sheet.ftl", "sheet",
				"notes/readme.txt", "notes",
				"data/empty/", ""));
		var plan = DataPackageInstaller.plan(DataPackageInstaller.read(archive), data, sheets, application);
		var progress = new ArrayList<Integer>();
		DataPackageInstaller.install(plan, progress::add);

		assertAll(
				() -> assertEquals("book", Files.readString(data.resolve("publisher/book.lst"))),
				() -> assertEquals("sheet", Files.readString(sheets.resolve("custom/sheet.ftl"))),
				() -> assertEquals("notes", Files.readString(application.resolve("notes/readme.txt"))),
				() -> assertTrue(Files.isDirectory(data.resolve("empty"))),
				() -> assertFalse(Files.exists(application.resolve("install.lst"))),
				() -> assertEquals(List.of(1, 2, 3, 4), progress));
	}

	@ParameterizedTest
	@ValueSource(strings = {"data/../../outside.lst", "data/a/b/../../../../outside.lst",
			"outputsheets/../../outside.lst", "../outside.lst", "..\\outside.lst", "/outside.lst",
			"data/../../escaped/", "../escaped/"})
	void plan_should_rejectEntriesOutsideTheirDestination(String entry) throws IOException
	{
		var contents = DataPackageInstaller.read(writePackage(Map.of(entry, "unsafe")));
		assertThrows(IOException.class, () -> DataPackageInstaller.plan(contents, data, sheets, application));
	}

	@Test
	void install_should_normalizeWindowsSeparatorsAndContainedParentSegments() throws IOException
	{
		var contents = DataPackageInstaller.read(writePackage(Map.of(
				"data\\publisher\\book.lst", "windows",
				"data/sub/../normalized.lst", "normalized")));
		DataPackageInstaller.install(DataPackageInstaller.plan(contents, data, sheets, application), _ -> { });
		assertAll(
				() -> assertEquals("windows", Files.readString(data.resolve("publisher/book.lst"))),
				() -> assertEquals("normalized", Files.readString(data.resolve("normalized.lst"))));
	}

	@Test
	void without_should_skipExistingFilesButInstallTheRemainder() throws IOException
	{
		Files.writeString(data.resolve("existing.lst"), "keep");
		var contents = DataPackageInstaller.read(writePackage(Map.of(
				"data/existing.lst", "replacement", "data/new.lst", "new")));
		var plan = DataPackageInstaller.plan(contents, data, sheets, application);
		assertEquals(List.of("data/existing.lst"), plan.overwrites().stream()
				.map(DataPackageInstaller.InstallEntry::name).toList());

		DataPackageInstaller.install(plan.without(plan.overwrites()), _ -> { });
		assertAll(
				() -> assertEquals("keep", Files.readString(data.resolve("existing.lst"))),
				() -> assertEquals("new", Files.readString(data.resolve("new.lst"))));
	}

	@Test
	void install_should_replaceAnApprovedExistingFile() throws IOException
	{
		Files.writeString(data.resolve("existing.lst"), "old");
		var contents = DataPackageInstaller.read(writePackage(Map.of("data/existing.lst", "replacement")));
		DataPackageInstaller.install(DataPackageInstaller.plan(contents, data, sheets, application), _ -> { });
		assertEquals("replacement", Files.readString(data.resolve("existing.lst")));
	}

	@Test
	void install_should_notOverwriteAFileCreatedAfterPlanning() throws IOException
	{
		var contents = DataPackageInstaller.read(writePackage(Map.of("data/new.lst", "package")));
		var plan = DataPackageInstaller.plan(contents, data, sheets, application);
		Files.writeString(data.resolve("new.lst"), "created later");
		assertThrows(FileAlreadyExistsException.class, () -> DataPackageInstaller.install(plan, _ -> { }));
		assertEquals("created later", Files.readString(data.resolve("new.lst")));
	}

	@Test
	void without_should_skipNonstandardEntries() throws IOException
	{
		var contents = DataPackageInstaller.read(writePackage(Map.of(
				"data/book.lst", "book", "notes/readme.txt", "notes")));
		var plan = DataPackageInstaller.plan(contents, data, sheets, application);
		assertEquals(List.of("notes/readme.txt"), plan.nonstandard().stream()
				.map(DataPackageInstaller.InstallEntry::name).toList());
		DataPackageInstaller.install(plan.without(plan.nonstandard()), _ -> { });
		assertAll(
				() -> assertEquals("book", Files.readString(data.resolve("book.lst"))),
				() -> assertFalse(Files.exists(application.resolve("notes"))));
	}

	@Test
	@EnabledOnOs({OS.LINUX, OS.MAC})
	void plan_should_rejectAnExistingSymlinkOutsideTheDestination() throws IOException
	{
		var outside = Files.createDirectory(directory.resolve("outside"));
		Files.createSymbolicLink(data.resolve("link"), outside);
		var contents = DataPackageInstaller.read(writePackage(Map.of("data/link/book.lst", "unsafe")));
		assertThrows(IOException.class, () -> DataPackageInstaller.plan(contents, data, sheets, application));
	}

	@Test
	@EnabledOnOs({OS.LINUX, OS.MAC})
	void install_should_recheckPathsAfterConfirmation() throws IOException
	{
		var contents = DataPackageInstaller.read(writePackage(Map.of("data/later/book.lst", "unsafe")));
		var plan = DataPackageInstaller.plan(contents, data, sheets, application);
		var outside = Files.createDirectory(directory.resolve("outside"));
		Files.createSymbolicLink(data.resolve("later"), outside);
		assertThrows(IOException.class, () -> DataPackageInstaller.install(plan, _ -> { }));
		assertFalse(Files.exists(outside.resolve("book.lst")));
	}

	private Path writePackage(Map<String, String> files) throws IOException
	{
		var entries = new LinkedHashMap<String, String>();
		entries.put("install.lst", MANIFEST);
		entries.putAll(files);
		return writeArchive(entries);
	}

	private Path writeArchive(Map<String, String> entries) throws IOException
	{
		Path archive = directory.resolve("dataset.pcz");
		try (var zip = new ZipOutputStream(Files.newOutputStream(archive)))
		{
			for (var entry : entries.entrySet())
			{
				zip.putNextEntry(new ZipEntry(entry.getKey()));
				zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
				zip.closeEntry();
			}
		}
		return archive;
	}
}
