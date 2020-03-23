package fr.usubelli.accounting.backend.usecase;

import fr.usubelli.accounting.backend.dto.Customer;
import fr.usubelli.accounting.backend.dto.Organization;
import fr.usubelli.accounting.backend.exception.OrganisationAlreadyExistsException;
import fr.usubelli.accounting.backend.port.OrganizationGateway;

import java.util.List;

public class CreateCustomer {

    private final OrganizationGateway organizationGateway;

    public CreateCustomer(OrganizationGateway organizationGateway) {
        this.organizationGateway = organizationGateway;
    }

    public Customer createCustomer(String siren, Organization customerOrganization, String billingContactEmail) {

        try {
            organizationGateway.createOrganisation(customerOrganization);
        } catch (OrganisationAlreadyExistsException e) {}

        final Organization ownOrganization = organizationGateway.findOrganisation(siren);

        final List<Customer> customers = ownOrganization.getCustomers();
        final Customer customer = new Customer(customerOrganization.getSiren(), billingContactEmail);
        customers.add(customer);

        organizationGateway.updateOrganisation(new Organization(
                ownOrganization.getName(),
                ownOrganization.getAddress(),
                ownOrganization.getZipcode(),
                ownOrganization.getTown(),
                ownOrganization.getSiren(),
                ownOrganization.getTva(),
                customers));

        return customer;
    }

}
