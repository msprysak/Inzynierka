
import android.icu.text.SimpleDateFormat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.databinding.ItemReportBinding
import java.util.Date
import java.util.Locale

class ReportItem(private val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindReportsRecyclerView(report: Reports) {
        binding.reportTitle.text = report.reportTitle
        binding.reportDescription.text = report.reportDescription
        binding.reportDate.text = formatReportDate(report.reportDate!!.toDate())
        if (report.reportImages.isNotEmpty()){
            Glide.with(binding.root)
                .load(report.reportImages.first())
                .circleCrop()
                .placeholder(R.drawable.ic_report)
                .into(binding.reportImage)
        }
    }

    private fun formatReportDate(date: Date): String {
        val javaUtilDate = Date(date.time)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(javaUtilDate)
    }
}
