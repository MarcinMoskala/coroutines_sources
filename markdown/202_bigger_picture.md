```
// Spring Boot example controller
@Controller
class NewsController(
    private val newsService: NewsService,
) {
    @GetMapping("/news")
    suspend fun getNews(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody search: SearchNewsJson
    ): NewsListJson {
        return newsService
            .getNews(authorization, search.toDomain())
            .toJson()
    }
    
    @PostMapping("/news")
    suspend fun createNews(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody news: PostNewsJson
    ): Boolean {
        return newsService
            .createNews(authorization, news.toDomain())
    }
}
```


```
class NewsViewModel(
    private val newsRepository: NewsRepository,
): BaseViewModel() {
    private val _news = MutableStateFlow(emptyList<News>())
    val news: StateFlow<List<News>> = _news
    
    fun onCreate() {
        viewModelScope.launch {
            val news = newsRepository.getNews()
            _news.value = news
        }
    }
}
```


```
class NewsService(
    private val userService: UserService,
    private val newsRepository: NewsRepository,
) {
    suspend fun getNews(
        authorization: String, 
        search: SearchNews
    ): NewsList = coroutineScope {
        val paidUser = async { userService.isPaidUser(authorization) }
        val news = async { newsRepository.getNews(search) }
        return news.await()
            .filter { !it.isPaid || paidUser.await()  }
    }
    
    suspend fun createNews(
        authorization: String,
        news: PostNews
    ): Boolean {
        val userId = userService.getUserId(authorization)
        return newsRepository.createNews(userId, news)
    }
}
```


```
// Ktor Client example
suspend fun getNews(search: SearchNews): NewsList = 
    client.get("news") {
        parameter("search", search)
    }

suspend fun createNews(userId: String, news: PostNews): Boolean =
    client.post("news") {
        parameter("userId", userId)
        contentType(Json)
        body = news
    }.status == HttpStatusCode.OK
```


```
//1
class TestNewsService {
    @Test
    fun `should return news`() = runTest { 
        val service = NewsService(
            userService = FakeUserService(),
            newsRepository = FakeNewsRepository(),
        )
        val news = service.getNews("auth", SearchNews("query"))
        assert(news.isNotEmpty())
    }
}
```