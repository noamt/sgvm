package org._10ne.sgvm

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class CandidateInstallerSpec extends Specification {

    def 'Install a distribution archive'() {
        setup:
        def installer = new CandidateInstaller()

        def contextTemp = Files.createTempDirectory('contextTemp')

        def candidateDir = Files.createTempDirectory('candidateDir')

        def context = Mock(Context)

        when:
        Path installedDistro = installer.installCandidateVersion(context, 'grails', '2.1.4', Paths.get(CandidateInstallerSpec.getResource('/grails-2.1.4.zip').toURI()))

        then:
        1 * context.tmp() >> { contextTemp }
        1 * context.candidateDir('grails') >> { String candidate -> candidateDir }
        1 * context.candidateVersionDir(candidateDir, '2.1.4') >> { Path candidate, String version ->
            candidateDir.resolve(version)
        }

        contextTemp.toFile().list().size() == 0
        Files.exists(installedDistro.resolve('dummy.file'))
    }
}
