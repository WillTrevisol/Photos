package com.trevisol.photos.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trevisol.photos.databinding.FragmentPhotosBinding
import com.trevisol.photos.domain.entities.Photo
import com.trevisol.photos.view.adapters.PhotosAdapter
import com.trevisol.photos.viewmodel.PhotosViewModel
import kotlinx.coroutines.launch

class PhotosFragment: Fragment() {

    private lateinit var fragmentPhotosBinding: FragmentPhotosBinding
    private val viewModel: PhotosViewModel by viewModels {
        PhotosViewModel.photosViewFactory(this.requireContext())
    }
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPhotosBinding = FragmentPhotosBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photos.collect {
                    when (it) {
                        is PhotosViewModel.PhotoState.Error -> {
                            fragmentPhotosBinding.loadingIndicator.visibility = View.GONE
                        }
                        PhotosViewModel.PhotoState.Initial -> {
                            fragmentPhotosBinding.loadingIndicator.visibility = View.GONE
                        }
                        PhotosViewModel.PhotoState.Loading -> {
                            fragmentPhotosBinding.loadingIndicator.visibility = View.VISIBLE
                        }
                        is PhotosViewModel.PhotoState.Success -> {
                            fragmentPhotosBinding.loadingIndicator.visibility = View.GONE
                            setupSpinnerRv(it.photos)
                        }
                    }
                }
            }
        }

        return fragmentPhotosBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.retrievePhotos()
    }

    private fun setupSpinnerRv(photos: List<Photo>) {
        photosAdapter = PhotosAdapter(this.requireContext(), photos)
        fragmentPhotosBinding.photosSpinner.apply {
            adapter = photosAdapter
        }
    }

}