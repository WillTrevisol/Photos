package com.trevisol.photos.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
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
    private lateinit var photos: List<Photo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPhotosBinding = FragmentPhotosBinding.inflate(inflater, container, false)

        fragmentPhotosBinding.photosSpinner.apply {
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val photo = photos[position]
                    viewModel.retrievePhoto(photo.url)
                    viewModel.retrieveThumbnail(photo.thumbnailUrl)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photos.collect {
                    when (it) {
                        is PhotosViewModel.PhotoState.Error -> {
                            updateImageViewsVisibilityToGone()
                            updateLoadingVisibility(View.GONE)
                            Toast.makeText(this@PhotosFragment.requireContext() , it.resId, Toast.LENGTH_SHORT).show()
                        }
                        PhotosViewModel.PhotoState.Initial -> {
                            updateImageViewsVisibilityToGone()
                            updateLoadingVisibility(View.GONE)
                        }
                        PhotosViewModel.PhotoState.Loading -> {
                            updateImageViewsVisibilityToGone()
                            updateLoadingVisibility(View.VISIBLE)
                        }
                        is PhotosViewModel.PhotoState.Success -> {
                            updateLoadingVisibility(View.GONE)
                            photos = it.photos
                            setupSpinnerRv(it.photos)
                        }
                        is PhotosViewModel.PhotoState.PhotoSuccess -> {
                            fragmentPhotosBinding.apply {
                                photoTextView.visibility = View.VISIBLE
                                photoImageView.setImageBitmap(it.bitmap)
                                photoImageView.visibility = View.VISIBLE
                                updateLoadingVisibility(View.GONE)
                            }
                        }
                        is PhotosViewModel.PhotoState.ThumbnailSuccess -> {
                            fragmentPhotosBinding.apply {
                                thumbnailTextView.visibility = View.VISIBLE
                                thumbnailImageView.setImageBitmap(it.bitmap)
                                thumbnailImageView.visibility = View.VISIBLE
                                updateLoadingVisibility(View.GONE)
                            }
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

    private fun updateLoadingVisibility(visibility: Int) {
        fragmentPhotosBinding.loadingIndicator.visibility = visibility
    }

    private fun updateImageViewsVisibilityToGone() {
        fragmentPhotosBinding.apply {
            photoTextView.visibility = View.GONE
            photoImageView.visibility = View.GONE
            thumbnailTextView.visibility = View.GONE
            thumbnailImageView.visibility = View.GONE
        }
    }

    private fun setupSpinnerRv(photos: List<Photo>) {
        photosAdapter = PhotosAdapter(this.requireContext(), photos)
        fragmentPhotosBinding.photosSpinner.apply {
            adapter = photosAdapter
        }
    }

}