package com.msprysak.rentersapp.ui.invoices

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.databinding.FragmentPdfViewerBinding
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewerFragment: BaseFragment() {


    private var _binding: FragmentPdfViewerBinding? = null
    private val binding get() = _binding!!

    private lateinit var pdfFile: PdfFile
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfViewerBinding.inflate(inflater, container, false)
        return _binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfView = binding.pdfView
        pdfFile = arguments?.getParcelable("item")!!

        Toast.makeText(requireContext(), pdfFile.fileName, Toast.LENGTH_SHORT).show()

        RetrievePDFfromUrl(pdfView).execute(pdfFile.fileUrl)


    }
    class RetrievePDFfromUrl(private val pdfView: PDFView) : AsyncTask<String, Void, InputStream>() {
        override fun doInBackground(vararg strings: String): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url = URL(strings[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                return null
            }
            return inputStream
        }

        override fun onPostExecute(inputStream: InputStream?) {
            pdfView.fromStream(inputStream)
                .pages(0, 2, 1, 3, 3, 3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(0)
                .autoSpacing(false)
                .pageFitPolicy(FitPolicy.WIDTH)
                .fitEachPage(true)
                .pageSnap(false)
                .pageFling(false)
                .nightMode(false)
                .load()
        }
    }

}