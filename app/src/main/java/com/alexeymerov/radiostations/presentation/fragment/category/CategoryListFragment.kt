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
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.databinding.FragmentCategoryListBinding
import com.alexeymerov.radiostations.presentation.activity.main.MainActivity
import com.alexeymerov.radiostations.presentation.adapter.CategoriesRecyclerAdapter
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState
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
    lateinit var recyclerAdapter: CategoriesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Fragment onViewCreated")
        initViews()
        initViewModel()
    }

    private fun initViews() {
        (activity as? MainActivity)?.supportActionBar?.title = args.categoryTitle
        binding.progressBar.isVisible = true
        initRecycler()
    }

    private fun initRecycler() {
        recyclerAdapter.onClick = ::onCategoryClick
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

    private fun onCategoryClick(categoryEntity: CategoryEntity) {
        navigateTo(CategoryListFragmentDirections.toCategoriesFragment(categoryEntity.url, categoryEntity.text))
    }

    private fun navigateTo(direction: NavDirections) {
        Timber.d("Navigation -> navigateTo: $direction")
        findNavController().navigate(direction)
    }

    private fun initViewModel() = with(viewModel) {
        viewState.collectWhenResumed(viewLifecycleOwner, ::processNewState)
        sendNewAction(ViewAction.LoadCategories(args.categoryUrl))
    }

    private fun processNewState(state: ViewState) {
        Timber.d("New state: " + state.javaClass.simpleName)
        when (state) {
            is ViewState.CategoriesLoaded -> recyclerAdapter.submitList(state.categories)
            ViewState.NothingAvailable -> binding.nothingAvailableTv.isVisible = true
        }
        binding.progressBar.isVisible = false
    }

    private fun sendNewAction(action: ViewAction) = viewModel.processAction(action)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}