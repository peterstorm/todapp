package domain

import io.circe._
import io.circe.syntax._
import java.util.UUID

opaque type Tasks = Iterable[(UUID, Task)]

object Tasks:

    def apply(tasks: Iterable[(UUID, Task)]): Tasks = tasks

    val empty: Tasks = List.empty

    extension (t: Tasks):
        def toList: List[(UUID, Task)] = t.toList

        def toMap: Map[UUID, Task] = t.toMap

    val elementDecoder = new Decoder[(UUID, Task)]:
        def apply(c: HCursor): Decoder.Result[(UUID, Task)] =
            for
                id <- c.downField("id").as[UUID]
                task <- c.downField("task").as[Task]
            yield (id -> task)

    given taskCodec as Codec[Tasks]:
        def apply(c: HCursor): Decoder.Result[Tasks] =
            c.as(Decoder.decodeList(elementDecoder)).map(t => Tasks(t))
        
        def apply(t: Tasks): Json =
            Json.arr(
                t.toArray.map { 
                    case (id, task) => Json.obj("id" -> Json.fromString(id.toString), "task" -> task.asJson)
                }: _*
            )