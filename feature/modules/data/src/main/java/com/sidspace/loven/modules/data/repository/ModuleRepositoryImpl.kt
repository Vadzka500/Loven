package com.sidspace.loven.modules.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.modules.data.mapper.toDomain
import com.sidspace.loven.modules.domain.model.ModuleDomain
import com.sidspace.loven.modules.domain.repository.ModuleRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ModuleRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) : ModuleRepository {
    override suspend fun getModulesByLanguage(id: String): DomainResult<List<ModuleDomain>> {
        return suspendCoroutine { cont ->
            firestore.collection(FirestoreCollections.LANGUAGE).document(id).collection(FirestoreCollections.MODULES)
                .get().addOnSuccessListener { result ->

                cont.resume(DomainResult.Success(result.toDomain(id)))
            }.addOnFailureListener { error ->
                cont.resume(DomainResult.Error)
            }
        }
    }
}
