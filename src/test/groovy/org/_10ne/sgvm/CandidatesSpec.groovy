package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class CandidatesSpec extends Specification {

    def 'Get the directory of a valid candidate'() {
        setup:
        def candidates = new Candidates()

        def candidateTempDir = Files.createTempDirectory(candidateName)
        def context = Mock(Context)

        when:
        def candidateDir = candidates.get(context, candidateName)

        then:
        1 * context.candidateDir(candidateName) >> candidateTempDir
        candidateDir == candidateTempDir

        where:
        candidateName << Candidates.KNOWN_CANDIDATES
    }

    def 'Get the directory of an invalid candidate'() {
        setup:
        def candidates = new Candidates()

        when:
        candidates.get(null, 'bob')

        then:
        def iae = thrown(IllegalArgumentException)
        iae.message == 'bob is not a valid candidate.'
    }
}
