package com.heyzeusv.solitaire.util

import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 *  Base spec for testing ViewModel.
 *
 *  Since we are using `viewModelScope` in the ViewModel which uses Main dispatcher, this spec
 *  sets Test dispatcher as a Main dispatcher so that it becomes easy to test the ViewModel.
 *
 *  Found here: [https://github.com/PatilShreyas/NotyKT/blob/master/noty-android/app/src/test/java/dev/shreyaspatil/noty/base/ViewModelBehaviorSpec.kt]
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelBehaviorSpec(body: BehaviorSpec.() -> Unit = { }) : BehaviorSpec ({
    val dispatcher = StandardTestDispatcher()

    coroutineTestScope = true
    Dispatchers.setMain(dispatcher)

    apply(body)

    afterSpec { Dispatchers.resetMain() }
})