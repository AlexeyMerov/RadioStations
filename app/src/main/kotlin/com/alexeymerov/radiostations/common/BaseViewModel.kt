package com.alexeymerov.radiostations.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

interface BaseViewAction
interface BaseViewEffect
interface BaseViewState

abstract class BaseViewModel<S : BaseViewState, A : BaseViewAction, E : BaseViewEffect> : ViewModel() {

    private val initialState: S by lazy { createInitialState() }

    private val _viewState = MutableStateFlow<S>(initialState)
    val viewState = _viewState.asStateFlow()

    private val _viewAction = MutableSharedFlow<A>()
    private val viewAction = _viewAction.asSharedFlow()

    private val _viewEffect = Channel<E>(Channel.CONFLATED)
    val viewEffect = _viewEffect.receiveAsFlow()

    protected open val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }

    init {
        subscribeActions()
    }

    protected abstract fun createInitialState(): S

    protected abstract fun handleAction(action: A)

    open fun setAction(action: A) {
        _viewAction.emit(viewModelScope, action)
    }

    protected fun setState(state: S, expected: S? = null) {
        if (expected != null) {
            _viewState.compareAndSet(expected, state)
        } else {
            _viewState.value = state
        }
    }

    protected fun setEffect(effect: E) {
        _viewEffect.send(viewModelScope, effect)
    }

    private fun subscribeActions() {
        viewModelScope.launch {
            viewAction.collect {
                handleAction(it)
            }
        }
    }

    fun clear() {
        super.onCleared()
    }
}