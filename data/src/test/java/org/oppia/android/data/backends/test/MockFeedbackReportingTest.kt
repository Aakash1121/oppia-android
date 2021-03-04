package org.oppia.android.data.backends.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oppia.android.app.model.FeedbackReport
import org.oppia.android.app.model.FeedbackReportingSystemContext
import org.oppia.android.app.model.Suggestion
import org.oppia.android.app.model.Suggestion.SuggestionCategory.LANGUAGE_SUGGESTION
import org.oppia.android.app.model.UserSuppliedFeedback
import org.oppia.android.data.backends.api.MockFeedbackReportingService
import org.oppia.android.data.backends.gae.NetworkInterceptor
import org.oppia.android.data.backends.gae.NetworkSettings
import org.oppia.android.data.backends.gae.api.FeedbackReportingService
import org.robolectric.annotation.LooperMode
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

/** Test for [FeedbackReportingService] retrofit instance using a [MockFeedbackReportingService]. */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MockFeedbackReportingTest {
  private lateinit var mockRetrofit: MockRetrofit
  private lateinit var retrofit: Retrofit

  // Timestamp for Aug 20, 2019 05:29 GMT
  private val unixTimestamp = 1566278940160.576
  private val systemContext = FeedbackReportingSystemContext.newBuilder()
    .setPackageName("example.package.name")
    .setPackageVersionCode(1)
    .setCountryLocale("IN")
    .setLanguageLocale("EN")
    .build()
  private val suggestion = Suggestion.newBuilder()
    .setSuggestionCategory(LANGUAGE_SUGGESTION)
    .setUserSubmittedSuggestion("french")
    .build()
  private val userSuppliedInfo = UserSuppliedFeedback.newBuilder()
    .setSuggestion(suggestion)
    .build()
  private val feedbackReport = FeedbackReport.newBuilder()
    .setReportCreationTimestampMs(unixTimestamp)
    .setSystemContext(systemContext)
    .setUserSuppliedInfo(userSuppliedInfo)
    .build()

  @Before
  fun setUp() {
    val client = OkHttpClient.Builder()
    client.addInterceptor(NetworkInterceptor())

    retrofit = retrofit2.Retrofit.Builder()
      .baseUrl(NetworkSettings.getBaseUrl())
      .addConverterFactory(MoshiConverterFactory.create())
      .client(client.build())
      .build()

    val behavior = NetworkBehavior.create()
    mockRetrofit = MockRetrofit.Builder(retrofit)
      .networkBehavior(behavior)
      .build()
  }

  @Test
  fun testFeedbackReportingService_postRequest_successfulResponseReceived() {
    val delegate = mockRetrofit.create(FeedbackReportingService::class.java)
    val mockService = MockFeedbackReportingService(delegate)
    val response = mockService.postFeedbackReport(feedbackReport).execute()

    // Service returns a Unit type so no information is contained in the response.
    assertThat(response.isSuccessful).isTrue()
  }
}