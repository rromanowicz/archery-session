package ex.rr.archerysession

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
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

        childFragmentManager.commit {
            childFragmentManager.fragments.forEach { remove(it) }
            val sessionDetails = SessionDetailsFragment(getLastSessionId(), true)
            add(R.id.mainLinearLayout, sessionDetails)
            setReorderingAllowed(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    }

    private fun getLastSessionId(): Long? {
        val db = DBHelper(requireContext(), null)
        val lastSessionId = db.getLastSessionId()
        db.close()
        return lastSessionId
    }
}