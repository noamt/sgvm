package org._10ne.sgvm

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.HttpEntity
import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class GvmHttpClientSpec extends Specification {

    def 'Resolve the default version of a candidate'() {
        setup:
        def httpBuilder = Mock(HTTPBuilder)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.httpBuilder = httpBuilder

        when:
        def defaultVersion = gvmHttpClient.defaultVersion('candidateName')

        then:
        1 * httpBuilder.get(_ as Map) >> { Map map ->
            assert map.path == "candidates/candidateName/default"
            'versionName'
        }
        defaultVersion == 'versionName'
    }

    def 'Resolve the default version of a candidate and encounter an exception'() {
        setup:
        def httpBuilder = Mock(HTTPBuilder)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.httpBuilder = httpBuilder

        when:
        def defaultVersion = gvmHttpClient.defaultVersion('candidateName')

        then:
        1 * httpBuilder.get(_ as Map) >> { Map map ->
            assert map.path == "candidates/candidateName/default"
            throw new Exception('error')
        }
        defaultVersion == ''
    }

    def 'Validate a candidate version'() {
        setup:
        def httpBuilder = Mock(HTTPBuilder)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.httpBuilder = httpBuilder

        when:
        def versionIsValid = gvmHttpClient.validCandidateVersion('candidateName', 'versionName')

        then:
        1 * httpBuilder.get(_ as Map) >> { Map map ->
            assert map.path == "candidates/candidateName/versionName"
            expectedResponse
        }
        versionIsValid == resolvedState

        where:
        expectedResponse | resolvedState
        'valid'          | true
        'invalid'        | false
    }

    def 'Validate a candidate version and encounter an exception'() {
        setup:
        def httpBuilder = Mock(HTTPBuilder)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.httpBuilder = httpBuilder

        when:
        def versionIsValid = gvmHttpClient.validCandidateVersion('candidateName', 'versionName')

        then:
        1 * httpBuilder.get(_ as Map) >> { Map map ->
            assert map.path == "candidates/candidateName/versionName"
            throw new Exception('error')
        }
        !versionIsValid
    }

    def 'Download a candidate distribution'() {
        setup:
        def httpBuilder = Mock(HTTPBuilder)

        def gvmHttpClient = new GvmHttpClient()
        gvmHttpClient.httpBuilder = httpBuilder

        def context = Mock(Context)

        def httpEntity = Mock(HttpEntity)
        def responseDecorator = Mock(HttpResponseDecorator) {
            1 * getEntity() >> { httpEntity }
        }

        when:
        def archivePath = gvmHttpClient.downloadCandidate(context, 'candidateName', 'versionName')

        then:
        1 * httpBuilder.get(_ as Map, _ as Closure) >> { Map map, Closure closure ->
            assert map.path == "download/candidateName/versionName"
            assert map.query == [platform: 'Linux']
            closure.call(responseDecorator)
        }
        1 * context.candidateArchive('candidateName', 'versionName') >> {
            Files.createTempDirectory('test').resolve('file')
        }
        1 * httpEntity.writeTo(_ as OutputStream) >> { OutputStream out ->
            out << 'jim'
        }
        archivePath.toFile().text == 'jim'
    }
}
