package fr.adbonnin.albedo.util.io.resource.support;

import java.io.IOException;

public class EntryNotFoundException extends IOException {

    public EntryNotFoundException(String message) {
        super(message);
    }
}
