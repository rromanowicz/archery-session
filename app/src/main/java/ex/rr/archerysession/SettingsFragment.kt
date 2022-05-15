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

    private lateinit var viewModel: SharedViewModel

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

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        updateBowSpinner()
        updateDistanceSpinner()

        binding.addBow.setOnClickListener {
            AddBowFragment().show(parentFragmentManager, AddBowFragment.TAG)
        }

        binding.editBow.setOnClickListener {
            AddBowFragment(binding.bowList.selectedItem.toString()).show(
                childFragmentManager,
                AddBowFragment.TAG
            )
        }

        binding.addDistance.setOnClickListener {
            val distance = binding.targetDistance.text.toString()
            if (distance.isNotEmpty()) {
                val db = DBHelper(requireContext(), null)
                db.addTargetDistance(distance)
                binding.targetDistance.setText("")
                binding.targetDistance.clearFocus()
                updateDistanceSpinner()
                makeToast(getString(R.string.target_distance_added))
            }
        }

        binding.removeDistance.setOnClickListener {
            val db = DBHelper(requireContext(), null)
            db.removeTargetDistance(binding.targetDistanceList.selectedItem.toString())
            binding.targetDistance.setText("")
            binding.targetDistance.clearFocus()
            updateDistanceSpinner()
            makeToast(getString(R.string.target_distance_removed))
        }

        viewModel.newBow.observe(requireActivity()) {
            var msg = ""
            Log.d(this::class.java.name, "Received new bow from dialog.")
            makeToast(getString(R.string.bow_added))
        }

        viewModel.reloadBows.observe(requireActivity()) {
            Log.d(this::class.java.name, "Setting bow refresh flag.")
            if (it != null && it) {
                updateBowSpinner()
                viewModel.setReloadBows()
            }
        }
    }

    private fun makeToast(msg: String) {
        Toast.makeText(
            requireContext(),
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateBowSpinner() {
        var items = arrayListOf(getString(R.string.nothing_available))

        val db = DBHelper(requireContext(), null)
        val bows = db.getBows()
        if (bows.isNotEmpty()) {
            items = bows.mapTo(arrayListOf()) { it.name }
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bowList.adapter = adapter
    }

    private fun updateDistanceSpinner() {
        var items = arrayListOf(getString(R.string.nothing_available))

        val db = DBHelper(requireContext(), null)
        val targetDistanceList = db.getTargetDistanceList()
        if (targetDistanceList.isNotEmpty()) {
            items = ArrayList(targetDistanceList)
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.targetDistanceList.adapter = adapter
    }


}