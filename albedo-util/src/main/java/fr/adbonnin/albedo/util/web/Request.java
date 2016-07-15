package fr.adbonnin.albedo.util.web;

import java.net.URI;

public interface Request {

    String method();

    URI uri();

    UnmodifiableEntries pathVariables();
}
