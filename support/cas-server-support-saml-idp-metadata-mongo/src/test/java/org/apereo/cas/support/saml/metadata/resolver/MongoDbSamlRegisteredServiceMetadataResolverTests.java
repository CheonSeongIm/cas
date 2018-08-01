package org.apereo.cas.support.saml.metadata.resolver;

import org.apereo.cas.support.saml.BaseMongoDbSamlMetadataTests;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.SamlMetadataDocument;

import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * This is {@link MongoDbSamlRegisteredServiceMetadataResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@TestPropertySource(properties = {
    "cas.authn.samlIdp.metadata.mongo.dropCollection=true",
    "cas.authn.samlIdp.metadata.mongo.collection=samlResolver",
    "cas.authn.samlIdp.metadata.mongo.host=localhost",
    "cas.authn.samlIdp.metadata.mongo.port=8081",
    "cas.authn.samlIdp.metadata.location=file:/tmp"
    })
public class MongoDbSamlRegisteredServiceMetadataResolverTests extends BaseMongoDbSamlMetadataTests {
    @Before
    public void initialize() throws Exception {
        val mongo = casProperties.getAuthn().getSamlIdp().getMetadata().getMongo();
        val res = new ClassPathResource("sp-metadata.xml");
        val md = new SamlMetadataDocument();
        md.setName("SP");
        md.setValue(IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8));
        mongoDbSamlMetadataResolverTemplate.save(md, mongo.getCollection());
    }

    @Test
    public void verifyResolver() {
        val service = new SamlRegisteredService();
        service.setName("SAML Service");
        service.setServiceId("https://carmenwiki.osu.edu/shibboleth");
        service.setDescription("Testing");
        service.setMetadataLocation("mongodb://");
        assertTrue(resolver.supports(service));
        val resolvers = resolver.resolve(service);
        assertTrue(resolvers.size() == 1);
    }
}
