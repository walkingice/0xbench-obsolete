/*
 * Copyright (C) 2010 0xlab - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zeroxlab.gc;

import org.zeroxlab.benchmark.TesterGC;

import android.os.Message;

class Node {
    Node left, right;
    int i, j;
    Node(Node l, Node r) { left = l; right = r; }
    Node() { }
}

public class GCBenchmark  {
    public static final int kStretchTreeDepth    = 16;    // about 8Mb
    public static final int kLongLivedTreeDepth  = 14;  // about 2Mb
    public static final int kArraySize  = 125000;  // about 2Mb
    public static final int kMinTreeDepth = 2;
    public static final int kMaxTreeDepth = 8;

    public static StringBuffer out = new StringBuffer();

    static void update(String s){
        out.append(s+"\n");
        Message m = new Message();
        m.what = TesterGC.GUINOTIFIER;
        TesterGC.mHandler.sendMessage(m);
    }

    // Nodes used by a tree of a given size
    static int TreeSize(int i) {
        return ((1 << (i + 1)) - 1);
    }

    // Number of iterations to use for a given tree depth
    static int NumIters(int i) {
        return 2 * TreeSize(kStretchTreeDepth) / TreeSize(i);
    }

    // Build tree top down, assigning to older objects. 
    static void Populate(int iDepth, Node thisNode) {
        if (iDepth<=0) {
            return;
        } else {
            iDepth--;
            thisNode.left  = new Node();
            thisNode.right = new Node();
            Populate (iDepth, thisNode.left);
            Populate (iDepth, thisNode.right);
        }
    }

    // Build tree bottom-up
    static Node MakeTree(int iDepth) {
        if (iDepth<=0) {
            return new Node();
        } else {
            return new Node(MakeTree(iDepth-1),
                    MakeTree(iDepth-1));
        }
    }

    static void PrintDiagnostics() {
        long lFreeMemory = Runtime.getRuntime().freeMemory();
        long lTotalMemory = Runtime.getRuntime().totalMemory();

        update("*Total memory:"
                + lTotalMemory + " bytes");
        update("*Free  memory:" + lFreeMemory + " bytes\n");
    }

    static void TimeConstruction(int depth) {
        Node    root;
        long    tStart, tFinish;
        int     iNumIters = NumIters(depth);
        Node    tempTree;

        update("Create " + iNumIters +
                " trees of depth " + depth);
        tStart = System.currentTimeMillis();
        for (int i = 0; i < iNumIters; ++i) {
            tempTree = new Node();
            Populate(depth, tempTree);
            tempTree = null;
        }
        tFinish = System.currentTimeMillis();
        update("- Top down: "
                + (tFinish - tStart) + "msecs");
        tStart = System.currentTimeMillis();
        for (int i = 0; i < iNumIters; ++i) {
            tempTree = MakeTree(depth);
            tempTree = null;
        }
        tFinish = System.currentTimeMillis();
        update("- Bottom up: "
                + (tFinish - tStart) + "msecs");

    }

    public static void benchmark() {
        out = new StringBuffer();
        Node    root;
        Node    longLivedTree;
        Node    tempTree;
        long    tStart, tFinish;
        long    tElapsed;

        //    Debug.startMethodTracing("gcbench");
        // update("Garbage Collector Test");
        update(
                "Stretching memory:\n    binary tree of depth "
                + kStretchTreeDepth);
        PrintDiagnostics();
        tStart = System.currentTimeMillis();

        // Stretch the memory space quickly
        tempTree = MakeTree(kStretchTreeDepth);
        tempTree = null;

        // Create a long lived object
        update(
                "Creating:\n    long-lived binary tree of depth " +
                kLongLivedTreeDepth);
        longLivedTree = new Node();
        Populate(kLongLivedTreeDepth, longLivedTree);

        // Create long-lived array, filling half of it
        update(
                "    long-lived array of "
                + kArraySize + " doubles");
        double array[] = new double[kArraySize];
        for (int i = 0; i < kArraySize/2; ++i) {
            array[i] = 1.0/i;
        }
        PrintDiagnostics();

        for (int d = kMinTreeDepth; d <= kMaxTreeDepth; d += 2) {
            TimeConstruction(d);
        }

        if (longLivedTree == null || array[1000] != 1.0/1000)
            update("Failed");
        // fake reference to LongLivedTree
        // and array
        // to keep them from being optimized away

        tFinish = System.currentTimeMillis();
        tElapsed = tFinish-tStart;
        PrintDiagnostics();
        update("Completed in " + tElapsed + "ms.");
        TesterGC.time = tElapsed;
        //Debug.stopMethodTracing();
    }
}

