
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.unicefwinterizationplatform.winterization_android.CouchBaseManager;
import com.unicefwinterizationplatform.winterization_android.LoadingActivity;
import com.unicefwinterizationplatform.winterization_android.LoginActivity;
import com.unicefwinterizationplatform.winterization_android.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by Tarek on 8/4/15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class test {

    public static final String USERNAME = "unicef-nco";
    public static final String PASSWORD = "unicef2015";



    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<LoginActivity>(
            LoginActivity.class);


    @Test
    public void login_test() throws InterruptedException {


        try {

            CouchBaseManager.getInstance(mActivityRule.getActivity()).startCBSetUp();

            CouchBaseManager.getInstance(mActivityRule.getActivity()).startPullReplication();
        }
        catch (Exception e)
        {

        }

        Thread.sleep(500);

        onView(withId(R.id.editText_username))
                .perform(typeText(USERNAME), closeSoftKeyboard());


        onView(withId(R.id.editText_username)).check(matches(isDisplayed()));

        Thread.sleep(500);

        onView(withId(R.id.editText_password))
               .perform(typeText(PASSWORD), closeSoftKeyboard());


        onView(withId(R.id.editText_password)).check(matches(isDisplayed()));

        Thread.sleep(500);

        onView(withId(R.id.button_login)).perform(click());

        Thread.sleep(500);

        onView(withId(R.id.main_page)).check(matches(isDisplayed()));

    }




}
