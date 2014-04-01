package org._10ne.sgvm

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class Install {

    Context context
    Candidates candidates = new Candidates()
    CandidateVersions candidateVersions = new CandidateVersions()
    CandidateInstaller candidateInstaller = new CandidateInstaller()

    def methodMissing(String name, def args) {
        def candidateDir = candidates.get(context, name)

        def options = new Options(args)
        def version = candidateVersions.determine(context, candidateDir, options)

        if (Files.exists(version.dir)) {
            return version.dir
        }

        def versionDir = candidateInstaller.installCandidateVersion(context, name, version.name)
        if (options.defaultVersion) {
            def currentVersion = context.candidateCurrentVersion(candidateDir)
            Files.deleteIfExists(currentVersion)
            return Files.createSymbolicLink(currentVersion, versionDir)
        }
        versionDir
    }
}
