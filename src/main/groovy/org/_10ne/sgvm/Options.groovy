package org._10ne.sgvm

/**
 * @author Noam Y. Tenne.
 */
class Options {

    String version
    boolean install

    Options(Object[] args) {
        if (!args || (args.size == 0) || !(args[0] instanceof Map)) {
            version = 'latest'
            install = false
        } else {
            Map options = args[0]
            version = (options.version) ?: 'latest'
            install = options.install?.toBoolean()
        }
    }
}
