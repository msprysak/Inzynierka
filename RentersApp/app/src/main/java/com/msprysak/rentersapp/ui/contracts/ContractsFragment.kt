package com.msprysak.rentersapp.ui.contracts

import ItemsDecorator
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.FilesAdapter
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.interfaces.OnItemClickListener
import com.msprysak.rentersapp.data.model.PdfFile
import com.msprysak.rentersapp.databinding.FragmentReportsBinding

class ContractsFragment: BaseFragment(), OnItemClickListener {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val contractsViewModel by viewModels<ContractsViewModel>()

    private lateinit var filesAdapter: FilesAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        contractsViewModel.getContracts()
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = binding.addReportButton

        recyclerView = binding.reportsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        contractsViewModel.contractsList.observe(viewLifecycleOwner){
            println("contracts: $it")

            val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
            recyclerView.addItemDecoration(itemDecoration)
            filesAdapter = FilesAdapter(
                it,
                this,
                contractsViewModel.userRole
            )
            recyclerView.adapter = filesAdapter
            filesAdapter.notifyDataSetChanged()        }

        addButton.setOnClickListener {
            selectPdf()
        }
    }

    private fun selectPdf() {
        pickPdf.launch("application/pdf")

    }
    private val pickPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val pdfFileName = getFileName(uri)
            contractsViewModel.uploadPdfFile(pdfFileName, uri, object : CallBack {
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

    override fun onLandlordClick(item: Any, anchorView: View) {
        item as PdfFile
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.files_landlord_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.download -> {
                    contractsViewModel.downloadFile(item, requireContext(),  object : CallBack {
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
                    contractsViewModel.deleteFile(item, object : CallBack {
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
                    contractsViewModel.downloadFile(item,requireContext(), object : CallBack {
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