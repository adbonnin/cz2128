package fr.adbonnin.albedo.util.web.support;

import fr.adbonnin.albedo.util.web.Entries;

import java.util.Iterator;
import java.util.Set;

public class SimpleEntries implements Entries {



    @Override
    public Set<String> names() {
        return null;
    }

    @Override
    public Iterator<String> values(String name) {
        return null;
    }

    @Override
    public String first(String name) {
        return null;
    }

    @Override
    public boolean empty() {
        return false;
    }
}
