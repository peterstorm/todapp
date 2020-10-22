package domain

import io.circe._
import java.time.ZonedDateTime

enum State:

    def completed: Boolean =
        this match {
            case _: State.Completed => true
            case _ => false
        }

    def active: Boolean =
        this match {
            case State.Active => true
            case _ => false
        }

    case Active
    case Completed(data: ZonedDateTime)

object State:
        
    def completedNow: State =
        Completed(ZonedDateTime.now())

    given stateCodec as Codec[State]:
        def apply(c: HCursor): Decoder.Result[State] =
            c.downField("state").as[String].flatMap {
                case "active" => Right(Active)
                case "completed" =>
                    c.downField("date")
                        .as[String]
                        .map(s => ZonedDateTime.parse(s))
                        .map(d => Completed(d))
                case err =>
                    Left(DecodingFailure(s"The task type ${err} is not valid", List.empty))
            }
                    
        def apply(s: State): Json =
            s match
                case Active =>
                    Json.obj("state" -> Json.fromString("active"))
                case Completed(date) =>
                    Json.obj(
                        "state" -> Json.fromString("completed"),
                        "date" -> Json.fromString(date.toString)
                    )