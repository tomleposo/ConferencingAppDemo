package com.codewithleposo.conferencingappdemo.screens.room

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithleposo.conferencingappdemo.data.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.HMSTrack
import live.hms.video.media.tracks.HMSTrackType
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.*
import live.hms.video.sdk.models.enums.HMSPeerUpdate
import live.hms.video.sdk.models.enums.HMSRoomUpdate
import live.hms.video.sdk.models.enums.HMSTrackUpdate
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(private val repository: CallRepository) : ViewModel() {

    private val _peers: MutableState<List<HMSPeer>> =
        mutableStateOf(emptyList(), neverEqualPolicy())
    val peers: State<List<HMSPeer>> = _peers

    val localMic: State<Boolean> = repository.localMic

    var loading = false

    fun leaveTheCall() {
        repository.leaveRoom()
    }

    fun switchCamera() {
        repository.switchCamera()
    }

    fun setLocalAudioEnabled() {
        repository.setLocalAudioEnabled()
    }

    fun setLocalVideoEnabled() {
        repository.setLocalVideoEnabled()
    }

    fun startMeeting(name: String) {
        loading = true
        viewModelScope.launch {
            val token = repository.getAccessToken(name).token

            repository.joinRoom(
                name,
                token,
                object : HMSUpdateListener {
                    override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {
                        Timber.d("onChangeTrackStateRequest")
                    }

                    override fun onError(error: HMSException) {
                        loading = false
                        Timber.d(error.message)
                    }

                    override fun onJoin(room: HMSRoom) {
                        loading = false
                        _peers.value = room.peerList.asList()
                    }

                    override fun onMessageReceived(message: HMSMessage) {
                        Timber.d(message.message)
                    }

                    override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
                        Timber.d("There was a peer update: $type")

                        // Handle peer updates.
                        when (type) {
                            HMSPeerUpdate.PEER_JOINED -> _peers.value =
                                _peers.value.plus(peer)
                            HMSPeerUpdate.PEER_LEFT -> _peers.value =
                                _peers.value.filter { currentPeer -> currentPeer.peerID != peer.peerID }
                            HMSPeerUpdate.VIDEO_TOGGLED -> {
                                Timber.d("${peer.name} video toggled")
                            }
                        }
                    }

                    override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {
                        Timber.d("Role change request")
                    }

                    override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
                        Timber.d("Room update")
                    }

                    override fun onTrackUpdate(
                        type: HMSTrackUpdate,
                        track: HMSTrack,
                        peer: HMSPeer
                    ) {
                        if (type == HMSTrackUpdate.TRACK_REMOVED) {
                            Timber.d("Checking, $type, $track")
                            if (track.type == HMSTrackType.VIDEO) {
                                _peers.value =
                                    _peers.value.filter { currentPeer -> currentPeer.peerID != peer.peerID }
                                        .plus(peer)
                            }
                        }
                    }
                })
        }
    }
}