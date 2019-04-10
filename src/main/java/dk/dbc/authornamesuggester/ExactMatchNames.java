/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.authornamesuggester;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ExactMatchNames extends AuthorNameSuggestion {
    private Map<String, String> fields;

    @JsonProperty("term-fo")
    private String termFo;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        if (fields != null) {
            this.fields = new HashMap<>(fields);
        }
    }

    public String getTermFo() {
        return termFo;
    }

    public void setTermFo(String termFo) {
        this.termFo = termFo;
    }
}
