@file:JvmName("MessagingUtils")

package com.matejdro.wearutils.messages

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.matejdro.wearutils.coroutines.await
import java.util.Collections
import java.util.Comparator

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
        if (connectedNodes != null && !connectedNodes.isEmpty()) {
            Collections.sort(connectedNodes, NodeNearbyComparator.INSTANCE)
            node = connectedNodes[0].id
        }

        callback.onNodeRecevived(node)
    }
}

fun sendSingleMessage(googleApiClient: GoogleApiClient, targetPath: String, payload: ByteArray) {
    getOtherNodeIdAsync(googleApiClient, object : NodeCallback {
        override fun onNodeRecevived(node: String?) {
            Wearable.MessageApi.sendMessage(googleApiClient, node, targetPath, payload)
        }
    })
}

fun sendSingleMessage(googleApiClient: GoogleApiClient, targetPath: String, payload: ByteArray, callback: ResultCallback<in MessageApi.SendMessageResult>) {
    getOtherNodeIdAsync(googleApiClient, object : NodeCallback {
        override fun onNodeRecevived(node: String?) {
            Wearable.MessageApi.sendMessage(googleApiClient, node, targetPath, payload).setResultCallback(callback)
        }
    })
}

suspend fun getOtherNodeSuspending(context: Context): Node? {
    val nodeClient = Wearable.getNodeClient(context)

    val connectedNodes = nodeClient.connectedNodes.await()

    return connectedNodes?.minWith(NodeNearbyComparator.INSTANCE)
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
