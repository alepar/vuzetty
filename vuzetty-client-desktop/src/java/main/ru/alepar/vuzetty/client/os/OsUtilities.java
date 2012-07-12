package ru.alepar.vuzetty.client.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsUtilities {

    private static final Logger log = LoggerFactory.getLogger(OsUtilities.class);

    public static String getUserHome() {
        final String userHome = System.getProperty("user.home");
        log.debug("user.home = {}", userHome);
        return userHome;
    }
}
