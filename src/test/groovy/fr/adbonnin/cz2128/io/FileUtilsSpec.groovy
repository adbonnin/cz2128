package fr.adbonnin.cz2128.io

import spock.lang.Requires
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences

/**
 * @link https://git.eclipse.org/c/statet/org.eclipse.statet-commons.git/diff/jcommons/org.eclipse.statet.jcommons.util/src/org/eclipse/statet/jcommons/io/FileUtils.java?id=fcedc3a623283cf2e5156bd4fd12302aaff4b36a
 * @link https://git.eclipse.org/c/statet/org.eclipse.statet-commons.git/diff/jcommons/org.eclipse.statet.jcommons.util-tests/src/org/eclipse/statet/jcommons/io/FileUtilsTest.java?id=fcedc3a623283cf2e5156bd4fd12302aaff4b36a
 */
class FileUtilsSpec extends Specification {

    Path tempDir

    void setup() {
        tempDir = Files.createTempDirectory(FileUtilsSpec.simpleName).toAbsolutePath()
    }

    void cleanup() {
        FileUtils.deleteRecursively(tempDir)
    }

    static boolean isRunningAsAdministrator() {
        def preferences = Preferences.systemRoot()
        def name = 'SPOCK_IS_RUNNING_AS_ADMINISTRATOR'

        try {
            preferences.put(name, 'true')
            preferences.remove(name)
            preferences.flush()
            return true
        }
        catch (Exception ignored) {
            return false
        }
    }

    void "should recursively delete the folder"() {
        given:
        def dir = Files.createDirectory(tempDir.resolve("dir"))
        Files.write(dir.resolve("file"), "Hello".bytes)
        Files.write(dir.resolve("file_2"), "Hello".bytes)

        def subDir = Files.createDirectory(dir.resolve("sub"))
        Files.write(subDir.resolve("file"), "Hello".bytes)

        when:
        def result = FileUtils.deleteRecursively(dir)

        then:
        result
        Files.notExists(dir)
    }

    void "should return false if the file doesn't exists"() {
        given:
        def dir = tempDir.resolve("dir")

        expect:
        Files.notExists(dir)

        when:
        def result = FileUtils.deleteRecursively(dir)

        then:
        !result
        Files.notExists(dir)
    }

    @Requires({ isRunningAsAdministrator() })
    void "should delete recursively the folder with symbolic link file"() {
        given:
        def dir = Files.createDirectory(tempDir.resolve("dir"))
        Files.write(dir.resolve("file"), "Hello".bytes)

        and: "Target file with symbolic link"
        def targetFile = Files.write(tempDir.resolve("target-file"), "Hello".bytes)
        Files.createSymbolicLink(dir.resolve("file-link"), targetFile)

        when:
        def result = FileUtils.deleteRecursively(dir)

        then:
        result
        Files.notExists(dir)

        and: "Target symbolic linked file still exists"
        Files.exists(targetFile)
    }

    void "should delete recursively the folder with file links"() {
        given:
        def dir = Files.createDirectory(tempDir.resolve("dir"))
        Files.write(dir.resolve("file"), "Hello".bytes)

        and: "Target file with link"
        def targetFile = Files.write(tempDir.resolve("target-file"), "Hello".bytes)
        Files.createLink(dir.resolve("file-link"), targetFile)

        when:
        def result = FileUtils.deleteRecursively(dir)

        then:
        result
        Files.notExists(dir)

        and: "Target linked file still exists"
        Files.exists(targetFile)
    }

    @Requires({ isRunningAsAdministrator() })
    void "should delete recursively the folder without deleting folders with symbolic link"() {
        given:
        def dir = Files.createDirectory(tempDir.resolve("dir"))
        Files.write(dir.resolve("file"), "Hello".bytes)

        and: "Target folder with symbolic link"
        def targetDir = Files.createDirectory(tempDir.resolve("target-dir"))
        Files.createSymbolicLink(dir.resolve("dir-link"), targetDir)

        def targetDirFile = Files.write(targetDir.resolve("target-dir-file"), "Hello".bytes)

        when:
        def result = FileUtils.deleteRecursively(dir)

        then:
        result
        Files.notExists(dir)

        and: "Target symbolic folder still exists with its content"
        Files.exists(targetDir)
        Files.exists(targetDirFile)
    }

    @Requires({ os.windows })
    void "should delete recursively the folder without deleting folders created with the Windows junction command"() {
        given:
        def dir = Files.createDirectory(tempDir.resolve("dir"))
        Files.write(dir.resolve("file"), "Hello".bytes)

        and: "Target folder with junction command"
        def targetDir = Files.createDirectory(tempDir.resolve("file"))
        def linkDir = dir.resolve("dir-link")

        new ProcessBuilder("cmd", "/C", "mklink", "/J", linkDir.toString(), targetDir.toString())
            .directory(tempDir.toFile())
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectErrorStream(true)
            .start()
            .waitFor(5, TimeUnit.SECONDS)

        def targetDirFile = Files.write(targetDir.resolve("target-dir-file"), "Hello".bytes)

        expect:
        Files.isDirectory(linkDir)

        when:
        def result = FileUtils.deleteRecursively(dir)

        then:
        result
        Files.notExists(dir)

        and: "Target folder still exists with its content"
        Files.exists(targetDir)
        Files.exists(targetDirFile)
    }
}
