package com.msprysak.rentersapp.ui.addpremises

import ItemsDecorator
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.PremisesAdapter
import com.msprysak.rentersapp.data.model.Premises
import com.msprysak.rentersapp.databinding.FragmentAddNewHomeBinding
import com.msprysak.rentersapp.interfaces.OnPremisesClickListener
import com.msprysak.rentersapp.ui.createhome.AddHomeDialogFragment

class AddPremisesFragment: BaseFragment(), OnPremisesClickListener {

    private var _binding: FragmentAddNewHomeBinding? = null
    private val addPremisesViewModel by viewModels<AddPremisesViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var premisesAdapter: PremisesAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewHomeBinding.inflate(inflater, container, false)

        addPremisesViewModel.getAllPremises()
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = binding.addNewHome

        addButton.setOnClickListener {
            val addHomeDialogFragment = AddHomeDialogFragment()
            addHomeDialogFragment.show(parentFragmentManager, "AddHomeDialogFragment")
        }

        addPremisesViewModel.premisesList.observe(viewLifecycleOwner) {
            setupRecyclerView(it)
        }
    }
    private fun setupRecyclerView(paymentList: List<Premises>) {
        recyclerView = binding.homesRecyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if (recyclerView.itemDecorationCount == 0) {
            val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
            recyclerView.addItemDecoration(itemDecoration)
        }
        premisesAdapter = PremisesAdapter(paymentList, this)
        recyclerView.adapter = premisesAdapter
        premisesAdapter.notifyDataSetChanged()
    }

    override fun onPremisesClick(premises: Premises, anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.premises_popup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.choose -> {
                    addPremisesViewModel.premisesRepository.updatePremises(premises)
                    Toast.makeText(requireContext(), "Poprawnie wybrano nieruchomość ${premises.name}", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.delete -> {

                    AlertDialog.Builder(requireContext())
                        .setMessage("Czy na pewno chcesz usunąć nieruchomość ,,${premises.name}''?")
                        .setPositiveButton(R.string.delete) { _, _ ->
                            Toast.makeText(requireContext(), "Pomyślnie usunięto nieruchomość.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton(R.string.cancel){
                                dialog, _ -> dialog.dismiss()
                        }
                        .show()
                    true
                }
                else -> false
            }

        }
        popupMenu.show()
    }
}