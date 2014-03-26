package org._10ne.sgvm

import net.gvmtool.client.GvmClient
import net.gvmtool.client.GvmClientException

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class GvmHttpClient {

    GvmClient client = GvmClient.instance()

    String defaultVersion(String candidateName) {
        try {
            client.getDefaultVersionFor(candidateName).name
        } catch (GvmClientException gce) {
            gce.printStackTrace()
            ''
        }
    }

    boolean validCandidateVersion(String candidateName, String versionName) {
        try {
            client.validCandidateVersion(candidateName, versionName)
        } catch (GvmClientException gce) {
            gce.printStackTrace()
            false
        }
    }

    Path downloadCandidate(Context context, String candidateName, String versionName) {
        def downloadUrl = client.getDownloadURL(candidateName, versionName)

        def candidateArchive = context.candidateArchive(candidateName, versionName)
        Files.createFile(candidateArchive)
        candidateArchive.toFile().withOutputStream { OutputStream output ->
            downloadUrl.withInputStream { InputStream input ->
                output << input
            }
        }

        candidateArchive
    }
}
