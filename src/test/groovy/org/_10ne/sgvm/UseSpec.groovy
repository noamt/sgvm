package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class UseSpec extends Specification {

    def 'Use a candidate with no arguments'() {
        setup:
        def context = new Context()

        def use = new Use(context: context)

        def candidates = Mock(Candidates)
        use.candidates = candidates

        def candidatePath = Paths.get('candidate')

        when:
        def returnedPath = use.candidate()

        then:
        1 * candidates.use(context, 'candidate', _ as Options) >> { Context ctx, String candidateName, Options opts ->
            assert !opts.version
            assert !opts.offline
            assert !opts.install
            candidatePath
        }
        returnedPath == candidatePath
    }

    def 'Use a candidate with arguments'() {
        setup:
        def context = new Context()

        def use = new Use(context: context)

        def candidates = Mock(Candidates)
        use.candidates = candidates

        def candidatePath = Paths.get('candidate')

        when:
        def returnedPath = use.candidate([version: '1.0', offline: true, install: true])

        then:
        1 * candidates.use(context, 'candidate', _ as Options) >> { Context ctx, String candidateName, Options opts ->
            assert opts.version == '1.0'
            assert opts.offline
            assert opts.install
            candidatePath
        }
        returnedPath == candidatePath
    }
}
