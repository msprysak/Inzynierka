package com.msprysak.rentersapp.ui.payments

import ItemsDecorator
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.PaymentsSelectUsersAdapter
import com.msprysak.rentersapp.adapters.TenantsAdapter
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentPaymentsLandlordBinding
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.interfaces.OnItemClickListener
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class PaymentsLandlordFragment : BaseFragment(), OnItemClickListener {

    private var _binding: FragmentPaymentsLandlordBinding? = null
    private val binding get() = _binding!!

    private val paymentsViewModel by viewModels<PaymentsViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var tenantsAdapter: TenantsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsLandlordBinding.inflate(inflater, container, false)

        return _binding!!.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentsViewModel.fetchUsers()
        paymentsViewModel.usersListData.observe(viewLifecycleOwner) { usersList ->
            if (usersList.isNotEmpty()) {
                binding.addToSelectedUsers.visibility = View.VISIBLE
                binding.addToSelectedUsers.setOnClickListener {
                    showUsersAlertDialog(usersList)
                }

            } else {
                binding.addToSelectedUsers.visibility = View.GONE
            }
        }


        paymentsViewModel.payment.observe(viewLifecycleOwner){ payment ->

            if (payment.paymentSince != null && payment.paymentTo != null) {
                val formattedStartDate =
                    payment.paymentSince?.let {
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                            it
                        )
                    }
                val formattedEndDate =
                    payment.paymentTo?.let {
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                            it
                        )
                    }
                showPaymentsDates(formattedStartDate.toString(), formattedEndDate.toString())
            }

        }


        setupTextChangeListeners()
        buttonBindings()

        val paymentAmount = binding.paymentAmount
        val saveButton = binding.saveButton
        setDecimalFilter(paymentAmount)

        saveButton.setOnClickListener {
            setupSaveButton()
        }

        binding.calednarButton.setOnClickListener {
            showDateRangePicker()
        }

    }

    private fun setupTextChangeListeners() {
        binding.paymentTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val title = s?.toString()
                if (title != null) {
                    paymentsViewModel.setPaymentTitle(title)
                }
            }
        })

        binding.paymentAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val amountString = s?.toString()
                if (!amountString.isNullOrBlank()) {
                    paymentsViewModel.setPaymentAmount(amountString.toDouble())
                } else {
                    paymentsViewModel.setPaymentAmount(0.0)
                }
            }
        })
    }



    private fun setDecimalFilter(editText: EditText) {
        val filter = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val input = dest.toString() + source.toString()
                if (input.isEmpty()) return null
                try {
                    val inputDouble = input.toDouble()
                    val decimalCount =
                        input.split("\\.".toRegex()).toTypedArray().getOrNull(1)?.length ?: 0
                    if (decimalCount > 2) {
                        return dest?.subSequence(dstart, dend)
                    }
                } catch (e: NumberFormatException) {
                    return dest?.subSequence(dstart, dend)
                }
                return null
            }
        }

        (editText as? TextInputEditText)?.filters = arrayOf(filter)
    }

    private fun showUsersAlertDialog(userList: List<User>) {

        val filteredUsers = userList.filter { user ->
            !user.houseRoles!!.values.contains("landlord")
        }

        val userAdapter = PaymentsSelectUsersAdapter(requireContext(), filteredUsers)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.select_users))
            .setAdapter(userAdapter, null)
            .setPositiveButton(resources.getString(R.string.save)) { _, _ ->
                paymentsViewModel.addSelectedUsers(userAdapter.getSelectedUsers())
                setupRecyclerView()
                buttonBindings()
            }
            .setNeutralButton(resources.getString(R.string.select_all_users)) { _, _ ->
                paymentsViewModel.addSelectedUsers(filteredUsers.toMutableSet())
                setupRecyclerView()
                buttonBindings()
            }
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .show()
    }

    private fun setupSaveButton() {
        val paymentTitle = binding.paymentTitle
        val paymentAmount = binding.paymentAmount


        if (!paymentTitle.text.isNullOrBlank() && !paymentAmount.text.isNullOrEmpty() && paymentsViewModel.selectedUsers.isNotEmpty()) {
            Toast.makeText(requireContext(), "Zapisano", Toast.LENGTH_SHORT).show()
            paymentsViewModel.createNewPayment(paymentsViewModel.payment.value!!, paymentsViewModel.selectedUsers.toList(), object :
                CallBack {
                override fun onSuccess() {
                    clearFields()
                    Toast.makeText(requireContext(), "Dzieki Działa", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(errorMessage: String) {
                    Toast.makeText(requireContext(), "Upss, coś poszło nie tak. Spróbuj ponownie później!", Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            Toast.makeText(requireContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
        }

    }

    private fun clearFields() {
        binding.paymentTitle.text?.clear()
        binding.paymentTitle.clearFocus()
        binding.paymentAmount.text?.clear()
        binding.paymentAmount.clearFocus()

        paymentsViewModel.payment.value?.clear()
        paymentsViewModel.clearSelectedUsers()
        setupRecyclerView()
        hidePaymentsDates()
    }


    private fun buttonBindings() {
        if (paymentsViewModel.selectedUsers.isNotEmpty()) {
            binding.addToSelectedUsers.text = resources.getString(R.string.edit_selected_users)
        } else {
            binding.addToSelectedUsers.text = resources.getString(R.string.add_to_selected_users)
        }
    }

    private fun setupRecyclerView() {
        recyclerView = binding.usersRecyclerView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        if (recyclerView.itemDecorationCount == 0) {
            val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
            recyclerView.addItemDecoration(itemDecoration)
        }
        tenantsAdapter = TenantsAdapter(
            paymentsViewModel.selectedUsers.toList(),
            this,
            paymentsViewModel.userRole
        )
        recyclerView.adapter = tenantsAdapter
        tenantsAdapter.notifyDataSetChanged()
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(resources.getString(R.string.select_payment_dates))
            .build()

        dateRangePicker.show(childFragmentManager, "date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener {
            val startDate = dateRangePicker.selection?.first
            val endDate = dateRangePicker.selection?.second

            if (startDate != null && endDate != null) {

                paymentsViewModel.setPaymentDate(Date(startDate), Date(endDate))

                val formattedStartDate =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Timestamp(startDate))
                val formattedEndDate =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Timestamp(endDate))
                showPaymentsDates(formattedStartDate, formattedEndDate)
            }
        }
    }

    private fun hidePaymentsDates() {
        binding.startPaymentDateLabel.visibility = View.GONE
        binding.endPaymentDateLabel.visibility = View.GONE
        binding.startPaymentDate.visibility = View.GONE
        binding.endPaymentDate.visibility = View.GONE
        binding.calednarButton.text = resources.getString(R.string.select_payment_dates)
    }

    private fun showPaymentsDates(startDate: String, endDate: String) {
        binding.startPaymentDateLabel.visibility = View.VISIBLE
        binding.endPaymentDateLabel.visibility = View.VISIBLE
        binding.startPaymentDate.visibility = View.VISIBLE
        binding.endPaymentDate.visibility = View.VISIBLE

        binding.startPaymentDate.text = startDate
        binding.endPaymentDate.text = endDate
        binding.calednarButton.text = resources.getString(R.string.change_dates)
    }

    override fun onLandlordClick(item: Any, anchorView: View) {
        item as User
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.users_landlord_popup, popupMenu.menu)
        popupMenu.menu.findItem(R.id.delete).isVisible =
            !item.houseRoles!!.containsValue("landlord")
        popupMenu.menu.findItem(R.id.preview).isVisible = false
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    paymentsViewModel.removeSelectedUsers(item)
                    buttonBindings()
                    setupRecyclerView()

                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onTenantClick(item: Any, anchorView: View) {
    }

}