package io.github.akbarrizky.util;

import org.jboss.logging.Logger;

/**
 * Global logging utility.
 * call directly: LogUtil.info("message");
 */
public class LogUtil {

    private static final Logger LOG = Logger.getLogger("TicketingAPI");

    private LogUtil() {
        // Prevent instantiation
    }

    public static void info(String message) {
        LOG.info(message);
    }

    public static void info(String message, Object... params) {
        LOG.infof(message, params);
    }

    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void error(String message) {
        LOG.error(message);
    }

    public static void error(String message, Throwable t) {
        LOG.error(message, t);
    }

    public static void debug(String message) {
        LOG.debug(message);
    }
}
