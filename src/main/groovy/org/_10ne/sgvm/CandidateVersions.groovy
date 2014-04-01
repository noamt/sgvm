package org._10ne.sgvm

import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class CandidateVersions {

    CandidateVersion determine(Context context, Path candidateDir, Options options) {
        String candidateName = candidateDir.fileName.toString()
        def versionName = options.version

        if (options.offline) {
            if (versionName) {
                if (context.candidateVersionInstalled(candidateDir, versionName)) {
                    return version(context, candidateDir, versionName)
                } else {
                    throw new Exception('Not available offline')
                }
            } else {
                if (context.candidateHasCurrentVersion(candidateDir)) {
                    def resolvedCurrentDir = context.candidateResolveCurrentDir(candidateDir)
                    return version(context, candidateDir, resolvedCurrentDir.fileName.toString())
                } else {
                    throw new Exception('Not available offline')
                }
            }
        } else {
            if (!versionName) {
                return version(context, candidateDir, context.service.defaultVersion(candidateName))
            } else {
                boolean versionValid = context.service.validCandidateVersion(candidateName, versionName)
                if (versionValid) {
                    return version(context, candidateDir, versionName)
                }

                if (context.candidateVersionIsSymlink(candidateDir, versionName)) {
                    return version(context, candidateDir, versionName)
                }

                if (context.candidateVersionIsDir(candidateDir, versionName)) {
                    return version(context, candidateDir, versionName)
                }

                throw new Exception("$versionName is not a valid $candidateName version.")
            }
        }
    }

    private CandidateVersion version(Context context, Path candidateDir, String name) {
        def candidateVersionDir = context.candidateVersionDir(candidateDir, name)
        new CandidateVersion(name: name, dir: candidateVersionDir)
    }
}