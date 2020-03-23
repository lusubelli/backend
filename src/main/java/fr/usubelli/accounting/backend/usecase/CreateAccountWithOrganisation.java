package fr.usubelli.accounting.backend.usecase;

import fr.usubelli.accounting.backend.dto.CreateAccountWithOrganisationRequest;
import fr.usubelli.accounting.backend.dto.OrganisationRights;
import fr.usubelli.accounting.backend.dto.Organization;
import fr.usubelli.accounting.backend.dto.User;
import fr.usubelli.accounting.backend.exception.OrganisationAlreadyExistsException;
import fr.usubelli.accounting.backend.exception.UserAlreadyExistsException;
import fr.usubelli.accounting.backend.port.OrganizationGateway;
import fr.usubelli.accounting.backend.port.UserGateway;

import java.util.Arrays;

public class CreateAccountWithOrganisation {

    private final UserGateway userGateway;
    private final OrganizationGateway organizationGateway;

    public CreateAccountWithOrganisation(UserGateway userGateway, OrganizationGateway organizationGateway) {
        this.userGateway = userGateway;
        this.organizationGateway = organizationGateway;
    }

    public CreateAccountWithOrganisationRequest createAccountWithOrganisation(CreateAccountWithOrganisationRequest request)
            throws UserAlreadyExistsException, OrganisationAlreadyExistsException {

        OrganisationRights rights = new OrganisationRights(request.getOrganization().getSiren(), Arrays.asList(
                OrganisationRights.Right.UPDATE_ORGANISATION,
                OrganisationRights.Right.REDUCE_RIGHT,
                OrganisationRights.Right.GRANT_RIGHT));

        userGateway.createUser(new User(
                request.getAccount().getEmail(),
                request.getAccount().getPassword(),
                rights,
                User.UserState.CREATED));

        Organization organization = organizationGateway.createOrganisation(request.getOrganization());

        return new CreateAccountWithOrganisationRequest(
                request.getAccount(),
                organization);
    }

}
