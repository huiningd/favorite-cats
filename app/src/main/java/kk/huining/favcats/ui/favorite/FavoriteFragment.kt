package kk.huining.favcats.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kk.huining.favcats.R
import kk.huining.favcats.di.viewmodel.ViewModelFactory
import kk.huining.favcats.ui.common.BaseFragment
import javax.inject.Inject

class FavoriteFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Creates an instance of Presentation component and injects this fragment to the Component.
        // It is advised to call inject(this) before super.onCreate().
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchImages()
    }
}