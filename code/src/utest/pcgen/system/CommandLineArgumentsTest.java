package pcgen.system;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.TypedArgumentConverter;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import util.SystemExitInterceptor;

import java.io.File;
import java.util.Optional;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

class CommandLineArgumentsTest {
    //private static Runnable revertSystemExitInterceptor;

    /**
     * We temporarily replace the security manager in order to
     * intercept calls to System.exit from the argparse4j library.
     * Otherwise, these calls would terminate the Unit Test runner.
     * (SecurityManager is deprecated for removal.  As of Java 21,
     * this code will produce an UnsupportedOperation exception.
     * See https://snyk.io/blog/securitymanager-removed-java/
     * argparse4j will throw ArgumentParserException if an illegal
     * argument is passed _as long as_ we don't call parseArgsOrFail()
     * and just use parseArgs().  Main::main() now uses that
     * method and catches the exception, so we can catch the
     * exception here as well and don't need the interceptor
     * any more.
     */
    //@BeforeAll
    //static void initialize() {
    //    revertSystemExitInterceptor = SystemExitInterceptor.startInterceptor();
    //}

    //@AfterAll
    //static void cleanup() {
    //    revertSystemExitInterceptor.run();
    //}

    private CommandLineArguments from(String[] args) throws ArgumentParserException {
        return new CommandLineArguments(args);
    }

    public static class CSVtoArrayConverter extends TypedArgumentConverter<String, String[]> {
        protected CSVtoArrayConverter() {
            super(String.class, String[].class);
        }

        @Override
        protected String[] convert(String s) throws ArgumentConversionException {
            if (s == null) {
                return null;
            }

            return s.split("\\s*,\\s*");
        }
    }

    @ParameterizedTest
    @EmptySource
    void noArgs(String... args) throws ArgumentParserException {
        CommandLineArguments classUnderTest = from(args);
        Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
        Assertions.assertFalse(classUnderTest.isStartNameGenerator());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
        Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
        Assertions.assertFalse(classUnderTest.isVerbose());
    }

    @Nested
    class CharacterTest {
        @ParameterizedTest
        @ValueSource(strings = {"-c,characters/Sorcerer.pcg", "--character,characters/Sorcerer.pcg"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-c,file/that/doesn't/exist", "-c,", "-c", "-c,characters/Sorcerer.pcg,characters/Everything.pcg",
                "--character,file/that/doesn't/exist", "--character,", "--character", "--character,characters/Sorcerer.pcg,characters/Everything.pcg"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }

    @Nested
    class VerboseTest {
        @ParameterizedTest
        @ValueSource(strings = {"-v", "-vv"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertTrue(classUnderTest.isVerbose());
        }
    }

    @Nested
    class SettingsDirTest {
        @ParameterizedTest
        @ValueSource(strings = {"-s,characters", "--settingsdir,characters"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.of(new File("characters")), classUnderTest.getSettingsDir());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-s,folder/that/doesn't/exist", "-s,", "-s", "-s,characters,data", "-s,characters/Sorcerer.pcg",
                "--settingsdir,folder/that/doesn't/exist", "--settingsdir,", "--settingsdir", "--settingsdir,characters,data"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }


    @Nested
    class ConfigFileNameTest {
        @ParameterizedTest
        @ValueSource(strings = {"-S,testname.ini", "--configfilename,testname.ini"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.of("testname.ini"), classUnderTest.getConfigFileName());

            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-S,", "-S", "-S,too,many",
                "--configfilename,", "--configfilename", "--configfilename,too,many"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }

    @Nested
    class CampaignModeTest {
        @ParameterizedTest
        @ValueSource(strings = {"-m,testCampaign", "--campaignmode,testCampaign"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.of("testCampaign"), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-m,", "-m", "-m,too,many",
                "--campaignmode,", "--campaignmode", "--campaignmode,too,many"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }

    @Nested
    class TabTest {
        @ParameterizedTest
        @ValueSource(strings = {"-D,test", "--tab,test"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.of("test"), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-D,", "-D", "-D,too,many",
                "--tab,", "--tab", "--tab,too,many"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }


    @Nested
    class ExportSheetTest {
        @ParameterizedTest
        @ValueSource(strings = {"-E,characters/Sorcerer.pcg", "--exportsheet,characters/Sorcerer.pcg"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-E,file/that/doesn't/exist", "-E,", "-E", "-E,characters/Sorcerer.pcg,characters/Everything.pcg",
                "--exportsheet,file/that/doesn't/exist", "--exportsheet,", "--exportsheet", "--exportsheet,characters/Sorcerer.pcg,characters/Everything.pcg"})
        void testillegalUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }

    @Nested
    class PartyTest {
        @ParameterizedTest
        @ValueSource(strings = {"-p,characters/Sorcerer.pcg", "--party,characters/Sorcerer.pcg"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-c,file/that/doesn't/exist", "-c,", "-c", "-c,characters/Sorcerer.pcg,characters/Everything.pcg",
                "--character,file/that/doesn't/exist", "--character,", "--character", "--character,characters/Sorcerer.pcg,characters/Everything.pcg"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }

    @Nested
    class OutputFileTest {
        @ParameterizedTest
        @ValueSource(strings = {"-o,characters/nonexisting-file", "--outputfile,characters/nonexisting-file"})
        void validNonexistingUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.of(new File("characters/nonexisting-file")), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-o,characters/Sorcerer.pcg", "--outputfile,characters/Sorcerer.pcg"})
        void validExistingUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.of(new File("characters/Sorcerer.pcg")), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertFalse(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-o,", "-o", "-o,more/than,one/file",
                "--outputfile,", "--outputfile", "--outputfile,more/than,one/file"})
        void invalidUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) {
            Assertions.assertThrows(ArgumentParserException.class, () -> from(args));
        }
    }

    @Nested
    class NameGeneratorTest {
        @ParameterizedTest
        @ValueSource(strings = {"--name-generator"})
        void validUsage(@ConvertWith(CSVtoArrayConverter.class) String... args) throws ArgumentParserException {
            CommandLineArguments classUnderTest = from(args);

            Assertions.assertEquals(Optional.empty(), classUnderTest.getCampaignMode());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getCharacterFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getExportSheet());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getOutputFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getPartyFile());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getSettingsDir());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getConfigFileName());
            Assertions.assertTrue(classUnderTest.isStartNameGenerator());
            Assertions.assertEquals(Optional.empty(), classUnderTest.getTab());
            Assertions.assertFalse(classUnderTest.isVerbose());
        }
    }
}
