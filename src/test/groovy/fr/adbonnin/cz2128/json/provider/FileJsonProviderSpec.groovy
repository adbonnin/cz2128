package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.JsonProvider

import java.nio.file.Files
import java.nio.file.Paths

class FileJsonProviderSpec extends MemoryJsonProviderSpec {

    @Override
    JsonProvider setupJsonProvider(String content) {
        def tempFile = Files.createTempFile('test-', '.json')
        def jsonProvider = new FileJsonProvider(tempFile)
        jsonProvider.content = content
        return jsonProvider
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
