package com.matejdro.wearutils.companion

import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.matejdro.wearutils.messages.getNearestNodeId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CompanionConnection(
    private val nodeClient: NodeClient,
    private val messageClient: MessageClient,
    private val coroutineScope: CoroutineScope
) {
    private val commandChannel = Channel<Command>(Channel.BUFFERED)
    private var started = false
    private var nodeId: String? = null

    init {
        coroutineScope.launch {
            try {
                while (isActive) {
                    select<Unit> {
                        commandChannel.onReceive { nextCommand ->
                            println("got command $nextCommand $nodeId")
                            when (nextCommand) {
                                is Command.Message -> {
                                    if (!started) {
                                        performStart()
                                    }
                                    nodeId?.let {
                                        messageClient.sendMessage(
                                            it,
                                            nextCommand.path,
                                            nextCommand.data
                                        )
                                    }
                                }

                                Command.Start -> {
                                    if (started) {
                                        return@onReceive
                                    }

                                    performStart()
                                }

                                Command.Stop -> {
                                    if (!started) {
                                        return@onReceive
                                    }

                                    withContext(NonCancellable) {
                                        performStop()
                                    }
                                }
                            }
                        }

                        onTimeout(KEEPALIVE_DELAY) {
                            nodeId?.let {
                                messageClient.sendMessage(
                                    it,
                                    CONN_MESSAGE_KEEPALIVE,
                                    null
                                )
                            }
                        }
                    }
                }
            } finally {
                if (started) {
                    withContext(NonCancellable) {
                        delay(1_000)
                        performStop()
                    }
                }
            }
        }
    }

    private suspend fun performStart() {
        val nodeId = nodeClient.getNearestNodeId()
        this.nodeId = nodeId

        if (nodeId != null) {
            messageClient.sendMessage(nodeId, CONN_MESSAGE_START, null)
            started = true
        }
    }

    private suspend fun performStop() {
        withContext(NonCancellable) {
            nodeId?.let { messageClient.sendMessage(it, CONN_MESSAGE_STOP, null) }
        }
    }

    suspend fun start() {
        commandChannel.trySend(Command.Start)
    }

    suspend fun stop() {
        commandChannel.trySend(Command.Stop)
    }

    suspend fun sendMessage(path: String, data: ByteArray? = null) {
        println("sensMessage")
        commandChannel.trySend(Command.Message(path, data))
    }

    fun autoStartFromFlow(mutableStateFlow: MutableStateFlow<*>) {
        coroutineScope.launch {
            SharingStarted.WhileSubscribed(5_000).command(mutableStateFlow.subscriptionCount).collect {
                println("hasActiveSubscripbers $it")
                when (it) {
                    SharingCommand.START -> start()
                    SharingCommand.STOP -> stop()
                    SharingCommand.STOP_AND_RESET_REPLAY_CACHE -> stop()
                }
            }
        }
    }

    internal sealed class Command {
        internal data object Start : Command()
        internal data object Stop : Command()
        internal class Message(val path: String, val data: ByteArray?) : Command()
    }

    companion object {
        internal const val CONN_MESSAGE_READY = "/Companion/Ready"
        internal const val CONN_MESSAGE_START = "/Companion/Start"
        internal const val CONN_MESSAGE_STOP = "/Companion/Stop"
        internal const val CONN_MESSAGE_KEEPALIVE = "/Companion/Keepalive"
        internal val KEEPALIVE_DELAY = 20.seconds
        internal val KEEPALIVE_TIMEOUT = 30.seconds
    }
}
