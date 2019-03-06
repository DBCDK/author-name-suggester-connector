package dk.dbc.authornamesuggester;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorNameSuggestion {

    @JsonProperty("input-name")
    private String inputName;

    @JsonProperty("authority")
    private String authority;

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
