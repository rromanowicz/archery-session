package ex.rr.archerysession

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentSessionDetailsBinding
import ex.rr.archerysession.db.DBHelper


class SessionDetailsFragment(sessionId: Long? = null) : DialogFragment() {

    private var sessionId: Long? = null

    init {
        this.sessionId = sessionId
    }

    companion object {
        const val TAG = "SessionDetailsFragment"
    }

    private lateinit var binding: FragmentSessionDetailsBinding

    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSessionDetailsBinding.inflate(layoutInflater)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setGravity(Gravity.TOP)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        if (sessionId != null) {
            fillSessionDetails()
            Log.d(this::class.java.name, "Loaded session details.")
        }

    }

    private fun fillSessionDetails() {
        val db = DBHelper(requireContext(), null)
        val lastSession = db.getSessionById(sessionId.toString())[0]

        binding.fabClose.visibility = View.VISIBLE
        binding.fabClose.setOnClickListener {
            dismiss()
        }

        binding.sessionId.text = lastSession.id.toString()
        binding.startDate.text =
            MainFragment.formatter.format(lastSession.session.startDate)
        binding.endDate.text =
            MainFragment.formatter.format(lastSession.session.endDate!!)
        binding.endCount.text =
            lastSession.session.ends.toString()
        binding.arrowCount.text =
            lastSession.session.arrows.toString()
        binding.avgArrowValue.text =
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
                resources.getDimension(R.dimen.dialogTexTSize)
            )
            textView.text =
                ("${++i}: [${if (endSum.toString().length == 1) "0$endSum" else "$endSum"}] \t$formattedScores")
            binding.mainScrollView.addView(textView)
        }
    }

}