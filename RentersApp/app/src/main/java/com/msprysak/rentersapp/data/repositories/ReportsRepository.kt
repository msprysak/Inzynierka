package com.msprysak.rentersapp.data.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.User
import java.util.UUID

class ReportsRepository  {

    private val storage = FirebaseStorage.getInstance()
    private val cloud = FirebaseFirestore.getInstance()


    private val DEBUG = "ReportsRepository_DEBUG"
     fun createNewReport(
        report: Reports,
        premisesId: String,
        userId: String,
        selectedImages: List<Uri>,
        callBack: CallBack
    ) {
        val reportDocRef = cloud.collection("reports").document()

        val reportData = hashMapOf(
            "reportId" to reportDocRef.id,
            "userId" to userId,
            "premisesId" to premisesId,
            "reportDescription" to report.reportDescription,
            "reportStatus" to "pending",
            "reportTitle" to report.reportTitle,
            "reportDate" to (report.reportDate ?: Timestamp.now()),
            "reportImages" to listOf<String>()
        )

        val uploadedPhotoUrls = mutableListOf<String>()

        if (selectedImages.isNotEmpty()) {
            selectedImages.forEach { selectedImage ->
                val randomFileName = UUID.randomUUID().toString()
                val storageRef = storage.reference
                    .child("premises")
                    .child(premisesId)
                    .child("reports")
                    .child(reportDocRef.id)
                    .child(randomFileName)

                storageRef.putFile(selectedImage)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }
                    .addOnCompleteListener { downloadUrlTask ->
                        if (downloadUrlTask.isSuccessful) {
                            val downloadUrl = downloadUrlTask.result.toString()
                            uploadedPhotoUrls.add(downloadUrl)

                            // Jeśli wszystkie zdjęcia zostały przesłane, dodaj raport do Firestore
                            if (uploadedPhotoUrls.size == selectedImages.size) {
                                // Przygotuj dane do Firestore
                                val finalPhotoUrls = uploadedPhotoUrls.toList()

                                reportData["reportImages"] = finalPhotoUrls

                                reportDocRef.set(reportData)
                                    .addOnSuccessListener {
                                        Log.d(DEBUG, "uploadReportPhotosAndAddToFirestore: success")
                                        callBack.onSuccess()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(
                                            DEBUG,
                                            "uploadReportPhotosAndAddToFirestore: ${exception.message}"
                                        )
                                        callBack.onFailure("Ups, coś poszło nie tak, spróbuj ponownie później.")
                                    }
                            }
                        }
                    }
            }
        } else {
            reportDocRef.set(reportData)
                .addOnSuccessListener {
                    Log.d(DEBUG, "uploadReportPhotosAndAddToFirestore: success")
                    callBack.onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.d(
                        DEBUG,
                        "uploadReportPhotosAndAddToFirestore: ${exception.message}"
                    )
                    callBack.onFailure("Ups, coś poszło nie tak, spróbuj ponownie później, lub skontaktuj się z administratorem.")
                }
        }
    }


     fun editReport() {
        TODO("Not yet implemented")
    }

     fun deleteReport() {
        TODO("Not yet implemented")
    }

//    fun setupReportsListener(premisesId: String) {
//        cloud.collection("reports")
//            .whereEqualTo("premisesId", premisesId)
//            .orderBy("reportDate")
//
//            .addSnapshotListener{ documentSnapshot, error ->
//                if (error != null){
//                    Log.d(DEBUG, "setupReportsObserver: ${error.message}")
//                    return@addSnapshotListener
//                }
//                val reports = mutableListOf<Reports>()
//                for (doc in documentSnapshot!!){
//                    val report = doc.toObject(Reports::class.java)
//                    reports.add(report)
//                }
//                reportsLiveData.value = reports
//            }
//    }
//
//    override fun getReports(): LiveData<List<Reports>> {
//        return reportsLiveData
//    }

    //    override fun getFullReportById(reportId: String): LiveData<FullReport> {
//        val reportDocRef = cloud.collection("reports").document(reportId)
//            .get()
//            .addOnSuccessListener { documentSnapshot -> }
//
//
//    }
     fun setupReportsListener(premisesId: String): LiveData<List<Pair<Reports, User>>> {
        val reportsLiveData = MutableLiveData<List<Pair<Reports, User>>>()

        cloud.collection("reports")
            .whereEqualTo("premisesId", premisesId)
            .orderBy("reportDate", Query.Direction.DESCENDING)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Log.d(DEBUG, "setupReportsObserver: ${error.message}")
                    return@addSnapshotListener
                }

                val reports = mutableListOf<Pair<Reports, User>>()
                val tasks = mutableListOf<Task<DocumentSnapshot>>()

                for (doc in documentSnapshot!!) {
                    val report = doc.toObject(Reports::class.java)
                    val userTask = cloud.collection("users").document(report.userId!!)
                        .get()

                    tasks.add(userTask)
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                    .addOnSuccessListener { userSnapshots ->
                        for ((index, doc) in documentSnapshot.withIndex()) {
                            val report = doc.toObject(Reports::class.java)
                            val user = userSnapshots[index].toObject(User::class.java)
                            reports.add(Pair(report, user!!))
                        }
                        reportsLiveData.postValue(reports)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(DEBUG, "setupReportsObserver: ${exception.message}")
                    }
            }

        return reportsLiveData
    }

//    override fun getReports(): LiveData<List<Pair<Reports, User>>> {
//        return reportsLiveData
//    }

}