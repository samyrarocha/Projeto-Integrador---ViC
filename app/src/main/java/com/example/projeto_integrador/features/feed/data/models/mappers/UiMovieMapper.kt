package com.example.projeto_integrador.features.feed.data.models.mappers

import com.example.projeto_integrador.common.domain.model.movies.Movie
import com.example.projeto_integrador.features.feed.data.models.UIMovie

class UiMovieMapper: UiMapper<Movie, UIMovie> {

    override fun mapToView(input: Movie): UIMovie {
        return UIMovie(
            id = input.discoverMovieId,
            name = input.discoverMovieTitle,
            image = input.discoverPosterPath
        )
    }
}