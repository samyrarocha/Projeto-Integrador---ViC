package com.example.projeto_integrador.features.feed.presentation

import android.accounts.NetworkErrorException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projeto_integrador.R
import com.example.projeto_integrador.common.domain.model.NetworkUnavailableException
import com.example.projeto_integrador.common.domain.model.NoMoreMoviesException
import com.example.projeto_integrador.common.domain.model.movies.Discover
import com.example.projeto_integrador.common.domain.model.movies.Movie
import com.example.projeto_integrador.features.feed.data.models.Event
import com.example.projeto_integrador.features.feed.data.models.mappers.UiMovieMapper
import com.example.projeto_integrador.features.feed.domain.usecases.RequestNextPageOfMoviesUseCase
import com.example.projeto_integrador.features.feed.uttils.DispatchersProvider
import com.example.projeto_integrador.features.feed.uttils.DispatchersProviderImp
import com.example.projeto_integrador.features.feed.uttils.createExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllMoviesViewModel(
    private val uiMovieMapper: UiMovieMapper,
    private val requestNextPageOfMoviesUseCase: RequestNextPageOfMoviesUseCase,
    private val dispatchersProvider: DispatchersProviderImp,
): ViewModel() {

    companion object {
        const val UI_PAGE_SIZE = 20
    }

    private val _state = MutableLiveData<AllMoviesViewState>()
    val state: LiveData<AllMoviesViewState>  = _state

    var isLoadingMoreMovies: Boolean = false
    var isLastPage = false

    private var page = 0

    init {
        _state.value = AllMoviesViewState(loading = true)
        subscribeToMovieUpdate()
    }

    fun onEvent(event: AllMoviesEvent){
        when(event) {
            is AllMoviesEvent.RequestInitialMoviesList -> loadMovies()
            is AllMoviesEvent.RequestMoreMovies -> loadNextMoviePage()
        }
    }

    private fun subscribeToMovieUpdate() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    requestNextPageOfMoviesUseCase(page++)
                }
            }.onSuccess {
                onNewMovieList(it.movies)
            }.onFailure {
                onFailure(it)
            }
        }
    }

    fun onNewMovieList(movies: List<Movie>) {
        _state.value = _state.value?.copy(
            loading = false,
            movies = movies.map { uiMovieMapper.mapToView(it) }
        )
    }

    private fun loadMovies() {
        if (state.value?.movies?.isEmpty() == true) {
            loadNextMoviePage()
        }
    }

    private fun loadNextMoviePage() {
        val exceptionHandler = viewModelScope.createExceptionHandler(R.string.an_error_occurred){
            onFailure(it)
        }

        viewModelScope.launch(exceptionHandler) {
            val discover = withContext(dispatchersProvider.io()) {
                requestNextPageOfMoviesUseCase(++page)
            }
            onPaginationInfoObtained(discover)
        }
    }

    private fun onPaginationInfoObtained(pagination: Discover) {
        page = pagination.page
    }


    private fun onFailure(failure: Throwable) {
        when(failure) {
            is NetworkErrorException,
            is NetworkUnavailableException -> {
                _state.value = _state.value?.copy(
                    loading = false,
                    failure = Event(failure)
                )
            }
            is NoMoreMoviesException -> {
                _state.value = _state.value?.copy(
                    loading = false,
                    noMoreMovies = true,
                    failure = Event(failure)
                )
            }
        }
    }
}