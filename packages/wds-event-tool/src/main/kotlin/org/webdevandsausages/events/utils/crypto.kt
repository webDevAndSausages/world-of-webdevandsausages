package org.webdevandsausages.events.utils

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun prepareSecreteKey(myKey: String): SecretKeySpec {
    val sha = MessageDigest.getInstance("SHA-1")
    val key = Arrays.copyOf(sha.digest(myKey.toByteArray(StandardCharsets.UTF_8)), 16)
    val secretKey = SecretKeySpec(key, "AES")
    return secretKey
}

fun encrypt(strToEncrypt: String, secret: String): Either<Throwable, String> {
    try {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, prepareSecreteKey(secret))
        return Right(Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8")))))
    } catch (e: Exception) {
        return Left(e)
    }
}

fun decrypt(strToDecrypt: String, secret: String): Either<Exception, String> {
    try {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, prepareSecreteKey(secret))
        return Right(String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt))))
    } catch (e: Exception) {
        return Left(e)
    }
}
