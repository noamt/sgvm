package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class CandidateVersionsSpec extends Specification {

    def 'Determine a provided version that\'s already installed while in offline mode'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')
        def context = Mock(Context)

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir,
                new Options(version: '1.0', offline: true))

        then:
        1 * context.candidateVersionInstalled(candidateDir, '1.0') >> { true }
        determinedVersion == '1.0'
    }

    def 'Determine a current version while in offline mode'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')
        def installedVersionDir = Files.createDirectories(candidateDir.resolve('1.0'))
        def context = Mock(Context)

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir,
                new Options(offline: true))

        then:
        1 * context.candidateHasCurrentVersion(candidateDir) >> { true }
        1 * context.candidateResolveCurrentDir(candidateDir) >> { installedVersionDir }
        determinedVersion == '1.0'
    }

    def 'Determine a provided version that\'s not installed while in offline mode'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')
        def context = Mock(Context)

        when:
        candidateVersions.determine(context, candidateDir, new Options(version: '1.0', offline: true))

        then:
        def e = thrown(Exception)
        e.message == 'Not available offline'
    }

    def 'Determine the latest version while in offline mode'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')
        def context = Mock(Context)

        when:
        candidateVersions.determine(context, candidateDir, new Options(offline: true))

        then:
        def e = thrown(Exception)
        e.message == 'Not available offline'
    }

    def 'Determine the latest version'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')

        def gvmService = Mock(GvmHttpClient)

        def context = Mock(Context) {
            1 * getService() >> { gvmService }
        }

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, new Options())

        then:
        1 * gvmService.defaultVersion(candidateDir.fileName.toString()) >> { '1.0' }
        determinedVersion == '1.0'
    }

    def 'Validate a provided candidate version name'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')

        def gvmService = Mock(GvmHttpClient)

        def context = Mock(Context) {
            1 * getService() >> { gvmService }
        }

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, new Options(version: '1.0'))

        then:
        1 * gvmService.validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { true }
        determinedVersion == '1.0'
    }

    def 'Validate a symlinked provided candidate version name'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')

        def gvmService = Mock(GvmHttpClient)

        def context = Mock(Context) {
            1 * getService() >> { gvmService }
        }

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, new Options(version: '1.0'))

        then:
        1 * gvmService.validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { false }
        1 * context.candidateVersionIsSymlink(candidateDir, '1.0') >> { true }
        determinedVersion == '1.0'
    }

    def 'Validate an installed provided candidate version name'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')

        def gvmService = Mock(GvmHttpClient)

        def context = Mock(Context) {
            1 * getService() >> { gvmService }
        }

        when:
        def determinedVersion = candidateVersions.determine(context, candidateDir, new Options(version: '1.0'))

        then:
        1 * gvmService.validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { false }
        1 * context.candidateVersionIsSymlink(candidateDir, '1.0') >> { false }
        1 * context.candidateVersionIsDir(candidateDir, '1.0') >> { true }
        determinedVersion == '1.0'
    }

    def 'Validate an invalid provided candidate version name'() {
        setup:
        CandidateVersions candidateVersions = new CandidateVersions()

        def candidateDir = Files.createTempDirectory('temp')

        def gvmService = Mock(GvmHttpClient)

        def context = Mock(Context) {
            1 * getService() >> { gvmService }
        }

        when:
        candidateVersions.determine(context, candidateDir, new Options(version: '1.0'))

        then:
        1 * gvmService.validCandidateVersion(candidateDir.fileName.toString(), '1.0') >> { false }
        1 * context.candidateVersionIsSymlink(candidateDir, '1.0') >> { false }
        1 * context.candidateVersionIsDir(candidateDir, '1.0') >> { false }
        def e = thrown(Exception)
        e.message == "1.0 is not a valid ${candidateDir.fileName.toString()} version."
    }
}
