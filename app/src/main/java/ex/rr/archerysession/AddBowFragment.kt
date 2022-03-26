package ex.rr.archerysession

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import ex.rr.archerysession.data.Bow
import ex.rr.archerysession.db.DBHelper


class AddBowFragment : DialogFragment() {

    companion object {
        const val TAG = "AddBowFragment"
    }

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_bow, container, false)
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
        val submitButton = view.findViewById<Button>(R.id.submitBowButton)
        submitButton.setOnClickListener {
            val bowName = view.findViewById<TextView>(R.id.bowName)?.text.toString()
            val bowDescription = view.findViewById<TextView>(R.id.bowDescription)?.text.toString()
            submitButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (!bowName.isEmpty()) {
                val db = DBHelper(requireContext(), null)
                viewModel.sendNewBow(db.addBow(Bow(0L, bowName, bowDescription)))
            }
            dismiss()
        }
    }

}