package com.sidspace.loven

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import com.sidspace.loven.ads.di.YandexRewardedAdManager
import com.sidspace.loven.navigation.AppNavHost
import com.sidspace.loven.navigation.LovenTopBar
import com.sidspace.loven.ui.theme.LovenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var rewardedManager: YandexRewardedAdManager

    @Inject
    lateinit var userManager: com.sidspace.core.data.model.UserManager


    private val launcher = registerForActivityResult(
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
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent

        launcher.launch(signInIntent)
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "✅ Sign in success: ${auth.currentUser?.email}")
                } else {
                    Log.e("Auth", "❌ Sign in failed", task.exception)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        auth = FirebaseAuth.getInstance()
        rewardedManager.load(this)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id)) // из google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        //signOut()

        val db = Firebase.firestore

        val batch = db.batch()

        val words = listOf(
            mapOf("word_translate" to "I", "word_ru" to "я"),
            mapOf("word_translate" to "you", "word_ru" to "ты"),
            mapOf("word_translate" to "he", "word_ru" to "он"),
            mapOf("word_translate" to "she", "word_ru" to "она"),
            mapOf("word_translate" to "it", "word_ru" to "оно"),
            mapOf("word_translate" to "we", "word_ru" to "мы"),
            mapOf("word_translate" to "they", "word_ru" to "они"),
            mapOf("word_translate" to "my", "word_ru" to "мой"),
            mapOf("word_translate" to "your", "word_ru" to "твой"),
            mapOf("word_translate" to "his", "word_ru" to "его"),
            mapOf("word_translate" to "her", "word_ru" to "ее"),
            mapOf("word_translate" to "our", "word_ru" to "наш"),
            mapOf("word_translate" to "their", "word_ru" to "их"),
            mapOf("word_translate" to "this", "word_ru" to "этот"),
            mapOf("word_translate" to "that", "word_ru" to "тот"),
            mapOf("word_translate" to "be", "word_ru" to "быть"),
            mapOf("word_translate" to "have", "word_ru" to "иметь"),
            mapOf("word_translate" to "do", "word_ru" to "делать"),
            mapOf("word_translate" to "go", "word_ru" to "идти"),
            mapOf("word_translate" to "come", "word_ru" to "приходить"),
            mapOf("word_translate" to "see", "word_ru" to "видеть"),
            mapOf("word_translate" to "say", "word_ru" to "говорить"),
            mapOf("word_translate" to "know", "word_ru" to "знать"),
            mapOf("word_translate" to "get", "word_ru" to "получать"),
            mapOf("word_translate" to "make", "word_ru" to "создавать"),
            mapOf("word_translate" to "take", "word_ru" to "брать"),
            mapOf("word_translate" to "give", "word_ru" to "давать"),
            mapOf("word_translate" to "find", "word_ru" to "находить"),
            mapOf("word_translate" to "think", "word_ru" to "думать"),
            mapOf("word_translate" to "tell", "word_ru" to "рассказывать"),
            mapOf("word_translate" to "ask", "word_ru" to "спрашивать"),
            mapOf("word_translate" to "work", "word_ru" to "работать"),
            mapOf("word_translate" to "feel", "word_ru" to "чувствовать"),
            mapOf("word_translate" to "try", "word_ru" to "пытаться"),
            mapOf("word_translate" to "call", "word_ru" to "звонить"),
            mapOf("word_translate" to "like", "word_ru" to "нравиться"),
            mapOf("word_translate" to "want", "word_ru" to "хотеть"),
            mapOf("word_translate" to "need", "word_ru" to "нуждаться"),
            mapOf("word_translate" to "can", "word_ru" to "мочь"),
            mapOf("word_translate" to "will", "word_ru" to "будет"),
            mapOf("word_translate" to "may", "word_ru" to "можно"),
            mapOf("word_translate" to "must", "word_ru" to "должен"),
            mapOf("word_translate" to "should", "word_ru" to "следует"),
            mapOf("word_translate" to "put", "word_ru" to "класть"),
            mapOf("word_translate" to "look", "word_ru" to "смотреть"),
            mapOf("word_translate" to "use", "word_ru" to "использовать"),
            mapOf("word_translate" to "help", "word_ru" to "помогать"),
            mapOf("word_translate" to "play", "word_ru" to "играть"),
            mapOf("word_translate" to "run", "word_ru" to "бежать"),
            mapOf("word_translate" to "eat", "word_ru" to "есть"),
            mapOf("word_translate" to "drink", "word_ru" to "пить"),
            mapOf("word_translate" to "sleep", "word_ru" to "спать"),
            mapOf("word_translate" to "read", "word_ru" to "читать"),
            mapOf("word_translate" to "write", "word_ru" to "писать"),
            mapOf("word_translate" to "learn", "word_ru" to "учить"),
            mapOf("word_translate" to "open", "word_ru" to "открывать"),
            mapOf("word_translate" to "close", "word_ru" to "закрывать"),
            mapOf("word_translate" to "buy", "word_ru" to "покупать"),
            mapOf("word_translate" to "sell", "word_ru" to "продавать"),
            mapOf("word_translate" to "live", "word_ru" to "жить"),
            mapOf("word_translate" to "family", "word_ru" to "семья"),
            mapOf("word_translate" to "mother", "word_ru" to "мама"),
            mapOf("word_translate" to "father", "word_ru" to "папа"),
            mapOf("word_translate" to "brother", "word_ru" to "брат"),
            mapOf("word_translate" to "sister", "word_ru" to "сестра"),
            mapOf("word_translate" to "child", "word_ru" to "ребенок"),
            mapOf("word_translate" to "son", "word_ru" to "сын"),
            mapOf("word_translate" to "daughter", "word_ru" to "дочь"),
            mapOf("word_translate" to "people", "word_ru" to "люди"),
            mapOf("word_translate" to "man", "word_ru" to "мужчина"),
            mapOf("word_translate" to "woman", "word_ru" to "женщина"),
            mapOf("word_translate" to "friend", "word_ru" to "друг"),
            mapOf("word_translate" to "name", "word_ru" to "имя"),
            mapOf("word_translate" to "person", "word_ru" to "персона"),
            mapOf("word_translate" to "boy", "word_ru" to "мальчик"),
            mapOf("word_translate" to "girl", "word_ru" to "девочка"),
            mapOf("word_translate" to "house", "word_ru" to "дом"),
            mapOf("word_translate" to "home", "word_ru" to "жилище"),
            mapOf("word_translate" to "room", "word_ru" to "комната"),
            mapOf("word_translate" to "door", "word_ru" to "дверь"),
            mapOf("word_translate" to "window", "word_ru" to "окно"),
            mapOf("word_translate" to "table", "word_ru" to "стол"),
            mapOf("word_translate" to "chair", "word_ru" to "стул"),
            mapOf("word_translate" to "bed", "word_ru" to "кровать"),
            mapOf("word_translate" to "kitchen", "word_ru" to "кухня"),
            mapOf("word_translate" to "bathroom", "word_ru" to "ванная"),
            mapOf("word_translate" to "floor", "word_ru" to "пол"),
            mapOf("word_translate" to "wall", "word_ru" to "стена"),
            mapOf("word_translate" to "garden", "word_ru" to "сад"),
            mapOf("word_translate" to "food", "word_ru" to "еда"),
            mapOf("word_translate" to "water", "word_ru" to "вода"),
            mapOf("word_translate" to "bread", "word_ru" to "хлеб"),
            mapOf("word_translate" to "milk", "word_ru" to "молоко"),
            mapOf("word_translate" to "apple", "word_ru" to "яблоко"),
            mapOf("word_translate" to "egg", "word_ru" to "яйцо"),
            mapOf("word_translate" to "meat", "word_ru" to "мясо"),
            mapOf("word_translate" to "rice", "word_ru" to "рис"),
            mapOf("word_translate" to "tea", "word_ru" to "чай"),
            mapOf("word_translate" to "coffee", "word_ru" to "кофе"),
            mapOf("word_translate" to "fruit", "word_ru" to "фрукт"),
            mapOf("word_translate" to "vegetable", "word_ru" to "овощ"),
            mapOf("word_translate" to "salt", "word_ru" to "соль"),
            mapOf("word_translate" to "sugar", "word_ru" to "сахар"),
            mapOf("word_translate" to "city", "word_ru" to "город"),
            mapOf("word_translate" to "street", "word_ru" to "улица"),
            mapOf("word_translate" to "shop", "word_ru" to "магазин"),
            mapOf("word_translate" to "school", "word_ru" to "школа"),
            mapOf("word_translate" to "car", "word_ru" to "автомобиль"),
            mapOf("word_translate" to "book", "word_ru" to "книга"),
            mapOf("word_translate" to "money", "word_ru" to "деньги"),
            mapOf("word_translate" to "park", "word_ru" to "парк"),
            mapOf("word_translate" to "hospital", "word_ru" to "больница"),
            mapOf("word_translate" to "bank", "word_ru" to "банк"),
            mapOf("word_translate" to "sun", "word_ru" to "солнце"),
            mapOf("word_translate" to "moon", "word_ru" to "луна"),
            mapOf("word_translate" to "star", "word_ru" to "звезда"),
            mapOf("word_translate" to "tree", "word_ru" to "дерево"),
            mapOf("word_translate" to "flower", "word_ru" to "цветок"),
            mapOf("word_translate" to "fire", "word_ru" to "огонь"),
            mapOf("word_translate" to "air", "word_ru" to "воздух"),
            mapOf("word_translate" to "sky", "word_ru" to "небо"),
            mapOf("word_translate" to "rain", "word_ru" to "дождь"),
            mapOf("word_translate" to "snow", "word_ru" to "снег"),
            mapOf("word_translate" to "animal", "word_ru" to "животное"),
            mapOf("word_translate" to "cat", "word_ru" to "кот"),
            mapOf("word_translate" to "dog", "word_ru" to "собака"),
            mapOf("word_translate" to "bird", "word_ru" to "птица"),
            mapOf("word_translate" to "fish", "word_ru" to "рыба"),
            mapOf("word_translate" to "horse", "word_ru" to "лошадь"),
            mapOf("word_translate" to "time", "word_ru" to "время"),
            mapOf("word_translate" to "day", "word_ru" to "день"),
            mapOf("word_translate" to "night", "word_ru" to "ночь"),
            mapOf("word_translate" to "year", "word_ru" to "год"),
            mapOf("word_translate" to "week", "word_ru" to "неделя"),
            mapOf("word_translate" to "month", "word_ru" to "месяц"),
            mapOf("word_translate" to "today", "word_ru" to "сегодня"),
            mapOf("word_translate" to "tomorrow", "word_ru" to "завтра"),
            mapOf("word_translate" to "yesterday", "word_ru" to "вчера"),
            mapOf("word_translate" to "hour", "word_ru" to "час"),
            mapOf("word_translate" to "minute", "word_ru" to "минута"),
            mapOf("word_translate" to "thing", "word_ru" to "вещь"),
            mapOf("word_translate" to "world", "word_ru" to "мир"),
            mapOf("word_translate" to "life", "word_ru" to "жизнь"),
            mapOf("word_translate" to "hand", "word_ru" to "рука"),
            mapOf("word_translate" to "eye", "word_ru" to "глаз"),
            mapOf("word_translate" to "head", "word_ru" to "голова"),
            mapOf("word_translate" to "foot", "word_ru" to "нога"),
            mapOf("word_translate" to "face", "word_ru" to "лицо"),
            mapOf("word_translate" to "word", "word_ru" to "слово"),
            mapOf("word_translate" to "number", "word_ru" to "число"),
            mapOf("word_translate" to "problem", "word_ru" to "проблема"),
            mapOf("word_translate" to "answer", "word_ru" to "ответ"),
            mapOf("word_translate" to "question", "word_ru" to "вопрос"),
            mapOf("word_translate" to "color", "word_ru" to "цвет"),
            mapOf("word_translate" to "good", "word_ru" to "хороший"),
            mapOf("word_translate" to "bad", "word_ru" to "плохой"),
            mapOf("word_translate" to "big", "word_ru" to "большой"),
            mapOf("word_translate" to "small", "word_ru" to "маленький"),
            mapOf("word_translate" to "new", "word_ru" to "новый"),
            mapOf("word_translate" to "old", "word_ru" to "старый"),
            mapOf("word_translate" to "young", "word_ru" to "молодой"),
            mapOf("word_translate" to "long", "word_ru" to "длинный"),
            mapOf("word_translate" to "short", "word_ru" to "короткий"),
            mapOf("word_translate" to "high", "word_ru" to "высокий"),
            mapOf("word_translate" to "low", "word_ru" to "низкий"),
            mapOf("word_translate" to "hot", "word_ru" to "горячий"),
            mapOf("word_translate" to "cold", "word_ru" to "холодный"),
            mapOf("word_translate" to "warm", "word_ru" to "теплый"),
            mapOf("word_translate" to "happy", "word_ru" to "счастливый"),
            mapOf("word_translate" to "sad", "word_ru" to "грустный"),
            mapOf("word_translate" to "beautiful", "word_ru" to "красивый"),
            mapOf("word_translate" to "ugly", "word_ru" to "уродливый"),
            mapOf("word_translate" to "rich", "word_ru" to "богатый"),
            mapOf("word_translate" to "poor", "word_ru" to "бедный"),
            mapOf("word_translate" to "strong", "word_ru" to "сильный"),
            mapOf("word_translate" to "weak", "word_ru" to "слабый"),
            mapOf("word_translate" to "fast", "word_ru" to "быстрый"),
            mapOf("word_translate" to "slow", "word_ru" to "медленный"),
            mapOf("word_translate" to "easy", "word_ru" to "легкий"),
            mapOf("word_translate" to "hard", "word_ru" to "трудный"),
            mapOf("word_translate" to "right", "word_ru" to "правильный"),
            mapOf("word_translate" to "wrong", "word_ru" to "неправильный"),
            mapOf("word_translate" to "true", "word_ru" to "истинный"),
            mapOf("word_translate" to "false", "word_ru" to "ложный"),
            mapOf("word_translate" to "clean", "word_ru" to "чистый"),
            mapOf("word_translate" to "dirty", "word_ru" to "грязный"),
            mapOf("word_translate" to "full", "word_ru" to "полный"),
            mapOf("word_translate" to "empty", "word_ru" to "пустой"),
            mapOf("word_translate" to "white", "word_ru" to "белый"),
            mapOf("word_translate" to "black", "word_ru" to "черный"),
            mapOf("word_translate" to "red", "word_ru" to "красный"),
            mapOf("word_translate" to "blue", "word_ru" to "синий"),
            mapOf("word_translate" to "green", "word_ru" to "зеленый"),
            mapOf("word_translate" to "yellow", "word_ru" to "желтый"),
            mapOf("word_translate" to "yes", "word_ru" to "да"),
            mapOf("word_translate" to "no", "word_ru" to "нет"),
            mapOf("word_translate" to "not", "word_ru" to "не"),
            mapOf("word_translate" to "very", "word_ru" to "очень"),
            mapOf("word_translate" to "too", "word_ru" to "слишком"),
            mapOf("word_translate" to "here", "word_ru" to "здесь"),
            mapOf("word_translate" to "there", "word_ru" to "там"),
            mapOf("word_translate" to "now", "word_ru" to "сейчас"),
            mapOf("word_translate" to "then", "word_ru" to "затем"),
            mapOf("word_translate" to "well", "word_ru" to "хорошо"),
            mapOf("word_translate" to "also", "word_ru" to "также"),
            mapOf("word_translate" to "always", "word_ru" to "всегда"),
            mapOf("word_translate" to "never", "word_ru" to "никогда"),
            mapOf("word_translate" to "often", "word_ru" to "часто"),
            mapOf("word_translate" to "sometimes", "word_ru" to "иногда"),
            mapOf("word_translate" to "in", "word_ru" to "в"),
            mapOf("word_translate" to "on", "word_ru" to "на"),
            mapOf("word_translate" to "at", "word_ru" to "у"),
            mapOf("word_translate" to "to", "word_ru" to "к"),
            mapOf("word_translate" to "for", "word_ru" to "для"),
            mapOf("word_translate" to "of", "word_ru" to "из"),
            mapOf("word_translate" to "with", "word_ru" to "с"),
            mapOf("word_translate" to "from", "word_ru" to "от"),
            mapOf("word_translate" to "by", "word_ru" to "по"),
            mapOf("word_translate" to "about", "word_ru" to "о"),
            mapOf("word_translate" to "what", "word_ru" to "что"),
            mapOf("word_translate" to "who", "word_ru" to "кто"),
            mapOf("word_translate" to "where", "word_ru" to "где"),
            mapOf("word_translate" to "when", "word_ru" to "когда"),
            mapOf("word_translate" to "why", "word_ru" to "почему"),
            mapOf("word_translate" to "one", "word_ru" to "один"),
            mapOf("word_translate" to "two", "word_ru" to "два"),
            mapOf("word_translate" to "three", "word_ru" to "три"),
            mapOf("word_translate" to "four", "word_ru" to "четыре"),
            mapOf("word_translate" to "five", "word_ru" to "пять"),
            mapOf("word_translate" to "six", "word_ru" to "шесть"),
            mapOf("word_translate" to "seven", "word_ru" to "семь"),
            mapOf("word_translate" to "eight", "word_ru" to "восемь"),
            mapOf("word_translate" to "nine", "word_ru" to "девять"),
            mapOf("word_translate" to "ten", "word_ru" to "десять")
        )


        /*repeat(13) { index ->
            val batch = db.batch()

            val lessonData = mapOf(
                "name" to "lesson ${index + 1}",
                "type" to if (index == 12) "LAST_GAME" else if (index % 2 == 0) "DEFAULT" else "SWAP"
            )

            val doc = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                .document("Mp9dlVP4MkXGPUoH6zb4")
                .collection("lessons").document("lesson ${index + 1}")// авто id

            batch.set(doc, lessonData)


            words.shuffled().take(if (index == 12) 100 else 50).forEach { word ->

                val docRef = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                    .document("Mp9dlVP4MkXGPUoH6zb4")
                    .collection("lessons").document(doc.id).collection("words").document() // авто id
                batch.set(docRef, word)
            }

            batch.commit()
                .addOnSuccessListener { println("Добавлено") }
                .addOnFailureListener { e -> println("❌ Ошибка: $e") }
        }*/

        /*  words.forEach { word ->
              val docRef =  db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules").document("Mp9dlVP4MkXGPUoH6zb4")
                  .collection("lessons").document("lesson 1").collection("words").document() // авто id
              batch.set(docRef, word)
          }

          batch.commit()
              .addOnSuccessListener { println("✅ Все данные загружены!") }
              .addOnFailureListener { e -> println("❌ Ошибка: $e") }*/

        /*
                db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                    .document("Mp9dlVP4MkXGPUoH6zb4").collection("lessons").get().addOnSuccessListener { result ->

                    println("datas = " + result)
                     for(document in result){
                         println("data = " + document.id)
                     }
                }.addOnFailureListener { exception ->
                    println("error = $exception")
                }*/

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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ElevatedPressableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: MutableState<Color>,
    content: @Composable () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    var isTap by remember { mutableStateOf(false) }

    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 0.dp,
        animationSpec = tween(durationMillis = 0),
        label = "offsetY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "CartoonButtonScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 10.dp, // при нажатии тень меньше
        label = "elevation"
    )

    var color by remember {
        mutableStateOf(Color.White)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
        tonalElevation = elevation, // для Material3
        shadowElevation = elevation, // для Material2
        modifier = modifier

            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 3.dp,
                color = borderColor.value,
                shape = RoundedCornerShape(12.dp)
            )
            .offset(y = offsetY)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {

                        isPressed = true
                        try {
                            awaitRelease()
                            if (isTap) {
                                isPressed = false
                                isTap = false
                            } else {
                                isTap = true
                            }
                        } catch (e: Exception) {
                            isPressed = false
                            e.printStackTrace()
                        } finally {
                            //color = Color.Red

                        }
                    },
                    onTap = {
                        //isPressed = !isPressed

                        onClick()
                    }

                )
            }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 48.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

