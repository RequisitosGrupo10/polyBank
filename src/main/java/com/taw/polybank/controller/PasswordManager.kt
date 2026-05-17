package com.taw.polybank.controller

import com.taw.polybank.dto.ClientDTO
import com.taw.polybank.service.ClientService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

/**
 * @author Illya Rozumovskyy
 */
class PasswordManager(
  private val clientService: ClientService,
) {
  private val secureRandom: SecureRandom = SecureRandom()
  private var encoder: BCryptPasswordEncoder? = null

  fun savePassword(
    client: ClientDTO,
    plainPassword: String?,
  ): Array<String?> {
    if (clientService.findById(client.getId()).isPresent()) {
      throw RuntimeException("ERROR: can not reset password using this method.")
    }
    // generating new salt
    val seed = ByteArray(SALT_SIZE)
    secureRandom.nextBytes(seed)
    // setting up the seed and initializing encoder
    primeRandom(seed)
    initializeEncoder()

    val saltAndPass = arrayOfNulls<String>(2)

    saltAndPass[0] = String(seed, StandardCharsets.ISO_8859_1)
    saltAndPass[1] = encoder!!.encode(plainPassword)
    return saltAndPass
  }

  fun verifyPassword(
    client: ClientDTO?,
    password: String?,
  ): Boolean {
    var result = false
    if (client != null) {
      val salt = clientService.getSalt(client.id)
      val seed: ByteArray = salt.toByteArray(StandardCharsets.ISO_8859_1)
      primeRandom(seed)
      initializeEncoder()
      result = encoder!!.matches(password, clientService.getPassword(client.getId()))
    }
    return result
  }

  fun resetPassword(
    client: ClientDTO,
    newPassword: String?,
  ) {
    val salt = clientService.getSalt(client.getId())
    val seed: ByteArray = salt.toByteArray(StandardCharsets.ISO_8859_1)
    primeRandom(seed)
    initializeEncoder()

    clientService.updateUserPassword(client.getId(), encoder!!.encode(newPassword))
  }

  private fun initializeEncoder() {
    this.encoder = BCryptPasswordEncoder(ENCODER_VERSION, ITERATIONS, this.secureRandom)
  }

  private fun primeRandom(bytes: ByteArray?) {
    secureRandom.setSeed(bytes)
  }

  companion object {
    private val ENCODER_VERSION = BCryptVersion.`$2B`
    private const val ITERATIONS = 5
    private const val SALT_SIZE = 32
  }
}
