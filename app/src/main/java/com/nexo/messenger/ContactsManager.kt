package com.nexo.messenger

import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class NexoContact(
    val id: String,
    val name: String,
    val phone: String
)

object ContactsManager {

    private const val PREFS = "nexo_contacts"
    private const val KEY_CONTACTS = "saved_contacts"

    fun getSavedContacts(context: Context): List<NexoContact> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CONTACTS, "[]") ?: "[]"
        val result = mutableListOf<NexoContact>()

        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                result.add(
                    NexoContact(
                        item.getString("id"),
                        item.getString("name"),
                        item.getString("phone")
                    )
                )
            }
        } catch (_: Exception) {
        }

        return result
    }

    fun saveInNexo(context: Context, name: String, phone: String): NexoContact {
        val contacts = getSavedContacts(context).toMutableList()
        val contact = NexoContact(UUID.randomUUID().toString(), name.trim(), phone.trim())

        contacts.add(contact)

        val array = JSONArray()
        contacts.forEach {
            array.put(
                JSONObject().apply {
                    put("id", it.id)
                    put("name", it.name)
                    put("phone", it.phone)
                }
            )
        }

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CONTACTS, array.toString())
            .apply()

        return contact
    }

    fun readPhoneContacts(context: Context): List<NexoContact> {
        val result = mutableListOf<NexoContact>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            val nameColumn = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val phoneColumn = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn) ?: continue
                val name = cursor.getString(nameColumn) ?: "Sin nombre"
                val phone = cursor.getString(phoneColumn) ?: continue

                result.add(NexoContact(id, name, phone))
            }
        }

        return result.distinctBy { it.id + it.phone }
    }

    fun saveInPhone(context: Context, name: String, phone: String): Boolean {
        return try {
            val resolver = context.contentResolver

            val rawContact = ContentValues().apply {
                putNull(ContactsContract.RawContacts.ACCOUNT_TYPE)
                putNull(ContactsContract.RawContacts.ACCOUNT_NAME)
            }

            val rawUri = resolver.insert(
                ContactsContract.RawContacts.CONTENT_URI,
                rawContact
            ) ?: return false

            val rawId = rawUri.lastPathSegment ?: return false

            val nameValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            }

            resolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

            val phoneValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                put(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
            }

            resolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
            true
        } catch (_: SecurityException) {
            false
        } catch (_: Exception) {
            false
        }
    }
}
