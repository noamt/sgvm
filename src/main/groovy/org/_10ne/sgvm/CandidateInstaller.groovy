package org._10ne.sgvm

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author Noam Y. Tenne.
 */
class CandidateInstaller {

    Path installCandidateVersion(Context context, String name, String version) {
        def archive = context.candidateArchive(name, version)
        if (!Files.exists(archive)) {
            Files.createDirectories(context.archives())
            archive = context.service.downloadCandidate(context, name, version)
        }

        def tempDir = context.tmp()
        archive.toFile().withInputStream {
            def zipIn = new ZipInputStream(it)
            ZipEntry entry
            while ((entry = zipIn.nextEntry) != null) {
                if (!entry.directory) {
                    def entryPath = tempDir.resolve(entry.name)
                    Files.createDirectories(entryPath.parent)
                    def entryFile = entryPath.toFile()
                    entryFile.withOutputStream {
                        int len = 0
                        byte[] buffer = new byte[4096]
                        while ((len = zipIn.read(buffer)) > 0) {
                            it.write(buffer, 0, len)
                        }
                    }
                    entryFile.setExecutable(true, true)
                    entryFile.setReadable(true, true)
                    entryFile.setWritable(true, true)
                }
            }
        }

        def extractedDirName = tempDir.toFile().list().find { it.endsWith(version) }
        def candidateDir = context.candidateDir(name)
        def versionDir = context.candidateVersionDir(candidateDir, version)
        Files.move(tempDir.resolve(extractedDirName), versionDir, StandardCopyOption.REPLACE_EXISTING)
    }
}