package com.heyzeusv.solitaire.util

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.heyzeusv.solitaire.StatPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class StatPreferencesSerializer @Inject constructor() : Serializer<StatPreferences> {
    override val defaultValue: StatPreferences = StatPreferences.getDefaultInstance().toBuilder()
        .setKtoLowestMoves(9999)
        .setKtoFastestWin(359999L)
        .setKtoBestTotalScore(99999L)
        .build()

    override suspend fun readFrom(input: InputStream): StatPreferences {
        try {
            return StatPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StatPreferences, output: OutputStream) = t.writeTo(output)
}