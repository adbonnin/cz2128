package fr.adbonnin.albedo.util.web.support;

import java.net.URISyntaxException;

public class RuntimeURISyntaxException extends RuntimeException {

    public RuntimeURISyntaxException(URISyntaxException cause) {
        super(cause);
    }
}
