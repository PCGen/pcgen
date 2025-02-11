package pcgen.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.TypedArgumentConverter;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import pcgen.util.GracefulExit;

import java.io.File;
import java.text.MessageFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("squid:S2698") // Don't show the warning in SonarQube related to a missing assertion text message
class CommandLineArgumentsTest
{
	/**
	 * We temporarily replace the interceptors to prevent the JVM from exiting. The exception is thrown to prevent
	 * the system from executing the code after the command line arguments were parsed with errors.
	 */
	@BeforeEach
	void initialize()
	{
		GracefulExit.registerExitFunction((int status) -> {
			assertEquals(1, status,
					MessageFormat.format("The command line parsing must always fail. But the current status is {0}.",
							status));
			throw new IllegalStateException("The test execution is aborted intentionally.");
		});
	}

	/**
	 * We revert the changes to the interceptors to allow the JVM to exit normally.
	 */
	@BeforeEach
	void cleanup()
	{
		GracefulExit.registerExitFunction(System::exit);
	}

	/**
	 * Helper method to create a CommandLineArguments object from the given arguments.
	 * @param args The arguments to parse as an array of strings (can be taken from {@link Main#main(String...)})
	 * @return The {@link CommandLineArguments} object
	 */
	private CommandLineArguments from(String[] args)
	{
		return new CommandLineArguments(args);
	}

	public static class CSVtoArrayConverter extends TypedArgumentConverter<String, String[]>
	{
		protected CSVtoArrayConverter()
		{
			super(String.class, String[].class);
		}

		@Override
		protected String[] convert(String s) throws ArgumentConversionException
		{
			return s.split("\\s*,\\s*");
		}
	}

	@ParameterizedTest
	@EmptySource
	void noArgs(String... args)
	{
		CommandLineArguments classUnderTest = from(args);
		assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
		assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
		assertEquals(Optional.empty(), classUnderTest.getExportSheet());
		assertEquals(Optional.empty(), classUnderTest.getOutputFile());
		assertEquals(Optional.empty(), classUnderTest.getPartyFile());
		assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
		assertFalse(classUnderTest.isStartNameGenerator());
		assertEquals(Optional.empty(), classUnderTest.getTab());
		assertEquals(Optional.empty(), classUnderTest.getTab());
		assertFalse(classUnderTest.isVerbose());
	}

	@ParameterizedTest
	@ValueSource(strings = {"-h", "--help"})
	void testHelpArgs(@ConvertWith(CSVtoArrayConverter.class) String... args) {
		GracefulExit.registerExitFunction((int status) -> {
			assertEquals(0, status,
					MessageFormat.format("The command line parsing must be successful because help command arguments are parsed. But the current status is {0}.",
							status));
			throw new IllegalStateException("The test execution is aborted intentionally.");
		});

		assertThrows(IllegalStateException.class, () -> from(args),
				"The help arguments were parsed successfully. The test must be interrupted intentionally.");
	}

	@Nested
	class CharacterTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-c,characters/Sorcerer.pcg", "--character,characters/Sorcerer.pcg"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-c,file/that/doesn't/exist", "-c,", "-c",
				"-c,characters/Sorcerer.pcg,characters/Everything.pcg",
				"--character,file/that/doesn't/exist", "--character,", "--character",
				"--character,characters/Sorcerer.pcg,characters/Everything.pcg"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The character file does not exist. The parsing must fail.");
		}
	}

	@Nested
	class VerboseTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-v", "-vv"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertTrue(classUnderTest.isVerbose());
		}
	}

	@Nested
	class SettingsDirTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-s,characters", "--settingsdir,characters"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.of(new File("characters")), classUnderTest.getSettingsDir());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-s,folder/that/doesn't/exist", "-s,", "-s", "-s,characters,data",
				"-s,characters/Sorcerer.pcg",
				"--settingsdir,folder/that/doesn't/exist", "--settingsdir,", "--settingsdir",
				"--settingsdir,characters,data"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The settings directory not exist. The parsing must fail.");
		}
	}

	@Nested
	class ConfigFileNameTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-S,testname.ini", "--configfilename,testname.ini"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.of("testname.ini"), classUnderTest.getConfigFileName());

			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-S,", "-S", "-S,too,many",
				"--configfilename,", "--configfilename", "--configfilename,too,many"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The config file name is wrong. The parsing must fail.");
		}
	}

	@Nested
	class CampaignModeTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-m,testCampaign", "--campaignmode,testCampaign"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.of("testCampaign"), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-m,", "-m", "-m,too,many",
				"--campaignmode,", "--campaignmode", "--campaignmode,too,many"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The campaign mode is wrong. The parsing must fail.");
		}
	}

	@Nested
	class TabTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-D,test", "--tab,test"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.of("test"), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-D,", "-D", "-D,too,many",
				"--tab,", "--tab", "--tab,too,many"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The tab is wrong. The parsing must fail.");
		}
	}

	@Nested
	class ExportSheetTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-E,characters/Sorcerer.pcg", "--exportsheet,characters/Sorcerer.pcg"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-E,file/that/doesn't/exist", "-E,", "-E",
				"-E,characters/Sorcerer.pcg,characters/Everything.pcg",
				"--exportsheet,file/that/doesn't/exist", "--exportsheet,", "--exportsheet",
				"--exportsheet,characters/Sorcerer.pcg,characters/Everything.pcg"})
		void testillegalUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The export sheet does not exist. The parsing must fail.");
		}
	}

	@Nested
	class PartyTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-p,characters/Sorcerer.pcg", "--party,characters/Sorcerer.pcg"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-c,file/that/doesn't/exist", "-c,", "-c",
				"-c,characters/Sorcerer.pcg,characters/Everything.pcg",
				"--character,file/that/doesn't/exist", "--character,", "--character",
				"--character,characters/Sorcerer.pcg,characters/Everything.pcg"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The character file does not exist. The parsing must fail.");
		}
	}

	@Nested
	class OutputFileTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"-o,characters/nonexisting-file", "--outputfile,characters/nonexisting-file"})
		void testValidNonExistingUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.of(new File("characters/nonexisting-file")), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-o,characters/Sorcerer.pcg", "--outputfile,characters/Sorcerer.pcg"})
		void testValidExistingUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertFalse(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}

		@ParameterizedTest
		@ValueSource(strings = {"-o,", "-o", "-o,more/than,one/file",
				"--outputfile,", "--outputfile", "--outputfile,more/than,one/file"})
		void testInvalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			assertThrows(IllegalStateException.class, () -> from(args),
					"The output file is wrong. The parsing must fail.");
		}
	}

	@Nested
	class NameGeneratorTest
	{
		@ParameterizedTest
		@ValueSource(strings = {"--name-generator"})
		void testValidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args)
		{
			CommandLineArguments classUnderTest = from(args);

			assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
			assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
			assertEquals(Optional.empty(), classUnderTest.getExportSheet());
			assertEquals(Optional.empty(), classUnderTest.getOutputFile());
			assertEquals(Optional.empty(), classUnderTest.getPartyFile());
			assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
			assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
			assertTrue(classUnderTest.isStartNameGenerator());
			assertEquals(Optional.empty(), classUnderTest.getTab());
			assertFalse(classUnderTest.isVerbose());
		}
	}
}
