package fr.adbonnin.albedo.util.web;

import java.util.Iterator;
import java.util.Set;

public interface UnmodifiableEntries {

    Set<String> names();

    Iterator<String> values(String name);

    String first(String name);

    boolean empty();
}
