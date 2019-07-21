package service.directives

import service.models.{ApiError, CreateInquest, UpdateInquest}

trait Validator[T] {

  def validate(t: T): Option[ApiError]

}

object CreateInquestValidator extends Validator[CreateInquest] {

  def validate(createInquest: CreateInquest): Option[ApiError] = {
    if (createInquest.title.isEmpty)
      Some(ApiError.invalidInquestTitle(""))
    else
      None
  }

}

object UpdateInquestValidator extends Validator[UpdateInquest] {

  def validate(updateInquest: UpdateInquest): Option[ApiError] = {
    if (updateInquest.title.exists(_.isEmpty))
      Some(ApiError.invalidInquestTitle(""))
    else
      None
  }

}
