package fr.adbonnin.cz2128.fixture

import fr.adbonnin.cz2128.CZ2128

abstract class BaseJsonProviderSpec extends BaseJsonSpec {

    abstract CZ2128.JsonProviderBuilder setupJsonProvider(String content)
}
