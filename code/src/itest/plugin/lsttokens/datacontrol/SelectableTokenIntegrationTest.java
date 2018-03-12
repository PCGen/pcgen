package plugin.lsttokens.datacontrol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import pcgen.base.format.StringManager;
import pcgen.base.util.BasicIndirect;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.converter.DereferencingConverter;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Campaign;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.util.enumeration.Visibility;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import plugin.lsttokens.testsupport.TokenRegistration;
import util.TestURI;

import static org.junit.Assert.*;

public class SelectableTokenIntegrationTest
{

	private static final String PROP_1 = "Property";
	private static final String PROP_2 = "Psychology";
	private static final StringManager STRING_MGR = new StringManager();
	private static final SelectableToken token = new SelectableToken();
	FactDefinition cd;

	protected LoadContext context;

	protected static CampaignSourceEntry testCampaign;

	@BeforeClass
	public static void classSetUp() throws URISyntaxException
	{
		testCampaign =
				new CampaignSourceEntry(new Campaign(), TestURI.getURI());
	}

	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		TokenRegistration.clearTokens();
		TokenRegistration.register(token);
		resetContext();
	}

	private void resetContext()
	{
		URI testURI = testCampaign.getURI();
		context =
				new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
					new ConsolidatedListCommitStrategy());
		context.setSourceURI(testURI);
		context.setExtractURI(testURI);
		cd = new FactDefinition();
		cd.setDisplayName(PROP_1);
		cd.setFormatManager(STRING_MGR);
		cd.setName(PROP_1);
		cd.setFactName(PROP_1);
		cd.setUsableLocation(Domain.class);
		cd.setVisibility(Visibility.DEFAULT);
	}

	@Test
	public void testValidStringYes() throws PersistenceLayerException
	{
		assertNull(cd.getSelectable());
		assertTrue(token.parseToken(context, cd, "YES").passed());
		assertNotNull(cd.getSelectable());
		assertTrue(cd.getSelectable().booleanValue());
		context.getReferenceContext().importObject(cd);
		SourceFileLoader.processFactDefinitions(context);
		//Now check for group!!
		Domain d1 = createDomain("Domain1", PROP_1, "Aggressive");
		Domain d2 = createDomain("Domain2", PROP_1, "Aggressive");
		Domain d3 = createDomain("Domain3", PROP_1, "Crazy");
		Domain d4 = createDomain("Domain4", PROP_2, "Aggressive");
		ReferenceManufacturer<Domain> mfg = context.getReferenceContext().getManufacturer(Domain.class);
		PrimitiveCollection<Domain> pcf = context.getPrimitiveChoiceFilter(mfg, "PROPERTY=Aggressive");
		context.getReferenceContext().resolveReferences(null);
		Collection<?> coll = pcf.getCollection(null, new DereferencingConverter<>(null));
		assertEquals(2, coll.size());
		assertTrue(coll.contains(d1));
		assertTrue(coll.contains(d2));
		assertFalse(coll.contains(d3));
		assertFalse(coll.contains(d4));
	}

	private Domain createDomain(String key, String prop, String val)
	{
		Domain d = new Domain();
		d.setName(key);
		FactKey<String> fk = FactKey.getConstant(prop, STRING_MGR);
		d.put(fk, new BasicIndirect<>(STRING_MGR, val));
		context.getReferenceContext().importObject(d);
		return d;
	}


}
