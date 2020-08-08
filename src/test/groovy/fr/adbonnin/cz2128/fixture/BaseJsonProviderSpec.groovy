package fr.adbonnin.cz2128.fixture

import fr.adbonnin.cz2128.JsonProvider

abstract class BaseJsonProviderSpec extends BaseJsonSpec {

    abstract JsonProvider setupJsonProvider(String content)
}
