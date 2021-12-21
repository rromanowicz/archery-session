package ex.rr.archerysession

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentHistoryBinding
import ex.rr.archerysession.db.DBHelper
import java.text.SimpleDateFormat
import java.util.*


class HistoryFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val parent = view.findViewById(R.id.historyLayout) as ViewGroup

        val db = DBHelper(requireContext(), null)
        val last10Sessions = db.getXSessions(NO_OF_SESSIONS, "0")

        last10Sessions.forEach {
            val historyItem: View = inflater.inflate(R.layout.history_item, null)
            historyItem.findViewById<TextView>(R.id.idValue).text = it.id.toString()
            historyItem.findViewById<TextView>(R.id.dateValue).text =
                formatter.format(it.session.startDate)
            historyItem.findViewById<TextView>(R.id.endsValue).text = it.session.ends.toString()
            historyItem.findViewById<TextView>(R.id.arrowsValue).text = it.session.arrows.toString()
            historyItem.findViewById<TextView>(R.id.avgArrowValue).text =
                it.session.scores.sumOf { t -> t.sumOf { x -> x } }.div(it.session.arrows)
                    .toString()

            val id = it.id
            historyItem.setOnClickListener {
                SessionDetailsFragment(id).show(childFragmentManager, SessionDetailsFragment.TAG)
            }
            binding.historyScrollView.addView(historyItem)

        }
    }

    companion object {
        const val NO_OF_SESSIONS: String = "10"
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    }
}