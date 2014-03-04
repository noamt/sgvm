package org._10ne.sgvm

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Noam Y. Tenne.
 */
class Context {

    Path gvmHomeDir
    GvmHttpClient service

    static Context get() {
        def context = new Context()
        context.gvmHomeDir = Paths.get(System.properties['user.home'], '.gvm')

        if (Files.notExists(context.gvmHomeDir) || !Files.isDirectory(context.gvmHomeDir)) {
            throw new Exception("Cannot find the GVM home directory at ${context.gvmHomeDir}")
        }
        if (!Files.isReadable(context.gvmHomeDir)) {
            throw new Exception("Cannot read the GVM home directory at ${context.gvmHomeDir}")
        }

        context.service = new GvmHttpClient()
        context
    }

    Path archives() {
        gvmHomeDir.resolve('archives')
    }

    Path tmp() {
        gvmHomeDir.resolve('tmp')
    }

    Path candidateDir(String candidateName) {
        gvmHomeDir.resolve(candidateName)
    }

    boolean candidateVersionInstalled(Path candidateDir, String versionName) {
        Files.exists(candidateVersionDir(candidateDir, versionName))
    }

    boolean candidateVersionIsSymlink(Path candidateDir, String versionName) {
        Files.isSymbolicLink(candidateVersionDir(candidateDir, versionName))
    }

    boolean candidateVersionIsDir(Path candidateDir, String versionName) {
        Files.isDirectory(candidateVersionDir(candidateDir, versionName))
    }

    Path candidateVersionDir(Path candidateDir, String versionName) {
        candidateDir.resolve(versionName)
    }

    boolean candidateHasCurrentVersion(Path candidateDir) {
        candidateVersionIsSymlink(candidateDir, 'current')
    }

    Path candidateResolveCurrentDir(Path candidateDir) {
        Files.readSymbolicLink(candidateVersionDir(candidateDir, 'current'))
    }

    Path candidateArchive(String candidateName, String versionName) {
        archives().resolve("$candidateName-${versionName}.zip")
    }
}
