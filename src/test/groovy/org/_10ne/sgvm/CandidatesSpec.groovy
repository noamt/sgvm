package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class CandidatesSpec extends Specification {

    def 'Get an invalid candidate'() {
        setup:
        Candidates candidates = new Candidates()

        when:
        candidates.get(null, 'serenity', null)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "serenity is not a valid candidate."
    }

    def 'Get an installed candidate version'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = Files.createDirectory(candidateDir.resolve('1.0'))

        def context = Mock(Context)
        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions
        def options = new Options()

        when:
        def returnedVersionDir = candidates.get(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Get a non-installed candidate version while not permitting installation'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')

        def context = Mock(Context)
        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions
        def options = new Options()

        when:
        candidates.get(context, candidateName, options)

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

    def 'Get a downloaded but not-installed candidate version while permitting installation'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')
        def candidateArchive = Files.createTempFile(candidateName, 'zip')

        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions

        def candidateInstaller = Mock(CandidateInstaller)
        candidates.candidateInstaller = candidateInstaller

        def context = Mock(Context)
        def options = new Options(install: true)

        when:
        def returnedVersionDir = candidates.get(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }
        1 * context.candidateArchive(candidateName, '1.0') >> { candidateArchive }
        1 * candidateInstaller.installCandidateVersion(context, candidateName, '1.0', candidateArchive) >> {
            versionDir
        }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }

    def 'Get a not-downloaded ant not-installed candidate version while permitting installation'() {
        setup:
        Candidates candidates = new Candidates()

        def candidateDir = Files.createTempDirectory(candidateName)
        def versionDir = candidateDir.resolve('1.0')
        def archivesDir = Files.createTempDirectory('archives')
        def candidateArchive = archivesDir.resolve('somezip')

        def candidateVersions = Mock(CandidateVersions)
        candidates.candidateVersions = candidateVersions

        def candidateInstaller = Mock(CandidateInstaller)
        candidates.candidateInstaller = candidateInstaller

        def gvmService = Mock(GvmHttpClient)

        def context = Mock(Context) {
            1 * getService() >> { gvmService }
        }
        def options = new Options(install: true)

        when:
        def returnedVersionDir = candidates.get(context, candidateName, options)

        then:
        1 * context.candidateDir(candidateName) >> { candidateDir }
        1 * candidateVersions.determine(context, candidateDir, options) >> { '1.0' }
        1 * context.candidateVersionDir(candidateDir, '1.0') >> { versionDir }
        1 * context.candidateArchive(candidateName, '1.0') >> { candidateArchive }
        1 * context.archives() >> { archivesDir }
        1 * gvmService.downloadCandidate(context, candidateName, '1.0') >> {
            Files.createFile(candidateArchive)
            candidateArchive
        }
        1 * candidateInstaller.installCandidateVersion(context, candidateName, '1.0', candidateArchive) >> {
            versionDir
        }

        returnedVersionDir == versionDir

        where:
        candidateName << ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv', 'lazybones', 'springboot',
                'vertx']
    }
}
