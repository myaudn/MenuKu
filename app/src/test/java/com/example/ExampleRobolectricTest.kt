package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MenuKu", appName)
  }

  @Test
  fun `verify OneSignal app ID configuration`() {
    assertEquals("89574a92-4428-4a46-9eaa-0f180e8d680d", MenuKuApplication.ONESIGNAL_APP_ID)
    assertEquals("89574a92-4428-4a46-9eaa-0f180e8d680d", BuildConfig.ONESIGNAL_APP_ID)
  }
}
