package com.manomitra.app.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseUserMetadata
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockMetadata: FirebaseUserMetadata
    
    private lateinit var staticAuth: MockedStatic<FirebaseAuth>
    private lateinit var staticFirestore: MockedStatic<FirebaseFirestore>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        mockAuth = mock(FirebaseAuth::class.java)
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockUser = mock(FirebaseUser::class.java)
        mockMetadata = mock(FirebaseUserMetadata::class.java)

        staticAuth = mockStatic(FirebaseAuth::class.java)
        staticFirestore = mockStatic(FirebaseFirestore::class.java)

        staticAuth.`when`<FirebaseAuth> { FirebaseAuth.getInstance() }.thenReturn(mockAuth)
        staticFirestore.`when`<FirebaseFirestore> { FirebaseFirestore.getInstance() }.thenReturn(mockFirestore)

        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("test_uid")
        `when`(mockUser.metadata).thenReturn(mockMetadata)
    }

    @After
    fun tearDown() {
        staticAuth.close()
        staticFirestore.close()
        Dispatchers.resetMain()
    }

    @Test
    fun deleteAccount_requiresRecentLogin_fails() = runTest {
        // Last sign in was 10 minutes ago
        val tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000)
        `when`(mockMetadata.lastSignInTimestamp).thenReturn(tenMinutesAgo)

        val viewModel = AuthViewModel()
        var testResult: Result<Unit>? = null
        
        viewModel.deleteAccount { res ->
            testResult = res
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(testResult)
        assertTrue(testResult!!.isFailure)
        assertEquals("REQUIRES_RECENT_LOGIN", testResult!!.exceptionOrNull()?.message)
    }

    @Test
    fun deleteAccount_success() = runTest {
        // Last sign in was 1 minute ago
        val oneMinuteAgo = System.currentTimeMillis() - (1 * 60 * 1000)
        `when`(mockMetadata.lastSignInTimestamp).thenReturn(oneMinuteAgo)

        // Mock Firestore calls
        val mockCollection = mock(CollectionReference::class.java)
        val mockDoc = mock(DocumentReference::class.java)
        val mockTaskQuery = mock(Task::class.java) as Task<QuerySnapshot>
        val mockTaskVoid = mock(Task::class.java) as Task<Void>
        val mockSnapshot = mock(QuerySnapshot::class.java)

        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollection)
        `when`(mockCollection.document(anyString())).thenReturn(mockDoc)
        
        // Mock subcollections get
        val mockSubCollection = mock(CollectionReference::class.java)
        `when`(mockDoc.collection(anyString())).thenReturn(mockSubCollection)
        `when`(mockSubCollection.get()).thenReturn(mockTaskQuery)
        `when`(mockTaskQuery.isComplete).thenReturn(true)
        `when`(mockTaskQuery.isSuccessful).thenReturn(true)
        `when`(mockTaskQuery.result).thenReturn(mockSnapshot)
        `when`(mockTaskQuery.exception).thenReturn(null)
        `when`(mockSnapshot.documents).thenReturn(emptyList())

        // Mock Firestore WriteBatch
        val mockBatch = mock(com.google.firebase.firestore.WriteBatch::class.java)
        `when`(mockFirestore.batch()).thenReturn(mockBatch)
        `when`(mockBatch.delete(any(DocumentReference::class.java))).thenReturn(mockBatch)
        `when`(mockBatch.commit()).thenReturn(mockTaskVoid)
        `when`(mockTaskVoid.isComplete).thenReturn(true)
        `when`(mockTaskVoid.isSuccessful).thenReturn(true)
        `when`(mockTaskVoid.result).thenReturn(null)
        `when`(mockTaskVoid.exception).thenReturn(null)

        // Mock currentUser.delete()
        val mockDeleteTask = mock(Task::class.java) as Task<Void>
        `when`(mockUser.delete()).thenReturn(mockDeleteTask)
        `when`(mockDeleteTask.isComplete).thenReturn(true)
        `when`(mockDeleteTask.isSuccessful).thenReturn(true)
        `when`(mockDeleteTask.result).thenReturn(null)
        `when`(mockDeleteTask.exception).thenReturn(null)

        val viewModel = AuthViewModel()
        var testResult: Result<Unit>? = null

        viewModel.deleteAccount { res ->
            testResult = res
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(testResult)
        assertTrue(testResult!!.isSuccess)
    }
}
