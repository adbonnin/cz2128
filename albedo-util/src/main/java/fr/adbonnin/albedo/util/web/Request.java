package fr.adbonnin.albedo.util.web;

import fr.adbonnin.albedo.util.collect.UnmodifiableIterableMap;

import java.net.URI;

public interface Request {

    String method();

    URI uri();

    UnmodifiableIterableMap<String, String> pathVariables();

    PartialFilter partialResponse();
}
