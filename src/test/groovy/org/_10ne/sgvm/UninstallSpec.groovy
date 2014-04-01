package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class UninstallSpec extends Specification {

    def 'Execute an uninstall without providing a version name'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)

        def uninstall = new Uninstall(context: context, candidates: candidates)

        when:
        uninstall.methodMissing('candidate', null)

        then:
        1 * candidates.get(context, 'candidate')
        def e = thrown(IllegalArgumentException)
        e.message == 'No valid candidate version was provided.'
    }

    def 'Execute an uninstall'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)

        def candidateDir = Files.createTempDirectory('candidate')
        def currentVersionDir = Files.createTempDirectory(candidateDir, 'current')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def uninstall = new Uninstall(context: context, candidates: candidates)

        when:
        uninstall.methodMissing('candidate', [[version: '1.0']] as Object[])

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * context.candidateHasCurrentVersion(candidateDir) >> true
        1 * context.candidateCurrentVersion(candidateDir) >> currentVersionDir
        1 * context.candidateVersionInstalled(candidateDir, '1.0') >> true
        1 * context.candidateVersionIsDir(candidateDir, '1.0') >> true
        1 * context.candidateVersionDir(candidateDir, '1.0') >> candidateVersionDir

        Files.notExists(currentVersionDir)
        Files.notExists(candidateVersionDir)
    }
}
