package com.cafstone.dicodingstoryapp.login

import android.content.Intent
import android.widget.ProgressBar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.cafstone.dicodingstoryapp.R
import com.cafstone.dicodingstoryapp.utils.EspressoIdlingResource
import com.cafstone.dicodingstoryapp.utils.ProgressBarIdlingResource
import com.cafstone.dicodingstoryapp.view.login.LoginActivity
import com.cafstone.dicodingstoryapp.view.main.MainActivity
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


        onView(withId(R.id.emailEditText)).perform(typeText("arif@hotmail.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText)).perform(typeText("arif_ganteng"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.swiperefresh)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogout() {

        activity.scenario.onActivity {
            it.startActivity(Intent(it, MainActivity::class.java))
        }

        onView(withId(R.id.action_settings)).perform(click())

        onView(withId(R.id.welcome)).check(matches(isDisplayed()))
    }
}