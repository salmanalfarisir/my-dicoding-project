package com.salman.application.view.login

import android.content.Intent
import android.widget.ProgressBar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.salman.application.R
import com.salman.application.utils.EspressoIdlingResource
import com.salman.application.utils.ProgressBarIdlingResource
import com.salman.application.view.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        activityScenario.onActivity {
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            IdlingRegistry.getInstance().register(ProgressBarIdlingResource(progressBar))
        }


        onView(withId(R.id.ed_login_email)).perform(typeText("alisa999@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.ed_login_password)).perform(typeText("alisacantik80"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        onView(withText(R.string.yeah)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText(R.string.lanjut)).perform(click())

        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogout() {

        activity.scenario.onActivity {
            it.startActivity(Intent(it, MainActivity::class.java))
        }

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)

        onView(withText(R.string.logout)).perform(click())

        onView(withId(R.id.welcome)).check(matches(isDisplayed()))
    }
}