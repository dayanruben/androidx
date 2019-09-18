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

package androidx.lifecycle.viewmodel.savedstate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.filters.MediumTest
import androidx.testutils.withActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

internal const val FRAGMENT_MODE = "fragment"
internal const val ACTIVITY_MODE = "activity"

@RunWith(Parameterized::class)
@MediumTest
class ViewModelsWithStateTest(private val mode: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun initParameters(): Array<Any> = arrayOf(FRAGMENT_MODE, ACTIVITY_MODE)
    }

    @Test
    fun testSimpleSavingVM() {
        val newValue = "para"
        val state = with(ActivityScenario.launch(FakingSavedStateActivity::class.java)) {
            onActivity {
                val vm = vmProvider(it).get(VM::class.java)
                vm.mLiveData.value = newValue
            }

            moveToState(Lifecycle.State.CREATED)
            val state = withActivity { savedState }
            moveToState(Lifecycle.State.DESTROYED)
            assertThat(state.isEmpty).isFalse()
            state
        }

        val intent = createIntent(state)

        val vm = ActivityScenario.launch<FakingSavedStateActivity>(intent).withActivity {
            vmProvider(this).get(VM::class.java)
        }
        assertThat(vm.mLiveData.value).isEqualTo(newValue)
    }

    @Test
    @Throws(Throwable::class)
    fun testReattachment() {
        val newValue = "newValue"
        val state = with(ActivityScenario.launch(FakingSavedStateActivity::class.java)) {
            val escapedVM = withActivity { vmProvider(this).get(VM::class.java) }
            recreate()
            val vm = withActivity {
                val vm = vmProvider(this).get(VM::class.java)
                vm.mLiveData.value = newValue
                vm
            }
            assertThat(vm).isEqualTo(escapedVM)
            moveToState(Lifecycle.State.CREATED)

            val state = withActivity { savedState }
            moveToState(Lifecycle.State.DESTROYED)
            state
        }

        val intent = createIntent(state)
        val vm = ActivityScenario.launch<FakingSavedStateActivity>(intent).withActivity {
            vmProvider(this).get(VM::class.java)
        }
        assertThat(vm.mLiveData.value).isEqualTo(newValue)
    }

    @Test
    @Throws(Throwable::class)
    fun testFirstAccessAfterOnStop() {
        val newValue = "newValue"
        val state = with(ActivityScenario.launch(FakingSavedStateActivity::class.java)) {
            moveToState(Lifecycle.State.CREATED)
            val escapedVM = withActivity {
                val escapedVM = vmProvider(this).get(VM::class.java)
                escapedVM.mLiveData.value = newValue
                escapedVM
            }
            recreate()
            val vm = withActivity { vmProvider(this).get(VM::class.java) }
            assertThat(vm).isEqualTo(escapedVM)
            moveToState(Lifecycle.State.CREATED)
            val state = withActivity { savedState }
            assertThat(state.isEmpty).isFalse()
            moveToState(Lifecycle.State.DESTROYED)
            state
        }
        val intent = createIntent(state)
        val vm = ActivityScenario.launch<FakingSavedStateActivity>(intent).withActivity {
            vmProvider(this).get(VM::class.java)
        }
        assertThat(vm.mLiveData.value).isEqualTo(newValue)
    }

    private fun vmProvider(activity: FakingSavedStateActivity): ViewModelProvider {
        if (FRAGMENT_MODE == mode) {
            val fragment = activity.fragment
            return ViewModelProvider(
                fragment, SavedStateViewModelFactory(activity.application, fragment)
            )
        }
        return ViewModelProvider(
            activity, SavedStateViewModelFactory(activity.application, activity)
        )
    }

    class VM(handle: SavedStateHandle) : ViewModel() {
        val mLiveData = handle.getLiveData<String>("state")
    }
}

internal const val FAKE_SAVED_STATE = "fake_saved_state"

internal fun createIntent(savedState: Bundle): Intent {
    val intent = Intent()
    intent.setClassName(
        "androidx.lifecycle.viewmodel.savedstate.test",
        FakingSavedStateActivity::class.java.canonicalName!!
    )
    return intent.putExtra(FAKE_SAVED_STATE, savedState)
}

class FakingSavedStateActivity : FragmentActivity() {
    private val FRAGMENT_TAG = "tag"

    private fun fakeSavedState() = intent?.getBundleExtra(FAKE_SAVED_STATE)

    val fragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)!!

    var savedState: Bundle = Bundle.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        val alternativeState = savedInstanceState ?: fakeSavedState()
        super.onCreate(alternativeState)
        if (alternativeState == null) {
            supportFragmentManager.beginTransaction().add(Fragment(), FRAGMENT_TAG).commitNow()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        savedState = outState
    }
}
