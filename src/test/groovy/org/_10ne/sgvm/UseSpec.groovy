package org._10ne.sgvm

import spock.lang.Specification

/**
 * @author Noam Y. Tenne.
 */
class UseSpec extends Specification {

    def 'Use a latest candidate'() {
        setup:
        def use = new Use()

        when:
        Candidate candidate = use."$candidateName"()

        then:
        candidate.name == candidateName
        candidate.options.version == 'latest'
        !candidate.options.install

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot', 'vertx']
    }

    def 'Use a specific candidate'() {
        setup:
        def use = new Use()

        when:
        Candidate candidate = use."$candidateName"(version: '1.6')

        then:
        candidate.name == candidateName
        candidate.options.version == '1.6'
        !candidate.options.install

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot', 'vertx']
    }

    def 'Use a specific candidate with the option of installing'() {
        setup:
        def use = new Use()

        when:
        Candidate candidate = use."$candidateName"(version: '1.6', install: true)

        then:
        candidate.name == candidateName
        candidate.options.version == '1.6'
        candidate.options.install

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot', 'vertx']
    }

    def 'Use an unknown candidate'() {
        setup:
        def use = new Use()

        when:
        use.jim()

        then:
        def iae = thrown(IllegalArgumentException)
        iae.message == 'jim is not a valid candidate.'
    }
}
