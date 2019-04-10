/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ExactMatchNamesTest {
    @Test
    void validXml() throws JSONBException, JsonProcessingException {
        final JSONBContext jsonbContext = new JSONBContext();
        final ExactMatchNames names = jsonbContext.unmarshall(
                "{\"fields\":{\"245a\":\"title\",\"z99a\":\"dra\"},\"input-name\":\"John Doe\",\"authority\":\"123456\",\"term-fo\":\"foobar\"}",
                ExactMatchNames.class);

        final XmlMapper xmlMapper = new XmlMapper();
        assertThat(xmlMapper.writeValueAsString(names),
                is("<ExactMatchNames>" +
                            "<fields>" +
                                "<field245a>title</field245a>" +
                                "<fieldz99a>dra</fieldz99a>" +
                            "</fields>" +
                            "<input-name>John Doe</input-name>" +
                            "<authority>123456</authority>" +
                            "<term-fo>foobar</term-fo>" +
                         "</ExactMatchNames>"));
    }
}