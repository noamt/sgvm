package org._10ne.sgvm

import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class CandidateVersions {

    String determine(Context context, Path candidateDir, Options options) {
        String candidateName = candidateDir.fileName.toString()
        def versionName = options.version

        if (options.offline) {
            if (versionName) {
                if (context.candidateVersionInstalled(candidateDir, versionName)) {
                    return versionName
                } else {
                    throw new Exception('Not available offline')
                }
            } else {
                if (context.candidateHasCurrentVersion(candidateDir)) {
                    def resolvedCurrentDir = context.candidateResolveCurrentDir(candidateDir)
                    return resolvedCurrentDir.fileName
                } else {
                    throw new Exception('Not available offline')
                }
            }
        } else {
            if (!versionName) {
                return context.service.defaultVersion(candidateName)
            } else {
                boolean versionValid = context.service.validCandidateVersion(candidateName, versionName)
                if (versionValid) {
                    return versionName
                }

                if (context.candidateVersionIsSymlink(candidateDir, versionName)) {
                    return versionName
                }

                if (context.candidateVersionIsDir(candidateDir, versionName)) {
                    return versionName
                }

                throw new Exception("$versionName is not a valid $candidateName version.")
            }
        }
    }
}