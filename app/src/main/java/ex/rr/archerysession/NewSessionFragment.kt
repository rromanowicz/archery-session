package ex.rr.archerysession

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentNewSessionBinding
import ex.rr.archerysession.data.Session
import ex.rr.archerysession.db.DBHelper
import ex.rr.archerysession.file.FileProcessor
import kotlinx.coroutines.runBlocking
import java.util.*

class NewSessionFragment : Fragment() {

    private lateinit var binding: FragmentNewSessionBinding

    private var sessionRunning = false

    private lateinit var sessionTime: TextView

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private lateinit var sharedViewModel: SharedViewModel
    private var session: Session? = null
    private lateinit var endsView: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewSessionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!sessionRunning) {
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        endsView = binding.endsView

        binding.buttonStart.setOnClickListener {
            sessionStart()
        }
        binding.buttonEnd.setOnClickListener {
            ConfirmSessionFragment().show(parentFragmentManager, ConfirmSessionFragment.TAG)
        }

        sessionTime = binding.sessionTimeValue

        binding.addEndScores.setOnClickListener {
            AddScoresFragment().show(parentFragmentManager, AddScoresFragment.TAG)
        }

        sharedViewModel.scores.observe(requireActivity(), {
            Log.d(this::class.java.name, "Received scores from dialog.")
            if (it != null && it.isNotEmpty()) {
                session!!.addEndScores(it)
                updateResults()
            }
        })

        sharedViewModel.endSession.observe(requireActivity(), {
            Log.d(this::class.java.name, "EndSession received from dialog.")
            if (it != null && it) {
                sessionEnd()
            }
        })
    }

    private fun updateResults() {
        binding.endsValue.text = session!!.ends.toString()
        binding.arrowsValue.text = session!!.arrows.toString()
        binding.endsScrollView.post {
            binding.endsScrollView.fullScroll(View.FOCUS_DOWN)
        }

        val lastEnd = session!!.scores[session!!.scores.lastIndex]

        binding.endsText.append("${session!!.ends}: ${getString(R.string.arrows)} ${lastEnd.size}, ${getString(R.string.total_score)} ${lastEnd.sumOf { it1 -> it1 }}\n\t\t\t$lastEnd\n")
    }

    private fun sessionStart() {
        if (session == null) {
            session = Session()
        } else {
            session!!.clear()
        }

        binding.endsText.text = ""
        sessionRunning = true
        setView()
        runBlocking { timer() }

    }

    private fun sessionEnd() {
        session!!.endSession()
        if (session!!.arrows != 0) {
            val db = DBHelper(requireContext(), null)
            val sessionId = db.addSession(session!!)
            Log.d(this::class.java.name, "Saved session with id: $sessionId")
            FileProcessor().saveToFile(sessionId, session!!)
        }
        sharedViewModel.clear()
        sessionRunning = false
        setView()
    }

    private fun timer() {
        val startDate = Date()
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                if (!sessionRunning) return
                handler!!.postDelayed(this, 1000)
                try {
                    val currentDate = Date()
                    var diff = (currentDate.time - startDate.time)
                    val days = diff / (24 * 60 * 60 * 1000)
                    diff -= days * (24 * 60 * 60 * 1000)
                    val hours = diff / (60 * 60 * 1000)
                    diff -= hours * (60 * 60 * 1000)
                    val minutes = diff / (60 * 1000)
                    diff -= minutes * (60 * 1000)
                    val seconds = diff / 1000
                    sessionTime.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        handler!!.postDelayed(runnable as Runnable, (1 * 1000).toLong())
    }

    private fun setView() {
        if (sessionRunning) {
            (activity as MainActivity).findViewById<View>(R.id.fab).visibility = View.GONE
            (activity as MainActivity).findViewById<View>(R.id.toolbar).visibility = View.GONE
            binding.addEndScoreLayout.visibility = View.VISIBLE
            binding.buttonStart.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.inactive, requireContext().theme))
            binding.buttonStart.isClickable = false
            binding.buttonEnd.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, requireContext().theme))
            binding.buttonEnd.isClickable = true
        } else {
            (activity as MainActivity).findViewById<View>(R.id.fab).visibility = View.VISIBLE
            (activity as MainActivity).findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            binding.addEndScoreLayout.visibility = View.INVISIBLE
            binding.buttonStart.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, requireContext().theme))
            binding.buttonStart.isClickable = true
            binding.buttonEnd.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.inactive, requireContext().theme))
            binding.buttonEnd.isClickable = false
        }
    }
}