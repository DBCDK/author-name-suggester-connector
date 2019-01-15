/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Suggestions {

    @JsonProperty("aut-names")
    private List<String> authorNames;

    @JsonProperty("ner-names")
    private List<String> nerNames;

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public List<String> getNerNames() {
        return nerNames;
    }

    public void setNerNames(List<String> nerNames) {
        this.nerNames = nerNames;
    }

    @Override
    public String toString() {
        return "Suggestions{" +
                "authorNames=" + authorNames +
                ", nerNames=" + nerNames +
                '}';
    }
}
