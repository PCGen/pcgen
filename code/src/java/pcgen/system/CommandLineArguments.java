package pcgen.system;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.Validate;
import pcgen.cdom.base.Constants;
import pcgen.util.GracefulExit;

public class CommandLineArguments
{
    private Namespace namespace = new Namespace(new HashMap<>());

    public CommandLineArguments(String[] args)
    {
        Validate.notNull(args, "Parameter 'args' must not be null");
        var parser = this.getParser();
        try
        {
            this.namespace = parser.parseArgs(args);
        } catch (HelpScreenException e)
        {
            parser.handleError(e);
            GracefulExit.exit(0);
        } catch (ArgumentParserException e)
        {
            parser.handleError(e);
            GracefulExit.exit(1);
        }
    }

    /**
     * @return an ArgumentParser used to perform argument parsing
     */
    private ArgumentParser getParser()
    {
        ArgumentParser parser = ArgumentParsers.newFor(Constants.APPLICATION_NAME).build().defaultHelp(false)
                .description("RPG Character Generator").version(PCGenPropBundle.getVersionNumber());

        parser.addArgument("-v", "--verbose").help("verbose logging").type(Boolean.class).action(Arguments.count());

        parser.addArgument("-V", "--version").action(Arguments.version());

        MutuallyExclusiveGroup startupMode =
                parser.addMutuallyExclusiveGroup().description("start up on a specific mode");

        startupMode.addArgument("--name-generator").help("run the name generator").type(Boolean.class)
                .action(Arguments.storeTrue());

        startupMode.addArgument("-D", "--tab").nargs(1);

        parser.addArgument("-s", "--settingsdir").nargs(1)
                .type(Arguments.fileType().verifyIsDirectory().verifyCanRead().verifyExists());
        parser.addArgument("-S", "--configfilename").nargs(1).type(String.class);

        parser.addArgument("-m", "--campaignmode").nargs(1).type(String.class);
        parser.addArgument("-E", "--exportsheet").nargs(1)
                .type(Arguments.fileType().verifyCanRead().verifyExists().verifyIsFile());

        parser.addArgument("-o", "--outputfile").nargs(1)
                .type(Arguments.fileType()
                        .verifyNotExists().verifyCanCreate()
                        .or()
                        .verifyIsFile().verifyCanWrite());

        parser.addArgument("-c", "--character").nargs(1)
                .type(Arguments.fileType().verifyCanRead().verifyExists().verifyIsFile());

        parser.addArgument("-p", "--party").nargs(1)
                .type(Arguments.fileType().verifyCanRead().verifyExists().verifyIsFile());

        return parser;
    }

    private <T> Optional<T> getSingle(List<T> list)
    {
        return Optional.ofNullable(list)
                .filter(Predicate.not(List::isEmpty))
                .map(List::getFirst);
    }

    public Optional<File> getSettingsDir()
    {
        return getSingle(namespace.get("settingsdir"));
    }

    public Optional<String> getConfigFileName()
    {
        return getSingle(namespace.get("configfilename"));
    }

    public Optional<String> getCampaignMode()
    {
        return getSingle(namespace.get("campaignmode"));
    }

    public Optional<String> getTab()
    {
        return getSingle(namespace.get("tab"));
    }

    public Optional<File> getExportSheet()
    {
        return getSingle(namespace.get("exportsheet"));
    }

    public Optional<File> getPartyFile()
    {
        return getSingle(namespace.get("party"));
    }

    public Optional<File> getCharacterFile()
    {
        return getSingle(namespace.get("character"));
    }

    public Optional<File> getOutputFile()
    {
        return getSingle(namespace.get("outputfile"));
    }

    public boolean isStartNameGenerator()
    {
        return Optional.ofNullable(namespace.getBoolean("name_generator"))
                .orElse(false);
    }

    public boolean isVerbose()
    {
        // Why allow the flag multiple times and count them if we just evaluate them to boolean afterward?
        // Seems unintentional.
        return Optional.ofNullable(namespace.getInt("verbose"))
                .orElse(0) > 0;
    }
}
