package fr.adbonnin.cz2128.fixture

import fr.adbonnin.cz2128.json.Json

abstract class BaseJsonProviderSpec extends BaseJsonSpec {

    abstract Json.Provider setupJsonProvider(String content)
}
