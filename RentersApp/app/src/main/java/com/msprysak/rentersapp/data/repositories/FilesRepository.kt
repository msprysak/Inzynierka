package com.msprysak.rentersapp.data.repositories

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.interfaces.CallBack
import java.io.File
import java.util.Date

class FilesRepository {

    private val DEBUG = "FilesRepository"

    private val storage = FirebaseStorage.getInstance()
    private val cloud = FirebaseFirestore.getInstance()
    private val userRepository = UserRepositoryInstance.getInstance()
    private val premisesRepository = PremisesRepository.getInstance(userRepository.user)

    fun getFiles(collection: String, callback: (List<PdfFile>) -> Unit){
        val premisesId = premisesRepository.premises.value?.premisesId

        if (premisesId != null) {
            val contractsCollectionRef = cloud.collection("files").document(premisesId)
                .collection(collection)
                .orderBy("creationDate", Query.Direction.DESCENDING)

            contractsCollectionRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(DEBUG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val contracts = mutableListOf<PdfFile>()

                    for (doc in snapshot) {
                        val contract = doc.toObject(PdfFile::class.java)
                        contracts.add(contract)
                    }
                    callback(contracts)
                }
            }
        }
    }

    fun uploadPdfFile(fileName: String, pdfUri: Uri,collection: String, callBack: CallBack) {
        val premisesId = premisesRepository.premises.value?.premisesId

        if (premisesId != null) {
            val contractsCollectionRef = cloud.collection("files").document(premisesId)
                .collection(collection)

            val storageRef = storage.reference
                .child("premises")
                .child(premisesId)

            val pdfDocRef = contractsCollectionRef.document()

            val storageFilePath = "$collection/${pdfDocRef.id}"

            val pdfFileData = hashMapOf(
                "fileName" to fileName,
                "creationDate" to Date(System.currentTimeMillis()),
                "fileId" to pdfDocRef.id
            )

            val uploadTask = storageRef.child(storageFilePath).putFile(pdfUri)

            uploadTask.addOnCompleteListener { uploadSnapshot ->
                storageRef.child(storageFilePath).downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val pdfUrl = it.result.toString()
                        pdfFileData["fileUrl"] = pdfUrl
                        contractsCollectionRef.document(pdfDocRef.id).set(pdfFileData)
                    }
                }
                    .addOnSuccessListener { callBack.onSuccess() }
            }
                .addOnFailureListener {
                    Log.w(DEBUG, "uploadPdfFile: ${it.message}")
                    callBack.onFailure("Ups, coś poszło nie tak, spróbuj ponownie później.")
                }

        }
    }

    fun downloadFile(file: PdfFile, context: Context, callBack: CallBack, collection: String) {
        val premisesId = premisesRepository.premises.value?.premisesId

        val storageRef = storage.reference
            .child("premises")
            .child(premisesId!!)
            .child(collection)
            .child(file.fileId.toString())

        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            val request = DownloadManager.Request(downloadUri)
                .setTitle(file.fileName)
                .setDescription("Pobieranie pliku...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // Ustal lokalizację docelową na zewnętrznym katalogu publicznym
            val destinationDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val localFilePath = File(destinationDir, file.fileName)
            request.setDestinationUri(Uri.fromFile(localFilePath))

            // Wymagane uprawnienia
            request.setAllowedOverMetered(true)
            request.setAllowedOverRoaming(true)

            // Uruchom DownloadManager
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
            .addOnFailureListener {
                Log.w(DEBUG, "downloadFileFromStorage: $it")
            }
    }




    fun deletePdfFile(fileId: String, callBack: CallBack, collection: String) {
        val premisesId = premisesRepository.premises.value?.premisesId

        if (premisesId != null) {
            val contractsCollectionRef = cloud.collection("files").document(premisesId)
                .collection(collection)

            val storageRef = storage.reference
                .child("premises")
                .child(premisesId)

            val storageFilePath = "$collection/$fileId"

            // Usunięcie dokumentu z kolekcji Firestore
            contractsCollectionRef.document(fileId).delete()
                .addOnSuccessListener {
                    // Usunięcie pliku z Storage
                    storageRef.child(storageFilePath).delete()
                        .addOnSuccessListener {
                            callBack.onSuccess()
                        }
                        .addOnFailureListener {
                            Log.w(DEBUG, "deletePdfFile: $it")
                            callBack.onFailure("Ups, coś poszło nie tak podczas usuwania pliku.")
                        }
                }
                .addOnFailureListener {
                    Log.w(DEBUG, "deletePdfFile: $it")
                    callBack.onFailure("Ups, coś poszło nie tak podczas usuwania dokumentu z bazy danych.")
                }
        }
    }





}

