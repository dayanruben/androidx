/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.ui.core.input

/**
 * An object for focusable object.
 *
 * Any component that will have input focus must implement this FocusNode.
 */
class FocusNode

/**
 * A callback interface for focus transition
 *
 * The callback is called when the focused node has changed with the previously focused node and
 * currently focused node.
 */
typealias FocusTransitionObserver = (FocusNode?, FocusNode?) -> Unit

/**
 * Interface of manager of focused composable.
 *
 * Focus manager keeps tracking the input focused node and provides focus transitions.
 */
interface FocusManager {
    /**
     * Request the focus assiciated with the identifier.
     *
     * Do nothing if there is no focusable node assciated with given identifier.
     *
     * @param identifier The focusable node identifier.
     */
    fun requestFocusById(identifier: String)

    /**
     * Register the focusable node with identifier.
     *
     * The caller must unregister when the focusable node is disposed.
     * This function overwrites if the focusable node is already registered.
     *
     * @param identifier focusable client identifier
     * @param node focusable client.
     */
    fun registerFocusNode(identifier: String, node: FocusNode)

    /**
     * Unregister the focusable node associated with given identifier.
     *
     * @param identifier focusable client identifier
     */
    fun unregisterFocusNode(identifier: String)

    /**
     * Request the input focus
     *
     * @param client A focusable client.
     */
    fun requestFocus(client: FocusNode)

    /**
     * Release the focus if given focus node is focused
     *
     * @param client A focusable client.
     */
    fun blur(client: FocusNode)

    /**
     * Observe focus transition for the passed [FocusNode].
     *
     * The observer is called AFTER the focus transition happens. So there is no way of
     * preventing focus gain or focus lose.
     */
    fun registerObserver(node: FocusNode, observer: FocusTransitionObserver)
}

/**
 * Manages focused composable and available.
 */
internal class FocusManagerImpl : FocusManager {
    /**
     * The focused client. Maybe null if nothing is focused.
     */
    private var focusedClient: FocusNode? = null

    private val observerMap = mutableMapOf<FocusNode, MutableList<FocusTransitionObserver>>()

    /**
     * The identifier to focusable node map.
     */
    private val focusMap = mutableMapOf<String, FocusNode>()

    override fun requestFocusById(identifier: String) {
        // TODO(nona): Good to defer the task for avoiding possible infinity loop.
        focusMap[identifier]?.let { requestFocus(it) }
    }

    override fun registerFocusNode(identifier: String, node: FocusNode) {
        focusMap[identifier] = node
    }

    override fun unregisterFocusNode(identifier: String) {
        focusMap.remove(identifier)
    }

    override fun requestFocus(client: FocusNode) {
        val currentFocus = focusedClient
        if (currentFocus == client) {
            return // focus in to the same component. Do nothing.
        }
        focusedClient = client

        callFocusTransition(currentFocus, client)
    }

    override fun blur(client: FocusNode) {
        if (focusedClient == client) {
            focusedClient = null
            callFocusTransition(client, null)
        }
    }

    private fun callFocusTransition(fromNode: FocusNode?, toNode: FocusNode?) {
        // We create new list so that we can safely call them even if somebody register observer
        // during calling callbacks.
        val observers = observerMap[fromNode].orEmpty() + observerMap[toNode].orEmpty()

        observers.forEach { it(fromNode, toNode) }
    }

    override fun registerObserver(node: FocusNode, observer: FocusTransitionObserver) {
        observerMap.getOrPut(node, { mutableListOf() }).add(observer)
    }
}