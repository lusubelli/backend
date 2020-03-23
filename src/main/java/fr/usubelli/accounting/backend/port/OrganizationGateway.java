package fr.usubelli.accounting.backend.port;

import fr.usubelli.accounting.backend.dto.Organization;
import fr.usubelli.accounting.backend.exception.OrganisationAlreadyExistsException;

public interface OrganizationGateway {

    Organization createOrganisation(Organization organization) throws OrganisationAlreadyExistsException;

    Organization findOrganisation(String siren);

    Organization updateOrganisation(Organization organization);

}
