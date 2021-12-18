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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentNewSessionBinding
import kotlinx.coroutines.runBlocking
import java.util.*


class NewSessionFragment : Fragment() {

    private var _binding: FragmentNewSessionBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private var sessionRunning = false

    private lateinit var sessionTime: TextView
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var ends = 0
    private var arrows = 0
    private lateinit var endScores: MutableList<MutableList<Int>>
    private lateinit var endsView: LinearLayout
    private var endCounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        endScores = mutableListOf()

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.scores.observe(requireActivity(), Observer {
            Log.e("OBSERVER", it.toString())
            endScores.add(it)
            updateResults()

            val textView = TextView(context)
            textView.textSize = 20f
            textView.text = ("$endCounter: Shots: ${it.size}, Total score: ${it.sumOf { it1 -> it1 }}\n\t\t\t$it")

            endsView.addView(textView)
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!sessionRunning) {
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun updateResults() {
        endCounter++
        ends = endScores.size
        arrows = endScores.sumOf { it.size }
        binding.endsValue.text = "$ends"
        binding.arrowsValue.text = "$arrows"
        binding.endsScrollView.post {
            binding.endsScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        endsView = binding.endsView

        binding.buttonStart.setOnClickListener {
            sessionStart()
        }
        binding.buttonEnd.setOnClickListener {
            sessionEnd()
        }

        sessionTime = binding.sessionTimeValue

        binding.addEndScores.setOnClickListener {
            AddScoresFragment().show(parentFragmentManager , AddScoresFragment.TAG)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sessionStart() {
        sessionRunning = true
        setView()
        runBlocking { timer() }

    }

    private fun sessionEnd() {
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
                    // Please here set your event date//YYYY-MM-DD
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
                ColorStateList.valueOf(resources.getColor(R.color.inactive));
            binding.buttonStart.isClickable = false
            binding.buttonEnd.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
            binding.buttonEnd.isClickable = true
        } else {
            (activity as MainActivity).findViewById<View>(R.id.fab).visibility = View.VISIBLE
            (activity as MainActivity).findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            binding.addEndScoreLayout.visibility = View.INVISIBLE
            binding.buttonStart.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
            binding.buttonStart.isClickable = true
            binding.buttonEnd.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.inactive));
            binding.buttonEnd.isClickable = false
        }
    }




}