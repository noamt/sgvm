package org._10ne.sgvm

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class Candidate {

    String name
    Options options

    Path get() {
        def candidateDir = Gvm.GVM_HOME_DIR.resolve(name)

        def version = version()
        def versionDir = candidateDir.resolve(version)

        if (Files.exists(versionDir)) {
            return versionDir.resolve('bin').resolve(name)
        } else {
            if (options.install) {
                throw new IllegalArgumentException('Not implemented')
            } else {
                throw new IllegalArgumentException("$name $version is not installed")
            }
        }
    }

    private String version() {
        if ('latest' == options.version) {
            throw new IllegalArgumentException('Not implemented')
        } else {
            options.version
        }
    }
}
