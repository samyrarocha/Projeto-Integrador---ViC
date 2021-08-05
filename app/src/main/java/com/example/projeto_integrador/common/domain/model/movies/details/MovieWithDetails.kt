package com.example.projeto_integrador.common.domain.model.movies.details


class MovieWithDetails (
    val discoverMovieId: Long,
    val discoverMovieTitle: String,
    val discoverVoteAverage: Int,
    val favorite: Boolean,
    val discoverPosterPath: String,
    val details: Details
) {
    val aproval: Int
        get() = discoverVoteAverage.times(10)

}