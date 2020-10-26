package domain

import  io.circe._

final case class Tag(tag: String)

object Tag:
    given tagCodec as Codec[Tag]:
        def apply(c: HCursor): Decoder.Result[Tag] =
            c.downField("tag").as[String].map(n => Tag(n))

        def apply(t: Tag): Json =
            Json.obj("tag" -> Json.fromString(t.tag))