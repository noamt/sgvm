package org._10ne.sgvm

/**
 * @author Noam Y. Tenne.
 */
class Options {

    String version
    boolean offline
    boolean install

    Options(Object[] methodMissingArgs) {
        if (!methodMissingArgs || (methodMissingArgs.size == 0) || !(methodMissingArgs[0] instanceof Map)) {
            version = null
            offline = false
            install = false
        } else {
            Map options = methodMissingArgs[0]
            version = (options.version) ?: null
            offline = options.offline?.toBoolean()
            install = options.install?.toBoolean()
        }
    }
}
