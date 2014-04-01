package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class InstallSpec extends Specification {

    def 'Install an existing version'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)
        def candidateVersions = Mock(CandidateVersions)

        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def install = new Install(context: context, candidates: candidates, candidateVersions: candidateVersions)

        when:
        def versionDir = install.methodMissing('candidate', null)

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * candidateVersions.determine(context, candidateDir, _ as Options) >> new CandidateVersion(name: '1.0', dir: candidateVersionDir)
        versionDir == candidateVersionDir
    }

    def 'Install a non-existing version'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)
        def candidateVersions = Mock(CandidateVersions)
        def candidateInstaller = Mock(CandidateInstaller)

        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def install = new Install(context: context, candidates: candidates, candidateVersions: candidateVersions, candidateInstaller: candidateInstaller)

        when:
        def versionDir = install.methodMissing('candidate', null)

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * candidateVersions.determine(context, candidateDir, _ as Options) >> new CandidateVersion(name: '1.0', dir: candidateDir.resolve('momo'))
        1 * candidateInstaller.installCandidateVersion(context, 'candidate', '1.0') >> candidateVersionDir
        versionDir == candidateVersionDir
    }

    def 'Install a non-existing version and set as default'() {
        setup:
        def context = Mock(Context)
        def candidates = Mock(Candidates)
        def candidateVersions = Mock(CandidateVersions)
        def candidateInstaller = Mock(CandidateInstaller)

        def candidateDir = Files.createTempDirectory('candidate')
        def candidateVersionDir = Files.createTempDirectory(candidateDir, '1.0')

        def install = new Install(context: context, candidates: candidates, candidateVersions: candidateVersions, candidateInstaller: candidateInstaller)

        when:
        def versionDir = install.methodMissing('candidate', [[default: true]] as Object[])

        then:
        1 * candidates.get(context, 'candidate') >> candidateDir
        1 * candidateVersions.determine(context, candidateDir, _ as Options) >> new CandidateVersion(name: '1.0', dir: candidateDir.resolve('momo'))
        1 * candidateInstaller.installCandidateVersion(context, 'candidate', '1.0') >> candidateVersionDir
        1 * context.candidateCurrentVersion(candidateDir) >> candidateDir.resolve('current')
        versionDir == candidateDir.resolve('current')
    }
}
