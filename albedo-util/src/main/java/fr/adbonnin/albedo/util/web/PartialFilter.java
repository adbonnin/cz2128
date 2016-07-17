package fr.adbonnin.albedo.util.web;

public interface PartialFilter {

    PartialFilter in(String field);

    boolean match(String field);
}
