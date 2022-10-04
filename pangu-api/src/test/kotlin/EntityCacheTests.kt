import com.rainbow.pangu.api.ApiApplication
import com.rainbow.pangu.repository.DemoRepo
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import javax.annotation.Resource

@SpringBootTest(classes = [ApiApplication::class])
class EntityCacheTests {
    @Resource
    lateinit var demoRepo: DemoRepo

    @Test
    fun entityCache() {
        demoRepo.save(com.rainbow.pangu.entity.Demo())
        val demo1 = demoRepo.save(com.rainbow.pangu.entity.Demo())
        val demo2 = demoRepo.saveAll(listOf(com.rainbow.pangu.entity.Demo()))[0]!!
        val test = demoRepo.findById(demo2.id)
        val tests = demoRepo.findAllById(listOf(demo2.id, demo1.id))
        val exists = demoRepo.existsById(demo2.id)
        assert(test.isPresent)
        assert(tests.size == 2)
        assert(exists)
        val demo3 = com.rainbow.pangu.entity.Demo()
        val demo4 = com.rainbow.pangu.entity.Demo()
        val demo5 = com.rainbow.pangu.entity.Demo()
        demoRepo.saveAll(listOf(demo3, demo4, demo5))
        demoRepo.deleteById(demo1.id)
        demoRepo.deleteAllById(listOf(demo2.id, demo3.id))
        demoRepo.delete(demo4)
        demoRepo.deleteAll(listOf(demo5))
    }
}