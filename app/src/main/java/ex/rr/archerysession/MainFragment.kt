package ex.rr.archerysession

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentMainBinding
import ex.rr.archerysession.db.DBHelper
import java.text.SimpleDateFormat
import java.util.*

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

        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val db = DBHelper(requireContext(), null)
        val lastSession = db.getXSessions("1")[0]

        val sessionDetails: View = inflater.inflate(R.layout.session_details, null)
        sessionDetails.findViewById<TextView>(R.id.sessionId).text = lastSession.id.toString()
        sessionDetails.findViewById<TextView>(R.id.startDate).text =
            formatter.format(lastSession.session.startDate)
        sessionDetails.findViewById<TextView>(R.id.endDate).text =
            formatter.format(lastSession.session.endDate!!)
        sessionDetails.findViewById<TextView>(R.id.endCount).text =
            lastSession.session.ends.toString()
        sessionDetails.findViewById<TextView>(R.id.arrowCount).text =
            lastSession.session.arrows.toString()
        sessionDetails.findViewById<TextView>(R.id.avgArrowValue).text =
            lastSession.session.scores.sumOf { t -> t.sumOf { x -> x } }
                .div(lastSession.session.arrows)
                .toString()

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
            sessionDetails.findViewById<LinearLayout>(R.id.mainScrollView).addView(textView)
        }
        binding.mainLinearLayout.addView(sessionDetails)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    }
}