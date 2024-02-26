package com.msprysak.rentersapp.ui.invoices

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.repositories.room.UserApplication
import com.msprysak.rentersapp.data.repositories.room.UserInfo
import com.msprysak.rentersapp.databinding.FragmentInvoicesPdftemplateBinding
import com.msprysak.rentersapp.interfaces.CallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class InvoicesTemplateFragment: BaseFragment() {

    private val invoicesViewModel: InvoicesViewModel by viewModels {
        InvoicesViewModelFactory((requireActivity().application as UserApplication).roomRepository)
    }

    private var _binding: FragmentInvoicesPdftemplateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoicesPdftemplateBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Assuming roomRepository.userInfo returns a Flow<UserInfo>
        invoicesViewModel.userInfo.observe(viewLifecycleOwner) { user ->
            println("OBSERVER")
            if (user != null) {
                binding.sellerNameSurname.setText(user.userNameSurname)
                binding.sellerNip.setText(user.userNip)
                binding.sellerStreet.setText(user.userStreet)
                binding.sellerPostalCode.setText(user.userPostalCode)
                binding.sellerTown.setText(user.userCity)
            }
        }


        bindData()
    }


    private suspend fun updateUserInfo() {
        val userInfo = UserInfo(
            userNameSurname = binding.sellerNameSurname.text.toString(),
            userNip = binding.sellerNip.text.toString(),
            userStreet = binding.sellerStreet.text.toString(),
            userPostalCode = binding.sellerPostalCode.text.toString(),
            userCity = binding.sellerTown.text.toString()
        )

        println("User info: ${userInfo.userCity}")

        withContext(Dispatchers.IO) {
            invoicesViewModel.updateUserInfo(userInfo)
        }
    }

    private fun bindData() {
        setDecimalFilter(binding.priceNetto)

        binding.issueDate.setOnClickListener {
            showDatePickerDialog(binding.issueDate)
        }

        binding.saleDate.setOnClickListener {
            showDatePickerDialog(binding.saleDate)
        }
        binding.paymentDeadline.setOnClickListener {
            showDatePickerDialog(binding.paymentDeadline)
        }

        binding.saveButton.setOnClickListener {
            generatePdfAWithTemplate(requireContext(), false)
            lifecycleScope.launch {
                updateUserInfo()
            }

        }

        binding.saveDownloadButton.setOnClickListener {
            generatePdfAWithTemplate(requireContext(), true)
            lifecycleScope.launch {
                updateUserInfo()
            }
        }
    }

    private fun showDatePickerDialog(date: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.select_payment_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "date_picker")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = datePicker.selection

            if (selectedDate != null) {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate)
                date.setText(formattedDate)
            }
        }
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
                    input.toDouble()
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

    private fun generatePdfAWithTemplate(context: Context, downloadFile: Boolean) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.invoice_template)

            val destinationDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            println("Destination directory: $destinationDir")

            val dateFormat = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).parse(binding.issueDate.text.toString())
            val formattedDate =
                dateFormat?.let {
                    SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(it).toString()
                }

            val factureName = ("Faktura VAT $formattedDate")
            val filename = factureName.replace("/", "-")
            val outputFile = File(destinationDir, "$filename.pdf")


            val outputStream = FileOutputStream(outputFile)
            val pdfReader = PdfReader(inputStream)

            val stamper = PdfStamper(pdfReader, outputStream)

            val formFields = stamper.acroFields


            formFields.setField("miejsce_wystawienia", binding.issuePlace.text.toString())
            formFields.setField(
                "sprzedawca_imie_nazwisko",
                binding.sellerNameSurname.text.toString()
            )
            formFields.setField("sposob_platnosci", "Przelew")
            formFields.setField("wystawiajacy", binding.sellerNameSurname.text.toString())
            formFields.setField("nazwa_uslugi", binding.serviceName.text.toString())
            formFields.setField("cena_netto", binding.priceNetto.text.toString())
            formFields.setField("uwagi", binding.comments.text.toString())
            formFields.setField("data_wystawienia", binding.issueDate.text.toString())
            formFields.setField("data_sprzedazy", binding.saleDate.text.toString())
            formFields.setField("do_zaplaty", binding.priceNetto.text.toString() + " PLN")
            formFields.setField(
                "do_zaplaty_slownie",
                numberInWords(binding.priceNetto.text.toString().toDouble()) + " PLN"
            )
            formFields.setField("sprzedawca_nip", "NIP: " + binding.sellerNip.text.toString())
            formFields.setField("sprzedawca_ulica", binding.sellerStreet.text.toString())
            formFields.setField("sprzedawca_kod_pocztowy", binding.sellerPostalCode.text.toString())
            formFields.setField("sprzedawca_miejscowosc", binding.sellerTown.text.toString())
            formFields.setField("nabywca_imie_nazwisko", binding.buyerNameSurname.text.toString())
            formFields.setField("nabywca_pesel", "PESEL: " + binding.buyerPesel.text.toString())
            formFields.setField("nabywca_ulica", binding.buyerStreet.text.toString())
            formFields.setField("nabywca_kod_pocztowy", binding.buyerPostalCode.text.toString())
            formFields.setField("nabywca_miejscowosc", binding.buyerTown.text.toString())
            formFields.setField("nazwa_faktury", factureName)
            formFields.setField("wartosc_netto", binding.priceNetto.text.toString())
            formFields.setField("wartosc_brutto", binding.priceNetto.text.toString())
            formFields.setField("wartosc_netto_w_tym", binding.priceNetto.text.toString())
            formFields.setField("wartosc_brutto_w_tym", binding.priceNetto.text.toString())
            formFields.setField("wartosc_brutto_razem", binding.priceNetto.text.toString())
            formFields.setField("wartosc_netto_razem", binding.priceNetto.text.toString())
            stamper.setFormFlattening(true)


            stamper.close()
            outputStream.close()
            pdfReader.close()

            invoicesViewModel.uploadPdfFile(factureName, outputFile.toUri(), object : CallBack {
                override fun onSuccess() {
                    Toast.makeText(context, "Plik został przesłany", Toast.LENGTH_SHORT).show()
                    clearFields()
                }

                override fun onFailure(errorMessage: String) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })

            if (downloadFile && outputFile.exists()) {
                outputFile.delete()
            }

            println("File created: $outputFile")

        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception: $e")
            println("Failed to create file. Reason: $e")

            // Dodaj odpowiednią obsługę błędu, jeśli to konieczne
        }


    }

    private fun clearFields() {
        binding.issuePlace.text?.clear()
        binding.serviceName.text?.clear()
        binding.priceNetto.text?.clear()
        binding.comments.text?.clear()
        binding.issueDate.text?.clear()
        binding.saleDate.text?.clear()
        binding.paymentDeadline.text?.clear()
        binding.buyerNameSurname.text?.clear()
        binding.buyerPesel.text?.clear()
        binding.buyerStreet.text?.clear()
        binding.buyerPostalCode.text?.clear()
        binding.buyerTown.text?.clear()
    }

    private fun numberInWords(number: Double): String {
        if (number == 0.0) return "zero"

        val unity = arrayOf(
            "",
            "jeden",
            "dwa",
            "trzy",
            "cztery",
            "pięć",
            "sześć",
            "siedem",
            "osiem",
            "dziewięć"
        )
        val teens = arrayOf(
            "dziesięć",
            "jedenaście",
            "dwanaście",
            "trzynaście",
            "czternaście",
            "piętnaście",
            "szesnaście",
            "siedemnaście",
            "osiemnaście",
            "dziewiętnaście"
        )
        val douzens = arrayOf(
            "",
            "",
            "dwadzieścia",
            "trzydzieści",
            "czterdzieści",
            "pięćdziesiąt",
            "sześćdziesiąt",
            "siedemdziesiąt",
            "osiemdziesiąt",
            "dziewięćdziesiąt"
        )
        val hundreds = arrayOf(
            "",
            "sto",
            "dwieście",
            "trzysta",
            "czterysta",
            "pięćset",
            "sześćset",
            "siedemset",
            "osiemset",
            "dziewięćset"
        )

        val totalPart = number.toInt()
        val fractionalPart = ((number - totalPart) * 100).toInt()

        var numberInWords = ""
        var rest = totalPart

        // tysiące
        val tysiace = rest / 1_000
        if (tysiace > 0) {
            numberInWords += "${numberInWords(tysiace.toDouble())} tysięcy "
            rest %= 1_000
        }

        // setki
        val hundredsIndex = rest / 100
        if (hundredsIndex > 0) {
            numberInWords += "${hundreds[hundredsIndex]} "
            rest %= 100
        }

        // dziesiątki i jedności
        val douzensIndex = rest / 10
        if (douzensIndex >= 2) {
            numberInWords += "${douzens[douzensIndex]} "
            rest %= 10
        } else if (douzensIndex == 1) {
            numberInWords += "${teens[rest]} "
            rest = 0
        }

        // jedności
        if (rest > 0) {
            numberInWords += "${unity[rest]} "
        }

        if (fractionalPart > 0) {
            numberInWords += "i $fractionalPart/100"
        }

        return numberInWords.trim()
    }


}