package service.router

import akka.http.scaladsl.server.Route

import clients.firebase.FirebaseClient
import db.models.Action
import db.spec.{InquestRepository, UserRepository}
import service.directives._
import service.models._

class InquestRouter(
    inquestRepository: InquestRepository,
    userRepository: UserRepository,
    firebaseClient: FirebaseClient
  ) extends Router with AuthDirectives with HandlerDirectives with ValidatorDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private implicit val userRepo: UserRepository = userRepository
  private implicit val fbClient: FirebaseClient = firebaseClient

  override def route: Route = pathPrefix("api") {
    pathPrefix("inquests") {
      pathEndOrSingleSlash {
        get {
          handleWithGeneric(inquestRepository.all()) { inquests =>
            complete(inquests)
          }
        } ~ post {
          authorizeAction(Action.EditAuthority) apply { _ =>
            entity(as[InquestCreateRequest]) { createInquest =>
              validateWith(InquestCreateRequestValidator)(createInquest) {
                handleWithGeneric(inquestRepository.create(createInquest)) { inquest =>
                  complete(inquest)
                }
              }
            }
          }
        }
      } ~ path(IntNumber) { id: Int =>
        put {
         authorizeAction(Action.EditAuthority) apply { _ =>
           entity(as[InquestUpdateRequest]) { updateInquest =>
             validateWith(InquestUpdateRequestValidator)(updateInquest) {
               handle(inquestRepository.update(id, updateInquest)) {
                 case InquestRepository.InquestNotFound(_) =>
                   ApiError.inquestNotFound
                 case _ =>
                   ApiError.generic
               } { inquest =>
                 complete(inquest)
               }
             }
           }
         }
       }
      }
    }
  }

}
