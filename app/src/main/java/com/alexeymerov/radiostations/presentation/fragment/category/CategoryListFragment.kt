package com.alexeymerov.radiostations.presentation.fragment.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexeymerov.radiostations.common.collectWhenResumed
import com.alexeymerov.radiostations.databinding.FragmentCategoryListBinding
import com.alexeymerov.radiostations.domain.dto.CategoriesDto
import com.alexeymerov.radiostations.presentation.activity.main.MainActivity
import com.alexeymerov.radiostations.presentation.adapter.CategoriesRecyclerAdapter
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private val args: CategoryListFragmentArgs by navArgs()
    private val viewModel: CategoryListViewModel by viewModels()

    @Inject
    lateinit var requestOptions: RequestOptions
    private val requestManager by lazy { Glide.with(this).setDefaultRequestOptions(requestOptions) }

    @Inject
    lateinit var recyclerAdapter: CategoriesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated for ${args.categoryUrl}")
        initViews()
        initViewModel()
    }

    private fun initViews() {
        // in case of multiply fragment make some BaseFragment or something...
        (activity as? MainActivity)?.supportActionBar?.title = args.categoryTitle
        binding.progressBar.isVisible = true
        initRecycler()
    }

    private fun initRecycler() {
        recyclerAdapter.also {
            it.onClick = ::onCategoryClick
            it.requestManager = requestManager
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

    //todo consider relocate it to DI
    private fun initLayoutManager() = LinearLayoutManager(context).apply {
        isMeasurementCacheEnabled = true
        isItemPrefetchEnabled = true
        orientation = RecyclerView.VERTICAL
    }

    private fun onCategoryClick(category: CategoriesDto) {
        Timber.d("Go to category: ${category.text}")
        navigateTo(CategoryListFragmentDirections.toCategoriesFragment(category.url, category.text))
    }

    private fun navigateTo(direction: NavDirections) {
        Timber.d("navigateTo: $direction")
        findNavController().navigate(direction)
    }

    private fun initViewModel() = with(viewModel) {
        viewState.collectWhenResumed(viewLifecycleOwner, ::processNewState)
        getCategories(args.categoryUrl).collectWhenResumed(viewLifecycleOwner, ::updateRecycler)
    }

    private fun processNewState(state: ViewState) {
        Timber.d("New state: " + state.javaClass.simpleName)
        if (state == ViewState.NothingAvailable) {
            binding.nothingAvailableTv.isVisible = true
            binding.progressBar.isVisible = false
        }
    }

    private fun updateRecycler(list: List<CategoriesDto>) {
        Timber.d("New list (${list.size} elements) update for ${args.categoryUrl}")
        recyclerAdapter.submitList(list)
        binding.progressBar.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}