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

            mapOf("word_translate" to "law", "word_ru" to "закон"),
            mapOf("word_translate" to "legal", "word_ru" to "юридический"),
            mapOf("word_translate" to "justice", "word_ru" to "справедливость"),
            mapOf("word_translate" to "court", "word_ru" to "суд"),
            mapOf("word_translate" to "attorney", "word_ru" to "адвокат"),
            mapOf("word_translate" to "prosecution", "word_ru" to "обвинение"),
            mapOf("word_translate" to "defense", "word_ru" to "защита"),
            mapOf("word_translate" to "trial", "word_ru" to "судебный процесс"),
            mapOf("word_translate" to "verdict", "word_ru" to "приговор"),
            mapOf("word_translate" to "appeal", "word_ru" to "апелляция"),
            mapOf("word_translate" to "evidence", "word_ru" to "доказательство"),
            mapOf("word_translate" to "witness", "word_ru" to "свидетель"),
            mapOf("word_translate" to "contract", "word_ru" to "контракт"),
            mapOf("word_translate" to "agreement", "word_ru" to "соглашение"),
            mapOf("word_translate" to "liability", "word_ru" to "ответственность"),
            mapOf("word_translate" to "negligence", "word_ru" to "халатность"),
            mapOf("word_translate" to "fraud", "word_ru" to "мошенничество"),
            mapOf("word_translate" to "compliance", "word_ru" to "соответствие"),
            mapOf("word_translate" to "regulation", "word_ru" to "регулирование"),
            mapOf("word_translate" to "legislation", "word_ru" to "законодательство"),
            mapOf("word_translate" to "precedent", "word_ru" to "прецедент"),
            mapOf("word_translate" to "jurisdiction", "word_ru" to "юрисдикция"),
            mapOf("word_translate" to "plaintiff", "word_ru" to "истец"),
            mapOf("word_translate" to "defendant", "word_ru" to "ответчик"),
            mapOf("word_translate" to "sentence", "word_ru" to "приговор"),
            mapOf("word_translate" to "appeal", "word_ru" to "обжалование"),
            mapOf("word_translate" to "prosecutor", "word_ru" to "прокурор"),
            mapOf("word_translate" to "litigation", "word_ru" to "судебный процесс"),
            mapOf("word_translate" to "arbitration", "word_ru" to "арбитраж"),
            mapOf("word_translate" to "mediation", "word_ru" to "медиация"),
            mapOf("word_translate" to "settlement", "word_ru" to "урегулирование"),
            mapOf("word_translate" to "breach", "word_ru" to "нарушение"),
            mapOf("word_translate" to "provision", "word_ru" to "положение"),
            mapOf("word_translate" to "clause", "word_ru" to "статья"),
            mapOf("word_translate" to "litigant", "word_ru" to "участник процесса"),
            mapOf("word_translate" to "attestation", "word_ru" to "заверение"),
            mapOf("word_translate" to "injunction", "word_ru" to "судебный запрет"),
            mapOf("word_translate" to "settlement", "word_ru" to "компромисс"),
            mapOf("word_translate" to "arrest", "word_ru" to "арест"),
            mapOf("word_translate" to "appeal court", "word_ru" to "апелляционный суд"),
            mapOf("word_translate" to "bar association", "word_ru" to "ассоциация адвокатов"),
            mapOf("word_translate" to "bylaw", "word_ru" to "подзаконный акт"),
            mapOf("word_translate" to "charter", "word_ru" to "устав"),
            mapOf("word_translate" to "compliance officer", "word_ru" to "специалист по соблюдению правил"),
            mapOf("word_translate" to "conflict of interest", "word_ru" to "конфликт интересов"),
            mapOf("word_translate" to "court order", "word_ru" to "судебный приказ"),
            mapOf("word_translate" to "custody", "word_ru" to "опека"),
            mapOf("word_translate" to "damages", "word_ru" to "убытки"),
            mapOf("word_translate" to "deposition", "word_ru" to "показания под присягой"),
            mapOf("word_translate" to "enforcement", "word_ru" to "исполнение"),
            mapOf("word_translate" to "equity", "word_ru" to "справедливость"),
            mapOf("word_translate" to "evidence-based", "word_ru" to "основанный на доказательствах"),
            mapOf("word_translate" to "fiduciary", "word_ru" to "фидуциарный"),
            mapOf("word_translate" to "forensic", "word_ru" to "судебно-экспертный"),
            mapOf("word_translate" to "grievance", "word_ru" to "жалоба"),
            mapOf("word_translate" to "hearing", "word_ru" to "слушание"),
            mapOf("word_translate" to "indictment", "word_ru" to "обвинительное заключение"),
            mapOf("word_translate" to "injury", "word_ru" to "ущерб"),
            mapOf("word_translate" to "judgment", "word_ru" to "судебное решение"),
            mapOf("word_translate" to "jurisprudence", "word_ru" to "юриспруденция"),
            mapOf("word_translate" to "legality", "word_ru" to "законность"),
            mapOf("word_translate" to "litigate", "word_ru" to "вести судебный процесс"),
            mapOf("word_translate" to "malpractice", "word_ru" to "профессиональная халатность"),
            mapOf("word_translate" to "mediation", "word_ru" to "медиация"),
            mapOf("word_translate" to "notary", "word_ru" to "нотариус"),
            mapOf("word_translate" to "obligation", "word_ru" to "обязанность"),
            mapOf("word_translate" to "ordinance", "word_ru" to "правовой акт"),
            mapOf("word_translate" to "paralegal", "word_ru" to "помощник юриста"),
            mapOf("word_translate" to "penalty", "word_ru" to "штраф, наказание"),
            mapOf("word_translate" to "plaintiff", "word_ru" to "истец"),
            mapOf("word_translate" to "precedent", "word_ru" to "прецедент"),
            mapOf("word_translate" to "prosecution", "word_ru" to "обвинение"),
            mapOf("word_translate" to "ratification", "word_ru" to "ратификация"),
            mapOf("word_translate" to "regulation", "word_ru" to "регламент"),
            mapOf("word_translate" to "reinstatement", "word_ru" to "восстановление"),
            mapOf("word_translate" to "settlement", "word_ru" to "урегулирование"),
            mapOf("word_translate" to "statute", "word_ru" to "закон, статут"),
            mapOf("word_translate" to "subpoena", "word_ru" to "повестка в суд"),
            mapOf("word_translate" to "testimony", "word_ru" to "свидетельские показания"),
            mapOf("word_translate" to "tort", "word_ru" to "деликт"),
            mapOf("word_translate" to "verdict", "word_ru" to "приговор"),
            mapOf("word_translate" to "violation", "word_ru" to "нарушение"),
            mapOf("word_translate" to "warrant", "word_ru" to "ордер"),
            mapOf("word_translate" to "witness", "word_ru" to "свидетель"),
            mapOf("word_translate" to "arbitrator", "word_ru" to "арбитр"),
            mapOf("word_translate" to "brief", "word_ru" to "юридическое заключение"),
            mapOf("word_translate" to "case law", "word_ru" to "судебная практика"),
            mapOf("word_translate" to "civil law", "word_ru" to "гражданское право"),
            mapOf("word_translate" to "common law", "word_ru" to "система прецедентного права"),
            mapOf("word_translate" to "constitutional", "word_ru" to "конституционный"),
            mapOf("word_translate" to "decree", "word_ru" to "указ"),
            mapOf("word_translate" to "defamation", "word_ru" to "клевета"),
            mapOf("word_translate" to "equity", "word_ru" to "справедливость"),
            mapOf("word_translate" to "exoneration", "word_ru" to "оправдание"),
            mapOf("word_translate" to "filing", "word_ru" to "подача документа"),
            mapOf("word_translate" to "habeas corpus", "word_ru" to "право на судебное разбирательство"),
            mapOf("word_translate" to "injunction", "word_ru" to "судебный запрет"),
            mapOf("word_translate" to "juror", "word_ru" to "присяжный"),
            mapOf("word_translate" to "litigation", "word_ru" to "судебное разбирательство"),
            mapOf("word_translate" to "plea", "word_ru" to "заявление, признание"),
            mapOf("word_translate" to "rebuttal", "word_ru" to "опровержение"),
            mapOf("word_translate" to "remedy", "word_ru" to "правовое средство"),
            mapOf("word_translate" to "sentence", "word_ru" to "приговор"),
            mapOf("word_translate" to "substantive law", "word_ru" to "материальное право"),
            mapOf("word_translate" to "testator", "word_ru" to "завещатель"),
            mapOf("word_translate" to "jurisprudence", "word_ru" to "правоведение"),
            mapOf("word_translate" to "adjudication", "word_ru" to "судебное решение"),
            mapOf("word_translate" to "arbitration", "word_ru" to "арбитраж"),
            mapOf("word_translate" to "litigation", "word_ru" to "судебный процесс"),
            mapOf("word_translate" to "mediation", "word_ru" to "посредничество"),
            mapOf("word_translate" to "conciliation", "word_ru" to "примирение"),
            mapOf("word_translate" to "jurisdiction", "word_ru" to "юрисдикция"),
            mapOf("word_translate" to "venue", "word_ru" to "место рассмотрения"),
            mapOf("word_translate" to "forum", "word_ru" to "форум"),
            mapOf("word_translate" to "precedent", "word_ru" to "прецедент"),
            mapOf("word_translate" to "stare decisis", "word_ru" to "судебный прецедент"),
            mapOf("word_translate" to "obiter dictum", "word_ru" to "попутное замечание"),
            mapOf("word_translate" to "ratio decidendi", "word_ru" to "основания решения"),
            mapOf("word_translate" to "dictum", "word_ru" to "изречение"),
            mapOf("word_translate" to "holding", "word_ru" to "правовая позиция"),
            mapOf("word_translate" to "ruling", "word_ru" to "постановление"),
            mapOf("word_translate" to "judgment", "word_ru" to "судебное решение"),
            mapOf("word_translate" to "verdict", "word_ru" to "вердикт"),
            mapOf("word_translate" to "sentence", "word_ru" to "приговор"),
            mapOf("word_translate" to "decree", "word_ru" to "декрет"),
            mapOf("word_translate" to "order", "word_ru" to "приказ"),
            mapOf("word_translate" to "injunction", "word_ru" to "судебный запрет"),
            mapOf("word_translate" to "writ", "word_ru" to "судебный приказ"),
            mapOf("word_translate" to "summons", "word_ru" to "судебная повестка"),
            mapOf("word_translate" to "subpoena", "word_ru" to "судебная повестка"),
            mapOf("word_translate" to "warrant", "word_ru" to "ордер"),
            mapOf("word_translate" to "affidavit", "word_ru" to "письменное показание"),
            mapOf("word_translate" to "deposition", "word_ru" to "показание под присягой"),
            mapOf("word_translate" to "testimony", "word_ru" to "показание"),
            mapOf("word_translate" to "evidence", "word_ru" to "доказательство"),
            mapOf("word_translate" to "exhibit", "word_ru" to "вещественное доказательство"),
            mapOf("word_translate" to "discovery", "word_ru" to "раскрытие доказательств"),
            mapOf("word_translate" to "disclosure", "word_ru" to "раскрытие информации"),
            mapOf("word_translate" to "pleading", "word_ru" to "исковое заявление"),
            mapOf("word_translate" to "complaint", "word_ru" to "жалоба"),
            mapOf("word_translate" to "petition", "word_ru" to "петиция"),
            mapOf("word_translate" to "motion", "word_ru" to "ходатайство"),
            mapOf("word_translate" to "brief", "word_ru" to "докладная записка"),
            mapOf("word_translate" to "memorandum", "word_ru" to "меморандум"),
            mapOf("word_translate" to "argument", "word_ru" to "аргумент"),
            mapOf("word_translate" to "contention", "word_ru" to "утверждение"),
            mapOf("word_translate" to "allegation", "word_ru" to "утверждение"),
            mapOf("word_translate" to "claim", "word_ru" to "требование"),
            mapOf("word_translate" to "counterclaim", "word_ru" to "встречный иск"),
            mapOf("word_translate" to "crossclaim", "word_ru" to "перекрестный иск"),
            mapOf("word_translate" to "defense", "word_ru" to "защита"),
            mapOf("word_translate" to "rebuttal", "word_ru" to "опровержение"),
            mapOf("word_translate" to "rejoinder", "word_ru" to "возражение"),
            mapOf("word_translate" to "surrebuttal", "word_ru" to "вторичное возражение"),
            mapOf("word_translate" to "demurrer", "word_ru" to "возражение по существу"),
            mapOf("word_translate" to "objection", "word_ru" to "возражение"),
            mapOf("word_translate" to "exception", "word_ru" to "исключение"),
            mapOf("word_translate" to "appeal", "word_ru" to "апелляция"),
            mapOf("word_translate" to "review", "word_ru" to "пересмотр"),
            mapOf("word_translate" to "rehearing", "word_ru" to "повторное слушание"),
            mapOf("word_translate" to "retrial", "word_ru" to "повторный процесс"),
            mapOf("word_translate" to "prosecution", "word_ru" to "обвинение"),
            mapOf("word_translate" to "indictment", "word_ru" to "обвинительный акт"),
            mapOf("word_translate" to "arraignment", "word_ru" to "предъявление обвинения"),
            mapOf("word_translate" to "bail", "word_ru" to "залог"),
            mapOf("word_translate" to "parole", "word_ru" to "условно-досрочное"),
            mapOf("word_translate" to "probation", "word_ru" to "испытательный срок"),
            mapOf("word_translate" to "commutation", "word_ru" to "смягчение наказания"),
            mapOf("word_translate" to "pardon", "word_ru" to "помилование"),
            mapOf("word_translate" to "amnesty", "word_ru" to "амнистия"),
            mapOf("word_translate" to "extradition", "word_ru" to "экстрадиция"),
            mapOf("word_translate" to "deportation", "word_ru" to "депортация"),
            mapOf("word_translate" to "expatriation", "word_ru" to "лишение гражданства"),
            mapOf("word_translate" to "naturalization", "word_ru" to "натурализация"),
            mapOf("word_translate" to "citizenship", "word_ru" to "гражданство"),
            mapOf("word_translate" to "domicile", "word_ru" to "место жительства"),
            mapOf("word_translate" to "residence", "word_ru" to "место проживания"),
            mapOf("word_translate" to "nationality", "word_ru" to "национальность"),
            mapOf("word_translate" to "sovereignty", "word_ru" to "суверенитет"),
            mapOf("word_translate" to "immunity", "word_ru" to "иммунитет"),
            mapOf("word_translate" to "diplomacy", "word_ru" to "дипломатия"),
            mapOf("word_translate" to "treaty", "word_ru" to "договор"),
            mapOf("word_translate" to "convention", "word_ru" to "конвенция"),
            mapOf("word_translate" to "protocol", "word_ru" to "протокол"),
            mapOf("word_translate" to "accord", "word_ru" to "соглашение"),
            mapOf("word_translate" to "pact", "word_ru" to "пакт"),
            mapOf("word_translate" to "covenant", "word_ru" to "завет"),
            mapOf("word_translate" to "charter", "word_ru" to "хартия"),
            mapOf("word_translate" to "statute", "word_ru" to "статут"),
            mapOf("word_translate" to "legislation", "word_ru" to "законодательство"),
            mapOf("word_translate" to "regulation", "word_ru" to "регламент"),
            mapOf("word_translate" to "ordinance", "word_ru" to "постановление"),
            mapOf("word_translate" to "by-law", "word_ru" to "подзаконный акт"),
            mapOf("word_translate" to "resolution", "word_ru" to "резолюция"),
            mapOf("word_translate" to "proclamation", "word_ru" to "провозглашение"),
            mapOf("word_translate" to "decree", "word_ru" to "указ"),
            mapOf("word_translate" to "edict", "word_ru" to "эдикт"),
            mapOf("word_translate" to "mandate", "word_ru" to "мандат"),
            mapOf("word_translate" to "directive", "word_ru" to "директива"),
            mapOf("word_translate" to "instruction", "word_ru" to "инструкция"),
            mapOf("word_translate" to "guideline", "word_ru" to "руководство"),
            mapOf("word_translate" to "policy", "word_ru" to "политика"),
            mapOf("word_translate" to "procedure", "word_ru" to "процедура"),
            mapOf("word_translate" to "process", "word_ru" to "процесс"),
            mapOf("word_translate" to "mechanism", "word_ru" to "механизм"),
            mapOf("word_translate" to "framework", "word_ru" to "структура"),
            mapOf("word_translate" to "system", "word_ru" to "система"),
            mapOf("word_translate" to "structure", "word_ru" to "структура"),
            mapOf("word_translate" to "hierarchy", "word_ru" to "иерархия"),
            mapOf("word_translate" to "bureaucracy", "word_ru" to "бюрократия"),
            mapOf("word_translate" to "administration", "word_ru" to "администрация"),
            mapOf("word_translate" to "governance", "word_ru" to "управление"),
            mapOf("word_translate" to "regulation", "word_ru" to "регулирование"),
            mapOf("word_translate" to "supervision", "word_ru" to "надзор"),
            mapOf("word_translate" to "oversight", "word_ru" to "контроль"),
            mapOf("word_translate" to "monitoring", "word_ru" to "мониторинг"),
            mapOf("word_translate" to "audit", "word_ru" to "аудит"),
            mapOf("word_translate" to "inspection", "word_ru" to "инспекция"),
            mapOf("word_translate" to "investigation", "word_ru" to "расследование"),
            mapOf("word_translate" to "inquiry", "word_ru" to "расследование"),
            mapOf("word_translate" to "probe", "word_ru" to "расследование"),
            mapOf("word_translate" to "scrutiny", "word_ru" to "тщательная проверка"),
            mapOf("word_translate" to "examination", "word_ru" to "исследование"),
            mapOf("word_translate" to "analysis", "word_ru" to "анализ"),
            mapOf("word_translate" to "assessment", "word_ru" to "оценка"),
            mapOf("word_translate" to "evaluation", "word_ru" to "оценивание"),
            mapOf("word_translate" to "appraisal", "word_ru" to "оценка"),
            mapOf("word_translate" to "valuation", "word_ru" to "оценка стоимости"),
            mapOf("word_translate" to "estimation", "word_ru" to "оценка"),
            mapOf("word_translate" to "calculation", "word_ru" to "расчет"),
            mapOf("word_translate" to "computation", "word_ru" to "вычисление"),
            mapOf("word_translate" to "accounting", "word_ru" to "бухгалтерия"),
            mapOf("word_translate" to "bookkeeping", "word_ru" to "ведение книг"),
            mapOf("word_translate" to "auditing", "word_ru" to "аудирование"),
            mapOf("word_translate" to "reporting", "word_ru" to "отчетность"),
            mapOf("word_translate" to "filing", "word_ru" to "подача документов"),
            mapOf("word_translate" to "registration", "word_ru" to "регистрация"),
            mapOf("word_translate" to "recording", "word_ru" to "запись"),
            mapOf("word_translate" to "documentation", "word_ru" to "документирование"),
            mapOf("word_translate" to "certification", "word_ru" to "сертификация"),
            mapOf("word_translate" to "authentication", "word_ru" to "аутентификация"),
            mapOf("word_translate" to "verification", "word_ru" to "верификация"),
            mapOf("word_translate" to "validation", "word_ru" to "валидация"),
            mapOf("word_translate" to "accreditation", "word_ru" to "аккредитация"),
            mapOf("word_translate" to "licensing", "word_ru" to "лицензирование"),
            mapOf("word_translate" to "permitting", "word_ru" to "разрешение"),
            mapOf("word_translate" to "authorization", "word_ru" to "авторизация"),
            mapOf("word_translate" to "empowerment", "word_ru" to "наделение полномочиями"),
            mapOf("word_translate" to "delegation", "word_ru" to "делегирование"),
            mapOf("word_translate" to "assignment", "word_ru" to "назначение"),
            mapOf("word_translate" to "appointment", "word_ru" to "назначение"),
            mapOf("word_translate" to "nomination", "word_ru" to "номинация"),
            mapOf("word_translate" to "election", "word_ru" to "выборы"),
            mapOf("word_translate" to "selection", "word_ru" to "отбор"),
            mapOf("word_translate" to "designation", "word_ru" to "обозначение"),
            mapOf("word_translate" to "specification", "word_ru" to "спецификация"),
            mapOf("word_translate" to "qualification", "word_ru" to "квалификация"),
            mapOf("word_translate" to "requirement", "word_ru" to "требование"),
            mapOf("word_translate" to "prerequisite", "word_ru" to "предварительное условие"),
            mapOf("word_translate" to "condition", "word_ru" to "условие"),
            mapOf("word_translate" to "stipulation", "word_ru" to "условие"),
            mapOf("word_translate" to "provision", "word_ru" to "положение"),
            mapOf("word_translate" to "clause", "word_ru" to "пункт"),
            mapOf("word_translate" to "article", "word_ru" to "статья"),
            mapOf("word_translate" to "section", "word_ru" to "раздел"),
            mapOf("word_translate" to "subsection", "word_ru" to "подраздел"),
            mapOf("word_translate" to "paragraph", "word_ru" to "параграф"),
            mapOf("word_translate" to "subparagraph", "word_ru" to "подпараграф"),
            mapOf("word_translate" to "item", "word_ru" to "пункт"),
            mapOf("word_translate" to "point", "word_ru" to "пункт"),
            mapOf("word_translate" to "term", "word_ru" to "термин"),
            mapOf("word_translate" to "definition", "word_ru" to "определение"),
            mapOf("word_translate" to "interpretation", "word_ru" to "толкование"),
            mapOf("word_translate" to "construction", "word_ru" to "толкование"),
            mapOf("word_translate" to "meaning", "word_ru" to "значение"),
            mapOf("word_translate" to "significance", "word_ru" to "значимость"),
            mapOf("word_translate" to "implication", "word_ru" to "последствие"),
            mapOf("word_translate" to "connotation", "word_ru" to "коннотация"),
            mapOf("word_translate" to "denotation", "word_ru" to "денотация"),
            mapOf("word_translate" to "nuance", "word_ru" to "нюанс"),
            mapOf("word_translate" to "subtlety", "word_ru" to "тонкость"),
            mapOf("word_translate" to "distinction", "word_ru" to "различие"),
            mapOf("word_translate" to "difference", "word_ru" to "разница"),
            mapOf("word_translate" to "discrepancy", "word_ru" to "несоответствие"),
            mapOf("word_translate" to "disparity", "word_ru" to "неравенство"),
            mapOf("word_translate" to "inequality", "word_ru" to "неравенство"),
            mapOf("word_translate" to "disproportion", "word_ru" to "диспропорция"),
            mapOf("word_translate" to "imbalance", "word_ru" to "дисбаланс"),
            mapOf("word_translate" to "bias", "word_ru" to "предвзятость"),
            mapOf("word_translate" to "prejudice", "word_ru" to "предубеждение"),
            mapOf("word_translate" to "discrimination", "word_ru" to "дискриминация"),
            mapOf("word_translate" to "segregation", "word_ru" to "сегрегация"),
            mapOf("word_translate" to "exclusion", "word_ru" to "исключение"),
            mapOf("word_translate" to "marginalization", "word_ru" to "маргинализация"),
            mapOf("word_translate" to "oppression", "word_ru" to "угнетение"),
            mapOf("word_translate" to "suppression", "word_ru" to "подавление"),
            mapOf("word_translate" to "repression", "word_ru" to "репрессия"),
            mapOf("word_translate" to "persecution", "word_ru" to "преследование"),
            mapOf("word_translate" to "harassment", "word_ru" to "домогательство"),
            mapOf("word_translate" to "intimidation", "word_ru" to "запугивание"),
            mapOf("word_translate" to "coercion", "word_ru" to "принуждение"),
            mapOf("word_translate" to "duress", "word_ru" to "принуждение"),
            mapOf("word_translate" to "compulsion", "word_ru" to "принуждение"),
            mapOf("word_translate" to "force", "word_ru" to "сила"),
            mapOf("word_translate" to "violence", "word_ru" to "насилие"),
            mapOf("word_translate" to "aggression", "word_ru" to "агрессия"),
            mapOf("word_translate" to "assault", "word_ru" to "нападение"),
            mapOf("word_translate" to "battery", "word_ru" to "побои"),
            mapOf("word_translate" to "abuse", "word_ru" to "жестокое обращение"),
            mapOf("word_translate" to "mistreatment", "word_ru" to "плохое обращение"),
            mapOf("word_translate" to "neglect", "word_ru" to "пренебрежение"),
            mapOf("word_translate" to "abandonment", "word_ru" to "оставление"),
            mapOf("word_translate" to "desertion", "word_ru" to "дезертирство"),
            mapOf("word_translate" to "betrayal", "word_ru" to "предательство"),
            mapOf("word_translate" to "treason", "word_ru" to "измена"),
            mapOf("word_translate" to "sedition", "word_ru" to "подстрекательство"),
            mapOf("word_translate" to "subversion", "word_ru" to "подрывная деятельность"),
            mapOf("word_translate" to "sabotage", "word_ru" to "саботаж"),
            mapOf("word_translate" to "espionage", "word_ru" to "шпионаж"),
            mapOf("word_translate" to "intelligence", "word_ru" to "разведка"),
            mapOf("word_translate" to "surveillance", "word_ru" to "наблюдение"),
            mapOf("word_translate" to "monitoring", "word_ru" to "мониторинг"),
            mapOf("word_translate" to "interception", "word_ru" to "перехват"),
            mapOf("word_translate" to "eavesdropping", "word_ru" to "подслушивание"),
            mapOf("word_translate" to "wiretapping", "word_ru" to "прослушивание"),
            mapOf("word_translate" to "bugging", "word_ru" to "установка жучков"),
            mapOf("word_translate" to "infiltration", "word_ru" to "проникновение"),
            mapOf("word_translate" to "penetration", "word_ru" to "проникновение"),
            mapOf("word_translate" to "breach", "word_ru" to "нарушение"),
            mapOf("word_translate" to "violation", "word_ru" to "нарушение"),
            mapOf("word_translate" to "infringement", "word_ru" to "нарушение"),
            mapOf("word_translate" to "transgression", "word_ru" to "нарушение"),
            mapOf("word_translate" to "trespass", "word_ru" to "вторжение"),
            mapOf("word_translate" to "encroachment", "word_ru" to "вторжение"),
            mapOf("word_translate" to "intrusion", "word_ru" to "вторжение"),
            mapOf("word_translate" to "invasion", "word_ru" to "вторжение"),
            mapOf("word_translate" to "occupation", "word_ru" to "оккупация"),
            mapOf("word_translate" to "annexation", "word_ru" to "аннексия"),
            mapOf("word_translate" to "colonization", "word_ru" to "колонизация"),
            mapOf("word_translate" to "imperialism", "word_ru" to "империализм"),
            mapOf("word_translate" to "colonialism", "word_ru" to "колониализм"),
            mapOf("word_translate" to "hegemony", "word_ru" to "гегемония"),
            mapOf("word_translate" to "domination", "word_ru" to "доминирование"),
            mapOf("word_translate" to "supremacy", "word_ru" to "верховенство"),
            mapOf("word_translate" to "sovereignty", "word_ru" to "суверенитет"),
            mapOf("word_translate" to "autonomy", "word_ru" to "автономия"),
            mapOf("word_translate" to "independence", "word_ru" to "независимость"),
            mapOf("word_translate" to "freedom", "word_ru" to "свобода"),
            mapOf("word_translate" to "liberty", "word_ru" to "свобода"),
            mapOf("word_translate" to "emancipation", "word_ru" to "освобождение"),
            mapOf("word_translate" to "liberation", "word_ru" to "освобождение"),
            mapOf("word_translate" to "deliverance", "word_ru" to "избавление"),
            mapOf("word_translate" to "salvation", "word_ru" to "спасение"),
            mapOf("word_translate" to "redemption", "word_ru" to "искупление"),
            mapOf("word_translate" to "atonement", "word_ru" to "искупление"),
            mapOf("word_translate" to "reconciliation", "word_ru" to "примирение"),
            mapOf("word_translate" to "reunification", "word_ru" to "воссоединение"),
            mapOf("word_translate" to "integration", "word_ru" to "интеграция"),
            mapOf("word_translate" to "assimilation", "word_ru" to "ассимиляция"),
            mapOf("word_translate" to "acculturation", "word_ru" to "аккультурация"),
            mapOf("word_translate" to "incorporation", "word_ru" to "включение"),
            mapOf("word_translate" to "inclusion", "word_ru" to "включение"),
            mapOf("word_translate" to "participation", "word_ru" to "участие"),
            mapOf("word_translate" to "involvement", "word_ru" to "вовлеченность"),
            mapOf("word_translate" to "engagement", "word_ru" to "вовлеченность"),
            mapOf("word_translate" to "commitment", "word_ru" to "обязательство"),
            mapOf("word_translate" to "dedication", "word_ru" to "преданность"),
            mapOf("word_translate" to "devotion", "word_ru" to "преданность"),
            mapOf("word_translate" to "loyalty", "word_ru" to "лояльность"),
            mapOf("word_translate" to "allegiance", "word_ru" to "верность"),
            mapOf("word_translate" to "fidelity", "word_ru" to "верность"),
            mapOf("word_translate" to "faithfulness", "word_ru" to "верность"),
            mapOf("word_translate" to "constancy", "word_ru" to "постоянство"),
            mapOf("word_translate" to "steadfastness", "word_ru" to "стойкость"),
            mapOf("word_translate" to "perseverance", "word_ru" to "настойчивость"),
            mapOf("word_translate" to "persistence", "word_ru" to "настойчивость"),
            mapOf("word_translate" to "tenacity", "word_ru" to "упорство"),
            mapOf("word_translate" to "determination", "word_ru" to "решимость"),
            mapOf("word_translate" to "resolution", "word_ru" to "решимость"),
            mapOf("word_translate" to "willpower", "word_ru" to "сила воли"),
            mapOf("word_translate" to "fortitude", "word_ru" to "стойкость"),
            mapOf("word_translate" to "courage", "word_ru" to "мужество"),
            mapOf("word_translate" to "bravery", "word_ru" to "храбрость"),
            mapOf("word_translate" to "valor", "word_ru" to "доблесть"),
            mapOf("word_translate" to "heroism", "word_ru" to "героизм"),
            mapOf("word_translate" to "gallantry", "word_ru" to "рыцарство")


        )

        println("words1 = " + words.size)
        words = words.distinctBy { it["word_translate"] }.distinctBy { it["word_ru"] }.filter { it["word_ru"].toString().length <= 18 }
        println("words2 = " + words.size)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


        /*scope.launch {


            val data = hashMapOf(
                "description" to "C1 Юридическое мастерство",
                "starsToEnable" to 910,
                "imageUrl" to "/way",
                "lessonsCount" to 21, //17 //13
                "name" to "Модуль 30",
            )

            val module = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules").add(data).await()
            repeat(21) { index ->

                val batch = db.batch()

                val lessonData = mapOf(
                    "name" to "lesson ${index + 1}",
                    "type" to if (index == 20) "LAST_GAME" else if (index % 2 == 0) "DEFAULT" else "SWAP"
                )

                val doc = db.collection("languages").document("efdMkdOPEKagGccx2euX").collection("modules")
                    .document(module.id)
                    .collection("lessons").document("lesson ${index + 1}")// авто id

                batch.set(doc, lessonData)


                words.shuffled().take(if (index == 20) 100 else 50).forEach { word ->

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

