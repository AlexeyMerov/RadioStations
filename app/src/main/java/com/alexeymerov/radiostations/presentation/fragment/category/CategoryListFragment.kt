package com.alexeymerov.radiostations.presentation.fragment.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexeymerov.radiostations.common.collectWhenResumed
import com.alexeymerov.radiostations.databinding.FragmentCategoryListBinding
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.presentation.adapter.CategoriesRecyclerAdapter
import com.alexeymerov.radiostations.presentation.fragment.BaseFragment
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CategoryListFragment : BaseFragment<FragmentCategoryListBinding>() {

    private val args: CategoryListFragmentArgs by navArgs()
    private val viewModel: CategoryListViewModel by viewModels()

    @Inject
    lateinit var requestOptions: RequestOptions
    private val glideRequestManager by lazy { Glide.with(this).setDefaultRequestOptions(requestOptions) }

    @Inject
    lateinit var recyclerAdapter: CategoriesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initViews() {
        setToolbarTitle(args.categoryTitle)
        binding.progressBar.isVisible = true
        initRecycler()
    }

    private fun initRecycler() {
        recyclerAdapter.also {
            it.onCategoryClick = ::onCategoryClick
            it.onAudioClick = ::onAudioClick
            it.glideRequestManager = glideRequestManager
        }
        binding.recyclerView.also {
            it.setHasFixedSize(true)
            val layoutManager = initLayoutManager()
            it.layoutManager = layoutManager
            it.addItemDecoration(DividerItemDecoration(it.context, layoutManager.orientation))
            it.adapter = recyclerAdapter
            it.setItemViewCacheSize(4)
        }
    }

    private fun initLayoutManager() = LinearLayoutManager(context).apply {
        isMeasurementCacheEnabled = true
        isItemPrefetchEnabled = true
        orientation = RecyclerView.VERTICAL
    }

    private fun onCategoryClick(category: CategoryItemDto) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  Go to category: ${category.text}")
        navigateTo(CategoryListFragmentDirections.toCategoriesFragment(category.url, category.text))
    }

    private fun onAudioClick(category: CategoryItemDto) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  Go to audio: ${category.text}")
        navigateTo(
            CategoryListFragmentDirections.toAudioFragment(
                rawUrl = category.url,
                stationName = category.text,
                stationImgUrl = category.image.orEmpty()
            )
        )
    }

    override fun initViewModel(): Unit = with(viewModel) {
        viewState.collectWhenResumed(viewLifecycleOwner, ::processNewState)
        getCategories(args.categoryUrl).collectWhenResumed(viewLifecycleOwner, ::updateRecycler)
    }

    private fun processNewState(state: ViewState) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  New state: " + state.javaClass.simpleName)
        if (state == ViewState.NothingAvailable) {
            binding.nothingAvailableTv.isVisible = true
            binding.progressBar.isGone = true
        }
    }

    private fun updateRecycler(list: List<CategoryItemDto>) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  New list (${list.size} elements) update for ${args.categoryUrl}")
        recyclerAdapter.submitList(list)
        if (list.isNotEmpty()) binding.progressBar.isGone = true
    }

}