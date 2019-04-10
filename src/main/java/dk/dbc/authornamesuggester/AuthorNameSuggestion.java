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

    public static class Builder {
        private String inputName;
        private String authority;

        public Builder withInputName(String inputName) {
            this.inputName = inputName;
            return this;
        }

        public Builder withAuthority(String authority) {
            this.authority = authority;
            return this;
        }

        public AuthorNameSuggestion build() {
            AuthorNameSuggestion authorNameSuggestion = new AuthorNameSuggestion();
            authorNameSuggestion.setInputName(inputName);
            authorNameSuggestion.setAuthority(authority);

            return authorNameSuggestion;
        }
    }

    @Override
    public String toString() {
        return "AuthorNameSuggestion{" +
                "inputName='" + inputName + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
