package net.cydhra.nidhogg

import net.cydhra.nidhogg.data.NameEntry
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.time.Instant
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
    }

    @Rule
    @JvmField
    val ruleThrown = ExpectedException.none()!!

    @Test
    fun checkStatus() {
    }

    @Test
    fun getUUIDbyUsername() {
        val uuidEntry = client.getUUIDbyUsername("Cydhra", Instant.ofEpochSecond(EPOCH_SECOND))

        Assert.assertNotNull(uuidEntry)

        Assert.assertEquals(CYDHRA_UUID, uuidEntry!!.uuid.toString())
        Assert.assertEquals("Cydhra", uuidEntry.name)
        Assert.assertNull(uuidEntry.legacy)
        Assert.assertNull(uuidEntry.demo)
    }

    @Test
    fun getNameHistoryByUUID() {
        Assert.assertEquals(
                client.getNameHistoryByUUID(UUID.fromString(CYDHRA_UUID))[0],
                NameEntry("Cydhra", null)
        )
    }

    @Test
    fun getUUIDsByNames() {
        val uuidEntries = client.getUUIDsByNames(listOf("Cydhra", "ThisPlayerDoesNotExistBecause@"))
        Assert.assertEquals(1, uuidEntries.size)
    }

    @Test
    fun requestMoreThan100UUIDs() {
        ruleThrown.expect(IllegalArgumentException::class.java)
        client.getUUIDsByNames(Collections.nCopies(101, "Cydhra"))
    }

    @Test
    fun requestInvalidUsernames() {
        ruleThrown.expect(IllegalArgumentException::class.java)
        client.getUUIDsByNames(listOf(""))
    }

    @Test
    fun getProfileByUUID() {
        val profile = client.getProfileByUUID(UUID.fromString(CYDHRA_UUID))

        Assert.assertNotNull(profile)
        Assert.assertNotNull(profile.textures)
    }
}