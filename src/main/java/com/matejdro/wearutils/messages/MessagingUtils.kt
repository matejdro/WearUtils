@file:JvmName("MessagingUtils")

package com.matejdro.wearutils.messages

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await
import java.util.*

@WorkerThread
fun getOtherNodeId(googleApiClient: GoogleApiClient): String? {
    val connectedNodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().nodes
    if (connectedNodes == null || connectedNodes.isEmpty())
        return null

    Collections.sort(connectedNodes, NodeNearbyComparator.INSTANCE)

    return connectedNodes[0].id
}

fun getOtherNodeIdAsync(googleApiClient: GoogleApiClient, callback: NodeCallback) {
    Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback { getConnectedNodesResult ->
        val connectedNodes = getConnectedNodesResult.nodes

        var node: String? = null
        if (connectedNodes != null && connectedNodes.isNotEmpty()) {
            Collections.sort(connectedNodes, NodeNearbyComparator.INSTANCE)
            node = connectedNodes[0].id
        }

        callback.onNodeRecevived(node)
    }
}

suspend fun NodeClient.getNearestNodeId(): String? {
    val connectedNodes = this.connectedNodes.await()
            .sortedWith(NodeNearbyComparator.INSTANCE)

    return connectedNodes.firstOrNull()?.id
}

/**
 * @return if successful, an ID used to identify the sent message. If no client was connected, null. If sending failed, [ApiException].
 * A successful result doesn't guarantee delivery.
 */
suspend fun MessageClient.sendMessageToNearestClient(nodeClient: NodeClient, path: String, data: ByteArray? = null): Int? {
    try {
        val nearestNode = nodeClient.getNearestNodeId() ?: return null
        return this.sendMessage(nearestNode, path, data).await()
    } catch (e: ApiException) {
        // Api Exception usually mean bluetooth connection failure. We can't do anything.
        return null

    }
}

/**
 * @return if successful, an ID used to identify the sent message. If no client was connected, null. If sending failed, [ApiException].
 * A successful result doesn't guarantee delivery.
 */
suspend fun MessageClient.sendMessageToNearestClient(
        nodeClient: NodeClient,
        path: String,
        data: ByteArray? = null,
        messageOptions: MessageOptions
): Int? {
    val nearestNode = nodeClient.getNearestNodeId() ?: return null
    return this.sendMessage(nearestNode, path, data, messageOptions).await()
}

fun sendSingleMessage(googleApiClient: GoogleApiClient, targetPath: String, payload: ByteArray) {
    getOtherNodeIdAsync(googleApiClient, object : NodeCallback {
        override fun onNodeRecevived(node: String?) {
            Wearable.MessageApi.sendMessage(googleApiClient, requireNotNull(node), targetPath, payload)
        }
    })
}

fun sendSingleMessage(googleApiClient: GoogleApiClient, targetPath: String, payload: ByteArray, callback: (MessageApi.SendMessageResult) -> Unit) {
    getOtherNodeIdAsync(googleApiClient, object : NodeCallback {
        override fun onNodeRecevived(node: String?) {
            Wearable.MessageApi.sendMessage(googleApiClient, requireNotNull(node), targetPath, payload).setResultCallback(callback)
        }
    })
}

suspend fun getOtherNodeSuspending(context: Context): Node? {
    val nodeClient = Wearable.getNodeClient(context)

    val connectedNodes = nodeClient.connectedNodes.await()

    return connectedNodes.minWithOrNull(NodeNearbyComparator.INSTANCE)
}

interface NodeCallback {
    fun onNodeRecevived(node: String?)
}

class NodeNearbyComparator : Comparator<Node> {

    override fun compare(a: Node, b: Node): Int {
        val nearbyA = if (a.isNearby) 1 else 0
        val nearbyB = if (b.isNearby) 1 else 0
        return nearbyB - nearbyA
    }

    companion object {
        val INSTANCE = NodeNearbyComparator()
    }
}
