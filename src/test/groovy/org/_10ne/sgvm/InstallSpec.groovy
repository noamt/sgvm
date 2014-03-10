package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class InstallSpec extends Specification {

    def 'Install a candidate with no arguments'() {
        setup:
        def context = new Context()

        def install = new Install(context: context)

        def candidates = Mock(Candidates)
        install.candidates = candidates

        def candidatePath = Paths.get('candidate')

        when:
        def returnedPath = install.candidate()

        then:
        1 * candidates.install(context, 'candidate', _ as Options) >> { Context ctx, String candidateName, Options opts ->
            assert !opts.version
            assert !opts.offline
            assert !opts.install
            assert !opts.defaultVersion
            candidatePath
        }
        returnedPath == candidatePath
    }

    def 'Install a candidate with arguments'() {
        setup:
        def context = new Context()

        def install = new Install(context: context)

        def candidates = Mock(Candidates)
        install.candidates = candidates

        def candidatePath = Paths.get('candidate')

        when:
        def returnedPath = install.candidate([version: '1.0', offline: true, install: true, default: true])

        then:
        1 * candidates.install(context, 'candidate', _ as Options) >> { Context ctx, String candidateName, Options opts ->
            assert opts.version == '1.0'
            assert opts.offline
            assert opts.install
            assert opts.defaultVersion
            candidatePath
        }
        returnedPath == candidatePath
    }
}
