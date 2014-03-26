package org._10ne.sgvm

import net.gvmtool.client.GvmClient
import net.gvmtool.client.GvmClientException
import net.gvmtool.client.Version
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class GvmHttpClientSpec extends Specification {

    def 'Resolve the default version of a candidate'() {
        setup:
        def gvmClient = GroovyMock(GvmClient, global: true)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.client = gvmClient

        when:
        def defaultVersion = gvmHttpClient.defaultVersion('candidateName')

        then:
        1 * gvmClient.getDefaultVersionFor('candidateName') >> { new Version(name: 'versionName') }
        defaultVersion == 'versionName'
    }

    def 'Resolve the default version of a candidate and encounter an exception'() {
        setup:
        def gvmClient = GroovyMock(GvmClient, global: true)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.client = gvmClient

        when:
        def defaultVersion = gvmHttpClient.defaultVersion('candidateName')

        then:
        1 * gvmClient.getDefaultVersionFor('candidateName') >> { throw new GvmClientException('') }
        defaultVersion == ''
    }

    def 'Validate a candidate version'() {
        setup:
        def gvmClient = GroovyMock(GvmClient, global: true)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.client = gvmClient

        when:
        def versionIsValid = gvmHttpClient.validCandidateVersion('candidateName', 'versionName')

        then:
        1 * gvmClient.validCandidateVersion('candidateName', 'versionName') >> { response }
        versionIsValid == response

        where:
        response << [true, false]
    }

    def 'Validate a candidate version and encounter an exception'() {
        setup:
        def gvmClient = GroovyMock(GvmClient, global: true)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.client = gvmClient

        when:
        def versionIsValid = gvmHttpClient.validCandidateVersion('candidateName', 'versionName')

        then:
        1 * gvmClient.validCandidateVersion('candidateName', 'versionName') >> { throw new GvmClientException('') }
        !versionIsValid
    }

    def 'Download a candidate distribution'() {
        setup:
        def gvmClient = GroovyMock(GvmClient, global: true)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.client = gvmClient

        def context = Mock(Context)

        Path tempFile = Files.createTempFile('some', 'file')
        tempFile.toFile() << 'jim'

        when:
        def archivePath = gvmHttpClient.downloadCandidate(context, 'candidateName', 'versionName')

        then:
        1 * gvmClient.getDownloadURL('candidateName', 'versionName') >> { tempFile.toUri().toURL() }
        1 * context.candidateArchive('candidateName', 'versionName') >> {
            Files.createTempDirectory('test').resolve('file')
        }
        archivePath.toFile().text == 'jim'
    }
}
