package ltd.evilcorp.atox.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.profile_fragment.view.*
import ltd.evilcorp.atox.R
import ltd.evilcorp.atox.activity.ContactListActivity
import ltd.evilcorp.atox.di.ViewModelFactory
import javax.inject.Inject

class ProfileFragment : Fragment() {
    companion object {
        fun newInstance() = ProfileFragment()
    }

    @Inject
    lateinit var vmFactory: ViewModelFactory
    private lateinit var viewModel: ProfileViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_fragment, container, false).apply {
            btnCreate.setOnClickListener {
                btnCreate.isEnabled = false

                viewModel.startToxThread()
                viewModel.setName(if (username.text.isNotEmpty()) username.text.toString() else "aTox user")
                viewModel.setPassword(if (password.text.isNotEmpty()) password.text.toString() else "")

                startActivity(Intent(requireContext(), ContactListActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory).get(ProfileViewModel::class.java)

        viewModel.tryLoadToxSave()?.also { save ->
            viewModel.startToxThread(save)
            startActivity(Intent(requireContext(), ContactListActivity::class.java))
            requireActivity().finish()
        }
    }
}