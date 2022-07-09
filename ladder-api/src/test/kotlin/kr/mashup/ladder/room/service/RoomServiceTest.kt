package kr.mashup.ladder.room.service

import kr.mashup.ladder.SetupMemberIntegrationTest
import kr.mashup.ladder.domain.common.error.model.ForbiddenException
import kr.mashup.ladder.domain.room.domain.InvitationKey
import kr.mashup.ladder.domain.room.domain.Room
import kr.mashup.ladder.domain.room.domain.RoomNotFoundException
import kr.mashup.ladder.domain.room.infra.jpa.RoomRepository
import kr.mashup.ladder.room.dto.request.RoomCreateRequest
import kr.mashup.ladder.room.dto.request.RoomUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class RoomServiceTest(
    private val roomService: RoomService,
    private val roomRepository: RoomRepository,
) : SetupMemberIntegrationTest() {

    @Test
    fun `새로운 방을 생성한다`() {
        // given
        val request = RoomCreateRequest(
            description = "방에 대한 설명"
        )

        // when
        roomService.create(request, memberId = member.id)

        // then
        val rooms = roomRepository.findAll()
        assertThat(rooms).hasSize(1)
        rooms[0].also {
            assertThat(it.description).isEqualTo(request.description)
            assertThat(it.memberId).isEqualTo(member.id)
            assertThat(it.invitationKey).isNotNull
        }
    }

    @Test
    fun `방에 대한 정보를 수정합니다`() {
        // given
        val room = Room(
            memberId = member.id,
            description = "방에 대한 설명",
            invitationKey = InvitationKey.newInstance()
        )
        roomRepository.save(room)

        val request = RoomUpdateRequest(
            description = "변경 된 방에 대한 설명"
        )

        // when
        roomService.update(roomId = room.id, request = request, memberId = member.id)

        // then
        val rooms = roomRepository.findAll()
        assertThat(rooms).hasSize(1)
        rooms[0].also {
            assertThat(it.description).isEqualTo(request.description)
            assertThat(it.memberId).isEqualTo(member.id)
            assertThat(it.invitationKey).isNotNull
        }
    }

    @Test
    fun `존재하지 않는 방에 대해서 정보를 수정할 수 없다`() {
        // given
        val notFoundRoomId = -1L

        val request = RoomUpdateRequest(
            description = "변경 된 방에 대한 설명"
        )

        // when
        assertThatThrownBy {
            roomService.update(roomId = notFoundRoomId, request = request, memberId = member.id)
        }.isInstanceOf(RoomNotFoundException::class.java)
    }

    @Test
    fun `방장만이 방에 대한 정보를 수정할 수 있다`() {
        // given
        val isNotOwnerId = -1L

        val room = Room(
            memberId = member.id,
            description = "방에 대한 설명",
            invitationKey = InvitationKey.newInstance()
        )
        roomRepository.save(room)

        val request = RoomUpdateRequest(
            description = "변경 된 방에 대한 설명"
        )

        // when & then
        assertThatThrownBy {
            roomService.update(roomId = room.id, request = request, memberId = isNotOwnerId)
        }.isInstanceOf(ForbiddenException::class.java)
    }

}