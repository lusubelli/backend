package fr.usubelli.accounting.backend.dto;

public class Article {

    private Integer object;
    private String description;
    private Double unitPriceWithoutTax;
    private Double quantity;
    private Double tax;

    public Article(Integer object, String description, Double unitPriceWithoutTax, Double quantity, Double tax) {
        this.object = object;
        this.description = description;
        this.unitPriceWithoutTax = unitPriceWithoutTax;
        this.quantity = quantity;
        this.tax = tax;
    }

    public Integer getObject() {
        return object;
    }

    public String getDescription() {
        return description;
    }

    public Double getUnitPriceWithoutTax() {
        return unitPriceWithoutTax;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getTax() {
        return tax;
    }
}
