package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class UseSpec extends Specification {

    def 'Use a pre-installed version'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)
        def candidateVersions = Mock(CandidateVersions)

        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def use = new Use(context: context, candidates: candidates, candidateVersions: candidateVersions)

        when:
        def versionDir = use.methodMissing('candidate', null)

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * candidateVersions.determine(context, candidateDir, _ as Options) >> new CandidateVersion(name: '1.0', dir: candidateVersionDir)
        versionDir == candidateVersionDir
    }

    def 'Use a non-installed version with the install options switched off'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)
        def candidateVersions = Mock(CandidateVersions)

        def candidateDir = Files.createTempDirectory('candidate')

        def use = new Use(context: context, candidates: candidates, candidateVersions: candidateVersions)

        when:
        use.methodMissing('candidate', null)

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * candidateVersions.determine(context, candidateDir, _ as Options) >> new CandidateVersion(name: '1.0', dir: candidateDir.resolve('1.0'))
        def exception = thrown(Exception)
        exception.message == "candidate 1.0 is not installed"
    }

    def 'Use a non-installed version with the install options switched on'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)
        def candidateVersions = Mock(CandidateVersions)
        def candidateInstaller = Mock(CandidateInstaller)

        def candidateDir = Files.createTempDirectory('candidate')

        def use = new Use(context: context, candidates: candidates, candidateVersions: candidateVersions, candidateInstaller: candidateInstaller)

        when:
        def installedVersion = use.methodMissing('candidate', [[install: true]] as Object[])

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * candidateVersions.determine(context, candidateDir, _ as Options) >> new CandidateVersion(name: '1.0', dir: Paths.get('bob', 'mob'))
        1 * candidateInstaller.installCandidateVersion(context, 'candidate', '1.0') >> Files.createDirectory(candidateDir.resolve('1.0'))
        installedVersion == candidateDir.resolve('1.0')
    }
}
