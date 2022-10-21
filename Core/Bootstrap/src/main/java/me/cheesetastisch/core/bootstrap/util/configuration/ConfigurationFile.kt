package me.cheesetastisch.core.bootstrap.util.configuration

import java.io.File

class ConfigurationFile(val file: File, providerClass: Class<out IFileConfigurationProvider>) {

    val configurationProvider: IFileConfigurationProvider

    init {
        configurationProvider = providerClass.constructors[0].newInstance(file) as IFileConfigurationProvider
    }

}