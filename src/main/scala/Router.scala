import akka.http.scaladsl.server.{Directives, Route}

import scala.util.control.NonFatal
import scala.util.{Failure, Success}

trait Router {

  def route: Route

}

class InquestRouter(inquestRepository: InquestRepository) extends Router with InquestDirectives with ValidatorDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("inquests") {
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(inquestRepository.all()) { inquests =>
          complete(inquests)
        }
      } ~ post {
        entity(as[CreateInquest]) { createInquest =>
          validateWith(CreateInquestValidator)(createInquest) {
            handleWithGeneric(inquestRepository.create(createInquest)) { inquest =>
              complete(inquest)
            }
          }
        }
      }
    }
  }
}