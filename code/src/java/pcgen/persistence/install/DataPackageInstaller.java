/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.persistence.install;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.zip.ZipFile;

/** Inspects and extracts PCGen data packages without depending on a UI toolkit. */
public final class DataPackageInstaller
{
	private DataPackageInstaller()
	{
	}

	/**
	 * An inspected package, excluding its root installation manifest from extraction.
	 *
	 * @param archive the package file
	 * @param installInfo the UTF-8 manifest text
	 * @param entries the archive entries to install
	 */
	public record DataPackage(Path archive, String installInfo, List<ArchiveEntry> entries)
	{
	}

	/**
	 * A file or directory in the archive.
	 *
	 * @param name the original ZIP entry name
	 * @param directory whether the entry represents a directory
	 */
	public record ArchiveEntry(String name, boolean directory)
	{
	}

	/**
	 * An entry mapped to its installation destination.
	 *
	 * @param name the original ZIP entry name
	 * @param target the canonical output path
	 * @param base the permitted destination root
	 * @param directory whether the entry represents a directory
	 * @param nonstandard whether it lies outside data and outputsheets
	 * @param overwrite whether the target existed when the plan was made
	 */
	public record InstallEntry(String name, Path target, Path base, boolean directory,
			boolean nonstandard, boolean overwrite)
	{
	}

	/**
	 * The entries the caller has approved for installation.
	 *
	 * @param data the inspected package
	 * @param entries the entries remaining after any user exclusions
	 */
	public record Plan(DataPackage data, List<InstallEntry> entries)
	{
		/**
		 * Returns entries that require nonstandard-location confirmation.
		 *
		 * @return the nonstandard entries
		 */
		public List<InstallEntry> nonstandard()
		{
			return entries.stream().filter(InstallEntry::nonstandard).toList();
		}

		/**
		 * Returns existing files that require overwrite confirmation.
		 *
		 * @return the entries that would replace files
		 */
		public List<InstallEntry> overwrites()
		{
			return entries.stream().filter(InstallEntry::overwrite).toList();
		}

		/**
		 * Omits entries the user declined without discarding the remainder.
		 *
		 * @param excluded the declined entries
		 * @return the filtered installation plan
		 */
		public Plan without(List<InstallEntry> excluded)
		{
			return new Plan(data, entries.stream().filter(entry -> !excluded.contains(entry)).toList());
		}
	}

	/** Indicates that a ZIP is not a PCGen data package. */
	public static final class MissingInstallFileException extends IOException
	{
		/**
		 * Identifies the archive missing its manifest.
		 *
		 * @param archive the selected archive
		 */
		public MissingInstallFileException(Path archive)
		{
			super("No install.lst in " + archive);
		}
	}

	/**
	 * Reads the root manifest and enumerates the package's files and directories.
	 *
	 * @param archive the selected PCZ or ZIP file
	 * @return the package contents
	 * @throws IOException if the archive or its manifest cannot be read
	 */
	public static DataPackage read(Path archive) throws IOException
	{
		try (var zip = new ZipFile(archive.toFile()))
		{
			var manifest = zip.stream().filter(entry -> entry.getName().equalsIgnoreCase("install.lst"))
					.findFirst().orElseThrow(() -> new MissingInstallFileException(archive));
			var text = new StringWriter();
			try (var reader = new InputStreamReader(zip.getInputStream(manifest), StandardCharsets.UTF_8.newDecoder()))
			{
				reader.transferTo(text);
			}
			var entries = zip.stream().filter(entry -> !entry.getName().equalsIgnoreCase("install.lst"))
					.map(entry -> new ArchiveEntry(entry.getName(), entry.getName().replace('\\', '/').endsWith("/")))
					.toList();
			return new DataPackage(archive, text.toString(), entries);
		}
	}

	/**
	 * Resolves every archive path before any files are written.
	 *
	 * @param data the inspected package
	 * @param dataDirectory the selected Data, Vendor Data, or Homebrew directory
	 * @param sheetDirectory the configured output-sheet directory
	 * @param applicationDirectory the root for nonstandard entries
	 * @return the mapped entries and their confirmation requirements
	 * @throws IOException if a path escapes its destination or the data directory is missing
	 */
	public static Plan plan(DataPackage data, Path dataDirectory, Path sheetDirectory,
			Path applicationDirectory) throws IOException
	{
		if (!Files.isDirectory(dataDirectory))
		{
			throw new NotDirectoryException(dataDirectory.toString());
		}
		var entries = new ArrayList<InstallEntry>();
		for (var entry : data.entries())
		{
			String name = entry.name().replace('\\', '/');
			Path base;
			String relative;
			boolean nonstandard;
			if (name.regionMatches(true, 0, "data/", 0, 5))
			{
				base = dataDirectory;
				relative = name.substring(5);
				nonstandard = false;
			}
			else if (name.regionMatches(true, 0, "outputsheets/", 0, 13))
			{
				base = sheetDirectory;
				relative = name.substring(13);
				nonstandard = false;
			}
			else
			{
				base = applicationDirectory;
				relative = name;
				nonstandard = true;
			}
			base = base.toFile().getCanonicalFile().toPath();
			Path target = contained(base, base.resolve(relative));
			entries.add(new InstallEntry(entry.name(), target, base, entry.directory(),
					nonstandard, !entry.directory() && Files.exists(target)));
		}
		return new Plan(data, List.copyOf(entries));
	}

	/**
	 * Extracts an approved plan, creating parents even when the ZIP omits directories.
	 *
	 * @param plan the entries approved by the caller
	 * @param progress receives the number of entries completed
	 * @throws IOException if an entry cannot be read or safely written
	 */
	public static void install(Plan plan, IntConsumer progress) throws IOException
	{
		try (var zip = new ZipFile(plan.data().archive().toFile()))
		{
			int completed = 0;
			for (var entry : plan.entries())
			{
				Path target = contained(entry.base(), entry.target());
				if (entry.directory())
				{
					Files.createDirectories(target);
				}
				else
				{
					var source = zip.getEntry(entry.name());
					if (source == null || source.isDirectory())
					{
						throw new IOException("Package no longer contains file " + entry.name());
					}
					Files.createDirectories(target.getParent());
					var creation = entry.overwrite() ? StandardOpenOption.CREATE : StandardOpenOption.CREATE_NEW;
					try (var input = zip.getInputStream(source);
							var output = Files.newOutputStream(target, creation,
									StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))
					{
						input.transferTo(output);
					}
				}
				progress.accept(++completed);
			}
		}
	}

	/** Checks canonical paths so existing symlinks cannot redirect an entry outside its root. */
	private static Path contained(Path base, Path target) throws IOException
	{
		Path canonical = target.toFile().getCanonicalFile().toPath();
		if (!canonical.startsWith(base))
		{
			throw new IOException("Archive entry escapes " + base + ": " + target);
		}
		return canonical;
	}
}
