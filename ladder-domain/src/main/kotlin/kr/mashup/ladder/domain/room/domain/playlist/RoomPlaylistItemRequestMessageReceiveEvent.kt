package kr.mashup.ladder.domain.room.domain.playlist

class RoomPlaylistItemRequestMessageReceiveEvent(
    val roomId: Long,
    val playlistId: Long,
    val playlistItemId: Long,
)
