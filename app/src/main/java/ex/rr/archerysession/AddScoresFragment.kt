package ex.rr.archerysession

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
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
        scores = mutableListOf()
        updateScore()
        return inflater.inflate(R.layout.fragment_add_scores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
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
        view.findViewById<Button>(R.id.submitButton).setOnClickListener {
            if(scores.isNotEmpty()) {
                viewModel.sendScores(scores)
            }
            dismiss()
        }

        view.findViewById<Button>(R.id.scoreButton_0).setOnClickListener {
            addScore(0)
        }

        view.findViewById<Button>(R.id.scoreButton_6).setOnClickListener {
            addScore(6)
        }

        view.findViewById<Button>(R.id.scoreButton_7).setOnClickListener {
            addScore(7)
        }

        view.findViewById<Button>(R.id.scoreButton_8).setOnClickListener {
            addScore(8)
        }

        view.findViewById<Button>(R.id.scoreButton_9).setOnClickListener {
            addScore(9)
        }

        view.findViewById<Button>(R.id.scoreButton_10).setOnClickListener {
            addScore(10)
        }

        view.findViewById<ImageButton>(R.id.scoreButton_delete).setOnClickListener {
            removeScore()
        }
    }
    private fun addScore(score: Int){
        scores.add(score)
        updateScore()
    }

    private fun removeScore() {
        scores.removeLast()
        updateScore()
    }

    private fun updateScore() {
        totalScore = scores.sumOf { it }
        shots = scores.size
        outputTextView = scores.toString()
        view?.findViewById<TextView>(R.id.scoresText1)?.text = ("Shots: [$shots] Score: [$totalScore]")
        view?.findViewById<TextView>(R.id.scoresText2)?.text = ("$scores")

    }

}