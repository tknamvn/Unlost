package com.dev.unlost.util

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as whenever

class SecurityPinManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private val memoryStorage = HashMap<String, Any?>()

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        mockPrefs = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)

        whenever(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
        whenever(mockPrefs.edit()).thenReturn(mockEditor)

        whenever(mockEditor.putString(anyString(), anyOrNull())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<String?>(1)
            memoryStorage[key] = value
            mockEditor
        }

        whenever(mockEditor.putBoolean(anyString(), anyBoolean())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<Boolean>(1)
            memoryStorage[key] = value
            mockEditor
        }

        whenever(mockEditor.putInt(anyString(), anyInt())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<Int>(1)
            memoryStorage[key] = value
            mockEditor
        }

        whenever(mockEditor.putLong(anyString(), anyLong())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<Long>(1)
            memoryStorage[key] = value
            mockEditor
        }

        whenever(mockEditor.remove(anyString())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            memoryStorage.remove(key)
            mockEditor
        }

        whenever(mockPrefs.getString(anyString(), anyOrNull())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val default = invocation.getArgument<String?>(1)
            (memoryStorage[key] as? String) ?: default
        }

        whenever(mockPrefs.getBoolean(anyString(), anyBoolean())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val default = invocation.getArgument<Boolean>(1)
            (memoryStorage[key] as? Boolean) ?: default
        }

        whenever(mockPrefs.getInt(anyString(), anyInt())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val default = invocation.getArgument<Int>(1)
            (memoryStorage[key] as? Int) ?: default
        }
    }

    @Test
    fun savePin_and_verifyPin_success() {
        val pinManager = SecurityPinManager(mockContext)
        pinManager.savePin("1234")

        assertTrue(pinManager.hasPinSet())
        assertTrue(pinManager.verifyPin("1234"))
        assertFalse(pinManager.verifyPin("9999"))
    }
}
