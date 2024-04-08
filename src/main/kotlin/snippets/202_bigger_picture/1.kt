package f_202_bigger_picture.s_1

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
