package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.JsonProvider
import fr.adbonnin.cz2128.base.FileUtils
import spock.lang.Subject

import java.nio.file.Files
import java.nio.file.Paths

class FileJsonProviderSpec extends MemoryJsonProviderSpec {

    @Subject
    def fileJsonProvider = new FileJsonProvider(Files.createTempFile('test-', '.json'))

    @Override
    JsonProvider setupJsonProvider(String content) {
        fileJsonProvider.content = content
        return fileJsonProvider
    }

    def cleanup() {
        FileUtils.deleteIfExistsQuietly(fileJsonProvider.file)
        FileUtils.deleteIfExistsQuietly(fileJsonProvider.tempFile)
    }

    @Override
    String getJsonProviderContent() {
        return fileJsonProvider.content
    }

    void "should build default temporary file"() {
        expect:
        FileJsonProvider.buildDefaultTempFile(Paths.get(file)) == Paths.get(expectedTempFile)

        where:
        file       || expectedTempFile
        'file'     || 'file.tmp'
        'dir/file' || 'dir/file.tmp'
    }
}
