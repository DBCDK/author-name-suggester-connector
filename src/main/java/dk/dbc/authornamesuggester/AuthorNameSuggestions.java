/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class AuthorNameSuggestions {

    @JsonProperty("aut-names")
    @JacksonXmlProperty(localName = "aut-name")
    @JacksonXmlElementWrapper(localName = "aut-names")
    private List<String> authorityIds;

    @JsonProperty("ner-names")
    @JacksonXmlProperty(localName = "ner-name")
    @JacksonXmlElementWrapper(localName = "ner-names")
    private List<String> nerNames;

    public List<String> getAuthorityIds() {
        return authorityIds;
    }

    public void setAuthorityIds(List<String> authorityIds) {
        this.authorityIds = authorityIds;
    }

    public List<String> getNerNames() {
        return nerNames;
    }

    public void setNerNames(List<String> nerNames) {
        this.nerNames = nerNames;
    }

    @Override
    public String toString() {
        return "AuthorNameSuggestions{" +
                "authorityIds=" + authorityIds +
                ", nerNames=" + nerNames +
                '}';
    }
}
