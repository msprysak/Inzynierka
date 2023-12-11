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
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.databinding.FragmentInvoicesPdftemplateBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class InvoicesTemplateFragment: BaseFragment() {

    private val invoicesViewModel by viewModels<InvoicesViewModel>()

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

        bindData()

        println("Invoice: ")
        println(invoicesViewModel.invoice.value?.nettoPrice)
        println(invoicesViewModel.invoice.value?.sellerName)

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
            generatePdfAWithTemplate(requireContext())
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

    private fun generatePdfAWithTemplate(context: Context) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.invoice_template)

            val destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            println("Destination directory: $destinationDir")

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(binding.issueDate.text.toString())
            val formattedDate =
                dateFormat?.let { SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(it).toString() }

            val filename = ("Faktura VAT ${formattedDate}")
            val outputFile = File(destinationDir, "$filename.pdf")

            if (outputFile.createNewFile() || outputFile.exists()) {
                println("File is created or already exists.")

                val outputStream = FileOutputStream(outputFile)
                val pdfReader = PdfReader(inputStream)

                val stamper = PdfStamper(pdfReader, outputStream)

                val formFields = stamper.acroFields


                formFields.setField("miejsce_wystawienia", binding.issuePlace.text.toString())
                formFields.setField("sprzedawca_imie_nazwisko", binding.sellerNameSurname.text.toString())
                formFields.setField("sposob_platnosci", "Przelew")
                formFields.setField("wystawiajacy", binding.sellerNameSurname.text.toString())
                formFields.setField("nazwa_uslugi", binding.serviceName.text.toString())
                formFields.setField("cena_netto", binding.priceNetto.text.toString())
                formFields.setField("uwagi", binding.comments.text.toString())
                formFields.setField("data_wystawienia", binding.issueDate.text.toString())
                formFields.setField("data_sprzedazy", binding.saleDate.text.toString())
                formFields.setField("do_zaplaty", binding.priceNetto.text.toString() + " PLN")
                formFields.setField("do_zaplaty_slownie", numberInWords(binding.priceNetto.text.toString().toDouble()) + " PLN")
                formFields.setField("sprzedawca_nip", "NIP: " + binding.sellerNip.text.toString())
                formFields.setField("sprzedawca_ulica", binding.sellerStreet.text.toString())
                formFields.setField("sprzedawca_kod_pocztowy", binding.sellerPostalCode.text.toString())
                formFields.setField("sprzedawca_miejscowosc", binding.sellerTown.text.toString())
                formFields.setField("nabywca_imie_nazwisko", binding.buyerNameSurname.text.toString())
                formFields.setField("nabywca_pesel", "PESEL: " + binding.buyerPesel.text.toString())
                formFields.setField("nabywca_ulica", binding.buyerStreet.text.toString())
                formFields.setField("nabywca_kod_pocztowy", binding.buyerPostalCode.text.toString())
                formFields.setField("nabywca_miejscowosc", binding.buyerTown.text.toString())
                formFields.setField("nazwa_faktury", filename)
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
                println("File created: $outputFile")
            } else {
                println("Failed to create file.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Exception: $e")
            println("Failed to create file. Reason: $e")

            // Dodaj odpowiednią obsługę błędu, jeśli to konieczne
        }


    }
    private fun numberInWords(number: Double): String {
        if (number == 0.0) return "zero"

        val unity = arrayOf("", "jeden", "dwa", "trzy", "cztery", "pięć", "sześć", "siedem", "osiem", "dziewięć")
        val teens = arrayOf("dziesięć", "jedenaście", "dwanaście", "trzynaście", "czternaście", "piętnaście", "szesnaście", "siedemnaście", "osiemnaście", "dziewiętnaście")
        val douzens = arrayOf("", "", "dwadzieścia", "trzydzieści", "czterdzieści", "pięćdziesiąt", "sześćdziesiąt", "siedemdziesiąt", "osiemdziesiąt", "dziewięćdziesiąt")
        val hundreds = arrayOf("", "sto", "dwieście", "trzysta", "czterysta", "pięćset", "sześćset", "siedemset", "osiemset", "dziewięćset")

        val totalPart = number.toInt()
        val fractionalPart = ((number - totalPart) * 100).toInt()

        var numberInWords = ""
        var rest = totalPart


        // tysiące
        val tysiace = rest / 1_000
        if (tysiace > 0) {
            numberInWords += numberInWords(tysiace.toDouble()) + " tysięcy "
            rest %= 1_000
        }

        // setki
        val hundredsIndex = rest / 100
        if (hundredsIndex > 0) {
            numberInWords += hundreds[hundredsIndex.toInt()] + " "
            rest %= 100
        }

        // dziesiątki i jedności
        val douzensIndex = rest / 10
        if (douzensIndex >= 2) {
            numberInWords += douzens[douzensIndex.toInt()] + " "
            rest %= 10
        } else if (douzensIndex == 1) {
            numberInWords += teens[rest.toInt()] + " "
            rest = 0
        }

        // jedności
        val unityIndex = rest.toInt()
        if (unityIndex > 0) {
            numberInWords += unity[unityIndex] + " "
        }

        if (fractionalPart > 0) {
            numberInWords += "i $fractionalPart/100"
        }

        return numberInWords.trim()
    }


}