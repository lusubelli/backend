package fr.usubelli.accounting.backend.dto;

import java.time.ZonedDateTime;
import java.util.List;

public class Bill {

    private String customerSiren;
    private String number;
    private ZonedDateTime date;
    private ZonedDateTime term;
    private String method;
    private List<Article> articles;
    private String conditions;
    private String note;

    public Bill(String customerSiren, String number, ZonedDateTime date, ZonedDateTime term, String method, List<Article> articles, String conditions, String note) {
        this.customerSiren = customerSiren;
        this.number = number;
        this.date = date;
        this.term = term;
        this.method = method;
        this.articles = articles;
        this.conditions = conditions;
        this.note = note;
    }

    public String getCustomerSiren() {
        return customerSiren;
    }

    public String getNumber() {
        return number;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public ZonedDateTime getTerm() {
        return term;
    }

    public String getMethod() {
        return method;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public String getConditions() {
        return conditions;
    }

    public String getNote() {
        return note;
    }
}
