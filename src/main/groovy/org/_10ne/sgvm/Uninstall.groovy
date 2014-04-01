package org._10ne.sgvm

import java.nio.file.Files

/**
 * @author Noam Y. Tenne.
 */
class Uninstall {

    Context context
    Candidates candidates = new Candidates()

    def methodMissing(String name, def args) {
        def candidateDir = candidates.get(context, name)

        def options = new Options(args)
        def versionName = options.version
        if (!versionName) {
            throw new IllegalArgumentException('No valid candidate version was provided.');
        }

        if (context.candidateHasCurrentVersion(candidateDir)) {
            Files.delete(context.candidateCurrentVersion(candidateDir))
        }

        if (context.candidateVersionInstalled(candidateDir, versionName) &&
                context.candidateVersionIsDir(candidateDir, versionName)) {
            context.candidateVersionDir(candidateDir, versionName).toFile().deleteDir()
        }
    }
}
