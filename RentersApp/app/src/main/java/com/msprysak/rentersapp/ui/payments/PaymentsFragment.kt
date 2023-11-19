package com.msprysak.rentersapp.ui.payments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.databinding.FragmentPaymentsAdminBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class PaymentsFragment : BaseFragment() {

    private var _binding: FragmentPaymentsAdminBinding? = null
    private val binding get() = _binding!!

    private val paymentsViewModel by viewModels<PaymentsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsAdminBinding.inflate(inflater, container, false)
        return _binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locale = Locale("pl", "PL")
        Locale.setDefault(locale)
        paymentsViewModel.fetchUsers()

        paymentsViewModel.usersListData.observe(viewLifecycleOwner) { usersList ->
            println(usersList)
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        binding.calednarButton.setOnClickListener {
            showDateRangePicker()
        }
    }


    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Wybierz daty płatności")
            .build()

        dateRangePicker.show(childFragmentManager, "date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener {
            val startDate = dateRangePicker.selection?.first
            val endDate = dateRangePicker.selection?.second

            if (startDate != null && endDate != null) {
                val formattedStartDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(startDate))
                val formattedEndDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(endDate))
                showPaymentsDates(formattedStartDate, formattedEndDate)
            }
        }
    }

    private fun showPaymentsDates(startDate: String, endDate: String) {
        binding.startPaymentDateLabel.visibility = View.VISIBLE
        binding.endPaymentDateLabel.visibility = View.VISIBLE
        binding.startPaymentDate.visibility = View.VISIBLE
        binding.endPaymentDate.visibility = View.VISIBLE

        binding.startPaymentDate.text = startDate
        binding.endPaymentDate.text = endDate
        binding.calednarButton.text = "Edyuj daty płatności"
    }

}