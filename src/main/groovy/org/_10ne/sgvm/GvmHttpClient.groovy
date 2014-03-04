package org._10ne.sgvm

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import org.apache.commons.lang.BooleanUtils

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class GvmHttpClient {

    HTTPBuilder httpBuilder = new HTTPBuilder('http://api.gvmtool.net/')

    String defaultVersion(String candidateName) {
        String defaultVersion
        try {
            defaultVersion = httpBuilder.get([path: "candidates/$candidateName/default"])
        } catch (Throwable t) {
            t.printStackTrace()
            defaultVersion = ''
        }
        defaultVersion
    }

    boolean validCandidateVersion(String candidateName, String versionName) {
        String validityState
        try {
            validityState = httpBuilder.get([path: "candidates/$candidateName/$versionName"])
        } catch (Throwable t) {
            t.printStackTrace()
            validityState = 'invalid'
        }
        BooleanUtils.toBoolean(validityState, 'valid', 'invalid')
    }

    Path downloadCandidate(Context context, String candidateName, String versionName) {
        httpBuilder.get([path: "download/$candidateName/$versionName", query: [platform: 'Linux']]) { HttpResponseDecorator resp ->
            def candidateArchive = context.candidateArchive(candidateName, versionName)
            Files.createFile(candidateArchive)
            candidateArchive.toFile().withOutputStream {
                it
                resp.entity.writeTo(it)
            }

            candidateArchive
        }
    }
}
