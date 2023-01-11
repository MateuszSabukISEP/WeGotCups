package fr.isep.wegotcups.databasehandler

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isep.wegotcups.event.EventData
import java.util.ArrayList

class DatabaseHandler {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun addEvent(eventData: EventData?){
        val event = hashMapOf(
            "name" to eventData?.name.toString(),
            "owner" to auth.currentUser?.uid,
            "date" to eventData?.date.toString(),
            "time" to eventData?.time.toString(),
            "location" to eventData?.location.toString(),
            //TODO set image uri
            "imageUri" to eventData?.imageUri.toString(),
            "dresscode" to eventData?.dresscode.toString(),
            "description" to eventData?.description.toString(),
        )

        db.collection("events")
            .add(event)
            .addOnSuccessListener { Log.d(TAG, "Event successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun getMyEvents(funForEveryEvent: (EventData) -> Unit,afterEventsLoaded: () -> Unit, includeSharedWithMe: Boolean = true) {
        db.collection("events")
            .whereEqualTo("owner", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val event = EventData(document)
                    funForEveryEvent(event)
                }
                afterEventsLoaded()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun addFriend(uid: String){
        val docRef: DocumentReference = db.document("/users/$uid")
        docRef.get()
            .addOnSuccessListener { user ->
                if (user.exists()){
                    db.collection("users").document(auth.currentUser?.uid.toString())
                        .update("friends", FieldValue.arrayUnion(docRef))
                        .addOnSuccessListener { Log.d(TAG, "Friend successfully added!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error looking for the document", e) }

    }

    fun getMyFriends(funForEveryFriend: (String) -> Unit){
        db.collection("users").document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { user ->
                val friends = user?.data?.get("friends") as ArrayList<DocumentReference>
                for (friend in friends){
                    friend.get().addOnSuccessListener{ friend ->
                        funForEveryFriend(friend?.data?.get("name").toString())
                    }
                    .addOnFailureListener{exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun createUser(user: FirebaseUser) {
        val data = hashMapOf(
            "name" to user.displayName.toString(),
            "friends" to arrayListOf<DocumentReference>(),
            "email" to user.email.toString(),
        )
        db.collection("users").document(user.uid).set(data, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Event successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
}