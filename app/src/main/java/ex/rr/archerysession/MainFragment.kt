package ex.rr.archerysession

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentMainBinding
import ex.rr.archerysession.db.DBHelper
import java.text.SimpleDateFormat

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")

        val db = DBHelper(requireContext(), null)
        val lastSession = db.getXSessions("1")[0]

        binding.sessionId.text = lastSession.id.toString()
        binding.startDate.text = formatter.format(lastSession.session.startDate)
        binding.endDate.text = formatter.format(lastSession.session.endDate!!)
        binding.endCount.text = lastSession.session.ends.toString()
        binding.arrowCount.text = lastSession.session.arrows.toString()
        var i = 0
        lastSession.session.scores.forEach { score ->
            val formattedScores: MutableList<String> = mutableListOf()
            val endSum = score.sumOf { it }
            for (s in score) {
                formattedScores.add(if (s.toString().length == 1) " \t$s" else "$s")
            }
            val textView = TextView(context)
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.mainTexTSize)
            )
            textView.text =
                ("${++i}: [${if (endSum.toString().length == 1) "0$endSum" else "$endSum"}] \t$formattedScores")
            binding.mainScrollView.addView(textView)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}