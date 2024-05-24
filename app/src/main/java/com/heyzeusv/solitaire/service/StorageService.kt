package com.heyzeusv.solitaire.service

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.heyzeusv.solitaire.games.Games
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) {
    suspend fun usernameExists(username: String): Boolean {
        val query =
            firestore.collection(USERNAMES_COLLECTION).whereEqualTo(FieldPath.documentId(), username)
        return !query.get().await().isEmpty
    }

    suspend fun addUsername(usernameToAdd: String) {
        val user = hashMapOf(
            "username" to usernameToAdd,
            "usernameLower" to usernameToAdd.lowercase()
        )
        val username = hashMapOf("userId" to auth.currentUserId)
        firestore.runBatch { batch ->
            batch.set(firestore.collection(USERS_COLLECTION).document(auth.currentUserId), user)
            batch.set(
                firestore.collection(USERNAMES_COLLECTION).document(usernameToAdd.lowercase()),
                username
            )
        }.await()
    }

    suspend fun uploadStats(gameStats: List<FsGameStats>) {
        firestore.runBatch { batch ->
            gameStats.forEach { stats ->
                batch.set(gameDocRef(auth.currentUserId, stats.game), stats)
            }
        }.await()
    }

    suspend fun uploadStatsAfterGame(gameStats: FsGameStats, allGameStats: FsGameStats) {
        firestore.runBatch { batch ->
            batch.set(gameDocRef(auth.currentUserId, gameStats.game), gameStats)
            batch.set(gameDocRef(auth.currentUserId, allGameStats.game), allGameStats)
        }.await()
    }

    suspend fun downloadPersonalStats(): List<FsGameStats> {
        val query = firestore.collection(USERS_COLLECTION).document(auth.currentUserId)
            .collection(GAMESTATS_COLLECTION)
        return query.get().await().toObjects(FsGameStats::class.java)
    }

    suspend fun downloadGlobalStats(): List<FsGameStats> {
        val globalStats = mutableListOf<FsGameStats>()
        Games.statsOrderedSubclasses.forEach { game ->
            val gameStatList = firestore.collectionGroup(GAMESTATS_COLLECTION)
                .whereEqualTo(GAME_FIELD, game.dbName).get().await()
                .toObjects(FsGameStats::class.java)
            var combinedStats = FsGameStats(game = game.dbName)
            gameStatList.forEach { combinedStats = combinedStats combineWith it }
            globalStats.add(combinedStats)
        }
        return globalStats
    }

    private fun gameDocRef(userId: String, game: String): DocumentReference {
        return firestore.collection(USERS_COLLECTION).document(userId)
            .collection(GAMESTATS_COLLECTION).document(game)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val USERNAMES_COLLECTION = "usernames"
        private const val GAMESTATS_COLLECTION = "gameStats"
        private const val GAME_FIELD = "game"
    }
}