package com.example.yukgym


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ActivityRegisterTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(ActivityRegister::class.java)

    @Test
    fun activityRegisterTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(3000)

        val materialButton2 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton2.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText = onView(
            allOf(
                withId(R.id.etName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilName),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText.perform(scrollTo(), click())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.etName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilName),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText2.perform(scrollTo(), replaceText("test12"), closeSoftKeyboard())

        val materialButton3 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton3.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.etNoTelp),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilNoTelp),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText3.perform(scrollTo(), replaceText("0852"), closeSoftKeyboard())

        val materialButton4 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton4.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.etNoTelp), withText("0852"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilNoTelp),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText4.perform(scrollTo(), click())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.etNoTelp), withText("0852"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilNoTelp),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText5.perform(scrollTo(), replaceText("085212345678"))

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.etNoTelp), withText("085212345678"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilNoTelp),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText6.perform(closeSoftKeyboard())

        val materialButton5 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton5.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText11 = onView(
            allOf(
                withId(R.id.etBirthDate),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilBirthDate),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText11.perform(scrollTo(), click())

        val materialButton7 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton7.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val materialButton8 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton8.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))
//        1
//
//        val materialButton20 = onView(
//            allOf(
//                withId(R.id.btnSignUp), withText("SignUp"),
//                childAtPosition(
//                    allOf(
//                        withId(R.id.linearLayout2),
//                        childAtPosition(
//                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
//                            8
//                        )
//                    ),
//                    1
//                )
//            )
//        )
//        materialButton20.perform(scrollTo(), click())
//        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText7 = onView(
            allOf(
                withId(R.id.etEmail),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilEmail),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText7.perform(scrollTo(), replaceText("test12@gmail"), closeSoftKeyboard())

        val materialButton6 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton6.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText8 = onView(
            allOf(
                withId(R.id.etEmail), withText("test12@gmail"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilEmail),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText8.perform(scrollTo(), click())

        val textInputEditText9 = onView(
            allOf(
                withId(R.id.etEmail), withText("test12@gmail"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilEmail),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText9.perform(scrollTo(), replaceText("test12@gmail.com"))

        val textInputEditText10 = onView(
            allOf(
                withId(R.id.etEmail), withText("test12@gmail.com"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilEmail),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText10.perform(closeSoftKeyboard())

//        val materialButton21 = onView(
//            allOf(
//                withId(android.R.id.button1), withText("OK"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    3
//                )
//            )
//        )
//        materialButton21.perform(scrollTo(), click())
//        onView(isRoot()).perform(waitFor(3000))

        val materialButton24 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton24.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText12 = onView(
            allOf(
                withId(R.id.etPassword),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilPassword),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText12.perform(scrollTo(), replaceText("test12"), closeSoftKeyboard())

        val materialButton9 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton9.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText13 = onView(
            allOf(
                withId(R.id.etPassword), withText("test12"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilPassword),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText13.perform(scrollTo(), click())

        val textInputEditText14 = onView(
            allOf(
                withId(R.id.etPasswordConfirm),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilPasswordConfirm),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText14.perform(scrollTo(), replaceText("test"), closeSoftKeyboard())

        val materialButton10 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton10.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

        val textInputEditText15 = onView(
            allOf(
                withId(R.id.etPasswordConfirm), withText("test"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilPasswordConfirm),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText15.perform(scrollTo(), click())

        val textInputEditText16 = onView(
            allOf(
                withId(R.id.etPasswordConfirm), withText("test"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilPasswordConfirm),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText16.perform(scrollTo(), replaceText("test12"))

        val textInputEditText17 = onView(
            allOf(
                withId(R.id.etPasswordConfirm), withText("test12"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.ilPasswordConfirm),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText17.perform(closeSoftKeyboard())

        val materialButton11 = onView(
            allOf(
                withId(R.id.btnSignUp), withText("SignUp"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            8
                        )
                    ),
                    1
                )
            )
        )
        materialButton11.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(3000))

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
    fun waitFor(delay: Long): ViewAction?{
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $delay milliseconds."
            }

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(delay)
            }
        }
    }
}
