package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class CandidatesSpec extends Specification {

    def 'Use an invalid candidate'() {
        setup:
        Candidates candidates = new Candidates()

        when:
        candidates.use(null, 'serenity', null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "serenity is not a valid candidate."
    }

    def 'Use an installed candidate version'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = Files.createDirectory(candidateDir.resolve('1.0'))

        def context = Mock(Context)
        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions
        def options = new Options()

        when:
        def returnedVersionDir = candidates.use(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Use a non-installed candidate version while not permitting installation'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')

        def context = Mock(Context)
        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions
        def options = new Options()

        when:
        candidates.use(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }

        def e = thrown(Exception)
        e.message == "$candidateName 1.0 is not installed"

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Use a not-installed candidate version while permitting installation'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')

        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions

        def candidateInstaller = Mock(CandidateInstaller)
        candidates.candidateInstaller = candidateInstaller

        def context = Mock(Context)
        def options = new Options(install: true)

        when:
        def returnedVersionDir = candidates.use(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }
        1 * candidateInstaller.installCandidateVersion(context, candidateName, '1.0') >> {
            versionDir
        }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Install an installed candidate version'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = Files.createDirectory(candidateDir.resolve('1.0'))

        def context = Mock(Context)
        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions
        def options = new Options()

        when:
        def returnedVersionDir = candidates.install(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Install a not-installed candidate version'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')

        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions

        def candidateInstaller = Mock(CandidateInstaller)
        candidates.candidateInstaller = candidateInstaller

        def context = Mock(Context)
        def options = new Options()

        when:
        def returnedVersionDir = candidates.install(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }
        1 * candidateInstaller.installCandidateVersion(context, candidateName, '1.0') >> {
            versionDir
        }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Install a not-installed candidate version and set it as default'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')
        def currentVersionDir = candidateDir.resolve('current')

        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions

        def candidateInstaller = Mock(CandidateInstaller)
        candidates.candidateInstaller = candidateInstaller

        def context = Mock(Context)
        def options = new Options('default': true)

        when:
        def returnedVersionDir = candidates.install(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }
        1 * candidateInstaller.installCandidateVersion(context, candidateName, '1.0') >> {
            versionDir
        }
        1 * context.candidateCurrentVersion(candidateDir) >> { currentVersionDir }

        returnedVersionDir == currentVersionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }
}
