package net.cydhra.nidhogg

import com.soywiz.klock.DateTime
import kotlinx.coroutines.runBlocking
import net.cydhra.nidhogg.data.MetricKeys
import net.cydhra.nidhogg.data.NameHistoryEntry
import net.cydhra.nidhogg.mojang.MojangClient
import org.junit.*
import org.junit.rules.ExpectedException
import java.util.*

/* an epoch second, where the UUID of account Cydhra was valid */
private const val EPOCH_SECOND = 1492297560L
private const val CYDHRA_UUID = "fdba166c-4eab-43ea-b0e8-8d7d62e1c417"

class MojangClientTest {

    companion object {
        lateinit var client: MojangClient

        @JvmStatic
        @BeforeClass
        fun setup() {
            client = MojangClient()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            client.close()
        }
    }

    @Rule
    @JvmField
    val ruleThrown = ExpectedException.none()!!

    @Test
    fun checkStatus() {
        runBlocking {
            Assert.assertTrue(client.checkStatus().isNotEmpty())
        }
    }

    @Test
    fun getUUIDbyUsername() {
        runBlocking {
            val uuidEntry = client.getUUIDbyUsername("Cydhra", DateTime.fromUnix(EPOCH_SECOND))

            Assert.assertNotNull(uuidEntry)

            Assert.assertEquals(CYDHRA_UUID, uuidEntry!!.uuid.toString())
            Assert.assertNull(uuidEntry.legacy)
            Assert.assertNull(uuidEntry.demo)
        }
    }

    @Test
    fun getNameHistoryByUUID() {
        runBlocking {
            Assert.assertEquals(
                    client.getNameHistoryByUUID(UUID.fromString(CYDHRA_UUID))[0],
                    NameHistoryEntry("Cydhra", null)
            )
        }
    }

    @Test
    fun getUUIDsByNames() {
        runBlocking {
            val uuidEntries = client.getUUIDsByNames(listOf("Cydhra", "@invalid_username@"))
            Assert.assertEquals(1, uuidEntries.size)
        }
    }

    @Test
    fun requestMoreThan100UUIDs() {
        runBlocking {
            ruleThrown.expect(IllegalArgumentException::class.java)
            client.getUUIDsByNames(Collections.nCopies(101, "Cydhra"))
        }
    }

    @Test
    fun requestInvalidUsernames() {
        runBlocking {
            ruleThrown.expect(IllegalArgumentException::class.java)
            client.getUUIDsByNames(listOf(""))
        }
    }

    @Test
    fun getProfileByUUID() {
        runBlocking {
            val profile = client.getProfileByUUID(UUID.fromString(CYDHRA_UUID))

            Assert.assertNotNull(profile)
            Assert.assertNotNull(profile.textures)
        }
    }

    @Test
    fun getBlockedServers() {
        runBlocking {
            Assert.assertTrue(client.getBlockedServers().isNotEmpty())
        }
    }

    @Test
    fun saleMetrics() {
        runBlocking {
            Assert.assertTrue(
                    client.getSaleStatistics(MetricKeys.all()).total
                            > client.getSaleStatistics(MetricKeys.minecraft()).total
            )
        }
    }
}