package com.matejdro.wearutils.messages;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessagingUtils
{
    @WorkerThread
    public static String getOtherNodeId(GoogleApiClient googleApiClient)
    {
        List<Node> connectedNodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();
        if (connectedNodes == null || connectedNodes.isEmpty())
            return null;

        Collections.sort(connectedNodes, NodeNearbyComparator.INSTANCE);

        return connectedNodes.get(0).getId();
    }

    public static void getOtherNodeIdAsync(GoogleApiClient googleApiClient, final NodeCallback callback)
    {
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                List<Node> connectedNodes = getConnectedNodesResult.getNodes();

                String node = null;
                if (connectedNodes != null && !connectedNodes.isEmpty()) {
                    Collections.sort(connectedNodes, NodeNearbyComparator.INSTANCE);
                    node = connectedNodes.get(0).getId();
                }

                callback.onNodeRecevived(node);
            }
        });
    }

    public static void sendSingleMessage(final GoogleApiClient googleApiClient, final String targetPath, final byte[] payload) {
        getOtherNodeIdAsync(googleApiClient, new NodeCallback() {
            @Override
            public void onNodeRecevived(String node) {
                Wearable.MessageApi.sendMessage(googleApiClient, node, targetPath, payload);
            }
        });
    }

    public static void sendSingleMessage(final GoogleApiClient googleApiClient, final String targetPath, final byte[] payload, final ResultCallback<? super MessageApi.SendMessageResult> callback) {
        getOtherNodeIdAsync(googleApiClient, new NodeCallback() {
            @Override
            public void onNodeRecevived(String node) {
                Wearable.MessageApi.sendMessage(googleApiClient, node, targetPath, payload).setResultCallback(callback);
            }
        });
    }


    public interface NodeCallback {
        void onNodeRecevived(String node);
    }

    public static class NodeNearbyComparator implements Comparator<Node>
    {
        public static final NodeNearbyComparator INSTANCE = new NodeNearbyComparator();

        @Override
        public int compare(Node a, Node b)
        {
            int nearbyA = a.isNearby() ? 1 : 0;
            int nearbyB = b.isNearby() ? 1 : 0;
            return nearbyB - nearbyA;
        }
    }
}
