package com.msprysak.rentersapp.ui.invoices

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.databinding.FragmentReportsBinding

class InvoicesFragment : BaseFragment() {


    private val invoicesViewModel by viewModels<InvoicesViewModel>()

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addReportButton.setOnClickListener {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
            } else {
                invociceDialog(requireContext())
            }
        }
    }

    private fun selectPdf() {
        pickPdf.launch("application/pdf")

    }
    private val pickPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val pdfFileName = getFileName(uri)
            invoicesViewModel.uploadPdfFile(pdfFileName, uri, object : CallBack {
                override fun onSuccess() {
                    Toast.makeText(context, "Plik został przesłany", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            })

        }
    }
    private fun getFileName(uri: Uri): String {
        // Pobierz nazwę pliku z URI
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val fileName = cursor?.getString(nameIndex ?: 0)
        cursor?.close()
        return fileName ?: "unknown.pdf"
    }
    private fun invociceDialog(context: Context){
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.add_invoice))
            .setMessage(resources.getString(R.string.choose_add_inovice_option))
            .setPositiveButton(resources.getString(R.string.upload)) { dialog, which ->
                selectPdf()
            }
            .setNegativeButton(resources.getString(R.string.create_from_template)) { dialog, which ->
                val navController = findNavController()
                navController.navigate(R.id.action_invoicesFragment_to_invoicesTemplateFragment)
            }
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
            }
            .show()
    }









}