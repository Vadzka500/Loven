package com.sidspace.loven.modules.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.core.data.model.UserManager
import com.sidspace.core.data.model.UserModule
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.modules.data.mapper.toDomain
import com.sidspace.loven.modules.data.mapper.toUserModule
import com.sidspace.loven.modules.domain.model.ModuleDomain
import com.sidspace.loven.modules.domain.repository.ModuleRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ModuleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userManager: UserManager
) : ModuleRepository {
    override suspend fun getModulesByLanguage(id: String): DomainResult<List<ModuleDomain>> {

        val modules =
            firestore.collection(FirestoreCollections.LANGUAGE).document(id).collection(FirestoreCollections.MODULES)
                .get().await()

        var usersModules = getUsersModules(id)
        if (usersModules.isEmpty) {
            addUserModule(
                id,
                modules.documents.sortedBy { it.get("name").toString().replace("Модуль ", "").toLong() }[0].id
            )
        }
        usersModules = getUsersModules(id)

        /*modules.documents.forEach { item ->
            if (item.data!!["name"].toString() == "Модуль 17") {
                firestore.collection(FirestoreCollections.LANGUAGE).document(id)
                    .collection(FirestoreCollections.MODULES).document(item.id).delete().addOnSuccessListener {

                    }
            }
        }*/


        return DomainResult.Success(
            modules.toDomain(
                id,
                usersModules.documents.associate { it.id to it.toUserModule() }
            )
        )

    }

    suspend fun getUsersModules(idLanguage: String): QuerySnapshot {

        val modulesShapshot = firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id)
            .collection(FirestoreCollections.LANGUAGE).document(idLanguage)
            .collection(FirestoreCollections.MODULES).get().await()

        return modulesShapshot


    }

    suspend fun addUserModule(idLanguage: String, idModule: String) {
        firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id)
            .collection(FirestoreCollections.LANGUAGE).document(idLanguage)
            .collection(FirestoreCollections.MODULES).document(idModule)
            .set(UserModule())
            .await()
    }
}
