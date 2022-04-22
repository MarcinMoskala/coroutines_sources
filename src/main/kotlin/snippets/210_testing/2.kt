package f_210_testing.s_2

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import java.util.concurrent.Executors
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.test.assertEquals

class Data

interface MainView {
    suspend fun show(data: Data)
}

interface DataRepo {
    suspend fun fetchData(): Data
}

//sampleStart
class MainPresenter(
    private val mainView: MainView,
    private val dataRepository: DataRepo
) {
    suspend fun onCreate() = coroutineScope {
        launch(Dispatchers.Main) {
            val data = dataRepository.fetchData()
            mainView.show(data)
        }
    }
}

class FakeMainView : MainView {
    var dispatchersUsedToShow: List<CoroutineContext?> =
        emptyList()

    override suspend fun show(data: Data) {
        dispatchersUsedToShow +=
            coroutineContext[ContinuationInterceptor]
    }
}

class FakeDataRepo : DataRepo {
    override suspend fun fetchData(): Data {
        delay(1000)
        return Data()
    }
}

class SomeTest {

    private val mainDispatcher = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSomeUI() = runBlocking {
        // given
        val view = FakeMainView()
        val repo = FakeDataRepo()
        val presenter = MainPresenter(view, repo)

        // when
        presenter.onCreate()

        // then show was called on the main dispatcher
        assertEquals(
            listOf(Dispatchers.Main),
            view.dispatchersUsedToShow
        )
    }
}
//sampleEnd
