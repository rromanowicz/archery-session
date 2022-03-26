package ex.rr.archerysession

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentSettingsBinding
import ex.rr.archerysession.db.DBHelper

class SettingsFragment : Fragment() {

    companion object {
        const val TAG = "SettingsFragment"
    }

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)
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

        updateSpinner()

        binding.addBow.setOnClickListener {
            AddBowFragment().show(parentFragmentManager, AddBowFragment.TAG)
        }

        binding.removeBow.setOnClickListener {
            val db = DBHelper(requireContext(), null)
            db.removeBow(binding.bowList.selectedItem.toString())
            updateSpinner()
        }


        sharedViewModel.newBow.observe(requireActivity()) {
            var msg = ""
            Log.d(this::class.java.name, "Received new bow from dialog.")
            if (it != null && it == 0) {
                msg = getString(R.string.bow_added)
                updateSpinner()
            } else if (it==1) {
                msg = getString(R.string.bow_already_exists)
            } else {
                msg = getString(R.string.unexpected_error)
            }

            Toast.makeText(
                requireContext(),
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun updateSpinner() {
        var items = arrayListOf("Nothing available")

        val db = DBHelper(requireContext(), null)
        var bows = db.getSettings()?.bows
        if (bows != null && bows.isNotEmpty()) {
            items = bows.mapTo(arrayListOf()) { it.name }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bowList.adapter = adapter
    }

}