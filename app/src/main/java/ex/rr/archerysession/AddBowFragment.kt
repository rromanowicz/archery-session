package ex.rr.archerysession

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rr.archerysession.R
import com.rr.archerysession.databinding.FragmentAddBowBinding
import com.rr.archerysession.databinding.FragmentSessionDetailsBinding
import ex.rr.archerysession.data.Bow
import ex.rr.archerysession.db.DBHelper
import ex.rr.archerysession.file.SessionFileProcessor


class AddBowFragment(bowName: String? = null) : DialogFragment() {

    companion object {
        const val TAG = "AddBowFragment"
    }

    private var bowName: String? = null

    init {
        this.bowName = bowName
    }

    private lateinit var binding: FragmentAddBowBinding

    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddBowBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        if (!bowName.isNullOrBlank()) {
            binding.bowHeaderLabel.text = ""
            binding.bowIdRow.visibility = View.VISIBLE

            val db = DBHelper(requireContext(), null)
            val bow = db.getBowByName(bowName!!)

            binding.bowId.text = bow!!.id.toString()
            binding.bowName.setText(bow.name)
            binding.bowDescription.setText(bow.description)
            binding.bowLength.setText(bow.length.toString())
            binding.bowBh.setText(bow.braceHeight.toString())
            binding.bowTopTiller.setText(bow.topTiller.toString())
            binding.bowBottomTiller.setText(bow.bottomTiller.toString())

            binding.fabDelete.visibility = View.VISIBLE
            binding.fabDelete.setOnClickListener {
                try {
                    db.removeBow(bow.id)
                    viewModel.setReloadBows()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.bow_removed),
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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
            val bowId = view.findViewById<TextView>(R.id.bowId)?.text.toString()
            val bowName = view.findViewById<TextView>(R.id.bowName)?.text.toString()
            val bowDescription = view.findViewById<TextView>(R.id.bowDescription)?.text.toString()
            val bowLength = view.findViewById<TextView>(R.id.bowLength).text.toString()
            val bowBh = view.findViewById<TextView>(R.id.bowBh).text.toString()
            val bowTopTiller = view.findViewById<TextView>(R.id.bowTopTiller).text.toString()
            val bowBottomTiller = view.findViewById<TextView>(R.id.bowBottomTiller).text.toString()

            submitButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val db = DBHelper(requireContext(), null)
            if (bowName.isNotEmpty()) {
                viewModel.sendNewBow(
                    db.addBow(
                        Bow(
                            if (bowId.isEmpty()) {
                                0L
                            } else {
                                bowId.toLong()
                            },
                            bowName, bowDescription,
                            bowLength.toIntOrNull(),
                            bowBh.toLongOrNull(),
                            bowTopTiller.toLongOrNull(),
                            bowBottomTiller.toLongOrNull()
                        )
                    )
                )
                viewModel.setReloadBows()
            }
            dismiss()
        }
    }

}