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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

        var words = listOf(
            mapOf("word_translate" to "doctor", "word_ru" to "врач"),
            mapOf("word_translate" to "nurse", "word_ru" to "медсестра"),
            mapOf("word_translate" to "hospital", "word_ru" to "больница"),
            mapOf("word_translate" to "clinic", "word_ru" to "клиника"),
            mapOf("word_translate" to "patient", "word_ru" to "пациент"),
            mapOf("word_translate" to "surgery", "word_ru" to "операция"),
            mapOf("word_translate" to "operation", "word_ru" to "операция"),
            mapOf("word_translate" to "treatment", "word_ru" to "лечение"),
            mapOf("word_translate" to "medicine", "word_ru" to "медицина"),
            mapOf("word_translate" to "drug", "word_ru" to "лекарство"),
            mapOf("word_translate" to "prescription", "word_ru" to "рецепт"),
            mapOf("word_translate" to "diagnosis", "word_ru" to "диагноз"),
            mapOf("word_translate" to "symptom", "word_ru" to "симптом"),
            mapOf("word_translate" to "disease", "word_ru" to "болезнь"),
            mapOf("word_translate" to "virus", "word_ru" to "вирус"),
            mapOf("word_translate" to "bacteria", "word_ru" to "бактерия"),
            mapOf("word_translate" to "infection", "word_ru" to "инфекция"),
            mapOf("word_translate" to "injury", "word_ru" to "травма"),
            mapOf("word_translate" to "pain", "word_ru" to "боль"),
            mapOf("word_translate" to "fever", "word_ru" to "жар"),
            mapOf("word_translate" to "blood", "word_ru" to "кровь"),
            mapOf("word_translate" to "heart", "word_ru" to "сердце"),
            mapOf("word_translate" to "lung", "word_ru" to "легкое"),
            mapOf("word_translate" to "stomach", "word_ru" to "желудок"),
            mapOf("word_translate" to "brain", "word_ru" to "мозг"),
            mapOf("word_translate" to "skeleton", "word_ru" to "скелет"),
            mapOf("word_translate" to "muscle", "word_ru" to "мышца"),
            mapOf("word_translate" to "nerve", "word_ru" to "нерв"),
            mapOf("word_translate" to "cell", "word_ru" to "клетка"),
            mapOf("word_translate" to "tissue", "word_ru" to "ткань"),
            mapOf("word_translate" to "organ", "word_ru" to "орган"),
            mapOf("word_translate" to "vaccination", "word_ru" to "вакцинация"),
            mapOf("word_translate" to "immunity", "word_ru" to "иммунитет"),
            mapOf("word_translate" to "health", "word_ru" to "здоровье"),
            mapOf("word_translate" to "exercise", "word_ru" to "упражнение"),
            mapOf("word_translate" to "therapy", "word_ru" to "терапия"),
            mapOf("word_translate" to "check-up", "word_ru" to "медицинский осмотр"),
            mapOf("word_translate" to "emergency", "word_ru" to "чрезвычайная ситуация"),
            mapOf("word_translate" to "ambulance", "word_ru" to "скорая помощь"),
            mapOf("word_translate" to "first aid", "word_ru" to "первая помощь"),
            mapOf("word_translate" to "equipment", "word_ru" to "оборудование"),
            mapOf("word_translate" to "machine", "word_ru" to "машина"),
            mapOf("word_translate" to "device", "word_ru" to "устройство"),
            mapOf("word_translate" to "technology", "word_ru" to "технология"),
            mapOf("word_translate" to "engineer", "word_ru" to "инженер"),
            mapOf("word_translate" to "computer", "word_ru" to "компьютер"),
            mapOf("word_translate" to "software", "word_ru" to "программное обеспечение"),
            mapOf("word_translate" to "hardware", "word_ru" to "аппаратное обеспечение"),
            mapOf("word_translate" to "network", "word_ru" to "сеть"),
            mapOf("word_translate" to "system", "word_ru" to "система"),
            mapOf("word_translate" to "technology development", "word_ru" to "развитие технологий"),
            mapOf("word_translate" to "innovation", "word_ru" to "инновация"),
            mapOf("word_translate" to "research", "word_ru" to "исследование"),
            mapOf("word_translate" to "experiment", "word_ru" to "эксперимент"),
            mapOf("word_translate" to "laboratory", "word_ru" to "лаборатория"),
            mapOf("word_translate" to "scientist", "word_ru" to "учёный"),
            mapOf("word_translate" to "analysis", "word_ru" to "анализ"),
            mapOf("word_translate" to "data", "word_ru" to "данные"),
            mapOf("word_translate" to "diagnostic tool", "word_ru" to "диагностический инструмент"),
            mapOf("word_translate" to "medication", "word_ru" to "лекарство"),
            mapOf("word_translate" to "treatment plan", "word_ru" to "план лечения"),
            mapOf("word_translate" to "healthcare", "word_ru" to "здравоохранение"),
            mapOf("word_translate" to "clinic visit", "word_ru" to "визит в клинику"),
            mapOf("word_translate" to "surgery room", "word_ru" to "операционная"),
            mapOf("word_translate" to "technical skill", "word_ru" to "технический навык"),
            mapOf("word_translate" to "mechanical", "word_ru" to "механический"),
            mapOf("word_translate" to "electrical", "word_ru" to "электрический"),
            mapOf("word_translate" to "circuit", "word_ru" to "схема"),
            mapOf("word_translate" to "engine", "word_ru" to "двигатель"),
            mapOf("word_translate" to "robot", "word_ru" to "робот"),
            mapOf("word_translate" to "automation", "word_ru" to "автоматизация"),
            mapOf("word_translate" to "innovation technology", "word_ru" to "инновационная технология"),
            mapOf("word_translate" to "technical equipment", "word_ru" to "техническое оборудование"),
            mapOf("word_translate" to "medical equipment", "word_ru" to "медицинское оборудование"),
            mapOf("word_translate" to "surgical instrument", "word_ru" to "хирургический инструмент"),
            mapOf("word_translate" to "vaccine development", "word_ru" to "разработка вакцины"),
            mapOf("word_translate" to "medical research", "word_ru" to "медицинское исследование"),
            mapOf("word_translate" to "diagnosis process", "word_ru" to "процесс диагностики"),
            mapOf("word_translate" to "technical process", "word_ru" to "технический процесс"),
            mapOf("word_translate" to "laboratory test", "word_ru" to "лабораторный тест"),
            mapOf("word_translate" to "clinical trial", "word_ru" to "клиническое испытание"),
            mapOf("word_translate" to "scientific discovery", "word_ru" to "научное открытие"),
            mapOf("word_translate" to "researcher", "word_ru" to "исследователь"),
            mapOf("word_translate" to "technical design", "word_ru" to "технический дизайн"),
            mapOf("word_translate" to "prototype", "word_ru" to "прототип"),
            mapOf("word_translate" to "mechanical system", "word_ru" to "механическая система"),
            mapOf("word_translate" to "electrical system", "word_ru" to "электрическая система"),
            mapOf("word_translate" to "digital technology", "word_ru" to "цифровая технология"),
            mapOf("word_translate" to "software development", "word_ru" to "разработка программного обеспечения"),
            mapOf("word_translate" to "hardware component", "word_ru" to "аппаратный компонент"),
            mapOf("word_translate" to "network system", "word_ru" to "сетевая система"),
            mapOf("word_translate" to "biomedical", "word_ru" to "биомедицинский"),
            mapOf("word_translate" to "medical device", "word_ru" to "медицинское устройство"),
            mapOf("word_translate" to "surgical procedure", "word_ru" to "хирургическая процедура"),
            mapOf("word_translate" to "diagnostic procedure", "word_ru" to "диагностическая процедура"),
            mapOf("word_translate" to "health monitoring", "word_ru" to "мониторинг здоровья"),
            mapOf("word_translate" to "medical treatment", "word_ru" to "медицинское лечение"),
            mapOf("word_translate" to "technical innovation", "word_ru" to "техническая инновация"),
            mapOf("word_translate" to "engineering", "word_ru" to "инженерия"),
            mapOf("word_translate" to "technical solution", "word_ru" to "техническое решение"),
            mapOf("word_translate" to "automation system", "word_ru" to "система автоматизации"),
            mapOf("word_translate" to "robotics", "word_ru" to "робототехника"),
            mapOf("word_translate" to "medical technology", "word_ru" to "медицинская технология"),
            mapOf("word_translate" to "digital health", "word_ru" to "цифровое здравоохранение"),
            mapOf("word_translate" to "electronic device", "word_ru" to "электронное устройство"),
            mapOf("word_translate" to "technical equipment maintenance", "word_ru" to "обслуживание технического оборудования"),
            mapOf("word_translate" to "scientific research", "word_ru" to "научное исследование"),
            mapOf("word_translate" to "technical documentation", "word_ru" to "техническая документация"),
            mapOf("word_translate" to "clinical diagnosis", "word_ru" to "клиническая диагностика"),
            mapOf("word_translate" to "medical research center", "word_ru" to "центр медицинских исследований"),
            mapOf("word_translate" to "clinical laboratory", "word_ru" to "клиническая лаборатория"),
            mapOf("word_translate" to "medical imaging", "word_ru" to "медицинская визуализация"),
            mapOf("word_translate" to "MRI scanner", "word_ru" to "МРТ сканер"),
            mapOf("word_translate" to "X-ray machine", "word_ru" to "рентгеновский аппарат"),
            mapOf("word_translate" to "ultrasound", "word_ru" to "ультразвук"),
            mapOf("word_translate" to "blood test", "word_ru" to "анализ крови"),
            mapOf("word_translate" to "diagnostic test", "word_ru" to "диагностический тест"),
            mapOf("word_translate" to "vaccination program", "word_ru" to "программа вакцинации"),
            mapOf("word_translate" to "epidemic", "word_ru" to "эпидемия"),
            mapOf("word_translate" to "pandemic", "word_ru" to "пандемия"),
            mapOf("word_translate" to "health policy", "word_ru" to "политика здравоохранения"),
            mapOf("word_translate" to "medical research project", "word_ru" to "проект медицинских исследований"),
            mapOf("word_translate" to "technical innovation project", "word_ru" to "проект технических инноваций"),
            mapOf("word_translate" to "engineering design", "word_ru" to "инженерный дизайн"),
            mapOf("word_translate" to "mechanical engineering", "word_ru" to "машиностроение"),
            mapOf("word_translate" to "electrical engineering", "word_ru" to "электротехника"),
            mapOf("word_translate" to "civil engineering", "word_ru" to "гражданское строительство"),
            mapOf("word_translate" to "chemical engineering", "word_ru" to "химическая инженерия"),
            mapOf("word_translate" to "biomedical engineering", "word_ru" to "биомедицинская инженерия"),
            mapOf("word_translate" to "robotics engineering", "word_ru" to "инженерия робототехники"),
            mapOf("word_translate" to "computer engineering", "word_ru" to "компьютерная инженерия"),
            mapOf("word_translate" to "software engineer", "word_ru" to "программист"),
            mapOf("word_translate" to "hardware engineer", "word_ru" to "инженер по аппаратному обеспечению"),
            mapOf("word_translate" to "automation engineer", "word_ru" to "инженер автоматизации"),
            mapOf("word_translate" to "technical project", "word_ru" to "технический проект"),
            mapOf("word_translate" to "industrial technology", "word_ru" to "промышленная технология"),
            mapOf("word_translate" to "manufacturing process", "word_ru" to "производственный процесс"),
            mapOf("word_translate" to "technical development", "word_ru" to "техническое развитие"),
            mapOf("word_translate" to "medical equipment maintenance", "word_ru" to "обслуживание медицинского оборудования"),
            mapOf("word_translate" to "technical solution", "word_ru" to "техническое решение"),
            mapOf("word_translate" to "emergency medicine", "word_ru" to "неотложная медицина"),
            mapOf("word_translate" to "surgical team", "word_ru" to "хирургическая бригада"),
            mapOf("word_translate" to "operating room", "word_ru" to "операционная"),
            mapOf("word_translate" to "health monitoring system", "word_ru" to "система мониторинга здоровья"),
            mapOf("word_translate" to "medical assistant", "word_ru" to "медицинский ассистент"),
            mapOf("word_translate" to "technical support", "word_ru" to "техническая поддержка"),
            mapOf("word_translate" to "laboratory technician", "word_ru" to "лаборант"),
            mapOf("word_translate" to "diagnostic equipment", "word_ru" to "диагностическое оборудование"),
            mapOf("word_translate" to "health data analysis", "word_ru" to "анализ данных о здоровье"),
            mapOf("word_translate" to "software system", "word_ru" to "программная система"),
            mapOf("word_translate" to "hardware device", "word_ru" to "аппаратное устройство"),
            mapOf("word_translate" to "technical instrument", "word_ru" to "технический инструмент"),
            mapOf("word_translate" to "mechanical device", "word_ru" to "механическое устройство"),
            mapOf("word_translate" to "electrical device", "word_ru" to "электрическое устройство"),
            mapOf("word_translate" to "robotic device", "word_ru" to "роботизированное устройство"),
            mapOf("word_translate" to "digital device", "word_ru" to "цифровое устройство"),
            mapOf("word_translate" to "healthcare system", "word_ru" to "система здравоохранения"),
            mapOf("word_translate" to "clinical system", "word_ru" to "клиническая система"),
            mapOf("word_translate" to "technical field", "word_ru" to "техническая область"),
            mapOf("word_translate" to "medical field", "word_ru" to "медицинская область"),
            mapOf("word_translate" to "research center", "word_ru" to "научно-исследовательский центр"),
            mapOf("word_translate" to "technical project manager", "word_ru" to "менеджер технического проекта"),
            mapOf("word_translate" to "software project", "word_ru" to "программный проект"),
            mapOf("word_translate" to "engineering project", "word_ru" to "инженерный проект"),
            mapOf("word_translate" to "medical software", "word_ru" to "медицинское программное обеспечение"),
            mapOf("word_translate" to "technical documentation", "word_ru" to "техническая документация"),
            mapOf("word_translate" to "research study", "word_ru" to "исследовательское исследование"),
            mapOf("word_translate" to "technical manual", "word_ru" to "техническое руководство"),
            mapOf("word_translate" to "health record", "word_ru" to "медицинская карта"),
            mapOf("word_translate" to "patient record", "word_ru" to "карта пациента"),
            mapOf("word_translate" to "medical examination", "word_ru" to "медицинское обследование"),
            mapOf("word_translate" to "medical procedure", "word_ru" to "медицинская процедура"),
            mapOf("word_translate" to "technical process", "word_ru" to "технический процесс"),
            mapOf("word_translate" to "engineering solution", "word_ru" to "инженерное решение"),
            mapOf("word_translate" to "industrial equipment", "word_ru" to "промышленное оборудование"),
            mapOf("word_translate" to "medical instrument", "word_ru" to "медицинский инструмент"),
            mapOf("word_translate" to "health monitoring device", "word_ru" to "устройство мониторинга здоровья"),
            mapOf("word_translate" to "laboratory equipment", "word_ru" to "лабораторное оборудование"),
            mapOf("word_translate" to "surgical tool", "word_ru" to "хирургический инструмент"),
            mapOf("word_translate" to "diagnostic tool", "word_ru" to "диагностический инструмент"),
            mapOf("word_translate" to "technical expert", "word_ru" to "технический эксперт"),
            mapOf("word_translate" to "medical expert", "word_ru" to "медицинский эксперт"),
            mapOf("word_translate" to "medicine", "word_ru" to "медицина"),
            mapOf("word_translate" to "medical", "word_ru" to "медицинский"),
            mapOf("word_translate" to "health", "word_ru" to "здоровье"),
            mapOf("word_translate" to "disease", "word_ru" to "болезнь"),
            mapOf("word_translate" to "illness", "word_ru" to "заболевание"),
            mapOf("word_translate" to "sickness", "word_ru" to "болезнь"),
            mapOf("word_translate" to "patient", "word_ru" to "пациент"),
            mapOf("word_translate" to "doctor", "word_ru" to "врач"),
            mapOf("word_translate" to "physician", "word_ru" to "доктор"),
            mapOf("word_translate" to "surgeon", "word_ru" to "хирург"),
            mapOf("word_translate" to "nurse", "word_ru" to "медсестра"),
            mapOf("word_translate" to "hospital", "word_ru" to "больница"),
            mapOf("word_translate" to "clinic", "word_ru" to "клиника"),
            mapOf("word_translate" to "pharmacy", "word_ru" to "аптека"),
            mapOf("word_translate" to "drug", "word_ru" to "лекарство"),
            mapOf("word_translate" to "medicine", "word_ru" to "лекарство"),
            mapOf("word_translate" to "pill", "word_ru" to "таблетка"),
            mapOf("word_translate" to "tablet", "word_ru" to "таблетка"),
            mapOf("word_translate" to "capsule", "word_ru" to "капсула"),
            mapOf("word_translate" to "injection", "word_ru" to "инъекция"),
            mapOf("word_translate" to "vaccine", "word_ru" to "вакцина"),
            mapOf("word_translate" to "antibiotic", "word_ru" to "антибиотик"),
            mapOf("word_translate" to "vitamin", "word_ru" to "витамин"),
            mapOf("word_translate" to "mineral", "word_ru" to "минерал"),
            mapOf("word_translate" to "protein", "word_ru" to "белок"),
            mapOf("word_translate" to "carbohydrate", "word_ru" to "углевод"),
            mapOf("word_translate" to "fat", "word_ru" to "жир"),
            mapOf("word_translate" to "calorie", "word_ru" to "калория"),
            mapOf("word_translate" to "diet", "word_ru" to "диета"),
            mapOf("word_translate" to "nutrition", "word_ru" to "питание"),
            mapOf("word_translate" to "exercise", "word_ru" to "упражнение"),
            mapOf("word_translate" to "fitness", "word_ru" to "фитнес"),
            mapOf("word_translate" to "therapy", "word_ru" to "терапия"),
            mapOf("word_translate" to "treatment", "word_ru" to "лечение"),
            mapOf("word_translate" to "surgery", "word_ru" to "хирургия"),
            mapOf("word_translate" to "operation", "word_ru" to "операция"),
            mapOf("word_translate" to "anesthesia", "word_ru" to "анестезия"),
            mapOf("word_translate" to "pain", "word_ru" to "боль"),
            mapOf("word_translate" to "fever", "word_ru" to "лихорадка"),
            mapOf("word_translate" to "temperature", "word_ru" to "температура"),
            mapOf("word_translate" to "pressure", "word_ru" to "давление"),
            mapOf("word_translate" to "pulse", "word_ru" to "пульс"),
            mapOf("word_translate" to "heart", "word_ru" to "сердце"),
            mapOf("word_translate" to "blood", "word_ru" to "кровь"),
            mapOf("word_translate" to "vein", "word_ru" to "вена"),
            mapOf("word_translate" to "artery", "word_ru" to "артерия"),
            mapOf("word_translate" to "organ", "word_ru" to "орган"),
            mapOf("word_translate" to "tissue", "word_ru" to "ткань"),
            mapOf("word_translate" to "cell", "word_ru" to "клетка"),
            mapOf("word_translate" to "gene", "word_ru" to "ген"),
            mapOf("word_translate" to "dna", "word_ru" to "днк"),
            mapOf("word_translate" to "virus", "word_ru" to "вирус"),
            mapOf("word_translate" to "bacteria", "word_ru" to "бактерия"),
            mapOf("word_translate" to "infection", "word_ru" to "инфекция"),
            mapOf("word_translate" to "inflammation", "word_ru" to "воспаление"),
            mapOf("word_translate" to "allergy", "word_ru" to "аллергия"),
            mapOf("word_translate" to "cancer", "word_ru" to "рак"),
            mapOf("word_translate" to "tumor", "word_ru" to "опухоль"),
            mapOf("word_translate" to "fracture", "word_ru" to "перелом"),
            mapOf("word_translate" to "wound", "word_ru" to "рана"),
            mapOf("word_translate" to "burn", "word_ru" to "ожог"),
            mapOf("word_translate" to "cut", "word_ru" to "порез"),
            mapOf("word_translate" to "bruise", "word_ru" to "синяк"),
            mapOf("word_translate" to "swelling", "word_ru" to "отек"),
            mapOf("word_translate" to "rash", "word_ru" to "сыпь"),
            mapOf("word_translate" to "headache", "word_ru" to "головная боль"),
            mapOf("word_translate" to "cough", "word_ru" to "кашель"),
            mapOf("word_translate" to "sneeze", "word_ru" to "чихание"),
            mapOf("word_translate" to "breath", "word_ru" to "дыхание"),
            mapOf("word_translate" to "lung", "word_ru" to "легкое"),
            mapOf("word_translate" to "brain", "word_ru" to "мозг"),
            mapOf("word_translate" to "nerve", "word_ru" to "нерв"),
            mapOf("word_translate" to "muscle", "word_ru" to "мышца"),
            mapOf("word_translate" to "bone", "word_ru" to "кость"),
            mapOf("word_translate" to "skin", "word_ru" to "кожа"),
            mapOf("word_translate" to "eye", "word_ru" to "глаз"),
            mapOf("word_translate" to "ear", "word_ru" to "ухо"),
            mapOf("word_translate" to "nose", "word_ru" to "нос"),
            mapOf("word_translate" to "mouth", "word_ru" to "рот"),
            mapOf("word_translate" to "tooth", "word_ru" to "зуб"),
            mapOf("word_translate" to "stomach", "word_ru" to "желудок"),
            mapOf("word_translate" to "liver", "word_ru" to "печень"),
            mapOf("word_translate" to "kidney", "word_ru" to "почка"),
            mapOf("word_translate" to "intestine", "word_ru" to "кишечник"),
            mapOf("word_translate" to "diagnosis", "word_ru" to "диагноз"),
            mapOf("word_translate" to "symptom", "word_ru" to "симптом"),
            mapOf("word_translate" to "sign", "word_ru" to "признак"),
            mapOf("word_translate" to "test", "word_ru" to "тест"),
            mapOf("word_translate" to "analysis", "word_ru" to "анализ"),
            mapOf("word_translate" to "scan", "word_ru" to "сканирование"),
            mapOf("word_translate" to "xray", "word_ru" to "рентген"),
            mapOf("word_translate" to "ultrasound", "word_ru" to "ультразвук"),
            mapOf("word_translate" to "mri", "word_ru" to "мрт"),
            mapOf("word_translate" to "ct", "word_ru" to "кт"),
            mapOf("word_translate" to "ecg", "word_ru" to "экг"),
            mapOf("word_translate" to "eeg", "word_ru" to "ээг"),
            mapOf("word_translate" to "blood test", "word_ru" to "анализ крови"),
            mapOf("word_translate" to "urine test", "word_ru" to "анализ мочи"),
            mapOf("word_translate" to "biopsy", "word_ru" to "биопсия"),
            mapOf("word_translate" to "prevention", "word_ru" to "профилактика"),
            mapOf("word_translate" to "hygiene", "word_ru" to "гигиена"),
            mapOf("word_translate" to "sanitation", "word_ru" to "санитария"),
            mapOf("word_translate" to "disinfection", "word_ru" to "дезинфекция"),
            mapOf("word_translate" to "sterilization", "word_ru" to "стерилизация"),
            mapOf("word_translate" to "quarantine", "word_ru" to "карантин"),
            mapOf("word_translate" to "isolation", "word_ru" to "изоляция"),
            mapOf("word_translate" to "epidemic", "word_ru" to "эпидемия"),
            mapOf("word_translate" to "pandemic", "word_ru" to "пандемия"),
            mapOf("word_translate" to "vaccination", "word_ru" to "вакцинация"),
            mapOf("word_translate" to "immunity", "word_ru" to "иммунитет"),
            mapOf("word_translate" to "antibody", "word_ru" to "антитело"),
            mapOf("word_translate" to "hormone", "word_ru" to "гормон"),
            mapOf("word_translate" to "enzyme", "word_ru" to "фермент"),
            mapOf("word_translate" to "metabolism", "word_ru" to "метаболизм"),
            mapOf("word_translate" to "digestion", "word_ru" to "пищеварение"),
            mapOf("word_translate" to "respiration", "word_ru" to "дыхание"),
            mapOf("word_translate" to "circulation", "word_ru" to "кровообращение"),
            mapOf("word_translate" to "nervous system", "word_ru" to "нервная система"),
            mapOf("word_translate" to "immune system", "word_ru" to "иммунная система"),
            mapOf("word_translate" to "reproductive", "word_ru" to "репродуктивный"),
            mapOf("word_translate" to "pregnancy", "word_ru" to "беременность"),
            mapOf("word_translate" to "birth", "word_ru" to "рождение"),
            mapOf("word_translate" to "childbirth", "word_ru" to "роды"),
            mapOf("word_translate" to "contraception", "word_ru" to "контрацепция"),
            mapOf("word_translate" to "menopause", "word_ru" to "менопауза"),
            mapOf("word_translate" to "aging", "word_ru" to "старение"),
            mapOf("word_translate" to "death", "word_ru" to "смерть"),
            mapOf("word_translate" to "autopsy", "word_ru" to "вскрытие"),
            mapOf("word_translate" to "transplant", "word_ru" to "трансплантация"),
            mapOf("word_translate" to "prosthesis", "word_ru" to "протез"),
            mapOf("word_translate" to "implant", "word_ru" to "имплант"),
            mapOf("word_translate" to "device", "word_ru" to "устройство"),
            mapOf("word_translate" to "equipment", "word_ru" to "оборудование"),
            mapOf("word_translate" to "instrument", "word_ru" to "инструмент"),
            mapOf("word_translate" to "tool", "word_ru" to "инструмент"),
            mapOf("word_translate" to "machine", "word_ru" to "машина"),
            mapOf("word_translate" to "apparatus", "word_ru" to "аппарат"),
            mapOf("word_translate" to "system", "word_ru" to "система"),
            mapOf("word_translate" to "technology", "word_ru" to "технология"),
            mapOf("word_translate" to "technical", "word_ru" to "технический"),
            mapOf("word_translate" to "engineering", "word_ru" to "инженерия"),
            mapOf("word_translate" to "design", "word_ru" to "дизайн"),
            mapOf("word_translate" to "development", "word_ru" to "разработка"),
            mapOf("word_translate" to "production", "word_ru" to "производство"),
            mapOf("word_translate" to "manufacturing", "word_ru" to "производство"),
            mapOf("word_translate" to "assembly", "word_ru" to "сборка"),
            mapOf("word_translate" to "installation", "word_ru" to "установка"),
            mapOf("word_translate" to "maintenance", "word_ru" to "обслуживание"),
            mapOf("word_translate" to "repair", "word_ru" to "ремонт"),
            mapOf("word_translate" to "service", "word_ru" to "сервис"),
            mapOf("word_translate" to "operation", "word_ru" to "эксплуатация"),
            mapOf("word_translate" to "function", "word_ru" to "функция"),
            mapOf("word_translate" to "performance", "word_ru" to "производительность"),
            mapOf("word_translate" to "efficiency", "word_ru" to "эффективность"),
            mapOf("word_translate" to "capacity", "word_ru" to "емкость"),
            mapOf("word_translate" to "power", "word_ru" to "мощность"),
            mapOf("word_translate" to "energy", "word_ru" to "энергия"),
            mapOf("word_translate" to "electricity", "word_ru" to "электричество"),
            mapOf("word_translate" to "voltage", "word_ru" to "напряжение"),
            mapOf("word_translate" to "current", "word_ru" to "ток"),
            mapOf("word_translate" to "circuit", "word_ru" to "цепь"),
            mapOf("word_translate" to "wire", "word_ru" to "провод"),
            mapOf("word_translate" to "cable", "word_ru" to "кабель"),
            mapOf("word_translate" to "connector", "word_ru" to "коннектор"),
            mapOf("word_translate" to "switch", "word_ru" to "переключатель"),
            mapOf("word_translate" to "button", "word_ru" to "кнопка"),
            mapOf("word_translate" to "sensor", "word_ru" to "датчик"),
            mapOf("word_translate" to "detector", "word_ru" to "детектор"),
            mapOf("word_translate" to "transmitter", "word_ru" to "передатчик"),
            mapOf("word_translate" to "receiver", "word_ru" to "приемник"),
            mapOf("word_translate" to "antenna", "word_ru" to "антенна"),
            mapOf("word_translate" to "signal", "word_ru" to "сигнал"),
            mapOf("word_translate" to "frequency", "word_ru" to "частота"),
            mapOf("word_translate" to "wavelength", "word_ru" to "длина волны"),
            mapOf("word_translate" to "amplitude", "word_ru" to "амплитуда"),
            mapOf("word_translate" to "modulation", "word_ru" to "модуляция"),
            mapOf("word_translate" to "digital", "word_ru" to "цифровой"),
            mapOf("word_translate" to "analog", "word_ru" to "аналоговый"),
            mapOf("word_translate" to "binary", "word_ru" to "бинарный"),
            mapOf("word_translate" to "code", "word_ru" to "код"),
            mapOf("word_translate" to "program", "word_ru" to "программа"),
            mapOf("word_translate" to "software", "word_ru" to "программное обеспечение"),
            mapOf("word_translate" to "hardware", "word_ru" to "аппаратное обеспечение"),
            mapOf("word_translate" to "computer", "word_ru" to "компьютер"),
            mapOf("word_translate" to "processor", "word_ru" to "процессор"),
            mapOf("word_translate" to "memory", "word_ru" to "память"),
            mapOf("word_translate" to "storage", "word_ru" to "хранилище"),
            mapOf("word_translate" to "disk", "word_ru" to "диск"),
            mapOf("word_translate" to "drive", "word_ru" to "драйв"),
            mapOf("word_translate" to "network", "word_ru" to "сеть"),
            mapOf("word_translate" to "internet", "word_ru" to "интернет"),
            mapOf("word_translate" to "website", "word_ru" to "вебсайт"),
            mapOf("word_translate" to "server", "word_ru" to "сервер"),
            mapOf("word_translate" to "database", "word_ru" to "база данных"),
            mapOf("word_translate" to "algorithm", "word_ru" to "алгоритм"),
            mapOf("word_translate" to "interface", "word_ru" to "интерфейс"),
            mapOf("word_translate" to "display", "word_ru" to "дисплей"),
            mapOf("word_translate" to "screen", "word_ru" to "экран"),
            mapOf("word_translate" to "keyboard", "word_ru" to "клавиатура"),
            mapOf("word_translate" to "mouse", "word_ru" to "мышь"),
            mapOf("word_translate" to "printer", "word_ru" to "принтер"),
            mapOf("word_translate" to "scanner", "word_ru" to "сканер"),
            mapOf("word_translate" to "camera", "word_ru" to "камера"),
            mapOf("word_translate" to "microphone", "word_ru" to "микрофон"),
            mapOf("word_translate" to "speaker", "word_ru" to "динамик"),
            mapOf("word_translate" to "robot", "word_ru" to "робот"),
            mapOf("word_translate" to "automation", "word_ru" to "автоматизация"),
            mapOf("word_translate" to "control", "word_ru" to "управление"),
            mapOf("word_translate" to "monitoring", "word_ru" to "мониторинг"),
            mapOf("word_translate" to "measurement", "word_ru" to "измерение"),
            mapOf("word_translate" to "calibration", "word_ru" to "калибровка"),
            mapOf("word_translate" to "accuracy", "word_ru" to "точность"),
            mapOf("word_translate" to "precision", "word_ru" to "точность"),
            mapOf("word_translate" to "error", "word_ru" to "ошибка"),
            mapOf("word_translate" to "failure", "word_ru" to "сбой"),
            mapOf("word_translate" to "defect", "word_ru" to "дефект"),
            mapOf("word_translate" to "malfunction", "word_ru" to "неисправность"),
            mapOf("word_translate" to "breakdown", "word_ru" to "поломка"),
            mapOf("word_translate" to "troubleshooting", "word_ru" to "устранение неисправностей"),
            mapOf("word_translate" to "diagnostic", "word_ru" to "диагностический"),
            mapOf("word_translate" to "testing", "word_ru" to "тестирование"),
            mapOf("word_translate" to "inspection", "word_ru" to "инспекция"),
            mapOf("word_translate" to "quality", "word_ru" to "качество"),
            mapOf("word_translate" to "standard", "word_ru" to "стандарт"),
            mapOf("word_translate" to "specification", "word_ru" to "спецификация"),
            mapOf("word_translate" to "requirement", "word_ru" to "требование"),
            mapOf("word_translate" to "certification", "word_ru" to "сертификация"),
            mapOf("word_translate" to "safety", "word_ru" to "безопасность"),
            mapOf("word_translate" to "security", "word_ru" to "защита"),
            mapOf("word_translate" to "protection", "word_ru" to "защита"),
            mapOf("word_translate" to "risk", "word_ru" to "риск"),
            mapOf("word_translate" to "hazard", "word_ru" to "опасность"),
            mapOf("word_translate" to "emergency", "word_ru" to "чрезвычайная ситуация"),
            mapOf("word_translate" to "procedure", "word_ru" to "процедура"),
            mapOf("word_translate" to "protocol", "word_ru" to "протокол"),
            mapOf("word_translate" to "manual", "word_ru" to "руководство"),
            mapOf("word_translate" to "instruction", "word_ru" to "инструкция"),
            mapOf("word_translate" to "guide", "word_ru" to "руководство"),
            mapOf("word_translate" to "documentation", "word_ru" to "документация"),
            mapOf("word_translate" to "report", "word_ru" to "отчет"),
            mapOf("word_translate" to "record", "word_ru" to "запись"),
            mapOf("word_translate" to "data", "word_ru" to "данные"),
            mapOf("word_translate" to "information", "word_ru" to "информация"),
            mapOf("word_translate" to "knowledge", "word_ru" to "знание"),
            mapOf("word_translate" to "expertise", "word_ru" to "экспертиза"),
            mapOf("word_translate" to "skill", "word_ru" to "навык"),
            mapOf("word_translate" to "training", "word_ru" to "обучение"),
            mapOf("word_translate" to "education", "word_ru" to "образование"),
            mapOf("word_translate" to "certificate", "word_ru" to "сертификат"),
            mapOf("word_translate" to "diploma", "word_ru" to "диплом"),
            mapOf("word_translate" to "degree", "word_ru" to "степень"),
            mapOf("word_translate" to "qualification", "word_ru" to "квалификация"),
            mapOf("word_translate" to "profession", "word_ru" to "профессия"),
            mapOf("word_translate" to "career", "word_ru" to "карьера"),
            mapOf("word_translate" to "job", "word_ru" to "работа"),
            mapOf("word_translate" to "work", "word_ru" to "работа"),
            mapOf("word_translate" to "employment", "word_ru" to "трудоустройство"),
            mapOf("word_translate" to "unemployment", "word_ru" to "безработица"),
            mapOf("word_translate" to "salary", "word_ru" to "зарплата"),
            mapOf("word_translate" to "wage", "word_ru" to "заработная плата"),
            mapOf("word_translate" to "income", "word_ru" to "доход"),
            mapOf("word_translate" to "benefit", "word_ru" to "пособие"),
            mapOf("word_translate" to "insurance", "word_ru" to "страхование"),
            mapOf("word_translate" to "pension", "word_ru" to "пенсия"),
            mapOf("word_translate" to "retirement", "word_ru" to "пенсия"),
            mapOf("word_translate" to "vacation", "word_ru" to "отпуск"),
            mapOf("word_translate" to "holiday", "word_ru" to "праздник"),
            mapOf("word_translate" to "leave", "word_ru" to "отпуск"),
            mapOf("word_translate" to "absence", "word_ru" to "отсутствие"),
            mapOf("word_translate" to "attendance", "word_ru" to "посещаемость"),
            mapOf("word_translate" to "punctuality", "word_ru" to "пунктуальность"),
            mapOf("word_translate" to "discipline", "word_ru" to "дисциплина"),
            mapOf("word_translate" to "behavior", "word_ru" to "поведение"),
            mapOf("word_translate" to "conduct", "word_ru" to "поведение"),
            mapOf("word_translate" to "ethics", "word_ru" to "этика"),
            mapOf("word_translate" to "morality", "word_ru" to "мораль"),
            mapOf("word_translate" to "integrity", "word_ru" to "честность"),
            mapOf("word_translate" to "honesty", "word_ru" to "правдивость"),
            mapOf("word_translate" to "trust", "word_ru" to "доверие"),
            mapOf("word_translate" to "loyalty", "word_ru" to "верность"),
            mapOf("word_translate" to "commitment", "word_ru" to "приверженность"),
            mapOf("word_translate" to "dedication", "word_ru" to "преданность"),
            mapOf("word_translate" to "motivation", "word_ru" to "мотивация"),
            mapOf("word_translate" to "inspiration", "word_ru" to "вдохновение"),
            mapOf("word_translate" to "initiative", "word_ru" to "инициатива"),
            mapOf("word_translate" to "creativity", "word_ru" to "креативность"),
            mapOf("word_translate" to "innovation", "word_ru" to "новаторство"),
            mapOf("word_translate" to "adaptability", "word_ru" to "адаптивность"),
            mapOf("word_translate" to "flexibility", "word_ru" to "гибкость"),
            mapOf("word_translate" to "versatility", "word_ru" to "универсальность"),
            mapOf("word_translate" to "reliability", "word_ru" to "надежность"),
            mapOf("word_translate" to "dependability", "word_ru" to "надежность"),
            mapOf("word_translate" to "responsibility", "word_ru" to "ответственность"),
            mapOf("word_translate" to "accountability", "word_ru" to "подотчетность"),
            mapOf("word_translate" to "professionalism", "word_ru" to "профессионализм"),
            mapOf("word_translate" to "expertise", "word_ru" to "экспертиза"),
            mapOf("word_translate" to "competence", "word_ru" to "компетентность"),
            mapOf("word_translate" to "proficiency", "word_ru" to "мастерство"),
            mapOf("word_translate" to "skill", "word_ru" to "умение"),
            mapOf("word_translate" to "ability", "word_ru" to "способность"),
            mapOf("word_translate" to "talent", "word_ru" to "талант"),
            mapOf("word_translate" to "gift", "word_ru" to "дар"),
            mapOf("word_translate" to "experience", "word_ru" to "опыт"),
            mapOf("word_translate" to "knowledge", "word_ru" to "знание"),
            mapOf("word_translate" to "understanding", "word_ru" to "понимание"),
            mapOf("word_translate" to "awareness", "word_ru" to "осведомленность"),
            mapOf("word_translate" to "consciousness", "word_ru" to "сознание"),
            mapOf("word_translate" to "cognition", "word_ru" to "познание"),
            mapOf("word_translate" to "intelligence", "word_ru" to "интеллект"),
            mapOf("word_translate" to "wisdom", "word_ru" to "мудрость"),
            mapOf("word_translate" to "judgment", "word_ru" to "суждение"),
            mapOf("word_translate" to "decision", "word_ru" to "решение"),
            mapOf("word_translate" to "choice", "word_ru" to "выбор"),
            mapOf("word_translate" to "option", "word_ru" to "вариант"),
            mapOf("word_translate" to "alternative", "word_ru" to "альтернатива"),
            mapOf("word_translate" to "preference", "word_ru" to "предпочтение"),
            mapOf("word_translate" to "priority", "word_ru" to "приоритет"),
            mapOf("word_translate" to "importance", "word_ru" to "важность"),
            mapOf("word_translate" to "significance", "word_ru" to "значимость"),
            mapOf("word_translate" to "relevance", "word_ru" to "релевантность"),
            mapOf("word_translate" to "value", "word_ru" to "ценность"),
            mapOf("word_translate" to "worth", "word_ru" to "стоимость"),
            mapOf("word_translate" to "merit", "word_ru" to "заслуга"),
            mapOf("word_translate" to "advantage", "word_ru" to "преимущество"),
            mapOf("word_translate" to "benefit", "word_ru" to "выгода"),
            mapOf("word_translate" to "gain", "word_ru" to "приобретение"),
            mapOf("word_translate" to "profit", "word_ru" to "выгода"),
            mapOf("word_translate" to "loss", "word_ru" to "потеря"),
            mapOf("word_translate" to "risk", "word_ru" to "риск"),
            mapOf("word_translate" to "danger", "word_ru" to "опасность"),
            mapOf("word_translate" to "threat", "word_ru" to "угроза"),
            mapOf("word_translate" to "challenge", "word_ru" to "испытание"),
            mapOf("word_translate" to "difficulty", "word_ru" to "трудность"),
            mapOf("word_translate" to "problem", "word_ru" to "проблема"),
            mapOf("word_translate" to "issue", "word_ru" to "вопрос"),
            mapOf("word_translate" to "matter", "word_ru" to "дело"),
            mapOf("word_translate" to "subject", "word_ru" to "тема"),
            mapOf("word_translate" to "topic", "word_ru" to "предмет"),
            mapOf("word_translate" to "theme", "word_ru" to "тема"),
            mapOf("word_translate" to "content", "word_ru" to "содержание"),
            mapOf("word_translate" to "context", "word_ru" to "контекст"),
            mapOf("word_translate" to "background", "word_ru" to "фон"),
            mapOf("word_translate" to "environment", "word_ru" to "окружение"),
            mapOf("word_translate" to "atmosphere", "word_ru" to "атмосфера"),
            mapOf("word_translate" to "climate", "word_ru" to "климат"),
            mapOf("word_translate" to "culture", "word_ru" to "культура"),
            mapOf("word_translate" to "tradition", "word_ru" to "традиция"),
            mapOf("word_translate" to "custom", "word_ru" to "обычай"),
            mapOf("word_translate" to "practice", "word_ru" to "практика"),
            mapOf("word_translate" to "habit", "word_ru" to "привычка"),
            mapOf("word_translate" to "routine", "word_ru" to "рутина"),
            mapOf("word_translate" to "pattern", "word_ru" to "шаблон"),
            mapOf("word_translate" to "system", "word_ru" to "система"),
            mapOf("word_translate" to "structure", "word_ru" to "структура"),
            mapOf("word_translate" to "framework", "word_ru" to "каркас"),
            mapOf("word_translate" to "model", "word_ru" to "модель"),
            mapOf("word_translate" to "template", "word_ru" to "шаблон"),
            mapOf("word_translate" to "format", "word_ru" to "формат"),
            mapOf("word_translate" to "layout", "word_ru" to "макет"),
            mapOf("word_translate" to "design", "word_ru" to "дизайн"),
            mapOf("word_translate" to "style", "word_ru" to "стиль"),
            mapOf("word_translate" to "approach", "word_ru" to "подход"),
            mapOf("word_translate" to "method", "word_ru" to "метод"),
            mapOf("word_translate" to "technique", "word_ru" to "техника"),
            mapOf("word_translate" to "procedure", "word_ru" to "процедура"),
            mapOf("word_translate" to "process", "word_ru" to "процесс"),
            mapOf("word_translate" to "operation", "word_ru" to "операция"),
            mapOf("word_translate" to "action", "word_ru" to "действие"),
            mapOf("word_translate" to "activity", "word_ru" to "активность"),
            mapOf("word_translate" to "work", "word_ru" to "работа"),
            mapOf("word_translate" to "labor", "word_ru" to "труд"),
            mapOf("word_translate" to "effort", "word_ru" to "усилие"),
            mapOf("word_translate" to "energy", "word_ru" to "энергия"),
            mapOf("word_translate" to "time", "word_ru" to "время"),
            mapOf("word_translate" to "resource", "word_ru" to "ресурс"),
            mapOf("word_translate" to "asset", "word_ru" to "актив"),
            mapOf("word_translate" to "capital", "word_ru" to "капитал"),
            mapOf("word_translate" to "investment", "word_ru" to "вложение"),
            mapOf("word_translate" to "return", "word_ru" to "возврат"),
            mapOf("word_translate" to "yield", "word_ru" to "доходность"),
            mapOf("word_translate" to "profit", "word_ru" to "прибыль"),
            mapOf("word_translate" to "gain", "word_ru" to "прирост"),
            mapOf("word_translate" to "growth", "word_ru" to "рост"),
            mapOf("word_translate" to "development", "word_ru" to "развитие"),
            mapOf("word_translate" to "progress", "word_ru" to "прогресс"),
            mapOf("word_translate" to "advancement", "word_ru" to "продвижение"),
            mapOf("word_translate" to "improvement", "word_ru" to "улучшение"),
            mapOf("word_translate" to "enhancement", "word_ru" to "улучшение"),
            mapOf("word_translate" to "optimization", "word_ru" to "оптимизация"),
            mapOf("word_translate" to "maximization", "word_ru" to "максимизация"),
            mapOf("word_translate" to "minimization", "word_ru" to "минимизация"),
            mapOf("word_translate" to "reduction", "word_ru" to "сокращение"),
            mapOf("word_translate" to "increase", "word_ru" to "увеличение"),
            mapOf("word_translate" to "decrease", "word_ru" to "уменьшение"),
            mapOf("word_translate" to "change", "word_ru" to "изменение"),
            mapOf("word_translate" to "transformation", "word_ru" to "трансформация"),
            mapOf("word_translate" to "modification", "word_ru" to "модификация"),
            mapOf("word_translate" to "adjustment", "word_ru" to "корректировка"),
            mapOf("word_translate" to "adaptation", "word_ru" to "адаптация"),
            mapOf("word_translate" to "evolution", "word_ru" to "эволюция"),
            mapOf("word_translate" to "revolution", "word_ru" to "революция"),
            mapOf("word_translate" to "innovation", "word_ru" to "нововведение"),
            mapOf("word_translate" to "invention", "word_ru" to "изобретение"),
            mapOf("word_translate" to "discovery", "word_ru" to "открытие"),
            mapOf("word_translate" to "creation", "word_ru" to "создание"),
            mapOf("word_translate" to "production", "word_ru" to "производство"),
            mapOf("word_translate" to "manufacturing", "word_ru" to "производство"),
            mapOf("word_translate" to "assembly", "word_ru" to "сборка"),
            mapOf("word_translate" to "construction", "word_ru" to "строительство"),
            mapOf("word_translate" to "building", "word_ru" to "строительство"),
            mapOf("word_translate" to "development", "word_ru" to "разработка"),
            mapOf("word_translate" to "design", "word_ru" to "проектирование"),
            mapOf("word_translate" to "engineering", "word_ru" to "инженерия"),
            mapOf("word_translate" to "technology", "word_ru" to "технология"),
            mapOf("word_translate" to "science", "word_ru" to "наука"),
            mapOf("word_translate" to "research", "word_ru" to "исследование"),
            mapOf("word_translate" to "study", "word_ru" to "изучение"),
            mapOf("word_translate" to "analysis", "word_ru" to "анализ"),
            mapOf("word_translate" to "synthesis", "word_ru" to "синтез"),
            mapOf("word_translate" to "evaluation", "word_ru" to "оценка"),
            mapOf("word_translate" to "assessment", "word_ru" to "оценивание"),
            mapOf("word_translate" to "measurement", "word_ru" to "измерение"),
            mapOf("word_translate" to "calculation", "word_ru" to "расчет"),
            mapOf("word_translate" to "computation", "word_ru" to "вычисление"),
            mapOf("word_translate" to "estimation", "word_ru" to "оценка"),
            mapOf("word_translate" to "research", "word_ru" to "исследование"),
            mapOf("word_translate" to "study", "word_ru" to "изучение"),
            mapOf("word_translate" to "analysis", "word_ru" to "анализ"),
            mapOf("word_translate" to "synthesis", "word_ru" to "синтез"),
            mapOf("word_translate" to "evaluation", "word_ru" to "оценка"),
            mapOf("word_translate" to "assessment", "word_ru" to "оценивание"),
            mapOf("word_translate" to "measurement", "word_ru" to "измерение"),
            mapOf("word_translate" to "calculation", "word_ru" to "расчет"),
            mapOf("word_translate" to "computation", "word_ru" to "вычисление"),
            mapOf("word_translate" to "estimation", "word_ru" to "оценка"),
            mapOf("word_translate" to "forecast", "word_ru" to "прогноз"),
            mapOf("word_translate" to "prediction", "word_ru" to "предсказание"),
            mapOf("word_translate" to "planning", "word_ru" to "планирование"),
            mapOf("word_translate" to "scheduling", "word_ru" to "составление графика"),
            mapOf("word_translate" to "organization", "word_ru" to "организация"),
            mapOf("word_translate" to "coordination", "word_ru" to "координация"),
            mapOf("word_translate" to "management", "word_ru" to "управление"),
            mapOf("word_translate" to "administration", "word_ru" to "администрирование"),
            mapOf("word_translate" to "supervision", "word_ru" to "надзор"),
            mapOf("word_translate" to "leadership", "word_ru" to "лидерство"),
            mapOf("word_translate" to "direction", "word_ru" to "руководство"),
            mapOf("word_translate" to "guidance", "word_ru" to "руководство"),
            mapOf("word_translate" to "advice", "word_ru" to "совет"),
            mapOf("word_translate" to "counsel", "word_ru" to "консультация"),
            mapOf("word_translate" to "consultation", "word_ru" to "консультация"),
            mapOf("word_translate" to "recommendation", "word_ru" to "рекомендация"),
            mapOf("word_translate" to "suggestion", "word_ru" to "предложение"),
            mapOf("word_translate" to "proposal", "word_ru" to "предложение"),
            mapOf("word_translate" to "offer", "word_ru" to "предложение"),
            mapOf("word_translate" to "bid", "word_ru" to "ставка"),
            mapOf("word_translate" to "tender", "word_ru" to "тендер"),
            mapOf("word_translate" to "application", "word_ru" to "заявка"),
            mapOf("word_translate" to "request", "word_ru" to "запрос"),
            mapOf("word_translate" to "requirement", "word_ru" to "требование"),
            mapOf("word_translate" to "demand", "word_ru" to "требование"),
            mapOf("word_translate" to "need", "word_ru" to "потребность"),
            mapOf("word_translate" to "necessity", "word_ru" to "необходимость"),
            mapOf("word_translate" to "essential", "word_ru" to "существенный"),
            mapOf("word_translate" to "important", "word_ru" to "важный"),
            mapOf("word_translate" to "critical", "word_ru" to "критический"),
            mapOf("word_translate" to "crucial", "word_ru" to "решающий"),
            mapOf("word_translate" to "vital", "word_ru" to "жизненно важный"),
            mapOf("word_translate" to "fundamental", "word_ru" to "фундаментальный"),
            mapOf("word_translate" to "basic", "word_ru" to "базовый"),
            mapOf("word_translate" to "primary", "word_ru" to "первичный"),
            mapOf("word_translate" to "principal", "word_ru" to "главный"),
            mapOf("word_translate" to "main", "word_ru" to "основной"),
            mapOf("word_translate" to "major", "word_ru" to "основной"),
            mapOf("word_translate" to "key", "word_ru" to "ключевой"),
            mapOf("word_translate" to "central", "word_ru" to "центральный"),
            mapOf("word_translate" to "core", "word_ru" to "основной"),
            mapOf("word_translate" to "essential", "word_ru" to "сущностный"),
            mapOf("word_translate" to "necessary", "word_ru" to "необходимый"),
            mapOf("word_translate" to "required", "word_ru" to "требуемый"),
            mapOf("word_translate" to "mandatory", "word_ru" to "обязательный"),
            mapOf("word_translate" to "compulsory", "word_ru" to "принудительный"),
            mapOf("word_translate" to "obligatory", "word_ru" to "обязательный"),
            mapOf("word_translate" to "voluntary", "word_ru" to "добровольный"),
            mapOf("word_translate" to "optional", "word_ru" to "необязательный"),
            mapOf("word_translate" to "additional", "word_ru" to "дополнительный"),
            mapOf("word_translate" to "extra", "word_ru" to "дополнительный"),
            mapOf("word_translate" to "supplementary", "word_ru" to "дополняющий"),
            mapOf("word_translate" to "complementary", "word_ru" to "дополняющий"),
            mapOf("word_translate" to "alternative", "word_ru" to "альтернативный"),
            mapOf("word_translate" to "different", "word_ru" to "различный"),
            mapOf("word_translate" to "various", "word_ru" to "разнообразный"),
        )

        println("words1 = " + words.size) //260 239
        words = words.distinctBy { it["word_translate"] }.distinctBy { it["word_ru"] }.filter { it["word_ru"].toString().length < 15 }
        println("words2 = " + words.size)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


        /*scope.launch {


            val data = hashMapOf(
                "description" to "B1 Медицинский и технический",
                "starsToEnable" to 480,
                "imageUrl" to "/way",
                "lessonsCount" to 25, //17 //13
                "name" to "Модуль 17",
            )

            val module = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules").add(data).await()
            repeat(25) { index ->

                val batch = db.batch()

                val lessonData = mapOf(
                    "name" to "lesson ${index + 1}",
                    "type" to if (index == 24) "LAST_GAME" else if (index % 2 == 0) "DEFAULT" else "SWAP"
                )

                val doc = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                    .document(module.id)
                    .collection("lessons").document("lesson ${index + 1}")// авто id

                batch.set(doc, lessonData)


                words.shuffled().take(if (index == 24) 100 else 50).forEach { word ->

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

