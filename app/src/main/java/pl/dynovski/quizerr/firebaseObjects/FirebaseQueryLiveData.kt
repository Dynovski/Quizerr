package pl.dynovski.quizerr.firebaseObjects

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class FirebaseQueryLiveData: LiveData<QuerySnapshot> {

    private val TAG = "FIREBASE_LIVEDATA"

    private val query: Query
    private val listener: ValueEventListener = ValueEventListener()
    private lateinit var registration: ListenerRegistration

    constructor(query: Query) {
        this.query = query
    }

    constructor(ref: CollectionReference) {
        query = ref
    }

    // Attaching listener
    override fun onActive() {
        Log.d(TAG, "onActive")
        registration = query.addSnapshotListener(listener)
    }

    // Removing listener
    override fun onInactive() {
        Log.d(TAG, "onInactive")
        registration.remove()
    }

    private inner class ValueEventListener: EventListener<QuerySnapshot> {

        override fun onEvent(
            queryDocumentSnapshots: QuerySnapshot?,
            e: FirebaseFirestoreException?
        ) {
            if (e != null)
                Log.d(TAG, "Listen failed: $e")
            else {
                for (dc in queryDocumentSnapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG, "New document: " + dc.document.data)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "Modified document: " + dc.document.data)
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed document: " + dc.document.data)
                        }
                    }
                }
                value = queryDocumentSnapshots!!
            }
        }
    }
}