package com.sidspace.loven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.sidspace.loven.ads.di.YandexRewardedAdManager
import com.sidspace.loven.navigation.AppNavHost
import com.sidspace.loven.navigation.LovenTopBar
import com.sidspace.loven.ui.theme.LovenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var auth: FirebaseAuth
    //private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var rewardedManager: YandexRewardedAdManager

    @Inject
    lateinit var userManager: com.sidspace.core.data.model.UserManager


    /*private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                firebaseAuthWithGoogle(idToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    /* private fun firebaseAuthWithGoogle(idToken: String) {
         val credential = GoogleAuthProvider.getCredential(idToken, null)
         auth.signInWithCredential(credential)
             .addOnCompleteListener(this) { task ->
                 if (task.isSuccessful) {
                     Log.d("Auth", "✅ Sign in success: ${auth.currentUser?.email}")
                 } else {
                     Log.e("Auth", "❌ Sign in failed", task.exception)
                 }
             }
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        auth = FirebaseAuth.getInstance()
        rewardedManager.load(this)

        /* val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
             .requestEmail()
             .build()*/

        //googleSignInClient = GoogleSignIn.getClient(this, gso)


        //signOut()


        //addWords()


        setContent {
            LovenTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        LovenTopBar(navController = navController, onShowAds = {
                            rewardedManager.show(this)
                        }, userManager = userManager)
                    }) { innerPadding ->


                    AppNavHost(navController = navController, innerPaddingValues = innerPadding)


                }

            }
        }
    }
}

private fun addWords() {
    val db = Firebase.firestore

    val batch = db.batch()

    var words = listOf(
        mapOf("word_translate" to "style", "word_ru" to "стиль"),

        )

    println("words1 = " + words.size)
    words = words.distinctBy { it["word_translate"] }.distinctBy { it["word_ru"] }
        .filter { it["word_ru"].toString().length <= 21 }
    println("words2 = " + words.size)

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    /*scope.launch {


        val data = hashMapOf(
            "description" to "C2+ Авторский стиль",
            "starsToEnable" to 1180,
            "imageUrl" to "/way",
            "lessonsCount" to 13, //17 //13
            "name" to "Модуль 45",
        )

        val module = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules").add(data).await()
        repeat(13) { index ->

            val batch = db.batch()

            val lessonData = mapOf(
                "name" to "lesson ${index + 1}",
                "type" to if (index == 12) "LAST_GAME" else if (index % 2 == 0) "DEFAULT" else "SWAP"
            )

            val doc = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                .document(module.id)
                .collection("lessons").document("lesson ${index + 1}")// авто id

            batch.set(doc, lessonData)


            words.shuffled().take(if (index == 12) 100 else 50).forEach { word ->

                val docRef = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                    .document(module.id)
                    .collection("lessons").document(doc.id).collection("words").document() // авто id
                batch.set(docRef, word)
            }

            batch.commit()
                .addOnSuccessListener { println("Добавлено") }
                .addOnFailureListener { e -> println("❌ : $e") }
        }
    }*/
}

