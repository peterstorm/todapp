package domain

import io.circe._
import io.circe.syntax._
import java.time.ZonedDateTime
import java.util.UUID

final case class Task(
    state: State,
    description: String,
    notes: Option[String],
    tags: List[Tag]
): 
    def complete(date: ZonedDateTime): Task =
        val newState =
            state match
                case State.Active => State.completedNow(date)
                case State.Completed(d) => State.Completed(d)
        this.copy(state = newState)

object Task:
    given Codec[Task] with
        def apply(c: HCursor): Decoder.Result[Task] =
            for
                state <- c.downField("state").as[State]
                description <- c.downField("description").as[String]
                notes <- c.downField("notes").as[Option[String]]
                tags <- c.downField("tags").as[List[Tag]]
            yield Task(state, description, notes, tags)

        def apply(t: Task): Json =
            Json.obj(
                "state" -> t.state.asJson,
                "description" -> Json.fromString(t.description),
                "notes" -> t.notes.asJson,
                "tags" -> t.tags.asJson
            )
            
            
