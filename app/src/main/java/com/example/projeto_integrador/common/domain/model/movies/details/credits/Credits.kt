package com.example.projeto_integrador.common.domain.model.movies.details.credits

import com.example.projeto_integrador.common.data.api.ApiCast
import com.example.projeto_integrador.common.domain.model.movies.details.credits.cast.Cast
import com.example.projeto_integrador.common.domain.model.movies.details.credits.crew.Crew

data class Credits(
    val creditsMovieId: Long?,
    val creditCast: List<Cast>,
    val creditCrew: List<Crew>
)