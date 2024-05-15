package com.heyzeusv.solitaire.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: Flow<UserData?>
        get() = auth.currentUser.flatMapLatest { user ->
            firestore
                .collection(USER_COLLECTION)
                .document(user.id)
                .dataObjects()
        }

    suspend fun usernameExists(username: String): Boolean {
        val query =
            firestore.collection(USERNAME_COLLECTION).whereEqualTo(FieldPath.documentId(), username)
        return !query.get().await().isEmpty
    }

    suspend fun addUsername(usernameToAdd: String) {
        val user = hashMapOf(
            "username" to usernameToAdd,
            "usernameLower" to usernameToAdd.lowercase(),
            "lastGameStatsUpload" to Timestamp.now(),
            "nextGameStatsUpload" to endOfDay()
        )
        val username = hashMapOf("userId" to auth.currentUserId)
        firestore.runBatch { batch ->
            batch.set(firestore.collection(USER_COLLECTION).document(auth.currentUserId), user)
            batch.set(
                firestore.collection(USERNAME_COLLECTION).document(usernameToAdd.lowercase()),
                username
            )
        }.await()
    }

    suspend fun uploadGameStats(gameStats: List<SingleGameStats>) {
        firestore.runBatch { batch ->
            gameStats.forEach { stats ->
                batch.set(
                    firestore.collection(USER_COLLECTION).document(auth.currentUserId).collection(
                        GAMESTATS_COLLECTION
                    ).document(stats.game), stats
                )
            }
        }.await()
    }

    suspend fun retrieveGameStats(): List<SingleGameStats> {
        val query = firestore.collection(USER_COLLECTION).document(auth.currentUserId)
            .collection(GAMESTATS_COLLECTION)
        return query.get().await().toObjects(SingleGameStats::class.java)
    }

    /**
     *  Returns [Timestamp] to end of day today using UTC-7 as timezone.
     */
    private fun endOfDay(): Timestamp {
        val timestampNow = Timestamp.now()
        val timeIntoDay = (timestampNow.seconds - UTC_MINUS7) % DAY_IN_SECONDS
        val secondsEOD = timestampNow.seconds - timeIntoDay + DAY_IN_SECONDS
        return Timestamp(seconds = secondsEOD, nanoseconds = 0)
    }

    companion object {
        private const val USER_COLLECTION = "users"
        private const val USERNAME_COLLECTION = "usernames"
        private const val GAMESTATS_COLLECTION = "gameStats"
        private const val UTC_MINUS7 = 25200
        private const val DAY_IN_SECONDS = 86400
    }
}