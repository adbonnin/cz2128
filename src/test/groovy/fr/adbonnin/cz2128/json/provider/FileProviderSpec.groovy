package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.json.Json

import java.nio.file.Paths

class FileProviderSpec extends MemoryProviderSpec {

    @Override
    Json.ProviderFactory setupProviderFactory(String content) {
        return newFileProviderFactory(content)
    }

    @Override
    ContentProvider setupProvider() {
        return newFileProvider()
    }

    void "should build default temporary file"() {
        expect:
        FileProvider.buildDefaultTempFile(Paths.get(file)) == Paths.get(expectedTempFile)

        where:
        file       || expectedTempFile
        'file'     || 'file.tmp'
        'dir/file' || 'dir/file.tmp'
    }
}
