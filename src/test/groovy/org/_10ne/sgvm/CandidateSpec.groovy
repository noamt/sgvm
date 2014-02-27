package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class CandidateSpec extends Specification {

    def setup() {
        Gvm.GVM_HOME_DIR = Paths.get(System.properties['user.home'], '.gvm')
    }

    def 'Request a specific existing version'() {
        setup:
        Gvm.GVM_HOME_DIR = Files.createTempDirectory('temp.gvm')
        def candidateDir = Files.createDirectories(Gvm.GVM_HOME_DIR.resolve('grails').resolve('1.6'))

        def candidate = new Candidate(name: 'grails', options: new Options([version: '1.6']))

        expect:
        candidate.get() == candidateDir.resolve('bin').resolve('grails')
    }

    def 'Request a latest version'() {
        setup:
        Gvm.GVM_HOME_DIR = Files.createTempDirectory('temp.gvm')

        def candidate = new Candidate(name: 'grails', options: new Options([version: 'latest']))

        when:
        candidate.get()

        then:
        def iae = thrown(IllegalArgumentException)
        iae.message == 'Not implemented'
    }

    def 'Request a specific non-existing version with the install switch turned on'() {
        setup:
        Gvm.GVM_HOME_DIR = Files.createTempDirectory('temp.gvm')

        def candidate = new Candidate(name: 'grails', options: new Options([version: '5.7', install: true]))

        when:
        candidate.get()

        then:
        def iae = thrown(IllegalArgumentException)
        iae.message == 'Not implemented'
    }

    def 'Request a specific non-existing version with the install switch turned off'() {
        setup:
        Gvm.GVM_HOME_DIR = Files.createTempDirectory('temp.gvm')

        def candidate = new Candidate(name: 'grails', options: new Options([version: '5.7']))

        when:
        candidate.get()

        then:
        def iae = thrown(IllegalArgumentException)
        iae.message == 'grails 5.7 is not installed'
    }
}
