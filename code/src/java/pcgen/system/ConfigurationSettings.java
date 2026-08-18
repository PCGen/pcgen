/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.SystemUtils;

public final class ConfigurationSettings extends PropertyContext
{
	private static final String USER_LANGUAGE = "language";
	private static final String USER_COUNTRY = "country";
	public static final String SETTINGS_FILES_PATH = "settingsPath";
	public static final String SYSTEMS_DIR = "systemsPath";
	public static final String OUTPUT_SHEETS_DIR = "osPath";
	private static final String PLUGINS_DIR = "pluginsPath";
	public static final String PREVIEW_DIR = "previewPath";
	public static final String VENDOR_DATA_DIR = "vendordataPath";
	public static final String HOMEBREW_DATA_DIR = "homebrewdataPath";
	public static final String DOCS_DIR = "docsPath";
	public static final String PCC_FILES_DIR = "pccFilesPath";
	public static final String CUSTOM_DATA_DIR = "customPath";
	private static ConfigurationSettings instance = null;
	/** APPLICATION directory name, used in <em>~/.&lt;APPLICATION&gt;</em>, etc. */
	private static final String APPLICATION = "pcgen"; // $NON-NLS-1$

	/**
	 * Directory jpackage places beside {@code runtime} to hold the bundled
	 * application resources.
	 */
	private static final String BUNDLE_APP_DIR = "app"; // $NON-NLS-1$

	/**
	 * How many directory levels the install-root search climbs, counting
	 * java.home itself. Four covers the deepest supported layout (macOS
	 * {@code Contents/runtime/Contents/Home}); the rest is headroom for
	 * dev checkouts.
	 */
	private static final int MAX_ANCESTORS = 6;

	/** Memoised install root; java.home is fixed for the life of the JVM. */
	private static volatile Path installRoot;

	private ConfigurationSettings(String configFileName)
	{
		super(configFileName);
		//Initialize defaults
		setProperty(USER_LANGUAGE, SystemUtils.USER_LANGUAGE);
		setProperty(USER_COUNTRY, SystemUtils.USER_COUNTRY);
		setProperty(SYSTEMS_DIR, "@system");
		setProperty(OUTPUT_SHEETS_DIR, "@outputsheets");
		setProperty(PLUGINS_DIR, "@plugins");
		setProperty(PREVIEW_DIR, "@preview");
		setProperty(DOCS_DIR, "@docs");
		setProperty(PCC_FILES_DIR, "@data");
	}

	@Override
	protected void beforePropertiesSaved()
	{
		relativize(SYSTEMS_DIR);
		relativize(OUTPUT_SHEETS_DIR);
		relativize(PLUGINS_DIR);
		relativize(PREVIEW_DIR);
		relativize(DOCS_DIR);
		relativize(PCC_FILES_DIR);
	}

	public static String getLanguage()
	{
		return getSystemProperty(USER_LANGUAGE);
	}

	public static void setLanguage(String language)
	{
		setSystemProperty(USER_LANGUAGE, language);
	}

	static String getCountry()
	{
		return getSystemProperty(USER_COUNTRY);
	}

	public static void setCountry(String country)
	{
		setSystemProperty(USER_COUNTRY, country);
	}

	/**
	 * @return the current user directory
	 */
	public static String getUserDir()
	{
		return SystemUtils.USER_DIR;
	}

	public static ConfigurationSettings getInstance()
	{
		return getInstance(null);
	}

	public static ConfigurationSettings getInstance(String configFileName)
	{
		if (instance == null)
		{
			instance = new ConfigurationSettings(configFileName == null ? "config.ini" : configFileName);
		}
		return instance;
	}

	public static String getSystemsDir()
	{
		return getDirectory(SYSTEMS_DIR);
	}

	public static String getOutputSheetsDir()
	{
		return getDirectory(OUTPUT_SHEETS_DIR);
	}

	static String getPluginsDir()
	{
		return getDirectory(PLUGINS_DIR);
	}

	public static String getPreviewDir()
	{
		return getDirectory(PREVIEW_DIR);
	}

	public static String getDocsDir()
	{
		return getDirectory(DOCS_DIR);
	}

	public static String getPccFilesDir()
	{
		return getDirectory(PCC_FILES_DIR);
	}

	public static String getSettingsDir()
	{
		return getDirectory(SETTINGS_FILES_PATH);
	}

	public static String getSystemProperty(String key)
	{
		return getInstance().getProperty(key);
	}

	public static void setSystemProperty(String key, String value)
	{
		getInstance().setProperty(key, value);
	}

	private static String getDirectory(String key)
	{
		if (SETTINGS_FILES_PATH.equals(key))
		{
			return getSettingsDirFromFilePath(getSystemProperty(key));
		}
		return expandRelativePath(getSystemProperty(key));
	}

	/**
	 * The directory PCGen's bundled data (@data, @plugins, …) is resolved against,
	 * found via {@link #findInstallRoot} from java.home rather than the process
	 * working directory (a packaged/Finder launch gives user.dir=/).
	 */
	private static String getInstallRoot()
	{
		return installRootPath().toString();
	}

	/** The resolved install-root directory (java.home walk, else user.dir). */
	private static Path installRootPath()
	{
		// java.home cannot change while the JVM runs, so resolve once: the walk
		// hits the filesystem and every @-prefixed path expansion asks for it.
		Path resolved = installRoot;
		if (resolved == null)
		{
			resolved = findInstallRoot(SystemUtils.JAVA_HOME)
					.orElseGet(() -> Path.of(SystemUtils.USER_DIR));
			installRoot = resolved;
		}
		return resolved;
	}

	/**
	 * Whether the install root is writable — true for a portable/dev copy, false
	 * for an installed app (read-only DMG or a sealed bundle in /Applications).
	 * Disables the "PCGen Dir" settings option, which would otherwise fail silently
	 * at save time (mkdirs under a read-only root).
	 */
	public static boolean isInstallRootWritable()
	{
		return isWritableDir(installRootPath());
	}

	/** Package-private seam so the writability check can be unit-tested. */
	static boolean isWritableDir(Path dir)
	{
		return (dir != null) && Files.isDirectory(dir) && Files.isWritable(dir);
	}

	/**
	 * The nearest existing directory at or above {@code path}, or empty if none
	 * exists up the chain. Lets a directory chooser start at a real directory
	 * when the prefilled path does not exist yet.
	 */
	public static Optional<Path> nearestExistingDir(String path)
	{
		Path dir = (path == null) ? null : Path.of(path);
		while ((dir != null) && !Files.isDirectory(dir))
		{
			dir = dir.getParent();
		}
		return Optional.ofNullable(dir);
	}

	/**
	 * Walk up from {@code javaHome} and return the first ancestor — or that
	 * ancestor's {@code app} subdirectory — that holds PCGen's bundled data (the
	 * "data" + "system" folders), or empty if none is found (the caller then
	 * falls back to user.dir). Package-private and parameterised on
	 * {@code javaHome} so it can be exercised without touching real system
	 * properties.
	 * <p>
	 * Only the {@code app} sibling is considered, because that is the directory
	 * jpackage creates next to {@code runtime}. Scanning every child of every
	 * ancestor instead would climb to the filesystem root and adopt any unrelated
	 * directory that happened to contain {@code data} and {@code system} — a
	 * stray checkout under {@code /tmp} was enough to hijack the result. The
	 * climb is bounded for the same reason, so the search can never reach
	 * {@code /usr} or {@code /} from a deeply nested java.home.
	 *
	 * @param javaHome the runtime's home directory, or null
	 * @return the install root, or empty when it cannot be located
	 */
	static Optional<Path> findInstallRoot(String javaHome)
	{
		if (javaHome == null)
		{
			return Optional.empty();
		}
		return Stream.iterate(canonicalize(Path.of(javaHome)), Objects::nonNull, Path::getParent)
				.limit(MAX_ANCESTORS)
				.flatMap(dir -> Stream.of(dir, dir.resolve(BUNDLE_APP_DIR)))
				.filter(ConfigurationSettings::looksLikeInstallRoot)
				.findFirst();
	}

	/**
	 * Resolves symlinks so the ancestor walk climbs the real directory tree.
	 * {@link Path#getParent()} is purely lexical, so a symlinked java.home would
	 * otherwise yield the link's textual ancestors rather than the actual ones.
	 *
	 * @param path the path to canonicalize
	 * @return the real path, or the normalized absolute path if it cannot be resolved
	 */
	private static Path canonicalize(Path path)
	{
		try
		{
			return path.toRealPath();
		}
		catch (IOException _)
		{
			return path.toAbsolutePath().normalize();
		}
	}

	private static boolean looksLikeInstallRoot(Path dir)
	{
		return Files.isDirectory(dir.resolve("data")) && Files.isDirectory(dir.resolve("system"));
	}

	private static String expandRelativePath(String path)
	{
		if (path.startsWith("@"))
		{
			path = getInstallRoot() + File.separator + path.substring(1);
		}
		return path;
	}

	private static String unexpandRelativePath(String path)
	{
		String root = getInstallRoot();
		if (path.startsWith(root + File.separator))
		{
			path = '@' + path.substring(root.length() + 1);
		}
		return path;
	}

	private static void relativize(String property)
	{
		setSystemProperty(property, unexpandRelativePath(getSystemProperty(property)));
	}

	public enum SettingsFilesPath
	{

		/** User Directory */
		user,
		/** Indicates PCGen directory */
		pcgen,
		/** Freedesktop configuration directories */
		FD_USER,
		/** Indicate MAC specific directories */
		mac_user;

		public String getSettingsDir()
		{
			switch (this)
			{
				case user:
					return SystemUtils.USER_HOME + File.separator + '.' + APPLICATION; // $NON-NLS-1$
				case pcgen:
					// Install dir, resolved from java.home like @-paths — not user.dir,
					// which is / for a packaged/Finder launch (gave "//settings").
					return getInstallRoot() + File.separator + "settings"; // $NON-NLS-1$
				case mac_user:
					return SystemUtils.USER_HOME + "/Library/Preferences/" + APPLICATION; // $NON-NLS-1$
				case FD_USER:
					String config = System.getenv("XDG_CONFIG_HOME"); // $NON-NLS-1$
					if ((config == null) || config.isEmpty())
					{
						config = SystemUtils.USER_HOME + File.separator + ".config"; // $NON-NLS-1$
					}
					return config + File.separator + APPLICATION;
				default:
					throw new InternalError();
			}
		}
	}

	public static String getSettingsDirFromFilePath(String fType)
	{
		if ((fType == null) || fType.isEmpty())
		{
			// make sure we have a default
			fType = getDefaultSettingsFilesPath();
		}
		String path;
		try
		{
			//Check to see if this path is one of the standard path types
			path = SettingsFilesPath.valueOf(fType).getSettingsDir();
		}
		catch (IllegalArgumentException _)
		{
			//It must be a custom filepath
			path = fType;
		}
		return path;
	}

	/**
	 * @return A default Settings Files Path value.
	 */
	public static String getDefaultSettingsFilesPath()
	{
		String fType;
		if (SystemUtils.IS_OS_MAC_OSX)
		{
			fType = SettingsFilesPath.mac_user.name();
		}
		else if (SystemUtils.IS_OS_UNIX)
		{
			fType = SettingsFilesPath.FD_USER.name();
		}
		else
		{
			fType = SettingsFilesPath.user.name();
		}
		return fType;
	}

	/**
	 * @return "User Dir" dir Settings Files Path value.
	 */
	public static String getUserSettingsDirFromFilePath()
	{
		return getSettingsDirFromFilePath(getDefaultSettingsFilesPath());
	}

}
