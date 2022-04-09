package com.codewithleposo.conferencingappdemo.screens.room

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.codewithleposo.conferencingappdemo.screens.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import live.hms.video.media.tracks.HMSVideoTrack
import live.hms.video.sdk.models.HMSPeer
import live.hms.video.utils.SharedEglContext
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun CallScreen(
    name: String,
    navigator: DestinationsNavigator,
    viewModel: CallViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.startMeeting(name)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (viewModel.loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .padding(12.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart),
        ) {
            val peers = viewModel.peers.value
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                contentPadding = PaddingValues(7.dp)
            ) {
                items(peers) { peer ->
                    PersonItem(peer = peer)
                }
            }
        }

        CallBottomButtons(
            onSwitchCamera = {
                viewModel.switchCamera()
            },
            onTurnOffVideo = {
                viewModel.setLocalVideoEnabled()
            },
            onToggleMic = {
                viewModel.setLocalAudioEnabled()
            },
            onEndCall = {
                viewModel.leaveTheCall()
                navigator.navigate(LoginScreenDestination)
            },
            micOn = viewModel.localMic.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun CallBottomButtons(
    onSwitchCamera: () -> Unit = {},
    onTurnOffVideo: () -> Unit = {},
    onToggleMic: () -> Unit = {},
    onEndCall: () -> Unit = {},
    micOn: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        backgroundColor = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onSwitchCamera()
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                Icon(imageVector = Icons.Default.FlipCameraIos, contentDescription = null)
            }
            IconButton(
                onClick = {
                    onTurnOffVideo()
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                Icon(imageVector = Icons.Default.VideocamOff, contentDescription = null)
            }
            IconButton(
                onClick = {
                    onToggleMic()
                    Timber.d("Mic state: Mic is $micOn")
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = if (micOn) {
                        Icons.Default.Mic
                    } else {
                        Icons.Default.MicOff
                    },
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    onEndCall()
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun PersonItem(peer: HMSPeer) {
    var previousActivePeer by remember { mutableStateOf(peer) }
    var previousVideoTrack by remember { mutableStateOf<HMSVideoTrack?>(null) }

    Box {
        AndroidView(
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                    setEnableHardwareScaler(true)
                }
            },
            modifier = Modifier
                .size(Dp(160f), Dp(200f))
                .clip(RoundedCornerShape(8.dp)),
            update = {
                if (previousActivePeer.peerID != peer.peerID) {
                    if (previousVideoTrack != null) {
                        previousVideoTrack?.removeSink(it)
                        it.release()
                    }
                    previousActivePeer = peer
                }

                if (peer.videoTrack == null) {
                    Timber.d("Peer had no video")
                } else if (previousVideoTrack == null) {
                    it.init(SharedEglContext.context, null)
                    peer.videoTrack?.addSink(it)
                    previousVideoTrack = peer.videoTrack
                }
            }
        )
    }
}