package com.github.davidmoten.odata.client.generator;

import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.oasisopen.odata.csdl.v4.TDataServices;
import org.oasisopen.odata.csdl.v4.TEdmx;

public class GeneratorTest {

    @Test
    public void testGenerateMsgraph() throws JAXBException {
        JAXBContext c = JAXBContext.newInstance(TDataServices.class);
        Unmarshaller unmarshaller = c.createUnmarshaller();
        TEdmx t = unmarshaller.unmarshal(
                new StreamSource(GeneratorTest.class.getResourceAsStream("/msgraph-1.0-20180905-formatted.xml")),
                TEdmx.class).getValue();
        SchemaOptions schemaOptions = new SchemaOptions("microsoft.graph", "microsoft.graph.generated");
        Options options = new Options("target/generated-sources/odata", Collections.singletonList(schemaOptions));
        Generator g = new Generator(options, Collections.singletonList(t.getDataServices().getSchema().get(0)));
        g.generate();
    }

    @Test
    public void testGenerateODataTestService() throws JAXBException {
        JAXBContext c = JAXBContext.newInstance(TDataServices.class);
        Unmarshaller unmarshaller = c.createUnmarshaller();
        TEdmx t = unmarshaller.unmarshal(
                new StreamSource(GeneratorTest.class.getResourceAsStream("/odata-test-service-metadata.xml")),
                TEdmx.class).getValue();
        SchemaOptions schemaOptions = new SchemaOptions("ODataDemo", "odata.test.generated");
        Options options = new Options("target/generated-sources/odata", Collections.singletonList(schemaOptions));
        Generator g = new Generator(options, Collections.singletonList(t.getDataServices().getSchema().get(0)));
        g.generate();
    }

}
