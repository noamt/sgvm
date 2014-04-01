package org._10ne.sgvm

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class Use {

    Context context
    Candidates candidates = new Candidates()
    CandidateVersions candidateVersions = new CandidateVersions()
    CandidateInstaller candidateInstaller = new CandidateInstaller()

    def methodMissing(String name, def args) {
        def candidateDir = candidates.get(context, name)

        def options = new Options(args)
        def version = candidateVersions.determine(context, candidateDir, options)

        if (Files.exists(version.dir)) {
            version.dir
        } else {
            if (options.install) {
                candidateInstaller.installCandidateVersion(context, name, version.name)
            } else {
                throw new Exception("$name $version.name is not installed")
            }
        }
    }
}
