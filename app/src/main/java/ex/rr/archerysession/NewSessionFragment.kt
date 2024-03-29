package ex.rr.archerysession

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentNewSessionBinding
import ex.rr.archerysession.data.EndScores
import ex.rr.archerysession.data.Session
import ex.rr.archerysession.data.Target
import ex.rr.archerysession.db.DBHelper
import ex.rr.archerysession.file.SessionFileProcessor
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
            AddScoresFragment(session!!).show(parentFragmentManager, AddScoresFragment.TAG)
        }

        setView()
        updateBowSpinner()
        updateTargetSpinner()
        updateDistanceSpinner()
    }

    private fun updateResults(end: EndScores) {
        if(!session!!.scoreMap.containsKey(end.id)){
            addEnd(end)
        } else {
            updateEnd(end)
        }

        binding.endsValue.text = session!!.ends.toString()
        binding.arrowsValue.text = session!!.getTotalArrows().toString()
        binding.endsScrollView.post {
            binding.endsScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun updateEnd(end: EndScores) {
        session!!.addEndScores(end)
        val textView = (activity as MainActivity).findViewById<TextView>(end.id!!)
        textView.text = getEndText(end.endScores!!)
    }

    @SuppressLint("SetTextI18n")
    private fun addEnd(end: EndScores) {
        session!!.addEndScores(end)
        val textView = TextView(requireContext())
        textView.id = end.id!!
        textView.text = getEndText(end.endScores!!)

        textView.setOnClickListener {
            AddScoresFragment(session!!, end.endScores, end.id).show(parentFragmentManager, AddScoresFragment.TAG)
        }
        endsView.addView(textView)
    }

    private fun getEndText(lastEnd: MutableList<Int>) =
        "${session!!.ends}: ${activity?.getString(R.string.arrows)} ${lastEnd.size}, ${
            activity?.getString(
                R.string.total_score
            )
        } ${lastEnd.sumOf { it1 -> it1 }}\n\t\t\t$lastEnd\n"

    private fun sessionStart() {
        if (session == null) {
            session = Session()
        } else {
            session!!.clear()
        }

        session!!.bow = DBHelper(
            requireContext(),
            null
        ).getBowByName(binding.bowSelector.selectedItem.toString())

        session!!.target = binding.targetSelector.selectedItem.toString()

        session!!.distance = binding.distanceSelector.selectedItem.toString()

        sharedViewModel.sendTargetScores(Target.fromDesc(session!!.target!!)!!.pointRange)

        binding.endsText.text = ""
        sessionRunning = true
        setView()
        runBlocking { timer() }

        sharedViewModel.scores.observe(requireActivity()) {
            Log.d(this::class.java.name, "Received scores from dialog.")
            if (it != null && it.endScores!!.isNotEmpty()) {
                updateResults(it)
            }
        }

        sharedViewModel.endSession.observe(requireActivity()) {
            Log.d(this::class.java.name, "EndSession received from dialog.")
            if (it != null && it) {
                sessionEnd()
            }
        }

    }

    private fun sessionEnd() {
        session!!.endSession()
        if (session!!.arrows != 0) {
            val db = DBHelper(requireContext(), null)
            val sessionId = db.addSession(session!!)
            Log.d(
                this::class.java.name,
                "Saved session with id: $sessionId, ${session!!.getJSON()}"
            )
            SessionFileProcessor().saveToFile(sessionId, session!!)
        }

        sharedViewModel.scores.removeObservers(requireActivity())
        sharedViewModel.endSession.removeObservers(requireActivity())

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
            binding.bowSelector.visibility = View.GONE
            binding.targetSelector.visibility = View.GONE
            binding.distanceSelector.visibility = View.GONE
            binding.addEndScoreLayout.visibility = View.VISIBLE
            binding.buttonStart.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.inactive, requireContext().theme))
            binding.buttonStart.isClickable = false
            binding.buttonEnd.backgroundTintList =
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.cancel,
                        requireContext().theme
                    )
                )
            binding.buttonEnd.isClickable = true
        } else {
            (activity as MainActivity).findViewById<View>(R.id.fab).visibility = View.VISIBLE
            (activity as MainActivity).findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            binding.bowSelector.visibility = View.VISIBLE
            binding.targetSelector.visibility = View.VISIBLE
            binding.distanceSelector.visibility = View.VISIBLE
            binding.addEndScoreLayout.visibility = View.INVISIBLE
            binding.buttonStart.backgroundTintList =
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.confirm,
                        requireContext().theme
                    )
                )
            binding.buttonStart.isClickable = true
            binding.buttonEnd.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.inactive, requireContext().theme))
            binding.buttonEnd.isClickable = false
        }
    }

    private fun updateBowSpinner() {
        var items = arrayListOf(getString(R.string.no_bows_created))

        val db = DBHelper(requireContext(), null)
        val bows = db.getBows()
        if (bows.isNotEmpty()) {
            items = bows.mapTo(arrayListOf()) { it.name }
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bowSelector.adapter = adapter
    }

    private fun updateTargetSpinner() {
        val items = Target.values().map(Target::description)

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.targetSelector.adapter = adapter
    }

    private fun updateDistanceSpinner() {
        val items = arrayListOf(getString(R.string.nothing_available))

        val db = DBHelper(requireContext(), null)
        val targetDistanceList = db.getTargetDistanceList()
        if (targetDistanceList.isNotEmpty()) {
            items.clear()
            targetDistanceList.forEach { items.add(it + "m") }
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.distanceSelector.adapter = adapter
    }
}