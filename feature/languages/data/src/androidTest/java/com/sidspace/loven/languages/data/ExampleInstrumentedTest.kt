package com.sidspace.loven.languages.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.languages.data.repository.LanguageRepositoryImpl
import com.sidspace.loven.languages.domain.model.LanguageDomain
import com.sidspace.loven.languages.domain.repository.LanguageRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: LanguageRepository

    @Before
    fun setUp() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setProjectId("loven-683cc")
                .setApplicationId("1:629800082652:android:89a870294ed86c254d6819")
                .setApiKey("AIzaSyCZFtkCKeY9xk1ZFZ9iTyxMiZzT3bx_vBs")
                .setDatabaseUrl("https://loven-683cc-default-rtdb.firebaseio.com")
                .setStorageBucket("loven-683cc.firebasestorage.app")
                .build()

            FirebaseApp.initializeApp(context, options)
        }

        firestore = FirebaseFirestore.getInstance()
        repository = LanguageRepositoryImpl(firestore)
    }

    @Test
    fun getLanguages_returnsSuccessWithData() = runBlocking {
        val result = repository.getLanguages()

        assertTrue(result is DomainResult.Success)

        val data = (result as DomainResult.Success).data
        assertTrue("Список языков не должен быть пустым", data.isNotEmpty())

        println("Полученные языки: ${data.map { it.nameLanguage }}")
    }

    @Test
    fun getLanguages_returnsErrorForInvalidCollection() = runBlocking {
        val badRepo = object : LanguageRepository {
            override suspend fun getLanguages(): DomainResult<List<LanguageDomain>> {
                return try {
                    val snapshot = firestore.collection("INVALID_COLLECTION").get().await()
                    if (snapshot.isEmpty) DomainResult.Error
                    else DomainResult.Success(snapshot.documents.map {
                        LanguageDomain(it.id, it.getString("name") ?: "", "", 0, false)
                    })
                } catch (e: Exception) {
                    DomainResult.Error
                }
            }
        }

        val result = badRepo.getLanguages()
        assertTrue(result is DomainResult.Error)
    }
}
