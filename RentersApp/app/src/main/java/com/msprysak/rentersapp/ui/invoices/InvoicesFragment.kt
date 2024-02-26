package com.msprysak.rentersapp.ui.invoices

import ItemsDecorator
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.FilesAdapter
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.data.repositories.room.UserApplication
import com.msprysak.rentersapp.databinding.FragmentReportsBinding
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.interfaces.OnItemClickListener

class InvoicesFragment : BaseFragment(), OnItemClickListener {


    private val invoicesViewModel: InvoicesViewModel by viewModels {
        InvoicesViewModelFactory((requireActivity().application as UserApplication).roomRepository)
    }

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var filesAdapter: FilesAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        invoicesViewModel.getInvoices()
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

        recyclerView = binding.reportsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        invoicesViewModel.invoicesList.observe(viewLifecycleOwner){
            if (recyclerView.itemDecorationCount == 0) {
                val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
                recyclerView.addItemDecoration(itemDecoration)
            }
            filesAdapter = FilesAdapter(
                it,
                this,
                invoicesViewModel.userRole
            )
            recyclerView.adapter = filesAdapter
            filesAdapter.notifyDataSetChanged()        }
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

    override fun onLandlordClick(item: Any, anchorView: View) {
        item as PdfFile
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.files_landlord_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.download -> {
                    invoicesViewModel.downloadFile(item, requireContext(),  object : CallBack {
                        override fun onSuccess() {
                            Toast.makeText(
                                requireContext(),
                                "Plik został pobrany",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(message: String) {
                            Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                    true
                }
                R.id.delete -> {
                    invoicesViewModel.deleteFile(item, object : CallBack {
                        override fun onSuccess() {
                            Toast.makeText(
                                requireContext(),
                                "Plik został usunięty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(message: String) {
                            Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                    true
                }
                R.id.preview -> {
                    Toast.makeText(
                        requireContext(),
                        "Podgląd pliku",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onTenantClick(item: Any, anchorView: View) {
        item as PdfFile
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.files_user_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.download -> {
                    invoicesViewModel.downloadFile(item,requireContext(), object : CallBack {
                        override fun onSuccess() {
                            Toast.makeText(
                                requireContext(),
                                "Plik został pobrany",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(message: String) {
                            Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                    true
                }
                R.id.preview -> {
                    Toast.makeText(
                        requireContext(),
                        "Podgląd pliku",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


}