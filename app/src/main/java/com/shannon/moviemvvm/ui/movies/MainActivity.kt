package com.shannon.moviemvvm.ui.movies

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.shannon.moviemvvm.R
import com.shannon.moviemvvm.data.model.Movie
import com.shannon.moviemvvm.data.repository.NetworkState
import com.shannon.moviemvvm.databinding.ActivityMainBinding
import com.shannon.moviemvvm.ui.BaseActivity
import com.shannon.moviemvvm.utils.observe
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter : PopularMoviePagedListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()

    }

    private fun initViews() {
        movieAdapter = PopularMoviePagedListAdapter(this)

        val gridLayoutManager = GridLayoutManager(this, 3)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = movieAdapter.getItemViewType(position)
                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return 1
                else return 3
            }
        }
        
        binding.apply {
            rvMovieList.layoutManager = gridLayoutManager
            rvMovieList.setHasFixedSize(true)
            rvMovieList.adapter = movieAdapter
        }
    }

    override fun observeChange() {
        observe(viewModel.moviePagedList, ::onDataLoaded)
        observe(viewModel.networkState, ::handleLoading)
    }


    private fun handleLoading(status: NetworkState) {
        binding.progressBarPopular.visibility =
            if (viewModel.listIsEmpty() && status == NetworkState.LOADING) View.VISIBLE else View.GONE
        binding.txtErrorPopular.visibility =
            if (viewModel.listIsEmpty() && status == NetworkState.ERROR) View.VISIBLE else View.GONE

        if (!viewModel.listIsEmpty()) {
            movieAdapter.setNetworkState(status)
        }
    }

    private fun onDataLoaded(items: PagedList<Movie>) {
        movieAdapter.submitList(items)
    }

}
