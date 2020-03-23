package fr.usubelli.accounting.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrganisationRights {
    public enum Right {
        UPDATE_ORGANISATION,
        GRANT_RIGHT,
        REDUCE_RIGHT,
    }

    private final String siren;
    private final List<Right> rights;

    @JsonCreator
    public OrganisationRights(
            @JsonProperty("siren") String siren,
            @JsonProperty("rights") List<Right> rights) {
        this.siren = siren;
        this.rights = rights;
    }

    public String getSiren() {
        return siren;
    }

    public List<Right> getRights() {
        return rights;
    }

}
