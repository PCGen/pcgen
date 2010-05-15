/**
 * 
 */
package plugin.primitive.deity;

import java.net.URISyntaxException;
import java.util.Arrays;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.DeityToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

public class AbstractAlignTokenTest extends
		AbstractPrimitiveTokenTestCase<CDOMObject, Deity>
{
	static ChooseLst token = new ChooseLst();
	static DeityToken subtoken = new DeityToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	private static final AlignToken ALIGN_TOKEN = new AlignToken();

	public AbstractAlignTokenTest()
	{
		super("ALIGN", "LG", Arrays
				.asList(new String[] { "LawfulGood", "LG.NG" }));
	}

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCAlignment lg = primaryContext.ref.constructCDOMObject(
				PCAlignment.class, "Lawful Good");
		primaryContext.ref.registerAbbreviation(lg, "LG");
		PCAlignment slg = secondaryContext.ref.constructCDOMObject(
				PCAlignment.class, "Lawful Good");
		secondaryContext.ref.registerAbbreviation(slg, "LG");
		TokenRegistration.register(ALIGN_TOKEN);
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Deity> getTargetClass()
	{
		return Deity.class;
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

}