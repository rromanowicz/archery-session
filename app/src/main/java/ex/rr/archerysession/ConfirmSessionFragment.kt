package ex.rr.archerysession

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R

class ConfirmSessionFragment : DialogFragment() {

    companion object {
        const val TAG = "ConfirmSessionFragment"
    }

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_confirm_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {

        view.findViewById<Button>(R.id.end_cancel).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.end_confirm).setOnClickListener {
            viewModel.sendEndSession()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}