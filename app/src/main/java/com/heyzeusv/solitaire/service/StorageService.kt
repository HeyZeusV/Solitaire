package com.heyzeusv.solitaire.service

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

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val currentUserGameStats: Flow<List<SingleGameStats>>
//        get() = auth.currentUser.flatMapLatest { user ->
//            firestore
//                .collection(USER_COLLECTION)
//                .document(user.id)
//                .collection(GAMESTATS_COLLECTION)
//                .dataObjects()
//        }

    suspend fun usernameExists(username: String): Boolean {
        val query =
            firestore.collection(USERNAME_COLLECTION).whereEqualTo(FieldPath.documentId(), username)
        return !query.get().await().isEmpty
    }

    suspend fun addUsername(usernameToAdd: String) {
        val user = hashMapOf(
            "username" to usernameToAdd,
            "usernameLower" to usernameToAdd.lowercase()
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

    suspend fun uploadLocalData(gameStats: List<SingleGameStats>) {
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

    companion object {
        private const val USER_COLLECTION = "users"
        private const val USERNAME_COLLECTION = "usernames"
        private const val GAMESTATS_COLLECTION = "gameStats"
    }
}