package com.sidspace.loven.languages.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.languages.data.mapper.toLanguagesDomain
import com.sidspace.loven.languages.domain.model.LanguageDomain
import com.sidspace.loven.languages.domain.repository.LanguageRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LanguageRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) : LanguageRepository {
    override suspend fun getLanguages(): DomainResult<List<LanguageDomain>> {
        return suspendCoroutine { cont ->
            firestore.collection(FirestoreCollections.LANGUAGE).get().addOnSuccessListener { result ->

                cont.resume(DomainResult.Success(result.toLanguagesDomain()))
            }.addOnFailureListener { error ->
                cont.resume(DomainResult.Error)
            }
        }
    }


}
