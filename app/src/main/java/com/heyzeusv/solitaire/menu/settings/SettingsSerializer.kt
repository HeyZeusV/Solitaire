package com.heyzeusv.solitaire.menu.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.heyzeusv.solitaire.Settings
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 *  Used to convert from Proto to Kotlin and vice versa when working with DataStore.
 */
class SettingsSerializer @Inject constructor() : Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)
}