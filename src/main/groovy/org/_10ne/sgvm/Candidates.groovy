package org._10ne.sgvm

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Noam Y. Tenne.
 */
class Candidates {

    private static Set<String> KNOWN_CANDIDATES = ['gaiden', 'gradle', 'grails', 'griffon', 'groovy', 'groovyserv',
            'lazybones', 'springboot', 'vertx'] as Set

    CandidateVersions candidateVersions = new CandidateVersions()
    CandidateInstaller candidateInstaller = new CandidateInstaller()

    Path use(Context context, String name, Options options) {
        validateCandidate(name)
        def candidateDir = context.candidateDir(name)

        def version = candidateVersions.determine(context, candidateDir, options)
        def versionDir = context.candidateVersionDir(candidateDir, version)

        if (Files.exists(versionDir)) {
            versionDir
        } else {
            if (options.install) {
                candidateInstaller.installCandidateVersion(context, name, version)
            } else {
                throw new Exception("$name $version is not installed")
            }
        }
    }

    Path install(Context context, String name, Options options) {
        validateCandidate(name)
        def candidateDir = context.candidateDir(name)

        def version = candidateVersions.determine(context, candidateDir, options)
        def versionDir = context.candidateVersionDir(candidateDir, version)

        if (Files.exists(versionDir)) {
            return versionDir
        }

        versionDir = candidateInstaller.installCandidateVersion(context, name, version)
        if (options.defaultVersion) {
            def currentVersion = context.candidateCurrentVersion(candidateDir)
            Files.deleteIfExists(currentVersion)
            return Files.createSymbolicLink(currentVersion, versionDir)
        }
        versionDir
    }

    private void validateCandidate(String name) {
        if (!KNOWN_CANDIDATES.contains(name)) {
            throw new IllegalArgumentException("$name is not a valid candidate.");
        }
    }

}
