package day2.level03

import day2.level03.models._

import cats.effect.IO

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

class Controller(dataStore: DataStore) {

  def handle(appRequest: AppRequest): IO[Response[IO]] =
    requestToResponse(appRequest).flatMap {
      case Right(appResponse) => Ok(appResponseToJson(appResponse))
      case Left(err) => InternalServerError(errorToJson(err))
    }

  /**
    * Call functions defined on `DataStore` and construct an `AppResponse` for each possible `AppRequest`
    *
    * Hint: Pattern match on `appRequest`
    */
  private[level03] def requestToResponse(appRequest: AppRequest): IO[Either[String, AppResponse]] =
    appRequest match {
      case ListMoviesReq => toAppResponse(dataStore.listMovies(), ListMoviesResp)
      case GetReviewsReq(movieId) => toAppResponse(dataStore.getReviews(movieId), GetReviewsResp)
      case AddMovieReq(name, desc) => toAppResponse(dataStore.addMovie(name, desc), AddMovieResp)
      case AddReviewReq(movieId, ReviewToAdd(author, comment)) => toAppResponse(dataStore.addReview(movieId, author, comment), AddReviewResp)
    }

  def toAppResponse[A, B](io: IO[Either[String, A]], f: A => B): IO[Either[String, B]] =
    io.map(_.map(f))

}
