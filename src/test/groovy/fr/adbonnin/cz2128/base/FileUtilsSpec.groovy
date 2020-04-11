package fr.adbonnin.cz2128.base

import spock.lang.Specification

import java.nio.file.FileSystem
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider

class FileUtilsSpec extends Specification {

    void "should delete quietly if the file exists"() {
        given:
        def provider = Stub(FileSystemProvider) {
            _ >> { throw new IOException() }
        }

        def fs = Stub(FileSystem) {
            provider() >> provider
        }

        def path = Stub(Path) {
            getFileSystem() >> fs
        }

        when:
        FileUtils.deleteIfExistsQuietly(path)

        then:
        notThrown(IOException)
    }
}
