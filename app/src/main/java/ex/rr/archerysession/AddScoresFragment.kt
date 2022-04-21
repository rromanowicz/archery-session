package ex.rr.archerysession

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R


class AddScoresFragment : DialogFragment() {

    private lateinit var scores: MutableList<Int>
    private var shots = 0
    private var totalScore = 0
    private var outputTextView = ""

    companion object {
        const val TAG = "AddScoresFragment"
    }

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_scores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        scores = mutableListOf()
        updateScore()
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners(view: View) {
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            submitButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (scores.isNotEmpty()) {
                scores.sortDescending()
                viewModel.sendScores(scores)
            }
            dismiss()
        }

        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_0), ::addScore, 0)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_5), ::addScore, 5)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_6), ::addScore, 6)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_7), ::addScore, 7)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_8), ::addScore, 8)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_9), ::addScore, 9)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_10), ::addScore, 10)
        addOnClickListener(view.findViewById<Button>(R.id.scoreButton_delete), ::removeScore)
    }

    private fun addScore(score: Int) {
        scores.add(score)
        updateScore()
    }

    private fun removeScore(dummy: Int = 0) {
        scores.removeLast()
        updateScore()
    }

    private fun updateScore() {
        totalScore = scores.sumOf { it }
        shots = scores.size
        outputTextView = scores.toString()
        view?.findViewById<TextView>(R.id.scoresText1)?.text =
            ("${resources.getString(R.string.arrows)} [$shots] ${resources.getString(R.string.score)} [$totalScore]")

        view?.findViewById<TextView>(R.id.scoresText2)?.text = ("$scores")
    }

    private fun addOnClickListener(view: View, func: (score: Int) -> Unit, value: Int = 0) {
        view.setOnClickListener {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            func(value)
        }
    }

}