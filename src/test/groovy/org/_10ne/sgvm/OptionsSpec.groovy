package org._10ne.sgvm

import spock.lang.Specification

/**
 * @author Noam Y. Tenne.
 */
class OptionsSpec extends Specification {

    def 'Construct options from empty args'() {
        setup:
        def options = new Options()

        expect:
        !options.install
        !options.offline
        !options.version
        !options.defaultVersion
    }

    def 'Construct options'() {
        setup:
        def options = new Options([[install: true, offline: true, version: '1.0', 'default': true]] as Object[])

        expect:
        options.install
        options.offline
        options.version == '1.0'
        options.defaultVersion
    }
}
