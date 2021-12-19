package ex.rr.archerysession

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.rr.archerysession.databinding.FragmentMainBinding
import ex.rr.archerysession.data.Session
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

        try {
            val db = DBHelper(requireContext(), null)
            val lastSessionCursor = db.getXSessions("1")
            if (lastSessionCursor != null) {
                lastSessionCursor.moveToNext()
                val idIndex = lastSessionCursor.getColumnIndex(DBHelper.ID_COL)
                val id = lastSessionCursor.getLong(idIndex)
                val jsonIndex = lastSessionCursor.getColumnIndex(DBHelper.SESSION_COl)
                val sessionJson = lastSessionCursor.getString(jsonIndex)
                val session = Gson().fromJson(sessionJson, Session::class.java)
                binding.sessionId.text = id.toString()
                binding.startDate.text = formatter.format(session.startDate)
                binding.endDate.text = formatter.format(session.endDate!!)
                binding.endCount.text = session.ends.toString()
                binding.arrowCount.text = session.arrows.toString()
                var i: Int = 0
                session.scores.forEach { score ->
                    val textView = TextView(context)
                    textView.textSize = 20f
                    textView.text = ("${++i}: $score")
                    binding.mainScrollView.addView(textView)
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}