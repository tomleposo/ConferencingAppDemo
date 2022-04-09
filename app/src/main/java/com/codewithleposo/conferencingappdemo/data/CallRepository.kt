package com.codewithleposo.conferencingappdemo.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.codewithleposo.conferencingappdemo.model.Token
import com.codewithleposo.conferencingappdemo.model.TokenRequest
import com.google.gson.JsonObject
import live.hms.video.sdk.HMSSDK
import live.hms.video.sdk.HMSUpdateListener
import live.hms.video.sdk.models.HMSConfig
import timber.log.Timber
import javax.inject.Inject

class CallRepository @Inject constructor(private val tokenApi: TokenApi, private val HmsSdk: HMSSDK) {

    val localMic: MutableState<Boolean> = mutableStateOf(true)

    suspend fun getAccessToken(name: String, roomId: String = "622ff63144ae04b51cb01484") : Token {
        return tokenApi.requestAccessToken(
            TokenRequest(
                room_id = roomId,
                user_id = name,
                role = "guest"
            )
        )
    }

    fun joinRoom(userName: String, authToken: String, updateListener: HMSUpdateListener) {
        val info = JsonObject().apply { addProperty("name", userName) }
        val config = HMSConfig(
            userName = userName,
            authtoken = authToken,
            metadata = info.toString()
        )
        HmsSdk.join(config, updateListener)
    }

    fun leaveRoom() {
        HmsSdk.leave()
    }

    private fun isLocalAudioEnabled(): Boolean {
        val isAudioEnabled = HmsSdk.getLocalPeer()?.audioTrack?.isMute == true
        localMic.value = isAudioEnabled
        return isAudioEnabled
    }

    fun setLocalAudioEnabled() {
        HmsSdk.getLocalPeer()?.audioTrack?.apply {
            setMute(!isLocalAudioEnabled())
        }
    }

    fun setLocalVideoEnabled() {
        HmsSdk.getLocalPeer()?.videoTrack?.apply {
            setMute(!isLocalVideoEnabled())
        }
    }

    private fun isLocalVideoEnabled(): Boolean {
        return HmsSdk.getLocalPeer()?.videoTrack?.isMute == true
    }

    fun switchCamera(){
        try {
           HmsSdk.getLocalPeer()?.videoTrack?.switchCamera()
        }catch (e: Exception){
            Timber.d("camera switch: ${e.message}")
        }
    }
}