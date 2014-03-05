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

    Path get(Context context, String name, Options options) {
        validateCandidate(name)
        def candidateDir = context.candidateDir(name)

        def version = candidateVersions.determine(context, candidateDir, options)
        def versionDir = context.candidateVersionDir(candidateDir, version)

        if (Files.exists(versionDir)) {
            return versionDir
        } else {
            if (options.install) {
                def archive = context.candidateArchive(name, version)
                if (!Files.exists(archive)) {
                    Files.createDirectories(context.archives())
                    archive = context.service.downloadCandidate(context, name, version)
                }
                return candidateInstaller.installCandidateVersion(context, name, version, archive)
            } else {
                throw new Exception("$name $version is not installed")
            }
        }
    }

    private void validateCandidate(String name) {
        if (!KNOWN_CANDIDATES.contains(name)) {
            throw new IllegalArgumentException("$name is not a valid candidate.");
        }
    }
}
